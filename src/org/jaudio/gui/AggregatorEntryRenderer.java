package org.jaudio.gui;

import org.multihelp.file.FileNode;

import javax.swing.*;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.*;

/**
 * Created by dmcennis on 4/4/2016.
 */
public class AggregatorEntryRenderer extends DefaultTreeCellRenderer {

    public AggregatorEntryRenderer(){
        super();
    }
    /**
     * Get the component used to display the current row. Used for both directories and leaf nodes.
     *
     * @see javax.swing.tree.DefaultTreeCellRenderer#getTreeCellRendererComponent(javax.swing.JTree,
     *      java.lang.Object, boolean, boolean, boolean, int, boolean)
     */
    public Component getTreeCellRendererComponent(JTree tree, Object value,
                                                  boolean sel, boolean expanded, boolean leaf, int row,
                                                  boolean hasFocus) {
//		this.setLeafIcon(defaultLeaf);
        try {
            Component ret = ((FileNode) value).render(tree, value, sel,
                    expanded, leaf, row, hasFocus);
            if (ret != null) {
                return ret;
            }

            if (!leaf) {
//				System.out.println("Default rendering used ");
                return super.getTreeCellRendererComponent(tree,((FileNode)value).getText(),sel,expanded,leaf,row,hasFocus);
            } else {
                Icon icon = ((FileNode) value).getIcon();
                String text = ((FileNode) value).getText();
                JLabel label = new JLabel();

                if (icon != null) {
                    label.setIcon(icon);
                }else{
                    label.setIcon(super.getLeafIcon());
                }
                label.setText(text);
                ret = label;
                return ret;
            }
        } catch (ClassCastException e) {
            return super.getTreeCellRendererComponent(tree, value, sel, expanded,
                    leaf, row, hasFocus);
        }
    }

}
