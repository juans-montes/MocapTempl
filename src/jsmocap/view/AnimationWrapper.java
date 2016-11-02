package jsmocap.view;

import javax.vecmath.AxisAngle4d;
import javax.vecmath.Matrix4d;
import javax.vecmath.Vector3d;
import javax.vecmath.Vector4d;

import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Affine;
import javafx.scene.transform.MatrixType;
import jsmocap.skeleton.Bone;
import jsmocap.skeleton.Skeleton;

public class AnimationWrapper 
{
	public Bone bone_;
	public Sphere sphere_;
	public Cylinder cylinder_;
	public Bone parent_;
	public Skeleton skeleton_;
	
	public void update()
	{
		if(bone_!=null)
		{
			Affine aff=new Affine(MocapScene.matToArray(bone_.global_), MatrixType.MT_3D_4x4, 0);
	        sphere_.getTransforms().clear();
			sphere_.getTransforms().add(aff);
			
			Vector4d source=new Vector4d(0,0,0,1);
	        if(parent_!=null)
	        	parent_.global_.transform(source);
	        else
	        	skeleton_.global_.transform(source);
	        
	        Vector4d target=new Vector4d(0,0,0,1);
	        bone_.global_.transform(target);
	        
	        Vector4d cyl_trans=new Vector4d();
	        cyl_trans.add(source, target);
	        cyl_trans.scale(0.5);
	        
	        Matrix4d cyl_matrix=new Matrix4d();
	        cyl_matrix.setIdentity();
	        
	        Vector4d dir=new Vector4d();
	        dir.sub(target, source);
	        dir.normalize();
	        
	        Vector3d axis=new Vector3d(dir.x, dir.y, dir.z);
	        double angle=-Math.acos(axis.dot(new Vector3d(0,1,0)));
	        
	        axis.cross(new Vector3d(dir.x, dir.y, dir.z), new Vector3d(0,1,0));
	        axis.normalize();
	        
	        
	        AxisAngle4d aa=new AxisAngle4d(axis.x, axis.y, axis.z, angle);
	        
	        cyl_matrix.setRotation(aa);
	        cyl_matrix.setTranslation(new Vector3d(cyl_trans.x, cyl_trans.y, cyl_trans.z));
	        
	        
	        cylinder_.getTransforms().clear();
	        cylinder_.getTransforms().add(new Affine(MocapScene.matToArray(cyl_matrix), MatrixType.MT_3D_4x4, 0));
		}
		else
		{
			Affine aff=new Affine(MocapScene.matToArray(skeleton_.global_), MatrixType.MT_3D_4x4, 0);
	        sphere_.getTransforms().clear();
			sphere_.getTransforms().add(aff);
		}
	}
}
