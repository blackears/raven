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

package com.kitfox.raven.editor;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;

/**
 *
 * @author kitfox
 */
public class RavenSwingUtil
{
    public static void centerWindow(Window win, Rectangle area)
    {
        int dw = area.width - win.getWidth();
        int dh = area.height - win.getHeight();

        win.setLocation(area.x + dw / 2, area.y + dh / 2);
    }

    public static void centerWindow(Window win, Dimension dim)
    {
        int dw = dim.width - win.getWidth();
        int dh = dim.height - win.getHeight();

        win.setLocation(dw / 2, dh / 2);
    }

    public static void centerWindow(Window win)
    {
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        centerWindow(win, dim);
    }
}
