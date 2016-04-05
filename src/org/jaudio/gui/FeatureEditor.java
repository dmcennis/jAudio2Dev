package org.jaudio.gui;

import org.dynamicfactory.descriptors.Properties;
import org.dynamicfactory.swing.classEditors.AbstractEditor;
import org.dynamicfactory.swing.classEditors.Editor;
import org.jaudio.dsp.features.FeatureExtractor;
import org.jaudio.dsp.features.modules.MFCC;

/**
 * Created by dmcennis on 4/4/2016.
 */
public class FeatureEditor extends AbstractEditor {
    FeatureExtractor fe;

    public FeatureEditor(){
        fe = new MFCC();
    }

    public FeatureEditor(FeatureExtractor f){
        fe = f;
    }
    @Override
    public FeatureEditor prototype() {
        return new FeatureEditor(fe);
    }

    @Override
    public FeatureEditor prototype(Properties props) {
        if(props.quickCheck("Feature",FeatureExtractor.class)){
            return new FeatureEditor((FeatureExtractor)props.quickGet("Feature"));
        }
        return new FeatureEditor(new MFCC());
    }
}
