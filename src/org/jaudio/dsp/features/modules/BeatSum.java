/*
 * @(#)BeatSum.java	1.0	April 5, 2005.
 *
 * McGill Univarsity
 */

package org.jaudio.dsp.features.modules;

import org.dynamicfactory.descriptors.Properties;
import org.jaudio.dsp.features.FeatureDefinition;
import org.jaudio.dsp.features.FeatureExtractor;

import java.util.ResourceBundle;


/**
 * A feature extractor that extracts the Beat Sum from a signal. This is a good 
 * measure of how important a role regular beats play in a piece of music.
 *
 * <pThis is calculated by finding the sum of all values in the beat histogram.
 *
 * <p>No extracted feature values are stored in objects of this class.
 *
 *<p> Daniel McEnnis	05-07-05	Added clone
 * @author Cory McKay
 */
public class BeatSum
	extends FeatureExtractor
{
	@Override
	public FeatureExtractor prototype() {
		return new BeatSum();
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
	public BeatSum()
	{
		ResourceBundle bundle = ResourceBundle.getBundle("Translations");
		String name = "Beat Sum";
		String description = bundle.getString("the.sum.of.all.entries.in.the.beat.histogram.this.is.a.good.measure.of.the.importance.of.regular.beats.in.a.signal");
		boolean is_sequential = true;
		int dimensions = 1;
		definition = new FeatureDefinition( name,
		                                    description,
		                                    is_sequential,
		                                    dimensions );
		definition.setDependency("Beat Histogram");
	}


	/* PUBLIC METHODS **********************************************************/

	
	/**
	 * Extracts this feature from the given samples at the given sampling
	 * rate and given the other feature values.
	 *
	 * <p>In the case of this feature the sampling_rate is ignored.
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
		double[] beat_histogram = other_feature_values[0];
		
		if (beat_histogram != null)
		{
			double sum = 0.0;
			for (int i = 0; i < beat_histogram.length; i++)
				sum += beat_histogram[i];

			double[] result = new double[1];
			result[0] = sum;
			return result;
		}
		else
			return null;
	}
	
}