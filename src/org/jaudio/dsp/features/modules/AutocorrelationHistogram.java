/*
 * @(#)RMS.java	0.5	1.0	April 5, 2005.
 *
 * McGill Univarsity
 */

package org.jaudio.dsp.features.modules;

import org.dynamicfactory.descriptors.*;
import org.dynamicfactory.propertyQuery.NumericQuery;
import org.jaudio.dsp.features.FeatureDefinition;
import org.jaudio.dsp.features.FeatureExtractor;
import org.jaudio.dsp.features.MetaFeatureFactory;

import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Extends Cory McKay's RMS to perfor separately over all input dimensions.
 * A feature extractor that extracts the Root Mean Square (RMS) from a set of
 * samples on every dimension. This is a good measure of the power of a signal.
 *
 * <p>RMS is calculated by summing the squares of each sample, dividing this
 * by the number of samples in the window, and finding the square root of the
 * result.
 *
 * <p>No extracted feature values are stored in objects of this class.
 *
 * @author Cory McKay
 */
public class AutocorrelationHistogram
	extends MetaFeatureFactory
{
    FeatureExtractor child;
    @Override
    public FeatureExtractor prototype() {
        return this;
    }

    @Override
    public FeatureExtractor prototype(Properties props) {
        if(quickCheck("Feature",FeatureExtractor.class)){
            AutocorrelationHistogram m = new AutocorrelationHistogram();
            m.child = buildChild(props);
            for(Parameter p: this.definition.getParameters()){
                if(props.quickCheck(p.getType(),p.getParameterClass())){
                    m.definition.set(p.getType(),p.getValue());
                }
            }

            return m;
        }else{
            Logger.getLogger(this.getClass().getName()).log(Level.SEVERE,"Attempting to create AutocorrelationHistogram without providing a child feature");
            return null;
        }
    }

 //   int rmsWindowLength=256;

 //   int windowLength=256*256;

	/* CONSTRUCTOR **************************************************************/

    public AutocorrelationHistogram()
    {
        super();
        ParameterInternal param = ParameterFactory.newInstance().create("RMSWindowLength",Integer.class,"The number of windows to calculate a mean across.");
        param.setLongDescription("");
        param.setRestrictions(SyntaxCheckerFactory.newInstance().create(1,1,(new NumericQuery()).buildQuery(0.0,false, NumericQuery.Operation.GT),Integer.class));
        param.set(256);
        add(param);
        param = ParameterFactory.newInstance().create("WindowLength",Integer.class,"The number of windows to calculate a mean across.");
        param.setLongDescription("");
        param.setRestrictions(SyntaxCheckerFactory.newInstance().create(1,1,(new NumericQuery()).buildQuery(0.0,false, NumericQuery.Operation.GT),Integer.class));
        param.set(256*100);
        add(param);

    }

	/**
	 * Basic constructor that sets the definition and dependencies (and their
	 * offsets) of this feature.
	 */
	public AutocorrelationHistogram(MetaFeatureFactory mff)
	{
        super();
        this.chainMetaFeatureFactory(mff);

    }


	/* PUBLIC METHODS **********************************************************/


	/**
	 * Extracts this feature from the given samples at the given sampling
	 * rate and given the other feature values.
	 *
	 * <p>In the case of this feature, the sampling_rate and
	 * other_feature_values parameters are ignored.
	 *
	 * @param samples				The samples to extract the feature from.
	 * @param sampling_rate			The sampling rate that the samples are
	 *								encoded with.
	 * @param other_feature_values	The values of other features that are
	 *								needed to calculate this value. The
	 *								order and offsets of these features
	 *								must be the same as those returned by
	 *								this class's getDependencies and
	 *								getDependencyOffsets methods respectively.
	 *								The first indice indicates the feature/window
	 *								and the second indicates the value.
	 * @return						The extracted feature value(s).
	 * @throws Exception			Throws an informative exception if
	 *								the feature cannot be calculated.
	 */
	public double[] extractFeature( double[] samples,
	                                double sampling_rate,
	                                double[][] other_feature_values )
		throws Exception
	{
        int rowLength = (int)quickGet("WindowLength") / (int)quickGet("RMSWindowLength");
        double[] result = new double[other_feature_values.length*rowLength];
        for(int dim = 0;dim < other_feature_values.length;++dim) {
            double[] rowResult = new double[rowLength];
            // build the RMS array
            for (int samp = 0; samp < rowLength; ++samp) {
                double sum = 0.0;
                for (int window = 0; window < (int)quickGet("RMSWindowLength"); window++) {
                    sum += Math.pow(other_feature_values[dim][samp * (int)quickGet("RMSWindowLength") + window], 2);
                    double rms = Math.sqrt(sum / samples.length);
                    rowResult[samp] = rms;
                }
            }

            // perform auto correlation
            double[] auto_correlation = jAudioFeatureExtractor.jAudioTools.DSPMethods
                    .getAutoCorrelation(rowResult, 0, rowResult.length-1);
            for(int i=0;i<auto_correlation.length;++i){
                result[dim*rowLength+i]=auto_correlation[i];
            }

        }
		return result;
	}

    /**
     * Function permitting an unintelligent outside function (ie. EditFeatures
     * frame) to set the default values used to popylate the table's entries.
     * Like getElement, the correct index values are inferred from the
     * definition.getAttributes() value.
     * <p>
     * As a metafeature, recursively calls children to set the feature
     * requested.
     *
     * @param index
     *            attribute to be set
     * @param value
     *            new value of the attribute
     */
    public void setElement(int index, String value) throws Exception {
        if ((index >= definition.getAttributes().length) || (index < 0)) {
            ResourceBundle bundle = ResourceBundle.getBundle("Translations");
            throw new Exception(String.format(bundle.getString("internal.error.request.for.an.invalid.index.d4"),index));
        } else if (index == definition.getAttributes().length - 2) {
            try {
                int type = Integer.parseInt(value);
                if (type <= (int)quickGet("RMSWindowLength")) {
                    ResourceBundle bundle = ResourceBundle.getBundle("Translations");
                    throw new Exception( String.format(bundle.getString("the.new.window.length.must.be.greater.than.one.and.less.than.the.window.length.d"),(int)quickGet("RMSWindowLength")));
                } else {
                    setWindow(type);
                }
            } catch (NumberFormatException e) {
                ResourceBundle bundle = ResourceBundle.getBundle("Translations");
                throw new Exception(bundle.getString("width.of.window.must.be.an.integer"));
            }
        } else if (index == definition.getAttributes().length - 1) {
            try {
                int type = Integer.parseInt(value);
                if (type <= (int)quickGet("WindowLength")) {
                    ResourceBundle bundle = ResourceBundle.getBundle("Translations");
                    throw new Exception( String.format(bundle.getString("the.new.window.length.must.be.greater.than.the.rms.window.length.d"),(int)quickGet("RMSWindowLength")));
                } else {
                    setWindow(type);
                }
            } catch (NumberFormatException e) {
                ResourceBundle bundle = ResourceBundle.getBundle("Translations");
                throw new Exception(bundle.getString("width.of.window.must.be.an.integer"));
            }
        } else if (fe_ != null) {
            fe_.setElement(index, value);
        } else {
            ResourceBundle bundle = ResourceBundle.getBundle("Translations");
            throw new Exception(bundle.getString("internal.error.non.existant.index.for.mean.getelement.claims.to.have.children.but.child.is.null1"));
        }
    }

    @Override
    public MetaFeatureFactory defineFeature(FeatureExtractor fe) {
        ResourceBundle bundle = ResourceBundle.getBundle("Translations");
        AutocorrelationHistogram tmp = new AutocorrelationHistogram();
        String name = String.format(bundle.getString("multidimensional.auto.correlation.of.s"),fe);
        String description = "";
        boolean is_sequential = true;
        int dimensions = 0;
        definition = new FeatureDefinition( name,
                description,
                is_sequential,
                dimensions );

        String[] oldAttributes = fe.getFeatureDefinition().getAttributes();
        String[] myAttributes = new String[oldAttributes.length + 2];
        for (int i = 0; i < oldAttributes.length; ++i) {
            myAttributes[i] = oldAttributes[i];
        }
        myAttributes[myAttributes.length - 2] = bundle.getString("the.number.of.windows.of.samples.across.time.to.calculate.root.mean.square.across.this.implicitly.defines.the.granularity.in.time.of.the.autocorrelation.of.each.dimension");
        myAttributes[myAttributes.length - 1] = bundle.getString("the.number.of.windows.of.samples.across.time.to.calculate.correlations.across.this.with.rms.window.implicitly.defines.the.maximum.distance.in.time.auto.correlation.is.calculated.for");

        tmp.definition = new FeatureDefinition(name, description, true, fe
                .getFeatureDefinition().getDimensions(), myAttributes);
        tmp.definition.setDependency(fe.getFeatureDefinition().getName(),0,(int)quickGet("RMSWindowLength"));
        return tmp;
    }

    /**
     * Changes the number of dependant samples extracted for each object.
     *
     * @param n
     *            number of samples that should be included in the running
     *            average.
     * @throws Exception
     *             thrown if n is equal to or less than one
     */
    public void setWindow(int n) throws Exception {
        if (n <= 1) {
            ResourceBundle bundle = ResourceBundle.getBundle("Translations");
            throw new Exception(
                    bundle.getString("the.new.value.for.te.length.of.the.window.should.be.greater.than.one"));
        } else {
            set("RMSWindowLength",n);
            if (fe_ != null) {
                String tmp = fe_.getFeatureDefinition().getName();
                definition.setDependency(tmp,0,(int)quickGet("WindowLength"));
            }

        }
        super.setWindow(n);
    }

    /**
     * Function permitting an unintelligent outside function (ie. EditFeatures
     * frame) to get the default values used to populate the table's entries.
     * The correct index values are inferred from definition.attribute value.
     * <p>
     * As a metafeature, recursively calls children for the feature requested.
     *
     * @param index
     *            which of AreaMoment's attributes should be edited.
     */
    public String getElement(int index) throws Exception {
        ResourceBundle bundle = ResourceBundle.getBundle("Translations");
        if ((index >= definition.getAttributes().length) || (index < 0)) {
            throw new Exception(String.format(bundle.getString("internal.error.request.for.an.invalid.index.d3"),index));
        } else if (index == definition.getAttributes().length - 2) {
            return Integer.toString((int)quickGet("RMSWindowLength"));
        } else if (index == definition.getAttributes().length - 1) {
            return Integer.toString((int)quickGet("WindowLength"));
        } else if (fe_ != null) {
            return fe_.getElement(index);
        } else {
            throw new Exception(bundle.getString("internal.error.non.existant.index.for.mean.getelement.claims.to.have.children.but.child.is.null"));
        }
    }

    /**
     * Create an identical copy of this feature. This permits FeatureExtractor
     * to use the prototype pattern to create new composite features using
     * metafeatures.
     */
//    public Object clone() {
//        if(fe_ == null){
//            return new Mean();
//        }
//        if (this.fe_ instanceof MetaFeatureFactory) {
//            AutocorrelationHistogram ret = new AutocorrelationHistogram();
//            ret.fe_ = (FeatureExtractor)fe_.prototype();
//            try {
//                ret.setWindow(windowLength);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            ret.rmsWindowLength = rmsWindowLength;
//            ret.windowLength = windowLength;
//            String name = definition.getName();
//            String description = definition.getDescription();
//            String[] attributes = definition.getAttributes();
//            int dim = definition.getDimensions();
//            ret.definition = new FeatureDefinition(name,description,true,dim,attributes);
//
//            ret.definition.setDependency(definition.getDependency());
//
//            try{
//                ret.setWindow(windowLength);
//            }catch(Exception e){
//                e.printStackTrace();
//            }
//            return ret;
//        } else {
//            AutocorrelationHistogram ret = (AutocorrelationHistogram) defineFeature((FeatureExtractor) fe_.clone());
//            try {
//                ret.setWindow(windowLength);
//                ret.rmsWindowLength = rmsWindowLength;
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            return ret;
//        }
//    }

}