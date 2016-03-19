/**
 * Created by Daniel McEnnis on 2/23/2016
 * <p/>
 * Copyright Daniel McEnnis 2015
 */

package org.jaudio.gui;

import org.jaudio.gui.actions.ControllerFactory;
import org.jaudio.gui.actions.MenuFactory;
import org.jaudio.gui.actions.SamplingRateAction;
import org.multihelp.HelpWindow;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.util.ResourceBundle;

/**
 * Default Description Google Interview Project
 */
public class JAudio {

    private JButton saveButton;
    private JButton runButton;
    private JButton addButton;
    private JMenuBar mainMenuConstruction;
    private JCheckBoxMenuItem normalise = new JCheckBoxMenuItem(
            ResourceBundle.getBundle("Translations").getString("normalise.recordings"), false);
    private HelpWindow window = null;
    /**
     * Default constructor for JAudio
     */
    public JAudio() {

        ResourceBundle bundle = ResourceBundle.getBundle("Translations");
        runButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

        runButton.setToolTipText(bundle.getString("perform.analysis.on.sources.or.files"));

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

        addButton.setToolTipText(bundle.getString("add.this.aalysis.run.to.a.batch.processing.file"));
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        saveButton.setToolTipText(bundle.getString("save.settings.and.batches.to.a.file"));
    }

    private void createUIComponents() {
        mainMenuConstruction = new JMenuBar();
        ResourceBundle resourceBundle = ResourceBundle.getBundle("Translations");
        JMenu fileMenu = new JMenu(resourceBundle.getString("file"));
        fileMenu.add(ControllerFactory.get("SaveAction"));
        fileMenu.add(ControllerFactory.get("SaveBatch"));
        fileMenu.add(ControllerFactory.get("Load"));
        fileMenu.add(ControllerFactory.get("LoadBatch"));
        fileMenu.addSeparator();
        fileMenu.add(ControllerFactory.get("AddBatch"));
        fileMenu.add(ControllerFactory.get("ExecuteBatch"));
        JMenu removeBatch = MenuFactory.getInstance().create("RemoveBatch");
        removeBatch.setEnabled(false);
        fileMenu.add(ControllerFactory.get("RemoveBatch"));
        JMenu viewBatch = MenuFactory.getInstance().create("ViewBatch");
        viewBatch.setEnabled(false);
        fileMenu.add(ControllerFactory.get("ViewBatch"));
        fileMenu.addSeparator();
        fileMenu.add(ControllerFactory.get("ExitAction"));
        JMenu editMenu = MenuFactory.getInstance().create("Edit");
        editMenu.add(ControllerFactory.get("CutAction"));
        editMenu.add(ControllerFactory.get("CopyAction"));
        editMenu.add(ControllerFactory.get("PasteAction"));
        JMenu recordingMenu = MenuFactory.getInstance().create("Recording");
        recordingMenu.add(ControllerFactory.get("AddRecordingsAction"));
        recordingMenu.add(ControllerFactory.get("EditRecordingsAction"));
        recordingMenu.add(ControllerFactory.get("RemoveRecordingsAction"));
        recordingMenu.add(ControllerFactory.get("RecordFromMicAction"));
        recordingMenu.add(ControllerFactory.get("SynthesizeAction"));
        recordingMenu.add(ControllerFactory.get("ViewFileInfoAction"));
        recordingMenu.add(ControllerFactory.get("StoreSamples"));
        recordingMenu.add(ControllerFactory.get("Validate"));
        JMenu analysisMenu = MenuFactory.getInstance().create("Analysis");
        analysisMenu.add(ControllerFactory.get("GlobalWindowChangeAction"));
        JMenu outputType = MenuFactory.getInstance().create("OutputFormat");
        JRadioButtonMenuItem ace = new JRadioButtonMenuItem();
        ace.setSelected(true);
        ace.addActionListener(ControllerFactory.get("OutputType"));
        JRadioButtonMenuItem arff = new JRadioButtonMenuItem();
        arff.addActionListener(ControllerFactory.get("OutputType"));
        outputType.add(ace);
        outputType.add(arff);
        analysisMenu.add(outputType);
        JMenu sampleRate = MenuFactory.getInstance().create("sampleRate");
        JRadioButtonMenuItem sample8 = new JRadioButtonMenuItem("8");
        JRadioButtonMenuItem sample11 = new JRadioButtonMenuItem("11.025");
        JRadioButtonMenuItem sample16 = new JRadioButtonMenuItem("16");
        JRadioButtonMenuItem sample22 = new JRadioButtonMenuItem("22.05");
        JRadioButtonMenuItem sample44 = new JRadioButtonMenuItem("44.1");
        ButtonGroup sr = new ButtonGroup();
        sr.add(sample8);
        sr.add(sample11);
        sr.add(sample16);
        sr.add(sample22);
        sr.add(sample44);
        sample16.setSelected(true);
        sample8.addActionListener(ControllerFactory.get("SamplingRate"));
        sample11.addActionListener(ControllerFactory.get("SamplingRate"));
        sample16.addActionListener(ControllerFactory.get("SamplingRate"));
        sample22.addActionListener(ControllerFactory.get("SamplingRate"));
        sample44.addActionListener(ControllerFactory.get("SamplingRate"));
        ((SamplingRateAction)(ControllerFactory.get("SamplingRate"))).setTarget(new JRadioButtonMenuItem[] {
                sample8, sample11, sample16, sample22, sample44 });

        sampleRate.add(sample8);
        sampleRate.add(sample11);
        sampleRate.add(sample16);
        sampleRate.add(sample22);
        sampleRate.add(sample44);
        analysisMenu.add(ControllerFactory.get("SampleRate"));
        analysisMenu.add(normalise);
        JMenu playbackMenu = new JMenu(resourceBundle.getString("playback"));
        playbackMenu.add(ControllerFactory.get("PlayNowAction"));
        playbackMenu.add(ControllerFactory.get("PlaySamplesAction"));
        playbackMenu.add(ControllerFactory.get("StopPlayBackAction"));
        playbackMenu.add(ControllerFactory.get("PlayMIDIAction"));
        JMenu helpMenu = MenuFactory.getInstance().create("Help");
        JMenuItem helpTopics = new JMenuItem(resourceBundle.getString("help.topics"));
        helpTopics.addActionListener(new AbstractAction(){

            public void actionPerformed(ActionEvent e) {
                System.out.println(ResourceBundle.getBundle("Translations").getString("help.window.started"));
                if(window == null){
                    window = new HelpWindow();
                }
            }
        });
        helpMenu.add(helpTopics);
        helpMenu.add(ControllerFactory.get("AboutAction"));

    }
}
