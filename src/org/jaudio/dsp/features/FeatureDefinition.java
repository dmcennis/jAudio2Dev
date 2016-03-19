/*
 * @(#)FeatureDefinition.java	0.5	Feb 27, 2005.
 *
 * McGill Univarsity
 */

package org.jaudio.dsp.features;

import jAudioFeatureExtractor.ACE.XMLParsers.XMLDocumentParser;
import org.dynamicfactory.descriptors.PropertiesImplementation;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Vector;

/**
 * Objects of this class each hold meta-data about a feature, as specified by
 * the five public fields. Objects of this class do not hold any feature values
 * of particular instances.
 * <p>
 * Methods are available for viewing the features, veryifying the uniqueness of
 * their names, saving them to disk and loading the, from disk.
 * <p>
 * Daniel McEnnis 05-07-05 Added attributes to definition that describe the
 * names (and implicitly number) of editable features.
 * 
 * @author Cory McKay
 */
public class FeatureDefinition extends PropertiesImplementation implements Serializable  {

	/* FIELDS ***************************************************************** */

	/**
	 * An identifier for use in serialization.
	 */
	private static final long serialVersionUID = 2L;

	/* CONSTRUCTORS *********************************************************** */

	/**
	 * Generate an empty FeatureDefinition with the name "Undefined Feature".
	 */
	public FeatureDefinition() {
		ResourceBundle bundle = ResourceBundle.getBundle("Translations");

		setName(bundle.getString("undefined.feature"));
		setDescription(new String(""));
		setIs_sequential(false);
		setDimensions(1);
		setAttributes(new String[] {});
	}

	/**
	 * Explicitly define a new Feature Definition with no editable attributes.
	 * 
	 * @param name
	 *            The name of the feature. This name should be unique among each
	 *            set of features.
	 * @param description
	 *            A description of what the feature represents. May be left as
	 *            an empty string.
	 * @param is_sequential
	 *            Specifies whether a feature can be applied to sequential
	 *            windows of a data set. A value of true means that it can, and
	 *            a value of false means that only one feature value may be
	 *            extracted per data set.
	 * @param dimensions
	 *            The number of values that exist for the feature for a given
	 *            section of a data set. This value will be 1, except for
	 *            multi-dimensional features.
	 */
	public FeatureDefinition(String name, String description,
							 boolean is_sequential, int dimensions) {
		this.setName(name);
		this.setDescription(description);
		this.setIs_sequential(is_sequential);
		this.setDimensions(dimensions);
		this.setAttributes(new String[] {});
	}

	/**
	 * Explicitly define a feature along with a description of editable
	 * attributes.
	 * 
	 * @param name
	 *            The name of the feature. This name should be unique among each
	 *            set of features.
	 * @param description
	 *            A description of what the feature represents. May be left as
	 *            an empty string.
	 * @param is_sequential
	 *            Specifies whether a feature can be applied to sequential
	 *            windows of a data set. A value of true means that it can, and
	 *            a value of false means that only one feature value may be
	 *            extracted per data set.
	 * @param dimensions
	 *            The number of values that exist for the feature for a given
	 *            section of a data set. This value will be 1, except for
	 *            multi-dimensional features.
	 * @param attributes
	 *            The names of all editable attributes in the feature
	 */
	public FeatureDefinition(String name, String description,
							 boolean is_sequential, int dimensions, String[] attributes) {
		this.setName(name);
        this.get("Name").setDescription("The name of the feature. This name should be unique among each set of features.");
		this.setDescription(description);
        this.get("Description").setDescription("A description of what the feature represents. May be left as an empty string.");
		this.setIs_sequential(true);
        this.get("isSequential").setDescription("Specifies whether a feature can be applied to sequential windows of a data set. A value of true means that it can, and a value of false means that only one feature value may be extracted per data set.");
		this.setDimensions(dimensions);
        this.get("Dimensions").setDescription("The number of values that exist for the feature for a given section of a data set. This value will be 1, except for multi-dimensional features.");
		this.setAttributes(attributes);
        this.get("Attributes").setDescription("The names of all editable attributes in the feature");
	}

	/* PUBLIC METHODS ********************************************************* */

	/**
	 * Returns a formatted text description of the FeatureDescription object.
	 * 
	 * @return The formatted description.
	 */
	public String getFeatureDescription() {
		ResourceBundle bundle = ResourceBundle.getBundle("Translations");
		String info = String.format(bundle.getString("name.s.ndescription.s.nis.sequential.b.ndimensions.d.n.n"), getName(), getDescription(), is_sequential(), getDimensions());
		return info;
	}

    public List<FeatureDependency> getDependencies(){
        if(quickCheck("Dependency",FeatureDependency.class)){
            return (List<FeatureDependency>)get("Dependency").getValue();
        }else{
            return new LinkedList<FeatureDependency>();
        }
    }

