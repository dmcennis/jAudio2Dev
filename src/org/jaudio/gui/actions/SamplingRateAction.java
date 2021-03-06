package org.jaudio.gui.actions;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.ResourceBundle;

/**
 * Action handling changes in the sample rate.
 * 
 * @author Daniel McEnnis
 */
public class SamplingRateAction extends AbstractAction {

	static final long serialVersionUID = 1;

	private int sampleRateIndex = 2;

	/**
	 * references to the radio buttons representing the range of possible sample
	 * rates.
	 */
	public JRadioButtonMenuItem[] samplingRates;

	/**
	 * stores an index to the currently selected sampling rate.
	 */
	public void actionPerformed(ActionEvent e) {
		for (int i = 0; i < samplingRates.length; ++i) {
			if (e.getSource().equals(samplingRates[i])) {
				sampleRateIndex = i;
			}
		}
	}

	/**
	 * Sets references to the RadioButtons for sampling rate.
	 * 
	 * @param s array of menu buttons describing the sample rate
	 */
	public void setTarget(JRadioButtonMenuItem[] s) {
		samplingRates = new JRadioButtonMenuItem[s.length];
		for (int i = 0; i < samplingRates.length; ++i) {
			samplingRates[i] = s[i];
		}
	}

	/**
	 * Return the integer index into the array of radio buttons that corresponds
	 * to the selected sampling rate.
	 * 
	 * @return the index to the selected sampling rate
	 */
	public int getSelected() {
		return sampleRateIndex;
	}

	/**
	 * Returns the sample rate as an double representing samples per second.
	 * @return sampling rate as a double
	 */
	public double getSamplingRate() {
		double base = Double.parseDouble(samplingRates[sampleRateIndex]
				.getText());
		return base * 1000;
	}

	/**
	 * Allows the initial settings of the radio buttons.
	 * @param i which button is to be selected.
	 */
	public void setSelected(int i) {
		if ((i < 0) || (i >= samplingRates.length)) {
			ResourceBundle bundle = ResourceBundle.getBundle("Translations");
			System.err.println(String.format(bundle.getString("internal.error.d.does.not.correspond.to.any.sampling.rate.index"),i));
		} else {
			samplingRates[i].setSelected(true);
			sampleRateIndex = i;
		}
	}

}
