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

package com.kitfox.raven.util.tree;

import com.kitfox.raven.util.tree.TrackKey.Interp;
import com.kitfox.raven.util.undo.History;
import com.kitfox.raven.util.undo.HistoryAction;
import com.kitfox.xml.schema.ravendocumentschema.InterpolationType;
import com.kitfox.xml.schema.ravendocumentschema.PropertyDataType;
import com.kitfox.xml.schema.ravendocumentschema.PropertyStyleType;
import com.kitfox.xml.schema.ravendocumentschema.PropertyType;
import com.kitfox.xml.schema.ravendocumentschema.RepeatType;
import com.kitfox.xml.schema.ravendocumentschema.TrackKeyType;
import com.kitfox.xml.schema.ravendocumentschema.TrackType;
import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The PropertyWrapper extends the concept of a Java property by encapsulating
 * it in an object and adding some important information.  PropertyWrappers 
 * are the way NodeObjects declare and publish their properties.
 *
 * <p>PropertyWrappers manage a property.  The property value will ideally be immutable,
 * and if it isn't, it should be treated as if it is.  (An immutable object 
 * is one who's internal data doesn't change after it's been constructed.  A 
 * {@link java.lang.String} is immutable; a {@link java.awt.Point} is not). 
 * The values the properties manage must be 
 * effectively immutable because the PropertyWrapper
 * will only know if the value changes if its set*() methods are called.  If
 * you change the internal data of the value directly, the animation
 * system has no way of knowing that the value has changed.</p>
 * 
 * <p>PropertyWrappers not only track the current value of a property, but how
 * it is accessed too.  It does this by wrapping each value that is tracks
 * in a PropertyData class.  By using the appropriate {@link PropertyData}, you can
 * store the value directly on the object, pull it from the 
 * {@link com.kitfox.raven.util.resource.ResourceCache}, 
 * or create a reference to another node in your scene.  In fact, the
 * PropertyWrapper tracks {@link PropertyData} objects via the getData*() and 
 * setData*() methods, and provides the getValue*() and setValue*() methods
 * as a convenience.</p>
 * 
 * <p>When you set the property data, you have the option of recording your change
 * in the {@link History}.  You can indicate this by setting the history flag
 * in the set*() methods.  When you do this, an action will be created and 
 * added to the current project's history queue.  While you will usually want
 * to submit most of your changes to history, there are times when you may want
 * to bypass it - for example, an interactive tool that lets the user scrub
 * through many values to get feedback on how their changes affect things.
 * (In these cases, you will usually want to set the intermediate values without
 * using history and only set a value with history at the end when the user
 * finishes.)</p>
 * 
 * <p>The PropertyWrapper keeps track of both its current value (called the 
 * 'direct' value) and animated track based values.  A track is a series of key
 * values which are interpolated to determine intermediate values.
 * Tracks are defined on the root Symbol, and so all NodeObjects within your
 * symbol use the same set of tracks.  Since all {@link Track}s are
 * ordinary NodeObjects, the PropertyWrapper simply uses their node ID to keep
 * track of them.  The direct value is a special additional value that is used
 * to indicate the editing value of the property and is also used whenever
 * you refer to a {@link FrameKey} that is not defined by this property's 
 * tracks.</p>
 * 
 * <p>To keep things running quickly, the PropertyWrapper does a lot of caching.
 * Each interpolated frame within each track - the direct value as well -
 * have a {@link ValueCache} that keeps track of their values.  Whenever you
 * call a get*() method, the cache if first checked to see if a 
 * {@link ValueCache} has already been created, and if not will create one.
 * This way interpolated values that have been calculated once will not need
 * to be calculated again.  The {@link ValueCache} also allows the user 
 * to store custom data on it - this allows a NodeObject to derive custom
 * objects from the PropertyWrapper data and store them along side them in 
 * the cache.  Whenever a {@link FramKey} is determined to be invalid, its 
 * cache is erased and everything must be recalculated.</p>
 * 
 * <p>When a property is animated, its direct value (ie, current value) is 
 * repeatedly updated to the current {@link FrameKey}.  This is done by
 * calling the synchToTrack() method.  When this happens, the property's
 * tracks are checked to see if they provide an interpolated value for this 
 * frame.  If they do, it is copied into the direct value.  The direct value
 * also becomes synchronized, which means that all get operations will 
 * draw from the {@link ValueCache} of the synchronized {@link FrameKey}.
 * The synchronization will be broken the next time a set*() method is called.</p>
 * 
 * <p>The PropertyWrapper also handles serialization of properties within the
 * NodeObject.  When you declare a PropertyWrapper, you must pass it the
 * class of the property being managed.  When it needs to load or save a
 * property value, it will use this class to look for a corresponding 
 * {@link PropertyProvider}, which it will delegate to.</p>
 * 
 * @author kitfox
 */
