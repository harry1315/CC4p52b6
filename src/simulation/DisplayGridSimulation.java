package simulation;

import test.p5.ColorBarsPApplet;
import net.unitedfield.cc.PAppletDisplayGeometry;

import com.jme3.app.SimpleApplication;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Spatial;
import com.jme3.scene.debug.Grid;
import com.jme3.util.SkyFactory;

public class DisplayGridSimulation extends SimpleApplication  {
	PAppletDisplayGeometry[] player = new PAppletDisplayGeometry[80];

	@Override
	public void simpleInitApp() {
		//sky
		rootNode.attachChild(SkyFactory.createSky(assetManager, "Textures/Sky/Bright/FullskiesBlueClear03.dds", false));

		//light
		DirectionalLight dl = new DirectionalLight();
		dl.setDirection(new Vector3f(-0.1f, -1f, -1).normalizeLocal());
		rootNode.addLight(dl);	

		//girl
		for(int i = 0; i<30; i++){
			Spatial girl = assetManager.loadModel("myAssets/Models/WalkingGirl/WalkingGirl.obj");
			girl.rotate(0, (float)(Math.random()) * 180f, 0);
			girl.setLocalTranslation((float)(Math.random()) * 20f - 10f, 0, (float)(Math.random()) * 20f - 10f);
			this.rootNode.attachChild(girl);
		}

		//screen
		for(int i = 0; i<player.length; i++){
			this.player[i] = new PAppletDisplayGeometry("display"+i, assetManager, 0.8f, 0.8f, 					
					new ColorBarsPApplet(), 200, 200, false);
			rootNode.attachChild(player[i]);
			player[i].setLocalTranslation((i % 8) - 3f, 3f, (i / 8) - 3f);
			player[i].rotate((float) (Math.PI / 2.0), 0, 0);
		}

		//grid
		putGrid(new Vector3f(0, 0, 0), ColorRGBA.White);
	}

	public void putGrid(Vector3f pos, ColorRGBA color){
		putShape(new Grid(200, 200, 1.0f), color).center().move(pos);
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
	
	/* This is the update loop */
	/*
	@Override
	public void simpleUpdate(float tpf) {
		for(int i = 0; i<player.length; i++){
			player[i].rotate(i*tpf/40, i*tpf/20, 0);
		}
	}
	*/
	
	public void destroy() {
		super.destroy();
		System.exit(0);
	}
	
	public static void main(String[] args){
		SimpleApplication app = new DisplayGridSimulation();
		app.start();
	}
}