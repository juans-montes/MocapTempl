package jsmocap.training;

import javax.vecmath.Vector3d;
import javax.vecmath.Vector4d;

import jsmocap.skeleton.Bone;
import jsmocap.skeleton.Motion;

public class F7 implements Feature
{
	public static final double theta1_1 = 0.0;
	public static final double theta1_2 = 110.0;
	public static final double theta2_1 = 0.0;
	public static final double theta2_2 = 120.0;
	
	@Override
	public boolean[] computeFeature(Motion motion)
	{
		boolean vector[]=new boolean[motion.frames_.size()];
		
		Bone j1=motion.skeleton_.bones_.get("rhumerus");
		Bone j2=motion.skeleton_.bones_.get("rclavicle");
		Bone j3=motion.skeleton_.bones_.get("rhumerus");
		Bone j4=motion.skeleton_.bones_.get("rradius");

		for(int i=0; i<motion.frames_.size(); ++i)
		{
			motion.updateSkeleton(i);
			
			double now=computeDistance(j1, j2, j3, j4);
			
			if(now*180.0/Math.PI>theta1_1 && now*180.0/Math.PI<theta1_2)
				vector[i]=true;
			else
				vector[i]=false;
		}
		
		return vector;
	}
	
	public double computeDistance(Bone j1, Bone j2, Bone j3, Bone j4)
	{
		Vector4d j1pos4=new Vector4d(0,0,0,1);
		j1.global_.transform(j1pos4);
		Vector3d j1pos=new Vector3d(j1pos4.x, j1pos4.y, j1pos4.z);
		
		Vector4d j2pos4=new Vector4d(0,0,0,1);
		j2.global_.transform(j2pos4);
		Vector3d j2pos=new Vector3d(j2pos4.x, j2pos4.y, j2pos4.z);
		
		Vector4d j3pos4=new Vector4d(0,0,0,1);
		j3.global_.transform(j3pos4);
		Vector3d j3pos=new Vector3d(j3pos4.x, j3pos4.y, j3pos4.z);
		
		Vector4d j4pos4=new Vector4d(0,0,0,1);
		j4.global_.transform(j4pos4);
		Vector3d j4pos=new Vector3d(j4pos4.x, j4pos4.y, j4pos4.z);
		
		Vector3d axis1=new Vector3d();
		axis1.sub(j2pos, j1pos);
		axis1.normalize();
		
		Vector3d axis2=new Vector3d();
		axis2.sub(j4pos, j3pos);
		axis2.normalize();
		
		double val=axis1.angle(axis2);
		
		return val;
	}
}
