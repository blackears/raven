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

package com.kitfox.raven.editor.view.properties;

import com.kitfox.raven.util.tree.NodeObject;
import com.kitfox.raven.util.tree.PropertyWrapper;
import java.beans.PropertyEditor;
import java.util.ArrayList;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

/**
 *
 * @author kitfox
 */
public class PropertyModel implements TableModel
{

    private final NodeObject node;

    ArrayList<TableModelListener> tableModelListeners = new ArrayList<TableModelListener>();
    ArrayList<PropertyModelLine> rowProps = new ArrayList<PropertyModelLine>();

    public PropertyModel(NodeObject node)
    {
        this.node = node;

        //Assign each property with an editor to a table row
        for (int i = 0; i < node.getNumPropertyWrappers(); ++i)
        {
            PropertyWrapper prop = node.getPropertyWrapper(i);
            if (prop.isHidden())
            {
                continue;
            }

            PropertyModelLine info = new PropertyModelLine(this, prop, rowProps.size());
            if (info.getEditor() == null)
            {
                continue;
            }
            rowProps.add(info);
        }
    }

    public PropertyModelLine getPropertyInfo(String propName)
    {
        for (int i = 0; i < rowProps.size(); ++i)
        {
            PropertyModelLine info = rowProps.get(i);
            if (propName.equals(info.getProp().getName()))
            {
                return info;
            }
        }
        return null;
    }

    public Object getPropertyValue(String propName)
    {
        PropertyEditor ed = getPropertyInfo(propName).getEditor();
        return ed.getValue();
    }

    public String getPropertyAsText(String propName)
    {
        PropertyEditor ed = getPropertyInfo(propName).getEditor();
        return ed.getAsText();
    }

    public void setPropertyAsText(String propName, String value)
    {
        PropertyEditor ed = getPropertyInfo(propName).getEditor();
        ed.setAsText(value);
    }

    @Override
    public int getRowCount()
    {
        return rowProps.size();
    }

    @Override
    public int getColumnCount()
    {
        return 2;
    }

    @Override
    public String getColumnName(int columnIndex)
    {
        switch (columnIndex)
        {
            case 0:
                return "name";
            case 1:
                return "value";
            default:
                throw new RuntimeException();
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex)
    {
        switch (columnIndex)
        {
            case 0:
                return String.class;
            case 1:
                return PropertyModelLine.class;
            default:
                throw new RuntimeException();
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex)
    {
        switch (columnIndex)
        {
            case 0:
                return false;
            case 1:
                return true;
            default:
                throw new RuntimeException();
        }
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex)
    {
        switch (columnIndex)
        {
            case 0:
            {
                return rowProps.get(rowIndex).getProp().getName();
            }
            case 1:
            {
                return rowProps.get(rowIndex);
            }
            default:
                throw new RuntimeException();
        }
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex)
    {
        switch (columnIndex)
        {
            case 1:
            {
//                PropertyInfo info = props.get(rowIndex);
//                if (aValue instanceof String)
//                {
//                    info.ed.setAsText((String)aValue);
//                }
//                else if (aValue instanceof AbstractGameComponentEditor)
//                {
//
//                }
//                else
//                {
//                    info.ed.setValue(aValue);
//                }
                break;
            }
            default:
                throw new RuntimeException();
        }
    }

    @Override
    public void addTableModelListener(TableModelListener l)
    {
        tableModelListeners.add(l);
    }

    @Override
    public void removeTableModelListener(TableModelListener l)
    {
        tableModelListeners.remove(l);
    }

    protected void fireTableRowModified(int row)
    {
        TableModelEvent evt = new TableModelEvent(this, row);
        for (TableModelListener l: new ArrayList<TableModelListener>(tableModelListeners))
        {
            l.tableChanged(evt);
        }
    }

}
