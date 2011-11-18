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

import com.kitfox.raven.util.tree.TrackCurveComponent;
import java.awt.event.InputEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 *
 * @author kitfox
 */
public class Selection<T>
{
    
    final ArrayList<T> selection = new ArrayList<T>();
    HashMap<T, Info> selectionMap = new HashMap<T, Info>();
    
    final HashSet<SelectionListener> selectionListeners = new HashSet<SelectionListener>();
    
    
    public Selection()
    {
    }

    public Selection(Selection<T> selection)
    {
        this.selection.addAll(selection.selection);
        selectionMap.putAll(selection.selectionMap);
    }

    public boolean isEmpty()
    {
        return selection.isEmpty();
    }
    
    public void addSelectionListener(SelectionListener l)
    {
        selectionListeners.add(l);
    }
    
    public void removeSelectionListener(SelectionListener l)
    {
        selectionListeners.remove(l);
    }
    
    public void clear()
    {
        if (selection.isEmpty())
        {
            return;
        }
        
        selection.clear();
        selectionMap.clear();
        fireSelectionChanged();
    }
    
    public boolean isSelected(T node)
    {
        return selectionMap.containsKey(node);
    }
    
    public T getTopSelected()
    {
        if (selection.isEmpty()) return null;
        return selection.get(0);
    }
    
    public <R extends T> R getTopSelected(Class<R> filter)
    {
        for (T node: selection)
        {
            if (filter.isAssignableFrom(node.getClass()))
            {
                return (R)node;
            }
        }
        return null;
    }
    
    public int size()
    {
        return selection.size();
    }
    
    public T get(int index)
    {
        return selection.get(index);
    }
    
    public ArrayList<T> getSelection()
    {
        ArrayList<T> list = new ArrayList<T>();
        getSelection(list);
        return list;
    }

    public List<T> getSelection(List<T> retVec)
    {
        if (retVec == null) retVec = new ArrayList<T>();
        
        retVec.addAll(selection);
        
        return retVec;
    }
    
    public <R extends T> List<R> getSelection(Class<R> filter, List<R> retVec)
    {
        if (retVec == null) retVec = new ArrayList<R>();
        
        for (T node: selection)
        {
            if (filter.isAssignableFrom(node.getClass()))
            {
                retVec.add((R)node);
            }
        }
        
        return retVec;
    }
    
    public void select(Type type, T item)
    {
        switch (type)
        {
            case REPLACE:
            {
                selection.clear();
                selectionMap.clear();

                selection.add(item);
                selectionMap.put(item, new Info(item));
                break;
            }
            case INVERSE:
            {
                if (selectionMap.containsKey(item))
                {
                    selection.remove(item);
                    selectionMap.remove(item);
                }
                else
                {
                    selection.add(item);
                    selectionMap.put(item, new Info(item));
                }
                break;
            }
            case ADD:
            {
                if (!selectionMap.containsKey(item))
                {
                    selection.add(item);
                    selectionMap.put(item, new Info(item));
                }
                break;
            }
            case SUB:
            {
                selection.remove(item);
                selectionMap.remove(item);
                break;
            }
        }
        
        fireSelectionChanged();
    }
    
    public void select(Type type, Collection<? extends T> items)
    {
        switch (type)
        {
            case REPLACE:
            {
                selection.clear();
                selectionMap.clear();

                selection.addAll(items);
                for (T obj: items)
                {
                    selectionMap.put(obj, new Info(obj));
                }
                break;
            }
            case INVERSE:
            {
                ArrayList<T> intersection = new ArrayList<T>();
                intersection.addAll(selection);
                intersection.retainAll(items);
                
                selection.addAll(items);
                selection.removeAll(intersection);

                for (T obj: items)
                {
                    selectionMap.put(obj, new Info(obj));
                }
                for (T obj: intersection)
                {
                    selectionMap.remove(obj);
                }
                break;
            }
            case ADD:
            {
                ArrayList<T> addition = new ArrayList<T>();
                addition.addAll(items);
                addition.removeAll(selection);
                
                selection.addAll(addition);
                for (T obj: addition)
                {
                    selectionMap.put(obj, new Info(obj));
                }
                break;
            }
            case SUB:
            {
                selection.removeAll(items);
                for (T obj: items)
                {
                    selectionMap.remove(obj);
                }
                break;
            }
        }
        
        fireSelectionChanged();
    }
    
    
    public static Selection.Type suggestSelectType(InputEvent evt)
    {
        int mod = evt.getModifiersEx();
        boolean shift = (mod & InputEvent.SHIFT_DOWN_MASK) == InputEvent.SHIFT_DOWN_MASK;
        boolean ctrl = (mod & InputEvent.CTRL_DOWN_MASK) == InputEvent.CTRL_DOWN_MASK;
        
        return suggestSelectType(shift, ctrl);
    }
    
    public static Selection.Type suggestSelectType(boolean shift, boolean ctrl)
    {
        if (shift)
        {
            return ctrl ? Type.ADD : Type.INVERSE;
        }
        else
        {
            return ctrl ? Type.SUB : Type.REPLACE;
        }
    }

    public <R> R getSubselection(T item, Class<R> key)
    {
        Info info = selectionMap.get(item);
        return (info == null) ? null : info.getSubselection(key);
    }

    public <R> void setSubselection(T item, Class<R> key, R subselection)
    {
        Info info = selectionMap.get(item);
        if (info != null)
        {
            info.setSubselection(key, subselection);
        }
    }

    protected void fireSelectionChanged()
    {
        SelectionEvent evt = new SelectionEvent(this, selection.toArray());
        ArrayList<SelectionListener> listeners =
                new ArrayList<SelectionListener>(selectionListeners);
        for (int i = 0; i < listeners.size(); ++i)
        {
            listeners.get(i).selectionChanged(evt);
        }
    }

    protected <R> void fireSubselectionChanged(T item, Class<R> key,
            R oldSubSel, R newSubSel)
    {
        SelectionSubEvent evt = new SelectionSubEvent(this, item, key, oldSubSel, newSubSel);
        ArrayList<SelectionListener> listeners =
                new ArrayList<SelectionListener>(selectionListeners);
        for (int i = 0; i < listeners.size(); ++i)
        {
            listeners.get(i).subselectionChanged(evt);
        }
    }

    public void set(Selection<T> selNew)
    {
        selection.clear();
        selection.addAll(selNew.selection);

        selectionMap.clear();
        selectionMap.putAll(selNew.selectionMap);

        fireSelectionChanged();
    }

    //-------------------------------
    class Info
    {
        T item;
        HashMap<Object, Object> subselection = new HashMap<Object, Object>();
//        private Object subselection;

        public Info(T item)
        {
            this.item = item;
        }

        /**
         * @return the subselection
         */
        public <R> R getSubselection(Class<R> key)
        {
            return (R)subselection.get(key);
        }

        /**
         * @param value the subselection to set
         */
        public <R> void setSubselection(Class<R> key, R value)
        {
            R old = (R)subselection.get(key);

            if ((old == null && value == null)
                    || (old != null && old.equals(value)))
            {
                return;
            }

            subselection.put(key, value);
            fireSubselectionChanged(item, key, old, value);
        }
    }

    //------------------------------
    public enum Type { REPLACE, INVERSE, ADD, SUB };
}