public class PropertyWrapper<NodeType extends NodeObject, PropType>
{
    private final NodeType node;
    private final String name;
    private final Class<PropType> propertyType;

    PropertyData<PropType> directValue;

    //Set when direct value is synchronized (equal to)
    // to a particular frame in the animation curve.  If
    // set to DIRECT, has a unique cached value.
    FrameKey synchKey = FrameKey.DIRECT;
        
    public static final Color DEFAULT_DISPLAY_COLOR = Color.BLUE;
    private final Color displayColor;

    //private final boolean animatable;
    private final int flags;
    public static final int FLAG_ANIMATABLE = 1;
    public static final int FLAG_HIDDEN = 2;

    //Some common flag settings
    public static final int FLAGS_DEFAULT = FLAG_ANIMATABLE;
    public static final int FLAGS_NOANIM = FLAGS_DEFAULT & (~FLAG_ANIMATABLE);

    //Track UID->curve data
//    HashMap<Integer, TrackCurve<PropType>> trackMap
//            = new HashMap<Integer, TrackCurve<PropType>>();
    TrackCurve<PropType> curve;

    //Keep track of cached values and precomputed info
    HashMap<FrameKey, ValueCache<PropType>> valueCache
            = new HashMap<FrameKey, ValueCache<PropType>>();

    ArrayList<PropertyWrapperListener> listeners =
            new ArrayList<PropertyWrapperListener>();

    public PropertyWrapper(
            NodeType node,
            String name,
            Class<PropType> propertyType)
    {
        this(node, name, propertyType, (PropType)null);
    }

    public PropertyWrapper(
            NodeType node,
            String name,
            Class<PropType> propertyType,
            PropType initialValue)
    {
        this(node, name, propertyType, new PropertyDataInline<PropType>(initialValue));
    }

    public PropertyWrapper(
            NodeType node,
            String name,
            Class<PropType> propertyType,
            PropertyData<PropType> initialValue)
    {
        this(node, name, FLAGS_DEFAULT, propertyType, initialValue);
    }

    public PropertyWrapper(
            NodeType node,
            String name,
            int flags,
            Class<PropType> propertyType)
    {
        this(node, name, flags, propertyType, (PropType)null);
    }

    public PropertyWrapper(
            NodeType node,
            String name,
            int flags,
            Class<PropType> propertyType,
            PropType initialValue)
    {
        this(node, name, flags, propertyType, new PropertyDataInline<PropType>(initialValue));
    }

    public PropertyWrapper(
            NodeType node,
            String name,
            int flags,
            Class<PropType> propertyType,
            PropertyData<PropType> initialValue)
    {
        this(node, name, flags, DEFAULT_DISPLAY_COLOR, propertyType, initialValue);
    }

    public PropertyWrapper(
            NodeType node,
            String name,
            int flags,
            Color displayColor,
            Class<PropType> propertyType,
            PropertyData<PropType> initialValue)
    {
        this.node = node;
        this.name = name;
        this.flags = flags;
        this.displayColor = displayColor;
        this.propertyType = propertyType;

//        this.directValue = this.lastUndoableValue = initialValue;
        this.directValue = initialValue;

        curve = new TrackCurve<PropType>(propertyType);
        
        node.registerPropertyWrapper(this);
    }

