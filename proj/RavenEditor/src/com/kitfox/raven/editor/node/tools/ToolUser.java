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

package com.kitfox.raven.editor.node.tools;

import java.util.ArrayList;

/**
 *
 * @author kitfox
 */
public interface ToolUser
{
    /**
     * When a tool begins an action, it will call this method on its user to
     * determine if the desired service is available.  If so, it will continue
     * to use this service until the tool action is finished or canceled.
     *
     * For example, a MovementTool may query its ToolUser to see if it has
     * a Movement object it can call methods on.  If so, the ToolUser will
     * return such an object and the MovementTool will continue to use it
     * until its action is ended.
     *
     * @param <T> Type of object this tool needs to manipulate
     * @param serviceClass Class of the object that the tool needs to be able to
     * menipulate the ToolUser
     * @return An object that the tool can use to update the ToolUser or null if
     * ToolUser does not support this service
     */
    public <T> T getToolService(Class<T> serviceClass);

    public <T> void getToolServices(Class<T> serviceClass, ArrayList<T> appendList);
}
