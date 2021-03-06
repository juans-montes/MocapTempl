package jsmocap.training;

import javax.vecmath.Vector3d;
import javax.vecmath.Vector4d;

import jsmocap.skeleton.Bone;
import jsmocap.skeleton.Motion;

public class F35 implements Feature
{
	@Override
	public boolean[] computeFeature(Motion motion)
	{
		boolean vector[]=new boolean[motion.frames_.size()];
		
		Bone j1=motion.skeleton_.bones_.get("rhipjoint");
		Bone j2=motion.skeleton_.bones_.get("lhipjoint");
		Bone j3=motion.skeleton_.bones_.get("thorax");
		Bone j4=motion.skeleton_.bones_.get("rclavicle");
		Bone j5=motion.skeleton_.bones_.get("lclavicle");
		
		for(int i=0; i<motion.frames_.size(); ++i)
		{
			motion.updateSkeleton(i);
			
			boolean now=computeDistance(j1, j2, j3, j4, j5);
			
			if(now)
				vector[i]=true;
			else
				vector[i]=false;
		}
		
		return vector;
	}
	
	public boolean computeDistance(Bone j1, Bone j2, Bone j3, Bone j4, Bone j5)
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
		
		Vector4d j5pos4=new Vector4d(0,0,0,1);
		j5.global_.transform(j5pos4);
		Vector3d j5pos=new Vector3d(j5pos4.x, j5pos4.y, j5pos4.z);
		
		Vector3d axis1=new Vector3d();
		axis1.sub(j2pos, j1pos);
		axis1.normalize();
		
		Vector3d axis2=new Vector3d();
		axis2.sub(j3pos, j1pos);
		axis2.normalize();
		
		Vector3d normal=new Vector3d();
		normal.cross(axis1, axis2);
		normal.normalize();
		
		double dist1=0.0;
		dist1=normal.dot(j4pos);
		
		double dist2=0.0;
		dist2=normal.dot(j5pos);
		
		double toj4=Math.abs(dist1);
		double toj5=Math.abs(dist2);
		
		boolean val=(toj4 < toj5);
		
		return val;
	}
}
