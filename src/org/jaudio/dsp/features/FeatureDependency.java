/**
 * Created by Daniel McEnnis on 3/18/2016
 * <p/>
 * Copyright Daniel McEnnis 2015
 */

package org.jaudio.dsp.features;

import org.dynamicfactory.descriptors.*;

/**
 * Default Description Google Interview Project
 */
public class FeatureDependency{

    String featureName;

    int offset;

    public Properties getParams() {
        return params;
    }

    public void setParams(PropertiesInternal params) {
        this.params = params;
    }

    PropertiesInternal params;

    /**
     * Default constructor for FeatureDependency
     */
    public FeatureDependency() {

    }

    public FeatureDependency(String n){
        featureName = n;
        offset = 0;
        params = PropertiesFactory.newInstance().create();
    }

    public FeatureDependency(String n, int o,Properties p){
        featureName = n;
        offset = o;
        FeatureDefinition d = FeatureFactory.getInstance().create(n).getFeatureDefinition();
        params = PropertiesFactory.newInstance().create();
        for(Parameter param : d.getParameters()){
            if(p.quickCheck(param.getType(),param.getParameterClass())){
                params.add(ParameterFactory.newInstance().create(p));
            }else{
                params.add(param.getType(),param.getParameterClass(),param.getValue());
            }
        }
    }

    public FeatureDependency(String n, int o){
        featureName = n;
        offset = o;
        params = PropertiesFactory.newInstance().create();
    }

    public void setDependency(String name, int offset){
        featureName = name;
        this.offset = offset;
        params = PropertiesFactory.newInstance().create();
    }

    public void setDependency(String name, int offset, PropertiesInternal p){
        featureName = name;
        this.offset = offset;
        params = p;
    }

    public void setDependency(String name){
        setDependency(name,0,PropertiesFactory.newInstance().create());
    }

    public String getFeatureName() {
        return featureName;
    }

    public int getOffset() {
        return offset;
    }

    public FeatureExtractor get(){
        return FeatureFactory.getInstance().create(featureName,params);
    }
}
