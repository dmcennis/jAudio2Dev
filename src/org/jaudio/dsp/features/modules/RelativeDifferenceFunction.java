package org.jaudio.dsp.features.modules;

import org.dynamicfactory.descriptors.Properties;
import org.jaudio.dsp.features.FeatureDefinition;
import org.jaudio.dsp.features.FeatureExtractor;

import java.util.ResourceBundle;

/**
 * This feature calculates the log of the derivative of the RMS. This is useful
 * for onset detection.
 * 
 * @author Daniel McEnnis
 */
public class RelativeDifferenceFunction extends FeatureExtractor {

	@Override
	public FeatureExtractor prototype() {
		return new RelativeDifferenceFunction();
	}

	@Override
	public FeatureExtractor prototype(Properties props) {
		return prototype();
	}


	/**
	 * Basic constructor that sets the definition and dependencies (and their
	 * offsets) of this feature.
	 */
	public RelativeDifferenceFunction() {
		ResourceBundle bundle = ResourceBundle.getBundle("Translations");
		String name = "Relative Difference Function";
		String decsription = bundle.getString("log.of.the.derivative.of.rms.used.for.onset.detection");
		definition = new FeatureDefinition(name, decsription, true, 1);
        definition.setDependency("Root Mean Square",0,2);
	}

	/**
	 * Calculates the log ofthe derivative of the RMS from the last 2 versions
	 * of RMS
	 * 
	 * @param samples
	 *            The samples to extract the feature from.
	 * @param sampling_rate
	 *            The sampling rate that the samples are encoded with.
	 * @param other_feature_values
	 *            The values of other features that are needed to calculate this
	 *            value. The order and offsets of these features must be the
	 *            same as those returned by this class's getDependencies and
	 *            getDependencyOffsets methods respectively. The first indice
	 *            indicates the feature/window and the second indicates the
	 *            value.
	 * @return The extracted feature value(s).
	 * @throws Exception
	 *             Throws an informative exception if the feature cannot be
	 *             calculated.
	 */
	public double[] extractFeature(double[] samples, double sampling_rate,
			double[][] other_feature_values) throws Exception {
		double[] ret = new double[1];
		double difference = Math.abs(other_feature_values[0][0]
				- other_feature_values[1][0]);
		if (difference < 1E-50) {
			difference = 1E-50;
		}
		ret[0] = Math.log(difference);

		return ret;

	}

	/**
	 * Create an identical copy of this feature. This permits FeatureExtractor
	 * to use the prototype pattern to create new composite features using
	 * metafeatures.
	 */
	public Object clone() {
		return new RelativeDifferenceFunction();
	}

}
