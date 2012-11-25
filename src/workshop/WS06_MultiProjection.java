/*
 * プロジェクションマッピングのためのテンプレート
 *
 */

package workshop;
import net.unitedfield.cc.CaptureCameraNode;
import net.unitedfield.cc.PAppletProjectorNode;
import net.unitedfield.cc.PAppletProjectorShadowNode;
import processing.core.PApplet;
import test.p5.CapturePApplet;
import test.p5.CaptureSkewPApplet;
import test.p5.ColorBarsPApplet;
import test.p5.GravitySim;
import test.p5.HyperbolicCoral;
import test.p5.MikuFlipPApplet;
import test.p5.P_2_1_1_03;
import test.p5.P_2_2_5_01;
import test.p5.ParticleSphere;
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

public class WS06_MultiProjection extends SimpleApplication {
	PApplet applet1, applet2;
	Spatial object;
	int appletWidth, appletHeight;
	PAppletProjectorNode projectorNode1, projectorNode2;
	
	@Override
	// 初期設定
	public void simpleInitApp() {
		selectApplet();
		selectObject();
		setupEnvironment();
		setupProjector();
	}
	
	/******************* ここから下を設定 **************************/
	
	// 1. 投射するアプレットを選択
	private void selectApplet(){
		// Appletを選択
		// applet = new P_2_2_5_01();
		// applet = new P_2_1_1_03();
		// applet = new ColorBarsPApplet();
		// applet = new CapturePApplet();
		// applet = new GravitySim();
		// applet = new HyperbolicCoral();
		// applet = new MikuFlipPApplet();
		// applet = new SimpleGridPApplet();
		applet1 = new P_2_1_1_03();
		applet2 = new ParticleSphere();
		
		// 選択したアプレットの幅と高さを指定
		appletWidth = 400;
		appletHeight = 300;
	}
	
	// 2. 投射する建築物を選択
	private void selectObject(){
		// オブジェクトを読み込み
		//object = (Spatial) assetManager.loadModel("myAssets/Models/TokyoBigSite/TokyoBigSite.obj");
		//object = (Spatial) assetManager.loadModel("myAssets/Models/WaltDisneyConcertHall/WaltDisneyConcertHall.obj");
		//object = (Spatial) assetManager.loadModel("myAssets/Models/TokyoStation/TokyoStation.obj");
		//object = (Spatial) assetManager.loadModel("myAssets/Models/TheRedPyramid/TheRedPyramid.obj");
		object = (Spatial) assetManager.loadModel("myAssets/Models/TowerofTheSun/TowerofTheSun.obj");
		
		// 選択したオブジェクトをシーンに追加
		rootNode.attachChild(object);
		// プロジェクタの映を表示
		object.setShadowMode(ShadowMode.CastAndReceive);
	}
	
	// 3. プロジェクタ設定
	private void setupProjector(){
		// 新プロジェクター(1)を作成
		projectorNode1 = new PAppletProjectorNode("projector0", assetManager, applet1, appletWidth, appletHeight, true);
		// 建物にあわせて、プロジェクタの配置する位置を決定
		projectorNode1.setLocalTranslation(new Vector3f(0, 4f, 150));
		// 建物にあわせて、プロジェクタの視点(つまり投射する位置)を決定
		projectorNode1.lookAt(new Vector3f(0, 40f, 0f), Vector3f.UNIT_Y);
		// プロジェクタを追加
		rootNode.attachChild(projectorNode1);
		rootNode.attachChild(projectorNode1.getFrustmMdel());
		TextureProjectorRenderer ptr1 = new TextureProjectorRenderer(assetManager);
		ptr1.getTextureProjectors().add(projectorNode1.getProjector());
		viewPort.addProcessor(ptr1);
		
		// 新プロジェクター(2)を作成
		projectorNode2 = new PAppletProjectorNode("projector0", assetManager, applet2, appletWidth, appletHeight, true);
		// 建物にあわせて、プロジェクタの配置する位置を決定
		projectorNode2.setLocalTranslation(new Vector3f(0, 4f, -150));
		// 建物にあわせて、プロジェクタの視点(つまり投射する位置)を決定
		projectorNode2.lookAt(new Vector3f(0, 40f, 0f), Vector3f.UNIT_Y);
		// プロジェクタを追加
		rootNode.attachChild(projectorNode1);
		rootNode.attachChild(projectorNode1.getFrustmMdel());
		TextureProjectorRenderer ptr2 = new TextureProjectorRenderer(assetManager);
		ptr2.getTextureProjectors().add(projectorNode2.getProjector());
		viewPort.addProcessor(ptr2);
	}
	
	/******************* ここから下は必要に応じて設定 **************************/
	
	// 環境の設定
	private void setupEnvironment(){
		// カメラ設定
		cam.setLocation(new Vector3f(0, 10, 150));
		cam.lookAt(new Vector3f(10, 60, 0), Vector3f.UNIT_Y);
		flyCam.setMoveSpeed(20);
		flyCam.setDragToRotate(true);
		
		// ライティング
		DirectionalLight sun = new DirectionalLight();
		sun.setColor(ColorRGBA.White.mult(2.0f));
		sun.setDirection(new Vector3f(0f, 1.0f, -0.75f));
		rootNode.addLight(sun);
		AmbientLight al = new AmbientLight();
		al.setColor(ColorRGBA.White.mult(0.5f));
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
		int NUM = 100;
		Spatial[] girl = new Spatial[NUM];
		for(int i = 0; i < girl.length; i++) {
			girl[i] = assetManager.loadModel("myAssets/Models/WalkingGirl/WalkingGirl.obj");
			Vector3f girlPos = new Vector3f((float)(Math.random() * 160f - 80f), 0, (float)(Math.random() * 160f - 80f));
			girl[i].rotate(0, (float)(Math.random() * Math.PI/2.0f), 0);
			girl[i].setLocalTranslation(girlPos);
			this.rootNode.attachChild(girl[i]);
		}
		
		floorGeom.setShadowMode(ShadowMode.CastAndReceive);
	}
	
	@Override
	// 終了処理
	public void destroy() {
		super.destroy();
		System.exit(0);
	}
	
	// メイン
	public static void main(String[] args){
		SimpleApplication app = new WS06_MultiProjection();
		app.setPauseOnLostFocus(false); 
		app.start();
	}
}