package org.jaudio.dsp;

import jAudioFeatureExtractor.jAudioTools.AudioMethodsPlayback;
import jAudioFeatureExtractor.jAudioTools.FeatureProcessor;
import org.dynamicfactory.descriptors.*;
import org.dynamicfactory.descriptors.Properties;
import org.dynamicfactory.property.Property;
import org.jaudio.Cancel;
import org.jaudio.ModelListener;
import org.jaudio.Updater;
import org.jaudio.dsp.aggregators.Aggregator;
import org.jaudio.dsp.aggregators.AggregatorContainer;
import org.jaudio.dsp.aggregators.AggregatorFactory;
import org.jaudio.dsp.aggregators.ZernikeMoments;
import org.jaudio.dsp.features.*;

import java.io.File;
import java.io.OutputStream;
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
	public AggregatorContainer container;

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

//	public OutputStream featureKey = null;

//	public OutputStream featureValue = null;

	private TreeMap<Integer,HashSet<FeatureExtractor>> mapping = new TreeMap<Integer,HashSet<FeatureExtractor>>();


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
		// extractors.add(new AreaMoments());
		// def.add(false);
		// extractors.add(new BeatHistogram());
		// def.add(false);
		// extractors.add(new BeatHistogramLabels());
		// def.add(false);
		// extractors.add(new BeatSum());
		// def.add(false);
		// extractors.add(new Compactness());
		// def.add(true);
		// extractors.add(new FFTBinFrequencies());
		// def.add(false);
		// extractors.add(new FractionOfLowEnergyWindows());
		// def.add(true);
		// extractors.add(new HarmonicSpectralCentroid());
		// def.add(false);
		// extractors.add(new HarmonicSpectralFlux());
		// def.add(false);
		// extractors.add(new HarmonicSpectralSmoothness());
		// def.add(false);
		// extractors.add(new LPC());
		// def.add(false);
		// extractors.add(new MagnitudeSpectrum());
		// def.add(false);
		// extractors.add(new MFCC());
		// def.add(true);
		// extractors.add(new Moments());
		// def.add(true);
		// extractors.add(new PeakFinder());
		// def.add(false);
		// extractors.add(new PowerSpectrum());
		// def.add(false);
		// extractors.add(new RelativeDifferenceFunction());
		// def.add(false);
		// extractors.add(new RMS());
		// def.add(true);
		// extractors.add(new SpectralCentroid());
		// def.add(true);
		// extractors.add(new SpectralFlux());
		// def.add(true);
		// extractors.add(new SpectralRolloffPoint());
		// def.add(true);
		// extractors.add(new SpectralVariability());
		// def.add(false);
		// extractors.add(new StrengthOfStrongestBeat());
		// def.add(false);
		// extractors.add(new StrongestBeat());
		// def.add(false);
		// extractors.add(new StrongestFrequencyVariability());
		// def.add(false);
		// extractors.add(new StrongestFrequencyViaFFTMax());
		// def.add(false);
		// extractors.add(new StrongestFrequencyViaSpectralCentroid());
		// def.add(false);
		// extractors.add(new StrongestFrequencyViaZeroCrossings());
		// def.add(false);
		// extractors.add(new ZeroCrossings());
		// def.add(true);
