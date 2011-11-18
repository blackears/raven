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

package com.kitfox.raven.util.text;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 *
 * @author kitfox
 */
public class SystemFontLibrary
{
    static final SystemFontLibrary instance = new SystemFontLibrary();

    LinkedHashMap<String, Font> fontMap;
    boolean started = false;

    private SystemFontLibrary()
    {
        Loader loader = new Loader();
        loader.start();
    }

    public static SystemFontLibrary inst()
    {
        return instance;
    }

    public Font getFont(String family)
    {
        while (fontMap == null)
        {
            Thread.yield();
        }
        return fontMap.get(family);
    }

    private void load()
    {
        LinkedHashMap<String, Font> map = new LinkedHashMap<String, Font>();
        
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        String[] fontFam = ge.getAvailableFontFamilyNames();
        for (int i = 0; i < fontFam.length; ++i)
        {
            Font font = new Font(fontFam[i], Font.PLAIN, 20);
            map.put(fontFam[i], font);
        }

        fontMap = map;
    }

    public ArrayList<Font> getFonts()
    {
        while (fontMap == null)
        {
            Thread.yield();
        }
        return new ArrayList<Font>(fontMap.values());
    }

    //---------------------------------
    class Loader extends Thread
    {
        public Loader()
        {
            setDaemon(true);
            setPriority(MIN_PRIORITY);
        }

        @Override
        public void run()
        {
            load();
        }
    }
}
