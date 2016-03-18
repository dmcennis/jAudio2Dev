package org.jaudio.dsp.features;

import org.dynamicfactory.AbstractFactory;
import org.dynamicfactory.descriptors.Properties;
import org.jaudio.dsp.features.modules.*;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Daniel McEnnis on 3/15/2016
 * <p/>
 * Copyright Daniel McEnnis 2015
 */
public class FeatureFactory extends AbstractFactory<FeatureExtractor> {
    private static FeatureFactory ourInstance = new FeatureFactory();

    public static FeatureFactory getInstance() {
        return ourInstance;
    }

    private FeatureFactory() {
        map.put("AreaMoments",new AreaMoments());
        map.put("BeatHistogram", new BeatHistogram());
        map.put("BeatHistogramLabels", new BeatHistogramLabels());
        map.put("BeatSum", new BeatSum());
        map.put("Compactness", new Compactness());
        map.put("FFTBinFrequencies", new FFTBinFrequencies());
        map.put("FractionOfLowEnergyWindows", new FractionOfLowEnergyWindows());
        map.put("HarmonicSpectralCentroid", new HarmonicSpectralCentroid());
        map.put("HarmonicSpectralFlux", new HarmonicSpectralFlux());
        map.put("HarmonicSpectralSmoothness", new HarmonicSpectralSmoothness());
        map.put("LPC", new LPC());
        map.put("MagnitudeSpectrum", new MagnitudeSpectrum());
        map.put("MFCC", new MFCC());
        map.put("Moments", new Moments());
        map.put("PeakFinder", new PeakFinder());
        map.put("PowerSpectrum", new PowerSpectrum());
        map.put("RelativeDifferenceFunction", new RelativeDifferenceFunction());
        map.put("RMS", new RMS());
        map.put("SpectralCentroid", new SpectralCentroid());
        map.put("SpectralFlux", new SpectralFlux());
        map.put("SpectralRolloffPoint", new SpectralRolloffPoint());
        map.put("SpectralVariability", new SpectralVariability());
        map.put("StrengthOfStrongestBeat", new StrengthOfStrongestBeat());
        map.put("StrongestBeat", new StrongestBeat());
        map.put("StrongestFrequencyVariability", new StrongestFrequencyVariability());
        map.put("StrongestFrequencyViaFFTMax", new StrongestFrequencyViaFFTMax());
        map.put("StrongestFrequencyViaSpectralCentroid", new StrongestFrequencyViaSpectralCentroid());
        map.put("StrongestFrequencyViaZeroCrossings", new StrongestFrequencyViaZeroCrossings());
        map.put("ZeroCrossings", new ZeroCrossings());
    }

    @Override
    public FeatureExtractor create(Properties props) {

        if(props == null){
            Logger.getLogger("org.jadio.dsp.feature.FeatureFactory").log(Level.WARNING,"no feature is specified and no good default for features exist");
            return map.get("MFCC").prototype();
        }

        if(!props.quickCheck("ClassName",String.class)){
            Logger.getLogger("org.jadio.dsp.feature.FeatureFactory").log(Level.WARNING,"no feature is specified and no good default for features exist");
            return map.get("MFCC").prototype();
        }

        String name = (String)props.quickGet("ClassName");

        if(map.containsKey(props.quickGet("ClassName"))) {
            return map.get("ClassName").prototype();
        }else{
            Logger.getLogger("org.jadio.dsp.feature.FeatureFactory").log(Level.WARNING,String.format("Feature %s is not a known feature type",name));
            return map.get("MFCC").prototype();
        }
    }
}