//		try {
//			// replaced by the Factory object...
//			Collection<String> types = FeatureFactory.getInstance().getKnownTypes();
//            Collection<String> metaTypes = MetaFeatureFactoryFactory.getInstance().getKnownTypes();
//            for(String type : types){
//                extractors.add(FeatureFactory.getInstance().create(type));
//            }
//            for(String type : metaTypes){
//
//            }
//			Collection<String> aggArray = AggregatorFactory.getInstance().getKnownTypes();
//            for(String type : aggArray){
//                aggregatorMap.put(type,AggregatorFactory.getInstance().create(type));
//		}
//		} catch (Exception e) {
//			e.printStackTrace();
//			System.exit(1);
//		}
//		populateMetaFeatures(metaExtractors, extractors, def);
	}

	void populateMetaFeatures(LinkedList<MetaFeatureFactory> listMFF,
			LinkedList<FeatureExtractor> listFE, LinkedList<Boolean> def) {
		LinkedList<Boolean> tmpDefaults = new LinkedList<Boolean>();
		LinkedList<FeatureExtractor> tmpFeatures = new LinkedList<FeatureExtractor>();
		LinkedList<Boolean> isPrimaryList = new LinkedList<Boolean>();
		Iterator<FeatureExtractor> lFE = listFE.iterator();
		Iterator<Boolean> lD = def.iterator();
		while (lFE.hasNext()) {
			FeatureExtractor tmpF = lFE.next();
			Boolean tmpB = lD.next();
			tmpFeatures.add(tmpF);
			tmpDefaults.add(tmpB);
			isPrimaryList.add(new Boolean(true));
			tmpF.setParent(this);
			if (tmpF.getFeatureDefinition().getDimensions() != 0) {
				Iterator<MetaFeatureFactory> lM = listMFF.iterator();
				while (lM.hasNext()) {
					MetaFeatureFactory tmpMFF = lM.next();
					FeatureExtractor tmp = tmpMFF
							.defineFeature((FeatureExtractor) tmpF.clone());
					tmp.setParent(this);
					tmpFeatures.add(tmp);
					tmpDefaults.add(new Boolean(false));
					isPrimaryList.add(new Boolean(false));
				}
			}
		}
//		this.features = tmpFeatures.toArray(new FeatureExtractor[1]);
//		Boolean[] defaults_temp = tmpDefaults.toArray(new Boolean[1]);
//		Boolean[] is_primary_temp = isPrimaryList.toArray(new Boolean[] {});
//		this.defaults = new boolean[defaults_temp.length];
//		is_primary = new boolean[defaults_temp.length];
//		for (int i = 0; i < this.defaults.length; i++) {
//			this.defaults[i] = defaults_temp[i].booleanValue();
//			is_primary[i] = is_primary_temp[i].booleanValue();
//		}
//		this.featureDefinitions = new FeatureDefinition[this.defaults.length];
//		for (int i = 0; i < this.featureDefinitions.length; ++i) {
//			this.featureDefinitions[i] = features[i].getFeatureDefinition();
//		}

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

        LinkedList<FeatureExtractor> list = orderFeatureExtraction();

        if(!checks()){
            throw new Exception("There was an unknown error during validation the audio data.");
        }

        if(save_overall_recording_features){
            container.process(info,samplingRate);
        }else{
            extract(info, list, samplingRate, windowSize, windowOverlap);
        }
        //FIXME: Finish propogating changes through processing stacks
//		container.add(features,defaults);
//		// Prepare to extract features
//		FeatureProcessor processor = new FeatureProcessor(window_size,
//				window_overlap, sampling_rate, normalise, this.features,
//				this.defaults, save_features_for_each_window,
//				save_overall_recording_features, featureValue, featureKey,
//				outputType, cancel_, container);
//
//		// Extract features from recordings one by one and save them in XML
//		// files
////		AudioSamples recording_content;
//		for (int i = 0; i < recordings.length; i++) {
//			File load_file = new File(recordings[i].file_path);
//			if (updater != null) {
//				updater.announceUpdate(i, 0);
//			}
//			processor.extractFeatures(load_file, updater);
//		}

		// Finalize saved XML files

//		processor.finalize();

		// JOptionPane.showMessageDialog(null,
		// "Features successfully extracted and saved.", "DONE",
		// JOptionPane.INFORMATION_MESSAGE);
	}



    protected void extract(RecordingInfo[] info, LinkedList<FeatureExtractor> features, double samplingRate, int windowSize, double windowOverlap){
        //FIXME: Complete extraction Code
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

    protected LinkedList<FeatureExtractor> orderFeatureExtraction(){
        //FIXME: Complete Ordering code
        return null;
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
