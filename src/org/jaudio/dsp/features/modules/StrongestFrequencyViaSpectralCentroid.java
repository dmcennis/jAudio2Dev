/*
 * @(#)StrongestFrequencyViaSpectralCentroid.java	1.0	April 7, 2005.
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
 * by looking at the spectral centroid.
 *
 * <p>This is found by mapping the fraction in the spectral centroid to a
 * frequency in Hze
 *
 * <p>No extracted feature values are stored in objects of this class.
 *
 * @author Cory McKay
 */
public class StrongestFrequencyViaSpectralCentroid
	extends FeatureExtractor
{

	@Override
	public FeatureExtractor prototype() {
		return new StrongestFrequencyViaSpectralCentroid();
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
	public StrongestFrequencyViaSpectralCentroid()
	{
		ResourceBundle bundle = ResourceBundle.getBundle("Translations");
		String name = "Strongest Frequency Via Spectral Centroid";
		String description = bundle.getString("the.strongest.frequency.component.of.a.signal.in.hz.found.via.the.spectral.centroid");
		boolean is_sequential = true;
		int dimensions = 1;
		definition = new FeatureDefinition( name,
		                                    description,
		                                    is_sequential,
		                                    dimensions );
		definition.setDependency("Spectral Centroid");
		definition.addDependency("Power Spectrum");
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
		double spectral_centroid = other_feature_values[0][0];
		double[] pow_spectrum = other_feature_values[1];
		double[] result = new double[1];
		result[0] = (spectral_centroid / pow_spectrum.length) * (sampling_rate / 2.0);
		return result;
	}
	
	/**
	 * Create an identical copy of this feature. This permits FeatureExtractor
	 * to use the prototype pattern to create new composite features using
	 * metafeatures.
	 */
	public Object clone(){
		return new StrongestFrequencyViaSpectralCentroid();
	}
}