	/**
	 * Returns a formatted text description of the given FeatureDescription
	 * objects.
	 * 
	 * @param definitions
	 *            The feature definitions to describe.
	 * @return The formatted description.
	 */
	public static String getFeatureDescriptions(FeatureDefinition[] definitions) {
		String combined_descriptions = new String();
		for (int i = 0; i < definitions.length; i++)
			combined_descriptions = String.format("%s%s",combined_descriptions,definitions[i].getFeatureDescription());
		return combined_descriptions;
	}

	/**
	 * Parses a feature_key_file_path XML file and returns an array of
	 * FeatureDefinitionwith objects holding its contents. An exception is
	 * thrown if the file is invalid in some way or if the file contains
	 * multiple feature. with the same name.
	 * 
	 * @param feature_key_file_path
	 *            The path of the XML file to parse.
	 * @throws Exception
	 *             Informative exceptions is thrown if an invalid file or file
	 *             path is specified or if the file holds multiple features with
	 *             the same name.
	 */
	public static FeatureDefinition[] parseFeatureDefinitionsFile(
			String feature_key_file_path) throws Exception {
		// Parse the file
		Object[] results = (Object[]) XMLDocumentParser.parseXMLDocument(
				feature_key_file_path, "feature_key_file");
		FeatureDefinition[] parse_results = new FeatureDefinition[results.length];
		for (int i = 0; i < parse_results.length; i++)
			parse_results[i] = (FeatureDefinition) results[i];

		// Throw an exception if the definitions have features with duplicate
		// names
		String duplicates = verifyFeatureNameUniqueness(parse_results);
		if (duplicates != null){
			ResourceBundle bundle = ResourceBundle.getBundle("Translations");
			throw new Exception(String.format(bundle.getString("could.not.parse.because.there.are.multiple.noccurences.of.the.following.feature.names.s.n"),duplicates));
		}

		// Return the results
		return parse_results;
	}

	/**
	 * Saves a feature_key_file_path XML file with the contents specified in the
	 * given FeatureDefinition array and the comments specified in the comments
	 * parameter. Also verifies that all of the given definitions have unique
	 * names, and throws an exception if they do not.
	 * 
	 * @param definitions
	 *            The FeatureDefinitions to save.
	 * @param to_save_to
	 *            The file to save to.
	 * @param comments
	 *            Any comments to be saved inside the comments element of the
	 *            XML file.
	 * @throws Exception
	 *             An informative exception is thrown if the file cannot be
	 *             saved or if any of the given definitions have the same name.
	 */
	public static void saveFeatureDefinitions(FeatureDefinition[] definitions,
			File to_save_to, String comments) throws Exception {
		// Throw an exception if the definitions have features with duplicate
		// names
		String duplicates = verifyFeatureNameUniqueness(definitions);
		if (duplicates != null) {
            ResourceBundle bundle = ResourceBundle.getBundle("Translations");
            throw new Exception(String.format(bundle.getString("could.not.save.because.there.are.multiple.noccurences.of.the.following.feature.names.n.s"), duplicates));
        }

		// Perform the save
		try {
			// Prepare stream writer
			FileOutputStream to = new FileOutputStream(to_save_to);
			DataOutputStream writer = new DataOutputStream(to);

			// Write the header and the first element of the XML file
			String pre_tree_part = new String(
					"<?xml version=\"1.0\"?>\n"
							+ "<!DOCTYPE feature_key_file [\n"
							+ "   <!ELEMENT feature_key_file (comments, feature+)>\n"
							+ "   <!ELEMENT comments (#PCDATA)>\n"
							+ "   <!ELEMENT feature (name, description?, is_sequential, parallel_dimensions)>\n"
							+ "   <!ELEMENT name (#PCDATA)>\n"
							+ "   <!ELEMENT description (#PCDATA)>\n"
							+ "   <!ELEMENT is_sequential (#PCDATA)>\n"
							+ "   <!ELEMENT parallel_dimensions (#PCDATA)>\n"
							+ "]>\n\n" + "<feature_key_file>\n\n"
							+ "   <comments>" + comments + "</comments>\n\n");
			writer.writeBytes(pre_tree_part);

			// Write the XML code to represent the contents of each
			// FeatureDefinition
			for (int feat = 0; feat < definitions.length; feat++) {
				writer.writeBytes("   <feature>\n");
				writer.writeBytes("      <name>" + definitions[feat].getName()
						+ "</name>\n");
				if (definitions[feat].getDescription().compareTo("")!=0)
					writer.writeBytes("      <description>"
							+ definitions[feat].getDescription()
							+ "</description>\n");
				writer.writeBytes("      <is_sequential>"
						+ definitions[feat].is_sequential()
						+ "</is_sequential>\n");
				writer.writeBytes("      <parallel_dimensions>"
						+ definitions[feat].getDimensions()
						+ "</parallel_dimensions>\n");
				writer.writeBytes("   </feature>\n\n");
			}
			writer.writeBytes("</feature_key_file>");

			// Close the output stream
			writer.close();
		} catch (Exception e) {
            ResourceBundle bundle = ResourceBundle.getBundle("Translations");
			throw new Exception(String.format(bundle.getString("unable.to.write.file.s"),to_save_to.getName()));
		}
	}

