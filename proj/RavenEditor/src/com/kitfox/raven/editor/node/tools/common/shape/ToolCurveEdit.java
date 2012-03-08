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

package com.kitfox.raven.editor.node.tools.common.shape;

import com.kitfox.coyote.material.marquis.CyMaterialMarquisDrawRecord;
import com.kitfox.coyote.material.marquis.CyMaterialMarquisDrawRecordFactory;
import com.kitfox.coyote.math.CyColor4f;
import com.kitfox.coyote.math.CyMatrix4d;
import com.kitfox.coyote.renderer.CyDrawStack;
import com.kitfox.coyote.renderer.vertex.CyVertexBufferDataSquareLines;
import com.kitfox.raven.editor.RavenEditor;
import com.kitfox.raven.editor.node.scene.RenderContext;
import com.kitfox.raven.editor.node.tools.ToolProvider;
import com.kitfox.raven.editor.node.tools.ToolUser;
import com.kitfox.raven.editor.node.tools.common.ToolDisplay;
import com.kitfox.raven.util.PropertiesData;
import com.kitfox.raven.util.Selection.Operator;
import com.kitfox.raven.util.service.ServiceInst;
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.util.Properties;

/**
 *
 * @author kitfox
 */
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
        
        if (mouseStart != null)
        {
            int x0 = mouseStart.getX();
            int y0 = mouseStart.getY();
            int x1 = mouseCur.getX();
            int y1 = mouseCur.getY();
            
//            CyMatrix4d proj = stack.getProjXform();
//            CyMatrix4d mvp = CyMatrix4d.createIdentity();
            CyMatrix4d mv = CyMatrix4d.createIdentity();
            mv.translate(x0, y0, 0);
            mv.scale(x1 - x0, y1 - y0, 1);

            CyMatrix4d mvp = stack.getProjXform();
            mvp.mul(mv);
            
            CyMaterialMarquisDrawRecord rec =
                    CyMaterialMarquisDrawRecordFactory.inst().allocRecord();

            rec.setMesh(CyVertexBufferDataSquareLines.inst().getBuffer());
            rec.setColorBg(CyColor4f.BLACK);
            rec.setColorFg(CyColor4f.WHITE);
            rec.setOpacity(1);
            rec.setMvpMatrix(mvp);
            rec.setMvMatrix(mv);

            int lineWidth = 8;
            int fps = 32;
            long frames = fps * System.currentTimeMillis() / 1000;
            int offset = (int)(frames % lineWidth);
            
            rec.setOffset(-offset);
            rec.setLineWidth(lineWidth);

            stack.addDrawRecord(rec);                
            
        }
        
//        stack.popFrame();
    }

    
    //---------------------------------------

    @ServiceInst(service=ToolProvider.class)
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
            return new ToolCurveEditSettings(editor, this);
        }
    }
    
}
