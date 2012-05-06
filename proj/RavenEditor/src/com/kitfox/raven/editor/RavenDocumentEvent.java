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

import com.kitfox.raven.util.tree.NodeSymbol;
import java.util.EventObject;

/**
 *
 * @author kitfox
 */
public class RavenDocumentEvent extends EventObject
{
    private final NodeSymbol symbol;
    private final NodeSymbol oldSymbol;
    
    public RavenDocumentEvent(RavenDocument src, NodeSymbol document,
            NodeSymbol oldDocument)
    {
        super(src);
        this.symbol = document;
        this.oldSymbol = oldDocument;
    }
    
    public RavenDocumentEvent(RavenDocument src, NodeSymbol document)
    {
        this(src, document, null);
    }

    /**
     * @return the document
     */
    public NodeSymbol getSymbol()
    {
        return symbol;
    }

    /**
     * @return the document
     */
    public NodeSymbol getOldSymbol()
    {
        return oldSymbol;
    }
}
