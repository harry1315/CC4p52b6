package net.unitedfield.cc;

import static java.lang.Math.max;
import static java.lang.Math.min;

import java.util.ArrayList;

import com.jme3.asset.AssetManager;
import com.jme3.bounding.BoundingBox;
import com.jme3.bounding.BoundingVolume;
import com.jme3.material.Material;
import com.jme3.math.Matrix4f;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.post.SceneProcessor;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.Renderer;
import com.jme3.renderer.ViewPort;
import com.jme3.renderer.queue.GeometryList;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.shadow.ShadowUtil;
import com.jme3.texture.FrameBuffer;
import com.jme3.texture.Image.Format;
import com.jme3.texture.Texture2D;
import com.jme3.ui.Picture;
import com.jme3.util.TempVars;

/**
 * @author naka
 */
public class PointLightShadowRenderer implements SceneProcessor {

	protected RenderManager renderManager;
	protected ViewPort viewPort;

	protected FrameBuffer shadowFB;
	protected Texture2D shadowMap;
	protected Camera shadowCam;

	protected Material preshadowMat;
	protected Material postshadowMat;

	protected Picture dispPic = new Picture("Picture");
	protected boolean noOccluders = false;

	protected Vector3f location = new Vector3f();
	protected Vector3f[] points = new Vector3f[8];
	
	protected Quaternion direction = new Quaternion();
	//protected Vector3f direction = new Vector3f();
	
	float fov = 30;
	float near = 1, far = 1000;
		
	public PointLightShadowRenderer(AssetManager manager, int width, int height) {
		shadowCam = new Camera(width, height);
		shadowCam.setFrustumPerspective(fov, (float)width/height, near, far);

		shadowFB = new FrameBuffer(width, height, 1); 
		shadowMap = new Texture2D(width, height, Format.Depth);
		shadowFB.setDepthTexture(shadowMap);

		preshadowMat  = new Material(manager, "Common/MatDefs/Shadow/PreShadow.j3md");
		postshadowMat = new Material(manager, "Common/MatDefs/Shadow/PostShadow.j3md");
		postshadowMat.setTexture("ShadowMap", shadowMap);

		dispPic.setTexture(manager, shadowMap, false);

		for (int i = 0; i < points.length; i++) {
			points[i] = new Vector3f();
		}
	}

	public void initialize(RenderManager rm, ViewPort vp) {
		renderManager = rm;
		viewPort = vp;

		reshape(vp, vp.getCamera().getWidth(), vp.getCamera().getHeight());
	}

	public boolean isInitialized() {
		return viewPort != null;
	}

	public void lookAt(Vector3f destination, Vector3f up) {
		shadowCam.lookAt(destination, up);
	}
	public Quaternion getDirection() {
		return direction;
	}
	public void setDirection(Quaternion direction) {
		//this.direction.set(direction).normalizeLocal();
		this.shadowCam.setRotation(direction);		
	}

	public void setLocation(Vector3f location) {
		this.location = location;
	}
	public void setLocation(float x, float y, float z){
		this.location = new Vector3f(x,y,z);
	}
	public void setLocalTranslation(Vector3f translation) {
		this.location.addLocal(translation);
	}

	public Vector3f getLocation() {
		return location;
	}

	public Vector3f[] getPoints() {
		return points;
	}
	
	public Camera getShadowCamera() {
		return shadowCam;
	}
	
	public Material getPostShadowMat(){
		return postshadowMat;
	}

