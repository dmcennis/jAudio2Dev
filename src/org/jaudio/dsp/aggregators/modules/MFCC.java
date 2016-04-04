/**
 * MFCC aggregator
 * Created for the 2006 ISMIR jAudio release
 * Created by Daniel McEnnis
 */
package org.jaudio.dsp.aggregators.modules;

import jAudioFeatureExtractor.GeneralTools.Statistics;
import org.dynamicfactory.descriptors.Parameter;
import org.dynamicfactory.descriptors.Properties;
import org.jaudio.dsp.aggregators.Aggregator;
import org.jaudio.dsp.aggregators.AggregatorDefinition;
import org.jaudio.dsp.features.FeatureDefinition;
import org.jaudio.dsp.features.FeatureDependency;
import org.jaudio.dsp.features.FeatureExtractor;
import org.oc.ocvolume.dsp.featureExtraction;
import org.oc.ocvolume.dsp.fft;

import java.util.Collection;
import java.util.ResourceBundle;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * MFCC Aggregator
 * 
 * MFCC Aggregator is a general aggregator that produces the fist 10 MFCCs of each feature dimension independently.
 * Treats a signal as a 16 KHz signal, then calculates the MFCC's of this signal.
 *
 * @author Daniel McEnnis
 * 
 */
public class MFCC extends Aggregator {

	featureExtraction fe = new featureExtraction();

	int index = -1;
	
	/**
	 * Constructs a MFCC aggregator
	 */
	public MFCC() {
		ResourceBundle bundle = ResourceBundle.getBundle("Translations");

		metadata = new AggregatorDefinition("MFCC", bundle.getString("treats.the.window.by.window.data.as.a.16khz.signal"),true,null);
	}

	@Override
	public Aggregator prototype() {
		return new MFCC();
	}

	@Override
	public Aggregator prototype(Properties props) {
		MFCC polynomial = new MFCC();
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

        index = ((TreeMap<FeatureExtractor,Integer>)quickGet("FeatureMap")).get((FeatureExtractor)quickGet("Feature"));

		fe.numCepstra = 4;
		int valuesOffset = 0;
		while((valuesOffset<values.length)&&(values[valuesOffset][index]==null)){
			valuesOffset++;
		}
		
		//Handle Degenerate case here
		if(valuesOffset >= values.length){
			result = new double[definition.getDimensions()*4];
			for(int i=0;i<result.length;++i){
				result[i] = 0.0;
			}
			return null;
		}else{
			result = new double[values[values.length-1][index].length*4];
			definition.setDimensions(result.length);
			// get needed power of two array length for FFT.
			int size = Statistics.ensureIsPowerOfN(values.length-valuesOffset,2);
			double[] fftArray = new double[size];		
			java.util.Arrays.fill(fftArray,0.0);
			for(int i=0;i<values[values.length-1][index].length;++i){
				// build the next fft array
				java.util.Arrays.fill(fftArray,0.0);
				for(int fftArrayIndex = 0; fftArrayIndex+valuesOffset < values.length;++fftArrayIndex){
					fftArray[fftArrayIndex]=values[fftArrayIndex+valuesOffset][index][i];
				}
				
				fft data = new fft();
				
		        double magSpectrum[] = new double[fftArray.length];
		        
		        // calculate FFT for current frame
		        fft.computeFFT( fftArray );
		        
		        // calculate magnitude spectrum
		        for (int j = 0; j < fftArray.length; j++){
		            magSpectrum[j] = Math.pow(fft.real[j] * fft.real[j] + fft.imag[j] * fft.imag[j], 0.5);
		        }

				int[] cbin = fe.fftBinIndices(16000,
						magSpectrum.length);
				double[] fbank = fe.melFilter(magSpectrum,
						cbin);
				double[] f = fe.nonLinearTransformation(fbank);
				double[] cepc = fe.cepCoefficients(f);
				for(int j=0;j<cepc.length;++j){
					result[i*4+j] = cepc[j];
				}

			}
			return result;
		}
	}

	@Override
	public Object clone() {
		return new MFCC();
	}

//	@Override
//	public String[] getFeaturesToApply() {
//		return null;
//	}

//	@Override
//	public void init(int[] featureIndecis) throws Exception {
//		index = featureIndecis[0];
//	}

	@Override
	public void setSource(FeatureExtractor feature) {
		ResourceBundle bundle = ResourceBundle.getBundle("Translations");
		FeatureDefinition source = feature.getFeatureDefinition();
		definition = new FeatureDefinition("MFCC: " + source.getName(),
				String.format(bundle.getString("s.nmfcc.of.each.dimension.of.this.feature1"),source.getDescription()),
				source.is_sequential(), source.getDimensions() * 4);

	}

}
