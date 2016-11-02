package jsmocap.training;

import javax.vecmath.Vector3d;
import javax.vecmath.Vector4d;

import jsmocap.skeleton.Bone;
import jsmocap.skeleton.Motion;
import jsmocap.skeleton.Skeleton;

public class F11 implements Feature 
{
	public static final double theta1 = 1.4;
	public static final double theta2 = 1.2;
	
	@Override
	public boolean[] computeFeature(Motion motion)
	{
		boolean vector[]=new boolean[motion.frames_.size()];
		
		double lh=motion.skeleton_.bones_.get("lhumerus").length_;
		
		Bone j1=motion.skeleton_.bones_.get("rwrist");
		
		motion.updateSkeleton(0);
		
		double prev=computeDistance(j1, motion.skeleton_);
		
		for(int i=1; i<motion.frames_.size(); ++i)
		{
			motion.updateSkeleton(i);
			
			double now=computeDistance(j1, motion.skeleton_);
			
			if((now-prev)*30.0/lh>theta1)
				vector[i]=true;
			else
				vector[i]=false;
			
			prev=now;
		}
		
		vector[0]=vector[1];
		
		return vector;
	}
	
	public double computeDistance(Bone j1, Skeleton j2)
	{
		Vector4d j1pos4=new Vector4d(0,0,0,1);
		j1.global_.transform(j1pos4);
		Vector3d j1pos=new Vector3d(j1pos4.x, j1pos4.y, j1pos4.z);
		
		Vector4d j2pos4=new Vector4d(0,0,0,1);
		j2.global_.transform(j2pos4);
		Vector3d j2pos=new Vector3d(j2pos4.x, j2pos4.y, j2pos4.z);
		
		Vector3d dist=new Vector3d();
		dist.sub(j1pos, j2pos);
		
		double val=dist.length();
		
		return val;
	}
}
