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

package com.kitfox.raven.swf.importer.timeline;

import com.kitfox.coyote.math.CyColor4f;
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
import com.kitfox.swf.tags.sprite.DefineSprite;
import java.util.ArrayList;

/**
 *
 * @author kitfox
 */
public class SWFTimelineBuilder
{
    private CharacterDictionary dictionary = new CharacterDictionary();
    CyColor4f backgroundColor;

    private SWFTimeline timeline;

    private SWFTimelineBuilder(SWFDocument swfDoc)
    {
        timeline = importTimeline(swfDoc.getTags());
    }
    
    public static SWFTimelineBuilder importDoc(SWFDocument swfDoc)
    {
        SWFTimelineBuilder builder = new SWFTimelineBuilder(swfDoc);
        return builder;
    }
    
    private SWFTimeline importTimeline(ArrayList<SWFTag> tags)
    {
        SWFTimeline curTimeline = new SWFTimeline();
        
        for (SWFTag tag: tags)
        {
            switch (tag.getTagId())
            {
                case ShowFrame.TAG_ID:
                    curTimeline.showFrame();
                    break;

                case PlaceObject.TAG_ID:
                {
                    PlaceObject obj = (PlaceObject)tag;
                    curTimeline.placeObject(obj.getCharacterId(), obj.getDepth(),
                            obj.getMatrix(), "");
                    break;
                }
                case PlaceObject2.TAG_ID:
                {
                    PlaceObject2 obj = (PlaceObject2)tag;
                    curTimeline.placeObject(obj.getCharacterId(), obj.getDepth(),
                            obj.getMatrix(), obj.getName());
                    break;
                }
                case PlaceObject3.TAG_ID:
                {
                    PlaceObject3 obj = (PlaceObject3)tag;
                    curTimeline.placeObject(obj.getCharacterId(), obj.getDepth(),
                            obj.getMatrix(), obj.getName());
                    break;
                }
                case RemoveObject.TAG_ID:
                {
                    RemoveObject obj = (RemoveObject)tag;
                    curTimeline.removeObject(obj.getDepth());
                    break;
                }
                case RemoveObject2.TAG_ID:
                {
                    RemoveObject2 obj = (RemoveObject2)tag;
                    curTimeline.removeObject(obj.getDepth());
                    break;
                }

                case SetBackgroundColor.TAG_ID:
                    backgroundColor = new CyColor4f(
                            ((SetBackgroundColor)tag).getBgColor().asArgb());
                    break;

                case DefineShape.TAG_ID:
                {
                    DefineShape shape = (DefineShape)tag;
                    dictionary.addCharacter(new CharacterShape(
                            shape.getShapeId(), ((DefineShape)tag).getShapes()));
                    
                    break;
                }
                case DefineShape2.TAG_ID:
                {
                    DefineShape2 shape = (DefineShape2)tag;
                    dictionary.addCharacter(new CharacterShape(
                            shape.getShapeId(), ((DefineShape2)tag).getShapes()));
                    break;
                }
                case DefineShape3.TAG_ID:
                {
                    DefineShape3 shape = (DefineShape3)tag;
                    dictionary.addCharacter(new CharacterShape(
                            shape.getShapeId(), ((DefineShape3)tag).getShapes()));
                    break;
                }
                case DefineShape4.TAG_ID:
                {
                    DefineShape4 shape = (DefineShape4)tag;
                    dictionary.addCharacter(new CharacterShape(
                            shape.getShapeId(), ((DefineShape4)tag).getShapes()));
                    break;
                }
                case DefineSprite.TAG_ID:
                {
                    DefineSprite sprite = (DefineSprite)tag;
                    SWFTimeline tl = importTimeline(sprite.getTags());
                    dictionary.addCharacter(new CharacterSprite(
                            sprite.getSpriteId(), tl));
                    break;
                }
            }
        }
        
        return curTimeline;
    }

    /**
     * @return the dictionary
     */
    public CharacterDictionary getDictionary()
    {
        return dictionary;
    }

    /**
     * @return the timeline
     */
    public SWFTimeline getTimeline()
    {
        return timeline;
    }
    
}
