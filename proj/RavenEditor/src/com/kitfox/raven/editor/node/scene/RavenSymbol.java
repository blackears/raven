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

package com.kitfox.raven.editor.node.scene;

import com.kitfox.raven.editor.node.scene.wizard.RavenNodeRootWizard;
import com.kitfox.raven.util.service.ServiceInst;
import com.kitfox.raven.util.tree.NodeDocument;
import com.kitfox.raven.util.tree.NodeObjectProvider;
import com.kitfox.raven.util.tree.NodeObjectProviderIndex;
import com.kitfox.raven.util.tree.NodeSymbol;
import com.kitfox.raven.util.tree.NodeSymbolProvider;
import com.kitfox.raven.wizard.RavenWizardPageIterator;
import com.kitfox.xml.schema.ravendocumentschema.NodeSymbolType;

/**
 *
 * @author kitfox
 */
public class RavenSymbol extends NodeSymbol<RavenSymbolRoot>
{
    protected RavenSymbol(int symbolUid, NodeDocument document)
    {
        super(symbolUid, document);
    }

    public static RavenSymbol create(NodeDocument doc)
    {
        RavenSymbol sym = new RavenSymbol(doc.allocSymbolUid(), doc);
        
        NodeObjectProvider<RavenSymbolRoot> prov = 
                NodeObjectProviderIndex.inst().getProvider(RavenSymbolRoot.class);
        RavenSymbolRoot root = prov.createNode(sym);
        
        sym.setRoot(root);
        return sym;
    }

    protected static RavenSymbol create(NodeDocument doc,
            NodeSymbolType symType)
    {
        RavenSymbol sym = new RavenSymbol(symType.getSymbolUid(), doc);
        sym.load(symType);
        return sym;
    }

    //-----------------------------------------------
    
    @ServiceInst(service=NodeSymbolProvider.class)
    public static class Provider extends NodeSymbolProvider<RavenSymbol>
    {
        public Provider()
        {
            super(RavenSymbol.class, "Raven Animation 2D", "/icons/node/root.png");
        }

        @Override
        public RavenSymbol loadDocument(NodeDocument doc, NodeSymbolType docTree)
        {
            return RavenSymbol.create(doc, docTree);
        }

        @Override
        public RavenWizardPageIterator<RavenSymbol> createDocumentWizard(NodeDocument doc)
        {
            return new RavenNodeRootWizard(doc);
        }

        @Override
        public RavenSymbol create(NodeDocument doc)
        {
            return RavenSymbol.create(doc);
        }
    }
    
}
