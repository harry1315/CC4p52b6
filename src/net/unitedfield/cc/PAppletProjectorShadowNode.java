package net.unitedfield.cc;

import java.awt.BorderLayout;
import java.nio.ByteBuffer;

import javax.swing.JFrame;

import processing.core.PApplet;

import com.jme3.asset.AssetManager;
import com.jme3.material.MatParamTexture;
import com.jme3.material.Material;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Cylinder;
import com.jme3.scene.shape.Sphere;
import com.jme3.texture.Image;
import com.jme3.texture.Image.Format;
import com.jme3.texture.Texture;

/**
 * @author naka 
 **/
public class PAppletProjectorShadowNode extends Node{
	private Geometry geom;
	private	PApplet	applet;
	private	Image	appletImage;
	private	ByteBuffer abgrPixelData;
	public	PointLightTextureShadowRenderer ptsr;
	
	public int projectionWidth, projectionHeight;
	public boolean frameVisible = true;	
	private	JFrame	frame;
	
	public PAppletProjectorShadowNode(String name, AssetManager assetManager){
		super(name);
		
		Material mat = new Material(assetManager,"Common/MatDefs/Misc/ShowNormals.j3md");
//		Mesh sphere = new Sphere(10,10,0.2f);
//		geom = new Geometry();
//		geom.setMaterial(mat);
//		geom.setMesh(sphere);
//		this.attachChild(geom);
		
		Mesh box = new Box(Vector3f.ZERO, 0.2f, 0.1f, 0.2f);
		geom = new Geometry();
		geom.setMaterial(mat);
		geom.setMesh(box);
		
		Mesh cylinder = new Cylinder(10, 10, 0.05f, 0.1f);
		Geometry cylinderg = new Geometry();
		cylinderg.setMaterial(mat);
		cylinderg.setMesh(cylinder);
		
		cylinderg.setLocalTranslation(0, 0, 0.2f);
		this.attachChild(cylinderg);
		this.attachChild(geom);		
	}
	
	public PAppletProjectorShadowNode(String name, ViewPort viewPort, AssetManager assetManager, int projectionWidth, int projectionHeight, Texture texture){
		this(name, assetManager);					

		this.projectionWidth  = projectionWidth;
		this.projectionHeight = projectionHeight;
		this.ptsr = new PointLightTextureShadowRenderer(assetManager, projectionWidth, projectionHeight, texture);		
		viewPort.addProcessor(this.ptsr);
	}
	
	public PAppletProjectorShadowNode(String name, ViewPort viewPort, AssetManager assetManager, int projectionWidth, int projectionHeight, 
			PApplet applet,int appletFrameWidth, int appletFrameHeight, boolean frameVisible){
		
		this(name, viewPort, assetManager, projectionWidth, projectionHeight, assetManager.loadTexture("Interface/Logo/Monkey.jpg")); 
	
		this.frameVisible = frameVisible;
		this.frame = new JFrame();
		this.frame.setLayout(new BorderLayout());
		this.frame.setSize(appletFrameWidth, appletFrameHeight);        
        if(frameVisible)
        	this.frame.setVisible(true);
        this.frame.add(applet, BorderLayout.CENTER);
        this.frame.setResizable(false);
        
		this.applet = applet;
		this.applet.registerMethod("pre", this);
		this.applet.registerMethod("post", this);		
		
		this.applet.init();
	}


	public void pre() {
		this.applet.unregisterMethod("pre", this);
		//* after unregistaration, this pre() is not called from the PApplet. Therefore, following methods are called only once.
		this.abgrPixelData = ByteBuffer.allocateDirect(this.applet.width*this.applet.height*4);
		this.appletImage = new Image(Format.ABGR8, this.applet.width, this.applet.height, this.abgrPixelData);
	}	
	public void post(){	
		this.applet.loadPixels();
	    Material material = this.ptsr.getPostShadowMat();
		MatParamTexture matParamTexture = material.getTextureParam("ShadowTex");
		if(matParamTexture != null && this.abgrPixelData != null){
			this.abgrPixelData.clear();
			int c = 0;
			int w = this.applet.width;
			int len = this.applet.height * this.applet.width;
			
			//* if we simply put color to the buffer as follows,
			//for(int i=0; i <this.applet.width*this.applet.height; i++){
			//	int color = this.applet.pixels[i];
			//}
			//* the image is flipped, therefore, updated as follows			
			for(int i=0; i <len ; i+=w){
				for (int j = w-1; j >= 0; j--) {
					int color = this.applet.pixels[len-1-(i+j)];				
					int a = (color >> 24) & 0xff;
					int r = (color >> 16) & 0xff;
					int g = (color >> 8) & 0xff;
					int b = color & 0xff;
					int abgrColor = (a << 24) | (b << 16) | (g << 8) | r;
					this.abgrPixelData.asIntBuffer().put(c, abgrColor);
					c++;
				}
			}			
			
			this.appletImage.setData(this.abgrPixelData);
			matParamTexture.getTextureValue().setImage(this.appletImage);
		}
	}
	
	public void addToViewPort(ViewPort viewport){
		viewport.addProcessor(ptsr);
	}
		
    public void updateWorldTransforms(){
    	super.updateWorldTransforms();
        ptsr.setLocation(worldTransform.getTranslation());
    	ptsr.setDirection(worldTransform.getRotation()); 	
    }
    
    public void lookAt(Vector3f destination, Vector3f up){
    	super.lookAt(destination, up);
    	ptsr.lookAt(destination, up);
    }
    
}
