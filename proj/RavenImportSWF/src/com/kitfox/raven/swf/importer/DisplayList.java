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

import com.kitfox.game.control.color.Transform2DAngular;
import com.kitfox.raven.editor.node.scene.RavenNodeGroup;
import com.kitfox.raven.editor.node.scene.RavenNodeInstance;
import com.kitfox.raven.editor.node.scene.RavenNodeXformable;
import com.kitfox.raven.util.tree.NodeObjectProviderIndex;
import com.kitfox.raven.util.tree.PropertyDataInline;
import com.kitfox.raven.util.tree.PropertyDataReference;
import com.kitfox.swf.dataType.MATRIX;
import java.awt.geom.AffineTransform;
import java.util.HashMap;

/**
 *
 * @author kitfox
 */
public class DisplayList
{
    ImportSWFBuilder importTool;

    HashMap<Integer, RavenNodeXformable> characterMap =
            new HashMap<Integer, RavenNodeXformable>();
    HashMap<Integer, Tier> tierMap =
            new HashMap<Integer, Tier>();

    RavenNodeGroup tierGroup;
    int curFrame;

    public DisplayList(ImportSWFBuilder importTool)
    {
        this.importTool = importTool;

        tierGroup = NodeObjectProviderIndex.inst().createNode(
                RavenNodeGroup.class, importTool.root);
        tierGroup.setName("tiers");

        importTool.importGroup.children.add(tierGroup);
    }

    public void registerCharacter(int shapeId, RavenNodeXformable node)
    {
        characterMap.put(shapeId, node);
    }

    public void placeObject(int characterId, int depth, MATRIX matrix)
    {
        placeObject(characterId, depth, matrix, null);
    }

    public void placeObject(int characterId, int depth, MATRIX matrix, String name)
    {
        Tier tier = tierMap.get(depth);
        if (tier == null)
        {
            tier = new Tier(name, depth);
            tierMap.put(depth, tier);
        }

        tier.visible = true;
        tier.characterId = characterId;
        tier.matrix = matrix;
    }

    public void removeObject(int depth)
    {
        Tier tier = tierMap.get(depth);
        if (tier == null)
        {
            return;
        }

        tier.visible = false;
//        tier.characterId = -1;
    }

    public void showFrame()
    {
        int trackId = importTool.track.getUid();
        for (Tier tier: tierMap.values())
        {
            RavenNodeXformable character = characterMap.get(tier.characterId);

            PropertyDataReference<RavenNodeXformable> data =
                    new PropertyDataReference<RavenNodeXformable>(character.getUid());
            tier.node.source.setKeyAt(trackId, curFrame, data);

            tier.node.visible.setKeyAt(trackId, curFrame, new PropertyDataInline<Boolean>(tier.visible));

            AffineTransform xform = tier.matrix.asAffineTransform();
            Transform2DAngular x2 = Transform2DAngular.create(xform);

            tier.node.scaleX.setKeyAt(trackId, curFrame, new PropertyDataInline<Float>((float)x2.getScaleX()));
            tier.node.scaleY.setKeyAt(trackId, curFrame, new PropertyDataInline<Float>((float)x2.getScaleY()));
            tier.node.skewAngle.setKeyAt(trackId, curFrame, new PropertyDataInline<Float>((float)x2.getSkewAngle()));
            tier.node.rotation.setKeyAt(trackId, curFrame, new PropertyDataInline<Float>((float)x2.getRotate()));
            tier.node.transX.setKeyAt(trackId, curFrame, new PropertyDataInline<Float>((float)x2.getTransX()));
            tier.node.transY.setKeyAt(trackId, curFrame, new PropertyDataInline<Float>((float)x2.getTransY()));
        }

        ++curFrame;
    }

    //----------------------------
    class Tier
    {
        RavenNodeInstance node;
        int depth;
        boolean visible = true;
        int characterId;
        MATRIX matrix;

        public Tier(String name, int depth)
        {
            this.depth = depth;
            node = NodeObjectProviderIndex.inst().createNode(
                    RavenNodeInstance.class, importTool.root);
            if (name == null || "".equals(name))
            {
                name = "tier";
            }
            node.setName(name);

            tierGroup.children.add(node);

            int trackId = importTool.track.getUid();
            node.visible.setKeyAt(trackId, 0, new PropertyDataInline<Boolean>(false));
        }


    }

}
