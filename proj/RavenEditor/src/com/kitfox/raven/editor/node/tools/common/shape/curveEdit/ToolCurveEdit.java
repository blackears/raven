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

package com.kitfox.raven.editor.node.tools.common.shape.curveEdit;

import com.kitfox.coyote.material.color.CyMaterialColorDrawRecord;
import com.kitfox.coyote.material.color.CyMaterialColorDrawRecordFactory;
import com.kitfox.coyote.math.CyColor4f;
import com.kitfox.coyote.math.CyMatrix4d;
import com.kitfox.coyote.math.CyVector2d;
import com.kitfox.coyote.renderer.CyDrawStack;
import com.kitfox.coyote.renderer.CyVertexBuffer;
import com.kitfox.coyote.renderer.vertex.CyVertexBufferDataSquare;
import com.kitfox.coyote.renderer.vertex.CyVertexBufferDataSquareLines;
import com.kitfox.coyote.shape.CyPath2d;
import com.kitfox.coyote.shape.ShapeLinesProvider;
import com.kitfox.coyote.shape.bezier.BezierCurve2d;
import com.kitfox.coyote.shape.bezier.path.cut.Coord;
import com.kitfox.raven.editor.RavenEditor;
import com.kitfox.raven.editor.node.scene.RavenSymbolRoot;
import com.kitfox.raven.editor.node.scene.RenderContext;
import com.kitfox.raven.editor.node.tools.ToolProvider;
import com.kitfox.raven.editor.node.tools.ToolUser;
import com.kitfox.raven.editor.node.tools.common.ToolDisplay;
import com.kitfox.raven.editor.node.tools.common.shape.ServiceShapeManip;
import com.kitfox.raven.shape.network.pick.NetworkHandleEdge;
import com.kitfox.raven.shape.network.pick.NetworkHandleSelection;
import com.kitfox.raven.shape.network.pick.NetworkHandleVertex;
import com.kitfox.raven.util.PropertiesData;
import com.kitfox.raven.util.Selection;
import com.kitfox.raven.util.Selection.Operator;
import com.kitfox.raven.util.service.ServiceInst;
import com.kitfox.raven.util.tree.NodeObject;
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Properties;

/**
 *
 * @author kitfox
 */
@Deprecated
public class ToolCurveEdit extends ToolDisplay
{
    Provider toolProvider;
    MouseEvent mouseStart;
    MouseEvent mouseCur;

    protected ToolCurveEdit(ToolUser user, Provider toolProvider)
    {
        super(user);
        this.toolProvider = toolProvider;
    }

    @Override
    protected void click(MouseEvent evt)
    {
        Operator op = getSelectType(evt);

        switch (op)
        {
            case REPLACE:
                break;
            case ADD:
                break;
            case SUB:
                break;
            case INVERSE:
                break;
        }
    }

    @Override
    protected void startDrag(MouseEvent evt)
    {
        mouseCur = mouseStart = evt;
    }

    @Override
    protected void dragTo(MouseEvent evt)
    {
        mouseCur = evt;
    }

    @Override
    protected void endDrag(MouseEvent evt)
    {
        mouseCur = null;
        mouseStart = null;
    }

    @Override
    public void cancel()
    {
    }

    @Override
    public void dispose()
    {
    }
    
    @Override
    public void render(RenderContext ctx)
    {
        super.render(ctx);
        
        CyDrawStack stack = ctx.getDrawStack();
//        stack.pushFrame(null);
        
        drawGraph(stack);
        
        if (mouseStart != null)
        {
            int x0 = mouseStart.getX();
            int y0 = mouseStart.getY();
            int x1 = mouseCur.getX();
            int y1 = mouseCur.getY();
            
            drawMarquisRect(stack, x0, y0, x1, y1);
        }
        
//        stack.popFrame();
    }

    private void drawGraph(CyDrawStack stack)
    {
        Selection<NodeObject> sel = getSelection();
        
        for (NodeObject node: sel.getSelection())
        {
            ServiceShapeManip serv = 
                    node.getNodeService(ServiceShapeManip.class, false);
            if (serv != null)
            {
                drawGraph(stack, sel, node, serv);
            }
        }
    }

