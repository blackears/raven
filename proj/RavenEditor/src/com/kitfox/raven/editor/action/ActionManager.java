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

package com.kitfox.raven.editor.action;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.HashMap;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.KeyStroke;

/**
 *
 * @author kitfox
 */
public class ActionManager
{
    HashMap<KeyStroke, StrokeInfo> strokeMap = new HashMap<KeyStroke, StrokeInfo>();
    HashMap<String, StrokeInfo> strokeIdMap = new HashMap<String, StrokeInfo>();

    HashMap<String, ActionHot> actionsHot = new HashMap<String, ActionHot>();
    ArrayList<ActionScript> scriptActions = new ArrayList<ActionScript>();

    ArrayList<ActionManagerListener> listeners = new ArrayList<ActionManagerListener>();

    public ActionManager()
    {
        //Load saved keystroke mappings

        //Load scripted actions

        //Load hot actions
//        ServiceLoader<ActionProvider> loader = ServiceLoader.load(ActionProvider.class);
//        for (Iterator<ActionProvider> it = loader.iterator(); it.hasNext();)
        for (ActionProvider prov: ActionProviderIndex.inst().getProviders())
        {
//            ActionProvider prov = it.next();

            String id = prov.getId();

            actionsHot.put(id, new ActionHot(prov.getName(), id, prov.getAction()));

            if (!strokeIdMap.containsKey(id))
            {
                KeyStroke stroke = prov.getDefaultKeyStroke();
                StrokeInfo info = new StrokeInfo(id,
                        strokeMap.containsKey(stroke) ? null : stroke);
                strokeMap.put(stroke, info);
                strokeIdMap.put(id, info);
            }
        }
    }

    public void addActionManagerListener(ActionManagerListener l)
    {
        listeners.add(l);
    }

    public void removeActionManagerListener(ActionManagerListener l)
    {
        listeners.remove(l);
    }

    protected void fireHotkeyLayoutChanged()
    {
        EventObject evt = new EventObject(this);
        for (int i = 0; i < listeners.size(); ++i)
        {
            listeners.get(i).hotkeyLayoutChanged(evt);
        }
    }

    protected void fireHotkeyActionsChanged()
    {
        EventObject evt = new EventObject(this);
        for (int i = 0; i < listeners.size(); ++i)
        {
            listeners.get(i).hotkeyActionsChanged(evt);
        }
    }

    public void buildInputs(InputMap inputMap)
    {
        inputMap.clear();
        for (StrokeInfo info: strokeMap.values())
        {
            inputMap.put(info.stroke, info.id);
        }
    }

    public void buildActions(ActionMap actionMap)
    {
        actionMap.clear();

        for (ActionHot action: actionsHot.values())
        {
            actionMap.put(action.id, action.action);
        }

        for (ActionScript action: scriptActions)
        {
            actionMap.put(action.id, action.getAction());
        }
    }

    public void addActionScript(String name, String id, String language, String source)
    {
        ActionScript script = new ActionScript(name, id, language, source);
        scriptActions.add(script);

        fireHotkeyActionsChanged();
    }

    public void mapKeyStroke(KeyStroke stroke, String id)
    {
        StrokeInfo info = strokeIdMap.get(id);
        if (info == null)
        {
            info = new StrokeInfo(id, stroke);
            strokeIdMap.put(id, info);
        }
        else if (info.stroke != null)
        {
            strokeMap.remove(info.stroke);
            info.stroke = stroke;
        }
        strokeMap.put(stroke, info);

        fireHotkeyLayoutChanged();
    }

    public void unmapKeyStroke(KeyStroke stroke)
    {
        StrokeInfo info = strokeMap.remove(stroke);
        if (info != null)
        {
            info.stroke = null;
        }

        fireHotkeyLayoutChanged();
    }

    public void executeKeyStroke(KeyStroke stroke, Object source)
    {
        StrokeInfo info = strokeMap.get(stroke);
        if (info == null)
        {
            return;
        }

        ActionHot action = actionsHot.get(info.id);
        if (action == null)
        {
            return;
        }

        action.action.actionPerformed(new ActionEvent(source, 0, ""));
    }

    //--------------------------------------

    //Map an id to an action
    class ActionDef
    {
        String name;
        String id;

        public ActionDef(String name, String id)
        {
            this.name = name;
            this.id = id;
        }
    }

    class ActionScript extends ActionDef
    {
        String language;
        String source;

        public ActionScript(String name, String id, String language, String source)
        {
            super(name, id);
            this.source = source;
            this.language = language;
        }

        private Action getAction()
        {
            return null;
        }

    }

    class ActionHot extends ActionDef
    {
        Action action;

        public ActionHot(String name, String id, Action action)
        {
            super(name, id);
            this.action = action;
        }
    }

    //Map a keystroke to an action id
    class StrokeInfo
    {
        final String id;
        KeyStroke stroke;

        public StrokeInfo(String id, KeyStroke stroke)
        {
            this.id = id;
            this.stroke = stroke;
        }
    }
}
