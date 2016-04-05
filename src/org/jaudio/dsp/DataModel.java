package org.jaudio.dsp;

import jAudioFeatureExtractor.jAudioTools.AudioMethodsPlayback;
import org.dynamicfactory.descriptors.*;
import org.dynamicfactory.descriptors.Properties;
import org.dynamicfactory.property.Property;
import org.jaudio.Cancel;
import org.jaudio.ModelListener;
import org.jaudio.Updater;
import org.jaudio.dsp.aggregators.Aggregator;
import org.jaudio.dsp.aggregators.AggregatorContainer;
import org.jaudio.dsp.aggregators.AggregatorFactory;
import org.jaudio.dsp.features.*;

import java.io.File;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

//import jAudioFeatureExtractor.jAudioTools.AudioSamples;

/**
 * All components that are not tightly tied to GUI. Used by console interface as
 * well as the GUI interface.
 * 
 * @author Daniel McEnnis
 */
public class DataModel {

	/**
	 * Reference to use for piping progress updates
	 */
	private ModelListener ml_;

	/**
	 * Handle for killing in-progress analysis
	 */
	private Cancel cancel_;

    PropertiesInternal properties = PropertiesFactory.newInstance().create();

	/**
	 * list of which features are enabled by default
	 */
//	public boolean[] defaults
            // equality is defined by *both* algorithm and properties
    private HashSet<FeatureExtractor> featuresToExtract = new HashSet<FeatureExtractor>();


	/**
	 * list of all features available
	 */
//	public FeatureExtractor[] features;

	/**
	 * Mapping between aggregator names and aggregator prototypes
	 */
//	public java.util.HashMap<String, Aggregator> aggregatorMap;

	/**
	 * List of aggreggators to apply
	 * <p>
	 * Must be set externally. Duplicates of a class are permitted (hence not a
	 * map) but each entry in the array must be fully initialized prior to
	 * calling extract().
	 */
//	public Aggregator[] aggregators;
	
	/**
	 * wrapper object for the aggregators.  This reference is null until a file extraction has been performed.
	 */
	private AggregatorContainer container;

	/**
	 * whether or a feature is a derived feature or not
	 */
//	public boolean[] is_primary;

	/**
	 * cached FeatureDefinitions for all available features
	 */
//	public FeatureDefinition[] featureDefinitions;

	/**
	 * info on all recordings that are made avaiable for feature extraction
	 */
//	public RecordingInfo[] recordingInfo;

	/**
	 * thread for playing back a recording
	 */
	public AudioMethodsPlayback.PlayThread playback_thread;

	Updater updater = null;

    private double[][][][] windowData = null;

    private double[][][] aggData = null;

//	public OutputStream featureKey = null;

//	public OutputStream featureValue = null;

	private TreeMap<Integer,HashSet<FeatureExtractor>> mapping = new TreeMap<Integer,HashSet<FeatureExtractor>>();

    public double[][][][] getWindowData(){return windowData;}

    public double[][][] getAggData(){return aggData;}

    public void addFeature(FeatureExtractor f){
        featuresToExtract.add(f);
    }

    public void addFeature(Collection<FeatureExtractor> list){
        featuresToExtract.addAll(list);
    }

    public void setFeature(FeatureExtractor f){
        featuresToExtract.clear();
        featuresToExtract.add(f);
    }

    public void setFeature(Collection<FeatureExtractor> list){
        featuresToExtract.clear();
        featuresToExtract.addAll(list);
    }

    public void clearFeatures(){
        featuresToExtract.clear();
    }

    public void addAggregator(Aggregator f) throws Exception{
        container.add(f);
    }

    public void addAggregator(Collection<Aggregator> list) throws Exception{
        container.add(list.toArray(new Aggregator[]{}));
    }

    public void setAggregator(Aggregator f)throws Exception{
        container.clear();
        container.add(f);
    }

    public void setAggregator(Collection<Aggregator> list)throws Exception{
        container.add(list.toArray(new Aggregator[]{}));
    }

    public void clearAggregator(){
        container.clear();
    }
    public void add(ParameterInternal parameter) {
        properties.add(parameter);
    }


    public void add(String type, Object value) {
        properties.add(type, value);
    }


    public void add(String name, Class type, Object value) {
        properties.add(name, type, value);
    }


