package simulation;

import net.unitedfield.cc.PAppletDisplayGeometry;
import processing.core.PApplet;
import test.p5.OrangeWavePApplet;

import com.jme3.app.SimpleApplication;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.font.BitmapText;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.debug.Arrow;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.terrain.heightmap.AbstractHeightMap;
import com.jme3.terrain.heightmap.ImageBasedHeightMap;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.WrapMode;
import com.jme3.util.SkyFactory;
// for exercise 2

/** Sample 10 - How to create fast-rendering terrains from heightmaps,
and how to use texture splatting to make the terrain look good.  */
public class DisplayLandscapeNew extends SimpleApplication {
	private TerrainQuad terrain;
	Material mat_terrain;
	Node shootables;
	int displayNum = 0;

	@Override
	public void simpleInitApp() {
		shootables = new Node("Shootables");
		rootNode.attachChild(shootables);

		flyCam.setMoveSpeed(50);
		setupScene();
		createTerrain();
		initCrossHairs();
		initKeys();

		//Axis Arrow
		putArrow(Vector3f.ZERO, Vector3f.UNIT_X, ColorRGBA.Red);
		putArrow(Vector3f.ZERO, Vector3f.UNIT_Y, ColorRGBA.Green);
		putArrow(Vector3f.ZERO, Vector3f.UNIT_Z, ColorRGBA.Blue);
	}

	private void initKeys() {
		inputManager.addMapping("Shoot",
				new KeyTrigger(KeyInput.KEY_SPACE), // trigger 1: spacebar
				new MouseButtonTrigger(MouseInput.BUTTON_LEFT)); // trigger 2: left-button click
		inputManager.addListener(actionListener, "Shoot");
	}

	private void setupScene(){
		//cam
		cam.setLocation(new Vector3f(-150f, 4.0f, 0f));
		//cam.lookAt(new Vector3f(0f, 1.6f, 0f), Vector3f.UNIT_Y);
		flyCam.setMoveSpeed(10);

		//sky
		/*
		Texture west = assetManager.loadTexture("Textures/Sky/Lagoon/lagoon_west.jpg");
		Texture east = assetManager.loadTexture("Textures/Sky/Lagoon/lagoon_east.jpg");
		Texture north = assetManager.loadTexture("Textures/Sky/Lagoon/lagoon_north.jpg");
		Texture south = assetManager.loadTexture("Textures/Sky/Lagoon/lagoon_south.jpg");
		Texture up = assetManager.loadTexture("Textures/Sky/Lagoon/lagoon_up.jpg");
		Texture down = assetManager.loadTexture("Textures/Sky/Lagoon/lagoon_down.jpg");
		Spatial sky = SkyFactory.createSky(assetManager, west, east, north, south, up, down);
		rootNode.attachChild(sky);
		 */
		rootNode.attachChild(SkyFactory.createSky(assetManager, "Textures/Sky/Bright/BrightSky.dds", false));

		//sun
		DirectionalLight sun = new DirectionalLight();

		sun.setDirection(new Vector3f(-.5f,-.5f,-.5f).normalizeLocal());
		rootNode.addLight(sun);
	}

	public void createTerrain(){
		mat_terrain = new Material(assetManager, "Common/MatDefs/Terrain/Terrain.j3md");
		mat_terrain.setTexture("Alpha", assetManager.loadTexture("Textures/Terrain/splat/alphamap.png"));

		Texture grass = assetManager.loadTexture("Textures/Terrain/splat/grass.jpg");
		grass.setWrap(WrapMode.Repeat);
		mat_terrain.setTexture("Tex1", grass);
		mat_terrain.setFloat("Tex1Scale", 64f);

		Texture dirt = assetManager.loadTexture("Textures/Terrain/splat/dirt.jpg");
		dirt.setWrap(WrapMode.Repeat);
		mat_terrain.setTexture("Tex2", dirt);
		mat_terrain.setFloat("Tex2Scale", 32f);

		Texture rock = assetManager.loadTexture("Textures/Terrain/splat/road.jpg");
		rock.setWrap(WrapMode.Repeat);
		mat_terrain.setTexture("Tex3", rock);
		mat_terrain.setFloat("Tex3Scale", 256f);

		AbstractHeightMap heightmap = null;
		Texture heightMapImage = assetManager.loadTexture("Textures/Terrain/splat/mountains512.png");
		heightmap = new ImageBasedHeightMap(heightMapImage.getImage());
		heightmap.load();

		int patchSize = 65;
		terrain = new TerrainQuad("my terrain", patchSize, 513, heightmap.getHeightMap());

		terrain.setMaterial(mat_terrain);
		terrain.setLocalTranslation(0, 0, 0);
		terrain.setLocalScale(1f, 0.05f, 1f);
		shootables.attachChild(terrain);

		//TerrainLodControl control = new TerrainLodControl(terrain, getCamera());
		//terrain.addControl(control);
	}

