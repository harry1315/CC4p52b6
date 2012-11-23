package net.unitedfield.cc;

import java.util.List;

import com.jme3.asset.AssetManager;
import com.jme3.bounding.BoundingVolume;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.collision.UnsupportedCollisionException;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Cylinder;

public class DistanceSensorNode extends Node {
	float limit;
	CollisionResults collisionResults;
	Geometry		boxGeometry;
	Geometry 		beamGeometry;
	BoundingVolume	beamBoundingVolume;
		
	public DistanceSensorNode(float limit, AssetManager assetManager){
		Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Blue); 
		
		Mesh box = new Box(0.05f,0.05f,0.05f);	
		this.boxGeometry = new Geometry("SensorBox", box);		
		this.boxGeometry.setMaterial(mat);  
		this.attachChild(boxGeometry);
		
		Mesh cylinder = new Cylinder(20,10,0.03f,limit);		
		this.beamGeometry = new Geometry("SensorBeam", cylinder);
		this.beamGeometry.setMaterial(mat);
		this.beamGeometry.move(0,0,limit/2-0.05f);
		this.attachChild(beamGeometry);
		this.beamBoundingVolume = this.getWorldBound();
	}
	
	public boolean senseCollision(Node targetsNode){
		if(this.collisionResults == null)
			this.collisionResults = new CollisionResults();
		else
			this.collisionResults.clear();
		
		int size = collideWith(targetsNode);
		if(size == 0){
			this.beamGeometry.getMaterial().setColor("Color", ColorRGBA.Blue);
			return false;
		}else{	
			this.beamGeometry.getMaterial().setColor("Color", ColorRGBA.Red);
			return true;
		}
	}
		
	protected int collideWith(Node targetsNode) throws UnsupportedCollisionException{
		List<Spatial> children = targetsNode.getChildren();
		for(Spatial child: children){
			try{
				if(child instanceof Geometry){
					child.collideWith(beamBoundingVolume,  this.collisionResults);
				}
				if(child instanceof Node){
					this.collideWith((Node)child);
				}
			}catch(UnsupportedCollisionException uce){
				throw uce;
			}
		}
		return this.collisionResults.size();
	}
	
	public	float getClosestDistance(){
		int size = this.collisionResults.size();
		if(size > 0){
			CollisionResult result = this.collisionResults.getClosestCollision();
			Vector3f dstPos = result.getGeometry().getWorldTranslation();
			return dstPos.distance(this.boxGeometry.getWorldTranslation());
		}else
			return 0;
	}
	
	public float getFarthestDistance(){
		int size = this.collisionResults.size();
		if(size > 0){
			CollisionResult result = this.collisionResults.getFarthestCollision();			
			Vector3f dstPos = result.getGeometry().getWorldTranslation();
			return dstPos.distance(this.boxGeometry.getWorldTranslation());
		}else
			return 0;
	}
}