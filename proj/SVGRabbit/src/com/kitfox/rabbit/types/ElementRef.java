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

package com.kitfox.rabbit.types;

/**
 *
 * @author kitfox
 */
public class ElementRef
{
    private final int documentIndex;
    private final int elementIndex;

    public ElementRef(int documentIndex, int elementIndex)
    {
        this.documentIndex = documentIndex;
        this.elementIndex = elementIndex;
    }

    /**
     * @return the documentIndex
     */
    public int getDocumentIndex() {
        return documentIndex;
    }

    /**
     * @return the elementIndex
     */
    public int getElementIndex() {
        return elementIndex;
    }

    @Override
    public String toString()
    {
        return "eleRef(" + documentIndex + ", " + elementIndex + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ElementRef other = (ElementRef) obj;
        if (this.documentIndex != other.documentIndex) {
            return false;
        }
        if (this.elementIndex != other.elementIndex) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + this.documentIndex;
        hash = 79 * hash + this.elementIndex;
        return hash;
    }
    
}
