/*
 * @(#)StrongestFrequencyViaZeroCrossings.java	1.0	April 7, 2005.
 *
 * McGill Univarsity
 */

package org.jaudio.dsp.features.modules;

import org.dynamicfactory.descriptors.Properties;
import org.jaudio.dsp.features.FeatureDefinition;
import org.jaudio.dsp.features.FeatureExtractor;

import java.util.ResourceBundle;


/**
 * A feature extractor that finds the strongest frequency in Hz in a signal
 * by looking at the zero crossings.
 *
 * <p>This is found by mapping the fraction in the zero-crossings to a
 * frequency in Hz.
 *
 * <p>No extracted feature values are stored in objects of this class.
 *
 * @author Cory McKay
 */
public class StrongestFrequencyViaZeroCrossings
	extends FeatureExtractor
{

	@Override
	public FeatureExtractor prototype() {
		return new StrongestFrequencyViaZeroCrossings();
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
	public StrongestFrequencyViaZeroCrossings()
	{
		ResourceBundle bundle = ResourceBundle.getBundle("Translations");
		String name = "Strongest Frequency Via Zero Crossings";
		String description = bundle.getString("the.strongest.frequency.component.of.a.signal.in.hz.found.via.the.number.of.zero.crossings");
		boolean is_sequential = true;
		int dimensions = 1;
		definition = new FeatureDefinition( name,
		                                    description,
		                                    is_sequential,
		                                    dimensions );
		definition.setDependency("Zero Crossings");
	}


	/* PUBLIC METHODS **********************************************************/

	
	/**
	 * Extracts this feature from the given samples at the given sampling
	 * rate and given the other feature values.
	 *
	 * <p>In the case of this feature, the sampling_rate parameter is ignored.
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
		double zero_crossings = other_feature_values[0][0];
		double[] result = new double[1];
		result[0] = (zero_crossings / 2.0) * (sampling_rate / (double) samples.length);
		return result;
	}
}