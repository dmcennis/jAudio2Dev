/*
 * @(#)BeatHistogram.java	1.0	April 5, 2005.
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
 * A feature extractor that extracts the Beat Histogram from a signal. This is
 * histogram showing the strength of different rhythmic periodicities in a
 * signal. 
 * <p>
 * This is calculated by taking the RMS of 256 windows and then taking
 * the FFT of the result.
 * <p>
 * No extracted feature values are stored in objects of this class.
 * <p>
 * <b>IMPORTANT:
 * </P>
 * The window size of 256 RMS windows used here is hard-coded into the class
 * BeatHistogramLabels. Any changes to the window size in this class must be
 * made there as well.</b>
 * <p>
 * Daniel McEnnis 05-07-05 Added setElement, getElement, setElement, and clone
 * functions
 * 
 * @author Cory McKay
 */
public class BeatHistogram extends FeatureExtractor {

	private int number_windows = 256;

    @Override
    public FeatureExtractor prototype() {
        return new BeatHistogram();
    }

    @Override
    public FeatureExtractor prototype(Properties props) {
        return prototype();
    }

	/* CONSTRUCTOR ************************************************************* */

	/**
	 * Basic constructor that sets the definition and dependencies (and their
	 * offsets) of this feature.
	 */
	public BeatHistogram() {
		ResourceBundle bundle = ResourceBundle.getBundle("Translations");
		String name = "Beat Histogram";
		String description = bundle.getString("a.histogram.showing.the.relative.strength.of.different.rhythmic.periodicities.tempi.in.a.signal.found.by.calculating.the.auto.correlation.of.the.rms");
		boolean is_sequential = true;
		int dimensions = 0;
		ParameterInternal param = ParameterFactory.newInstance().create("NumberOfWindows",Integer.class,bundle.getString("a.histogram.showing.the.relative.strength.of.different.rhythmic.periodicities.tempi.in.a.signal.found.by.calculating.the.auto.correlation.of.the.rms"));
		param.setRestrictions(SyntaxCheckerFactory.newInstance().create(1,1,(new NumericQuery()).buildQuery(0.0,false, NumericQuery.Operation.GT),Integer.class));
		definition = new FeatureDefinition(name, description, is_sequential,
				dimensions);
		definition.add(param);
		// int number_windows = 256;

		definition.setDependency("Root Mean Square",0,number_windows);
	}

	/* PUBLIC METHODS ********************************************************* */

	/**
	 * Extracts this feature from the given samples at the given sampling rate
	 * and given the other feature values.
	 * <p>
	 * In the case of this feature the sampling_rate is ignored.
	 * 
	 * @param samples
	 *            The samples to extract the feature from.
	 * @param sampling_rate
	 *            The sampling rate that the samples are encoded with.
	 * @param other_feature_values
	 *            The values of other features that are needed to calculate this
	 *            value. The order and offsets of these features must be the
	 *            same as those returned by this class's getDependencies and
	 *            getDependencyOffsets methods respectively. The first index
	 *            indicates the feature/window and the second indicates the
	 *            value.
	 * @return The extracted feature value(s).
	 * @throws Exception
	 *             Throws an informative exception if the feature cannot be
	 *             calculated.
	 */
	public double[] extractFeature(double[] samples, double sampling_rate,
			double[][] other_feature_values) throws Exception {
		double[] rms = new double[other_feature_values.length];
		for (int i = 0; i < rms.length; i++)
			rms[i] = other_feature_values[i][0];

		double effective_sampling_rate = sampling_rate / ((double) rms.length);

		int min_lag = (int) (0.286 * effective_sampling_rate);
		int max_lag = (int) (3.0 * effective_sampling_rate);
		double[] auto_correlation = jAudioFeatureExtractor.jAudioTools.DSPMethods
				.getAutoCorrelation(rms, min_lag, max_lag);
		return auto_correlation;
	}

	/**
	 * Helper function to set window length for this feature. Note that this
	 * feature does *not* conform to the syntax of setWindow so this feature is
	 * not affected by a global window change. This is necessary since the beat
	 * bins have a different meaning than most windowed features.
	 * 
	 * @param n
	 *            new number of beat bins
	 * @throws Exception
	 *             thrown if the new value is less than 2
	 */
	public void setWindowLength(int n) throws Exception {
		if (n < 2) {
			ResourceBundle bundle = ResourceBundle.getBundle("Translations");
			throw new Exception(
					bundle.getString("beathistogram.window.length.must.be.greater.than.1"));
		} else {
			number_windows = n;
			definition.setDependency("Root Mean Square",0,number_windows);
		}
	}

	/**
	 * Function permitting an unintelligent outside function (ie. EditFeatures
	 * frame) to get the default values used to populate the table's entries.
	 * The correct index values are inferred from definition.attribute value.
	 * 
	 * @param index
	 *            which of Beat Histograms's attributes should be edited.
	 */
	public String getElement(int index) throws Exception {
		if (index != 0) {
			ResourceBundle bundle = ResourceBundle.getBundle("Translations");
			throw new Exception(String.format(bundle.getString("internal.error.invalid.index.d.sent.to.areamoments.getelement"),index));
		} else {
			return Integer.toString(number_windows);
		}
	}

	/**
	 * Function permitting an unintelligent outside function (i.e. EditFeatures
	 * frame) to set the default values used to populate the table's entries.
	 * Like getElement, the correct index values are inferred from the
	 * definition.getAttributes() value.
	 * 
	 * @param index
	 *            attribute to be set
	 * @param value
	 *            new value of the attribute
	 */
	public void setElement(int index, String value) throws Exception {
		if (index != 0) {
			ResourceBundle bundle = ResourceBundle.getBundle("Translations");
			throw new Exception(String.format(bundle.getString("internal.error.invalid.index.d.sent.to.areamoments.setelement"),index));
		} else {
			try {
				int type = Integer.parseInt(value);
				setWindowLength(type);
			} catch (Exception e) {
				ResourceBundle bundle = ResourceBundle.getBundle("Translations");
				throw new Exception(
						bundle.getString("length.of.area.method.of.moments.must.be.an.integer"));
			}
		}
	}

	/**
	 * Create an identical copy of this feature. This permits FeatureExtractor
	 * to use the prototype pattern to create new composite features using
	 * metafeatures.
	 */
	public Object clone() {
		BeatHistogram ret = new BeatHistogram();
		ret.number_windows = number_windows;
		return ret;
	}

}