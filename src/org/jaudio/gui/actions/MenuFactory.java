package org.jaudio.gui.actions;

import org.dynamicfactory.AbstractFactory;
import org.dynamicfactory.descriptors.Properties;

import javax.swing.*;
import java.util.ResourceBundle;

/**
 * Created by Daniel McEnnis on 3/18/2016
 * <p/>
 * Copyright Daniel McEnnis 2015
 */
public class MenuFactory extends AbstractFactory<JMenu>{
    private static MenuFactory ourInstance = new MenuFactory();

    public static MenuFactory getInstance() {
        return ourInstance;
    }

    private MenuFactory() {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("Translations");
        map.put("RemoveBatch",new JMenu(resourceBundle.getString("remove.batch")));
        map.put("ViewBatch",new JMenu(resourceBundle.getString("view.batch")));
        map.put("Edit",new JMenu(resourceBundle.getString("edit")));
        map.put("Recording",new JMenu(resourceBundle.getString("recording")));
        map.put("Analysis",new JMenu(resourceBundle.getString("analysis")));
        map.put("OutputFormat",new JMenu(resourceBundle.getString("output.format")));
        map.put("SampleRate",new JMenu(resourceBundle.getString("sample.rate.khz")));
        map.put("Playback",new JMenu(resourceBundle.getString("playback")));
        map.put("Help",new JMenu(resourceBundle.getString("help")));
    }

    @Override
    public JMenu create(Properties props) {
        return map.get(props.get("ClassName"));
    }
}
