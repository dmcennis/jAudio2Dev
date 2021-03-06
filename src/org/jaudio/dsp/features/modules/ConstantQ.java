//
//  ConstantQ.java
//  jAudio
//
//  Created by Daniel McEnnis on August 17, 2010.
//  Published under the LGPL license.  See most recent LGPL license on www.fsf.org
//  a copy of this license.
//

package org.jaudio.dsp.features.modules;

import org.dynamicfactory.descriptors.ParameterFactory;
import org.dynamicfactory.descriptors.ParameterInternal;
import org.dynamicfactory.descriptors.Properties;
import org.dynamicfactory.descriptors.SyntaxCheckerFactory;
import org.dynamicfactory.propertyQuery.NumericQuery;
import org.jaudio.dsp.features.FeatureDefinition;
import org.jaudio.dsp.features.FeatureExtractor;
import org.jaudio.dsp.features.MetaFeatureFactory;

import java.util.ResourceBundle;

/*
 * Constant Q
 * 
 * Transform from the time domain to the frequency domain that uses logarithmic bins. This algorithm is derived from an earlier paper
 * from 2004 that has been significantly modified from the original [reference]. The higher bins are now calculated across
 * the entire window, not just the first few elements, and the bins are scaled such that white noise has a perfectly even chroma.
 * These modifications are useful for removing a constantly increasing bin value that was an artifact of bin size and bin length,
 * making the features more useful for chord identification and polyphonic transcription.
 * 
 * @author Daniel McEnnis
 */
public class ConstantQ extends MetaFeatureFactory
{

	int n;
	int nk[];
	int windowLength;
	double[] freq;
	double[][] kernelReal;
	double[][] kernelImaginary;
	boolean calculated;

	@Override
	public FeatureExtractor prototype() {
		return new ConstantQ();
	}

	@Override
	public FeatureExtractor prototype(Properties props) {
		return prototype();
	}

/* CONSTRUCTOR **************************************************************/
	

	/**
	 * Basic constructor that sets the definition and dependencies (and their
	 * offsets) of this feature.
	 */
	public ConstantQ()
	{
		ResourceBundle bundle = ResourceBundle.getBundle("Translations");
		String name = "ConstantQ";
		String description = bundle.getString("signal.to.frequency.transform.using.exponential.spaced.frequency.bins");
		boolean is_sequential = true;
		int dimensions = 0;
		String[] attributes = new String[]{bundle.getString("percent.of.a.semitone.per.bin")};
		definition = new FeatureDefinition( name,
		                                    description,
		                                    is_sequential,
		                                    dimensions,
											attributes );
        ParameterInternal sizeOfBins = ParameterFactory.newInstance().create("SizeOfBins",Double.class,"Percent of a semitone (where 1.0 is 100%) for each bin of the output");
        sizeOfBins.set(1.0);
        sizeOfBins.setStructural(true);
        sizeOfBins.setLongDescription("ConstantQ provides a vector (ordered list of fixed length) numbers. Each number represents a 'bin'. Inside each bin, all frequencies in that range in this windows of the signal are collected into a single number. This parameter describes how 'wide' this 'band' of frequencies is on a logarithimic scale. For example, 12.0 represents bands of all frequencies of one octave, collecting together for instance middle c (c) to the next c (c1) or 261Hz to 522Hz, while 1.0 collects frequencies corresponding to a single semitone like c to d (261 to 294 ~Hz).");
		sizeOfBins.setRestrictions(SyntaxCheckerFactory.newInstance().create(1,1,(new NumericQuery()).buildQuery(0.0,false, NumericQuery.Operation.GT),Double.class));
        definition.add(sizeOfBins);

		calculated = false;
		
	}


