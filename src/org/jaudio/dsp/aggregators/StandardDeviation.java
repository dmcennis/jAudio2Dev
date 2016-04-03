/**
 * Standard Deviation
 * 
 * Created by Daniel McEnnis for the 2006 jAudio release
 * 
 */
package org.jaudio.dsp.aggregators;


import org.dynamicfactory.descriptors.Parameter;
import org.dynamicfactory.descriptors.Properties;
import org.jaudio.dsp.features.FeatureDefinition;
import org.jaudio.dsp.features.FeatureDependency;
import org.jaudio.dsp.features.FeatureExtractor;

import java.util.Collection;
import java.util.ResourceBundle;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
<h2>Standard Deviation</h2>
<p>This generic aggregator calculates standard deviation of each feature dimension independently.  It is one of the
original aggregators used in MIR research, present in the original Marsyas (2000) by Tzanetakis and Cook.</p>
 * @author Daniel McEnnis
 * 
 */
public class StandardDeviation extends Aggregator {

	int feature = -1;

	/**
	 * Constructs a new standard deviation aggregator.
	 */
	public StandardDeviation(){
		ResourceBundle bundle = ResourceBundle.getBundle("Translations");
		metadata = new AggregatorDefinition("Standard Deviation", bundle.getString("standard.deviation.of.the.window.by.window.data"),true,null);
	}

	@Override
	public Aggregator prototype() {
		return new StandardDeviation();
	}

	@Override
	public Aggregator prototype(Properties props) {

		StandardDeviation polynomial = new StandardDeviation();
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
		if(!quickCheck("FeatureMap", TreeMap.class)){
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE,"INTERNAL: aggregate() called before the aggregator was initialized with a map from features to their indeci");
			return null;
		}
		if(!quickCheck("Feature", FeatureExtractor.class)){
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE,"aggregate() called before the aggregator was initialized with a feature");
			return null;
		}
		feature = ((TreeMap<FeatureExtractor,Integer>)quickGet("FeatureMap")).get((FeatureExtractor)quickGet("Feature"));
		if (values[values.length-1][feature] == null) {
			definition.setDimensions(1);
			result = new double[] { 0.0 };
		} else {
			int max = values[values.length - 1][feature].length;
			definition.setDimensions(max);
			result = new double[max];
			for (int i = 0; i < max; ++i) {
				int count = 0;
				double average = 0.0;
				;
				for (int j = 0; j < values.length; ++j) {
					if ((values[j][feature] != null)
							&& (values[j][feature].length > i)) {
						average += values[j][feature][i];
						count++;
					}
				}
				if (count < 2) {
					result[i] = 0.0;
				} else {
					average /= ((double) count);
					for (int j = 0; j < values.length; ++j) {
						if ((values[j][feature] != null)
								&& (values[j][feature].length > i)) {
							result[i] += Math.pow(values[j][feature][i]
									- average, 2.0);
						}
					}
					result[i] = Math.sqrt(result[i] / (((double) count) - 1.0));
				}
			}
		}
		return result;
	}

	@Override
	public Object clone() {
		return new StandardDeviation();
	}

	@Override
	public FeatureDefinition getFeatureDefinition() {
		return definition;
	}

//	@Override
//	public String[] getFeaturesToApply() {
//		return null;
//	}

	@Override
	public void setSource(FeatureExtractor feature) {
		ResourceBundle bundle = ResourceBundle.getBundle("Translations");
		FeatureDefinition this_def = feature.getFeatureDefinition();
		definition = new FeatureDefinition(this_def.getName()
				+ " Overall Standard Deviation", String.format(bundle.getString("s.nthis.is.the.overall.standard.deviation.over.all.windows2"),this_def.getDescription()),
				this_def.is_sequential(), this_def.getDimensions());
	}

}
