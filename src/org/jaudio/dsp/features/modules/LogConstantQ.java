//
//  Log ConstantQ.java
//  jAudio
//
//  Created by Daniel McEnnis on August 18, 2010.
//  Published under the LGPL license.  See most recent LGPL license on www.fsf.org
//  a copy of this license.
//

package org.jaudio.dsp.features.modules;

import org.dynamicfactory.descriptors.Properties;
import org.jaudio.dsp.features.FeatureDefinition;
import org.jaudio.dsp.features.FeatureExtractor;

import java.util.ResourceBundle;

/**
 * Log Constant Q
 *
 * Performs the log linear transform of the bins of the constant q transform to produce a representation whose linear
 * content better represents how the human ear hears differences in amplitude.
 *
 * @author Daniel McEnnis
 */
public class LogConstantQ extends FeatureExtractor
{

	@Override
	public FeatureExtractor prototype() {
		return new LogConstantQ();
	}

	@Override
	public FeatureExtractor prototype(Properties props) {
		return prototype();
	}


	
	/* CONSTRUCTOR **************************************************************/
	
	
	/**
	 * Basic constructor that sets the definition and dependencies (and their
	 * offsets) of this feature.
	 */
	public LogConstantQ()
	{
		ResourceBundle bundle = ResourceBundle.getBundle("Translations");
		String name = "Log of ConstantQ";
		String description = bundle.getString("logarithm.of.each.bin.of.exponentially.spaced.frequency.bins");
		boolean is_sequential = true;
		int dimensions = 0;
		definition = new FeatureDefinition( name,
		                                    description,
		                                    is_sequential,
		                                    dimensions);
		definition.setDependency("ConstantQ");
	}


	/* PUBLIC METHODS **********************************************************/

	
	/**
	 * Extracts this feature from the given samples at the given sampling
	 * rate and given the other feature values.
	 *
	 * <p>In the case of this feature, the sampling_rate and 
	 * other_feature_values parameters are ignored.
	 *
	 * @param samples				The samples to extract the feature from.
	 * @param sampling_rate			The sampling rate that the samples are
	 *								encoded with.
	 * @param other_feature_values	The values of other features that are
	 *								needed to calculate this value. The
	 *								order and offsets of these features
	 *								must be the same as those returned by
	 *								this class's getDependencies and
	 *								getDependencyOffsets methods respectively.
	 *								The first indice indicates the feature/window
	 *								and the second indicates the value.
	 * @return						The extracted feature value(s).
	 * @throws Exception			Throws an informative exception if
	 *								the feature cannot be calculated.
	 */
	public double[] extractFeature( double[] samples,
	                                double sampling_rate,
	                                double[][] other_feature_values )
		throws Exception
	{
		double[] ret = new double[other_feature_values[0].length];
		for(int i=0;i<ret.length;++i)
		{
			if(other_feature_values[0][i] <= 0.0){
				ret[i] = -50.0;
			}else{
				ret[i] = Math.log(other_feature_values[0][i]);
				if(ret[i] < -50.0){
					ret[i]=-50.0;
				}
			}
		}
		return ret;
	}
	
}
