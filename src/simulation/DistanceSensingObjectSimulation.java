package simulation;

import net.unitedfield.cc.DistanceSensorNode;
import net.unitedfield.cc.PAppletProjectorShadowNode;
import test.p5.ColorLight;

import com.jme3.app.SimpleApplication;
import com.jme3.font.BitmapText;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.light.SpotLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.system.AppSettings;


public class DistanceSensingObjectSimulation extends SimpleApplication {
	Node projectorNode;
	Node senseTarget;
	Node objectNode;
	Geometry[] boxes = new Geometry[20];
	Geometry girl;
	DistanceSensorNode distanceSensor;
	float rotPhase;
	PAppletProjectorShadowNode ppg;
	SpotLight spot;

	@Override
	public void simpleInitApp() {
		cam.setLocation(new Vector3f(-1f, 1.5f, -3f));
		//cam.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);
		cam.lookAt(new Vector3f(0, 1.8f, 5f), Vector3f.UNIT_Y);
		setupEnvironment();
		setupObject();
		setupProjector();
		setupDistanceSensor();
	}

	private void setupEnvironment() {
		/* lights */
		DirectionalLight sun = new DirectionalLight();
		Vector3f lightDir = new Vector3f(-0.12f, -0.3729129f, 0.74847335f);
		//Vector3f lightDir = new Vector3f(0, 0, 0.1f);
		sun.setDirection(lightDir);
		sun.setColor(ColorRGBA.White.clone().multLocal(0.25f));
		this.rootNode.addLight(sun);

		AmbientLight al = new AmbientLight();
		al.setColor(ColorRGBA.White.mult(0.1f));
		this.rootNode.addLight(al);

		/* floor */
		Material floor_mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md"); 
		floor_mat.setTexture("DiffuseMap", assetManager.loadTexture("myAssets/Textures/woodFloor.jpg"));
		floor_mat.setFloat("Shininess", 12);
		floor_mat.setBoolean("UseMaterialColors", true);
		floor_mat.setColor("Ambient",  ColorRGBA.White);
		floor_mat.setColor("Diffuse",  ColorRGBA.White);
		floor_mat.setColor("Specular", ColorRGBA.White);

		Box floor = new Box(Vector3f.ZERO, 10.0f, 0.01f, 10.0f);
		Geometry floorGeom = new Geometry("Floor", floor);
		floorGeom.setMaterial(floor_mat);
		floorGeom.setLocalTranslation(0, 0, 0);
		floorGeom.setShadowMode(ShadowMode.CastAndReceive);
		this.rootNode.attachChild(floorGeom);

		/* screen */
		Material screen_mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md"); 
		screen_mat.setFloat("Shininess", 12);
		screen_mat.setBoolean("UseMaterialColors", true);
		screen_mat.setColor("Ambient",  ColorRGBA.White);
		screen_mat.setColor("Diffuse",  ColorRGBA.White);
		screen_mat.setColor("Specular", ColorRGBA.White);

		Box screen = new Box(Vector3f.ZERO, 10f, 4f, 0.01f);
		Geometry screenGeomF = new Geometry("ScreenF", screen);
		screenGeomF.setLocalTranslation(new Vector3f(0f, 4f, 9.5f));
		screenGeomF.setMaterial(screen_mat);
		screenGeomF.setShadowMode(ShadowMode.Receive);
		rootNode.attachChild(screenGeomF);

		Geometry screenGeomR = new Geometry("ScreenR", screen);
		screenGeomR.setLocalTranslation(new Vector3f(9.5f, 4f, 0f));
		screenGeomR.rotate(0, (float)(Math.PI)*0.5f, 0);
		screenGeomR.setMaterial(screen_mat);
		screenGeomR.setShadowMode(ShadowMode.Receive);
		rootNode.attachChild(screenGeomR);

		Geometry screenGeomL = new Geometry("ScreenL", screen);
		screenGeomL.setLocalTranslation(new Vector3f(-9.5f, 4f, 0f));
		screenGeomL.rotate(0, (float)(Math.PI)*0.5f, 0);
		screenGeomL.setMaterial(screen_mat);
		screenGeomL.setShadowMode(ShadowMode.Receive);
		rootNode.attachChild(screenGeomL);
	}

	private void setupProjector() {
		projectorNode = new Node("ProjectorNod");
		rootNode.attachChild(projectorNode);

		spot = new SpotLight(); 
		spot = new SpotLight(); 
		spot.setSpotRange(500); 
		spot.setColor(ColorRGBA.White.multLocal(2.0f));
		spot.setSpotOuterAngle(30 * FastMath.DEG_TO_RAD); 
		spot.setSpotInnerAngle(15 * FastMath.DEG_TO_RAD); 
		spot.setPosition(new Vector3f(0, 2.2f, -2f));
		spot.setDirection(new Vector3f(0, 0, 1));
		rootNode.addLight(spot);

		ppg = new PAppletProjectorShadowNode("Projector", viewPort, assetManager, 1024, 1024, new ColorLight(), 200, 200,false);
		ppg.addToViewPort(viewPort);		
		projectorNode.attachChild(ppg);
		//projectorNode.rotate((float)(Math.PI)*0.5f, 0, 0);
		Vector3f spotPos = spot.getPosition();
		projectorNode.setLocalTranslation(new Vector3f(spotPos.x, spotPos.y - 2f, spotPos.z));
		
	}

