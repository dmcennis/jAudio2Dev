package org.jaudio.dsp.features.modules;

import org.dynamicfactory.descriptors.Properties;
import org.jaudio.dsp.features.FeatureDefinition;
import org.jaudio.dsp.features.FeatureExtractor;
import org.jaudio.dsp.features.MetaFeatureFactory;

import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class implementing the most basic discrete derivative of a dependant feature.
 * Extends the MetaFeatureFactory abstract class.
 * 
 * @author Daniel McEnnis
 */
public class Derivative extends MetaFeatureFactory {

    FeatureExtractor child;
	@Override
	public FeatureExtractor prototype() {
		Logger.getLogger(this.getClass().getName()).log(Level.SEVERE,String.format("Attempting to create %s without providing a child feature","AutocorrelationHistogram"));
		return null;
	}

	@Override
	public FeatureExtractor prototype(Properties props) {
        if(quickCheck("Feature",FeatureExtractor.class)){
            child = buildChild(props);
            return this;
        }else{
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE,String.format("Attempting to create %s without providing a child feature","AutocorrelationHistogram"));
            return null;
        }
	}


	/**
	 * Basic constructor that initializes the metaFeature class variables
	 * appropiaretely for factory use
	 */
	public Derivative() {
		super();

	}

	/**
	 * Extracts the difference between adjacent points as a basic implementation
	 * of a discrete dirivative.
	 *
	 * @param samples
	 *            signal to be analyzed. Not used by this feature
	 * @param sampling_rate
	 *            sampling rate of the signal. Not used by this feature
	 * @param other_feature_values
	 *            provides most recent and next most recent values to be
	 *            compared
	 * @return discrete derivative of the underlying feature
	 */
	public double[] extractFeature(double[] samples, double sampling_rate,
			double[][] other_feature_values) throws Exception {
		double[] ret = new double[other_feature_values[0].length];
		for (int i = 0; i < ret.length; ++i) {
			ret[i] = other_feature_values[0][i] - other_feature_values[1][i];
		}
		return ret;
	}
}
