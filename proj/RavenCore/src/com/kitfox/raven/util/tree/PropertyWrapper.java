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
 *
 * @author kitfox
 */
public class PropertyWrapper<NodeType extends NodeObject, PropType>
{
    private final NodeType node;
    private final String name;
    private final Class<PropType> propertyType;

    PropertyData<PropType> directValue;
//    PropertyData<PropType> lastUndoableValue;

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
    HashMap<Integer, TrackCurve<PropType>> trackMap
            = new HashMap<Integer, TrackCurve<PropType>>();

//    HashMap<FrameKey, SoftReference<PropertyData<PropType>>> interpCache
//            = new HashMap<FrameKey, SoftReference<PropertyData<PropType>>>();
//    HashMap<Integer, TrackCache<PropType>> interpCache
//            = new HashMap<Integer, TrackCache<PropType>>();
    
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
        PropertyChangeEvent evt = new PropertyChangeEvent(this, name, oldValue, newValue);
        for (int i = 0; i < listeners.size(); ++i)
        {
            listeners.get(i).propertyWrapperDataChanged(evt);
        }
        node.notifyNodePropertyChanged(evt);
    }

    protected void firePropertyTrackChanged(int trackUid)
    {
        PropertyTrackChangeEvent evt =
                new PropertyTrackChangeEvent(this, trackUid);
        for (int i = 0; i < listeners.size(); ++i)
        {
            listeners.get(i).propertyWrapperTrackChanged(evt);
        }
    }

    protected void firePropertyTrackKeyChanged(int trackUid, int frame)
    {
        PropertyTrackKeyChangeEvent evt =
                new PropertyTrackKeyChangeEvent(this, trackUid, frame);
        for (int i = 0; i < listeners.size(); ++i)
        {
            listeners.get(i).propertyWrapperTrackKeyChanged(evt);
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
        return directValue == null ? null : directValue.getValue(node.getDocument());
    }

    public PropertyData<PropType> getData()
    {
        return directValue;
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
//        if (history)
//        {
//            if ((lastUndoableValue == null && value == null) ||
//                    (lastUndoableValue != null && lastUndoableValue.equals(value)))
//            {
//                return;
//            }
//            
//            SetValueDirectAction action = new SetValueDirectAction(lastUndoableValue, value);
//            doAction(action);
//        }
//        else
//        {
//            //Don't write to history
//            //action.redo();
//            PropertyData<PropType> dataOld = directValue;
//            directValue = value;
//            firePropertyDataChanged(dataOld, value);
//        }
    }

    protected void doAction(HistoryAction action)
    {
        NodeDocument doc = node.getDocument();
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

    public ArrayList<Integer> getTrackUids()
    {
        return new ArrayList<Integer>(trackMap.keySet());
    }

    public TrackCurve<PropType> getTrackCurve(int trackUid)
    {
        TrackCurve<PropType> curve = trackMap.get(trackUid);
        return curve == null ? null : new TrackCurve<PropType>(curve);
    }

    public void setTrackCurve(int trackUid, TrackCurve<PropType> curve)
    {
        setTrackCurve(trackUid, curve, true);
    }

    public void setTrackCurve(int trackUid, TrackCurve<PropType> curve, boolean history)
    {
        if (!isAnimatable())
        {
            return;
        }

        TrackCurve<PropType> curveOld = trackMap.get(trackUid);
        if (curve.equals(curveOld))
        {
            return;
        }

        SetTrackCurveAction action = new SetTrackCurveAction(trackUid, curveOld,
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

    public void deleteTrackCurve(int trackUid)
    {
        if (!isAnimatable())
        {
            return;
        }

        TrackCurve<PropType> curveOld = trackMap.get(trackUid);
        if (curveOld == null)
        {
            return;
        }

        DeleteTrackCurveAction action = new DeleteTrackCurveAction(trackUid, curveOld);
        doAction(action);
    }

    /**
     * @return the node
     */
    public NodeType getNode()
    {
        return node;
    }

    public boolean isKeyAt(int trackUid, int frame)
    {
        TrackCurve<PropType> curve = trackMap.get(trackUid);
        return curve == null ? false : curve.isKeyAt(frame);
    }

    public boolean isInterpolatedAt(int trackUid, int frame)
    {
        TrackCurve<PropType> curve = trackMap.get(trackUid);
        return curve == null ? false : curve.isInterpolatedAt(frame);
    }

    public void setKeyAt(int trackUid, int frame,
            PropertyData<PropType> data)
    {
        setKeyAt(trackUid, frame, data, TrackKey.Interp.CONST);
    }

    public void setKeyAt(int trackUid, int frame,
            PropertyData<PropType> data, TrackKey.Interp interp)
    {
        setKeyAt(trackUid, frame, data, interp, 1, 0, 1, 0);
    }

    public void setKeyAt(int trackUid, int frame,
            PropertyData<PropType> data, TrackKey.Interp interp,
            double dxIn, double dyIn, double dxOut, double dyOut)
    {
        if (!isAnimatable())
        {
            return;
        }

        SetTrackKeyAction action = new SetTrackKeyAction(trackUid, frame, 
                data, interp, dxIn, dyIn, dxOut, dyOut);
        doAction(action);
    }

    public void removeKeyAt(int trackUid, int frame)
    {
        if (!isAnimatable())
        {
            return;
        }

        TrackCurve curve = trackMap.get(trackUid);
        if (curve == null)
        {
            return;
        }
        if (!curve.isKeyAt(frame))
        {
            return;
        }

        DeleteTrackKeyAction action = new DeleteTrackKeyAction(trackUid, frame);
        doAction(action);
    }

    public void synchToTrack(int trackUid, int frame)
    {
        synchToTrack(new FrameKey(trackUid, frame));
    }
    
    public void synchToTrack(FrameKey key)
    {
        ValueCache<PropType> cache = getOrCreateValueCache(key);
        if (cache.key.equals(FrameKey.DIRECT))
        {
            return;
        }
        
        PropertyData<PropType> oldValue = directValue;
        ValueCache<PropType> cacheDirect = getOrCreateValueCache(FrameKey.DIRECT);
        cacheDirect.copyState(cache);
        directValue = cache.data;
        firePropertyDataChanged(oldValue, directValue);
        
        
        
        
        
//        TrackCurve<PropType> curve = trackMap.get(trackUid);
//        if (curve == null)
//        {
//            return;
//        }
//
//        if (curve.isKeyAt(frame))
//        {
//            TrackKey<PropType> curveKey = curve.getKey(frame);
//            setData(curveKey.getData(), false);
//        }
//        else if (curve.isInterpolatedAt(frame))
//        {
////            PropType value = curve.evaluate(frame, getNode().getDocument());
//            PropertyData<PropType> value = getData(trackUid, frame);
//            setData(value, false);
//        }
    }

    public PropType getValue(FrameKey key)
    {
        PropertyData<PropType> data = getData(key);
        
        return data == null ? getValue() : data.getValue(node.getDocument());
    }

    public PropType getValue(int trackUid, int frame)
    {
        return getValue(new FrameKey(trackUid, frame));
    }

    protected ValueCache<PropType> getOrCreateValueCache(FrameKey key)
    {
        ValueCache<PropType> cache = valueCache.get(key);
        if (cache != null)
        {
            return cache;
        }
        
        int trackUid = key.getTrackUid();
        int frame = key.getAnimFrame();
        TrackCurve<PropType> curve = trackMap.get(trackUid);
        
        if (curve != null)
        {
            if (curve.isKeyAt(frame))
            {
                TrackKey<PropType> curveKey = curve.getKey(frame);
                cache = new ValueCache(key, curveKey.getData());
                valueCache.put(key, cache);
                return cache;
            }
            else if (curve.isInterpolatedAt(frame))
            {
                PropType curveValue = curve.evaluate(frame, getNode().getDocument());
                cache = new ValueCache(key, new PropertyDataInline(curveValue));
                valueCache.put(key, cache);
                return cache;
            }
        }

        //Not handled by anim curves.  Return direct value
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

    public PropertyData<PropType> getData(int trackUid, int frame)
    {
        return getData(new FrameKey(trackUid, frame));
//        TrackCurve<PropType> curve = trackMap.get(trackUid);
//        if (curve == null)
//        {
//            return directValue;
//        }
//
//        if (curve.isKeyAt(frame))
//        {
//            TrackKey<PropType> curveKey = curve.getKey(frame);
//            return curveKey.getData();
//        }
//        else if (curve.isInterpolatedAt(frame))
//        {
//            ValueCache<PropType> cache = valueCache.get(trackUid);
//            if (cache == null)
//            {
//                cache = new ValueCache<PropType>();
//                interpCache.put(trackUid, cache);
//            }
//
//            PropertyData<PropType> value = cache.get(frame);
//
//            if (value == null)
//            {
//                PropType curveValue = curve.evaluate(frame, getNode().getDocument());
//                value = new PropertyDataInline(curveValue);
//                cache.set(frame, value);
//            }
//
//            return value;
//        }
//
//        return directValue;
    }

    public PropertyType export()
    {
        PropertyType propType = new PropertyType();

        propType.setName(name);
        propType.setDirect(export(directValue));

        NodeDocument doc = node.getDocument();
        for (Integer trackUid: trackMap.keySet())
        {
            NodeObject trackNode = doc.getNode(trackUid);
            if (trackNode == null)
            {
                //Do not export track if its track object has been deleted
                continue;
            }

            TrackType trackType = exportTrack(trackUid);
            if (trackType == null)
            {
                continue;
            }
            propType.getTrack().add(trackType);
        }

        return propType;
    }

    public TrackType exportTrack(int trackUid)
    {
        TrackCurve<PropType> curve = trackMap.get(trackUid);
        if (curve.isEmpty())
        {
            return null;
        }

        TrackType trackType = new TrackType();

        trackType.setTrackUid(trackUid);
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

            PropType value = ((PropertyDataInline<PropType>)data).getValue(node.getDocument());
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
        setData(load(type.getDirect()));

        for (TrackType trackType: type.getTrack())
        {
            TrackCurve<PropType> curve = createTrackCurve(trackType);
            
            setTrackCurve(trackType.getTrackUid(), curve);
        }
    }

    public TrackCurve<PropType> createTrackCurve(TrackType trackType)
    {
        //Create track
        TrackCurve<PropType> curve =
                new TrackCurve<PropType>(propertyType);
        curve.setBefore(TrackCurve.Repeat.valueOf(trackType.getBefore().name()));
        curve.setAfter(TrackCurve.Repeat.valueOf(trackType.getAfter().name()));

        //Add keys
        for (TrackKeyType keyType: trackType.getKey())
        {
            TrackKey<PropType> key =
                    new TrackKey<PropType>(load(keyType.getData()),
                    TrackKey.Interp.valueOf(keyType.getInterpolation().name()),
                    keyType.getTanInX(), keyType.getTanInY(),
                    keyType.getTanOutX(), keyType.getTanOutY());

            curve.setKey(keyType.getFrame(), key);
        }

        return curve;
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

    public int getPrevKeyFrame(int curFrame, int trackUid)
    {
        int bestFrame = Integer.MIN_VALUE;

        TrackCurve<PropType> curve = trackMap.get(trackUid);
        if (curve == null)
        {
            return bestFrame;
        }

        return curve.getPrevKeyFrame(curFrame);
    }

    public int getNextKeyFrame(int curFrame, int trackUid)
    {
        int bestFrame = Integer.MAX_VALUE;

        TrackCurve<PropType> curve = trackMap.get(trackUid);
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

    protected void invalidateCacheAtKey(int trackUid, int frame)
    {
        TrackCurve<PropType> curve = trackMap.get(trackUid);
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
            FrameKey cacheKey = new FrameKey(trackUid, i);
            valueCache.remove(cacheKey);
        }
    }
    
    protected void invalidateCacheAtTrack(int trackUid)
    {
        for (Iterator<FrameKey> it = valueCache.keySet().iterator(); it.hasNext();)
        {
            FrameKey key = it.next();
            if (key.getTrackUid() == trackUid)
            {
                it.remove();
            }
        }
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
        private final boolean removeCurve;
        private final TrackCurve<PropType> curve;
        private final TrackKey<PropType> keyOld;
        private final int frame;
        private final int trackUid;

        public DeleteTrackKeyAction(int trackUid, int frame)
        {
            this.frame = frame;
            this.trackUid = trackUid;
            this.curve = trackMap.get(trackUid);
            this.removeCurve = curve.getNumKeys() == 1;
            this.keyOld = curve.getKey(frame);
        }

        @Override
        public void redo(History history)
        {
            invalidateCacheAtKey(trackUid, frame);
            
            curve.removeKey(frame);
            firePropertyTrackKeyChanged(trackUid, frame);
            if (removeCurve)
            {
                trackMap.remove(trackUid);
                firePropertyTrackChanged(trackUid);
            }
//            interpCache.remove(trackUid);
        }

        @Override
        public void undo(History history)
        {
            if (removeCurve)
            {
                trackMap.put(trackUid, curve);
                firePropertyTrackChanged(trackUid);
            }
            curve.setKey(frame, keyOld);
//            interpCache.remove(trackUid);
            invalidateCacheAtKey(trackUid, frame);
            firePropertyTrackKeyChanged(trackUid, frame);
        }

        @Override
        public String getTitle()
        {
            return "Remove Key";
        }
    }

    public class SetTrackKeyAction implements HistoryAction
    {
        private final boolean createCurve;
        private final TrackCurve<PropType> curve;
        private final TrackKey<PropType> keyOld;
        private final TrackKey<PropType> keyNew;
        private final int frame;
        private final int trackUid;

        public SetTrackKeyAction(int trackUid, int frame,
                PropertyData<PropType> dataNew, TrackKey.Interp interp,
                double dxIn, double dyIn, double dxOut, double dyOut)
        {
            this.frame = frame;
            this.trackUid = trackUid;

            TrackCurve mapCurve = trackMap.get(trackUid);
            this.createCurve = mapCurve == null;
            this.curve = createCurve 
                    ? new TrackCurve<PropType>(propertyType)
                    : mapCurve;
            this.keyOld = curve == null ? null : curve.getKey(frame);
            this.keyNew = new TrackKey<PropType>(dataNew, interp,
                    dxIn, dyIn, dxOut, dyOut);
        }

        @Override
        public void redo(History history)
        {
            if (createCurve)
            {
                trackMap.put(trackUid, curve);
                firePropertyTrackChanged(trackUid);
            }
            curve.setKey(frame, keyNew);
//            interpCache.remove(trackUid);
            invalidateCacheAtKey(trackUid, frame);
            firePropertyTrackKeyChanged(trackUid, frame);
        }

        @Override
        public void undo(History history)
        {
            invalidateCacheAtKey(trackUid, frame);
            if (keyOld == null)
            {
                curve.removeKey(frame);
//                interpCache.remove(trackUid);
                firePropertyTrackKeyChanged(trackUid, frame);
            }
            else
            {
                curve.setKey(frame, keyOld);
//                interpCache.remove(trackUid);
                firePropertyTrackKeyChanged(trackUid, frame);
            }

            if (createCurve)
            {
                trackMap.remove(trackUid);
//                interpCache.remove(trackUid);
                firePropertyTrackChanged(trackUid);
            }
        }

        @Override
        public String getTitle()
        {
            return "Set Key";
        }
    }

    public class DeleteTrackCurveAction implements HistoryAction
    {
        final int trackUid;
        final TrackCurve<PropType> curveOld;

        public DeleteTrackCurveAction(int trackUid, TrackCurve<PropType> curveOld)
        {
            this.trackUid = trackUid;
            this.curveOld = curveOld;
        }

        @Override
        public void undo(History history)
        {
            trackMap.put(trackUid, curveOld);
            invalidateCacheAtTrack(trackUid);
//            interpCache.remove(trackUid);
            firePropertyTrackChanged(trackUid);
        }

        @Override
        public void redo(History history)
        {
            invalidateCacheAtTrack(trackUid);
            trackMap.remove(trackUid);
//            interpCache.remove(trackUid);
            firePropertyTrackChanged(trackUid);
        }

        @Override
        public String getTitle()
        {
            return "Delete Track";
        }
    }

    public class SetTrackCurveAction implements HistoryAction
    {
        int trackUid;
        final TrackCurve<PropType> curveOld;
        final TrackCurve<PropType> curveNew;

        public SetTrackCurveAction(int trackUid, TrackCurve<PropType> curveOld, TrackCurve<PropType> curveNew)
        {
            this.trackUid = trackUid;
            this.curveOld = curveOld;
            this.curveNew = curveNew;
        }

        @Override
        public void undo(History history)
        {
            invalidateCacheAtTrack(trackUid);
            if (curveOld == null)
            {
                trackMap.remove(trackUid);
            }
            else
            {
                trackMap.put(trackUid, curveOld);
            }
//            interpCache.remove(trackUid);
            firePropertyTrackChanged(trackUid);
        }

        @Override
        public void redo(History history)
        {
            trackMap.put(trackUid, curveNew);
            invalidateCacheAtTrack(trackUid);
//            interpCache.remove(trackUid);
            firePropertyTrackChanged(trackUid);
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

        public SetValueDirectAction(PropertyData<PropType> dataOld,
                PropertyData<PropType> dataNew)
        {
            this.dataOld = dataOld;
            this.dataNew = dataNew;
        }

        @Override
        public void undo(History history)
        {
            invalidateCacheAtDirect();
            //lastUndoableValue = directValue = dataOld;
            directValue = dataOld;
            firePropertyDataChanged(dataNew, dataOld);
        }

        @Override
        public void redo(History history)
        {
            invalidateCacheAtDirect();
//            lastUndoableValue = directValue = dataNew;
            directValue = dataNew;
            firePropertyDataChanged(dataOld, dataNew);
        }

        @Override
        public String getTitle()
        {
            return "Set " + name + ": " + dataNew;
        }
    }
}
