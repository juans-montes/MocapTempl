package jsmocap.skeleton;

import java.util.ArrayList;

import jsmocap.training.FeatureMatrix;

public class Training 
{
	public ArrayList<Motion> motions_;
	
	public double[][] template_;
	
	public Training(ArrayList<Motion> motions)
	{
		motions_=motions;
	}
	
	public double[][] computeClass()
	{
		ArrayList<double[][]> matrices=computeMatrices();
		
		ArrayList<double[][]> pass1=new ArrayList<double[][]>();
		for(int i=0; i<matrices.size(); ++i)
			pass1.add(globalDtw(i, matrices));
		
		double[][] result1=mean(pass1);
		
		double difference=0;
		do
		{
			ArrayList<double[][]> pass2=new ArrayList<double[][]>();
			for(int i=0; i<pass1.size(); ++i)
				pass2.add(globalDtw(i, pass1));
			
			double[][] result2=mean(pass2);
			difference=difference(result1, result2);
			System.out.println(difference);
			
			pass1=pass2;
			result1=result2;
		}
		while(difference>1);
		
		return result1;
	}
	
	private double difference(double[][] result1, double[][] result2)
	{
		double diff=0;
		for(int x=0; x<result1.length; ++x)
		{
			for(int y=0; y<result1[0].length; ++y)
				diff+=Math.abs(result1[x][y]-result2[x][y]);
		}	
		return diff;
	}
	
	private double[][] mean(ArrayList<double[][]> matrices)
	{
		double[][] result=new double[matrices.get(0).length][matrices.get(0)[0].length];
		for(int i=0; i<matrices.size(); ++i)
		{
			for(int x=0; x<result.length; ++x)
			{
				for(int y=0; y<result[0].length; ++y)
					result[x][y]+=matrices.get(i)[x][y]/matrices.size();
			}		
		}
		return result;
	}
	
	private void mean(ArrayList<double[]> weights, ArrayList<double[][]> matrices, double[] wavg, double[][] mavg)
	{
		for(int i=0; i<matrices.size(); ++i)
		{
			for(int y=0; y<mavg[0].length; ++y)
				wavg[y]+=weights.get(i)[y];
			
			for(int x=0; x<mavg.length; ++x)
			{
				for(int y=0; y<mavg[0].length; ++y)
					mavg[x][y]+=matrices.get(i)[x][y]*weights.get(i)[y];
			}		
		}
		
		for(int x=0; x<mavg.length; ++x)
		{
			for(int y=0; y<mavg[0].length; ++y)
				mavg[x][y]/=wavg[y];
		}	
		
		for(int y=0; y<mavg[0].length; ++y)
			wavg[y]/=weights.size();
	}
	
	private double[][] unwarp(double[] wavg, double[][] mavg)
	{
		double avgs=0;
		for(int i=0; i<wavg.length; ++i)
			avgs+=wavg[i];
		
		if(avgs-(int)avgs>0.1)
			avgs=Math.ceil(avgs);
		else
			avgs=Math.round(avgs);
		
		double[][] unwarped=new double[mavg.length][(int)avgs];
		
		double acc=0;
		double offset=0;
		int x=0;
		for(int i=0; i<mavg[0].length; ++i)
		{
			acc=wavg[i];
			
			if(acc+offset<1)
			{
				for(int j=0; j<mavg.length; ++j)
					unwarped[j][x]+=mavg[j][i]*acc;
				offset+=acc;
			}
			else if(acc+offset==1)
			{
				for(int j=0; j<mavg.length; ++j)
					unwarped[j][x]+=mavg[j][i]*acc;
				
				offset=0;
				++x;
			}
			else
			{
				for(int j=0; j<mavg.length; ++j)
					unwarped[j][x]+=mavg[j][i]*(1.0f-offset);
				++x;
				acc-=(1.0-offset);
				offset=0;
				
				while(acc>1)
				{
					for(int j=0; j<mavg.length; ++j)
						unwarped[j][x]+=mavg[j][i];
					++x;
					--acc;
				}
				
				if(x==unwarped[0].length)
					break;
				
				for(int j=0; j<mavg.length; ++j)
					unwarped[j][x]+=mavg[j][i]*acc;
				
				offset=acc;
			}
		}
		
		return unwarped;
	}
	
	private ArrayList<double[][]> computeMatrices()
	{
		ArrayList<double[][]> matrices=new ArrayList<double[][]>();
		for(int i=0; i<motions_.size(); ++i)
		{
			FeatureMatrix matrix=new FeatureMatrix(motions_.get(i));
			matrix.compute();
			matrices.add(matrix.matrix_);
		}
		return matrices;
	}
	
