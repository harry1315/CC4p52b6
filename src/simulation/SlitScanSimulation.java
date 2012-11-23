package simulation;
import net.unitedfield.cc.CaptureCameraNode;
import net.unitedfield.cc.PAppletDisplayGeometry;
import net.unitedfield.cc.PAppletProjectorNode;
import test.p5.SlitScanPApplet;

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
import com.jme3.system.AppSettings;
import com.jme3.util.SkyFactory;

public class SlitScanSimulation extends SimpleApplication {
	SlitScanPApplet captureApplet;
	CaptureCameraNode captureCameraNode;
	float phase;
	int NUM = 40;
	Spatial[] 	girl = new Spatial[NUM];
	Vector3f[]	girlPos = new Vector3f[NUM];
	float[] 	girlSpeed = new float[NUM];
	Geometry[] 	wallGeom = new Geometry[6];

	@Override
	public void simpleInitApp() {
		cam.setFrustumPerspective(80f, 1f, 1f, 1000f);
		cam.setLocation(new Vector3f(2, 4.0f, 12));
		cam.lookAt(new Vector3f(0, 1.6f, 0), Vector3f.UNIT_Y);    

		setupCapture();
		//setupVirtualCameraDisplay();
		setupVirtualCameraProjector();
		setupEnvironments();
		setupGirls();
	}

	private void setupCapture(){
		captureApplet = new SlitScanPApplet();
		captureCameraNode = new CaptureCameraNode("", 400, 300, assetManager, renderManager, renderer, rootNode);		
		captureApplet.setCapture(captureCameraNode.getCapture());
	}

	private void setupVirtualCameraDisplay(){      
		PAppletDisplayGeometry display = new PAppletDisplayGeometry("display", assetManager, 20f, 4f, captureApplet, 400,300,true);		
		rootNode.attachChild(display);
		display.setLocalTranslation(new Vector3f(0,4f,-1.85f));
	}

	private void setupVirtualCameraProjector(){    
        PAppletProjectorNode projectorNode = new PAppletProjectorNode("projector0", assetManager, captureApplet, 400, 300, true);        
        rootNode.attachChild(projectorNode);
        rootNode.attachChild(projectorNode.getFrustmMdel()); // if you don't want to see frustum, please don't attach it to rootNode. 
        //projector should be added to TextureProjectorRenderer, and TextureProjectorRenderer should be added to ViewPort.
		TextureProjectorRenderer ptr = new TextureProjectorRenderer(assetManager);
		ptr.getTextureProjectors().add(projectorNode.getProjector());
		viewPort.addProcessor(ptr);
		
        projectorNode.setLocalTranslation(new Vector3f(0,4,10));			// move the projector,
        projectorNode.lookAt(new Vector3f(0, 4, 0), Vector3f.UNIT_Y);   // and make it to look at where you want.        	
	}
	
	private void setupGirls(){
		for(int i = 0; i < girl.length; i++) {
			//girl
			girl[i] = assetManager.loadModel("myAssets/Models/WalkingGirl/WalkingGirl.obj");
			girlPos[i] = new Vector3f((float)(Math.random() * 16.0f - 8f), 0, (float)(Math.random() * 8f));
			girlSpeed[i] = (float)(Math.random() * -0.02f) + 0.01f;
			if(girlSpeed[i] < 0){
				girl[i].rotate(0, (float)(-Math.PI/2.0f), 0);
			} else {
				girl[i].rotate(0, (float)(Math.PI/2.0f), 0);
			}
			girl[i].setLocalTranslation(girlPos[i]);
			this.rootNode.attachChild(girl[i]);
		}
	}

	private void setupEnvironments() {
		//cam
		flyCam.setDragToRotate(true);
		//materials
		Material textureMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		textureMat.setTexture("ColorMap", assetManager.loadTexture("myAssets/Textures/woodFloor.jpg"));
		Material grayMat = assetManager.loadMaterial("myAssets/Materials/GrayColor.j3m");
		Material whitemat = assetManager.loadMaterial("Common/Materials/WhiteColor.j3m");  
		
		// sun
		DirectionalLight dl = new DirectionalLight();
		dl.setColor(ColorRGBA.White);
		dl.setDirection(Vector3f.UNIT_XYZ.negate());
		rootNode.addLight(dl);
		
		// sky
		rootNode.attachChild(SkyFactory.createSky(assetManager, "Textures/Sky/Bright/FullskiesBlueClear03.dds", false));
		
		//floor
		Box floor = new Box(Vector3f.ZERO, 20.0f, 0.01f, 20.0f);
		Geometry floorGeom = new Geometry("Floor", floor);
		floorGeom.setMaterial(textureMat);
		floorGeom.setLocalTranslation(0, 0, 0);
		rootNode.attachChild(floorGeom);
		
		//wall
		wallGeom[0] = new Geometry("Wall1", new Box(Vector3f.ZERO, 20f, 4f, 0.1f));
		wallGeom[1] = new Geometry("Wall2", new Box(Vector3f.ZERO, 20f, 4f, 0.1f));
		wallGeom[2] = new Geometry("Wall3", new Box(Vector3f.ZERO, 20f, 4f, 0.1f));
		wallGeom[0].setMaterial(whitemat);
		wallGeom[1].setMaterial(whitemat);
		wallGeom[2].setMaterial(whitemat);
		wallGeom[0].setLocalTranslation(0, 4, -2);
		wallGeom[1].setLocalTranslation(-20, 4, 0);
		wallGeom[2].setLocalTranslation(20, 4, 0);
		wallGeom[1].rotate(0, (float)(Math.PI/2f), 0);
		wallGeom[2].rotate(0, (float)(Math.PI/2f), 0);
		rootNode.attachChild(wallGeom[0]);
		rootNode.attachChild(wallGeom[1]);
		rootNode.attachChild(wallGeom[2]);
						
		// shadow & projection
		floorGeom.setShadowMode(ShadowMode.CastAndReceive);
		wallGeom[0].setShadowMode(ShadowMode.CastAndReceive);
		wallGeom[1].setShadowMode(ShadowMode.CastAndReceive);
		wallGeom[2].setShadowMode(ShadowMode.CastAndReceive);
		//girl[0].setShadowMode(ShadowMode.CastAndReceive);
	}

	public void destroy() {
		super.destroy();
		System.exit(0);
	}

	@Override
	public void simpleUpdate(float tpf) {
		for(int i = 0; i < girl.length; i++) {
			girlPos[i].x += girlSpeed[i];
			if(girlPos[i].x < -8.0f){
				girlPos[i].x = 8.0f;
			}
			girl[i].setLocalTranslation(girlPos[i]);
		}
		phase += 0.0001f;
	}

	public static void main(String[] args){
		SimpleApplication app = new SlitScanSimulation();
		AppSettings s = new AppSettings(true);
		s.setWidth(1024);
		s.setHeight(768);
		app.setSettings(s);
		app.setShowSettings(false);
		app.setPauseOnLostFocus(false); // call this method in order not to pause when click a window for applet.		
		app.start();
	}
}