    /**
     * @return the animatable
     */
    public boolean isAnimatable()
    {
        return (flags & FLAG_ANIMATABLE) == FLAG_ANIMATABLE;
    }

    public boolean isHidden()
    {
        return (flags & FLAG_HIDDEN) == FLAG_HIDDEN;
    }

    public void addPropertyWrapperListener(PropertyWrapperListener l)
    {
        listeners.add(l);
    }

    public void removePropertyWrapperListener(PropertyWrapperListener l)
    {
        listeners.remove(l);
    }

    protected void firePropertyDataChanged(Object oldValue, Object newValue)
    {
        if ((oldValue == null && newValue == null) 
                || (oldValue != null && oldValue.equals(newValue)))
        {
            return;
        }
        
        PropertyChangeEvent evt = new PropertyChangeEvent(this, name, oldValue, newValue);
        ArrayList<PropertyWrapperListener> list =
                new ArrayList<PropertyWrapperListener>(listeners);
        for (int i = 0; i < list.size(); ++i)
        {
            list.get(i).propertyWrapperDataChanged(evt);
        }
        node.notifyNodePropertyChanged(evt);
    }

    protected void firePropertyTrackChanged()
    {
        PropertyTrackChangeEvent evt =
                new PropertyTrackChangeEvent(this);
        ArrayList<PropertyWrapperListener> list =
                new ArrayList<PropertyWrapperListener>(listeners);
        for (int i = 0; i < list.size(); ++i)
        {
            list.get(i).propertyWrapperTrackChanged(evt);
        }
    }

    protected void firePropertyTrackKeyChanged(int frame)
    {
        PropertyTrackKeyChangeEvent evt =
                new PropertyTrackKeyChangeEvent(this, frame);
        ArrayList<PropertyWrapperListener> list =
                new ArrayList<PropertyWrapperListener>(listeners);
        for (int i = 0; i < list.size(); ++i)
        {
            list.get(i).propertyWrapperTrackKeyChanged(evt);
        }
    }

    /**
     * @return the name
     */
    public String getName()
    {
        return name;
    }

    /**
     * @return the propertyType
     */
    public Class<PropType> getPropertyType()
    {
        return propertyType;
    }

    public PropType getValue()
    {
        return getValue(synchKey);
//        return directValue == null ? null : directValue.getValue(node.getDocument());
    }

    public PropertyData<PropType> getData()
    {
        return getData(synchKey);
        //return directValue;
    }

    public void setValue(PropType value)
    {
        setData(new PropertyDataInline<PropType>(value));
    }

    public void setValue(PropType value, boolean history)
    {
        setData(new PropertyDataInline<PropType>(value), history);
    }

    public void setData(PropertyData<PropType> value)
    {
        setData(value, true);
    }

    public void setData(PropertyData<PropType> value, boolean history)
    {
        SetValueDirectAction action = new SetValueDirectAction(directValue, value);

        if (history)
        {
            doAction(action);
        }
        else
        {
            action.redo(null);
        }
    }

    protected void doAction(HistoryAction action)
    {
        NodeSymbol doc = node.getSymbol();
        History hist = doc == null ? null : doc.getHistory();
        if (hist == null)
        {
            action.redo(null);
            return;
        }
        hist.doAction(action);
    }

    @Override
    public String toString()
    {
        return name + ": " + getValue();
    }

    //------------------------------

//    public ArrayList<Integer> getTrackUids()
//    {
//        return new ArrayList<Integer>(trackMap.keySet());
//    }
//
//    public TrackCurve<PropType> getTrackCurve(int trackUid)
//    {
//        TrackCurve<PropType> curve = trackMap.get(trackUid);
//        return curve == null ? null : new TrackCurve<PropType>(curve);
//    }

