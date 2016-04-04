package org.jaudio.dsp.features.modules;

import org.dynamicfactory.descriptors.ParameterFactory;
import org.dynamicfactory.descriptors.ParameterInternal;
import org.dynamicfactory.descriptors.Properties;
import org.dynamicfactory.descriptors.SyntaxCheckerFactory;
import org.dynamicfactory.propertyQuery.NumericQuery;
import org.jaudio.dsp.features.FeatureDefinition;
import org.jaudio.dsp.features.FeatureExtractor;

import java.util.LinkedList;
import java.util.ResourceBundle;

/**
 * Implements a very basic peak detection algorithm. Peaks are calculated by
 * finding local maximum in the values of the frequency bins. All maxima within
 * a threshold of the largest value is considered a peak. The thresholds of all
 * peaks are provided in order without its bin location in the original signal.
 * 
 * @author Daniel McEnnis
 */
public class PeakFinder extends FeatureExtractor {

	int peakThreshold = 10;

	@Override
	public FeatureExtractor prototype() {
		return new PeakFinder();
	}

	@Override
	public FeatureExtractor prototype(Properties props) {
		return prototype();
	}


	/**
	 * Basic constructor that sets the definition and dependencies (and their
	 * offsets) of this feature.
	 */
	public PeakFinder() {
		ResourceBundle bundle = ResourceBundle.getBundle("Translations");

		String name = "Peak Detection";
		String description = bundle.getString("all.peaks.that.are.within.an.order.of.magnitude.of.the.highest.point");
		ParameterInternal param = ParameterFactory.newInstance().create("PeakThreshold",Double.class,description);
		param.setRestrictions(SyntaxCheckerFactory.newInstance().create(1,1,(new NumericQuery()).buildQuery(0.0,false, NumericQuery.Operation.GT),Double.class));
		definition.add(param);

		definition = new FeatureDefinition(name, description, true, 0);
		definition.setDependency("Magnitude Spectrum");
	}

	/**
	 * Extracts a set of peaks from this window.
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
	public double[] extractFeature(double[] samples, double sampling_rate,
			double[][] other_feature_values) throws Exception {
		int count = 0;
		double max = 0.0;
		double bins[] = other_feature_values[0];
		for (int i = 0; i < other_feature_values[0].length; ++i) {
			if (other_feature_values[0][i] > max) {
				max = other_feature_values[0][i];
			}
		}
		max /= peakThreshold;
		// double[] tmp = fv.getFeatureVector();
		LinkedList<Double> val = new LinkedList<Double>();
		for (int i = 1; i < bins.length - 1; ++i) {
			if ((bins[i - 1] < bins[i]) && (bins[i + 1] < bins[i])
					&& (bins[i] > max)) {
				val.add(bins[i]);
			}
		}
		Double[] ret_tmp = val.toArray(new Double[] {});
		double[] ret = new double[ret_tmp.length];
		for (int i = 0; i < ret.length; ++i) {
			ret[i] = ret_tmp[i].doubleValue();
		}
		return ret;
	}

	/**
	 * Sets the minumum fraction of the max point that will register as a peak. The value is interpreted as 1/N of the maximum.
	 * @param peak			sets 1/N as threshold for peak detection.
	 * @throws Exception	thrown if a non-positive threshold is set.
	 */
	public void setPeakThreshold(int peak) throws Exception {
		if (peak <= 0) {
			ResourceBundle bundle = ResourceBundle.getBundle("Translations");
			throw new Exception(
					bundle.getString("peakfinder.peakthreshold.must.be.a.positive.value"));
		} else {
			peakThreshold = peak;
		}
	}
}
