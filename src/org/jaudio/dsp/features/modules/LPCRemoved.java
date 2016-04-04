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
 * Linear Prediction Coeffecients calculated according to 'Numerical Recipes in C' (Press at al. 1992)
 * <p>Press, W., and S. Teukolsky, and W. Vetterling, and B. Flannery. 1992. <i>Numerical Recipes in C</i>. Cambridge: Cambridge University Press.
 * 
 * @author Daniel McEnnis
 *
 */
public class LPCRemoved extends FeatureExtractor {

//	private int (int)quickGet("Dimensions") = 10;

	@Override
	public FeatureExtractor prototype() {
		return new LPCRemoved();
	}

	@Override
	public FeatureExtractor prototype(Properties props) {
		return prototype();
	}


	public LPCRemoved() {
		ResourceBundle bundle = ResourceBundle.getBundle("Translations");
		String name = "LPC";
		String description = bundle.getString("linear.predictive.encoding.implemented.from.numerical.recipes.in.c");
		ParameterInternal param = ParameterFactory.newInstance().create("Dimensions",Integer.class,description);
		param.setLongDescription("");
		param.setRestrictions(SyntaxCheckerFactory.newInstance().create(1,1,(new NumericQuery()).buildQuery(0.0,false, NumericQuery.Operation.GT),Integer.class));
		param.set(10);

		definition = new FeatureDefinition(name, description, true,
				(int)quickGet("Dimensions"));
		definition.set(param);
	}

	/**
	 * Blatantly stolen from Numerical Recipes (Press et al. 1992).
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
		double ret[] = new double[10];
		double wk1[] = new double[samples.length];
		double wk2[] = new double[samples.length];
		double wkm[] = new double[(int)quickGet("Dimensions")];
		wk1[0] = samples[0];
		wk2[samples.length - 2] = samples[samples.length - 1];
		for (int i = 1; i < samples.length - 1; ++i) {
			wk1[i] = samples[i];
			wk2[i - 1] = samples[i];
		}
		for (int i = 0; i < (int)quickGet("Dimensions"); ++i) {
			double num = 0.0;
			double denom = 0.0;
			for (int j = 0; j < (samples.length - i); ++j) {
				num += wk1[j] * wk2[j];
				denom += wk1[j] * wk1[j] + wk2[j] * wk2[j];
			}
			ret[i] = 2.0 * num / denom;
			for (int j = 0; j < i; ++j) {
				ret[j] = wkm[j] - ret[i] * wkm[i - j];
			}
			for (int j = 0; j <= i; ++j) {
				wkm[j] = ret[j];
			}
			for (int j = 0; j < (samples.length - i - 1); ++j) {
				wk1[j] -= wkm[i] * wk2[j];
				wk2[j] = wk2[j + 1] - wkm[i] * wk1[j + 1];
			}
		}

		return ret;
	}

	/**
	 * Permits the number o LPC coeffecients to be calculated. This is a unique
	 * feature in that the number of dimensions of the feature are changed by
	 * this function, requiring a reference back to the parent to redraw the
	 * table displaying this information.
	 * 
	 * @param n
	 *            number of coeffecients to be calculated.
	 * @throws Exception
	 *             thrown if less than 1 feature is to be calculated.
	 */
	public void setNumberDimensions(int n) throws Exception {
		if (n < 1) {
            ResourceBundle bundle = ResourceBundle.getBundle("Translations");
			throw new Exception(bundle.getString("lpc.must.have.at.least.1.dimension"));
		} else {
			set("Dimensions",n);
			definition.setDimensions((int)quickGet("Dimensions"));
			parent.updateTable();
			// System.out.println("Updating Table");
		}
	}
}
