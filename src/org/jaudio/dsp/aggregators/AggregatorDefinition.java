/**
 * Created by Daniel McEnnis on 3/19/2016
 * <p/>
 * Copyright Daniel McEnnis 2015
 */

package org.jaudio.dsp.aggregators;

import org.dynamicfactory.descriptors.Parameter;
import org.dynamicfactory.descriptors.ParameterInternal;
import org.dynamicfactory.descriptors.PropertiesImplementation;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * Default Description Google Interview Project
 */
public class AggregatorDefinition extends PropertiesImplementation implements Serializable {


    public String getDescription() {
        if(this.quickCheck("Description",String.class)){
            return (String)quickGet("Description");
        }
        return "Default Description";
    }

    public void setDescription(String description) {
        set("Description",description);
        get("Description").setDescription("Description of the aggregators function");
    }

    public boolean isGeneric() {
        if(quickCheck("Generic",Boolean.class)){
            return ((Boolean)quickGet("Generic")).booleanValue();
        }else{
            return true;
        }
    }

    public void setGeneric(boolean generic) {
        set("Generic",Boolean.class,generic);
        get("Generic").setDescription("Should this be applied across all features, or a subset described in the 'Dependency'");
    }

    public List<Parameter> getParameters() {
        LinkedList<Parameter> ret = new LinkedList<Parameter>();
        for(Parameter p: get()){
            if((p.getType().compareTo("Description")!=0) &&
                    (p.getType().compareTo("Generic")!=0) &&
                    (p.getType().compareTo("Name")!=0)){
                ret.add(p);
            }
        }
        return ret;
    }

    public void setParameters(List<Parameter> parameters) {
        for(Parameter p : parameters){
            if((p.getType().compareTo("Description")!=0) &&
                    (p.getType().compareTo("Generic")!=0) &&
                    (p.getType().compareTo("Name")!=0)){
                replace(p);
            }
        }
    }

    public String getName() {
        if(quickCheck("Name",String.class)){
            return ((String)quickGet("Name"));
        }else{
            return "Unknown Default";
        }
    }

    public void setName(String name) {
        set("Name",String.class,name);
        get("Name").setDescription("The type name of this object in the AggregatorFactory. Use AggregatorFactory.getKnownTypes() to list available and valid aggregator types");
    }

    /**
     * Default constructor for AggregatorDefinition
     */
    public AggregatorDefinition() {
        setName("Unknown Default");
        setGeneric(false);
        setDescription("Default Description");
    }

    public AggregatorDefinition(String n, String d, boolean g, List<Parameter> p){
        setName(n);
        setDescription(d);
        setGeneric(g);
        setParameters(p);
    }
}
