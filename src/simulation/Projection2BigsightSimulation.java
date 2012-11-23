package simulation;
import net.unitedfield.cc.PAppletProjectorNode;
import net.unitedfield.cc.PAppletProjectorShadowNode;
import processing.core.PApplet;
import test.p5.SimpleGridPApplet;

import com.jme3.app.SimpleApplication;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.post.TextureProjectorRenderer;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.texture.Texture;
import com.jme3.util.SkyFactory;

public class Projection2BigsightSimulation extends SimpleApplication {
	int NUM = 100;
	Spatial[] girl = new Spatial[NUM];
	PApplet applet;

	@Override
	public void simpleInitApp() {
		setupScene();
		setupProjector();
	}

	private void setupProjector(){
		applet = new SimpleGridPApplet();
		//projector 
//		PAppletProjectorShadowNode ppn = new PAppletProjectorShadowNode("Projector", viewPort, assetManager, 1024, 768, applet, 400,300, false);
//		rootNode.attachChild(ppn);
//		ppn.setLocalTranslation(new Vector3f(0f, 4f, 180f));
//		ppn.lookAt(new Vector3f(0f, 90f, 0f), Vector3f.UNIT_Y);
				
		PAppletProjectorNode projectorNode = new PAppletProjectorNode("projector0", assetManager, applet, 400, 300, true);        
        rootNode.attachChild(projectorNode);
        rootNode.attachChild(projectorNode.getFrustmMdel()); // if you don't want to see frustum, please don't attach it to rootNode. 
        //projector should be added to TextureProjectorRenderer, and TextureProjectorRenderer should be added to ViewPort.
		TextureProjectorRenderer ptr = new TextureProjectorRenderer(assetManager);
		ptr.getTextureProjectors().add(projectorNode.getProjector());
		viewPort.addProcessor(ptr);
		
        projectorNode.setLocalTranslation(new Vector3f(0, 0, 180));	     // move the projector,
        projectorNode.lookAt(new Vector3f(0, 30, 0), Vector3f.UNIT_Y);   // and make it to look at where you want.
	}

	private void setupScene(){
		//cam
		//cam.setFrustumPerspective(60f, 1f, 1f, 1000f);
		cam.setLocation(new Vector3f(0f, 10f, 200f));
		cam.lookAt(new Vector3f(0f, 20f, 0f), Vector3f.UNIT_Y);
		flyCam.setMoveSpeed(20);

		//materials
		Material textureMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		textureMat.setTexture("ColorMap", assetManager.loadTexture("myAssets/Textures/metalTexture.jpg"));
		// sky
		//rootNode.attachChild(SkyFactory.createSky(assetManager, "Textures/Sky/Bright/FullskiesBlueClear03.dds", false));
		Texture west = assetManager.loadTexture("Textures/Sky/Lagoon/lagoon_west.jpg");
		Texture east = assetManager.loadTexture("Textures/Sky/Lagoon/lagoon_east.jpg");
		Texture north = assetManager.loadTexture("Textures/Sky/Lagoon/lagoon_north.jpg");
		Texture south = assetManager.loadTexture("Textures/Sky/Lagoon/lagoon_south.jpg");
		Texture up = assetManager.loadTexture("Textures/Sky/Lagoon/lagoon_up.jpg");
		Texture down = assetManager.loadTexture("Textures/Sky/Lagoon/lagoon_down.jpg");

		Spatial sky = SkyFactory.createSky(assetManager, west, east, north, south, up, down);
		rootNode.attachChild(sky);

		Box floor = new Box(Vector3f.ZERO, 1500f, 0.01f, 1500f);
		Geometry floorGeom = new Geometry("Floor", floor);
		floorGeom.setMaterial(textureMat);
		rootNode.attachChild(floorGeom);
		//floorGeom.setShadowMode(ShadowMode.CastAndReceive);

		//sun
		DirectionalLight sun = new DirectionalLight();
		sun.setColor(ColorRGBA.White);
		sun.setDirection(new Vector3f(-.5f,-.5f,-.5f).normalizeLocal());
		rootNode.addLight(sun);

		//girls
		for(int i = 0; i < girl.length; i++) {
			girl[i] = assetManager.loadModel("myAssets/Models/WalkingGirl/WalkingGirl.obj");
			Vector3f girlPos = new Vector3f((float)(Math.random() * 160f - 80f), 0, (float)(Math.random() * 160f - 80f));
			girl[i].rotate(0, (float)(Math.random() * Math.PI/2.0f), 0);
			girl[i].setLocalTranslation(girlPos);
			this.rootNode.attachChild(girl[i]);
		}

		//object
		Spatial object = (Spatial) assetManager.loadModel("myAssets/Models/bigsight/bigsight.obj");
		object.setMaterial(textureMat);
		rootNode.attachChild(object);
		object.setShadowMode(ShadowMode.CastAndReceive);
	}

	public void destroy() {
		super.destroy();
		System.exit(0);
	}
	
	public static void main(String[] args){
		SimpleApplication app = new Projection2BigsightSimulation();
		app.start(); // start the game
	}

}