    private void drawGraph(CyDrawStack stack, Selection<NodeObject> sel,
            NodeObject node, ServiceShapeManip serv)
    {
        ArrayList<? extends NetworkHandleEdge> edgeList =
                serv.getEdges();
        ArrayList<? extends NetworkHandleVertex> vertList =
                serv.getVertices();
        CyMatrix4d g2w = serv.getGraphToWorldXform();

        NetworkHandleSelection subSel = 
                sel.getSubselection(node, NetworkHandleSelection.class);
        
        //Find tangent handles to draw
        HashSet<TanRecord> tanRecords = new HashSet<TanRecord>();
        for (NetworkHandleEdge e: edgeList)
        {
            if (subSel != null && subSel.containsEdge(e.getIndex())
                    && !e.isLine())
            {
                tanRecords.add(new TanRecord(e, false));
                tanRecords.add(new TanRecord(e, true));
            }
        }
        
        for (NetworkHandleVertex v: vertList)
        {
            if (subSel != null && subSel.containsVertex(v.getIndex()))
            {
                for (NetworkHandleEdge e: v.getInputEdges())
                {
                    if (!e.isLine())
                    {
                        tanRecords.add(new TanRecord(e, false));
                    }
                }
                for (NetworkHandleEdge e: v.getOutputEdges())
                {
                    if (!e.isLine())
                    {
                        tanRecords.add(new TanRecord(e, true));
                    }
                }
            }
        }
        
        //Draw paths
        CyPath2d pathUnsel = new CyPath2d();
        CyPath2d pathSel = new CyPath2d();
        for (NetworkHandleEdge e: edgeList)
        {
            BezierCurve2d c = e.getCurveLocal();
            
            if (subSel != null && subSel.containsEdge(e.getIndex()))
            {
                pathSel.moveTo(c.getStartX(), c.getStartY());
                c.append(pathSel);
            }
            else
            {
                pathUnsel.moveTo(c.getStartX(), c.getStartY());
                c.append(pathUnsel);
            }
        }
        
        RavenSymbolRoot root = getDocument();
        CyMatrix4d w2d = stack.getViewXform();
        CyMatrix4d d2p = stack.getProjXform();
        
        CyMatrix4d g2p = new CyMatrix4d(d2p);
        g2p.mul(w2d);
        g2p.mul(g2w);
        
        if (!pathUnsel.isEmpty())
        {
            ShapeLinesProvider lines = new ShapeLinesProvider(pathUnsel);
            CyVertexBuffer buf = new CyVertexBuffer(lines);
            drawShape(stack, buf, g2p, 
                    root.getGraphColorEdge().asColor());
        }
        if (!pathSel.isEmpty())
        {
            ShapeLinesProvider lines = new ShapeLinesProvider(pathSel);
            CyVertexBuffer buf = new CyVertexBuffer(lines);
            drawShape(stack, buf, g2p, 
                    root.getGraphColorEdgeSelect().asColor());
        }
        
        //Draw verts
        CyVector2d pt = new CyVector2d();
        CyMatrix4d v2p = new CyMatrix4d();
        float radDisp = root.getGraphRadiusDisplay();
        CyVertexBuffer bufSquare = CyVertexBufferDataSquare.inst().getBuffer();
        CyVertexBuffer bufSquareLines = CyVertexBufferDataSquareLines.inst().getBuffer();
        CyMatrix4d g2d = new CyMatrix4d(w2d);
        g2d.mul(g2w);
        
        for (NetworkHandleVertex v: vertList)
        {
            Coord c = v.getCoord();
            pt.set(c.x, c.y);
            g2d.transformPoint(pt);
            
            v2p.set(d2p);
            v2p.translate(pt.x, pt.y, 0);
            v2p.scale(radDisp * 2, radDisp * 2, 1);
            v2p.translate(-.5, -.5, 0);
            
            if (subSel != null && subSel.containsVertex(v.getIndex()))
            {
                drawShape(stack, bufSquare, v2p, 
                    root.getGraphColorVertSelect().asColor());
                drawShape(stack, bufSquareLines, v2p, 
                    root.getGraphColorEdgeSelect().asColor());
            }
            else
            {
                drawShape(stack, bufSquare, v2p, 
                    root.getGraphColorVert().asColor());
                drawShape(stack, bufSquareLines, v2p, 
                    root.getGraphColorEdge().asColor());
            }
        }
    }
    
    private void drawShape(CyDrawStack stack, 
            CyVertexBuffer buf, CyMatrix4d mvp, CyColor4f color)
    {
        CyMaterialColorDrawRecord rec = 
                CyMaterialColorDrawRecordFactory.inst().allocRecord();

        rec.setColor(color);

        rec.setMesh(buf);

        rec.setOpacity(1);

//        CyMatrix4d mvp = stack.getModelViewProjXform();
//        mvp.mul(mvp);


        rec.setMvpMatrix(mvp);
        
        stack.addDrawRecord(rec);
    }
    
    //---------------------------------------

//    @ServiceInst(service=ToolProvider.class)
    static public class Provider extends ToolProvider<ToolCurveEdit>
    {

        public Provider()
        {
            super("Curve Edit", "/icons/tools/curveEdit.png", "/manual/tools/curveEdit.html");
        }

        @Override
        public void loadPreferences(Properties properties)
        {
            super.loadPreferences(properties);

            PropertiesData prop = new PropertiesData(properties);
        }

        @Override
        public Properties savePreferences()
        {
            Properties properties = new Properties();
            PropertiesData prop = new PropertiesData(properties);
            
            return properties;
        }

        @Override
        public ToolCurveEdit create(ToolUser user)
        {
            return new ToolCurveEdit(user, this);
        }

        @Override
        public Component createToolSettingsEditor(RavenEditor editor)
        {
            return null;
//            return new ToolCurveEditSettings(editor, this);
        }
    }

    class TanRecord
    {
        NetworkHandleEdge edge;
        boolean head;

        public TanRecord(NetworkHandleEdge edge, boolean head)
        {
            this.edge = edge;
            this.head = head;
        }

        @Override
        public boolean equals(Object obj)
        {
            if (obj == null)
            {
                return false;
            }
            if (getClass() != obj.getClass())
            {
                return false;
            }
            final TanRecord other = (TanRecord)obj;
            if (this.edge != other.edge && (this.edge == null || !this.edge.equals(other.edge)))
            {
                return false;
            }
            if (this.head != other.head)
            {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode()
        {
            int hash = 5;
            hash = 97 * hash + (this.edge != null ? this.edge.hashCode() : 0);
            hash = 97 * hash + (this.head ? 1 : 0);
            return hash;
        }
        
    }
        
}