	/* PUBLIC METHODS **********************************************************/

	
	/**
	 * Extracts this feature from the given samples at the given sampling
	 * rate and given the other feature values.
	 *
	 * <p>In the case of this feature, the sampling_rate and 
	 * other_feature_values parameters are ignored.
	 *
	 * @param samples				The samples to extract the feature from.
	 * @param sampling_rate			The sampling rate that the samples are
	 *								encoded with.
	 * @param other_feature_values	The values of other features that are
	 *								needed to calculate this value. The
	 *								order and offsets of these features
	 *								must be the same as those returned by
	 *								this class's getDependencies and
	 *								getDependencyOffsets methods respectively.
	 *								The first indice indicates the feature/window
	 *								and the second indicates the value.
	 * @return						The extracted feature value(s).
	 * @throws Exception			Throws an informative exception if
	 *								the feature cannot be calculated.
	 */
	public double[] extractFeature( double[] samples,
	                                double sampling_rate,
	                                double[][] other_feature_values )
		throws Exception
	{
		if(!calculated){
			windowLength = samples.length;
			calcFreq(samples,sampling_rate);
			calcNk(samples);
			calcKernels();
			calculated = true;
		}
		double[] returnValue = new double[nk.length];
		double[] ret = new double[2*nk.length];
		java.util.Arrays.fill(ret,0.0);
		for(int bankCounter=0;bankCounter<(ret.length/2);++bankCounter){
            //FIXED: Should be window length, not nk[bankCounter]
//			for(int i=0;i<nk[bankCounter];++i){
			for(int i=0;i<samples.length;++i){
				ret[bankCounter] += kernelReal[bankCounter][i]*samples[i];
				ret[bankCounter+nk.length] += kernelImaginary[bankCounter][i]*samples[i];
			}
		}
        // FIXED: requires a scaling factor ((passband width ret[0]) / (passband width bin[i]))
		for(int i=0;i<nk.length;++i){
            // Scale results so that pure white noise has a flat chroma
			returnValue[i] = Math.sqrt(ret[i]*ret[i]+ret[i+nk.length]*ret[i+nk.length])/Math.pow(2,((double)i)*((double)quickGet("SizeOfBins"))/12);
		}
		return returnValue;
	}
	
	private void calcFreq(double[] samples, double samplerate){
		double maxFreq = samplerate/2.0;
		double minFreq = samplerate/((double)samples.length);
		double carry = Math.log(maxFreq/minFreq);
		carry /= Math.log(2.0);
		carry *= 12.0/((double)quickGet("SizeOfBins"));
		int numFields = (int)(Math.floor(carry));
		
		freq = new double[numFields];
		double currentFreq = minFreq;
		for(int i=0;i<numFields;++i){
			freq[i]=currentFreq;
			currentFreq = Math.pow(2.0,((double)quickGet("SizeOfBins"))/12.0);
		}
	}
	
	private void calcNk(double[] samples){
		nk = new int[freq.length];
		double windowLength=samples.length;
		for(int i=0;i<nk.length;++i){
			nk[i] = (int)Math.ceil(windowLength/(Math.pow(2.0,((double)i)*((double)quickGet("SizeOfBins"))/12.0)));
		}
	}

    /**
     * FIXED: Restore scaling factors (scaling energy per bin by bin size) and calculation of frequencies over the entire window, not just the first nk[i] elements
     */
	private void calcKernels(){
		kernelReal = new double[nk.length][];
		kernelImaginary = new double[nk.length][];
		double q = Math.pow(2.0,((double)quickGet("SizeOfBins"))/12.0)-1.0;
		double hammingFactor = 25.0/46.0;
		for(int i=0;i<kernelReal.length;++i){
			kernelReal[i] = new double[nk[i]];
			kernelImaginary[i] = new double[nk[i]];
			for(int j=0;j<kernelReal[i].length;++j){
				// Hamming window needs to be adjusted to reflect that this window exceeds 2 Pi in length.
				kernelReal[i][j] = hammingFactor + (1-hammingFactor)*Math.cos(2.0*(((double)nk[i])/((double)windowLength))*Math.PI*((double)j)/((double)nk[i]));
				kernelReal[i][j] /= ((double)nk[i]);
				kernelImaginary[i][j] = kernelReal[i][j];
//				kernelReal[i][j] *= Math.cos(-2.0*Math.PI*q*((double)j)/((double)nk[i]));
//				kernelImaginary[i][j] *= Math.sin(-2.0*Math.PI*q*((double)j)/((double)nk[i]));
				// calculating FFT on a window greater than 2PI in length requires some scaling of the result
				kernelReal[i][j] *= Math.cos(-2.0*Math.PI*(((double)nk[i])/((double)windowLength))*q*((double)j)/((double)nk[i]));
				kernelImaginary[i][j] *= Math.sin(-2.0*Math.PI*(((double)nk[i])/((double)windowLength))*q*((double)j)/((double)nk[i]));
			}
		}
	}
}
