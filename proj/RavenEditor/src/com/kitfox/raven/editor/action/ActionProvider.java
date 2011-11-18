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

import javax.swing.Action;
import javax.swing.KeyStroke;

/**
 *
 * @author kitfox
 */
abstract public class ActionProvider
{
    private final String id;
    private final String name;
    private final KeyStroke stroke;
    private final Action action;

    public ActionProvider(String id, String name, KeyStroke stroke, Action action)
    {
        this.id = id;
        this.name = name;
        this.stroke = stroke;
        this.action = action;
    }


    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the stroke
     */
    public KeyStroke getDefaultKeyStroke() {
        return stroke;
    }

    /**
     * @return the action
     */
    public Action getAction() {
        return action;
    }
}
