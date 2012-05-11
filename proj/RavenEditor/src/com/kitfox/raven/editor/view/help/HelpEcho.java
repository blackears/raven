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

package com.kitfox.raven.editor.view.help;

import java.net.URL;
import java.util.ArrayList;

/**
 *
 * @author kitfox
 */
public class HelpEcho
{
    private static final HelpEcho instance = new HelpEcho();

    ArrayList<HelpEchoListener> listeners = new ArrayList<HelpEchoListener>();

    private HelpEcho()
    {
    }

    public static HelpEcho inst()
    {
        return instance;
    }

    public void addHelpEchoListener(HelpEchoListener l)
    {
        listeners.add(l);
    }

    public void removeHelpEchoListener(HelpEchoListener l)
    {
        listeners.remove(l);
    }

    private void fireShowHelpTopic(URL topic)
    {
        HelpBrowseEvent evt = new HelpBrowseEvent(this, topic);
        ArrayList<HelpEchoListener> list =
                new ArrayList<HelpEchoListener>(listeners);
        for (int i = 0; i < list.size(); ++i)
        {
            list.get(i).showHelpTopic(evt);
        }
    }

    public void showHelpTopic(URL topic)
    {
        fireShowHelpTopic(topic);
    }

}