    public void remove(String type) {
        properties.remove(type);
    }


    public void replace(Parameter type) {
        properties.replace(type);
    }


    public void setDefaultRestriction(SyntaxObject restriction) {
        properties.setDefaultRestriction(restriction);
    }


    public SyntaxObject getDefaultRestriction() {
        return properties.getDefaultRestriction();
    }


    public PropertiesInternal prototype() {
        return properties.prototype();
    }


    public ParameterInternal get(String string) {
        return properties.get(string);
    }


    public PropertiesInternal merge(Properties right) {
        return properties.merge(right);
    }


    public void clear() {
        properties.clear();
    }


    public void add(String type, List value) {
        properties.add(type, value);
    }


    public void add(String type, Class c, List value) {
        properties.add(type, c, value);
    }


    public void set(String type, Object value) {
        properties.set(type, value);
    }


    public void set(String type, List value) {
        properties.set(type, value);
    }


    public void set(String type, Class c, Object value) {
        properties.set(type, c, value);
    }


    public void set(String type, Class c, List value) {
        properties.set(type, c, value);
    }


    public void set(String type, List value, String description) {
        properties.set(type, value, description);
    }


    public void set(String type, Class c, Object value, String description) {
        properties.set(type, c, value, description);
    }


    public void set(String type, Class c, List value, String description) {
        properties.set(type, c, value, description);
    }


    public void set(String type, List value, String description, String longDescription) {
        properties.set(type, value, description, longDescription);
    }


    public void set(String type, Class c, Object value, String description, String longDescription) {
        properties.set(type, c, value, description, longDescription);
    }


    public void set(String type, Class c, List value, String description, String longDescription) {
        properties.set(type, c, value, description, longDescription);
    }


    public List<Parameter> get() {
        return properties.get();
    }


    public void set(Property value) {
        properties.set(value);
    }


    public boolean check(Parameter type) {
        return properties.check(type);
    }


    public boolean check(Properties props) {
        return properties.check(props);
    }


    public boolean quickCheck(String s, Class type) {
        return properties.quickCheck(s, type);
    }


    public Object quickGet(String s) {
        return properties.quickGet(s);
    }

    public int compareTo(Properties o) {
        return properties.compareTo(o);
    }

    /**
	 * Initializes each of the arrays with all available efeatures. Place to add
	 * new features.
	 * 
	 * @param ml
	 *            reference to a controller that will handle table updates.
	 */
	public DataModel(String featureXMLLocation, ModelListener ml) {
        properties.set("SaveFeaturesPerFile",Boolean.class,true,"","");
        properties.set("SaveFeaturesPerWindow",Boolean.class,true,"","");
        properties.set("WindowSize",Integer.class,256,"","");
        properties.set("WindowOverlap",Double.class,0.0,"","");
		ml_ = ml;
		cancel_ = new Cancel();
        container = new AggregatorContainer();
        Aggregator zernike = AggregatorFactory.getInstance().create("Zernike");
        FeatureExtractor feature = FeatureFactory.getInstance().create("ConstantQ");
        feature.set("SizeOfBins",new Double(0.5));
        zernike.set("Features",feature);
        try {
            container.add(zernike);
        }catch (Exception e){
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE,String.format("INTERNAL: Adding default aggregators threw an exception i the DataModel constructor. %s",e.getLocalizedMessage()));
        }

