package jsmocap.skeleton;

import java.util.ArrayList;

import java.util.HashMap;

import javax.vecmath.Matrix4d;
import javax.vecmath.Vector3d;

public class Skeleton 
{
	public Bone.Type[] order_;
	public Bone.Type[] rot_order_;
	public Vector3d position_;
	public Vector3d orientation_;
	
	public ArrayList<Bone> children_;
	public HashMap<String, Bone> bones_;
	
	public Matrix4d M_;
	
	private Matrix4d C_;
	private Matrix4d Cinv_;
	private Matrix4d B_;
	
	private Matrix4d local_;
	public Matrix4d global_;
	
	public Skeleton()
	{
		children_=new ArrayList<Bone>();
	}
	
	public void updateGlobal()
	{
		local_=new Matrix4d();
		global_=new Matrix4d();
		
		local_.mul(C_, M_);
		local_.mul(Cinv_);
		local_.mul(B_);
		
		global_=(Matrix4d)local_.clone();
		
		for(int i=0; i<children_.size(); ++i)
			children_.get(i).updateGlobal(this, null);
	}
	
	public void precompute()
	{
		M_=new Matrix4d();
		M_.setIdentity();
		
		C_=new Matrix4d();
		C_.setIdentity();
		
		Matrix4d rot=new Matrix4d();
		
		//if(rot_order_[0]==Type.RX)
		//{
			rot.rotZ(orientation_.z*Math.PI/180.0);
			C_.mul(rot);
		//}
		//else if(rot_order_[0]==Type.RY)
		//{
			rot.rotY(orientation_.y*Math.PI/180.0);
			C_.mul(rot);
		//}
		//else
		//{
			rot.rotX(orientation_.x*Math.PI/180.0);
			C_.mul(rot);
		//}
		
		/*if(rot_order_[1]==Type.RX)
		{
			rot.rotX(orientation_.x*Math.PI/180.0);
			C_.mul(rot);
		}
		else if(rot_order_[1]==Type.RY)
		{
			rot.rotX(orientation_.y*Math.PI/180.0);
			C_.mul(rot);
		}
		else
		{
			rot.rotX(orientation_.z*Math.PI/180.0);
			C_.mul(rot);
		}
		
		if(rot_order_[2]==Type.RX)
		{
			rot.rotX(orientation_.x*Math.PI/180.0);
			C_.mul(rot);
		}
		else if(rot_order_[2]==Type.RY)
		{
			rot.rotX(orientation_.y*Math.PI/180.0);
			C_.mul(rot);
		}
		else
		{
			rot.rotX(orientation_.z*Math.PI/180.0);
			C_.mul(rot);
		}*/
		
		Cinv_=new Matrix4d();
		Cinv_.invert(C_);
		
		B_=new Matrix4d();
		B_.setIdentity();
		
		B_.setTranslation(position_);
	}
}
