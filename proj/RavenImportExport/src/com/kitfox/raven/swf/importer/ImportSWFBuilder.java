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

package com.kitfox.raven.swf.importer;

import com.kitfox.raven.editor.RavenDocument;
import com.kitfox.raven.editor.node.scene.RavenNodeMesh;
import com.kitfox.raven.editor.node.scene.RavenSymbol;
import com.kitfox.raven.swf.importer.timeline.CharacterDictionary;
import com.kitfox.raven.swf.importer.timeline.CharacterShape;
import com.kitfox.raven.swf.importer.timeline.CharacterSprite;
import com.kitfox.raven.swf.importer.timeline.SWFCharacter;
import com.kitfox.raven.swf.importer.timeline.SWFEventPlaceCharacter;
import com.kitfox.raven.swf.importer.timeline.SWFTimeline;
import com.kitfox.raven.swf.importer.timeline.SWFTimelineBuilder;
import com.kitfox.raven.swf.importer.timeline.SWFTimelineTrack;
import com.kitfox.raven.swf.importer.timeline.SWFTrackEvent;
import com.kitfox.raven.util.tree.NodeObjectProviderIndex;
import com.kitfox.raven.util.tree.NodeSymbolProvider;
import com.kitfox.raven.util.tree.NodeSymbolProviderIndex;
import com.kitfox.raven.util.tree.PropertyDataInline;
import com.kitfox.raven.util.tree.PropertyDataReference;
import com.kitfox.raven.util.tree.Track;
import com.kitfox.raven.util.tree.TrackLibrary;
import com.kitfox.raven.util.undo.History;
import com.kitfox.swf.SWFDocument;
import com.kitfox.swf.dataType.MATRIX;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 *
 * @author kitfox
 */
public class ImportSWFBuilder
{
    RavenDocument doc;
    HashMap<Integer, RavenSymbol> symbolMap = 
            new HashMap<Integer, RavenSymbol>();

    SWFTimelineBuilder builder;
    
    public ImportSWFBuilder(RavenDocument doc)
    {
        this.doc = doc;
    }

    public void importDoc(SWFDocument swfDoc)
    {
        builder = SWFTimelineBuilder.importDoc(swfDoc);
        
        History hist = doc.getHistory();
        hist.beginTransaction("importSwf");
        
        //First, add all sprites
        CharacterDictionary dict = builder.getDictionary();
        for (SWFCharacter ch: dict.getCharacters())
        {
            if (ch instanceof CharacterSprite)
            {
                importSprite((CharacterSprite)ch);
            }
        }
        
        //Build root timeline
        RavenSymbol sym = (RavenSymbol)doc.getCurSymbol();
        importTimeline(sym, builder.getTimeline());

        hist.commitTransaction();
        
//        RavenEditor.inst().getPlayer().getPlayState()
        TrackLibrary trackLib = doc.getCurSymbol().getRoot().getTrackLibrary();
        trackLib.synchDocumentToFrame();
    }

    private void importSprite(CharacterSprite sprite)
    {
        NodeSymbolProvider<RavenSymbol> prov = 
                NodeSymbolProviderIndex.inst().getProvider(RavenSymbol.class);
        RavenSymbol sym = prov.create(doc);
        
        String newName = doc.getUnusedSymbolName("swfSymbol");
        sym.setName(newName);
        doc.addSymbol(sym);

        symbolMap.put(sprite.getId(), sym);
        importTimeline(sym, sprite.getTimeline());
    }

    private void importTimeline(RavenSymbol sym, SWFTimeline timeline)
    {
        TrackLibrary trackLib = sym.getRoot().getTrackLibrary();
        Track track = NodeObjectProviderIndex.inst().createNode(Track.class, sym);
        String name = sym.createUniqueName("swfTrack");
        track.setName(name);
        trackLib.tracks.add(track);
        
        ArrayList<SWFTimelineTrack> tracks = timeline.getTracks();
        Collections.sort(tracks);
        for (SWFTimelineTrack timeTrack: tracks)
        {
            if (containsShapes(timeTrack))
            {
                addShapeTrack(sym, track, timeTrack);
            }
            if (containsSprites(timeTrack))
            {
//                addSpriteTrack(root, track, timeTrack);
            }
        }

        trackLib.curTrack.setData(new PropertyDataReference<Track>(track.getUid()));
    }

