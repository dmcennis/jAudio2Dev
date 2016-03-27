/**
 * AreaPolynomialApproximation
 * created August 21, 2010
 * Author: Daniel McEnnis
 * Published under the LGPL see license.txt or at http://www.fsf.org
 * Utilizes the colt matrix package under either the LGPL or BSD license (see colt's online documentation for specifics).
 */
package org.jaudio.dsp.aggregators;

import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;
import cern.colt.matrix.linalg.Algebra;
import org.dynamicfactory.descriptors.*;
import org.dynamicfactory.propertyQuery.NumericQuery;
import org.jaudio.dsp.features.FeatureDefinition;
import org.jaudio.dsp.features.FeatureDependency;
import org.jaudio.dsp.features.FeatureExtractor;

import java.util.LinkedList;
import java.util.ResourceBundle;

/**
<h2>2D Polynomial Approximation</h2>
<p>This specific aggregator was first released in an August 2010 working paper by Daniel McEnnis.  It transforms
a 2D matrix of signal feature vectors into a set of coeffecients of the polynomial function f(x,y) that bests matches the 
given signal. It is calucalated by constructing a matrix of coeffecients by substituting concrete data points into each
term that coeffecient is attached to (such as x^2*y^3) and a answer matrix is created from the signal at 
that matrix index.  The coeffecients are then calculated by a least squares minimization matrix solver (colt matrix function).</p>
 * <p></p>
 * Utuilizes the Colt java matrix package.
 * @author Daniel McEnnis
 *
 */
public class AreaPolynomialApproximation extends Aggregator {

	int xDim=20;
	int yDim=5;	
	
	int windowLength=1;
	int featureLength=1;
	
	DenseDoubleMatrix2D terms;
	
	DenseDoubleMatrix2D z;

	String[] featureNames = null;
	int[] featureNameIndecis = null;
	
	public AreaPolynomialApproximation(){
		LinkedList<Parameter> list = new LinkedList<Parameter>();

		ParameterInternal x = ParameterFactory.newInstance().create();
		LinkedList values = new LinkedList<Integer>();
		values.add(20);
		x.set("xDimension",Integer.class,true,values,"The largest exponent of the X variable in the approximating polynomial","");
		x.setRestrictions(SyntaxCheckerFactory.newInstance().create(1,1,(new NumericQuery()).buildQuery(0.0,false, NumericQuery.Operation.GT),Integer.class));
		list.add(x);

		ParameterInternal y = ParameterFactory.newInstance().create();
		values = new LinkedList<Integer>();
		values.add(5);
		y.set("yDimension",Integer.class,true,values,"The largest exponent of the Y variable in the approximating polynomial","");
		y.setRestrictions(SyntaxCheckerFactory.newInstance().create(1,1,(new NumericQuery()).buildQuery(0.0,false, NumericQuery.Operation.GT),Integer.class));
		list.add(y);

        ResourceBundle bundle = ResourceBundle.getBundle("Translations");
        metadata = new AggregatorDefinition("2D Polynomial Approximation of a signal", bundle.getString("calculates.the.coefficients.of.a.polynomial.that.approximates.the.signal"),false,list);
	}

	@Override
	public Aggregator prototype() {
		return new AreaPolynomialApproximation();
	}

	@Override
	public Aggregator prototype(Properties props) {
        AreaPolynomialApproximation polynomial = new AreaPolynomialApproximation();
        if(props.quickCheck("Dependency", FeatureDependency.class)){
            polynomial.setSource((FeatureExtractor)props.quickGet("Dependency"));
        }
        return polynomial;
	}

	@Override
	public void aggregate(double[][][] values) {
		result = null;
		int offset = super.calculateOffset(values,featureNameIndecis);
		int[][] featureIndecis = super.collapseFeatures(values,featureNameIndecis);
		result[0] = 0.0;
		windowLength = featureNameIndecis.length-offset;
		featureLength = featureIndecis[0].length;
		for (int i=offset;i<values.length;++i){
			for(int j=0;j<featureIndecis.length;++j){
				result[0] += values[i][featureIndecis[j][0]][featureIndecis[j][1]];
			}
		}		
		terms = new DenseDoubleMatrix2D(xDim*yDim,windowLength*featureLength);
		z = new DenseDoubleMatrix2D(1,featureLength);
		calcTerms(terms);
		result = ((new Algebra()).solve(terms,z)).viewRow(0).toArray();
	}

	@Override
	public Object clone() {
		AreaPolynomialApproximation ret = new AreaPolynomialApproximation();
		if(featureNames != null){
			ret.featureNames = featureNames.clone();
		}
		if(featureNameIndecis != null){
			ret.featureNameIndecis = featureNameIndecis.clone();
		}
		return ret;
	}

	@Override
	public FeatureDefinition getFeatureDefinition() {
		return definition;
	}

//	@Override
//	public String[] getFeaturesToApply() {
//		return featureNames;
//	}

	@Override
	public void init(int[] featureIndecis) throws Exception {
		if(featureIndecis.length != featureNames.length){
            ResourceBundle bundle = ResourceBundle.getBundle("Translations");
            throw new Exception(bundle.getString("internal.error.agggregator.areapolynomialapproximation.number.of.feature.indeci.does.not.match.number.of.features"));
		}
		this.featureNameIndecis = featureIndecis;
	}

	/* (non-Javadoc)
	 * @see jAudioFeatureExtractor.Aggregators.Aggregator#getParamaters()
	 */
//	@Override
//	public String[] getParamaters() {
//		return new String[]{Integer.toString(xDim),Integer.toString(yDim)};
//	}
	
	private void calcTerms(DoubleMatrix2D terms){
		terms.assign(0.0);
		for(int x=0;x<windowLength;++x){
			for(int y=0;y<featureLength;++y){
				for(int i=0;i<xDim;++i){
					for(int j=0;j<yDim;++j){
						terms.set(yDim*i+j,featureLength*x+y,Math.pow(x,i)*Math.pow(y,j));
					}
				}
			}
		}
	}

	@Override
	public void setParameters(String[] featureNames, String[] params) throws Exception {
        ResourceBundle bundle = ResourceBundle.getBundle("Translations");
        // get number of x terms
			if (params.length != 2){
				xDim = 20;
				yDim = 5;
			}else{
				try {
					int val = Integer.parseInt(params[0]);
					if (val < 1) {

						throw new Exception(
                                bundle.getString("number.of.x.terms.in.area.polynomial.approximation.must.be.positive"));
					} else {
						xDim = val;
					}
				} catch (Exception e) {
					throw new Exception(
                            bundle.getString("number.of.x.terms.in.area.polynomial.approximation.must.be.an.integer"));
				}
				
					// get number of y terms
				try {
					int val = Integer.parseInt(params[1]);
					if (val < 1) {
						throw new Exception(
                                bundle.getString("number.of.y.terms.in.area.polynomial.approximation.must.be.positive"));
					} else {
						yDim = val;
					}
				} catch (Exception e) {
					throw new Exception(
                            bundle.getString("number.of.y.terms.of.area.polynomial.approximation.must.be.an.integer"));
				}
			}
		this.featureNames = featureNames;
		String names = featureNames[0];
		for(int i=1;i<featureNames.length;++i){
			names += " " + featureNames[i];
		}
		definition = new FeatureDefinition("2D Polynomial Approximation: "+names,String.format(bundle.getString("2d.moments.constructed.from.features.s"),names),true,0);
	}
}
