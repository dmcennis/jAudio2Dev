/**
 * Area Moments Aggregator
 *
 * Created by Daniel McEnnis for ISMIR 2006 jAUdio release.
 * Published under the LGPL see license.txt or at http://www.fsf.org
 */
package org.jaudio.dsp.aggregators;

import org.dynamicfactory.descriptors.BasicParameter;
import org.dynamicfactory.descriptors.Parameter;
import org.dynamicfactory.descriptors.Properties;
import org.jaudio.dsp.features.FeatureDefinition;
import org.jaudio.dsp.features.FeatureDependency;
import org.jaudio.dsp.features.FeatureExtractor;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <h2>Area Method of Moments Aggregator</h2>
 * <p></p>
 * <h3>Algorithm Description</h3>
 * <p>This specific aggregator was originally intended to be the first 10 statistical moments of a 2D area. 
 * This algorithm was first used in graphic machine learning by Fujinaga in 1998.  Its first use in digital
 * signal processing is in McEnnis and Fujinaga 2005.
 * <p>It is a specific feature as the effectiveness of the resulting features is heavily dependent on the importance of the feature ordering.
 * </p>
 * <h3>Algorithm History</h3>
 * <p>The algorithm treats the image as a 2D function f(x,y) = z where x and y are indecis of the underlying matrix.
 * The order of x and y is increased together from order 0 to order 3, caluclated with a coeefcient calculated by the binomial 
 * of the x and y order.</p>
 * <p>The original DSP version is a collaborative effort between the author of the code and Ichiro Fujinaga.
 * <p>Fujinaga, I. Adaptive Optical Music Recognition. PhD thesis, McGill University, 1997. </p>
 * <p></p>
 * <p>Code utilizes the Colt matrix package available under either LGPL or BSD license.  See Colt's online documentation for more details.
 * 
 * @author Daniel McEnnis
 *
 */
public class AreaMoments extends Aggregator {

//	String[] featureNames = null;
//	int[] featureNameIndecis = null;

    int order = 7;
	
	/**
	 * Constructs an AreaMoments aggregator.  This isn't valid until specific features are adde to the system (in a particular order).
	 */
	public AreaMoments(){
        ResourceBundle bundle = ResourceBundle.getBundle("Translations");
        BasicParameter param = new BasicParameter("OrderLength",Integer.class);
        param.setDescription(bundle.getString("maximum.order.length.is.order.2.of.2d.statistical.moments.to.calculate"));
        LinkedList<Parameter> list = new LinkedList<Parameter>();
        list.add(param);
        metadata = new AggregatorDefinition("Area Moments", bundle.getString("calculates.2d.statistical.moments.for.the.given.features"),false,
                list);
	}

	@Override
	public Aggregator prototype() {
		return new AreaMoments();
	}

	@Override
	public Aggregator prototype(Properties props) {
		AreaMoments polynomial = new AreaMoments();
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
		if(!quickCheck("Feature", FeatureExtractor.class)){
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE,"INTERNAL: aggregate() called before the aggregator was initialized with features to calculate over");
            return null;
        }
		result = new double[order*order];
		java.util.Arrays.fill(result,0.0);
		int offset = super.calculateOffset(values,(List<FeatureExtractor>)get("Feature").getValue());
		int[][] featureIndecis = super.collapseFeatures(values,(List<FeatureExtractor>)get("Feature").getValue());
        for (int i=offset; i < values.length; ++i) {
            double row = (2.0*((double)(i-offset))/((double)(values.length - offset))) - 1.0;
            for (int j = 0; j < featureIndecis.length; ++j) {
                double column = (2.0*((double)j)/((double)(featureIndecis.length)))-1.0;
                double xpow = 1.0;
                for(int x=0;x<order;++x){
                    double ypow = 1.0;
                    for (int y=0;y<order;++y){
                        result[order*x+y] += values[i][featureIndecis[j][0]][featureIndecis[j][1]] * xpow * ypow;
                        ypow *= column;
                    }
                    xpow *= row;
                }
            }
        }
        return result;
	}

//	@Override
//	public Object clone() {
//		AreaMoments ret = new AreaMoments();
//		if(featureNames != null){
//			ret.featureNames = featureNames.clone();
//		}
//		if(featureNameIndecis != null){
//			ret.featureNameIndecis = featureNameIndecis.clone();
//		}
//		return new AreaMoments();
//	}

	@Override
	public FeatureDefinition getFeatureDefinition() {
		return definition;
	}

//	@Override
//	public String[] getFeaturesToApply() {
//		return featureNames;
//	}

//	@Override
//	public void init(int[] featureIndecis) throws Exception {
//		if(featureIndecis.length != featureNames.length){
//            ResourceBundle bundle = ResourceBundle.getBundle("Translations");
//
//            throw new Exception(bundle.getString("internal.error.agggregator.areamoments.number.of.feature.indeci.does.not.match.number.of.features1"));
//		}
//		this.featureNameIndecis = featureIndecis;
//	}

//	@Override
//	public void setParameters(String[] featureNames, String[] params) throws Exception {
//		this.featureNames = featureNames;
//		String names = featureNames[0];
//		for(int i=1;i<featureNames.length;++i){
//			names += " " + featureNames[i];
//		}
//        if((params != null) && (params.length > 0)){
//            order = Integer.parseInt(params[0]);
//        }
//        ResourceBundle bundle = ResourceBundle.getBundle("Translations");
//
//        definition = new FeatureDefinition("Area Moments: "+names,String.format(bundle.getString("2d.moments.constructed.from.features.s"),names),true,order*order);
//	}

    /**
     * Provide a list of the values of all parameters this aggregator uses.
     * Aggregators without parameters return null.
     *
     * @return list of the values of parmeters or null.
     */
//    @Override
//    public String[] getParamaters() {
//        return new String[]{Integer.toString(order)};   //To change body of overridden methods use File | Settings | File Templates.
//    }


}
