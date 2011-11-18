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

package com.kitfox.raven.util;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kitfox
 */
public class PropertyChangeWeakListener implements PropertyChangeListener
{

    final WeakReference<PropertyChangeListener> ref;
    final Object src;

    public PropertyChangeWeakListener(PropertyChangeListener listener, Object src)
    {
        this.ref = new WeakReference<PropertyChangeListener>(listener);
        this.src = src;
    }

    public static boolean supportsPropertyChangeListener(Object src)
    {
        try {
            Method methodAdd = src.getClass().getMethod("addPropertyChangeListener", PropertyChangeListener.class);
            Method methodRemove = src.getClass().getMethod("removePropertyChangeListener", PropertyChangeListener.class);
            
            return methodAdd != null && methodRemove != null;
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(PropertyChangeWeakListener.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(PropertyChangeWeakListener.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    public static PropertyChangeWeakListener createWeakListener(PropertyChangeListener listener, Object src)
    {
        try {
            PropertyChangeWeakListener weakListener = new PropertyChangeWeakListener(listener, src);
            Method method = src.getClass().getMethod("addPropertyChangeListener", PropertyChangeListener.class);
            method.invoke(src, listener);

            return weakListener;
        } catch (InvocationTargetException ex) {
            Logger.getLogger(PropertyChangeWeakListener.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(PropertyChangeWeakListener.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(PropertyChangeWeakListener.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(PropertyChangeWeakListener.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public void remove()
    {
        try {
            Method method = src.getClass().getMethod("removePropertyChangeListener", PropertyChangeListener.class);
            method.invoke(src, this);

        } catch (InvocationTargetException ex) {
            Logger.getLogger(PropertyChangeWeakListener.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(PropertyChangeWeakListener.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(PropertyChangeWeakListener.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(PropertyChangeWeakListener.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt)
    {
        PropertyChangeListener l = ref.get();
        if (l == null)
        {
            remove();
            return;
        }
        l.propertyChange(evt);
    }
}
