/**
 * Created by Daniel McEnnis on 2/23/2016
 * <p/>
 * Copyright Daniel McEnnis 2015
 */

package org.jaudio.gui;

import jAudioFeatureExtractor.Aggregators.Aggregator;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;

/**
 * Default Description Google Interview Project
 */
public class AggregatorEntry extends DefaultMutableTreeNode {


    private Aggregator aggregator;

    /**
     * Default constructor for AggregatorEntry
     */
    public AggregatorEntry() {

    }

    /**
     * Provide the icon loaded in the constructor.
     *
     * @see org.multihelp.file.FileNode#getIcon()
     */
    public Icon getIcon() {
        return null;
    }

    /**
     * Provides the text loaded in the constructor.
     *
     * @see org.multihelp.file.FileNode#getText()
     */
    public String getText() {
        return "Default";
    }

    /**
     * Rendering all entries of this FileNode type.  Default returns null.
     */
    public Component render(JTree tree, Object value,
                            boolean sel, boolean expanded, boolean leaf, int row,
                            boolean hasFocus){
        return null;
    }

}