    public boolean containsShapes(SWFTimelineTrack timeTrack)
    {
        CharacterDictionary dict = builder.getDictionary();

        for (SWFTrackEvent event: timeTrack.getEvents())
        {
            if (event instanceof SWFEventPlaceCharacter)
            {
                SWFEventPlaceCharacter place = (SWFEventPlaceCharacter)event;
                SWFCharacter ch = dict.getCharacter(place.getCharacterId());
                if (ch instanceof CharacterShape)
                {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean containsSprites(SWFTimelineTrack timeTrack)
    {
        CharacterDictionary dict = builder.getDictionary();

        for (SWFTrackEvent event: timeTrack.getEvents())
        {
            if (event instanceof SWFEventPlaceCharacter)
            {
                SWFEventPlaceCharacter place = (SWFEventPlaceCharacter)event;
                SWFCharacter ch = dict.getCharacter(place.getCharacterId());
                if (ch instanceof CharacterSprite)
                {
                    return true;
                }
            }
        }
        return false;
    }

    private void addShapeTrack(RavenSymbol sym, Track track, SWFTimelineTrack timeTrack)
    {
        RavenNodeMesh mesh = NodeObjectProviderIndex.inst().createNode(
                RavenNodeMesh.class, sym);
        mesh.setName(sym.createUniqueName("mesh"));
        
        CharacterDictionary dict = builder.getDictionary();
        CharacterShape prevShape = null;
        int trackUid = track.getUid();

        for (Integer frame: timeTrack.getKeyFrames())
        {
            SWFTrackEvent event = timeTrack.getKeyFrame(frame);
            
            if (event instanceof SWFEventPlaceCharacter)
            {
                SWFEventPlaceCharacter place = (SWFEventPlaceCharacter)event;
                SWFCharacter ch = dict.getCharacter(place.getCharacterId());
                if (ch instanceof CharacterShape)
                {
                    CharacterShape shape = (CharacterShape)ch;
                    
                    if (prevShape == null || prevShape.getId() != shape.getId())
                    {
                        MeshBuilder meshBuilder = new MeshBuilder();
                        shape.getSws().buildShapes(meshBuilder);

                        mesh.mesh.setKeyAt(trackUid, frame, 
                                new PropertyDataInline(meshBuilder.mesh));
                    }
                
                    MATRIX m = place.getMatrix();
                    double m00 = m.getM00();
                    double m10 = m.getM10();
                    double m01 = m.getM01();
                    double m11 = m.getM11();

                    double scaleX = Math.sqrt(m00 * m00 + m10 * m10);
                    double scaleY = Math.sqrt(m01 * m01 + m11 * m11);
                    double ang0 = Math.toDegrees(Math.atan2(m10, m00));
                    double ang1 = Math.toDegrees(Math.atan2(m11, m01));

                    mesh.transX.setKeyAt(trackUid, frame, 
                            new PropertyDataInline((float)m.getXlateX()));
                    mesh.transY.setKeyAt(trackUid, frame, 
                            new PropertyDataInline((float)m.getXlateY()));
                    mesh.scaleX.setKeyAt(trackUid, frame, 
                            new PropertyDataInline((float)scaleX));
                    mesh.scaleY.setKeyAt(trackUid, frame, 
                            new PropertyDataInline((float)scaleY));
                    mesh.rotation.setKeyAt(trackUid, frame, 
                            new PropertyDataInline((float)ang0));
                    mesh.skewAngle.setKeyAt(trackUid, frame, 
                            new PropertyDataInline((float)(ang1 - ang0)));
                    
                    prevShape = shape;
                }
                else
                {
                    //Remove geometry
                    mesh.mesh.setKeyAt(trackUid, frame, null);
                    prevShape = null;
                }
            }
            else
            {
                //Remove geometry
                mesh.mesh.setKeyAt(trackUid, frame, null);
                prevShape = null;
            }
        }
        
        sym.getRoot().getSceneGraph().add(mesh);
//        return mesh;
    }
    
}