	/**
	 * Checks if the given FeatureDefinitions hold any features with the same
	 * names. Returns null if there are no duplicates and a formatted string of
	 * the names which are duplicated if there are duplicates.
	 * 
	 * @param definitions
	 *            The FeatureDefinitions to check for duplicate names.
	 * @return Null if there are no duplicates and the names of the duplicates
	 *         if there are duplicates.
	 */
	public static String verifyFeatureNameUniqueness(
			FeatureDefinition[] definitions) {
		boolean found_duplicate = false;
		Vector<String> duplicates = new Vector<String>();
		for (int i = 0; i < definitions.length - 1; i++)
			for (int j = i + 1; j < definitions.length; j++)
				if (definitions[i].getName().compareTo(definitions[j].getName())==0) {
					found_duplicate = true;
					duplicates.add(definitions[i].getName());
					j = definitions.length;
				}
		if (found_duplicate) {
			Object[] duplicated_names_obj = (Object[]) duplicates.toArray();
			String[] duplicated_names = new String[duplicated_names_obj.length];
			for (int i = 0; i < duplicated_names.length; i++)
				duplicated_names[i] = (String) duplicated_names_obj[i];
			String duplicates_formatted = new String();
			for (int i = 0; i < duplicated_names.length; i++) {
				duplicates_formatted += duplicated_names[i];
				if (i < duplicated_names.length - 1)
					duplicates_formatted += ", ";
			}
			return duplicates_formatted;
		} else
			return null;
	}


    /**
     * The name of the feature. This name should be unique among each set of
     * features.
     */
    public String getName() {

            return (String)super.quickGet("Name");
     }

    public void setName(String name) {
        add("Name",name);
    }

    /**
     * A description of what the feature represents. May be left as an empty
     * string.
     */
    public String getDescription() {
        return (String)quickGet("Description");
    }

    public void setDescription(String description) {
        add("Description",description);
    }

    /**
     * Specifies whether a feature can be applied to sub-section of a data set
     * (e.g. a window of audio). A value of true means that it can, and a value
     * of false means that only one feature value may be extracted per data set.
     */
    public boolean is_sequential() {
        return (boolean)quickGet("isSequental");
    }

    public void setIs_sequential(boolean is_sequential) {
        add("isSequential",is_sequential);
    }

    /**
     * The number of values that exist for the feature for a given section of a
     * data set. This value will be 1, except for multi-dimensional features.
     */
    public int getDimensions() {
        return (int)quickGet("Dimensions");
    }

    public void setDimensions(int dimensions) {
        add("Dimensions",dimensions);
    }

    /**
     * names of each editable attribute this feature has
     */
    public String[] getAttributes() {
        return (String[])(get("Attributes").getValue().toArray(new String[]{}));
    }

    public void setAttributes(String[] attributes) {
        add("Attributes",attributes);
    }

    public void setDependency(String name, int start, int length){
        setDependency(name,start);
        for(int i=start+1; i<start+length;++i){
            addDependency(name,i);
        }
    }
    public void addDependency(String name, int offset){
        add("Dependency",new FeatureDependency(name,0-offset));
    }

    public void addDependency(String name){
        setDependency(name,0);
    }

    public void addDependency(String name, int start, int length){
        for(int i=start; i<start+length;++i){
            addDependency(name,i);
        }
    }

    public void setDependency(String name, int offset){
        set("Dependency",new FeatureDependency(name,0-offset));
    }

    public void setDependency(String name){
        setDependency(name,0);
    }

    public void setDependency(FeatureDependency dep){
        set("Dependency",dep);
    }

    public void setDependency(List<FeatureDependency> dsp){
        set("Dependency",dsp);
    }

    public void addDependency(List<FeatureDependency> dsp){
        add("Dependency",dsp);
    }

    public void addDependency(FeatureDependency dep){
        add("Dependency",dep);
    }

    public List<FeatureDependency> getDependency(){
        if(quickCheck("Depedency",FeatureDependency.class)){
            return (List<FeatureDependency>)(get("Dependency").getValue());
        }else{
            return new LinkedList<FeatureDependency>();
        }
    }
}