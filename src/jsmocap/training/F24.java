package jsmocap.training;

import javax.vecmath.Vector3d;
import javax.vecmath.Vector4d;

import jsmocap.skeleton.Bone;
import jsmocap.skeleton.Motion;

public class F24 implements Feature
{
	public static final double theta1 = 1.4;
	public static final double theta2 = 1.2;
	
	@Override
	public boolean[] computeFeature(Motion motion)
	{
		boolean vector[]=new boolean[motion.frames_.size()];
		
		double lh=motion.skeleton_.bones_.get("lhumerus").length_;
		
		Bone j1=motion.skeleton_.bones_.get("rfoot");
		Bone j2=motion.skeleton_.bones_.get("lfoot");
		Bone j3=motion.skeleton_.bones_.get("rhipjoint");
		Bone j4=motion.skeleton_.bones_.get("lhipjoint");
		
		motion.updateSkeleton(0);
		
		double prev=computeDistance(j1, j2, j3, j4);
		
		for(int i=1; i<motion.frames_.size(); ++i)
		{
			motion.updateSkeleton(i);
			
			double now=computeDistance(j1, j2, j3, j4);
			
			if((now-prev)*30.0/lh>theta1)
				vector[i]=true;
			else
				vector[i]=false;
			
			prev=now;
		}
		
		vector[0]=vector[1];
		
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
		
		Vector3d normal=new Vector3d();
		normal.sub(j4pos, j3pos);
		normal.normalize();
		
		Vector3d relative=new Vector3d();
		relative.sub(j2pos, j1pos);
		
		double val=normal.dot(relative);
		
		return val;
	}
}
