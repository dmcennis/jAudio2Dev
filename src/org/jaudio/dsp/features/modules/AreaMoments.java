package org.jaudio.dsp.features.modules;

import org.dynamicfactory.descriptors.Properties;
import org.jaudio.dsp.features.FeatureDefinition;
import org.jaudio.dsp.features.FeatureExtractor;

import java.util.ResourceBundle;

/**
 * This class implements 2D statistical methods of moments as implemented by
 * Fujinaga (1997). The number of consecutive windows that one can edit across
 * is an editable property. Furthermore, this classes window property is
 * affected by global window size changes.
 * <p>
 * Fujinaga, I. <i>Adaptive Optical Music Recognition</i>. PhD thesis, McGill
 * University, 1997.
 * 
 */
public class AreaMoments extends FeatureExtractor {

	int lengthOfWindow = 10;
	
	int order=10;

	@Override
	public FeatureExtractor prototype() {
		return new AreaMoments();
	}

	@Override
	public FeatureExtractor prototype(Properties props) {
		return prototype();
	}

	/**
	 * Constructor that sets description, dependencies, and offsets from
	 * FeatureExtractor
	 */
	public AreaMoments() {
		ResourceBundle bundle = ResourceBundle.getBundle("Translations");

		String name = "Area Method of Moments";
		String description = bundle.getString("2d.statistical.method.of.moments");
		String[] attributes = new String[] {bundle.getString("area.method.of.moments.window.length"), bundle.getString("area.method.of.moments.max.order") };

		definition = new FeatureDefinition(name, description, true, order*order,
				attributes);
		definition.setDependency("Magnitude Spectrum",0,lengthOfWindow);
	}
	
	/**
	 * Calculates based on windows of magnitude spectrum. Encompasses portion of
	 * Moments class, but has a delay of lengthOfWindow windows before any
	 * results are calculated.
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
		double[] ret = new double[order*order];
		for (int i = 0; i < other_feature_values.length; ++i) {
			double row = (2.0*((double)i)/((double)(other_feature_values.length))) - 1.0;
			for (int j = 0; j < other_feature_values[i].length; ++j) {
				double column = (2.0*((double)j)/((double)(other_feature_values[0].length)))-1.0;
				double xpow = 1.0;
				for(int x=0;x<order;++x){
					double ypow = 1.0;
					for (int y=0;y<order;++y){
						ret[order*x+y] = other_feature_values[i][j] * xpow * ypow;
						ypow *= column;
					}
					xpow *= row;
				}
			}
		}

		return ret;
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
        ResourceBundle bundle = ResourceBundle.getBundle("Translations");
        if (n < 2) {
			throw new Exception(
                    bundle.getString("area.method.of.moment.s.window.length.must.be.two.or.greater"));
		} else {
			lengthOfWindow = n;
            definition.setDependency("Magnitude Spectrum",0,lengthOfWindow);
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
	public String getElement(int index) throws Exception {
		if (index > 1) {
            ResourceBundle bundle = ResourceBundle.getBundle("Translations");
            throw new Exception(String.format(bundle.getString("internal.error.invalid.index.d.sent.to.areamoments.getelement2"),index));
		} else if (index == 1){
			return Integer.toString(order);
		} else{
			return Integer.toString(lengthOfWindow);
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
		if (index > 1) {
            ResourceBundle bundle = ResourceBundle.getBundle("Translations");
            throw new Exception(String.format(bundle.getString("internal.error.invalid.index.d.sent.to.areamoments.setelement2"),index));
		} else if(index == 1){
			try {
				int type = Integer.parseInt(value);
				order = type;
			} catch (Exception e) {
                ResourceBundle bundle = ResourceBundle.getBundle("Translations");
                throw new Exception(
                        bundle.getString("order.of.area.method.of.moments.must.be.an.integer"));
			}
		} else {
			try {
				int type = Integer.parseInt(value);
				setWindow(type);
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
		AreaMoments ret = new AreaMoments();
		ret.lengthOfWindow = lengthOfWindow;
        ret.order = order;
		return ret;
	}

}
