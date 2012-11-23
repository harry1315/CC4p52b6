package simulation;

import net.unitedfield.cc.PAppletProjectorShadowNode;
import processing.core.PApplet;
import test.p5.SimpleGridPApplet;

import com.jme3.app.SimpleApplication;
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
import com.jme3.shadow.BasicShadowRenderer;
import com.jme3.util.TangentBinormalGenerator;

public class Projection2DisneyHallSimulation extends SimpleApplication {
    int NUM = 100;
    Spatial[] girl = new Spatial[NUM];
    PApplet applet;
    BasicShadowRenderer bsr;

    @Override
    public void simpleInitApp() {
        setupScene();
        setupProjector();
        setupEnvironment();
    }

    private void setupProjector() {
        applet = new SimpleGridPApplet();

        // projector  
        PAppletProjectorShadowNode ppg = new PAppletProjectorShadowNode("Projector", viewPort, assetManager, 1024, 768, applet, 400,300,false);
        ppg.setLocalTranslation(new Vector3f(80f, 5f, 80f));
        ppg.lookAt(new Vector3f(0f, 10f, 0f), Vector3f.UNIT_Y);
        rootNode.attachChild(ppg);
        
        SpotLight spot = new SpotLight(); 
		spot = new SpotLight(); 
		spot.setSpotRange(2000); 
		spot.setColor(ColorRGBA.White.clone().multLocal(4));
		spot.setSpotOuterAngle(20 * FastMath.DEG_TO_RAD); 
		spot.setSpotInnerAngle(10 * FastMath.DEG_TO_RAD); 
		spot.setPosition(ppg.getLocalTranslation());
		spot.setDirection(new Vector3f(-1.0f,0.2f,-1f));
		//spot.setDirection(new Vector3f(-0.5, -0.5, -0.5).normalizeLocal());
		rootNode.addLight(spot);
		
		Vector3f ppgPos = ppg.getLocalTranslation();
		cam.setLocation(new Vector3f(ppgPos.x, ppgPos.y, ppgPos.z - 10f));
    }

    private void setupEnvironment() {
        /* cam */
        cam.setFrustumPerspective(40f, 1f, 1f, 1000f);
        //cam.setLocation(new Vector3f(80f, 3f, 150f));
        cam.lookAt(new Vector3f(0f, 20f, 0f), Vector3f.UNIT_Y);
        flyCam.setMoveSpeed(20);
        
        /* sun */
        DirectionalLight sun = new DirectionalLight();
		Vector3f lightDir = new Vector3f(-0.2f, -0.3729129f, -0.74847335f);
		sun.setDirection(lightDir);
		sun.setColor(ColorRGBA.White.clone().multLocal(0.5f));
		this.rootNode.addLight(sun);
		
		AmbientLight al = new AmbientLight();
		al.setColor(ColorRGBA.White.mult(1f));
		rootNode.addLight(al);

        /* sky */
        //rootNode.attachChild(SkyFactory.createSky(assetManager, "Textures/Sky/Bright/FullskiesBlueClear03.dds", false));
        //rootNode.attachChild(SkyFactory.createSky(assetManager, "Textures/Sky/Bright/BrightSky.dds", false));

        /* terrain */
        Material mat = assetManager.loadMaterial("Textures/Terrain/Rocky/Rocky.j3m");
        Spatial scene = assetManager.loadModel("Models/Terrain/Terrain.mesh.xml");
        TangentBinormalGenerator.generate(((Geometry)((Node)scene).getChild(0)).getMesh());
        scene.setMaterial(mat);
        //scene.setShadowMode(ShadowMode.Receive);
        scene.setLocalScale(1200);
        scene.setLocalTranslation(0, 0, -120);
        rootNode.attachChild(scene);
    }

    private void setupScene() {
    	bsr = new BasicShadowRenderer(assetManager, 4096);
		bsr.setDirection(new Vector3f(-.5f,-.5f,-.5f).normalizeLocal()); // light direction
		//viewPort.addProcessor(bsr);
		
        /* girls */
        for (int i = 0; i < girl.length; i++) {
            girl[i] = assetManager.loadModel("myAssets/Models/WalkingGirl/WalkingGirl.obj");
            Vector3f girlPos = new Vector3f((float) (Math.random() * 160f - 80f), 1.0f, (float) (Math.random() * 160f - 80f));
            girl[i].rotate(0, (float) (Math.random() * Math.PI / 2.0f), 0);
            girl[i].setLocalTranslation(girlPos);
            girl[i].setShadowMode(ShadowMode.CastAndReceive);
            this.rootNode.attachChild(girl[i]);
        }
        
        /* object material */
        Material whitemat = assetManager.loadMaterial("Common/Materials/WhiteColor.j3m");  
        Material mat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
		//mat.setFloat("Shininess", 12);
		//mat.setTexture("DiffuseMap", assetManager.loadTexture("Textures/metalTexture.jpg"));
		//mat.setBoolean("UseMaterialColors", true);
		//mat.setBoolean("VertexLighting", true);
		//mat.setBoolean("HighQuality", true);
		mat.setColor("Ambient",  ColorRGBA.White.mult(0.5f));
		mat.setColor("Diffuse",  ColorRGBA.White.mult(0.5f));
		mat.setColor("Specular", ColorRGBA.White.mult(0.5f));

        /* object */
        Spatial object = (Spatial) assetManager.loadModel("myAssets/Models/WALT_DISNEY_CONERT_HALL/WALT_DISNEY_CONERT_HALL.obj");
        object.setMaterial(mat);
        rootNode.attachChild(object);
        object.setShadowMode(ShadowMode.Receive);
    }

    @Override
    public void destroy() {
        super.destroy();
        System.exit(0);
    }
    
    public static void main(String[] args) {
        SimpleApplication app = new Projection2DisneyHallSimulation();
        app.start();
    }
}