package org.jaudio.gui.actions;

import jAudioFeatureExtractor.Controller;
import jAudioFeatureExtractor.jAudioTools.AudioMethods;
import jAudioFeatureExtractor.jAudioTools.AudioMethodsPlayback;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ResourceBundle;

/**
 * Play a file from a reference. This playback can be stopped.
 * 
 * @author Daniel McEnnis
 */
public class PlayNowAction extends AbstractAction {

	static final long serialVersionUID = 1;

	private Controller controller;

	private JTable recordings_table;

	/**
	 * Constructor that sets menu text and keeps a reference to the controller
	 * 
	 * @param c
	 *            near global controller
	 */
	public PlayNowAction(Controller c) {
		super("Play From File...");
		controller = c;
	}

	public PlayNowAction() {

	}

	/**
	 * Sets references to the recording table. Needed to find out which
	 * recording to play.
	 * 
	 * @param recordings_table
	 *            table containing the list of recordings.
	 */
	public void setTable(JTable recordings_table) {
		this.recordings_table = recordings_table;
	}

	/**
	 * Play back the file.
	 */
	public void actionPerformed(ActionEvent e) {
		try {
			// Get the file selected for playback
			ResourceBundle bundle = ResourceBundle.getBundle("Translations");
			int selected_row = recordings_table.getSelectedRow();
			if (selected_row < 0)
				throw new Exception(bundle.getString("no.file.selcected.for.playback"));
			File play_file = new File(
					controller.dm_.recordingInfo[selected_row].file_path);
			// Perform playback of the file
			try {
				// Get the AudioInputStream from the file and the SourceDataLine
				// to
				// play to from the system
				AudioInputStream audio_input_stream = AudioSystem
						.getAudioInputStream(play_file);
				audio_input_stream = AudioMethods
						.convertUnsupportedFormat(audio_input_stream);
				SourceDataLine source_data_line = AudioMethods
						.getSourceDataLine(audio_input_stream.getFormat(), null);

				// Stop any previous playback
				((StopPlayBackAction)(ControllerFactory.get("StopPlayBack"))).stopPlayback();

				// Begin playback
				controller.dm_.playback_thread = AudioMethodsPlayback
						.playAudioInputStreamInterruptible(audio_input_stream,
								source_data_line);
			} catch (UnsupportedAudioFileException ex) {
				throw new Exception(String.format(bundle.getString("file.s.has.an.unsupported.audio.format"),play_file.getName()));
			} catch (Exception ex) {
				throw new Exception(String.format(bundle.getString("file.s.is.not.playable.ns"),play_file.getName(),ex.getMessage()));
			}
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(null, ex.getMessage(), "ERROR",
					JOptionPane.ERROR_MESSAGE);
		}
	}

}
