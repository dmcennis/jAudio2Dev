/**
 * Created by Daniel McEnnis on 3/19/2016
 * <p/>
 * Copyright Daniel McEnnis 2015
 */

package org.jaudio.dsp;

import org.dynamicfactory.descriptors.Properties;

/**
 * Default Description Interface AudioInput
 */
public interface AudioInput {
	AudioSamples prototype();

	AudioSamples prototype(Properties props);

	String getRecordingInfo();

	String getUniqueIdentifier();

	float getSamplingRate();

	double getSamplingRateAsDouble();

	int getNumberSamplesPerChannel();

	double getDuration();

	int getNumberChannels();

	double[] getSamplesMixedDown();

	double[][] getSamplesChannelSegregated();

	double[][] getSamplesChannelSegregated(int start_sample, int end_sample)
		throws Exception;

	double[][] getSamplesChannelSegregated(double start_time, double end_time)
            throws Exception;

	double[][][] getSampleWindowsChannelSegregated(int window_size)
                throws Exception;

	double[][][] getSampleWindowsChannelSegregated(double window_duration)
                    throws Exception;

	void normalizeIfClipped();

	double getMaximumAmplitude();

	double checkMixedDownSamplesForClipping();

	double checkChannelSegregatedSamplesForClipping();

	void normalizeMixedDownSamples();

	void normalizeChannelSegretatedSamples();

	void normalize();

	void setSamples(double[][] new_samples)
		throws Exception;
}
