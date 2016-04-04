package org.jaudio.dsp.features.modules;

import org.dynamicfactory.descriptors.*;
import org.dynamicfactory.propertyQuery.NumericQuery;
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

//	protected int runningAverage = 100;

	FeatureExtractor child;
	@Override
	public FeatureExtractor prototype() {
		return this;
	}

	@Override
	public FeatureExtractor prototype(Properties props) {
        ParameterInternal param = ParameterFactory.newInstance().create("RunningAverage",Integer.class,"The number of windows to calculate a mean across.");
        param.setLongDescription("");
        param.setRestrictions(SyntaxCheckerFactory.newInstance().create(1,1,(new NumericQuery()).buildQuery(0.0,false, NumericQuery.Operation.GT),Integer.class));
        param.set(100);
        if(quickCheck("Feature",FeatureExtractor.class)){
			Mean m = new Mean();
			m.child = buildChild(props);
            for(Parameter p: this.definition.getParameters()){
                if(props.quickCheck(p.getType(),p.getParameterClass())){
                    m.definition.set(p.getType(),p.getValue());
                }
            }
            definition.add(param);
            return m;
		}else{
            definition.add(param);
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
            set("RunningAverage",n);
			if (fe_ != null) {
				String tmp = fe_.getFeatureDefinition().getName();
                definition.setDependency(tmp,0,(int)quickGet("RunningAverage"));
			}

		}
		super.setWindow(n);
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
