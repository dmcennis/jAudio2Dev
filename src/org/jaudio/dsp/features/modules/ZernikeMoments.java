package org.jaudio.dsp.features.modules;

import org.dynamicfactory.descriptors.*;
import org.dynamicfactory.propertyQuery.NumericQuery;
import org.jaudio.dsp.features.FeatureDefinition;
import org.jaudio.dsp.features.FeatureExtractor;
import org.jaudio.dsp.features.MetaFeatureFactory;

import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ZernikeMoments extends MetaFeatureFactory {

	FeatureExtractor child;

	@Override
	public FeatureExtractor prototype() {
		return this;
	}

	@Override
	public FeatureExtractor prototype(Properties props) {
		if(quickCheck("Feature",FeatureExtractor.class)){
			ZernikeMoments m = new ZernikeMoments();
			m.child = buildChild(props);
			for(Parameter p: this.definition.getParameters()){
				if(props.quickCheck(p.getType(),p.getParameterClass())){
					m.definition.set(p.getType(),p.getValue());
				}
			}

			return m;
		}else{
			Logger.getLogger(this.getClass().getName()).log(Level.SEVERE,"Attempting to create AutocorrelationHistogram without providing a child feature");
			return null;
		}
	}

	@Override
	public FeatureDefinition getFeatureDefinition() {
		ResourceBundle bundle = ResourceBundle.getBundle("Translations");
		String name = "Zernike Moments";
        String description = bundle.getString("2d.moments.of.matrix.data");
        definition = new FeatureDefinition(name, description, true, 0);

		ParameterInternal param = ParameterFactory.newInstance().create("Order",Integer.class,"Maximum order to calculate");
		param.setLongDescription("");
		param.setRestrictions(SyntaxCheckerFactory.newInstance().create(1,1,(new NumericQuery()).buildQuery(0.0,false, NumericQuery.Operation.GT),Integer.class));
		param.set(7);
        definition.add(param);

		param = ParameterFactory.newInstance().create("WindowLength",Integer.class,bundle.getString("zernike.moments.window.length"));
		param.setLongDescription("");
		param.setRestrictions(SyntaxCheckerFactory.newInstance().create(1,1,(new NumericQuery()).buildQuery(2.0,false, NumericQuery.Operation.GTE),Integer.class));
		param.set(25);
        definition.add(param);
		definition.setDependency("MFCC",0,(int)quickGet("WindowLength"));
		return definition;
	}

    @Override
	public void setWindow(int n) throws Exception {
		if (n < 2) {
			ResourceBundle bundle = ResourceBundle.getBundle("Translations");
			throw new Exception(
					bundle.getString("zernike.moment.s.window.length.must.be.two.or.greater"));
		} else {
            set("WindowLength",n);
            definition.setDependency("MFCC",0,(int)quickGet("WindowLength"));
		}
	}

	@Override
	public double[] extractFeature(double[] samples, double sampling_rate,
			double[][] other_feature_values) throws Exception {
		double[] ret = new double[zernikeCount((int)quickGet("Order"))];
		int index=0;
		double[] powersOfP = new double[8];
		for(int i=0;i<other_feature_values.length;++i){
			for(int j=0;j<other_feature_values[0].length;++j){
				double base = Math.sqrt(Math.pow((2.0*((double)(i/other_feature_values.length)))-1.0,2.0)+Math.pow((2.0*((double)j)/((double)(other_feature_values[0].length)))-1.0,2.0));
				double value=other_feature_values[i][j];
				for(int p=0;p<powersOfP.length;++p){
					powersOfP[p] += value;
					value *= base;
				}
			}
		}
		while((index < ret.length)&&(index<19)){
			switch(index){
				case 0:
					ret[index++] = powersOfP[1];
					break;
				case 1:
					ret[index++] = 2*powersOfP[2]-powersOfP[0];
					break;
				case 2:
					ret[index++] = powersOfP[2];
					break;
				case 3:
					ret[index++] = 3*powersOfP[3]-2*powersOfP[1];
					break;
				case 4:
					ret[index++] = powersOfP[3];
					break;
				case 5:
					ret[index++] = 6*powersOfP[4] - 6*powersOfP[2]+powersOfP[0];
					break;
				case 6:
					ret[index++] = 4*powersOfP[4] - 3*powersOfP[2];
					break;
				case 7:
					ret[index++] = powersOfP[4];
					break;
				case 8:
					ret[index++] = 10*powersOfP[5]-12*powersOfP[3]+3*powersOfP[1];
					break;
				case 9:
					ret[index++] = 5*powersOfP[5]-4*powersOfP[3];
					break;
				case 10:
					ret[index++] = powersOfP[5];
					break;
				case 11:
					ret[index++] = 20*powersOfP[6]-30*powersOfP[4]+12*powersOfP[2]-powersOfP[0];
					break;
				case 12:
					ret[index++] = 15*powersOfP[6]-20*powersOfP[4]+6*powersOfP[2];
					break;
				case 13:
					ret[index++] = 6*powersOfP[6]-5*powersOfP[4];
					break;
				case 14:
					ret[index++] = powersOfP[6];
					break;
				case 15:
					ret[index++] = 35*powersOfP[7]-60*powersOfP[5]+30*powersOfP[3]-4*powersOfP[1];
					break;
				case 16:
					ret[index++] = 21*powersOfP[7]-30*powersOfP[5]+10*powersOfP[3];
					break;
				case 17:
					ret[index++] = 7*powersOfP[7] - 6*powersOfP[5];
					break;
				case 18:
					ret[index++] = powersOfP[7];
					break;
			}
		}
		for(int n=8;n<(int)quickGet("Order");n+=1){
			for(int m=n;m>=0;m-=2){
				ret[index]=0.0;
				for(int k=0;k<(n-m)/2;++k){
					double constant = ((-1*(k%2))*factorial(n-k))/(factorial(k)*factorial(((n+m)/2)-k)*factorial(((n-m)/2)-k));
					for(int i=0;i<other_feature_values.length;++i){
						for(int j=0;j<other_feature_values[0].length;++j){
							double radius = Math.sqrt(Math.pow((2.0*((double)i)/((double)other_feature_values.length))-1.0,2.0)+Math.pow((2.0*((double)j)/((double)(other_feature_values[0].length)))-1.0,2.0));
							ret[index] += constant*Math.pow(radius, n-2*k);
						}
					}
				}
				index++;
			}
		}
		return ret;
	}
	
	public double factorial(int order){
		double ret =1.0;
		for(int i=2;i<order;++i){
			ret *= i;
		}
		return ret;
	}
	
	public int zernikeCount(int order){
		int ret = 0;
		for(int i=1;i<order;i++){
			ret += (i/2) + 1;
		}
		return ret;
	}
}
