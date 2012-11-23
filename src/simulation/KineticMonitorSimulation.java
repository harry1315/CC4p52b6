package simulation;

import test.p5.ColorLight;
import net.unitedfield.cc.PAppletDisplayGeometry;

import com.jme3.app.SimpleApplication;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.system.AppSettings;
import com.jme3.texture.Texture;

public class KineticMonitorSimulation extends SimpleApplication {

	PAppletDisplayGeometry [] displays = new PAppletDisplayGeometry[100];
	Node displayNode;
	
	@Override
	public void simpleInitApp()	{
		setupEnvironment();
		initDisplays();

		cam.setLocation(new Vector3f(6f, 2.2f, -8f));
		cam.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);
	}

	private void setupEnvironment() {
		//sun
		DirectionalLight sun = new DirectionalLight();
		Vector3f lightDir = new Vector3f(-0.12f, -0.3729129f, 0.74847335f);
		sun.setDirection(lightDir);
		sun.setColor(ColorRGBA.White.clone().multLocal(2));
		this.rootNode.addLight(sun);
		
		//floor
		Material mat_tex = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md"); 
		Texture tex = assetManager.loadTexture("myAssets/Textures/woodFloor.jpg"); 
		mat_tex.setTexture("ColorMap", tex); 
		Box floor = new Box(Vector3f.ZERO, 20.0f, 0.01f, 20.0f);
		Geometry floorGeom = new Geometry("Floor", floor);
		floorGeom.setMaterial(mat_tex);
		floorGeom.setLocalTranslation(0, 0, 0);
		rootNode.attachChild(floorGeom);
		
		//girl
		Spatial girl = assetManager.loadModel("myAssets/Models/WalkingGirl/WalkingGirl.obj");
		girl.rotate(0, (float)(Math.PI)*-0.3f, 0);
		girl.setLocalTranslation(5f, 0, -6f);
		this.rootNode.attachChild(girl);
	}

	private void initDisplays() {
		//Mesh sphere = new Sphere(4, 4, 0.2f);
		Mesh box = new Box(Vector3f.ZERO, 0.49f, 0.49f, 0.5f);
		
		displayNode = new Node("DisplayNode");
		displayNode.setLocalTranslation(-4.5f, 0f, -4.5f);
		rootNode.attachChild(displayNode);
		
		for(int j = 0; j < 10; j++) {
			for(int i = 0; i < 10; i++) {
				ColorLight app = new ColorLight();
				this.displays[i] = new PAppletDisplayGeometry("display" + i, box, assetManager, app, 200, 200, false);
				this.displayNode.attachChild(this.displays[i]);
				this.displays[i].move(1f * i, 1f, 1f * j);
				this.displays[i].rotate(-(float)Math.PI/2, (float)Math.PI/2, 0);
				
				// Add Control
				this.displays[i].addControl(new ControlSinMotion((float)(i * Math.PI / 3f + j * Math.PI / 4f)));
			}
		}
	}
		
	public void destroy() {
		super.destroy();
		System.exit(0);
	}
	
	public static void main(String[] args) {		
		SimpleApplication app = new KineticMonitorSimulation();				
		AppSettings s = new AppSettings(true);
		s.setWidth(1024);
		s.setHeight(768);
		app.setSettings(s);
		app.setShowSettings(false);
        app.start();
	}
}
