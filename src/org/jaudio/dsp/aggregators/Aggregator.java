/**
 * 
 */
package org.jaudio.dsp.aggregators;

import org.dynamicfactory.Creatable;
import org.dynamicfactory.descriptors.*;
import org.dynamicfactory.property.Property;
import org.jaudio.dsp.features.FeatureDefinition;
import org.jaudio.dsp.features.FeatureExtractor;
import org.jaudio.dsp.features.FeatureFactory;

import java.io.DataOutputStream;
import java.io.Writer;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

/**
 * Aggregator is an interface for specifying the mechanism for collapsing
 * frame-by-frame features into per-file data. There exists two types of
 * aggregators - specific aggregators and generic aggregators.
 * <p>
 * Generic aggregators aggregate for each feature (seperately) that is to be
 * saved and should override init and setSource methods. Specific aggregators
 * can aggregate any number of features, but these features must be specified in
 * advance.
 * 
 * @author Daniel McEnnis
 * 
 */
public abstract class Aggregator implements Creatable<Aggregator>{

	double[] result = null;

	AggregatorDefinition metadata;

	FeatureDefinition definition;

    public void setName(String name) {
        metadata.setName(name);
    }

    public int compareTo(Properties o) {
        return metadata.compareTo(o);
    }

    public void clear() {
        metadata.clear();
    }

    public ParameterInternal get(String string) {
        return metadata.get(string);
    }

    public void set(String type, List value, String description) {
        metadata.set(type, value, description);
    }

    public void set(String type, Class c, Object value, String description) {
        metadata.set(type, c, value, description);
    }

   public void add(String name, Class type, Object value) {
        metadata.add(name, type, value);
    }

    public void set(Property value) {
        metadata.set(value);
    }

    public void add(String type, Object value) {
        metadata.add(type, value);
    }

    public void add(ParameterInternal parameter) {
        metadata.add(parameter);
    }

    public void remove(String type) {
        metadata.remove(type);
    }

    public SyntaxObject getDefaultRestriction() {
        return metadata.getDefaultRestriction();
    }

    public void setDefaultRestriction(SyntaxObject restriction) {
        metadata.setDefaultRestriction(restriction);
    }


    public List<Parameter> get() {
        return metadata.get();
    }


    public boolean check(Parameter type) {
        return metadata.check(type);
    }


    public boolean check(Properties props) {
        return metadata.check(props);
    }


    public void replace(Parameter type) {
        metadata.replace(type);
    }


    public PropertiesInternal merge(Properties right) {
        return metadata.merge(right);
    }


    public void add(String type, List value) {
        metadata.add(type, value);
    }


    public void add(String type, Class c, List value) {
        metadata.add(type, c, value);
    }


    public void set(String type, Object value) {
        metadata.set(type, value);
    }


    public void set(String type, List value) {
        metadata.set(type, value);
    }


    public void set(String type, Class c, Object value) {
        metadata.set(type, c, value);
    }


    public void set(String type, Class c, List value) {
        metadata.set(type, c, value);
    }


    public void set(String type, Class c, List value, String description) {
        metadata.set(type, c, value, description);
    }


    public void set(String type, List value, String description, String longDescription) {
        metadata.set(type, value, description, longDescription);
    }


    public void set(String type, Class c, Object value, String description, String longDescription) {
        metadata.set(type, c, value, description, longDescription);
    }


    public void set(String type, Class c, List value, String description, String longDescription) {
        metadata.set(type, c, value, description, longDescription);
    }


    public Object quickGet(String s) {
        return metadata.quickGet(s);
    }


    public boolean quickCheck(String s, Class type) {
        return metadata.quickCheck(s, type);
    }

    /**
	 * Convenience variable containing the end of line characters for this
	 * system.
	 */
	public static final String LINE_SEP = System.getProperty("line.separator");

	/**
	 * Provide a list of features that are to be aggregated by this feature.
	 * Returning null indicates that this aggregator accepts only one feature
	 * and every feature avaiable should be used.
	 * 
	 * @return list of features to be used by this aggregator or null
	 */
	public List<FeatureExtractor> getFeaturesToApply() {
		if(quickCheck("Features",FeatureDefinition.class)){
            LinkedList<FeatureExtractor> ret = new LinkedList<FeatureExtractor>();
            for(FeatureDefinition fd : (List<FeatureDefinition>)get("Features").getValue()){
                ret.add(FeatureFactory.getInstance().create(fd));
            }
            return ret;
		}else{
            return null;
        }
	}

	/**
	 * Provide a list of the values of all parameters this aggregator uses.
	 * Aggregators without parameters return null.
	 * 
	 * @return list of the values of parmeters or null.
	 */
	public List<Parameter> getParameters() {
		return getAggregatorDefinition().getParameters();
	}

	/**
	 * Description of a particular instantiation of an aggregate. This should
	 * not be called until after the specific features have been specified by
	 * the init function.
	 * 
	 * @return Feature Definition describing this instantiation of this
	 *         aggregate object
	 */
	public FeatureDefinition getFeatureDefinition() {
		return definition;
	}

	/**
	 * Returns a description of this instantiation of this class of aggregator
	 */
	public AggregatorDefinition getAggregatorDefinition() {
		return metadata;
	}

	public void setSource(FeatureExtractor feature) {
        set("Features",FeatureExtractor.class,feature);
	}

    public void addSource(FeatureExtractor feature) {
        add("Features",FeatureExtractor.class,feature);
    }

