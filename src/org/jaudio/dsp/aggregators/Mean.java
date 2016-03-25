/**
 * Mean Aggregator
 * Created for the 2006 ISMIR jAudio release
 * Created by Daniel McEnnis
 */
package org.jaudio.dsp.aggregators;

import org.dynamicfactory.descriptors.Properties;
import org.jaudio.dsp.features.FeatureDefinition;
import org.jaudio.dsp.features.FeatureDependency;
import org.jaudio.dsp.features.FeatureExtractor;

import java.util.ResourceBundle;

/**
 * Calculates the mean of a feature accross all windows where it is defined.
 * When the feature has more than one dimension, the mean has an equal number of
 * dimensions and the value of each dimension is the mean of that dimension. If
 * the feature has a variable number of dimensions, the dimensionality of the
 * result is the largest number of dimensions present and the mean for each
 * dimension is calculated over all values defined for that dimension.
 * 
 * @author Daniel McEnnis
 */
public class Mean extends Aggregator {
	
	int feature;
	
	public Mean(){
        ResourceBundle bundle = ResourceBundle.getBundle("Translations");
		metadata = new AggregatorDefinition("Mean", bundle.getString("this.is.the.overall.average.over.all.windows"),true,null);
	}


	@Override
	public Aggregator prototype() {
		return new Mean();
	}

	@Override
	public Aggregator prototype(Properties props) {
		Mean mfcc = new Mean();
		if(props.quickCheck("Dependency", FeatureDependency.class)){
			mfcc.setSource((FeatureExtractor)props.quickGet("Dependency"));
		}
		return mfcc;
	}

	/**
	 * Provide a list of features that are to be aggregated by this feature.
	 * Returning null indicates that this aggregator accepts only one feature
	 * and every feature avaiable should be used.
	 * 
	 * @return list of features to be used by this aggregator or null
	 */
//	public String[] getFeaturesToApply() {
//		return null;
//	}

	/**
	 * 
	 * @see Aggregator#getFeatureDefinition()
	 */
	public FeatureDefinition getFeatureDefinition() {
		return definition;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jAudioFeatureExtractor.Aggregators.Aggregator#init(jAudioFeatureExtractor.AudioFeatures.FeatureExtractor[])
	 */
	public void init(int[] featureIndeci)
			throws Exception {
		feature = featureIndeci[0];
	}
	
	

	@Override
	public Object clone() {
		return new Mean();
	}

	@Override
	public void setSource(FeatureExtractor feature) {
		ResourceBundle bundle = ResourceBundle.getBundle("Translations");

		FeatureDefinition this_def = feature.getFeatureDefinition();
		definition = new FeatureDefinition(this_def.getName() + " Overall Average",
				String.format(bundle.getString("s.nthis.is.the.overall.average.over.all.windows2"),this_def.getDescription()),
				this_def.is_sequential(), this_def.getDimensions());
		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see jAudioFeatureExtractor.Aggregators.Aggregator#aggregate(double[][][])
	 */
	public void aggregate(double[][][] values) {
		if ((values == null) || (values.length == 0)) {
			result = new double[1];
			result[0] = Double.NaN;
			definition.setDimensions(1);
		} else {
			// find the max number of dimensions
			int max = -1;
			for (int i = 0; i < values.length; ++i) {
				if ((values[i][feature] != null)
						&& (values[i][feature].length > max)) {
					max = values[i][feature].length;
				}
			}
			if (max <= 0) {
				result = new double[] { 0.0 };
				definition.setDimensions(1);
			} else {
				// now calculate means over all the dimensions
				result = new double[max];
				definition.setDimensions(max);
				for (int i = 0; i < max; ++i) {
					int count = 0;
					double sum = 0.0;
					for (int j = 0; j < values.length; ++j) {
						if ((values[j][feature] != null)
								&& (values[j][feature].length > i)) {
							sum += values[j][feature][i];
							count++;
						}
					}
					if (count == 0) {
						result[i] = 0.0;
					} else {
						result[i] = sum / ((double) count);
					}
				}
			}
		}
	}


}
