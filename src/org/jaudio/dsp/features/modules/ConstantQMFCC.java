//
//  ConstantQ.java
//  jAudio0.4.5.1
//
//  Created by Daniel McEnnis on August 18, 2010.
//  Published under the LGPL license.  See most recent LGPL license on www.fsf.org
//  a copy of this license.
//

package org.jaudio.dsp.features.modules;

import org.dynamicfactory.descriptors.Properties;
import org.dynamicfactory.descriptors.SyntaxCheckerFactory;
import org.dynamicfactory.propertyQuery.NumericQuery;
import org.jaudio.dsp.features.FeatureDefinition;
import org.jaudio.dsp.features.FeatureExtractor;

import java.util.ResourceBundle;

/*
 * Contant Q MFCCs
 *
 * Implements MFCCs using the log constant q function.  Produces MFCC's without the error of 
 * rebinning linear bins to logarithmic bins.
 *
 * @author Daniel McEnnis
 */
public class ConstantQMFCC extends FeatureExtractor
{

	int numCepstra=13;
	@Override
	public FeatureExtractor prototype() {
		return new ConstantQMFCC();
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
	public ConstantQMFCC()
	{
		ResourceBundle bundle = ResourceBundle.getBundle("Translations");
		String name = "ConstantQ derived MFCCs";
		String description = bundle.getString("mfccs.directly.caluclated.from.constantq.exponential.bins");
		boolean is_sequential = true;
		int dimensions = 0;
		String[] attributes = new String[]{bundle.getString("number.of.cepstra.to.return")};
		definition = new FeatureDefinition( name,
		                                    description,
		                                    is_sequential,
		                                    dimensions,
											attributes );
		definition.setDependency("Log of ConstantQ");
		definition.set("NumberOfCepstra",Integer.class,13,description);
		definition.get("NumberOfCepstra").setRestrictions(SyntaxCheckerFactory.newInstance().create(1,1,(new NumericQuery()).buildQuery(0.0,false, NumericQuery.Operation.GT),Integer.class));
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
		return cepCoefficients(other_feature_values[0]);
	}

    /**
	 * Borrowed from Orange Cow MFCC implementation (BSD)
     * Cepstral coefficients are calculated from the output of the Non-linear Transformation method<br>
     * calls: none<br>
     * called by: featureExtraction
     * @param f Output of the Non-linear Transformation method
     * @return Cepstral Coefficients
     */
    public double[] cepCoefficients(double f[]){
        double cepc[] = new double[numCepstra];
        
        for (int i = 0; i < cepc.length; i++){
            for (int j = 1; j <= f.length; j++){
                cepc[i] += f[j - 1] * Math.cos(Math.PI * i / f.length * (j - 0.5));
            }
        }
        
        return cepc;
    }
}