    /**
	 * Aggregates the values of the features specified by the init function
	 * accross all windows of the data recieved.
	 * 
	 * @param values
	 *            complete array of the extracted features. Indecis are window,
	 *            feature, and then feature value.
	 */
	public abstract double[] aggregate(double[][][] values) throws Exception ;

	/**
	 * Output the feature definition entry (for an ACE feature definition file)
	 * for this particular instantiation of the aggreagtor.
	 * 
	 * @param output
	 *            output stream to be used.
	 * @throws Exception
	 */
	public void outputACEFeatureKeyEntries(DataOutputStream output)
			throws Exception {
		output.writeBytes("	<feature>" + LINE_SEP);
		output.writeBytes("		<name>" + definition.getName() + "</name>" + LINE_SEP);
		output.writeBytes("		<description>" + definition.getDescription()
				+ "</description>" + LINE_SEP);
		output.writeBytes("		<is_sequential>" + definition.is_sequential()
				+ "</is_sequential>" + LINE_SEP);
		output.writeBytes("		<parallel_dimensions>" + definition.getDimensions()
				+ "</parallel_dimensions>" + LINE_SEP);
		output.writeBytes("	</feature>" + LINE_SEP);

	}

	/**
	 * Output the data definition entries of a the ACE format
	 * 
	 * @param output stream to write the data to
	 * @throws Exception
	 */
	public void outputACEValueEntries(DataOutputStream output) throws Exception {
		output.writeBytes("		<feature>" + LINE_SEP);
		output.writeBytes("			<name>" + definition.getName() + "</name>" + LINE_SEP);
		for (int i = 0; i < result.length; ++i) {
			output.writeBytes("			<v>"
					+ Double.toString(result[i])
					+ "</v>" + LINE_SEP);
		}
		output.writeBytes("		</feature>" + LINE_SEP);
	}

	/**
	 * Output the header entries of a Weka ARFF file.  This should only be called once the 
	 * full aggregator output has been calculated.
	 * 
	 * @param output stream to write the data to
	 * @throws Exception
	 */
	public void outputARFFHeaderEntries(DataOutputStream output)
			throws Exception {
		for (int i = 0; i < definition.getDimensions(); ++i) {
			output.writeBytes("@ATTRIBUTE \"" + definition.getName() + i
					+ "\" NUMERIC" + LINE_SEP);
		}
	}

	/**
	 * Output the data in the ARFF body.
	 * 
	 * @param output
	 * @throws Exception
	 */
	public void outputARFFValueEntries(DataOutputStream output)
			throws Exception {
		output.writeBytes(Double.toString(result[0]));
		for (int i = 1; i < definition.getDimensions(); ++i) {
			output
					.writeBytes(","
							+ Double.toString(result[i]));
		}
	}

    /**
     * Output the data in the ARFF body.
     *
     * @param output
     * @throws Exception
     */
    public void outputJSONEntries(Writer output)
            throws Exception {
        output.write("\t\""+definition.getName()+"\" : ["+LINE_SEP);
        boolean first = true;
        for (int i = 0; i < definition.getDimensions(); ++i) {
            if(first){
                first = false;
            }else{
                output.write(",");
            }
            output.write(Double.toString(result[i]));
        }
        output.write("\t\t]");
    }

//    /**
//	 * Set parameters of the aggregator to the given values.  For specific aggregators, the feature list
//	 * is non-null and references currently loaded features.
//	 * Throws exception if the feature list is null or contains invalid entries only if the aggregator is specific.
//	 * Otherwise it is ignored.
//	 * If the number of given parameters
//	 * is greater (but not neccessarily less) than the number of actual paramaters, or
//	 * if the parameters are in the wrong format, an aggregator that uses parameters may throw an exception.
//	 * Both null and zero length array imply no parameters, but only null guarantees an exception if a parameter
//	 * is present.
//	 *
//	 * @param non strings matching features for specific aggregation.
//	 * @param params strings that can be cast by toString to the appropriate parameter types.
//	 * @throws Exception for a number of format or null entry conditions (see above).
//	 */
//	public void setParameters(String[] featureNames, String[] params)
//			throws Exception {
//
//	}

	protected int calculateOffset(double[][][] values, List<FeatureExtractor> featureList) {
		TreeMap<FeatureExtractor,Integer> map = (TreeMap<FeatureExtractor,Integer>)quickGet("FeatureMap");
		int ret = 0;
		for (FeatureExtractor fe : featureList) {
			int offset = 0;
			while (values[offset][map.get(fe)] == null) {
				offset++;
			}
			if (offset > ret) {
				ret = offset;
			}
		}
		return ret;
	}

	protected int[][] collapseFeatures(double[][][] values, List<FeatureExtractor> indecis) {
		TreeMap<FeatureExtractor,Integer> map = (TreeMap<FeatureExtractor,Integer>)quickGet("FeatureMap");
		int count = 0;
		for (FeatureExtractor fe : indecis) {
			if (values[values.length - 1][map.get(fe)] != null) {
				count += values[values.length - 1][map.get(fe)].length;
			}
		}
		int[][] ret = new int[count][2];
		count = 0;
		for (FeatureExtractor fe : indecis) {
			if (values[values.length - 1][map.get(fe)] != null) {
				for (int j = 0; j < values[values.length - 1][map.get(fe)].length; ++j) {
					ret[count][0] = map.get(fe);
					ret[count][1] = j;
					count++;
				}
			}
		}
		return ret;
	}
	
	/**
	 * Returns the results in a double array (more useful for embedding than an XML pipe solution).
	 * 
	 * @output returns the calculated results of analysis or null depending on whether calculations have taken place or not.
	 */
	public double[] getResults(){
		return result;
	}

}