    public TrackCurve<PropType> getTrackCurve()
    {
        return curve == null ? null : new TrackCurve<PropType>(curve);
    }

    public void setTrackCurve(TrackCurve<PropType> curve)
    {
        setTrackCurve(curve, true);
    }

    public void setTrackCurve(TrackCurve<PropType> curve, boolean history)
    {
        if (!isAnimatable())
        {
            return;
        }

        TrackCurve<PropType> curveOld = this.curve;
        if (curve.equals(curveOld))
        {
            return;
        }

        SetTrackCurveAction action = new SetTrackCurveAction(curveOld,
                new TrackCurve<PropType>(curve));
        if (history)
        {
            doAction(action);
        }
        else
        {
            action.redo(null);
        }
    }

//    public void deleteTrackCurve(int trackUid)
//    {
//        if (!isAnimatable())
//        {
//            return;
//        }
//
//        TrackCurve<PropType> curveOld = trackMap.get(trackUid);
//        if (curveOld == null)
//        {
//            return;
//        }
//
//        DeleteTrackCurveAction action = new DeleteTrackCurveAction(trackUid, curveOld);
//        doAction(action);
//    }

    /**
     * @return the node
     */
    public NodeType getNode()
    {
        return node;
    }

    public boolean isKeyAt(int frame)
    {
        return curve == null ? false : curve.isKeyAt(frame);
    }

    public boolean isInterpolatedAt(int frame)
    {
        return curve == null ? false : curve.isInterpolatedAt(frame);
    }

    public void setKeyAt(int frame,
            PropertyData<PropType> data)
    {
        setKeyAt(frame, data, TrackKey.Interp.CONST);
    }

    public void setKeyAt(int frame,
            PropertyData<PropType> data, TrackKey.Interp interp)
    {
        setKeyAt(frame, data, interp, 1, 0, 1, 0);
    }

    public void setKeyAt(int frame,
            PropertyData<PropType> data, TrackKey.Interp interp,
            double dxIn, double dyIn, double dxOut, double dyOut)
    {
        if (!isAnimatable())
        {
            return;
        }

        SetTrackKeyAction action = new SetTrackKeyAction(frame, 
                data, interp, dxIn, dyIn, dxOut, dyOut);
        doAction(action);
    }

    public void removeKeyAt(int frame)
    {
        if (!isAnimatable())
        {
            return;
        }

//        TrackCurve curve = trackMap.get(trackUid);
        if (curve == null)
        {
            return;
        }
        if (!curve.isKeyAt(frame))
        {
            return;
        }

        DeleteTrackKeyAction action = new DeleteTrackKeyAction(frame);
        doAction(action);
    }

    public void synchToTrack(int frame)
    {
        synchToTrack(new FrameKey(frame));
    }
    
    public void synchToTrack(FrameKey key)
    {
//if ("transX".equals(getName()))
//{
//    int j = 9;
//}
        
        FrameKey synchOld = synchKey;
        synchKey = key;
        
        PropertyData<PropType> oldValue = getData(synchOld);
        PropertyData<PropType> newValue = getData(synchKey);

        //Update direct cache
        ValueCache<PropType> cache = getOrCreateValueCache(key);
        ValueCache<PropType> cacheDirect = getOrCreateValueCache(FrameKey.DIRECT);
        cacheDirect.copyState(cache);
        directValue = newValue;
        
        firePropertyDataChanged(oldValue, newValue);
    }

    public PropType getValue(FrameKey key)
    {
        PropertyData<PropType> data = getData(key);
        
//if (data == null)
//{
//    data = getData(key);
//    //throw new RuntimeException();
//}
        
        return data == null ? getValue() : data.getValue(node.getSymbol());
    }

    public PropType getValue(int frame)
    {
        return getValue(new FrameKey(frame));
    }

