package net.unitedfield.cc;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.texture.FrameBuffer;
import com.jme3.texture.Image.Format;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.MagFilter;
import com.jme3.texture.Texture.MinFilter;
import com.jme3.texture.Texture2D;

public class PointLightTextureShadowRenderer extends PointLightShadowRenderer{    

	public PointLightTextureShadowRenderer(AssetManager manager, int width, int height, Texture texture){
    	super(manager, width, height);  
        
        postshadowMat = new Material(manager, "myAssets/MatDefs/Shadow/PostTextureLightShadow.j3md");
        postshadowMat.setTexture("ShadowMap", shadowMap);        
        texture.setWrap(Texture.WrapMode.BorderClamp);
        texture.setMagFilter(MagFilter.Bilinear);
        texture.setMinFilter(MinFilter.Trilinear);
        postshadowMat.setTexture("ShadowTex", texture);               
    }	
}