package jsmocap.training;

import javax.vecmath.Vector3d;
import javax.vecmath.Vector4d;

import jsmocap.skeleton.Bone;
import jsmocap.skeleton.Motion;

public class F13 implements Feature
{
	public static final double theta1 = 2.5;
	public static final double theta2 = 2;
	
	@Override
	public boolean[] computeFeature(Motion motion)
	{
		boolean vector[]=new boolean[motion.frames_.size()];
		
		double lh=motion.skeleton_.bones_.get("lhumerus").length_;
		
		Bone j1=motion.skeleton_.bones_.get("rwrist");
		
		motion.updateSkeleton(0);
		
		Vector3d prev=computeDistance(j1);
		
		for(int i=1; i<motion.frames_.size(); ++i)
		{
			motion.updateSkeleton(i);
			
			Vector3d now=computeDistance(j1);
			
			Vector3d speed=new Vector3d();
			speed.sub(now, prev);
			
			if(speed.length()*30.0/lh>theta1)
				vector[i]=true;
			else
				vector[i]=false;
			
			prev=now;
		}
		
		vector[0]=vector[1];
		
		return vector;
	}
	
	public Vector3d computeDistance(Bone j1)
	{
		Vector4d j1pos4=new Vector4d(0,0,0,1);
		j1.global_.transform(j1pos4);
		Vector3d j1pos=new Vector3d(j1pos4.x, j1pos4.y, j1pos4.z);
		
		return j1pos;
	}
}
