package org.jaudio.gui.actions;

import javax.swing.*;
import java.util.HashMap;

/**
 * Created by Daniel McEnnis on 2/25/2016
 * <p/>
 * Copyright Daniel McEnnis 2015
 */
public class ControllerFactory {

    private HashMap<String,AbstractAction> actionMap = new HashMap<String,AbstractAction>();

    private static ControllerFactory ourInstance = new ControllerFactory();

    public static ControllerFactory getInstance() {
        return ourInstance;
    }

    private ControllerFactory() {
        actionMap.put("Copy",new CopyAction());
        actionMap.put("Cut",new CutAction());
        actionMap.put("EditRecording",new EditRecordingsAction(null));
        actionMap.put("ExecuteBatch",new ExecuteBatchAction());
        actionMap.put("Exit",new ExitAction());
        actionMap.put("GlobalWindowChange",new GlobalWindowChangeAction(null));
        actionMap.put("Load",new LoadAction());
        actionMap.put("LoadBatch",new LoadBatchAction());
        actionMap.put("MultipleToggle",new MultipleToggleAction());
        actionMap.put("OutputType",new OutputTypeAction());
        actionMap.put("Paste",new PasteAction());
        actionMap.put("PlayMIDI",new PlayMIDIAction());
        actionMap.put("PlayNow",new PlayNowAction());
        actionMap.put("PlaySamples",new PlaySamplesAction());
        actionMap.put("RecordFromMic",new RecordFromMicAction());
        actionMap.put("RemoveBatch",new RemoveBatchAction());
        actionMap.put("RemoveRecording",new RemoveRecordingAction());
        actionMap.put("SamplingRate",new SamplingRateAction());
        actionMap.put("Save",new SaveAction());
        actionMap.put("SaveBatch",new SaveBatchAction());
        actionMap.put("StopPlayback",new StopPlayBackAction());
        actionMap.put("Synthesize",new SynthesizeAction());
        actionMap.put("ViewBatch",new ViewBatchAction());
        actionMap.put("ViewFileInfo",new ViewFileInfoAction());

    }


    static public AbstractAction get(String action) {
        return ourInstance.getInstance().actionMap.get(action);
    }

    static public void register(String name, AbstractAction action){
        ourInstance.getInstance().actionMap.put(name,action);
    }
}