	private double[][] globalDtw(int r, ArrayList<double[][]> matrices)
	{
		ArrayList<double[]> weights_g=new ArrayList<double[]>();
		
		ArrayList<double[]> c_weights=new ArrayList<double[]>();
		ArrayList<double[][]> c_matrices=new ArrayList<double[][]>();
		
		for(int i=0; i<matrices.size(); ++i)
		{
			double[] weights=new double[matrices.get(i)[0].length];
			for(int j=0; j<weights.length; ++j)
				weights[j]=1.0f;
			weights_g.add(weights);
		}
		
		for(int i=0; i<matrices.size(); ++i)
		{
			if(r!=i)
			{
				double[] i_weights=new double[matrices.get(r)[0].length];
				double[][] i_matrix=new double[matrices.get(r).length][matrices.get(r)[0].length];
				dtw(matrices.get(r), matrices.get(i), weights_g.get(i), i_weights, i_matrix);
				c_weights.add(i_weights);
				c_matrices.add(i_matrix);
			}
			else
			{
				c_weights.add(weights_g.get(r));
				c_matrices.add(matrices.get(r));
			}
		}
		
		double[][] mavg=new double[matrices.get(r).length][matrices.get(r)[0].length];
		double[] wavg=new double[matrices.get(r)[0].length];
		
		mean(c_weights, c_matrices, wavg, mavg);

		return unwarp(wavg, mavg);
	}
	
	private void dtw(double[][] s, double[][] t, double[] tw, double[] wresult, double[][] result)
	{
		double[][] dtwm=new double[s[0].length+1][t[0].length+1];
		for(int i=0; i<s[0].length; ++i)
			dtwm[i+1][0]=Double.MAX_VALUE;
		for(int i=0; i<t[0].length; ++i)
			dtwm[0][i+1]=Double.MAX_VALUE;
		dtwm[0][0]=0;
		
		for(int i=1; i<=s[0].length; ++i)
		{
			for(int j=1; j<=t[0].length; ++j)
			{
				double cost=distance(s, t, i-1, j-1);
				dtwm[i][j]=cost+Math.min(Math.min(dtwm[i-1][j], dtwm[i][j-1]), dtwm[i-1][j-1]);
			}
		}
		
		adjustWeights(dtwm, tw, wresult, s, t, result);
		
		/*double tt=0;
		for(int i=0; i<wresult.length; ++i)
			tt+=wresult[i];
		System.out.println(tt+" "+tw.length);*/
	}
	
	private void adjustWeights(double[][] dtwm, double[] tw, double[] new_tw, double[][] ss, double[][] tt, double[][] warped)
	{
		int x=dtwm.length-1, y=dtwm[0].length-1, xd_s=0, yd_s=0;
		
		while(!(x==1 && y==1))
		{
			double tl=(x-1>0 && y-1>0) ? dtwm[x-1][y-1] : Double.MAX_VALUE;
			double t=(x-1>0) ? dtwm[x-1][y] : Double.MAX_VALUE;
			double l=(y-1>0) ? dtwm[x][y-1] : Double.MAX_VALUE;
			
			if(tl<=t && tl<=l)
			{
				if(yd_s>0)
					contract(warped, tw, new_tw, x, y, tt, yd_s);
				else if(xd_s>0)
					duplicate(warped, tw, new_tw, x, y, tt, xd_s);
				else
					keep(warped, tw, new_tw, x, y, tt);
				
				xd_s=0;
				yd_s=0;
				--x;--y;
			}
			else if(t<l)
			{
				if(yd_s>0)
					contract(warped, tw, new_tw, x, y, tt, yd_s);
				else if(xd_s==0)
					keep(warped, tw, new_tw, x, y, tt);
				
				yd_s=0;
				++xd_s;
				--x;
			}
			else
			{
				if(xd_s>0)
					duplicate(warped, tw, new_tw, x, y, tt, xd_s);
				else if(yd_s==0)
					keep(warped, tw, new_tw, x, y, tt);
				
				xd_s=0;
				++yd_s;
				--y;
			}
		}
		if(yd_s>0)
			contract(warped, tw, new_tw, x, y, tt, yd_s);
		else if(xd_s>0)
			duplicate(warped, tw, new_tw, x, y, tt, xd_s);
		else
			keep(warped, tw, new_tw, x, y, tt);
	}
	
	private void keep(double[][] warped, double[] tw, double[] new_tw, int x, int y, double[][] tt)
	{
		for(int i=0; i<warped.length; ++i)
			warped[i][x-1]=tt[i][y-1];
		new_tw[x-1]=tw[y-1];
	}
	
	private void duplicate(double[][] warped, double[] tw, double[] new_tw, int x, int y, double[][] tt, int xd_s)
	{
		for(int j=0; j<xd_s+1; ++j)
		{
			new_tw[x+j-1]=tw[y-1]/(xd_s+1);
			for(int i=0; i<warped.length; ++i)
			{
				warped[i][x+j-1]=tt[i][y-1];
			}
		}
	}
	
	private void contract(double[][] warped, double[] tw, double[] new_tw, int x, int y, double[][] tt, int yd_s)
	{
		for(int i=0; i<warped.length; ++i)
		{
			double average=0;
			double tweight=0;
			for(int j=0; j<yd_s+1; ++j)
			{
				tweight+=tw[y-1+j];
				average+=tw[y-1+j]*tt[i][y-1+j];
			}
			average/=(yd_s+1);
			warped[i][x-1]=average;
			new_tw[x-1]=tweight;
		}
	}
	
	private double distance(double[][] s, double[][] t, int i, int j)
	{
		double distance=0;
		for(int x=0; x<s.length; ++x)
			distance+=Math.abs(s[x][i]-t[x][j]);
		distance/=s.length;
		return distance;
	}
}