    protected ValueCache<PropType> getOrCreateValueCache(FrameKey key)
    {
//        if (FrameKey.DIRECT.equals(key))
//        {
//            //If we're synchronized to a keyed value, return information from 
//            // it instead
//            key = synchKey;
//        }
        
        ValueCache<PropType> cache = valueCache.get(key);
        if (cache != null)
        {
            return cache;
        }
        
        //Cache is empty at key.  Build it
        if (!FrameKey.DIRECT.equals(key))
        {
            int frame = key.getAnimFrame();
            if (curve.isKeyAt(frame))
            {
                TrackKey<PropType> curveKey = curve.getKey(frame);
                cache = new ValueCache(key, curveKey.getData());
                valueCache.put(key, cache);
                return cache;
            }
            else if (curve.isInterpolatedAt(frame))
            {
                PropType curveValue = curve.evaluate(frame, getNode().getSymbol());
                cache = new ValueCache(key, new PropertyDataInline(curveValue));
                valueCache.put(key, cache);
                return cache;
            }
        }

        //Not handled by anim curves.  Return direct value cache
        cache = valueCache.get(FrameKey.DIRECT);
        if (cache != null)
        {
            return cache;
        }

        cache = new ValueCache(FrameKey.DIRECT, directValue);
        valueCache.put(FrameKey.DIRECT, cache);
        return cache;
    }

    public PropertyData<PropType> getData(FrameKey frame)
    {
        //return getData(frame.getTrackUid(), frame.getAnimFrame());
        ValueCache<PropType> cache = getOrCreateValueCache(frame);
        return cache.data;
    }

    public PropertyData<PropType> getData(int frame)
    {
        return getData(new FrameKey(frame));
    }

    public PropertyType export()
    {
        PropertyType propType = new PropertyType();

        propType.setName(name);
        propType.setDirect(export(directValue));

//        NodeSymbol doc = node.getSymbol();
        if (curve != null)
        {
            TrackType trackType = exportTrack();
            propType.setTrack(trackType);
        }
//        for (Integer trackUid: trackMap.keySet())
//        {
//            NodeObject trackNode = doc.getNode(trackUid);
//            if (trackNode == null)
//            {
//                //Do not export track if its track object has been deleted
//                continue;
//            }
//
//            TrackType trackType = exportTrack(trackUid);
//            if (trackType == null)
//            {
//                continue;
//            }
//            propType.getTrack().add(trackType);
//        }

        return propType;
    }

    public TrackType exportTrack()
    {
        if (curve.isEmpty())
        {
            return null;
        }

        TrackType trackType = new TrackType();

        trackType.setBefore(RepeatType.valueOf(curve.getBefore().name()));
        trackType.setAfter(RepeatType.valueOf(curve.getAfter().name()));

        for (Integer frame: curve.getFrames())
        {
            TrackKey<PropType> key = curve.getKey(frame);
            TrackKeyType keyType = new TrackKeyType();
            trackType.getKey().add(keyType);

            keyType.setFrame(frame);
            keyType.setInterpolation(InterpolationType.valueOf(key.getInterp().name()));
            keyType.setTanInX(key.getTanInX());
            keyType.setTanInY(key.getTanInY());
            keyType.setTanOutX(key.getTanOutX());
            keyType.setTanOutY(key.getTanOutY());
            keyType.setData(export(key.getData()));
        }
        return trackType;
    }

    private PropertyDataType export(PropertyData<PropType> data)
    {
        PropertyDataType type = new PropertyDataType();

        if (data == null)
        {
            type.setStyle(PropertyStyleType.INLINE);
            type.setValue("");
        }
        else if (data instanceof PropertyDataInline)
        {
            type.setStyle(PropertyStyleType.INLINE);

            PropType value = ((PropertyDataInline<PropType>)data).getValue(node.getSymbol());
            if (value == null)
            {
                type.setValue("");
            }
            else
            {
                PropertyProvider prov =
                        PropertyProviderIndex.inst().getProviderBest(getPropertyType());
                String text = prov.asText(value);
                type.setValue(text);
            }
        }
        else if (data instanceof PropertyDataReference)
        {
            type.setStyle(PropertyStyleType.REFERENCE);
            type.setValue("" + ((PropertyDataReference)data).getUid());
        }
        else if (data instanceof PropertyDataResource)
        {
            type.setStyle(PropertyStyleType.RESOURCE);
            type.setValue("" + ((PropertyDataResource)data).getUri());
        }

        return type;
    }

