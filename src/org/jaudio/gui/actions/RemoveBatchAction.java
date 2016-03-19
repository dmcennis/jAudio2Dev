package org.jaudio.gui.actions;

import jAudioFeatureExtractor.Controller;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ResourceBundle;

/**
 * Removes the selected batch from the set of batch files currently loaded. Can
 * not be executed if there are no batches to be removed.
 * 
 * @author Daniel McEnnis
 */
public class RemoveBatchAction extends AbstractAction {

	static final long serialVersionUID = 1;

	private Controller controller;

	/**
	 * Constructor that sets the menu text and stores a reference to the controller.
	 * @param c near global controller.
	 */
	public RemoveBatchAction(Controller c) {
		super(ResourceBundle.getBundle("Translations").getString("remove.batch"));
		controller = c;
	}

	public RemoveBatchAction() {

	}

	/**
	 * Removes the selected batch from the list of stored batches.
	 */
	public void actionPerformed(ActionEvent e) {
		int count = 0;

		Component src = ((JMenu)(e.getSource())).getMenuComponent(count);
		String action = "";
		if (src instanceof JMenuItem) {
			action = ((JMenuItem) src).getActionCommand();
		}

		while (e.getActionCommand().compareTo(action)!=0) {
			src = controller.removeBatch.getMenuComponent(++count);
			if (src instanceof JMenuItem) {
				action = ((JMenuItem) src).getActionCommand();
			}
		}
		controller.batches.remove(count);
		controller.removeBatch.remove(count);
		controller.viewBatch.remove(count);
		if (controller.batches.size() == 0) {
			controller.removeBatch.setEnabled(false);
			controller.viewBatch.setEnabled(false);
		}
	}

}
