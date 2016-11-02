package jsmocap.skeleton;

import java.util.ArrayList;

import javax.vecmath.Point2i;

import jsmocap.training.FeatureMatrix;

public class Classifier 
{
	public String name_;
	public double[][] template_;
	
	public Classifier(double[][] template)
	{
		template_=template;
	}
	
	public String toString()
	{
		return name_;
	}
	
	public ArrayList<Point2i> classify(Motion motion, double t, double th)
	{
		double q[][]=quantize(t);
		FeatureMatrix fm=new FeatureMatrix(motion);
		fm.compute();
		
		return dwt(q, fm.matrix_, th);
	}
	
	private ArrayList<Point2i> dwt(double[][] q, double[][] m, double th)
	{
		double dwtm[][]=new double[q[0].length+1][m[0].length+1];
		
		for(int i=1; i<dwtm[0].length; ++i)
			dwtm[1][i]=distance(q, m, 0, i-1);

		double acc=0;
		for(int i=1; i<q[0].length+1; ++i)
		{
			acc+=distance(q, m, i-1, 0);
			dwtm[i][0]=Double.MAX_VALUE;
			dwtm[i][1]=acc;
		}
		
		for(int i=2; i<q[0].length+1; ++i)
		{
			for(int j=2; j<m[0].length+1; ++j)
			{
				double cost=distance(q, m, i-1, j-1);
				dwtm[i][j]=cost+Math.min(Math.min(dwtm[i-1][j-1], dwtm[i-2][j-1]), dwtm[i-1][j-2]);
			}
		}
		
		//
		/*try{
		PrintWriter pw=new PrintWriter(new File("./data/"+name_+".txt"));
		
		for(int i=dwtm.length-1; i<dwtm.length; ++i)
		{
			pw.print(dwtm[i][0]/dwtm.length);
			for(int j=1; j<dwtm[0].length; ++j)
				pw.print(" "+dwtm[i][j]/dwtm.length);
			pw.println();
		}
		pw.close();
		//for(int i=0; i<dwtm[0].length; ++i)
			//System.out.println(dwtm[dwtm.length-1][i]);
		}catch(Exception e){}*/
		//
		ArrayList<Integer> lm=findLocalMinimums(dwtm, th);
		
		return getIntervals(dwtm, lm);
	}
	
	private ArrayList<Integer> findLocalMinimums(double[][] dwtm, double th)
	{
		ArrayList<Integer> r=new ArrayList<Integer>();
		boolean cancelled[]=new boolean[dwtm[0].length];
		while(true)
		{
			double min=Double.MAX_VALUE;
			int idx=-1;
			for(int i=0; i<dwtm[0].length; ++i)
			{
				if(!cancelled[i] && dwtm[dwtm.length-1][i]<min)
				{
					idx=i;
					min=dwtm[dwtm.length-1][i];
				}
			}
			if(min/dwtm.length<=th)
			{
				r.add(idx);
				for(int i=idx;i>=0 && i>=idx-dwtm.length*3.0/4.0;--i)
					cancelled[i]=true;
				for(int i=idx;i<dwtm[0].length && i<=idx+dwtm.length*3.0/4.0;++i)
					cancelled[i]=true;
			}
			else
				break;
		}
		
		return r;
	}
	
	private ArrayList<Point2i> getIntervals(double[][] dwtm, ArrayList<Integer> lm)
	{
		ArrayList<Point2i> r=new ArrayList<Point2i>();
		
		for(int i=0; i<lm.size(); ++i)
		{
			int x=dwtm.length-1;
			int y=lm.get(i);
			
			while(x>1)
			{
				double pos11=dwtm[x-1][y-1];
				double pos21=dwtm[x-2][y-1];
				double pos12=dwtm[x-1][y-2];
				
				if(pos11<=pos21 && pos11<=pos12)
				{
					--x;
					--y;
				}
				else if(pos21<=pos12)
				{
					x-=2;
					--y;
				}
				else
				{
					--x;
					y-=2;
				}
			}
			r.add(new Point2i(y, lm.get(i)));
		}
		
		return r;
	}
	
	private double distance(double[][] q, double[][] m, int i, int j)
	{
		double distance=0;
		int ik=0;
		for(int x=0; x<q.length; ++x)
		{
			if(q[x][i]!=0.5)
			{
				distance+=Math.abs(q[x][i]-m[x][j]);
				++ik;
			}
		}
		distance/=ik;
		return distance;
	}
	
	private double[][] quantize(double t)
	{
		double[][] r=new double[template_.length][template_[0].length];
		for(int i=0; i<template_.length; ++i)
		{
			for(int j=0; j<template_[0].length; ++j)
			{
				if(template_[i][j]<t)
					r[i][j]=0.0;
				else if(template_[i][j]>1-t)
					r[i][j]=1.0;
				else
					r[i][j]=0.5;
			}
		}
		return r;
	}
}