    protected void load(PropertyType type)
    {
        try
        {
            setData(load(type.getDirect()));

            TrackType trackType = type.getTrack();
            if (trackType != null)
            {
                curve = createTrackCurve(trackType);
            }
//            for (TrackType trackType: type.getTrack())
//            {
//                TrackCurve<PropType> curve = createTrackCurve(trackType);
//
//                setTrackCurve(trackType.getTrackUid(), curve);
//            }
        }
        catch (Exception ex)
        {
            Logger.getLogger(PropertyWrapper.class.getName()).log(
                    Level.WARNING, "Error loading property " 
                    + node.getClass().getName() + ": " 
                    + node.getName() + "." + name, ex);
        }
    }

    public TrackCurve<PropType> createTrackCurve(TrackType trackType)
    {
        //Create track
        TrackCurve<PropType> newCurve =
                new TrackCurve<PropType>(propertyType);
        newCurve.setBefore(TrackCurve.Repeat.valueOf(trackType.getBefore().name()));
        newCurve.setAfter(TrackCurve.Repeat.valueOf(trackType.getAfter().name()));

        //Add keys
        for (TrackKeyType keyType: trackType.getKey())
        {
            TrackKey<PropType> key =
                    new TrackKey<PropType>(load(keyType.getData()),
                    TrackKey.Interp.valueOf(keyType.getInterpolation().name()),
                    keyType.getTanInX(), keyType.getTanInY(),
                    keyType.getTanOutX(), keyType.getTanOutY());

            newCurve.setKey(keyType.getFrame(), key);
        }

        return newCurve;
    }

    protected PropertyData<PropType> load(PropertyDataType type)
    {
        switch (type.getStyle())
        {
            case INLINE:
                PropertyProvider<PropType> prov =
                        PropertyProviderIndex.inst().getProviderBest(propertyType);
                if (prov == null)
                {
                    //Don't know how to handle this type of property
                    return new PropertyDataInline<PropType>(null);
                }

                PropType value = prov.fromText(type.getValue());
                return new PropertyDataInline<PropType>(value);
            case REFERENCE:
                return new PropertyDataReference<PropType>(
                        Integer.parseInt(type.getValue()));
            case RESOURCE:
                try
                {
                    return new PropertyDataResource<PropType>(
                            new URI(type.getValue()));
                } catch (URISyntaxException ex)
                {
                    Logger.getLogger(PropertyWrapper.class.getName()).log(Level.SEVERE, null, ex);
                }
        }
        return null;
    }

    public int getPrevKeyFrame(int curFrame)
    {
        int bestFrame = Integer.MIN_VALUE;

        if (curve == null)
        {
            return bestFrame;
        }

        return curve.getPrevKeyFrame(curFrame);
    }

    public int getNextKeyFrame(int curFrame)
    {
        int bestFrame = Integer.MAX_VALUE;

        if (curve == null)
        {
            return bestFrame;
        }

        return curve.getNextKeyFrame(curFrame);
    }

    public Color getDisplayColor()
    {
        return displayColor;
    }

    protected void invalidateCacheAtKey(int frame)
    {
        TrackKey<PropType> key = curve.getKey(frame);
        
        int startFrame = frame;
        {
            int prevFrame = curve.getPrevKeyFrame(frame);
            if (prevFrame != Integer.MIN_VALUE)
            {
                TrackKey<PropType> prev = curve.getKey(prevFrame);
                if (prev.getInterp() != Interp.NONE)
                {
                    startFrame = prevFrame + 1;
                }
            }
        }
        
        int endFrame = frame;
        if (key.getInterp() != Interp.NONE)
        {
            int nextFrame = curve.getNextKeyFrame(frame);
            if (nextFrame != Integer.MAX_VALUE)
            {
                endFrame = nextFrame - 1;
            }
        }
        
        //Drop cache values
        for (int i = startFrame; i <= endFrame; ++i)
        {
            FrameKey cacheKey = new FrameKey(i);
            valueCache.remove(cacheKey);
        }
    }
    
