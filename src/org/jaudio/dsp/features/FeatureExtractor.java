/*
 * @(#)FeatureExtractor.java	1.0	April 5, 2005.
 *
 * McGill Univarsity
 */

package org.jaudio.dsp.features;

import org.dynamicfactory.Creatable;
import org.dynamicfactory.descriptors.*;
import org.dynamicfactory.property.Property;
import org.jaudio.dsp.DataModel;

import java.io.File;
import java.util.List;
import java.util.ResourceBundle;

/**
 * The prototype for feature extractors. Each class that extends this class will
 * extract a particular feature from a window of audio samples. Such classes do
 * not store feature values, only extract them.
 * <p>
 * Classes that extend this class should have a constructor that sets the three
 * protected fields of this class.
 * <p>
 * Daniel McEnnis 05-07-05 Added code to allow generic access to feature
 * attributes
 * <p>
 * Daniel McEnnis 05-08-05 Added setWindow and setParent features following the
 * composite pattern for new features.
 * 
 * @author Cory McKay
 */
public abstract class FeatureExtractor implements Creatable<FeatureExtractor>{
	/* FIELDS ***************************************************************** */

	/**
	 * Meta-data describing a feature.
	 */
	protected FeatureDefinition definition;

	/**
	 * The names of other features that are needed in order for a feature to be
	 * calculated. Will be null if there are no dependencies.
	 */
//	protected String[] dependencies;

	/**
	 * The offset in windows of each of the features named in the dependencies
	 * field. An offset of -1, for example, means that the feature in
	 * dependencies with the same indice should be provided to this class's
	 * extractFeature method with a value that corresponds to the window prior
	 * to the window corresponding to this feature. Will be null if there are no
	 * dependencies. This must be null, 0 or a negative number. Positive numbers
	 * are not allowed.
	 */
//	protected int[] offsets;

	/**
	 * If a feature alters its number of dimensions, it needs to be able to
	 * notify the holding object that a visible change has occured.
	 */
	protected DataModel parent;
/* PUBLIC METHODS ********************************************************* */

	public String getFeatureDescription() {
		return definition.getFeatureDescription();
	}

	public void setIs_sequential(boolean is_sequential) {
		definition.setIs_sequential(is_sequential);
	}

	public boolean is_sequential() {
		return definition.is_sequential();
	}


	public void addDependency(FeatureDependency dep) {
		definition.addDependency(dep);
	}

	public static FeatureDefinition[] parseFeatureDefinitionsFile(String feature_key_file_path) throws Exception {
		return FeatureDefinition.parseFeatureDefinitionsFile(feature_key_file_path);
	}

	public void setDependency(String name) {
		definition.setDependency(name);
	}

	public String getName() {
		return definition.getName();
	}

	public void setAttributes(String[] attributes) {
		definition.setAttributes(attributes);
	}

	public void addDependency(List<FeatureDependency> dsp) {
		definition.addDependency(dsp);
	}

	public void setDependency(FeatureDependency dep) {
		definition.setDependency(dep);
	}

	public void setDescription(String description) {
		definition.setDescription(description);
	}

	public void addDependency(String name, int offset) {
		definition.addDependency(name, offset);
	}

	public void setDependency(List<FeatureDependency> dsp) {
		definition.setDependency(dsp);
	}

	public static String verifyFeatureNameUniqueness(FeatureDefinition[] definitions) {
		return FeatureDefinition.verifyFeatureNameUniqueness(definitions);
	}

	public String getDescription() {
		return definition.getDescription();
	}

	public void setName(String name) {
		definition.setName(name);
	}

	public static String getFeatureDescriptions(FeatureDefinition[] definitions) {
		return FeatureDefinition.getFeatureDescriptions(definitions);
	}

	public int getDimensions() {
		return definition.getDimensions();
	}

	public void addDependency(String name) {
		definition.addDependency(name);
	}

	public String[] getAttributes() {
		return definition.getAttributes();
	}

	public void setDependency(String name, int start, int length) {
		definition.setDependency(name, start, length);
	}

	public void addDependency(String name, int start, int length) {
		definition.addDependency(name, start, length);
	}

	public static void saveFeatureDefinitions(FeatureDefinition[] definitions, File to_save_to, String comments) throws Exception {
		FeatureDefinition.saveFeatureDefinitions(definitions, to_save_to, comments);
	}

	public void setDimensions(int dimensions) {
		definition.setDimensions(dimensions);
	}

    public int compareTo(Properties o) {
        return definition.compareTo(o);
    }


    public void clear() {
        definition.clear();
    }

    public ParameterInternal get(String string) {
        return definition.get(string);
    }


    public void set(String type, List value, String description) {
        definition.set(type, value, description);
    }


    public void set(String type, Class c, Object value, String description) {
        definition.set(type, c, value, description);
    }


    public void add(String name, Class type, Object value) {
        definition.add(name, type, value);
    }

    public void set(Property value) {
        definition.set(value);
    }

    public void add(String type, Object value) {
        definition.add(type, value);
    }

    public void add(ParameterInternal parameter) {
        definition.add(parameter);
    }

    public void remove(String type) {
        definition.remove(type);
    }

    public SyntaxObject getDefaultRestriction() {
        return definition.getDefaultRestriction();
    }

    public void setDefaultRestriction(SyntaxObject restriction) {
        definition.setDefaultRestriction(restriction);
    }

    public List<Parameter> get() {
        return definition.get();
    }

