package jsmocap.training;

import javax.vecmath.Vector3d;
import javax.vecmath.Vector4d;

import jsmocap.skeleton.Bone;
import jsmocap.skeleton.Motion;
import jsmocap.skeleton.Skeleton;

public class F16 implements Feature
{
	public static final double theta1 = 0.38;
	public static final double theta2 = 0.0;
	
	@Override
	public boolean[] computeFeature(Motion motion)
	{
		boolean vector[]=new boolean[motion.frames_.size()];
		
		double lh=motion.skeleton_.bones_.get("lhumerus").length_;
		
		Bone j2=motion.skeleton_.bones_.get("rhipjoint");
		Bone j3=motion.skeleton_.bones_.get("rtoes");
		Bone j4=motion.skeleton_.bones_.get("lfoot");
		
		for(int i=0; i<motion.frames_.size(); ++i)
		{
			motion.updateSkeleton(i);
			
			double now=computeDistance(motion.skeleton_, j2, j3, j4);
			
			if(now/lh>theta1)
				vector[i]=true;
			else
				vector[i]=false;
		}
		
		return vector;
	}
	
	public double computeDistance(Skeleton j1, Bone j2, Bone j3, Bone j4)
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
		
		Vector3d axis2=new Vector3d();
		axis2.sub(j2pos, j3pos);
		
		Vector3d plane_normal=new Vector3d();
		plane_normal.cross(axis2, axis1);
		plane_normal.normalize();
		
		Vector3d dist_v=new Vector3d();
		dist_v.sub(j4pos, j1pos);
		
		double val=plane_normal.dot(dist_v);
		
		return val;
	}
}
