/*
 * @(#)FractionOfLowEnergyWindows.java	1.0	April 5, 2005.
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
 * A feature extractor that extracts the Fraction Of Low Energy Windows from
 * window to window. This is a good measure of how much of a signal is quiet
 * relative to the rest of a signal. <pThis is calculated by taking the mean of
 * the RMS of the last 100 windows and finding what fraction of these 100
 * windows are below the mean.
 * <p>
 * No extracted feature values are stored in objects of this class.
 * <p>
 * Daniel McEnnis 05-07-05 added number_of_windows as editable property. Added
 * getElement, setElement, and clone
 * <p>
 * Daniel McEnnis 05-08-05 added setWindow to permit this feature to be edited
 * by GlobalWindow frame.
 * 
 * @author Cory McKay
 */
public class FractionOfLowEnergyWindows extends FeatureExtractor {

	@Override
	public FeatureExtractor prototype() {
		return new FractionOfLowEnergyWindows();
	}

	@Override
	public FeatureExtractor prototype(Properties props) {
		return prototype();
	}



	//private int number_windows = 100;

	/* CONSTRUCTOR ************************************************************* */

	/**
	 * Basic constructor that sets the definition and dependencies (and their
	 * offsets) of this feature.
	 */
	public FractionOfLowEnergyWindows() {
		ResourceBundle bundle = ResourceBundle.getBundle("Translations");
		String name = "Fraction Of Low Energy Windows";
		String description = bundle.getString("the.fraction.of.the.last.100.windows.that.has.an.rms.less.than.the.mean.rms.in.the.last.100.windows.this.can.indicate.how.much.of.a.signal.is.quiet.relative.to.the.rest.of.the.signal");
		boolean is_sequential = true;
		int dimensions = 1;
		definition = new FeatureDefinition(name, description, is_sequential,
				dimensions);

        ParameterInternal param = ParameterFactory.newInstance().create("FractionOfLowEnergyWindows",Double.class,description);
        param.setRestrictions(SyntaxCheckerFactory.newInstance().create(1,1,(new NumericQuery()).buildQuery(0.0,false, NumericQuery.Operation.GT),Integer.class));
        param.set(100);
		definition.add(param);

		definition.setDependency("Root Mean Square",0,(int)quickGet("FractionOfLowEnergyWindows"));
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
		double average = 0.0;
		for (int i = 0; i < other_feature_values.length; i++)
			average += other_feature_values[i][0];
		average = average / ((double) other_feature_values.length);

		int count = 0;
		for (int i = 0; i < other_feature_values.length; i++)
			if (other_feature_values[i][0] < average)
				count++;

		double[] result = new double[1];
		result[0] = ((double) count) / ((double) other_feature_values.length);

		return result;
	}

	/**
	 * Function that must be overridden to allow this feature to be set globally
	 * by GlobalChange frame.
	 * 
	 * @param n
	 *            the number of windows of offset to be used in calculating this
	 *            feature
	 */
	public void setWindow(int n) throws Exception {
		if (n < 2) {
			ResourceBundle bundle = ResourceBundle.getBundle("Translations");
			throw new Exception(
					bundle.getString("fraction.of.low.energy.frames.s.window.length.must.be.2.or.greater"));
		} else {
			set("FractionOfLowEnergyWindows",n);
            definition.setDependency("Root Mean Square",0,(int)quickGet("FractionOfLowEnergyWindows"));
		}
	}
}