package jsmocap.skeleton;

import java.util.ArrayList;

import javax.vecmath.Matrix4d;
import javax.vecmath.Vector3d;

public class Bone 
{
	public enum Type
	{
		TX, TY, TZ, RX, RY, RZ
	}
	
	public int id_;
	public String name_;
	
	public Vector3d direction_;
	public float length_;
	
	public Vector3d axis_;
	public Type[] rot_order_;
	
	public Type[] dofs_;
	public float[] limits_;

	public ArrayList<Bone> children_;
	
	public Matrix4d M_;
	
	private Matrix4d C_;
	private Matrix4d Cinv_;
	private Matrix4d B_;
	
	private Matrix4d local_;
	public Matrix4d global_;
	
	public Bone()
	{
		children_=new ArrayList<Bone>();
	}
	
	public void updateGlobal(Skeleton root, Bone parent)
	{
		local_=new Matrix4d();
		global_=new Matrix4d();
		
		local_.mul(C_, M_);
		local_.mul(Cinv_);
		local_.mul(B_);
		/*local_.mul(B_, C_);
		local_.mul(local_, M_);
		local_.mul(local_, Cinv_);*/
		
		if(parent==null)
			global_.mul(root.global_, local_);
		else
			global_.mul(parent.global_, local_);
		
		for(int i=0; i<children_.size(); ++i)
			children_.get(i).updateGlobal(root, this);
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
			rot.rotZ(axis_.z*Math.PI/180.0);
			C_.mul(rot);
		//}
		//else if(rot_order_[0]==Type.RY)
		//{
			rot.rotY(axis_.y*Math.PI/180.0);
			C_.mul(rot);
		//}
		//else
		//{
			rot.rotX(axis_.x*Math.PI/180.0);
			C_.mul(rot);
		//}
		
		/*if(rot_order_[1]==Type.RX)
		{
			rot.rotX(axis_.x*Math.PI/180.0);
			C_.mul(rot);
		}
		else if(rot_order_[1]==Type.RY)
		{
			rot.rotY(axis_.y*Math.PI/180.0);
			C_.mul(rot);
		}
		else
		{
			rot.rotZ(axis_.z*Math.PI/180.0);
			C_.mul(rot);
		}
		
		if(rot_order_[2]==Type.RX)
		{
			rot.rotX(axis_.x*Math.PI/180.0);
			C_.mul(rot);
		}
		else if(rot_order_[2]==Type.RY)
		{
			rot.rotY(axis_.y*Math.PI/180.0);
			C_.mul(rot);
		}
		else
		{
			rot.rotZ(axis_.z*Math.PI/180.0);
			C_.mul(rot);
		}*/
		
		Cinv_=new Matrix4d();
		Cinv_.invert(C_);
		
		B_=new Matrix4d();
		B_.setIdentity();
		
		Vector3d offset=(Vector3d)direction_.clone();
		offset.scale(length_);
		B_.setTranslation(offset);
	}
}
