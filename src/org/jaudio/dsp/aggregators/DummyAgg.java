/**
 * DummyAgg 
 * Created for the ISMIR 2006 jAudio release
 * stub aggregator for testing purposes 
 */
package org.jaudio.dsp.aggregators;

import org.dynamicfactory.descriptors.Parameter;
import org.dynamicfactory.descriptors.Properties;
import org.jaudio.dsp.features.FeatureDefinition;
import org.jaudio.dsp.features.FeatureDependency;
import org.jaudio.dsp.features.FeatureExtractor;

import java.io.DataOutputStream;
import java.util.Collection;

/**
 * <h2>DummyAgg</h2>
 * Dummy aggregator for testing aggregator loading.
 * 
 * @author Daniel McEnnis
 * 
 */
public class DummyAgg extends Aggregator {

	public int[] featureIndex = null;

	public FeatureExtractor[] featureName = null;
	
	public FeatureExtractor[] presetFeature = null;

	public FeatureDefinition definition = null;
	
	public double[][][] data = null; 

	public DummyAgg(){
		presetFeature = null;
	}
	
	public DummyAgg(FeatureExtractor[] f){
		presetFeature = f;
	}

	@Override
	public Aggregator prototype() {
		return new DummyAgg();
	}

	@Override
	public Aggregator prototype(Properties props) {

		DummyAgg polynomial = new DummyAgg();
		if(props.quickCheck("Dependency", FeatureDependency.class)){
			for(FeatureExtractor fe : (Collection<FeatureExtractor>)props.get("Dependency").getValue()){
				polynomial.addSource(fe);
			}
			for(Parameter p: this.definition.getParameters()){
				if(props.quickCheck(p.getType(),p.getParameterClass())){
					polynomial.definition.set(p.getType(),p.getValue());
				}
			}
		}
		return polynomial;

	}

	@Override
	public double[] aggregate(double[][][] values) {
		data = values;
        if((values.length>0)&&(values[0].length>0)){
            return values[0][0];
        }
		return null;
	}

	@Override
	public Object clone() {
		return new DummyAgg();
	}

	@Override
	public FeatureDefinition getFeatureDefinition() {
		return definition;
	}

//	@Override
//	public void init(int[] featureIndecis)
//			throws Exception {
//		featureIndex = featureIndecis;
//	}

	@Override
	public void setSource(FeatureExtractor feature) {
		definition = new FeatureDefinition(
				feature.getFeatureDefinition().getName() + "/DUMMY", feature
						.getFeatureDefinition().getName()
						+ "/DUMMY",
				feature.getFeatureDefinition().is_sequential(), feature
						.getFeatureDefinition().getDimensions());
	}

//	@Override
//	public String[] getFeaturesToApply() {
//		String[] ret = new String[presetFeature.length];
//		for(int i=0;i<ret.length;++i){
//			ret[i] = presetFeature[i].getFeatureDefinition().getName();
//		}
//		return ret;
//	}

	@Override
	public void outputACEFeatureKeyEntries(DataOutputStream output)
			throws Exception {
		// TODO Auto-generated method stub
		super.outputACEFeatureKeyEntries(output);
	}

	@Override
	public void outputACEValueEntries(DataOutputStream output) throws Exception {
		// TODO Auto-generated method stub
		super.outputACEValueEntries(output);
	}

	@Override
	public void outputARFFHeaderEntries(DataOutputStream output) throws Exception {
		// TODO Auto-generated method stub
		super.outputARFFHeaderEntries(output);
	}

	@Override
	public void outputARFFValueEntries(DataOutputStream output) throws Exception {
		// TODO Auto-generated method stub
		super.outputARFFValueEntries(output);
	}

}
