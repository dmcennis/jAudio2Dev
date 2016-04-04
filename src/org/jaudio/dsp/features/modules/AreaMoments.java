package org.jaudio.dsp.features.modules;

import org.dynamicfactory.descriptors.*;
import org.dynamicfactory.property.PropertyFactory;
import org.dynamicfactory.propertyQuery.NumericQuery;
import org.dynamicfactory.propertyQuery.PropertyQuery;
import org.dynamicfactory.propertyQuery.PropertyQueryFactory;
import org.jaudio.dsp.features.FeatureDefinition;
import org.jaudio.dsp.features.FeatureExtractor;
import org.jaudio.dsp.features.MetaFeatureFactory;

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
public class AreaMoments extends MetaFeatureFactory {


    FeatureExtractor child;
    @Override
    public FeatureExtractor prototype() {
        return this;
    }

    @Override
    public FeatureExtractor prototype(Properties props) {
        if(quickCheck("Feature",FeatureExtractor.class)){
            AreaMoments m = new AreaMoments();
            m.child = buildChild(props);
            for(Parameter p: this.definition.getParameters()){
                if(props.quickCheck(p.getType(),p.getParameterClass())){
                    m.definition.set(p.getType(),p.getValue());
                }
            }
            return m;
        }else{
            return this;
        }
    }


	/**
	 * Constructor that sets description, dependencies, and offsets from
	 * FeatureExtractor
	 */
	public AreaMoments() {
		super();
		ResourceBundle bundle = ResourceBundle.getBundle("Translations");

		String name = "Area Method of Moments";
		String description = bundle.getString("2d.statistical.method.of.moments");
		int order = 10;
		definition = new FeatureDefinition(name, description, true, 50);
		definition.set("xOrder",Integer.class,order,"Largest degree (deepest statistical moment) of the exponent describing the horizontal direction","");
		PropertyQuery onePositiveInteger = (new NumericQuery()).buildQuery(0.0,false, NumericQuery.Operation.GT);
        SyntaxObject syntax = SyntaxCheckerFactory.newInstance().create(1,1,onePositiveInteger,Integer.class);
		definition.get("xOrder").setRestrictions(syntax);
        definition.set("xOrder",10);
		definition.set("yOrder",Integer.class,order,"Largest degree (deepest statistical moment) of the exponent describing the vertical direction","");
        definition.get("yOrder").set(syntax);
        definition.set("yOrder",5);
		definition.set("WindowLength",Integer.class,100,"");
        definition.get("WindowLength").setRestrictions(SyntaxCheckerFactory.newInstance().create(1,Integer.MAX_VALUE,onePositiveInteger,Integer.class));
		definition.setDependency("Magnitude Spectrum",0,(int)definition.quickGet("WindowLength"));
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
		int xOrder = (int)quickGet("xOrder");
		int yOrder = (int)quickGet("yOrder");
		double[] ret = new double[xOrder*yOrder];
		for (int i = 0; i < other_feature_values.length; ++i) {
			double row = (2.0*((double)i)/((double)(other_feature_values.length))) - 1.0;
			for (int j = 0; j < other_feature_values[i].length; ++j) {
				double column = (2.0*((double)j)/((double)(other_feature_values[0].length)))-1.0;
				double xpow = 1.0;
				for(int x=0;x<xOrder;++x){
					double ypow = 1.0;
					for (int y=0;y<yOrder;++y){
						ret[xOrder*x+y] = other_feature_values[i][j] * xpow * ypow;
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
			set("WindowLength",n);
            definition.setDependency("Magnitude Spectrum",0,n);
		}
	}

}
