package org.jaudio.dsp.features.modules;

import org.dynamicfactory.descriptors.Properties;
import org.jaudio.dsp.features.FeatureDefinition;
import org.jaudio.dsp.features.FeatureExtractor;
import org.jaudio.dsp.features.MetaFeatureFactory;

import java.util.ResourceBundle;

/**
 * Calculates the running mean of an underlying feature.
 * 
 * @author Daniel McEnnis
 */
public class Mean extends MetaFeatureFactory {

	protected int runningAverage = 100;

	FeatureExtractor child;
	@Override
	public FeatureExtractor prototype() {
		return this;
	}

	@Override
	public FeatureExtractor prototype(Properties props) {
		if(quickCheck("Feature",FeatureExtractor.class)){
			Mean m = new Mean();
			m.child = buildChild(props);
			return m;
		}else{
			return this;
		}
	}


	/**
	 * Basic constructor that initializes the metafeautres values properly for
	 * use as a factory.
	 */
	public Mean() {
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
	public Mean(MetaFeatureFactory mff) {
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
		Mean tmp = new Mean();
		if ((fe_ != null) & (fe_ instanceof MetaFeatureFactory)) {
			tmp.fe_ = ((MetaFeatureFactory) fe_).defineFeature(fe);
		} else {
			tmp.fe_ = fe;
		}
		ResourceBundle bundle = ResourceBundle.getBundle("Translations");
			String name = "Running Mean of " + fe.getFeatureDefinition().getName();
			String description = String.format(bundle.getString("running.mean.of.s.s"),fe.getFeatureDefinition().getName(),fe.getFeatureDefinition().getDescription());

			String[] oldAttributes = fe.getFeatureDefinition().getAttributes();
			String[] myAttributes = new String[oldAttributes.length + 1];
			for (int i = 0; i < oldAttributes.length; ++i) {
				myAttributes[i] = oldAttributes[i];
			}
			myAttributes[myAttributes.length - 1] = bundle.getString("size.of.window.to.average.accross");

			tmp.definition = new FeatureDefinition(name, description, true, fe
					.getFeatureDefinition().getDimensions(), myAttributes);
            tmp.definition.setDependency(getDependencies());
			return tmp;
	}

	/**
	 * Calculates the mean over last 100 windows
	 *
	 * @param samples
	 *            signal being processed
	 * @param sampling_rate
	 *            sample rate of the signal
	 * @param other_feature_values
	 *            dependancies of the current signal
	 * @return mean over last 100 values of dependant feature.
	 */
	public double[] extractFeature(double[] samples, double sampling_rate,
			double[][] other_feature_values) throws Exception {
		double[] ret = new double[other_feature_values[0].length];
		for (int i = 0; i < ret.length; ++i) {
			for (int j = 0; j < other_feature_values.length; ++j) {
				ret[i] += other_feature_values[j][i];
			}
			ret[i] /= other_feature_values.length;
		}
		return ret;
	}

	/**
	 * Changes the number of dependant samples extracted for each object.
	 *
	 * @param n
	 *            number of samples that should be included in the running
	 *            average.
	 * @throws Exception
	 *             thrown if n is equal to or less than one
	 */
	public void setWindow(int n) throws Exception {
		if (n <= 1) {
			ResourceBundle bundle = ResourceBundle.getBundle("Translations");
			throw new Exception(
					bundle.getString("new.value.for.running.average.must.be.greater.than.one"));
		} else {
			runningAverage = n;
			if (fe_ != null) {
				String tmp = fe_.getFeatureDefinition().getName();
                definition.setDependency(tmp,0,runningAverage);
			}

		}
		super.setWindow(n);
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
		ResourceBundle bundle = ResourceBundle.getBundle("Translations");
		if ((index >= definition.getAttributes().length) || (index < 0)) {
			throw new Exception(String.format(bundle.getString("internal.error.request.for.an.invalid.index.d3"),index));
		} else if (index == definition.getAttributes().length - 1) {
			return Integer.toString(runningAverage);
		} else if (fe_ != null) {
			return fe_.getElement(index);
		} else {
			throw new Exception(bundle.getString("internal.error.non.existant.index.for.mean.getelement.claims.to.have.children.but.child.is.null"));
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
			throw new Exception(String.format(bundle.getString("internal.error.request.for.an.invalid.index.d4"),index));
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
			throw new Exception(bundle.getString("internal.error.non.existant.index.for.mean.getelement.claims.to.have.children.but.child.is.null1"));
		}
	}

	/**
	 * Create an identical copy of this feature. This permits FeatureExtractor
	 * to use the prototype pattern to create new composite features using
	 * metafeatures.
	 */
	public Object clone() {
		if(fe_ == null){
			return new Mean();
		}
		if (this.fe_ instanceof MetaFeatureFactory) {
			Mean ret = new Mean();
			ret.fe_ = (FeatureExtractor)fe_.clone();
			try {
				ret.setWindow(runningAverage);
			} catch (Exception e) {
				e.printStackTrace();
			}
			String name = definition.getName();
			String description = definition.getDescription();
			String[] attributes = definition.getAttributes();
			int dim = definition.getDimensions();
			ret.definition = new FeatureDefinition(name,description,true,dim,attributes);
			ret.definition.setDependency(definition.getDependency());

			try{
				ret.setWindow(runningAverage);
			}catch(Exception e){
				e.printStackTrace();
			}
			return ret;
		} else {
			Mean ret = (Mean)defineFeature((FeatureExtractor) fe_.clone());
			try {
				ret.setWindow(runningAverage);
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
		FeatureDefinition childFD=null;
		if(fe_ != null){
			childFD = fe_.getFeatureDefinition();
		}else{
			return definition;
		}
		attributes = new String[childFD.getAttributes().length + 1];
		for(int i=0;i<childFD.getAttributes().length;++i){
			attributes[i] = childFD.getAttributes()[i];
		}
		ResourceBundle bundle = ResourceBundle.getBundle("Translations");
		attributes[attributes.length-1] = bundle.getString("size.of.window.to.average.accross");
		dimensions = childFD.getDimensions();
		definition = new FeatureDefinition(name,description,true,dimensions,attributes);
		return definition;
	}

}
