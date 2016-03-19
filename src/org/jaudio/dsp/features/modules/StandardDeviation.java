package org.jaudio.dsp.features.modules;

import org.dynamicfactory.descriptors.Properties;
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

	int sampleWidth = 100;

    FeatureExtractor child;
    @Override
    public FeatureExtractor prototype() {
        return this;
    }

    @Override
    public FeatureExtractor prototype(Properties props) {
        if(properties.quickCheck("Feature",FeatureExtractor.class)){
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
		this.chainMetaFeatureFactory(mff);
	}

	/**
	 * Factory method for this class which generates a fully usable MetaFeature
	 * object. Using the structure stored in this Mean object, create a new
	 * FeatureExtractor with the given specific FeatureExtraction as a base. If
	 * we are calulating the mean of another meta-feature, recursively create
	 * the underlying meta feature first.
	 */
	public MetaFeatureFactory defineFeature(FeatureExtractor fe) {
		ResourceBundle bundle = ResourceBundle.getBundle("Translations");
		StandardDeviation ret = new StandardDeviation();
		if ((fe_ != null) & (fe_ instanceof MetaFeatureFactory)) {
			ret.fe_ = ((MetaFeatureFactory) fe_).defineFeature(fe);
		} else {
			ret.fe_ = fe;
		}
		String name = "Standard Deviation of "
				+ ret.fe_.getFeatureDefinition().getName();
		String description = String.format(bundle.getString("standard.deviation.of.s.s"),ret.fe_.getFeatureDefinition().getName(),ret.fe_.getFeatureDefinition().getDescription());

		String[] oldAttributes = fe.getFeatureDefinition().getAttributes();
		String[] myAttributes = new String[oldAttributes.length + 1];
		for (int i = 0; i < oldAttributes.length; ++i) {
			myAttributes[i] = oldAttributes[i];
		}
		myAttributes[myAttributes.length - 1] = bundle.getString("size.of.window.to.calculate.accross");

		ret.definition = new FeatureDefinition(name, description, true, ret.fe_
				.getFeatureDefinition().getDimensions(), myAttributes);
        ret.definition.setDependency(ret.fe_.getFeatureDefinition().getName(),0,sampleWidth);
		return ret;
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
			sampleWidth = n;
			String tmp;
			if (fe_ != null) {
				tmp = fe_.getFeatureDefinition().getName();
                definition.setDependency(tmp,0,sampleWidth);
			}
		}
	}

	/**
	 * Function that must be overridden to allow this feature to be set globally
	 * by GlobalChange frame.
	 *
	 * @param index
	 *            the number of windows of offset to be used in calculating this
	 *            feature
	 */
	public String getElement(int index) throws Exception {
		if ((index >= definition.getAttributes().length) || (index < 0)) {
            ResourceBundle bundle = ResourceBundle.getBundle("Translations");
			throw new Exception(String.format(bundle.getString("internal.error.request.for.an.invalid.index.d1"),index));
		} else if (index == definition.getAttributes().length - 1) {
			return Integer.toString(sampleWidth);
		} else if (fe_ != null) {
			return fe_.getElement(index);
		} else {
            ResourceBundle bundle = ResourceBundle.getBundle("Translations");
			throw new Exception(bundle.getString("internal.error.request.for.child.attribute.in.standrad.deviation.when.the.child.is.null"));
		}
	}

	/**
	 * Function permitting an unintelligent outside function (ie. EditFeatures
	 * frame) to get the default values used to populate the table's entries.
	 * The correct index values are inferred from definition.attribute value.
	 *
	 * @param index
	 *            which of AreaMoment's attributes should be edited.
	 */
	public void setElement(int index, String value) throws Exception {
		if ((index >= definition.getAttributes().length) || (index < 0)) {
            ResourceBundle bundle = ResourceBundle.getBundle("Translations");
			throw new Exception(String.format(bundle.getString("internal.error.request.for.an.invalid.index.d2"),index));
		} else if (index == definition.getAttributes().length - 1) {
			try {
				int type = Integer.parseInt(value);
				if (type <= 1) {
                    ResourceBundle bundle = ResourceBundle.getBundle("Translations");
					throw new Exception(
                            bundle.getString("width.of.the.window.must.be.greater.than.1"));
				} else {
					setWindow(type);
				}
			} catch (NumberFormatException e) {
                ResourceBundle bundle = ResourceBundle.getBundle("Translations");
				throw new Exception(bundle.getString("width.of.window.must.be.an.integer"));
			}
		} else if (fe_ != null) {
			fe_.setElement(index, value);
		} else {
            ResourceBundle bundle = ResourceBundle.getBundle("Translations");
			throw new Exception(bundle.getString("request.to.set.a.child.in.standarddeviation.attrbiute.when.the.child.is.null"));
		}
	}

	/**
	 * Create an identical copy of this feature. This permits FeatureExtractor
	 * to use the prototype pattern to create new composite features using
	 * metafeatures.
	 */
	public Object clone() {
		if(fe_ == null){
			return new StandardDeviation();
		}else if (fe_ instanceof MetaFeatureFactory) {
			StandardDeviation ret = new StandardDeviation();
			ret.fe_ = (FeatureExtractor)fe_.clone();
			ret.definition = new FeatureDefinition(definition.getName(),definition.getDescription(),true,definition.getDimensions(),definition.getAttributes().clone());
			try {
				ret.setWindow(sampleWidth);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return ret;
		} else {
			StandardDeviation ret = (StandardDeviation)defineFeature((FeatureExtractor)fe_.clone());
			try {
				ret.setWindow(sampleWidth);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return ret;
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
