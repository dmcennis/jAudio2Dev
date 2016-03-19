package org.jaudio.dsp;

import org.dynamicfactory.AbstractFactory;
import org.dynamicfactory.descriptors.Properties;

import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Daniel McEnnis on 3/19/2016
 * <p/>
 * Copyright Daniel McEnnis 2015
 */
public class AudioInputFactory extends AbstractFactory<AudioInput> {
    private static AudioInputFactory ourInstance = new AudioInputFactory();

    public static AudioInputFactory getInstance() {
        return ourInstance;
    }

    private AudioInputFactory() {
        map.put("Default",new AudioSamples());
    }

    @Override
    public AudioSamples create(Properties props) {
        if(props.quickCheck("ClassName",String.class)){
            if(map.containsKey((String)props.quickGet("ClassName"))){
                return map.get("ClassName").prototype(props);
            }else{
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE,String.format("Desired class %s not available, replaced with AudioSamples",props.quickGet("ClassName")));
                return map.get("Default").prototype(props);
            }
        }else{
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE,"ClassName parameter missing the desired className - replacing with AudioSamples");
            return map.get("Default").prototype(props);
        }
    }

    static public AudioInput create(String id, double[] data, double[][] channels){
        return create(id,data,channels,getInstance().properties);
    }
    static public AudioInput create(String id, double[] data, double[][] channels,Properties props){
        return getInstance().map.get("Default").prototype(props);
    }

//    @Override
//    public Collection<String> getKnownTypes() {
//
//    }
}
