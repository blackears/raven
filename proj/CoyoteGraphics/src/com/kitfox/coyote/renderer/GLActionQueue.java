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

package com.kitfox.coyote.renderer;

import java.util.ArrayList;

/**
 *
 * @author kitfox
 */
@Deprecated
public class GLActionQueue
{
    private static final GLActionQueue instance = new GLActionQueue();

    ArrayList<GLAction> actionList = new ArrayList<GLAction>();

    public static GLActionQueue inst()
    {
        return instance;
    }

    public void postAction(GLAction action)
    {
        actionList.add(action);
    }

    public void processActions(GLWrapper wrapper)
    {
        if (actionList.isEmpty())
        {
            return;
        }

        ArrayList<GLAction> list = actionList;
        actionList = new ArrayList<GLAction>();
        for (int i = 0; i < actionList.size(); ++i)
        {
            list.get(i).doAction(wrapper);
        }
    }

    public void clear()
    {
        actionList.clear();
    }
}