    protected void invalidateCacheAtTrack()
    {
        valueCache.clear();
        
//        for (Iterator<FrameKey> it = valueCache.keySet().iterator(); it.hasNext();)
//        {
//            FrameKey key = it.next();
//            if (key.getTrackUid() == trackUid)
//            {
//                it.remove();
//            }
//        }
    }
    
    protected void invalidateCacheAtDirect()
    {
        valueCache.remove(FrameKey.DIRECT);
    }

    public <T> T getUserCacheValue(Class<T> clsKey)
    {
        return getUserCacheValue(clsKey, FrameKey.DIRECT);
    }
    
    public <T> T getUserCacheValue(Class<T> clsKey, FrameKey frameKey)
    {
        ValueCache<PropType> cache = getOrCreateValueCache(frameKey);
        return cache.getUserCache(clsKey);
    }

    public <T> void setUserCacheValue(Class<T> clsKey, T data)
    {
        setUserCacheValue(clsKey, FrameKey.DIRECT, data);
    }
    
    public <T> void setUserCacheValue(Class<T> clsKey, FrameKey frameKey, T data)
    {
        ValueCache<PropType> cache = getOrCreateValueCache(frameKey);
        cache.setUserCache(clsKey, data);
    }
    
    //----------------------------------------------

    public class DeleteTrackKeyAction implements HistoryAction
    {
//        private final boolean removeCurve;
        private final TrackCurve<PropType> curve;
        private final TrackKey<PropType> keyOld;
        private final int frame;

        public DeleteTrackKeyAction(int frame)
        {
            this.frame = frame;
            this.curve = PropertyWrapper.this.curve;
//            this.removeCurve = curve.getNumKeys() == 1;
            this.keyOld = curve.getKey(frame);
        }

        @Override
        public void redo(History history)
        {
            invalidateCacheAtKey(frame);
            
            curve.removeKey(frame);
            firePropertyTrackKeyChanged(frame);
//            if (removeCurve)
//            {
//                trackMap.remove(trackUid);
//                firePropertyTrackChanged(trackUid);
//            }
//            interpCache.remove(trackUid);
        }

        @Override
        public void undo(History history)
        {
//            if (removeCurve)
//            {
//                trackMap.put(trackUid, curve);
//                firePropertyTrackChanged(trackUid);
//            }
            curve.setKey(frame, keyOld);
//            interpCache.remove(trackUid);
            invalidateCacheAtKey(frame);
            firePropertyTrackKeyChanged(frame);
        }

        @Override
        public String getTitle()
        {
            return "Remove Key";
        }
    }

    public class SetTrackKeyAction implements HistoryAction
    {
//        private final boolean createCurve;
//        private final TrackCurve<PropType> curve;
        private final TrackKey<PropType> keyOld;
        private final TrackKey<PropType> keyNew;
        private final int frame;
//        private final int trackUid;

        public SetTrackKeyAction(int frame,
                PropertyData<PropType> dataNew, TrackKey.Interp interp,
                double dxIn, double dyIn, double dxOut, double dyOut)
        {
            this.frame = frame;
//            this.trackUid = trackUid;

//            TrackCurve mapCurve = trackMap.get(trackUid);
//            this.curve = createCurve 
//                    ? new TrackCurve<PropType>(propertyType)
//                    : mapCurve;
            this.keyOld = PropertyWrapper.this.curve.getKey(frame);
            this.keyNew = new TrackKey<PropType>(dataNew, interp,
                    dxIn, dyIn, dxOut, dyOut);
        }