		LinkedList<FeatureExtractor> extractors = new LinkedList<FeatureExtractor>();
		LinkedList<Boolean> def = new LinkedList<Boolean>();
	}


	/**
	 * This is the function called when features change in such a way as the
	 * main display becomes out of date. WHen executed from the consol, this
	 * value is null.
	 */
	public void updateTable() {
		if (ml_ != null) {
			ml_.updateTable();
		}
	}

    public void extract(RecordingInfo[] info) throws Exception{
        extract(256,0.0,44100.0,false,false,true,info,0);
    }

	/**
	 * Function for executing the feature extraction process against a set of
	 * files.
	 * 
	 * @param windowSize
	 *            Size of the window in samples
	 * @param windowOverlap
	 *            Percent of the window to be overlapped - must be between 0 and
	 *            1.
	 * @param samplingRate
	 *            Sample rate given in samples per second
	 * @param normalise
	 *            indicates whether or not the file should be normalised before
	 *            feature extraction
	 * @param perWindowStats
	 *            should features be extracted for every window
	 * @param overallStats
	 *            should features be extracted over the entire window
	 * @param info
	 *            list of the files that are to be analyzed
	 * @param arff
	 *            output format of the data
	 * @throws Exception
	 */
	public void extract(int windowSize, double windowOverlap,
			double samplingRate, boolean normalise, boolean perWindowStats,
			boolean overallStats, RecordingInfo[] info, int arff)
			throws Exception {
		// Get the control parameters
		boolean save_features_for_each_window = perWindowStats;
		boolean save_overall_recording_features = overallStats;
		int window_size = windowSize;
		double window_overlap = windowOverlap;
		double sampling_rate = samplingRate;
		int outputType = arff;
		// Get the audio recordings to extract features from and throw an
		// exception
		// if there are none
		RecordingInfo[] recordings = info;
		if (recordings == null)
			throw new Exception(
					"No recordings available to extract features from.");

		if (updater != null) {
			updater.setNumberOfFiles(recordings.length);
		}

        container.add(featuresToExtract.toArray(new FeatureExtractor[]{}));

        HashMap<FeatureExtractor,Integer> offsets = new HashMap<FeatureExtractor,Integer>();

        HashMap<FeatureExtractor,Integer> indexInExtraction = new HashMap<FeatureExtractor, Integer>();

        HashMap<FeatureExtractor,HashMap<FeatureDependency,FeatureExtractor>> dependencyMap = new HashMap<FeatureExtractor, HashMap<FeatureDependency, FeatureExtractor>>();

        HashMap<FeatureExtractor,HashMap<Integer,Integer>> depOffsetList = new HashMap<FeatureExtractor,HashMap<Integer, Integer>>();


        FeatureExtractor[] list = orderFeatureExtraction(offsets,indexInExtraction,dependencyMap,depOffsetList);

        if(!checks()){
            throw new Exception("There was an unknown error during validation the audio data.");
        }

        HashMap<FeatureExtractor,double[][]> output = new HashMap<>();

        // recording - window index - feature index - feature dimension
        windowData = new double[recordings.length][][][];
        // recording  feature index - feature dimension
        aggData = new double[recordings.length][][];

        int recordingIndex=0;
        for(RecordingInfo rec : recordings){
            double[] samples = pad(rec.samples.samples,window_size,window_overlap);
            int increment = (int)((double)window_size*(1.0-window_overlap));
            double[][][] allData = new double[(samples.length-window_size)/increment][list.length][];
            int windowIndex=0;
            for(int current_index=0;current_index<samples.length-window_size;current_index+=increment){
                double[] window = new double[window_size];
                double[][] windowResults = new double[featuresToExtract.size()][];
                for(int i=0;i<window_size;++i){
                    window[i]=samples[current_index+i];
                }
                double[][] channels = rec.samples.getSamplesChannelSegregated(current_index,current_index+window_size);
                int outputIndex=0;
                for(int i=0;i<list.length;++i){
                    if(offsets.get(list[i])>=current_index) {

                        // create this feature's dependencies
                        List<FeatureDependency> deps = list[i].getDependencies();
                        double[][] dependecies = new double[deps.size()][];
                        int depList = 0;
                        for (FeatureDependency d : deps) {
                            dependecies[depList] = allData[windowIndex - offsets.get(dependencyMap.get(list[i]).get(d))][indexInExtraction.get(dependencyMap.get(list[i]).get(d))];
                        }

                        // process this feature
                        double[] ret = list[i].extractFeature(window,channels,sampling_rate,dependecies);
                        allData[windowIndex][i] = ret;
                        if(featuresToExtract.contains(list[i])){
                            windowResults[outputIndex] = ret;
                            outputIndex++;
                        }

                    }else{
                        allData[windowIndex][i] = new double[]{Double.NaN};
                        if(featuresToExtract.contains(list[i])){
                            windowResults[outputIndex] = new double[]{Double.NaN};
                            outputIndex++;
                        }
                    }
                }
                windowData[recordingIndex][current_index] = windowResults;
                windowIndex++;
            }
            if(save_overall_recording_features){
                container.aggregate(windowData[recordingIndex]);
                aggData[recordingIndex]=container.getResults();
            }
            if(!save_features_for_each_window){
                windowData[recordingIndex]=null;
            }
            recordingIndex++;
        }

        if(!save_features_for_each_window){
            windowData=null;
        }
		// Finalize saved XML files

//		processor.finalize();

		// JOptionPane.showMessageDialog(null,
		// "Features successfully extracted and saved.", "DONE",
		// JOptionPane.INFORMATION_MESSAGE);
	}

    private double[] pad(double[] samples, int window_size, double window_overlap) {
        int retLength = window_size+window_size*(int)Math.ceil(((double)samples.length - window_size)/((double)window_size*(1.0-window_overlap)));
        double[] ret = new double[retLength];
        Arrays.fill(ret,0.0);
        for(int i=0;i<samples.length;++i){
            ret[i]=samples[i];
        }
        return ret;
    }

    protected boolean checks() throws Exception{
        if(container!=null){
            if((container.getNumberOfAggregators()==0)&&((Boolean)properties.quickGet("SaveFeaturesPerWindow"))){
                throw new Exception(
                        "Saving aggregated values for each file without any aggregators specified");
            }
        }else if((Boolean)properties.quickGet("SaveFeaturesPerWindow")){
                    throw new Exception("Saving aggregators for each file but executed without setting a non-null AggregatorContainer object");
        }
        // Throw an exception if the control parameters are invalid
        if (!((Boolean)properties.quickGet("SaveFeaturesPerFile")) && !((Boolean)(properties.quickGet("SaveFeaturesPerWindow"))))
            throw new Exception(
                    "You must save at least one of the windows-based\n"
                            + "features and the overall file-based features.");
        // if (feature_values_save_path.equals(""))
        // throw new Exception("No save path specified for feature values.");
        // if (feature_definitions_save_path.equals(""))
        // throw new Exception(
        // "No save path specified for feature definitions.");
        if (((Double)properties.quickGet("WindowOverlap")) < 0.0 || ((Double)properties.quickGet("WindowOverlap")) >= 1.0)
            throw new Exception("Window overlap fraction is " + ((Double)properties.quickGet("WindowOverlap"))
                    + ".\n"
                    + "This value must be 0.0 or above and less than 1.0.");
        if (((Integer)properties.quickGet("WindowSize")) < 3)
            throw new Exception("Window size is " + ((Integer)properties.quickGet("WindowSize")) + ".\n"
                    + "This value must be above 2.");
        return true;
    }

    /**
     * While used to create an ordered list, this system defines a dependency graph guaranteeing features at the same
     * level and lower have no dependencies on this feature by any path.
     *
     * @return
     */
    protected FeatureExtractor[] orderFeatureExtraction(HashMap<FeatureExtractor,Integer> offsets,
                                                        HashMap<FeatureExtractor,Integer> indexInExtraction,
                                                        HashMap<FeatureExtractor,HashMap<FeatureDependency,FeatureExtractor>> dependencyMap,
                                                        HashMap<FeatureExtractor,HashMap<Integer,Integer>> depOffsetList){
        // Generate dependencies and merge

        // newest set of dependencies
        HashSet<FeatureExtractor> expanded = new HashSet<FeatureExtractor>();

        // set of all features to extract
        HashSet<FeatureExtractor> current = new HashSet<FeatureExtractor>();
        current.addAll(featuresToExtract);

        // set of all features without dependencies (leaves)
        HashSet<FeatureExtractor> baseFeatures = new HashSet<FeatureExtractor>();
        baseFeatures.addAll(featuresToExtract);

        // set of features known not to be base features (branches)
        HashSet<FeatureExtractor> nonBase = new HashSet<FeatureExtractor>();

        HashMap<FeatureExtractor,HashMap<FeatureExtractor,Integer>> dependencyGraph = new HashMap<FeatureExtractor,HashMap<FeatureExtractor,Integer>>();

        HashMap<FeatureExtractor,HashMap<FeatureExtractor,Integer>> inverseDependencyGraph = new HashMap<FeatureExtractor,HashMap<FeatureExtractor,Integer>>();

        HashMap<FeatureExtractor,HashMap<FeatureExtractor,Integer>> offsetSet = new HashMap<FeatureExtractor,HashMap<FeatureExtractor, Integer>>();

        do{
            current.addAll(expanded);
            expanded.clear();
            for(FeatureExtractor f : featuresToExtract){
                if(f.getDependencies().size()>0){
                    dependencyMap.put(f,new HashMap<FeatureDependency, FeatureExtractor>());
                    dependencyGraph.put(f, new HashMap<FeatureExtractor,Integer>());
                    offsetSet.put(f,new HashMap<FeatureExtractor,Integer>());
                    baseFeatures.remove(f);
                }
                for(FeatureDependency d : f.getDependencies()){
                    FeatureExtractor next = d.get();
                    dependencyMap.get(f).put(d,next);

                    expanded.add(next);
                    if(dependencyGraph.get(f).containsKey(next)){
                        dependencyGraph.get(f).put(next,Math.max(d.getOffset(),dependencyGraph.get(f).get(next)));
                    }else{
                        dependencyGraph.get(f).put(next,dependencyGraph.get(f).get(next));
                    }
                    if(!inverseDependencyGraph.containsKey(next)){
                        inverseDependencyGraph.put(next,new HashMap<FeatureExtractor,Integer>());
                    }
                    inverseDependencyGraph.get(next).put(f,d.getOffset());
                    if(!nonBase.contains(next)){
                        baseFeatures.add(next);
                    }
                }
            }
        }while(expanded.size()>0);

        HashMap<FeatureExtractor,HashSet<FeatureExtractor>> copyOfDependencyGraph = new HashMap<FeatureExtractor,HashSet<FeatureExtractor>>();
        for(FeatureExtractor f: dependencyGraph.keySet()){
            copyOfDependencyGraph.put(f,new HashSet<FeatureExtractor>());
            for(FeatureExtractor g: dependencyGraph.get(f).keySet()){
                copyOfDependencyGraph.get(f).add(g);
            }
        }

        LinkedList<FeatureExtractor> ret = new LinkedList<FeatureExtractor>();

        // populate the offset table
        for(FeatureExtractor f : current){
            offsets.put(f,0);
        }

        // Create an ordered list of these features and a map of dependencies
        int dependencyCount=0;
        while(ret.size()<current.size()){
            mapping.put(dependencyCount,new HashSet<FeatureExtractor>());
            HashSet<FeatureExtractor> nextBases = new HashSet<FeatureExtractor>();
            for(FeatureExtractor f : baseFeatures){
                ret.add(f);
                mapping.get(dependencyCount).add(f);
                current.remove(f);
                if(inverseDependencyGraph.containsKey(f)) {
                    for (FeatureExtractor parent : inverseDependencyGraph.get(f).keySet()) {
                        if(dependencyGraph.get(parent).get(f)+offsets.get(f) > offsets.get(parent)){
                            offsets.put(parent,dependencyGraph.get(parent).get(f)+offsets.get(f));
                        }
                        copyOfDependencyGraph.get(parent).remove(f);
                        if(copyOfDependencyGraph.get(parent).isEmpty()){
                            nextBases.add(parent);
                        }
                    }
                }
            }
            baseFeatures.clear();
            baseFeatures.addAll(nextBases);
            nextBases.clear();
            dependencyCount++;
        }
        for(FeatureExtractor f : ret) {
            depOffsetList.put(f, new HashMap<Integer, Integer>());
        }
        int index=0;
        for(FeatureExtractor f : ret){
            indexInExtraction.put(f,index);
            for(FeatureExtractor parent : inverseDependencyGraph.get(f).keySet()){
                depOffsetList.get(parent).put(index,dependencyGraph.get(parent).get(f));
            }
            index++;
        }

        return ret.toArray(new FeatureExtractor[]{});
    }

	/**
	 * Establish a listener for periodic updates on the feature extraction
	 * progress.
	 * 
	 * @param u
	 */
	public void setUpdater(Updater u) {
		this.updater = u;
	}

	public void validateFile(String definitions, String values)
			throws Exception {
		File feature_values_save_file = new File(values);
		File feature_definitions_save_file = new File(definitions);

		// Throw an exception if the given file paths are not writable. Involves
		// creating a blank file if one does not already exist.
		if (feature_values_save_file.exists())
			if (!feature_values_save_file.canWrite())
				throw new Exception("Cannot write to " + values + ".");
		if (feature_definitions_save_file.exists())
			if (!feature_definitions_save_file.canWrite())
				throw new Exception("Cannot write to " + definitions + ".");

	}

}
