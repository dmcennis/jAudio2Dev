package org.jaudio.dsp.features.modules;

import org.dynamicfactory.descriptors.ParameterFactory;
import org.dynamicfactory.descriptors.ParameterInternal;
import org.dynamicfactory.descriptors.Properties;
import org.dynamicfactory.descriptors.SyntaxCheckerFactory;
import org.dynamicfactory.propertyQuery.NumericQuery;
import org.jaudio.dsp.features.FeatureDefinition;
import org.jaudio.dsp.features.FeatureExtractor;
import org.oc.ocvolume.dsp.featureExtraction;

import java.util.ResourceBundle;

/**
 * Utilizes the MFCC code from the OrangeCow Volume project.
 * <p>
 * S. Pfeiffer, and C. Parker, and T. Vincent. 2005. <i>OC volume: Java speech
 * recognition engine</i>. 2005. [cited April 14, 2005].
 * 
 * @author Daniel McEnnis
 */
public class MFCC extends FeatureExtractor {

	@Override
	public FeatureExtractor prototype() {
		return new MFCC();
	}

	@Override
	public FeatureExtractor prototype(Properties props) {
		return prototype();
	}


	 featureExtraction fe;
	
	/**
	 * Construct a MFCC object, setting definition, dependencies, and offsets.
	 */
	public MFCC() {
		ResourceBundle bundle = ResourceBundle.getBundle("Translations");
		String name = "MFCC";
		String description = bundle.getString("mfcc.calculations.based.upon.orange.cow.code");
		ParameterInternal param = ParameterFactory.newInstance().create("NumberOfCoeffecients",Integer.class,description);
        param.setRestrictions(SyntaxCheckerFactory.newInstance().create(1,1,(new NumericQuery()).buildQuery(0.0,false, NumericQuery.Operation.GT),Integer.class));
        param.set(13);
		definition = new FeatureDefinition(name, description, true, 13);
		definition.setDependency("Magnitude Spectrum");
        definition.add(param);
		fe = new featureExtraction();
	}

	/**
	 * Calculate Mel Frequency Cepstrum Coeffecients from the magnitude spectrum
	 * of a signal
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

		int[] cbin = fe.fftBinIndices(sampling_rate,
				other_feature_values[0].length);
		double[] fbank = fe.melFilter(other_feature_values[0],
				cbin);
		double[] f = fe.nonLinearTransformation(fbank);
		double[] cepc = fe.cepCoefficients(f);
		return cepc;
	}
}
