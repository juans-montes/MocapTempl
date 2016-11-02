package jsmocap.training;

import java.util.HashMap;
import java.util.Set;

import javax.vecmath.Vector3d;
import javax.vecmath.Vector4d;

import jsmocap.skeleton.Bone;
import jsmocap.skeleton.Motion;
import jsmocap.skeleton.Skeleton;

public class F34 implements Feature
{
	public static final double theta1 = -1.2;
	public static final double theta2 = -1.4;
	
	@Override
	public boolean[] computeFeature(Motion motion)
	{
		boolean vector[]=new boolean[motion.frames_.size()];
		
		double lh=motion.skeleton_.bones_.get("lhumerus").length_;
		
		Bone j1=motion.skeleton_.bones_.get("lwrist");
		
		for(int i=0; i<motion.frames_.size(); ++i)
		{
			motion.updateSkeleton(i);
			
			double now=computeDistance(j1, motion.skeleton_);
			
			if(now/lh>theta1)
				vector[i]=true;
			else
				vector[i]=false;
		}
		
		return vector;
	}
	
	private double computeMinimum(Skeleton skeleton)
	{
		Vector4d min=new Vector4d(0,0,0,1);
		skeleton.global_.transform(min);
		
		HashMap<String, Bone > map=skeleton.bones_;
		
		Set<String> set=map.keySet();
		
		for(String s : set)
		{
			Bone b=map.get(s);
			
			Vector4d t=new Vector4d(0,0,0,1);
			b.global_.transform(t);
			
			if(t.y<min.y)
				min=t;
		}
		
		return min.y;
	}
	
	public double computeDistance(Bone j1, Skeleton skeleton)
	{
		Vector4d j1pos4=new Vector4d(0,0,0,1);
		j1.global_.transform(j1pos4);
		Vector3d j1pos=new Vector3d(j1pos4.x, j1pos4.y, j1pos4.z);
		
		double d=computeMinimum(skeleton);
		
		double val=d-j1pos.y;
		
		return val;
	}
}
