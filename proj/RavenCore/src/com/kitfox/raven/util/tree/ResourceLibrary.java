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

package com.kitfox.raven.util.tree;

import com.kitfox.raven.util.resource.ResourceIndex;
import com.kitfox.raven.util.resource.ResourceProvider;
import com.kitfox.raven.util.service.ServiceInst;
import com.kitfox.raven.util.undo.History;
import com.kitfox.raven.util.undo.HistoryAction;
import com.kitfox.xml.schema.ravendocumentschema.NodeObjectType;
import java.awt.event.ActionEvent;
import java.io.File;
import java.net.URI;
import java.util.HashMap;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFileChooser;

/**
 *
 * @author kitfox
 */
@Deprecated
public class ResourceLibrary
        //extends NodeObject
{
    /*
    HashMap<URI, Object> resourceMap = new HashMap<URI, Object>();

    protected ResourceLibrary(NodeDocument doc)
    {
        this(doc.allocUid());
    }

    protected ResourceLibrary(int uid)
    {
        super(uid);
    }

    protected ResourceLibrary(NodeObjectType nodeType)
    {
        this(nodeType.getUid());
    }


    @Override
    public Action[] getActions()
    {
        return new Action[]{new AddResourceUIAction()};
    }

    public void addResourceUI()
    {
        JFileChooser chooser = ResourceIndex.inst().getFileChooser();
        NodeDocument doc = getDocument();
        if (chooser.showOpenDialog(doc.getSwingRoot()) !=
                JFileChooser.APPROVE_OPTION)
        {
            return;
        }

        File[] files = chooser.getSelectedFiles();
        if (files.length == 1)
        {
            addResource(files[0]);
        }
        else if (files.length > 1)
        {
            History hist = getDocument().getHistory();

            hist.beginTransaction("Add resources");
            for (File file: files)
            {
                addResource(file);
            }
            hist.commitTransaction();
        }

    }

    public void addResource(File file)
    {
        addResource(file.toURI());
    }

    public void addResource(URI uri)
    {
        if (resourceMap.containsKey(uri))
        {
            return;
        }

        AddResourceAction action = new AddResourceAction(uri);
        doAction(action);
    }

    public void removeResource(URI uri)
    {
        if (!resourceMap.containsKey(uri))
        {
            return;
        }

        RemoveResourceAction action = new RemoveResourceAction(uri);
        doAction(action);
    }

    //-----------------------------------------------

    class RemoveResourceAction implements HistoryAction
    {
        final ResourceProvider prov;
        final URI uri;

        public RemoveResourceAction(URI uri)
        {
            this.uri = uri;
            this.prov = ResourceIndex.inst().getProvider(uri);
        }

        @Override
        public void undo(History history)
        {
            Object value = prov.load(uri);
            resourceMap.put(uri, value);
        }

        @Override
        public void redo(History history)
        {
            resourceMap.remove(uri);
        }

        @Override
        public String getTitle()
        {
            return "Add resource " + prov.getResourceClass().getSimpleName();
        }
    }

    class AddResourceAction implements HistoryAction
    {
        final ResourceProvider prov;
        final URI uri;

        public AddResourceAction(URI uri)
        {
            this.uri = uri;
            this.prov = ResourceIndex.inst().getProvider(uri);
        }

        @Override
        public void undo(History history)
        {
            resourceMap.remove(uri);
        }

        @Override
        public void redo(History history)
        {
            Object value = prov.load(uri);
            resourceMap.put(uri, value);
        }

        @Override
        public String getTitle()
        {
            return "Add resource " + prov.getResourceClass().getSimpleName();
        }
    }

    class AddResourceUIAction extends AbstractAction
    {
        public AddResourceUIAction()
        {
            super("Add resource");
        }

        @Override
        public void actionPerformed(ActionEvent e)
        {
            addResourceUI();
        }
    }

    @ServiceAnno(service=NodeObjectProvider.class)
    public static class Provider extends NodeObjectProvider<ResourceLibrary>
    {
        public Provider()
        {
            super(ResourceLibrary.class, "Resource Library", "/icons/node/resourceLibrary.png");
        }

        @Override
        public ResourceLibrary createNode(int uid)
        {
            return new ResourceLibrary(uid);
        }
    }
*/
}
