package jsmocap.training;

import java.util.ArrayList;

import jsmocap.skeleton.Motion;

public class FeatureMatrix 
{
	public double matrix_[][];
	private ArrayList<Feature> features_;
	private Motion motion_;
	
	public FeatureMatrix(Motion motion)
	{
		motion_=motion;
		features_=new ArrayList<Feature>();
		
		features_.add(new F1());
		features_.add(new F2());
		features_.add(new F3());
		features_.add(new F4());
		features_.add(new F5());
		features_.add(new F6());
		features_.add(new F7());
		features_.add(new F8());
		features_.add(new F9());
		features_.add(new F10());
		features_.add(new F11());
		features_.add(new F12());
		features_.add(new F13());
		features_.add(new F14());
		features_.add(new F15());
		features_.add(new F16());
		features_.add(new F17());
		features_.add(new F18());
		features_.add(new F19());
		features_.add(new F20());
		features_.add(new F21());
		features_.add(new F22());
		features_.add(new F23());
		features_.add(new F24());
		features_.add(new F25());
		features_.add(new F26());
		features_.add(new F27());
		features_.add(new F28());
		features_.add(new F29());
		features_.add(new F30());
		features_.add(new F31());
		features_.add(new F32());
		features_.add(new F33());
		features_.add(new F34());
		features_.add(new F35());
		features_.add(new F36());
		features_.add(new F39());
		
		matrix_=new double[features_.size()][motion_.frames_.size()];
	}
	
	public void compute()
	{
		for(int i=0; i<features_.size(); ++i)
		{
			boolean vector[]=features_.get(i).computeFeature(motion_);
			for(int j=0; j<motion_.frames_.size(); ++j)
				matrix_[i][j]=vector[j] ? 1.0 : 0.0;
		}
	}
}
