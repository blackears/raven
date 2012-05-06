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

import com.kitfox.raven.swf.importer.timeline.CharacterDictionary;
import com.kitfox.raven.swf.importer.timeline.CharacterShape;
import com.kitfox.raven.editor.node.scene.RavenNodeGroup;
import com.kitfox.raven.editor.node.scene.RavenNodeMesh2;
import com.kitfox.raven.editor.node.scene.RavenNodeRoot;
import com.kitfox.raven.editor.node.scene.RavenNodeSceneGraph;
import com.kitfox.raven.paint.common.RavenPaintColor;
import com.kitfox.raven.util.tree.NodeObjectProviderIndex;
import com.kitfox.raven.util.tree.PropertyDataReference;
import com.kitfox.raven.util.tree.Track;
import com.kitfox.raven.util.tree.TrackLibrary;
import com.kitfox.swf.SWFDocument;
import com.kitfox.swf.tags.SWFTag;
import com.kitfox.swf.tags.control.SetBackgroundColor;
import com.kitfox.swf.tags.display.PlaceObject;
import com.kitfox.swf.tags.display.PlaceObject2;
import com.kitfox.swf.tags.display.PlaceObject3;
import com.kitfox.swf.tags.display.RemoveObject;
import com.kitfox.swf.tags.display.RemoveObject2;
import com.kitfox.swf.tags.display.ShowFrame;
import com.kitfox.swf.tags.shapes.DefineShape;
import com.kitfox.swf.tags.shapes.DefineShape2;
import com.kitfox.swf.tags.shapes.DefineShape3;
import com.kitfox.swf.tags.shapes.DefineShape4;
import com.kitfox.swf.tags.shapes.ShapeWithStyle;
import java.awt.Color;

/**
 *
 * @author kitfox
 */
public class ImportSWFBuilder
{
    final RavenNodeRoot root;
    int meshCount;
    CharacterDictionary dictionary;
    DisplayList displayList;

    private boolean importBackgroundColor;

    RavenNodeGroup importGroup;
    RavenNodeGroup characterGroup;
    Track track;


    @Deprecated
    public ImportSWFBuilder(RavenNodeRoot root)
    {
        this.root = root;
    }

    /**
     * @return the importBackgroundColor
     */
    public boolean isImportBackgroundColor()
    {
        return importBackgroundColor;
    }

    /**
     * @param importBackgroundColor the importBackgroundColor to set
     */
    public void setImportBackgroundColor(boolean importBackgroundColor)
    {
        this.importBackgroundColor = importBackgroundColor;
    }

    public void importDoc(SWFDocument swfDoc)
    {
        importGroup = NodeObjectProviderIndex.inst().createNode(
                RavenNodeGroup.class, root);
        importGroup.setName("swfImport");
        RavenNodeSceneGraph sg = root.getSceneGraph();
        sg.add(importGroup);

        characterGroup = NodeObjectProviderIndex.inst().createNode(
                RavenNodeGroup.class, root);
        characterGroup.setName("characters");
        importGroup.children.add(characterGroup);
        characterGroup.visible.setValue(false);

        TrackLibrary trackLib = root.getTrackLibrary();

        track = NodeObjectProviderIndex.inst().createNode(Track.class, root);
        String name = root.createUniqueName("swfTrack");
        track.setName(name);
        trackLib.tracks.add(track);

        displayList = new DisplayList(this);

        for (SWFTag tag: swfDoc.getTags())
        {
            switch (tag.getTagId())
            {
                case ShowFrame.TAG_ID:
                    displayList.showFrame();
                    break;

                case PlaceObject.TAG_ID:
                {
                    PlaceObject obj = (PlaceObject)tag;
                    displayList.placeObject(obj.getCharacterId(), obj.getDepth(),
                            obj.getMatrix());
                    break;
                }
                case PlaceObject2.TAG_ID:
                {
                    PlaceObject2 obj = (PlaceObject2)tag;
                    displayList.placeObject(obj.getCharacterId(), obj.getDepth(),
                            obj.getMatrix(), obj.getName());
                    break;
                }
                case PlaceObject3.TAG_ID:
                {
                    PlaceObject3 obj = (PlaceObject3)tag;
                    displayList.placeObject(obj.getCharacterId(), obj.getDepth(),
                            obj.getMatrix(), obj.getName());
                    break;
                }
                case RemoveObject.TAG_ID:
                {
                    RemoveObject obj = (RemoveObject)tag;
                    displayList.removeObject(obj.getDepth());
                    break;
                }
                case RemoveObject2.TAG_ID:
                {
                    RemoveObject2 obj = (RemoveObject2)tag;
                    displayList.removeObject(obj.getDepth());
                    break;
                }

                case SetBackgroundColor.TAG_ID:
                    setBackgroundColor((SetBackgroundColor)tag);
                    break;

                case DefineShape.TAG_ID:
                {
                    DefineShape shape = (DefineShape)tag;
                    dictionary.addCharacter(new CharacterShape(
                            shape.getShapeId(), ((DefineShape)tag).getShapes()));
                    
//                    importShape(shape.getShapeId(), ((DefineShape)tag).getShapes());
                    break;
                }
                case DefineShape2.TAG_ID:
                {
                    DefineShape2 shape = (DefineShape2)tag;
                    dictionary.addCharacter(new CharacterShape(
                            shape.getShapeId(), ((DefineShape)tag).getShapes()));
//                    importShape(shape.getShapeId(), ((DefineShape2)tag).getShapes());
                    break;
                }
                case DefineShape3.TAG_ID:
                {
                    DefineShape3 shape = (DefineShape3)tag;
                    dictionary.addCharacter(new CharacterShape(
                            shape.getShapeId(), ((DefineShape)tag).getShapes()));
//                    importShape(shape.getShapeId(), ((DefineShape3)tag).getShapes());
                    break;
                }
                case DefineShape4.TAG_ID:
                {
                    DefineShape4 shape = (DefineShape4)tag;
                    dictionary.addCharacter(new CharacterShape(
                            shape.getShapeId(), ((DefineShape)tag).getShapes()));
//                    importShape(shape.getShapeId(), ((DefineShape4)tag).getShapes());
                    break;
                }
            }
        }

        //Switch to track we just created
        trackLib.curTrack.setData(new PropertyDataReference<Track>(track.getUid()));
//        trackLib.synchDocumentToFrame();
    }

    private void importShape(int shapeId, ShapeWithStyle sws)
    {
        RavenNodeMesh2 mesh = NodeObjectProviderIndex.inst().createNode(
                RavenNodeMesh2.class, root);
        mesh.setName(root.createUniqueName("mesh"));
        characterGroup.children.add(mesh);

        MeshBuilder builder = new MeshBuilder();
        sws.buildShapes(builder);

        mesh.setNetworkMesh(builder.mesh, false);
        
        displayList.registerCharacter(shapeId, mesh);
    }

    private void setBackgroundColor(SetBackgroundColor tag)
    {
        if (!importBackgroundColor)
        {
            return;
        }

        Color col = tag.getBgColor().asColor();
        root.background.setValue(new RavenPaintColor(col));
    }
    
}
