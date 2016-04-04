package org.jaudio.dsp.features.modules;

import org.dynamicfactory.descriptors.Properties;
import org.jaudio.dsp.features.FeatureDefinition;
import org.jaudio.dsp.features.FeatureExtractor;
import org.jaudio.dsp.features.MetaFeatureFactory;

import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class implementing the most basic discrete derivative of a dependant feature.
 * Extends the MetaFeatureFactory abstract class.
 * 
 * @author Daniel McEnnis
 */
public class Derivative extends MetaFeatureFactory {

    FeatureExtractor child;
	@Override
	public FeatureExtractor prototype() {
		Logger.getLogger(this.getClass().getName()).log(Level.SEVERE,String.format("Attempting to create %s without providing a child feature","AutocorrelationHistogram"));
		return null;
	}

	@Override
	public FeatureExtractor prototype(Properties props) {
        if(quickCheck("Feature",FeatureExtractor.class)){
            child = buildChild(props);
            return this;
        }else{
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE,String.format("Attempting to create %s without providing a child feature","AutocorrelationHistogram"));
            return null;
        }
	}


	/**
	 * Basic constructor that initializes the metaFeature class variables
	 * appropiaretely for factory use
	 */
	public Derivative() {
		super();

	}

	/**
	 * A convenience consrtuctor that extends the basic constructor to allow
	 * specifying a dependant MetaFeatureFactory object.
	 *
	 * @param mff
	 *            dependant MetaFeatureFactory object
	 */
	public Derivative(MetaFeatureFactory mff) {
		super();
		this.chainMetaFeatureFactory(mff);
	}

	/**
	 * Factory class for creating a new FeatureExtraction object. Recursively
	 * constructs the new object if there exists a dependant MetaFeatureFactory
	 * object.
	 *
	 * @param fe
	 *            base feature that this feature is to be bvased upon.
	 * @return fully constructed MetaFeatureFactory object ready for feature
	 *         extraction.
	 */
	public MetaFeatureFactory defineFeature(FeatureExtractor fe) {
		ResourceBundle bundle = ResourceBundle.getBundle("Translations");
		Derivative tmp = new Derivative();
		if ((fe_ != null) & (fe_ instanceof MetaFeatureFactory)) {
			tmp.fe_ = ((MetaFeatureFactory) fe_).defineFeature(fe);
		} else {
			tmp.fe_ = fe;
		}
		String name = "Derivative of " + tmp.fe_.getFeatureDefinition().getName();
		String description = String.format(bundle.getString("derivative.of.s.s"), tmp.fe_.getFeatureDefinition().getName(), tmp.fe_.getFeatureDefinition().getDescription());
		String[] oldAttributes = tmp.fe_.getFeatureDefinition().getAttributes();
		tmp.definition = new FeatureDefinition(name, description, true, tmp.fe_
				.getFeatureDefinition().getDimensions(), oldAttributes);
		tmp.definition.setDependency(tmp.fe_.getFeatureDefinition().getName(),0,2);
		return tmp;
	}

	/**
	 * Extracts the difference between adjacent points as a basic implementation
	 * of a discrete dirivative.
	 *
	 * @param samples
	 *            signal to be analyzed. Not used by this feature
	 * @param sampling_rate
	 *            sampling rate of the signal. Not used by this feature
	 * @param other_feature_values
	 *            provides most recent and next most recent values to be
	 *            compared
	 * @return discrete derivative of the underlying feature
	 */
	public double[] extractFeature(double[] samples, double sampling_rate,
			double[][] other_feature_values) throws Exception {
		double[] ret = new double[other_feature_values[0].length];
		for (int i = 0; i < ret.length; ++i) {
			ret[i] = other_feature_values[0][i] - other_feature_values[1][i];
		}
		return ret;
	}

	/**
	 * Function permitting an unintelligent outside function (ie. EditFeatures
	 * frame) to get the default values used to populate the table's entries.
	 * The correct index values are inferred from definition.attribute value.
	 * <p>
	 * As a metafeature, recursively calls children for the feature requested.
	 *
	 * @param index
	 *            which of AreaMoment's attributes should be edited.
	 */
	public String getElement(int index) throws Exception {
		if ((index >= definition.getAttributes().length) || (index < 0)) {
			ResourceBundle bundle = ResourceBundle.getBundle("Translations");
			throw new Exception(String.format(bundle.getString("internal.error.request.for.an.invalid.index.d"),index));
		} else {
			return fe_.getElement(index);
		}
	}

	/**
	 * Function permitting an unintelligent outside function (ie. EditFeatures
	 * frame) to set the default values used to popylate the table's entries.
	 * Like getElement, the correct index values are inferred from the
	 * definition.getAttributes() value.
	 * <p>
	 * As a metafeature, recursively calls children to set the feature
	 * requested.
	 *
	 * @param index
	 *            attribute to be set
	 * @param value
	 *            new value of the attribute
	 */
	public void setElement(int index, String value) throws Exception {
		if ((index >= definition.getAttributes().length) || (index < 0)) {
			ResourceBundle bundle = ResourceBundle.getBundle("Translations");
			throw new Exception(String.format(bundle.getString("internal.error.request.for.an.invalid.index"), index));
		} else {
			fe_.setElement(index, value);
		}
	}

	/**
	 * Create an identical copy of this feature. This permits FeatureExtractor
	 * to use the prototype pattern to create new composite features using
	 * metafeatures.
	 */
public Object clone() {
    return prototype();
//		if(fe_ == null){
//			return new Derivative();
//		}else if(fe_ instanceof MetaFeatureFactory){
//			Derivative ret = new Derivative();
//			ret.fe_ = fe_.prototype();
//			ResourceBundle bundle = ResourceBundle.getBundle("Translations");
//			String name = "Derivative of " + ret.fe_.getFeatureDefinition().getName();
//			String description = String.format(bundle.getString("derivative.of.s.s"), ret.fe_.getFeatureDefinition().getName(), ret.fe_.getFeatureDefinition().getDescription());
//			String[] oldAttributes = ret.fe_.getFeatureDefinition().getAttributes();
//			ret.definition = new FeatureDefinition(name, description, true, ret.fe_
//					.getFeatureDefinition().getDimensions(), oldAttributes);
//			ret.definition.setDependency(ret.fe_.getFeatureDefinition().getName(),0,2);
//			return ret;
//		}else{
//			return (new Derivative()).defineFeature((FeatureExtractor)fe_.clone());
//		}
	}
	/**
	 * Overridden to regenerate the feature definition. Perhaps its should be
	 * kept purely virtual, but currently, attributes and dimensions are
	 * recalculated for each iteration. This is necessary so that changes in
	 * children's defintions get propogated back to the top level. As of
	 * 05-08-05 LPC is the only feature that requires this.
	 */
	public FeatureDefinition getFeatureDefinition() {
		if ((fe_ != null)&&(fe_ instanceof MetaFeatureFactory)) {
			String[] oldAttributes = fe_.getFeatureDefinition().getAttributes();
			definition = new FeatureDefinition(definition.getName(), definition.getDescription(), true, fe_
					.getFeatureDefinition().getDimensions(), oldAttributes);
		} else if (fe_ != null) {
			String[] oldAttributes = fe_.getFeatureDefinition().getAttributes();
			definition = new FeatureDefinition(definition.getName(), definition.getDescription(), true, fe_
					.getFeatureDefinition().getDimensions(), oldAttributes);
		} 
		return definition;
	}

}
