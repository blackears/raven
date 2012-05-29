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

package com.kitfox.coyote.shape;

/**
 *
 * @author kitfox
 */
public class PathCollector extends PathConsumer
{
    private CyPath2d path;

    @Override
    public void beginPath()
    {
        path = new CyPath2d();
    }

    @Override
    public void beginSubpath(double x0, double y0)
    {
        path.moveTo(x0, y0);
    }

    @Override
    public void lineTo(double x0, double y0)
    {
        path.lineTo(x0, y0);
    }

    @Override
    public void quadTo(double x0, double y0, double x1, double y1)
    {
        path.quadTo(x0, y0, x1, y1);
    }

    @Override
    public void cubicTo(double x0, double y0, double x1, double y1, double x2, double y2)
    {
        path.cubicTo(x0, y0, x1, y1, x2, y2);
    }

    @Override
    public void closeSubpath()
    {
        path.close();
    }

    @Override
    public void endPath()
    {
    }

    /**
     * @return the path
     */
    public CyPath2d getPath()
    {
        return path;
    }

}
