/*
 * @(#)BeatHistogramLabels.java	1.0	April 7, 2005.
 *
 * McGill Univarsity
 */

package org.jaudio.dsp.features.modules;

import org.dynamicfactory.descriptors.ParameterFactory;
import org.dynamicfactory.descriptors.ParameterInternal;
import org.dynamicfactory.descriptors.Properties;
import org.dynamicfactory.descriptors.SyntaxCheckerFactory;
import org.dynamicfactory.propertyQuery.NumericQuery;
import org.jaudio.dsp.features.FeatureDefinition;
import org.jaudio.dsp.features.FeatureExtractor;

import java.util.ResourceBundle;


/**
 * A "feature extractor" that calculates the bin labels, in beats per minute, of 
 * a beat histogram.
 *
 * <p>Although this is not a useful feature for the purposes of classifying,
 * it can be useful for calculating other features.
 *
 * <p><b>IMPORTANT:</P> The window size of 256 RMS windows used in the 
 * BeatHistogram is hard-coded into this class. Any changes to the value
 * in that class must be made here as well.
 *
 *<p>Daniel McEnnis	05-08-05	added setBinNumber, getElement, setElement, and clone
 * @author Cory McKay
 */
public class BeatHistogramLabels
	extends FeatureExtractor
{
	@Override
	public FeatureExtractor prototype() {
		return new BeatHistogramLabels();
	}

	@Override
	public FeatureExtractor prototype(Properties props) {
		return prototype();
	}


//	private int binNumber = 256;
	/* CONSTRUCTOR **************************************************************/
	
	
	/**
	 * Basic constructor that sets the definition and dependencies (and their
	 * offsets) of this feature.
	 */
	public BeatHistogramLabels()
	{
		ResourceBundle bundle = ResourceBundle.getBundle("Translations");
		String name = "Beat Histogram Bin Labels";
		String description = bundle.getString("the.bin.label.in.beats.per.minute.of.each.beat.histogram.bin.not.useful.as.a.feature.in.itself.but.useful.for.calculating.other.features.from.the.beat.histogram");
		boolean is_sequential = true;
		int dimensions = 0;
		ParameterInternal param = ParameterFactory.newInstance().create("NumberOfBins",Integer.class,description);
        param.setRestrictions(SyntaxCheckerFactory.newInstance().create(1,1,(new NumericQuery()).buildQuery(0.0,false, NumericQuery.Operation.GT),Integer.class));
		param.set(256);
		definition = new FeatureDefinition( name,
		                                    description,
		                                    is_sequential,
		                                    dimensions );
        definition.add(param);
		definition.setDependency("Beat Histogram");
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
		double[] beat_histogram = other_feature_values[0];

		if (beat_histogram != null)
		{
			double effective_sampling_rate = sampling_rate / ((double)quickGet("NumberOfBins"));

			int min_lag = (int) (0.286 * effective_sampling_rate);
			int max_lag = (int) (3.0 * effective_sampling_rate);
			double[] labels =
				jAudioFeatureExtractor.jAudioTools.DSPMethods.getAutoCorrelationLabels( effective_sampling_rate,
				                                                                        min_lag,
				                                                                        max_lag );

			for (int i = 0; i < labels.length; i++)
				labels[i] *= 60.0;
			return labels;
		}
		else
			return null;

	}
	
	/**
	 * Sets the bin Number - changes should be linked to beatHistogramType
	 * @param n new number of beat bins
	 * @throws Exception thrown if new number of bins is les than 2
	 */
	public void setBinNumber(int n) throws Exception{
		if(n < 2){
			ResourceBundle bundle = ResourceBundle.getBundle("Translations");
			throw new Exception(bundle.getString("there.must.be.at.least.2.bins.in.beat.histogram.labels"));
		}else{
			set("NumberOfBins",n);
		}
	}
}