        @Override
        public void redo(History history)
        {
//            if (createCurve)
//            {
//                trackMap.put(trackUid, curve);
//                firePropertyTrackChanged(trackUid);
//            }
            curve.setKey(frame, keyNew);
//            interpCache.remove(trackUid);
            invalidateCacheAtKey(frame);
            firePropertyTrackKeyChanged(frame);
        }

        @Override
        public void undo(History history)
        {
            invalidateCacheAtKey(frame);
            if (keyOld == null)
            {
                curve.removeKey(frame);
                firePropertyTrackKeyChanged(frame);
            }
            else
            {
                curve.setKey(frame, keyOld);
                firePropertyTrackKeyChanged(frame);
            }

//            if (createCurve)
//            {
//                trackMap.remove(trackUid);
////                interpCache.remove(trackUid);
//                firePropertyTrackChanged(trackUid);
//            }
        }

        @Override
        public String getTitle()
        {
            return "Set Key";
        }
    }

//    public class DeleteTrackCurveAction implements HistoryAction
//    {
//        final int trackUid;
//        final TrackCurve<PropType> curveOld;
//
//        public DeleteTrackCurveAction(int trackUid, TrackCurve<PropType> curveOld)
//        {
//            this.trackUid = trackUid;
//            this.curveOld = curveOld;
//        }
//
//        @Override
//        public void redo(History history)
//        {
//            invalidateCacheAtTrack(trackUid);
//            trackMap.remove(trackUid);
//            firePropertyTrackChanged(trackUid);
//        }
//
//        @Override
//        public void undo(History history)
//        {
//            trackMap.put(trackUid, curveOld);
//            invalidateCacheAtTrack(trackUid);
//            firePropertyTrackChanged(trackUid);
//        }
//
//        @Override
//        public String getTitle()
//        {
//            return "Delete Track";
//        }
//    }

    public class SetTrackCurveAction implements HistoryAction
    {
        final TrackCurve<PropType> curveOld;
        final TrackCurve<PropType> curveNew;

        public SetTrackCurveAction(TrackCurve<PropType> curveOld, TrackCurve<PropType> curveNew)
        {
            this.curveOld = curveOld;
            this.curveNew = curveNew;
        }

        @Override
        public void redo(History history)
        {
            curve = curveNew;
//            trackMap.put(trackUid, curveNew);
            invalidateCacheAtTrack();
            firePropertyTrackChanged();
        }

        @Override
        public void undo(History history)
        {
            invalidateCacheAtTrack();
            curve = curveOld;
//            if (curveOld == null)
//            {
//                trackMap.remove(trackUid);
//            }
//            else
//            {
//                trackMap.put(trackUid, curveOld);
//            }
            firePropertyTrackChanged();
        }

        @Override
        public String getTitle()
        {
            return "Set Track";
        }
    }

    public class SetValueDirectAction implements HistoryAction
    {
        final PropertyData<PropType> dataOld;
        final PropertyData<PropType> dataNew;
        final FrameKey synchOld;

        public SetValueDirectAction(PropertyData<PropType> dataOld,
                PropertyData<PropType> dataNew)
        {
            this.dataOld = dataOld;
            this.dataNew = dataNew;
            this.synchOld = synchKey;
        }

        @Override
        public void redo(History history)
        {
            invalidateCacheAtDirect();
//            lastUndoableValue = directValue = dataNew;
            synchKey = FrameKey.DIRECT;
            directValue = dataNew;
            firePropertyDataChanged(dataOld, dataNew);
        }

        @Override
        public void undo(History history)
        {
            invalidateCacheAtDirect();
            //lastUndoableValue = directValue = dataOld;
            synchKey = synchOld;
            directValue = dataOld;
            firePropertyDataChanged(dataNew, dataOld);
        }

        @Override
        public String getTitle()
        {
            return "Set " + name + ": " + dataNew;
        }
    }
}
