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

package com.kitfox.raven.editor.action.bezier;

import com.kitfox.raven.editor.RavenDocument;
import com.kitfox.raven.editor.RavenEditor;
import com.kitfox.raven.editor.node.tools.common.ServiceBezierNetwork;
import com.kitfox.raven.shape.bezier.BezierNetwork;
import com.kitfox.raven.shape.bezier.BezierVertex;
import com.kitfox.raven.shape.bezier.VertexSmooth;
import com.kitfox.raven.util.Selection;
import com.kitfox.raven.util.tree.NodeObject;
import com.kitfox.raven.util.tree.SelectionRecord;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import javax.swing.AbstractAction;

/**
 *
 * @author kitfox
 */
abstract public class ActionPathVertexSmoothingChange extends AbstractAction
{
    final VertexSmooth vertexSmoothing;

    public ActionPathVertexSmoothingChange(String name, VertexSmooth vertexSmoothing)
    {
        super(name);
        this.vertexSmoothing = vertexSmoothing;
    }


    @Override
    public void actionPerformed(ActionEvent e)
    {
        RavenDocument doc = RavenEditor.inst().getDocument();
        if (doc == null)
        {
            return;
        }
        Selection<NodeObject> sel = doc.getCurSymbol().getSelection();
        for (NodeObject nodeObj: sel.getSelection())
        {
            ServiceBezierNetwork provBez =
                    nodeObj.getNodeService(ServiceBezierNetwork.class, false);

            if (provBez == null)
            {
                continue;
            }

            BezierNetwork network = provBez.getBezierNetwork();


            BezierNetwork.Subselection sub =
                    sel.getSubselection(nodeObj, BezierNetwork.Subselection.class);
            if (sub != null)
            {
                ArrayList<BezierVertex> vtxList = network.getVertices();
                for (BezierVertex vtx: vtxList)
                {
                    if (sub.contains(vtx))
                    {
                        vtx.setData(VertexSmooth.PlaneData.class, vertexSmoothing);
                        network.applyVertexSmoothing(vertexSmoothing, vtx);
                    }
                }
            }

            provBez.updateFromBezierNetwork(network, true);
        }

    }

}