	public void postQueue(RenderQueue rq) {
		GeometryList occluders = rq.getShadowQueueContent(ShadowMode.Cast);
		if (occluders.size() == 0) {
			noOccluders = true;
			return;
		} else {
			noOccluders = false;
		}

		GeometryList receivers = rq.getShadowQueueContent(ShadowMode.Receive);

		// update frustum points based on current camera
		Camera viewCam = viewPort.getCamera();
		shadowCam.setFrustumPerspective(fov, (float)shadowCam.getWidth()/shadowCam.getHeight(), near, far); 
				
		//shadowCam.setRotation(direction);
		shadowCam.update();
		shadowCam.setLocation(location);
		shadowCam.update();
		shadowCam.updateViewProjection();

		// render shadow casters to shadow map
		//ShadowUtil.updateShadowCamera(occluders, receivers, shadowCam, points);
		this.updateShadowCamera(occluders, receivers, shadowCam, points, null);

		Renderer r = renderManager.getRenderer();
		renderManager.setCamera(shadowCam, false);
		renderManager.setForcedMaterial(preshadowMat);

		r.setFrameBuffer(shadowFB);
		r.clearBuffers(false, true, false);
		viewPort.getQueue().renderShadowQueue(ShadowMode.Cast, renderManager, shadowCam, true);
		r.setFrameBuffer(viewPort.getOutputFrameBuffer());

		renderManager.setForcedMaterial(null);
		renderManager.setCamera(viewCam, false);
	}

	public void postFrame(FrameBuffer out) {
		if (!noOccluders) {
			postshadowMat.setMatrix4("LightViewProjectionMatrix",
					shadowCam.getViewProjectionMatrix());
			renderManager.setForcedMaterial(postshadowMat);
			viewPort.getQueue().renderShadowQueue(ShadowMode.Receive, renderManager,
					viewPort.getCamera(), true);
			renderManager.setForcedMaterial(null);
		}
	}

	public void preFrame(float tpf) {
	}

	public void cleanup() {
	}

	public Picture getDisplayPicture() {
		return dispPic;
	}

	public void reshape(ViewPort vp, int w, int h) {
		dispPic.setPosition(w / 20f, h / 20f);
		dispPic.setWidth(w / 5f);
		dispPic.setHeight(h / 5f);
	}

