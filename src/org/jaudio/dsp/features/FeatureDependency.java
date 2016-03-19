/**
 * Created by Daniel McEnnis on 3/18/2016
 * <p/>
 * Copyright Daniel McEnnis 2015
 */

package org.jaudio.dsp.features;

/**
 * Default Description Google Interview Project
 */
public class FeatureDependency{

    String featureName;

    int offset;

    /**
     * Default constructor for FeatureDependency
     */
    public FeatureDependency() {

    }

    public FeatureDependency(String n, int o){
        featureName = n;
        offset = o;
    }

    public void setDependency(String name, int offset){
        featureName = name;
        this.offset = offset;
    }

    public void setDependency(String name){
        setDependency(name,0);
    }

    public String getFeatureName() {
        return featureName;
    }

    public int getOffset() {
        return offset;
    }
}