    public boolean check(Parameter type) {
        return definition.check(type);
    }

    public boolean check(Properties props) {
        return definition.check(props);
    }

    public void replace(Parameter type) {
        definition.replace(type);
    }

    public PropertiesInternal merge(Properties right) {
        return definition.merge(right);
    }

    public void add(String type, List value) {
        definition.add(type, value);
    }


    public void add(String type, Class c, List value) {
        definition.add(type, c, value);
    }

    public void set(String type, Object value) {
        definition.set(type, value);
    }

    public void set(String type, List value) {
        definition.set(type, value);
    }

    public void set(String type, Class c, Object value) {
        definition.set(type, c, value);
    }

    public void set(String type, Class c, List value) {
        definition.set(type, c, value);
    }


    public void set(String type, Class c, List value, String description) {
        definition.set(type, c, value, description);
    }


    public void set(String type, List value, String description, String longDescription) {
        definition.set(type, value, description, longDescription);
    }


    public void set(String type, Class c, Object value, String description, String longDescription) {
        definition.set(type, c, value, description, longDescription);
    }


    public void set(String type, Class c, List value, String description, String longDescription) {
        definition.set(type, c, value, description, longDescription);
    }


    public Object quickGet(String s) {
        return definition.quickGet(s);
    }


    public boolean quickCheck(String s, Class type) {
        return definition.quickCheck(s, type);
    }

    public void setDependency(String name, int offset) {
		definition.setDependency(name, offset);
	}

	/**
	 * Returns meta-data describing this feature.
	 * <p>
	 * <b>IMPORTANT:</b> Note that a value of 0 in the returned dimensions of
	 * the FeatureDefinition implies that the feature dimensions are variable,
	 * and depend on the analyzed data.
	 */
	public FeatureDefinition getFeatureDefinition() {
		return definition;
	}

	/**
	 * Returns the names of other features that are needed in order to extract
	 * this feature. Will return null if no other features are needed.
	 */
	public List<FeatureDependency> getDependencies() {
		return getFeatureDefinition().getDependencies();
	}


	/**
	 * Returns the offsets of other features that are needed in order to extract
	 * this feature. Will return null if no other features are needed.
	 * <p>
	 * The offset is in windows, and the indice of the retuned array corresponds
	 * to the indice of the array returned by the getDependencies method. An
	 * offset of -1, for example, means that the feature returned by
	 * getDependencies with the same indice should be provided to this class's
	 * extractFeature method with a value that corresponds to the window prior
	 * to the window corresponding to this feature.
	 */
//	public int[] getDepenedencyOffsets() {
//		return offsets;
//	}

	/**
	 * The prototype function that classes extending this class will override in
	 * order to extract their feature from a window of audio.
	 * 
	 * @param samples
	 *            The samples to extract the feature from.
	 * @param sampling_rate
	 *            The sampling rate that the samples are encoded with.
	 * @param other_feature_values
	 *            The values of other features that are needed to calculate this
	 *            value. The order and offsets of these features must be the
	 *            same as those returned by this class's getDependencies and
	 *            getDependencyOffsets methods respectively. The first indice
	 *            indicates the feature/window and the second indicates the
	 *            value.
	 * @return The extracted feature value(s).
	 * @throws Exception
	 *             Throws an informative exception if the feature cannot be
	 *             calculated.
	 */
	public abstract double[] extractFeature(double[] samples,
			double sampling_rate, double[][] other_feature_values)
			throws Exception;

	/**
	 * Function permitting an unintelligent outside function (ie. EditFeatures
	 * frame) to get the default values used to populate the table's entries.
	 * The correct index values are inferred from definition.attribute value.
	 * 
	 * @param index
	 *            which of AreaMoment's attributes should be edited.
	 */
	public String getElement(int index) throws Exception {
		ResourceBundle bundle = ResourceBundle.getBundle("Translations");
		throw new Exception(
				bundle.getString("internal.error.this.feature.has.no.method.defined.for.editing.attributes.perhaps.the.author.forgot.to.define.this.method"));
	}

	/**
	 * Function permitting an unintelligent outside function (ie. EditFeatures
	 * frame) to set the default values used to popylate the table's entries.
	 * Like getElement, the correct index values are inferred from the
	 * definition.attributes value.
	 * 
	 * @param index
	 *            attribute to be set
	 * @param value
	 *            new value of the attribute
	 */
	public void setElement(int index, String value) throws Exception {
		ResourceBundle bundle = ResourceBundle.getBundle("Translations");
		throw new Exception(
				bundle.getString("internal.error.this.feature.has.no.method.defined.for.editing.attributes.perhaps.the.author.forgot.to.define.this.method1"));
	}

	/**
	 * Function that must be overridden to allow this feature to be set globally
	 * by GlobalChange frame.
	 * 
	 * @param windowSize
	 *            the number of windows of offset to be used in calculating this
	 *            feature
	 */
	public void setWindow(int windowSize) throws Exception {

	}

	/**
	 * Gives features a reference to the container frame to notify it that
	 * features have changed state and need to be redrawn.
	 * 
	 * @param parent
	 *            container frame which holds the model for displaying features
	 *            in the feature display panel.
	 */
	public void setParent(DataModel parent) {
		this.parent = parent;
		// System.out.println(this.getClass());
	}

	/**
	 * Create an identical copy of this feature. This permits FeatureExtractor
	 * to use the prototype pattern to create new composite features using
	 * metafeatures.
	 */
	public abstract Object clone();

}