	private void setupObject() {
		objectNode = new Node("ObjectNode");
		rootNode.attachChild(objectNode);
		objectNode.setLocalTranslation(new Vector3f(0, 2.2f, 4f));

		Material box_mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md"); 
		box_mat.setFloat("Shininess", 12);
		box_mat.setBoolean("UseMaterialColors", true);
		box_mat.setColor("Ambient",  new ColorRGBA(0.4f, 0.2f, 0.2f, 1f));
		box_mat.setColor("Diffuse",  new ColorRGBA(0.2f, 0.2f, 0.4f, 1f));
		box_mat.setColor("Specular", new ColorRGBA(0.2f, 0.4f, 0.2f, 1f));

		for(int i = 0; i < boxes.length; i++) {
			Box b = new Box(Vector3f.ZERO, 0.8f, 0.8f, 0.8f);			
			boxes[i] = new Geometry("Box" + i, b);
			boxes[i].setMaterial(box_mat);
			boxes[i].rotate(i * FastMath.DEG_TO_RAD * 2.0f, i * 2.2f * FastMath.DEG_TO_RAD, i * 2.3f * FastMath.DEG_TO_RAD);
			boxes[i].setShadowMode(ShadowMode.Receive);
			objectNode.attachChild(boxes[i]);			
		}
	}

	private void setupDistanceSensor() {
		senseTarget = new Node("senseTarget");
		rootNode.attachChild(senseTarget);

		Material m1 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		m1.setColor("Color", ColorRGBA.Blue);               
		distanceSensor = new DistanceSensorNode(5f, assetManager);
		distanceSensor.rotate(0, (float)(Math.PI), 0);
		distanceSensor.setLocalTranslation(
				objectNode.getLocalTranslation().x, 
				objectNode.getLocalTranslation().y - 1.2f,
				objectNode.getLocalTranslation().z);   
		rootNode.attachChild(distanceSensor);

		//girl
		Spatial girl = assetManager.loadModel("myAssets/Models/WalkingGirl/WalkingGirl.obj");
		girl.setShadowMode(ShadowMode.CastAndReceive);
		girl.setModelBound(girl.getWorldBound());
		senseTarget.attachChild(girl);
		// if ModelBound is too small, attach a box etc.
		//		Box b = new Box(Vector3f.ZERO, 0.1f, 0.1f, 0.1f);			
		//		Geometry box = new Geometry("tBox", b);
		//		box.setMaterial(m1);
		//		box.move(0, 1f, 0);
		//		senseTarget.attachChild(box);
		
		// Create input
		inputManager.addMapping("MoveRight", new KeyTrigger(KeyInput.KEY_J));
		inputManager.addMapping("MoveLeft", new KeyTrigger(KeyInput.KEY_K));
		//inputManager.addMapping("MoveUp", new KeyTrigger(KeyInput.KEY_I));
		//inputManager.addMapping("MoveDown", new KeyTrigger(KeyInput.KEY_K));
		inputManager.addMapping("MoveForward", new KeyTrigger(KeyInput.KEY_I));
		inputManager.addMapping("MoveBack", new KeyTrigger(KeyInput.KEY_M));
		inputManager.addListener(analogListener, new String[]{
				"MoveRight", "MoveLeft", "MoveUp", "MoveDown", "MoveForward", "MoveBack"
		});
		
        // Create debug text
        BitmapText helpText = new BitmapText(guiFont);
        helpText.setLocalTranslation(0, settings.getHeight(), 0);
        helpText.setText("J:Right, K:Left, I:Forward, M:Back");
        guiNode.attachChild(helpText);
	}

	private AnalogListener analogListener = new AnalogListener() {

		public void onAnalog(String name, float value, float tpf) {
			if (name.equals("MoveRight")) {
				senseTarget.move(2 * tpf, 0, 0);
			}
			if (name.equals("MoveLeft")) {
				senseTarget.move(-2 * tpf, 0, 0);
			}
			if (name.equals("MoveUp")) {
				senseTarget.move(0, 2 * tpf, 0);
			}
			if (name.equals("MoveDown")) {
				senseTarget.move(0, -2 * tpf, 0);
			}            
			if (name.equals("MoveForward")) {
				senseTarget.move(0, 0, 2 * tpf);
			}            
			if (name.equals("MoveBack")) {
				senseTarget.move(0, 0, -2 * tpf);
			}            
		}
	};

	@Override
	public void simpleUpdate(float tpf) {
		
		if(distanceSensor.senseCollision(senseTarget)) {
			float dist = 5f - distanceSensor.getClosestDistance();
			//System.out.println("closest distance: " + dist);

			for(int i = 0; i < boxes.length; i++) {
				boxes[i].rotate(
						i * FastMath.DEG_TO_RAD * dist / 400f, 
						i * FastMath.DEG_TO_RAD * dist * 1.3f / 400f, 
						i * FastMath.DEG_TO_RAD * dist * 1.5f / 400f
						);
				objectNode.rotate(0, i * FastMath.DEG_TO_RAD * dist / 400f, 0);
			}						
		} else {
			;//nothing to do
		}
	}

	@Override
	public void destroy() {
		super.destroy();
		System.exit(0);
	}

	public static void main(String[] args) {
		SimpleApplication app = new DistanceSensingObjectSimulation();		
		AppSettings s = new AppSettings(true);
		/*
		s.setWidth(640);
		s.setHeight(480);
		app.setSettings(s);
		app.setShowSettings(false);
		*/		
		app.start();
	}
}