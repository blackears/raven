/*
 * Copyright 2011 Mark McKay
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kitfox.raven.util.tree.property;

import com.kitfox.raven.util.tree.NodeObject;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 *
 * @author kitfox
 */
public class NodeObjectCellRenderer extends JLabel
        implements ListCellRenderer
{
    public NodeObjectCellRenderer()
    {
        setOpaque(true);
    }


    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
    {
        if (isSelected)
        {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        }
        else
        {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }

        if (value instanceof String)
        {
            setText((String)value);
            setIcon(null);
        }
        else if (value instanceof NodeObject)
        {
            NodeObject node = (NodeObject)value;
            setText(node == null ? "" : node.getName());
            setIcon(node == null ? null : node.getIcon());
        }

        return this;
    }

}
