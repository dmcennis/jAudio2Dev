/**
 * 
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
 * Calculates linear predictive coeffecients of an signal. Also includes a
 * warping factor lambda that is disabled by default. Based upon code published
 * at www.musicdsp.org.
 * <p>
 * 2005. <i>Music-dsp source code archive</i> [online]. [cited 17 May 2005].
 * Available from the World Wide Web:
 * (http://musicdsp.org/archive.php?classid=2#137)
 * 
 * @author Daniel McEnnis
 */
public class LPC extends FeatureExtractor {

	@Override
	public FeatureExtractor prototype() {
		return new LPC();
	}

	@Override
	public FeatureExtractor prototype(Properties props) {
		return prototype();
	}


//	double lambda = 0.0;

//	int numDimensions = 10;

	/**
	 * Basic constructor for LPC that sets definition, dependencies, and offsets
	 * field.
	 */
	public LPC() {
		ResourceBundle bundle = ResourceBundle.getBundle("Translations");
		String name = "LPC";
		String description = bundle.getString("linear.prediction.coeffecients.calculated.using.autocorrelation.and.levinson.durbin.recursion");

		String[] attributes = new String[] {,
				 };
		definition = new FeatureDefinition(name, description, true, 10);

		ParameterInternal param = ParameterFactory.newInstance().create("Lambda",Double.class,bundle.getString("lambda.for.frequency.warping"));
		param.setLongDescription("");
		param.set(0.0);
		definition.add(param);

		ParameterInternal param2 = ParameterFactory.newInstance().create("Dimensions",Integer.class,bundle.getString("number.of.coeffecients.to.calculate"));
		param2.setLongDescription("");
		param2.setRestrictions(SyntaxCheckerFactory.newInstance().create(1,1,(new NumericQuery()).buildQuery(0.0,false, NumericQuery.Operation.GT),Integer.class));
		param2.set(10);
		definition.add(param2);
	}

	/**
	 * Code taken from www.musicdsp.org.
	 * <p>
	 * mail.mutagene.net.2005. <i>Music dsp source archive</i> [online] [cited
	 * May 10, 2005] Available on world wide web
	 * (http://musicdsp.org/archive.php?classid=2#137)
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
	 * @see FeatureExtractor#extractFeature(double[],
	 *      double, double[][])
	 */
	public double[] extractFeature(double[] samples, double sampling_rate,
			double[][] other_feature_values) throws Exception {
		// find the order-P autocorrelation array, R, for the sequence x of
		// length L and warping of lambda
		// wAutocorrelate(&pfSrc[stIndex],siglen,R,P,0);

		double[] R = new double[(int)quickGet("Dimensions") + 1];
		double K[] = new double[(int)quickGet("Dimensions")];
		double A[] = new double[(int)quickGet("Dimensions")];
		double[] dl = new double[samples.length];
		double[] Rt = new double[samples.length];
		double r1, r2, r1t;
		R[0] = 0;
		Rt[0] = 0;
		r1 = 0;
		r2 = 0;
		r1t = 0;
		for (int k = 0; k < samples.length; k++) {
			Rt[0] += samples[k] * samples[k];

			dl[k] = r1 - (double)quickGet("Lambda") * (samples[k] - r2);
			r1 = samples[k];
			r2 = dl[k];
		}
		for (int i = 1; i < R.length; i++) {
			Rt[i] = 0;
			r1 = 0;
			r2 = 0;
			for (int k = 0; k < samples.length; k++) {
				Rt[i] += dl[k] * samples[k];

				r1t = dl[k];
				dl[k] = r1 - (double)quickGet("Lambda") * (r1t - r2);
				r1 = r1t;
				r2 = dl[k];
			}
		}
		for (int i = 0; i < R.length; i++)
			R[i] = Rt[i];

		// LevinsonRecursion(unsigned int P, float *R, float *A, float *K)
		double Am1[] = new double[62];
		;

		if (R[0] == 0.0) {
			for (int i = 1; i < (int)quickGet("Dimensions"); i++) {
				K[i] = 0.0;
				A[i] = 0.0;
			}
		} else {
			double km, Em1, Em;
			int k, s, m;
			for (k = 0; k < (int)quickGet("Dimensions"); k++) {
				A[0] = 0;
				Am1[0] = 0;
			}
			A[0] = 1;
			Am1[0] = 1;
			km = 0;
			Em1 = R[0];
			for (m = 1; m < (int)quickGet("Dimensions"); m++) // m=2:N+1
			{
				double err = 0.0f; // err = 0;
				for (k = 1; k <= m - 1; k++)
					// for k=2:m-1
					err += Am1[k] * R[m - k]; // err = err + am1(k)*R(m-k+1);
				km = (R[m] - err) / Em1; // km=(R(m)-err)/Em1;
				K[m - 1] = -km;
				A[m] = km; // am(m)=km;
				for (k = 1; k <= m - 1; k++)
					// for k=2:m-1
					A[k] = Am1[k] - km * Am1[m - k]; // am(k)=am1(k)-km*am1(m-k+1);
				Em = (1 - km * km) * Em1; // Em=(1-km*km)*Em1;
				for (s = 0; s < (int)quickGet("Dimensions"); s++)
					// for s=1:N+1
					Am1[s] = A[s]; // am1(s) = am(s)
				Em1 = Em; // Em1 = Em;
			}
		}
		return K;
	}


	/**
	 * Edits the number of LPC coeffecients to be calculated. This is a unique
	 * feature in that the number of dimensions of the feature are changed by
	 * this function, requiring a reference back to the parent to redraw the
	 * table displaying this information.
	 * 
	 * @param n
	 *            number of coeffecients to be calculated.
	 * @throws Exception
	 *             thrown if less than 1 feature is to be calculated.
	 */
	public void setNumDimensions(int n) throws Exception {
		if (n < 1) {
			ResourceBundle bundle = ResourceBundle.getBundle("Translations");
			throw new Exception(String.format(bundle.getString("must.have.at.least.1.lpc.coeffecient.d.provided"),n));
		} else {
			set("Dimensions",Integer.class,n);
			String name = definition.getName();
			String description = definition.getDescription();
			String[] attributes = definition.getAttributes();
			definition = new FeatureDefinition(name, description, true,
                    (int)quickGet("Dimensions"), attributes);
			if (parent != null) {
				parent.updateTable();
			}
		}
	}

	/**
	 * Provides a mechanism for editing the 'frequency warping' factor in the
	 * LPC code from musicdsp.
	 * 
	 * @param l
	 *            new lmbda value
	 * @throws Exception
	 *             throws if the lambda value is not a real number.
	 */
	public void setLambda(double l) throws Exception {
		if (Double.isNaN(l) || Double.isInfinite(l)) {
			ResourceBundle bundle = ResourceBundle.getBundle("Translations");
			throw new Exception(bundle.getString("lambda.must.be.a.real.number"));
		} else {
			set("Lambda", l);

		}
	}

}
