/*
 * 太陽の塔にプロジェクションマッピング
 *
 */

package simulation;
import net.unitedfield.cc.PAppletProjectorNode;
import net.unitedfield.cc.PAppletProjectorShadowNode;
import processing.core.PApplet;
import test.p5.ColorBarsPApplet;
import test.p5.P_2_1_1_03;
import test.p5.P_2_2_5_01;
import test.p5.SimpleGridPApplet;

import com.jme3.app.SimpleApplication;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.light.SpotLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.post.TextureProjectorRenderer;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.texture.Texture;
import com.jme3.util.SkyFactory;

public class Projection2ToweroftheSun extends SimpleApplication {
	int NUM = 100;
	Spatial[] girl = new Spatial[NUM];
	PApplet applet;
	PAppletProjectorShadowNode ppn;
	
	@Override
	// 初期設定
	public void simpleInitApp() {
		setupEnvironment();
		setupProjector();
	}
	
	// 環境の設定
	private void setupEnvironment(){
		// カメラ設定
		cam.setLocation(new Vector3f(0, 10, 150));
		cam.lookAt(new Vector3f(10, 60, 0), Vector3f.UNIT_Y);
		flyCam.setMoveSpeed(20);
		flyCam.setDragToRotate(true);
		
		// ライティング
		DirectionalLight sun = new DirectionalLight();
		sun.setColor(ColorRGBA.White.mult(1.5f));
		sun.setDirection(new Vector3f(0f, 1.0f, -0.75f));
		rootNode.addLight(sun);
		AmbientLight al = new AmbientLight();
		al.setColor(ColorRGBA.White.mult(0.1f));
		rootNode.addLight(al);
		
		// 空を設定
		Texture west = assetManager.loadTexture("Textures/Sky/Lagoon/lagoon_west.jpg");
		Texture east = assetManager.loadTexture("Textures/Sky/Lagoon/lagoon_east.jpg");
		Texture north = assetManager.loadTexture("Textures/Sky/Lagoon/lagoon_north.jpg");
		Texture south = assetManager.loadTexture("Textures/Sky/Lagoon/lagoon_south.jpg");
		Texture up = assetManager.loadTexture("Textures/Sky/Lagoon/lagoon_up.jpg");
		Texture down = assetManager.loadTexture("Textures/Sky/Lagoon/lagoon_down.jpg");
		Spatial sky = SkyFactory.createSky(assetManager, west, east, north, south, up, down);
		rootNode.attachChild(sky);
		
		// マテリアル
		Material textureMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		textureMat.setTexture("ColorMap", assetManager.loadTexture("myAssets/Textures/metalTexture.jpg"));
		
		// 地面を設定
		Box floor = new Box(Vector3f.ZERO, 1500f, 0.01f, 1500f);
		Geometry floorGeom = new Geometry("Floor", floor);
		floorGeom.setMaterial(textureMat);
		rootNode.attachChild(floorGeom);
		
		// ランダムに100人の女性を配置
		for(int i = 0; i < girl.length; i++) {
			girl[i] = assetManager.loadModel("myAssets/Models/WalkingGirl/WalkingGirl.obj");
			Vector3f girlPos = new Vector3f((float)(Math.random() * 160f - 80f), 0, (float)(Math.random() * 160f - 80f));
			girl[i].rotate(0, (float)(Math.random() * Math.PI/2.0f), 0);
			girl[i].setLocalTranslation(girlPos);
			this.rootNode.attachChild(girl[i]);
		}
		
		// 太陽の塔を配置
		Spatial object = (Spatial) assetManager.loadModel("myAssets/Models/TowerofTheSun/TowerofTheSun.obj");
		rootNode.attachChild(object);
		
		// プロジェクタの映像を影を表示
		object.setShadowMode(ShadowMode.CastAndReceive);
		floorGeom.setShadowMode(ShadowMode.CastAndReceive);
	}
	
	// プロジェクタ設定
	private void setupProjector(){
		// Appletを選択
		PApplet applet = new P_2_1_1_03();
		//PApplet applet = new P_2_2_5_01();
		//PApplet applet = new ColorBarsPApplet();
		
		// 新プロジェクターを配置
		PAppletProjectorNode projectorNode = new PAppletProjectorNode("projector0", assetManager, applet, 400, 400, true);
		projectorNode.setLocalTranslation(new Vector3f(0, 4f, 150));
		projectorNode.lookAt(new Vector3f(0, 40f, 0f), Vector3f.UNIT_Y);
		rootNode.attachChild(projectorNode);
		rootNode.attachChild(projectorNode.getFrustmMdel());
		TextureProjectorRenderer ptr = new TextureProjectorRenderer(assetManager);
		ptr.getTextureProjectors().add(projectorNode.getProjector());
		viewPort.addProcessor(ptr);
	}
	
	@Override
	// 終了処理
	public void destroy() {
		super.destroy();
		System.exit(0);
	}
	
	// メイン
	public static void main(String[] args){
		SimpleApplication app = new Projection2ToweroftheSun();
		app.setPauseOnLostFocus(false); 
		app.start();
	}
}