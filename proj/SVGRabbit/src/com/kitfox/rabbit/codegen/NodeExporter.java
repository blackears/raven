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

package com.kitfox.rabbit.codegen;

import com.kitfox.rabbit.nodes.RaElement;
import com.kitfox.rabbit.nodes.RaString;
import com.kitfox.rabbit.property.EnumEditor;
import com.kitfox.rabbit.style.Style;
import com.kitfox.rabbit.style.StyleKey;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kitfox
 */
public class NodeExporter
{
    final DocumentExporter doc;
    final RaElement element;
    final BeanInfo info;

    public NodeExporter(RaElement node, DocumentExporter doc)
    {
        this.element = node;
        this.doc = doc;
        BeanInfo beanInfo = null;
        try {
            beanInfo = Introspector.getBeanInfo(node.getClass());
        } catch (IntrospectionException ex) {
            Logger.getLogger(NodeExporter.class.getName()).log(Level.SEVERE, null, ex);
        }
        info = beanInfo;
    }

    public String getName()
    {
        return doc.builder.getNodeName(element);
    }

    public String getNodeClass()
    {
        return element.getClass().getCanonicalName();
    }

    public String getStringValue()
    {
        if (element instanceof RaString)
        {
            return ((RaString)element).getText();
        }
        return null;
    }


    public ArrayList<NodeExporter> getChildren()
    {
        ArrayList<NodeExporter> list = new ArrayList<NodeExporter>();

        if (element instanceof RaElement)
        {
            RaElement node = (RaElement)element;
            for (RaElement child: node.getChildren())
            {
                list.add(new NodeExporter(child, doc));
            }
        }

        return list;
    }

    public ArrayList<StyleInfo> getStyles() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException
    {
        ArrayList<StyleInfo> list = new ArrayList<StyleInfo>();

        Style style = element.getStyle();
        for (StyleKey key: style.getKeys())
        {
            Object value = style.get(key);
            if (value == null)
            {
                continue;
            }

            Class propType = value.getClass();
            PropertyEditor ed;
            if (Enum.class.isAssignableFrom(propType))
            {
                ed = new EnumEditor(propType);
            }
            else
            {
                ed = PropertyEditorManager.findEditor(propType);
            }

            if (ed == null)
            {
                continue;
            }
            ed.setValue(value);
            
            list.add(new StyleInfo(key,
                    ed.getJavaInitializationString()));
        }

        return list;
    }

    public ArrayList<PropertyInfo> getProperties()
    {
        ArrayList<PropertyInfo> list = new ArrayList<PropertyInfo>();

        RaElement defaultNode = null;
        try {
            defaultNode = element.getClass().newInstance();
        } catch (InstantiationException ex) {
            Logger.getLogger(NodeExporter.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(NodeExporter.class.getName()).log(Level.SEVERE, null, ex);
        }

        for (PropertyDescriptor prop: info.getPropertyDescriptors())
        {
            if ("style".equals(prop.getName()))
            {
                continue;
            }
            if (prop.getReadMethod() == null || prop.getWriteMethod() == null)
            {
                continue;
            }

            Method meth = prop.getReadMethod();
            Object value;
            Object valueDef;
            try {
                value = meth.invoke(element);
                valueDef = meth.invoke(defaultNode);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(NodeExporter.class.getName()).log(Level.SEVERE, null, ex);
                continue;
            } catch (IllegalArgumentException ex) {
                Logger.getLogger(NodeExporter.class.getName()).log(Level.SEVERE, null, ex);
                continue;
            } catch (InvocationTargetException ex) {
                Logger.getLogger(NodeExporter.class.getName()).log(Level.SEVERE, null, ex);
                continue;
            }
            
            if ((value == null && valueDef == null) ||
                    (value != null && value.equals(valueDef)))
            {
                //Skip default values
                continue;
            }

            Class propType = prop.getPropertyType();
            PropertyEditor ed;
            if (Enum.class.isAssignableFrom(propType))
            {
                ed = new EnumEditor(propType);
            }
            else
            {
                ed = PropertyEditorManager.findEditor(propType);
            }
            
            if (ed == null)
            {
                continue;
            }
            ed.setValue(value);

            PropertyInfo info = new PropertyInfo(
                prop.getName(),
                ed.getJavaInitializationString()
            );
            list.add(info);
        }
        return list;
    }
}
