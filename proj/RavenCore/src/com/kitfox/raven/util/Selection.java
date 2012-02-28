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
 * Maintains an ordered list of selected objects.  Also keeps track of
 * a subselection for each selected object.
 * 
 * Subselection implemented as a hashmap that maps class objects to
 * instances of that object.  
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
    
    /**
     * Removes all selected values.
     */
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
    
    /**
     * Test if item is currently selected
     * 
     * @param item Member to test
     * @return true if item is a member of this selection.
     */
    public boolean isSelected(T item)
    {
        return selectionMap.containsKey(item);
    }
    
    /**
     * Finds the first selected item.
     * 
     * @return First item in selection list.  null if selection is empty.
     */
    public T getTopSelected()
    {
        if (selection.isEmpty()) return null;
        return selection.get(0);
    }
    
    /**
     * Finds the first selected item that matches class filter.
     * 
     * @param filter Only selected items that are assignable to this
     * class are considered.
     * @return First item that matches filter.  null if none.
     */
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
    
    /**
     * Get a copy of selected items.
     * 
     * @return List of selected items.
     */
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

    /**
     * Get selected items that are assignable to the class filter.
     * 
     * @param filter Only items assignable to this filter are returned
     * @param retVec Array that results will be added to.  If null, a new
     * list will be created.
     * @return List with filtered items.
     */
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
    
    /**
     * Alter selection by combining it with passed item and selection
     * operator.
     * 
     * @param op Operator to apply with item against selection
     * @param item Item to modify selection with
     */
    public void select(T item, Operator op)
    {
        switch (op)
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
    
    /**
     * Alter selection by combining it with passed items and selection
     * operator.
     * 
     * @param op Operator to apply with items against selection
     * @param item Item to modify selection with
     */
    public void select(Collection<? extends T> items, Operator op)
    {
        switch (op)
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
    
    /**
     * Parse modifier keys used on an event to determine a standard
     * selection operator to use.
     * 
     * @param evt Event to examine
     * @return Standard operator for this pattern of modifier keys
     */
    public static Selection.Operator suggestSelectType(InputEvent evt)
    {
        int mod = evt.getModifiersEx();
        boolean shift = (mod & InputEvent.SHIFT_DOWN_MASK) == InputEvent.SHIFT_DOWN_MASK;
        boolean ctrl = (mod & InputEvent.CTRL_DOWN_MASK) == InputEvent.CTRL_DOWN_MASK;
        
        return suggestSelectType(shift, ctrl);
    }

    /**
     * Determine best operator for given modifier keys.
     * 
     * @param shift true if shift pressed
     * @param ctrl true if ctrl pressed
     * @return Standard operator for given pattern
     */
    public static Selection.Operator suggestSelectType(boolean shift, boolean ctrl)
    {
        if (shift)
        {
            return ctrl ? Operator.ADD : Operator.INVERSE;
        }
        else
        {
            return ctrl ? Operator.SUB : Operator.REPLACE;
        }
    }

    /**
     * Find the sub-selection of given class type for selected item.
     * 
     * @param item Selection item to get sub-selection for
     * @param key Class of sub-selection item to retrieve
     * @return Sub-selection item.  null if item is not member of 
     * selection, or has no sub-selection for the given key.
     */
    public <R> R getSubselection(T item, Class<R> key)
    {
        Info info = selectionMap.get(item);
        return (info == null) ? null : info.getSubselection(key);
    }

    /**
     * Sets a sub-selection item on a given item in our selection.
     * 
     * @param item Item to set sub-selection for
     * @param key Class key that is used to index sub-selection
     * @param subselection Instance of sub-selection
     */
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

    /**
     * Replaces this selection with copy of passed selection.
     * 
     * @param selNew Selection to copy.
     */
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
    public enum Operator { REPLACE, INVERSE, ADD, SUB };
}
