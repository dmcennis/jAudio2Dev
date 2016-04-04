package org.jaudio.dsp.features.modules;

import org.dynamicfactory.descriptors.ParameterFactory;
import org.dynamicfactory.descriptors.ParameterInternal;
import org.dynamicfactory.descriptors.Properties;
import org.dynamicfactory.descriptors.SyntaxCheckerFactory;
import org.dynamicfactory.propertyQuery.NumericQuery;
import org.jaudio.dsp.features.FeatureDefinition;
import org.jaudio.dsp.features.FeatureExtractor;
import org.jaudio.dsp.features.MetaFeatureFactory;

import java.util.ResourceBundle;

/**
 * Calculates the Standard Deviation of a feature over a large running window.
 * 
 * @author Daniel McEnnis
 *
 */
public class StandardDeviation extends MetaFeatureFactory {

    FeatureExtractor child;
    @Override
    public FeatureExtractor prototype() {
        return this;
    }

    @Override
    public FeatureExtractor prototype(Properties props) {
        if(quickCheck("Feature",FeatureExtractor.class)){
            StandardDeviation ret = new StandardDeviation();
            ret.child = buildChild(props);
            return ret;
        }else{
            return this;
        }
    }


	/**
	 * Basic constructor that initializes the metafeautres values properly for
	 * use as a factory.
	 */
	public StandardDeviation() {
		super();
        ParameterInternal param = ParameterFactory.newInstance().create("WindowLength",Integer.class,"The number of windows to calculate a mean across.");
        param.setLongDescription("");
        param.setRestrictions(SyntaxCheckerFactory.newInstance().create(1,1,(new NumericQuery()).buildQuery(0.0,false, NumericQuery.Operation.GT),Integer.class));
        param.set(10);
		definition.add(param);
	}

	/**
	 * Convenience constructor to create a new factory object with the given
	 * dependant metafeature
	 *
	 * @param mff
	 *            metafeature factory that this newly created object should
	 *            depend upon.
	 */
	public StandardDeviation(MetaFeatureFactory mff) {
		super();
	}

	/**
	 * Calculates the standard deviation over last 100 windows
	 *
	 * @param samples
	 *            signal being processed
	 * @param sampling_rate
	 *            sample rate of the signal
	 * @param other_feature_values
	 *            dependancies of the current signal
	 * @return standard deviation over last 100 values of dependant feature.
	 */
	public double[] extractFeature(double[] samples, double sampling_rate,
			double[][] other_feature_values) throws Exception {
		double[] ret = new double[other_feature_values[0].length];
		double[] x2 = new double[other_feature_values[0].length];
		double[] x = new double[other_feature_values[0].length];
		for (int i = 0; i < other_feature_values[0].length; ++i) {
			x2[i] = 0.0;
			x[i] = 0.0;
			for (int j = 0; j < other_feature_values.length; ++j) {
				x2[i] += other_feature_values[j][i] * other_feature_values[j][i];
				x[i] += other_feature_values[j][i];
			}
		}
		for (int i = 0; i < other_feature_values[0].length; ++i) {
			ret[i] = x[i] * x[i] - x2[i];
			ret[i] /= other_feature_values.length-1;
			ret[i] = Math.sqrt(ret[i]);
		}
		return ret;
	}

	/**
	 * Function permits this class to respond to a global window change request.
	 *
	 * @param n
	 *            new window length
	 * @throws Exception
	 *             thrown if the new window size is less than 2
	 */
	public void setWindow(int n) throws Exception {
		if (n <= 1) {
            ResourceBundle bundle = ResourceBundle.getBundle("Translations");
			throw new Exception(bundle.getString("width.must.be.2.or.greater"));
		} else {
			set("WindowLength",n);
			String tmp;
			if (fe_ != null) {
				tmp = fe_.getFeatureDefinition().getName();
                definition.setDependency(tmp,0,(int)quickGet("WindowLength"));
			}
		}
	}

	/**
	 * Overridden to regenerate the feature definition. Perhaps its should be
	 * kept purely virtual, but currently, attributes and dimensions are
	 * recalculated for each iteration. This is necessary so that changes in
	 * children's defintions get propogated back to the top level. As of
	 * 05-08-05 LPC is the only feature that requires this.
	 */
	public FeatureDefinition getFeatureDefinition() {
		String name = definition.getName();
		String description = definition.getDescription();
		String[] attributes;
		int dimensions;
		FeatureDefinition childFD = null;
		if (fe_ != null) {
			childFD = fe_.getFeatureDefinition();
		} else {
			return definition;
		}
		attributes = new String[childFD.getAttributes().length + 1];
		for (int i = 0; i < childFD.getAttributes().length; ++i) {
			attributes[i] = childFD.getAttributes()[i];
		}
        ResourceBundle bundle = ResourceBundle.getBundle("Translations");
		attributes[attributes.length - 1] = bundle.getString("size.of.window.for.standard.deviation");
		dimensions = childFD.getDimensions();
		definition = new FeatureDefinition(name, description, true, dimensions,
				attributes);
		return definition;
	}

}
