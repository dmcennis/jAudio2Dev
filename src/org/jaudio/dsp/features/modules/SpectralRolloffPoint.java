/*
 * @(#)SpectralRolloffPoint.java	1.0	April 5, 2005.
 *
 * McGill Univarsity
 */

package org.jaudio.dsp.features.modules;

import org.dynamicfactory.descriptors.ParameterFactory;
import org.dynamicfactory.descriptors.ParameterInternal;
import org.dynamicfactory.descriptors.Properties;
import org.dynamicfactory.descriptors.SyntaxCheckerFactory;
import org.dynamicfactory.propertyQuery.AndQuery;
import org.dynamicfactory.propertyQuery.NumericQuery;
import org.dynamicfactory.propertyQuery.PropertyQuery;
import org.jaudio.dsp.features.FeatureDefinition;
import org.jaudio.dsp.features.FeatureExtractor;

import java.util.LinkedList;
import java.util.ResourceBundle;

/**
 * A feature extractor that extracts the Spectral Rolloff Point. This is a
 * measure measure of the amount of the right-skewedness of the power spectrum.
 * <p>
 * The spectral rolloff point is the fraction of bins in the power spectrum at
 * which 85% of the power is at lower frequencies.
 * <p>
 * No extracted feature values are stored in objects of this class.
 * 
 * @author Cory McKay
 */
public class SpectralRolloffPoint extends FeatureExtractor {

	protected double cutoff = 0.85;

	@Override
	public FeatureExtractor prototype() {
		return new SpectralRolloffPoint();
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
	public SpectralRolloffPoint() {
		ResourceBundle bundle = ResourceBundle.getBundle("Translations");
		String name = "Spectral Rolloff Point";
		String description = bundle.getString("the.fraction.of.bins.in.the.power.spectrum.at.which.85.of.the.power.is.at.lower.frequencies.this.is.a.measure.of.the.right.skewedness.of.the.power.spectrum");

		boolean is_sequential = true;
		int dimensions = 1;
		ParameterInternal param = ParameterFactory.newInstance().create("CutoffPoint",Double.class,description);

        LinkedList<PropertyQuery> list = new LinkedList<PropertyQuery>();
        list.add((new NumericQuery()).buildQuery(0.0,false, NumericQuery.Operation.GT));
        list.add((new NumericQuery()).buildQuery(1.0,false, NumericQuery.Operation.LT));
		param.setRestrictions(SyntaxCheckerFactory.newInstance().create(1,1,(new AndQuery()).build(list),Double.class));
		definition.add(param);

		definition = new FeatureDefinition(name, description, is_sequential,
				dimensions);
		definition.setDependency("Power Spectrum");
	}

	/* PUBLIC METHODS ********************************************************* */

	/**
	 * Extracts this feature from the given samples at the given sampling rate
	 * and given the other feature values.
	 * <p>
	 * In the case of this feature, the sampling_rate parameter is ignored.
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
		double[] pow_spectrum = other_feature_values[0];

		double total = 0.0;
		for (int bin = 0; bin < pow_spectrum.length; bin++)
			total += pow_spectrum[bin];
		double threshold = total * cutoff;

		total = 0.0;
		int point = 0;
		for (int bin = 0; bin < pow_spectrum.length; bin++) {
			total += pow_spectrum[bin];
			if (total >= threshold) {
				point = bin;
				bin = pow_spectrum.length;
			}
		}

		double[] result = new double[1];
		result[0] = ((double) point) / ((double) pow_spectrum.length);
		return result;
	}

	/**
	 * Permits users to set the rpecise cutoff point. THis value should be
	 * strictly between 0 and 1.
	 * 
	 * @param c
	 *            new cutoff point
	 * @throws Exception
	 *             thrown if c is not a real number strictly between 0 and 1.
	 */
	public void setCutoff(double c) throws Exception {
		if (Double.isInfinite(c) || Double.isNaN(c)) {
			ResourceBundle bundle = ResourceBundle.getBundle("Translations");
			throw new Exception(bundle.getString("spectralrolloff.cutoff.must.be.a.real.number"));
		} else if ((c <= 0.0) || (c >= 1.0)) {
			ResourceBundle bundle = ResourceBundle.getBundle("Translations");
			throw new Exception(
					bundle.getString("spectralrolloff.cutoff.must.be.gretaer.than.0.and.less.than.1"));
		} else {
			cutoff = c;
		}
	}
}