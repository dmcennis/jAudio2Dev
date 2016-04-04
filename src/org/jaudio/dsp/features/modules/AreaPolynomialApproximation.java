package org.jaudio.dsp.features.modules;

import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;
import cern.colt.matrix.linalg.Algebra;
import org.dynamicfactory.descriptors.*;
import org.dynamicfactory.propertyQuery.NumericQuery;
import org.jaudio.dsp.features.FeatureDefinition;
import org.jaudio.dsp.features.FeatureExtractor;
import org.jaudio.dsp.features.MetaFeatureFactory;

import java.util.ResourceBundle;

/**
 * 2D Polynomial Approximation Feature
 *
 * Creates a set of polynomial factors for a 2D polynomial of order k*l where k is the number of
 * terms in the x direction and l is the number of terms in the y direction.  The source is a 
 * square matrix of DSP data of some sort.  (This version is over FFT data.)  The output is a 
 * vector of the coeffecients of the polynomial that best fits this data.  
 * 
 * @author Daniel McEnnis
 */
public class AreaPolynomialApproximation extends MetaFeatureFactory {

    FeatureExtractor child;

    @Override
    public FeatureExtractor prototype() {
        return this;
    }

    @Override
    public FeatureExtractor prototype(Properties props) {
        if(quickCheck("Feature",FeatureExtractor.class)){
            AreaPolynomialApproximation m = new AreaPolynomialApproximation();
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

    @Override
    public MetaFeatureFactory defineFeature(FeatureExtractor fe) {
        return null;
    }

//    int windowLength=50;
	
//	int featureLength=512;
	
//	int k=5;
	
//	int l=20;
	
	DenseDoubleMatrix2D terms;
	
	DenseDoubleMatrix2D z;
	
	/**
	 * Constructor that sets description, dependencies, and offsets from
	 * FeatureExtractor
	 */
	public AreaPolynomialApproximation() {
        ResourceBundle bundle = ResourceBundle.getBundle("Translations");
		String name = "2D Polynomial Approximation";
		String description = bundle.getString("coeffecients.of.2d.polynomial.best.describing.the.input.matrtix");
        definition = new FeatureDefinition(name, description, true, 0);

        ParameterInternal horizontalWindowLength = ParameterFactory.newInstance().create("HorizontalWindowLength",Integer.class,bundle.getString("horizontal.size.window.length"));
        horizontalWindowLength.setLongDescription("");
        horizontalWindowLength.setRestrictions(SyntaxCheckerFactory.newInstance().create(1,1,(new NumericQuery()).buildQuery(0.0,false, NumericQuery.Operation.GT),Integer.class));
        horizontalWindowLength.set(512);
        definition.add(horizontalWindowLength);

        ParameterInternal verticalWindowLength = ParameterFactory.newInstance().create("VerticalWindowLength",Integer.class,bundle.getString("vertical.size.number.of.feature.dimensions"));
        verticalWindowLength.setLongDescription("");
        verticalWindowLength.setRestrictions(SyntaxCheckerFactory.newInstance().create(1,1,(new NumericQuery()).buildQuery(0.0,false, NumericQuery.Operation.GT),Integer.class));
        verticalWindowLength.set(50);
        definition.add(verticalWindowLength);

        ParameterInternal k = ParameterFactory.newInstance().create("HorizontalOrder",Integer.class,bundle.getString("number.of.x.horizontal.terms"));
        k.setLongDescription("");
        k.setRestrictions(SyntaxCheckerFactory.newInstance().create(1,1,(new NumericQuery()).buildQuery(0.0,false, NumericQuery.Operation.GT),Integer.class));
        k.set(20);
        definition.add(k);

        ParameterInternal l = ParameterFactory.newInstance().create("VerticalOrder",Integer.class,bundle.getString("number.of.y.vertical.terms"));
        l.setLongDescription("");
        l.setRestrictions(SyntaxCheckerFactory.newInstance().create(1,1,(new NumericQuery()).buildQuery(0.0,false, NumericQuery.Operation.GT),Integer.class));
        l.set(5);
        definition.add(l);

		definition.setDependency("Magnitude Spectrum",0,(int)quickGet("HorizontalWindowLength"));

		terms = new DenseDoubleMatrix2D((int)quickGet("HorizontalOrder")*(int)quickGet("VerticalOrder"),(int)quickGet("HorizontalWindowLength")*(int)quickGet("VerticalWindowLength"));
		z = new DenseDoubleMatrix2D(1,(int)quickGet("HorizontalWindowLength")*(int)quickGet("VerticalWindowLength"));
		calcTerms(terms);
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
		if(((int)quickGet("") != other_feature_values[0].length)||((int)quickGet("HorizontalWindowLength") != other_feature_values.length)){
			terms = new DenseDoubleMatrix2D((int)quickGet("HorizontalOrder")*(int)quickGet("VerticalOrder"),(int)quickGet("HorizontalWindowLength")*(int)quickGet("VerticalWindowLength"));
			z = new DenseDoubleMatrix2D(1,(int)quickGet("VerticalWindowLength")*(int)quickGet("HorizontalWindowLength"));
			calcTerms(terms);
		}
		for(int i=0;i<(int)quickGet("HorizontalWindowLength");++i){
			for(int j=0;j<(int)quickGet("VerticalWindowLength");++j){
				z.set(0,(int)quickGet("VerticalWindowLength")*i+j,other_feature_values[i][j]);
			}
		}
		DoubleMatrix2D retMatrix = (new Algebra()).solve(terms,z);
		return retMatrix.viewRow(0).toArray();
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
        if (n < 1) {
			throw new Exception(
                    bundle.getString("area.polynomial.approximation.window.length.must.be.positive"));
		} else {
            set("HorizontalWindowLength",n);
			definition.setDependency("Magnitude Spectrum",0,(int)quickGet("HorizontalWindowLength"));
			terms = new DenseDoubleMatrix2D((int)quickGet("HorizontalOrder")*(int)quickGet("VerticalOrder"),(int)quickGet("HorizontalWindowLength")*(int)quickGet("VerticalWindowLength"));
			z = new DenseDoubleMatrix2D(1,(int)quickGet("VerticalWindowLength")*(int)quickGet("HorizontalWindowLength"));
			calcTerms(terms);
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
		switch (index){
			case 0:
				// get windowLength
				return Integer.toString((int)quickGet("HorizontalWindowLength"));
			
			case 1:
				// get featureLength
				return Integer.toString((int)quickGet("VerticalWindowLength"));
				
			case 2:
				// get number of x terms
				return Integer.toString((int)quickGet("HorizontalOrder"));
				
			case 3:
				// get number of y terms
				return Integer.toString((int)quickGet("VerticalOrder"));
				
			default:
				// get number of y terms
                ResourceBundle bundle = ResourceBundle.getBundle("Translations");
                throw new Exception(String.format(bundle.getString("internal.error.invalid.index.d.sent.to.areapolynomialapproximation.getelement"),index));
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
		switch (index){
			case 0:
				// get windowLength
			try {
				int val = Integer.parseInt(value);
				if (val < 1) {
					throw new Exception(
						"Area Polynomial Approximation window length must be positive");
				} else {
                    set("HorizontalWindowLength",val);
					definition.setDependency("Magnitude Spectrum",0,(int)quickGet("HorizontalWindowLength"));
					terms = new DenseDoubleMatrix2D((int)quickGet("HorizontalOrder")*(int)quickGet("VerticalOrder"),(int)quickGet("HorizontalWindowLength")*(int)quickGet("VerticalWindowLength"));
					z = new DenseDoubleMatrix2D(1,(int)quickGet("VerticalWindowLength")*(int)quickGet("HorizontalWindowLength"));
					calcTerms(terms);
				}
			} catch (Exception e) {
				throw new Exception(
						"horizontal (windowLength) of Area Polynomial Approximation must be an integer");
			}
			break;
			
			case 1:
				// get featureLength
			try {
				int val = Integer.parseInt(value);
				if (val < 1) {
					throw new Exception(
						"Area Polynomial Approximation feature dimension length must be positive");
				} else {
                    set("VerticalWindowLength",val);
					terms = new DenseDoubleMatrix2D((int)quickGet("HorizontalOrder")*(int)quickGet("VerticalOrder"),(int)quickGet("HorizontalWindowLength")*(int)quickGet("VerticalWindowLength"));
					z = new DenseDoubleMatrix2D(1,(int)quickGet("VerticalWindowLength")*(int)quickGet("HorizontalWindowLength"));
					calcTerms(terms);
				}
			} catch (Exception e) {
				throw new Exception(
						"vertical (feature dimensions) of Area Polynomial Approximation must be an integer");
			}
			break;
				
			case 2:
				// get number of x terms
			try {
				int val = Integer.parseInt(value);
				if (val < 1) {
					throw new Exception(
						"Number of x terms in Area Polynomial Approximation must be positive");
				} else {
                    set("HorizontalOrder",val);
					terms = new DenseDoubleMatrix2D((int)quickGet("HorizontalOrder")*(int)quickGet("VerticalOrder"),(int)quickGet("HorizontalWindowLength")*(int)quickGet("VerticalWindowLength"));
					z = new DenseDoubleMatrix2D(1,(int)quickGet("VerticalWindowLength")*(int)quickGet("HorizontalWindowLength"));
					calcTerms(terms);
				}
			} catch (Exception e) {
				throw new Exception(
						"Number of x terms in Area Polynomial Approximation must be an integer");
			}
			break;
				
			case 3:
				// get number of y terms
			try {
				int val = Integer.parseInt(value);
				if (val < 1) {
					throw new Exception(
						"Number of y terms in Area Polynomial Approximation must be positive");
				} else {
                    set("VerticalOrder",val);
					terms = new DenseDoubleMatrix2D((int)quickGet("HorizontalOrder")*(int)quickGet("VerticalOrder"),(int)quickGet("HorizontalWindowLength")*(int)quickGet("VerticalWindowLength"));
					z = new DenseDoubleMatrix2D(1,(int)quickGet("VerticalWindowLength")*(int)quickGet("HorizontalWindowLength"));
					calcTerms(terms);
				}
			} catch (Exception e) {
				throw new Exception(
						"Number of y terms of Area Polynomial Approximation must be an integer");
			}
			break;
				
			default:
				throw new Exception("INTERNAL ERROR: invalid index " + index
					+ " sent to AreaPolynomialApproximation:getElement");
		}
	}

	/**
	 * Create an identical copy of this feature. This permits FeatureExtractor
	 * to use the prototype pattern to create new composite features using
	 * metafeatures.
	 */
	public Object clone() {
		AreaPolynomialApproximation ret = new AreaPolynomialApproximation();
		return ret;
	}
	
	private void calcTerms(DoubleMatrix2D terms){
		terms.assign(0.0);
		for(int x=0;x<(int)quickGet("HorizontalWindowLength");++x){
			for(int y=0;y<(int)quickGet("VerticalWindowLength");++y){
				for(int i=0;i<(int)quickGet("HorizontalOrder");++i){
					for(int j=0;j<(int)quickGet("VerticalOrder");++j){
						terms.set((int)quickGet("VerticalOrder")*i+j,(int)quickGet("VerticalWindowLength")*x+y,Math.pow(x,i)*Math.pow(y,j));
					}
				}
			}
		}
	}
	
}
