package simulation;

import net.unitedfield.cc.PAppletProjectorNode;
import net.unitedfield.cc.PAppletProjectorShadowNode;
import processing.core.PApplet;
import test.p5.WaveGradient;

import com.jme3.app.SimpleApplication;
import com.jme3.light.DirectionalLight;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.post.TextureProjectorRenderer;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Spatial;
import com.jme3.system.AppSettings;
import com.jme3.util.SkyFactory;

public class P52ProjectionSimulation extends SimpleApplication {
	PAppletProjectorShadowNode projectionShadowNode;
	PAppletProjectorNode 		projectorNode;
	
	@Override
	public void simpleInitApp() {      
		// sun
		DirectionalLight dl1 = new DirectionalLight();
	    dl1.setDirection(new Vector3f(-1f, -1f, -1f).normalizeLocal());
	    rootNode.addLight(dl1);
		DirectionalLight dl2 = new DirectionalLight();
	    dl2.setDirection(new Vector3f(1f, 1f, 1).normalizeLocal());
	    rootNode.addLight(dl2);
	    // sky
	    rootNode.attachChild(SkyFactory.createSky(assetManager, "Textures/Sky/Bright/FullskiesBlueClear03.dds", false));
	    	    
	    // model
	    Spatial model = assetManager.loadModel("myAssets/Models/WALT_DISNEY_CONERT_HALL/WALT_DISNEY_CONERT_HALL.obj");
	    rootNode.attachChild(model);
	                         
	    // projector	     	    
	    PApplet applet = new WaveGradient();
	    //Projection and Shadow
	    projectionShadowNode = new PAppletProjectorShadowNode("projector0", viewPort, assetManager, 1024, 768, applet, 640, 360, true); 	    
	    projectionShadowNode.setLocalTranslation(100.0f, 50.0f, 100.0f);
	    rootNode.attachChild(projectionShadowNode);	    

	    //Projection only	    
//        projectorNode = new PAppletProjectorNode("projector1", assetManager, applet, 640, 360, true);        
//        rootNode.attachChild(projectorNode);
//        rootNode.attachChild(projectorNode.getFrustmMdel()); // if you don't want to see frustum, please don't attach it to rootNode.         
//		TextureProjectorRenderer ptr = new TextureProjectorRenderer(assetManager);
//		ptr.getTextureProjectors().add(projectorNode.getProjector());
//		viewPort.addProcessor(ptr); //projector should be added to TextureProjectorRenderer, and TextureProjectorRenderer should be added to ViewPort.
//		projectorNode.setLocalTranslation(50,  40, 70);
//		projectorNode.lookAt(new Vector3f(100, 40, 0), Vector3f.UNIT_Y);
	    
	    /* 
	     * According to ShadowMode values, nodes works as a screen or not.
	     */
	    /* if you want to set ShadowMode to all nodes at once, set it to the rootNode. */
	    //rootNode.setShadowMode(ShadowMode.CastAndReceive);        
	    /* if you want to set different ShadowMode to each nodes, set modes as follows. */ 
	    rootNode.setShadowMode(ShadowMode.Off);
	    model.setShadowMode(ShadowMode.CastAndReceive);
	    
        flyCam.setMoveSpeed(30);
		cam.setLocation(new Vector3f(120,60,120));
		cam.lookAt(Vector3f.ZERO, Vector3f.UNIT_Y);			
	}
		
	float angle = 0;
	public void simpleUpdate(float tpf){
		angle +=0.001;
		if(projectionShadowNode != null){
			Quaternion q = projectionShadowNode.getLocalRotation();
			projectionShadowNode.setLocalRotation(q.fromAngles(angle, 0, angle));
		}
		if(projectorNode != null){			
			projectorNode.lookAt(new Vector3f((float)(100*Math.cos(angle)), 0, (float)(100*Math.sin(angle))), Vector3f.UNIT_Y);
		}
	}
	
	public void destroy() {
		super.destroy();
		System.exit(0);
	}

	public static void main(String[] args) {
		P52ProjectionSimulation app = new P52ProjectionSimulation();
		AppSettings s = new AppSettings(true);		
		s.setWidth(1024);
		s.setHeight(768);
		app.setSettings(s);
		app.setShowSettings(false);			
		app.setPauseOnLostFocus(false); // call this method in order not to pause when click a window for applet.		
		app.start();
	}
}