	public void putArrow(Vector3f pos, Vector3f dir, ColorRGBA color){
		Arrow arrow = new Arrow(dir);
		arrow.setLineWidth(4); // make arrow thicker
		putShape(arrow, color).setLocalTranslation(pos);
	}

	public Geometry putShape(Mesh shape, ColorRGBA color){
		Geometry g = new Geometry("shape", shape);
		Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		mat.getAdditionalRenderState().setWireframe(true);
		mat.setColor("Color", color);
		g.setMaterial(mat);
		rootNode.attachChild(g);
		return g;
	}

	protected void initCrossHairs() {
		guiNode.detachAllChildren();
		guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
		BitmapText ch = new BitmapText(guiFont, false);
		ch.setSize(guiFont.getCharSet().getRenderedSize() * 2);
		ch.setText("+"); // crosshairs
		ch.setLocalTranslation( // center
				settings.getWidth() / 2 - guiFont.getCharSet().getRenderedSize() / 3 * 2,
				settings.getHeight() / 2 + ch.getLineHeight() / 2, 0);
		guiNode.attachChild(ch);
	}

	private void putNewGirl(Vector3f pos){
		Spatial girl = assetManager.loadModel("myAssets/Models/WalkingGirl/WalkingGirl.obj");
		girl.rotate(0, (float)(Math.PI), 0);
		girl.setLocalTranslation(pos);
		this.rootNode.attachChild(girl);
		displayNum++;
	}

	private void putNewDisplay(Vector3f pos){
		PApplet applet = new OrangeWavePApplet();
		PAppletDisplayGeometry display = new PAppletDisplayGeometry("display"+displayNum, assetManager, 0.4f, 2f, applet, 20, 100, false);
		rootNode.attachChild(display);
		//display.rotate((float) (-0.5 * Math.PI), 0, 0);
		display.setLocalTranslation(pos.x, pos.y+1f, pos.z);
		displayNum++;
	}

	private ActionListener actionListener = new ActionListener() {
		public void onAction(String name, boolean keyPressed, float tpf) {
			if (name.equals("Shoot") && !keyPressed) {
				// 1. Reset results list.
				CollisionResults results = new CollisionResults();
				// 2. Aim the ray from cam loc to cam direction.
				Ray ray = new Ray(cam.getLocation(), cam.getDirection());
				// 3. Collect intersections between Ray and Shootables in results list.
				shootables.collideWith(ray, results);
				// 4. Print the results
				System.out.println("----- Collisions? " + results.size() + "-----");
				for (int i = 0; i < results.size(); i++) {
					// For each hit, we know distance, impact point, name of geometry.
					float dist = results.getCollision(i).getDistance();
					Vector3f pt = results.getCollision(i).getContactPoint();
					String hit = results.getCollision(i).getGeometry().getName();
					System.out.println("* Collision #" + i);
					System.out.println("  You shot " + hit + " at " + pt + ", " + dist + " wu away.");
				}
				// 5. Use the results (we mark the hit object)
				if (results.size() > 0) {
					// The closest collision point is what was truly hit:
					CollisionResult closest = results.getClosestCollision();
					if(displayNum < 1){
						putNewGirl(closest.getContactPoint());
					} else {
						putNewDisplay(closest.getContactPoint());
					}
				} else {
					// No hits? Then remove the red mark.
					// rootNode.detachChild(mark);
				}
			}
		}
	};

	public void destroy() {
		super.destroy();
		System.exit(0);
	}
	
	public static void main(String[] args) {
		SimpleApplication app = new DisplayLandscapeNew();
		app.start();
	}
}