	  /**
     * Updates the shadow camera to properly contain the given
     * points (which contain the eye camera frustum corners) and the
     * shadow occluder objects.
     * 
     * @param occluders
     * @param shadowCam
     * @param points
     */
	// copied from com.jme3.shadow.ShadowUitl, and modified
    public void updateShadowCamera(GeometryList occluders,
            GeometryList receivers,
            Camera shadowCam,
            Vector3f[] points,
            GeometryList splitOccluders) {

        boolean ortho = shadowCam.isParallelProjection();

        shadowCam.setProjectionMatrix(null);

        if (ortho) {
            shadowCam.setFrustum(-1, 1, -1, 1, 1, -1);
        } else {
            //shadowCam.setFrustumPerspective(45, 1, 1, 150);
        	// changed by naka 
            shadowCam.setFrustumPerspective(fov, (float)shadowCam.getWidth()/shadowCam.getHeight(), near, far);
        }

        // create transform to rotate points to viewspace        
        Matrix4f viewProjMatrix = shadowCam.getViewProjectionMatrix();

        BoundingBox splitBB = ShadowUtil.computeBoundForPoints(points, viewProjMatrix);

        ArrayList<BoundingVolume> visRecvList = new ArrayList<BoundingVolume>();
        for (int i = 0; i < receivers.size(); i++) {
            // convert bounding box to light's viewproj space
            Geometry receiver = receivers.get(i);
            BoundingVolume bv = receiver.getWorldBound();
            BoundingVolume recvBox = bv.transform(viewProjMatrix, null);

            if (splitBB.intersects(recvBox)) {
                visRecvList.add(recvBox);
            }
        }

        ArrayList<BoundingVolume> visOccList = new ArrayList<BoundingVolume>();
        for (int i = 0; i < occluders.size(); i++) {
            // convert bounding box to light's viewproj space
            Geometry occluder = occluders.get(i);
            BoundingVolume bv = occluder.getWorldBound();
            BoundingVolume occBox = bv.transform(viewProjMatrix, null);

            boolean intersects = splitBB.intersects(occBox);
            if (!intersects && occBox instanceof BoundingBox) {
                BoundingBox occBB = (BoundingBox) occBox;
                //Kirill 01/10/2011
                // Extend the occluder further into the frustum
                // This fixes shadow dissapearing issues when
                // the caster itself is not in the view camera
                // but its shadow is in the camera
                //      The number is in world units
                occBB.setZExtent(occBB.getZExtent() + 50);
                occBB.setCenter(occBB.getCenter().addLocal(0, 0, 25));
                if (splitBB.intersects(occBB)) {
                    // To prevent extending the depth range too much
                    // We return the bound to its former shape
                    // Before adding it
                    occBB.setZExtent(occBB.getZExtent() - 50);
                    occBB.setCenter(occBB.getCenter().subtractLocal(0, 0, 25));
                    visOccList.add(occBox);
                    if (splitOccluders != null) {
                        splitOccluders.add(occluder);
                    }
                }
            } else if (intersects) {
                visOccList.add(occBox);
                if (splitOccluders != null) {
                    splitOccluders.add(occluder);
                }
            }
        }

        BoundingBox casterBB = ShadowUtil.computeUnionBound(visOccList);
        BoundingBox receiverBB = ShadowUtil.computeUnionBound(visRecvList);

        //Nehon 08/18/2010 this is to avoid shadow bleeding when the ground is set to only receive shadows
        if (visOccList.size() != visRecvList.size()) {
            casterBB.setXExtent(casterBB.getXExtent() + 2.0f);
            casterBB.setYExtent(casterBB.getYExtent() + 2.0f);
            casterBB.setZExtent(casterBB.getZExtent() + 2.0f);
        }

        TempVars vars = TempVars.get();

        Vector3f casterMin = casterBB.getMin(vars.vect1);
        Vector3f casterMax = casterBB.getMax(vars.vect2);

        Vector3f receiverMin = receiverBB.getMin(vars.vect3);
        Vector3f receiverMax = receiverBB.getMax(vars.vect4);

        Vector3f splitMin = splitBB.getMin(vars.vect5);
        Vector3f splitMax = splitBB.getMax(vars.vect6);

        splitMin.z = 0;

        if (!ortho) {
            //shadowCam.setFrustumPerspective(45, 1, 1, splitMax.z);
        	// changed by naka
        	shadowCam.setFrustumPerspective(fov, (float)shadowCam.getWidth()/shadowCam.getHeight(), near, splitMax.z);            
        }

        Matrix4f projMatrix = shadowCam.getProjectionMatrix();

        Vector3f cropMin = vars.vect7;
        Vector3f cropMax = vars.vect8;

        // IMPORTANT: Special handling for Z values
        cropMin.x = max(max(casterMin.x, receiverMin.x), splitMin.x);
        cropMax.x = min(min(casterMax.x, receiverMax.x), splitMax.x);

        cropMin.y = max(max(casterMin.y, receiverMin.y), splitMin.y);
        cropMax.y = min(min(casterMax.y, receiverMax.y), splitMax.y);

        cropMin.z = min(casterMin.z, splitMin.z);
        cropMax.z = min(receiverMax.z, splitMax.z);


        // Create the crop matrix.
        float scaleX, scaleY, scaleZ;
        float offsetX, offsetY, offsetZ;

        scaleX = (2.0f) / (cropMax.x - cropMin.x);
        scaleY = (2.0f) / (cropMax.y - cropMin.y);

        offsetX = -0.5f * (cropMax.x + cropMin.x) * scaleX;
        offsetY = -0.5f * (cropMax.y + cropMin.y) * scaleY;

        scaleZ = 1.0f / (cropMax.z - cropMin.z);
        offsetZ = -cropMin.z * scaleZ;




        Matrix4f cropMatrix = vars.tempMat4;
        cropMatrix.set(scaleX, 0f, 0f, offsetX,
                0f, scaleY, 0f, offsetY,
                0f, 0f, scaleZ, offsetZ,
                0f, 0f, 0f, 1f);


        Matrix4f result = new Matrix4f();
        result.set(cropMatrix);
        result.multLocal(projMatrix);
        vars.release();

        shadowCam.setProjectionMatrix(result);

    }

}