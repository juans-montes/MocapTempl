package jsmocap.skeleton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import javax.vecmath.Matrix4d;
import javax.vecmath.Vector3d;

import jsmocap.skeleton.Bone.Type;

public class Motion 
{
	public ArrayList< HashMap<String, ArrayList<Double> > > frames_;
	public Skeleton skeleton_;
	public String name_;
	
	public ArrayList<Annotation> annotations_;
	
	public Motion()
	{
		frames_=new ArrayList< HashMap<String, ArrayList<Double> > >();
		annotations_=new ArrayList<>();
	}
	
	public void updateSkeleton(int nframe)
	{
		HashMap<String, ArrayList<Double> > map=frames_.get(nframe);
		
		Set<String> set=map.keySet();
		for(String key : set)
		{
			if(key.equals("root"))
			{
				ArrayList<Double> list=map.get(key);
				Matrix4d m=new Matrix4d();
				m.setIdentity();
				Vector3d tr=new Vector3d();
				
				double rx=0, ry=0, rz=0;
				for(int i=0; i<skeleton_.order_.length; ++i)
				{	
					if(skeleton_.order_[i]==Type.RX)
						rx=list.get(i)*Math.PI/180.0;
					if(skeleton_.order_[i]==Type.RY)
						ry=list.get(i)*Math.PI/180.0;
					if(skeleton_.order_[i]==Type.RZ)
						rz=list.get(i)*Math.PI/180.0;
					if(skeleton_.order_[i]==Type.TX)
						tr.x=list.get(i);
					if(skeleton_.order_[i]==Type.TY)
						tr.y=list.get(i);
					if(skeleton_.order_[i]==Type.TZ)
						tr.z=list.get(i);
				}

				Matrix4d rot=new Matrix4d();
				
				rot.rotZ(rz);
				m.mul(rot);
				
				rot.rotY(ry);
				m.mul(rot);
				
				rot.rotX(rx);
				m.mul(rot);
				
				m.setTranslation(tr);
				skeleton_.M_=m;
			}
			else
				applyTransformation(map.get(key), skeleton_.bones_.get(key));
		}
		skeleton_.updateGlobal();
	}
	
	public void applyTransformation(ArrayList<Double> list, Bone bone)
	{
		Bone.Type[] dofs=bone.dofs_;
		
		
		Matrix4d m=new Matrix4d();
		m.setIdentity();
		
		double rx=0, ry=0, rz=0;
		for(int i=0; i<dofs.length; ++i)
		{
			if(dofs[i]==Type.RX)
				rx=list.get(i)*Math.PI/180.0;
			if(dofs[i]==Type.RY)
				ry=list.get(i)*Math.PI/180.0;
			if(dofs[i]==Type.RZ)
				rz=list.get(i)*Math.PI/180.0;
		}
		Matrix4d rot=new Matrix4d();
		
		rot.rotZ(rz);
		m.mul(rot);
		
		rot.rotY(ry);
		m.mul(rot);
		
		rot.rotX(rx);
		m.mul(rot);
		
		bone.M_=m;
	}
	
	public String toString()
	{
		return name_;
	}
}
