/*
 * @(#)StrongestFrequencyViaFFTMax.java	1.0	April 7, 2005.
 *
 * McGill Univarsity
 */

package org.jaudio.dsp.features.modules;

import org.dynamicfactory.descriptors.Properties;
import org.jaudio.dsp.features.FeatureDefinition;
import org.jaudio.dsp.features.FeatureExtractor;

import java.util.ResourceBundle;


/**
 * A feature extractor that finds the strongest frequency component of a 
 * signal, in Hz.
 *
 * <p>This is found by finding the highest bin in the power spectrum.
 *
 * <p>No extracted feature values are stored in objects of this class.
 *
 * @author Cory McKay
 */
public class StrongestFrequencyViaFFTMax
	extends FeatureExtractor
{

	@Override
	public FeatureExtractor prototype() {
		return new StrongestFrequencyViaFFTMax();
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
	public StrongestFrequencyViaFFTMax()
	{
		ResourceBundle bundle = ResourceBundle.getBundle("Translations");
		String name = "Strongest Frequency Via FFT Maximum";
		String description = bundle.getString("the.strongest.frequency.component.of.a.signal.in.hz.found.via.finding.the.fft.bin.with.the.highest.power");
		boolean is_sequential = true;
		int dimensions = 1;
		definition = new FeatureDefinition( name,
		                                    description,
		                                    is_sequential,
		                                    dimensions );
		definition.setDependency("Power Spectrum");
		definition.addDependency("FFT Bin Frequency Labels");
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
		double[] power_spectrum = other_feature_values[0];
		double[] labels = other_feature_values[1];
		int highest_bin = jAudioFeatureExtractor.GeneralTools.Statistics. getIndexOfLargest(power_spectrum);
		double[] result = new double[1];
		result[0] = labels[highest_bin];
		return result;
	}
}