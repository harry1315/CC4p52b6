package simulation;

import test.p5.OrangeWavePApplet;
import net.unitedfield.cc.PAppletDisplayGeometry;

import com.jme3.app.SimpleApplication;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.texture.Texture;
import com.jme3.util.SkyFactory;

public class DisplayLandscapeSimulation extends SimpleApplication  {
	PAppletDisplayGeometry[] player = new PAppletDisplayGeometry[50];
	float[] posX = {-30.4f, -26.8f, -27.9f, -29.2f, -25.7f, -22.2f, -19.8f, -15.1f, -23.1f, -15.700001f, -9.200001f, -9.5f, -10.9f, -2.7000008f, 2.0f, -2.0f, 2.5999985f, 10.599998f, 6.0999985f, 1.2999992f, 8.299999f, 12.900002f, 16.7f, 16.900002f, 23.8f};
	float[] posZ = {-33.3f, -32.3f, -28.9f, -25.0f, -25.0f, -20.7f, -20.7f, -15.4f, -12.700001f, -12.700001f, -11.9f, -5.799999f, 1.2999992f, 1.0999985f, 2.0999985f, 8.299999f, 11.400002f, 10.099998f, 17.099998f, 16.599998f, 22.7f, 22.099998f, 21.7f, 24.599998f, 24.699997f};

	@Override
	public void simpleInitApp() {
		setupScene();
		setupPlayer();
	}

	private void setupScene(){
		//cam
		//cam.setFrustumPerspective(80f, 1f, 1f, 1000f);
		cam.setLocation(new Vector3f(0.0f, 1.6f, 24.0f));
		cam.lookAt(new Vector3f(0f, 1.6f, 0f), Vector3f.UNIT_Y);
		flyCam.setMoveSpeed(5);

		//sky
		Texture west = assetManager.loadTexture("Textures/Sky/Lagoon/lagoon_west.jpg");
		Texture east = assetManager.loadTexture("Textures/Sky/Lagoon/lagoon_east.jpg");
		Texture north = assetManager.loadTexture("Textures/Sky/Lagoon/lagoon_north.jpg");
		Texture south = assetManager.loadTexture("Textures/Sky/Lagoon/lagoon_south.jpg");
		Texture up = assetManager.loadTexture("Textures/Sky/Lagoon/lagoon_up.jpg");
		Texture down = assetManager.loadTexture("Textures/Sky/Lagoon/lagoon_down.jpg");
		Spatial sky = SkyFactory.createSky(assetManager, west, east, north, south, up, down);
		rootNode.attachChild(sky);

		//floor
		Material textureMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		textureMat.setTexture("ColorMap", assetManager.loadTexture("myAssets/Textures/metalTexture.jpg"));
		Material grayMat = assetManager.loadMaterial("myAssets/Materials/GrayColor.j3m");
		Box floor = new Box(Vector3f.ZERO, 1500f, 0.01f, 1500f);
		Geometry floorGeom = new Geometry("Floor", floor);
		floorGeom.setMaterial(grayMat);
		rootNode.attachChild(floorGeom);

		//sun
		DirectionalLight sun = new DirectionalLight();
		sun.setColor(ColorRGBA.White);
		sun.setDirection(new Vector3f(-.5f,-.5f,-.5f).normalizeLocal());
		rootNode.addLight(sun);

		//girl
		Spatial girl = assetManager.loadModel("myAssets/Models/WalkingGirl/WalkingGirl.obj");
		girl.rotate(0, (float)(Math.PI), 0);
		girl.setLocalTranslation(0f, 0f, 20f);
		this.rootNode.attachChild(girl);
		girl.setShadowMode(ShadowMode.CastAndReceive);
	}

	private void setupPlayer() {
		//screen
		for(int i = 0; i<posX.length; i++){
			this.player[i] = new PAppletDisplayGeometry("display"+i, assetManager, 0.3f, 1.5f,  
					new OrangeWavePApplet(), 20, 100, false);
			rootNode.attachChild(player[i]);
			player[i].setLocalTranslation(posX[i], 1.5f, posZ[i]);
		}
	}

	public void destroy() {
		super.destroy();
		System.exit(0);
	}

	public static void main(String[] args){
		SimpleApplication app = new DisplayLandscapeSimulation();
		app.start(); // start the game
	}
}