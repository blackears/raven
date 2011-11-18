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

package com.kitfox.rabbit.nodes;

import com.kitfox.rabbit.render.RabbitFrame;
import com.kitfox.rabbit.render.RabbitRenderer;
import com.kitfox.rabbit.render.Surface2D;
import com.kitfox.rabbit.style.Style;
import com.kitfox.rabbit.style.StyleFilter;
import com.kitfox.rabbit.style.StyleKey;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author kitfox
 */
public class RaElement
{
    private RaElement parent;
    private Style style = new Style();
    private int[] styleClasses;
    ArrayList<RaElement> children = new ArrayList<RaElement>();

    ArrayList<RaElementListener> listeners = new ArrayList<RaElementListener>();

    public void addElementListener(RaElementListener l)
    {
        listeners.add(l);
    }

    public void removeElementListener(RaElementListener l)
    {
        listeners.remove(l);
    }

    protected void fireStructureChanged()
    {
        RaChangeEvent evt = new RaChangeEvent(this);
        for (int i = 0; i < listeners.size(); ++i)
        {
            listeners.get(i).structureChanged(evt);
        }
    }

    protected void notifyStructureChanged()
    {
        fireStructureChanged();
        if (parent != null)
        {
            parent.notifyStructureChanged();
        }
    }
    public void addChild(RaElement child)
    {
        children.add(child);
        child.setParent(this);
    }

    public void addText(String text)
    {
        addChild(new RaString(text));
    }

    public void addChildren(Collection<RaElement> list)
    {
        children.addAll(list);
    }

    public ArrayList<RaElement> getChildren()
    {
        return new ArrayList<RaElement>(children);
    }

    public int getNumChildren()
    {
        return children.size();
    }

    public RaElement getChild(int index)
    {
        return children.get(index);
    }

    /**
     * @return the style
     */
    public Style getStyle() {
        return style;
    }

    /**
     * @param style the style to set
     */
    public void setStyle(Style style) {
        this.style = style;
    }

    /**
     * @return the parent
     */
    public RaElement getParent() {
        return parent;
    }

    /**
     * @param parent the parent to set
     */
    public void setParent(RaElement parent) {
        this.parent = parent;
    }

    /**
     * @return the styleClasses
     */
    public int[] getStyleClasses() {
        return styleClasses;
    }

    /**
     * @param styleClasses the styleClasses to set
     */
    public void setStyleClasses(int[] styleClasses) {
        this.styleClasses = styleClasses;
    }

    public void render(RabbitRenderer renderer)
    {
        renderer.applyStyles(style);

        StyleFilter filterStyle = (StyleFilter)style.get(StyleKey.FILTER);
        if (filterStyle != null)
        {
            RaFilter filter = (RaFilter)renderer.getUniverse().lookupElement(filterStyle.getFilter());

            AffineTransform coordSys = new AffineTransform();
            Rectangle2D bounds = getBounds(renderer);
            Rectangle2D region;
            switch(filter.getFilterUnits())
            {
                default:
                case OBJECT_BOUNDING_BOX:
                    region = new Rectangle2D.Double(
                            bounds.getX() + bounds.getWidth() * filter.getX(),
                            bounds.getY() + bounds.getHeight() * filter.getY(),
                            bounds.getWidth() * filter.getWidth(),
                            bounds.getHeight() * filter.getHeight()
                            );
                    coordSys.translate(-bounds.getX() - bounds.getWidth() * filter.getX(),
                            -bounds.getY() - bounds.getHeight() * filter.getY());
                    break;
                case USER_SPACE_ON_USE:
                    region = new Rectangle2D.Float(filter.getX(), filter.getY(), filter.getWidth(), filter.getHeight());
                    break;
            }

            renderer.pushFrame(this);

            RabbitFrame frame = renderer.getCurFrame();
            Surface2D surfUnder = frame.getSurface();
            Surface2D surfFilter = surfUnder.createBlankSurface(region);

//            double dx = bounds.getX() - region.getX();
//            double dy = bounds.getY() - region.getY();
//            frame.getXform().translate(dx, dy);
            frame.setXform(coordSys);
            frame.setSurface(surfFilter);
            renderContent(renderer);

//Shape outline = getOutline(renderer);
//Graphics2D g = ((Surface2DAwtImage)surfFilter).getG();
//g.setColor(Color.red);
//g.fill(outline);
//try {
//    ImageIO.write(((Surface2DAwtImage) surfFilter).getImage(),
//            "png", new File("filterBuffer.png"));
//} catch (IOException ex) {
//    Logger.getLogger(RaElement.class.getName()).log(Level.SEVERE, null, ex);
//}
//            frame.getXform().translate(-dx, -dy);
//            frame.setSurface(surfUnder);
            renderer.popFrame();
            surfFilter.dispose();
            
            renderer.drawSurface(surfFilter);

        }
        else
        {
            renderContent(renderer);
        }
    }

    public void renderContent(RabbitRenderer renderer)
    {
    }

    public Shape getOutline(RabbitRenderer renderer)
    {
        return null;
    }

    public Rectangle2D getBounds(RabbitRenderer renderer)
    {
        Shape shape = getOutline(renderer);
        return shape == null ? null : shape.getBounds2D();
    }
}
