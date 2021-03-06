package org.jaudio.gui.actions;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.ResourceBundle;

/**
 * Action responsible for processing when a user changes from either ACE or ARFF
 * output format from the menu bar.
 * 
 * @author Daniel McEnnis
 */
public class OutputTypeAction extends AbstractAction {

	static final long serialVersionUID = 1;

	private int outputType = 0;

	private JRadioButtonMenuItem ace;

	private JRadioButtonMenuItem arff;

	private JCheckBox perWindow;

	private JCheckBox overall;

	/**
	 * When an output type is selected, change the output type to match and
	 * check against inconsistent states.
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().compareTo("ACE")==0) {
			outputType = 0;
		}
		if (e.getActionCommand().compareTo("ARFF")==0) {
			checkConsistantState();
			outputType = 1;
		}
	}

	/**
	 * Sets all the references needed to check for invalid state combinations
	 * 
	 * @param ace
	 *            "ACE" radio button
	 * @param arff
	 *            "ARFF" radio button
	 * @param perWindow
	 *            should features be saved for each window
	 * @param overall
	 *            should global features be saved
	 */
	public void setTarget(JRadioButtonMenuItem ace, JRadioButtonMenuItem arff,
			JCheckBox perWindow, JCheckBox overall) {
		this.ace = ace;
		this.arff = arff;
		this.perWindow = perWindow;
		this.overall = overall;
	}

	/**
	 * Provide a human friendly version of the outputType variable
	 * 
	 * @return either ACE or ARFF depending on the currently selected value.
	 */
	public String getOutputType() {
		if (outputType == 0) {
			return "ACE";
		} else {
			return "ARFF";
		}
	}

	/**
	 * Return the integer value corresponding to the current output type
	 * selected
	 * 
	 * @return integer value corresponding to the currently selected output type
	 */
	public int getSelected() {
		return outputType;
	}

	/**
	 * Set the current selected state. Invalid values are mapped to ARFF.
	 * 
	 * @param i integer representing the desired output type.
	 */
	public void setSelected(int i) {
		if (i == 0) {
			ace.setSelected(true);
			outputType = 0;
		} else {
			checkConsistantState();
			arff.setSelected(true);
			outputType = 1;
		}
	}

	private void checkConsistantState() {
		if (perWindow.isSelected() && overall.isSelected()) {
			ResourceBundle bundle = ResourceBundle.getBundle("Translations");
			JOptionPane
					.showMessageDialog(
							null,
							bundle.getString("weka.format.only.supports.one.type.of.output.either.output.per.file.or.output.per.window"),
							"ERROR", JOptionPane.ERROR_MESSAGE);
			overall.setSelected(false);
		}
	}
}
