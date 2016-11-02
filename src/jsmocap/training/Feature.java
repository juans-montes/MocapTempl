package jsmocap.training;

import jsmocap.skeleton.Motion;

public interface Feature 
{
	public boolean[] computeFeature(Motion motion);
}
