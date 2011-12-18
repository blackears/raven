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

package com.kitfox.raven.util.service;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation used by the ServiceApt processor to identify classes that
 * should be compiled into lists of services.  Any file marked with this
 * class will be added to a list in the META-INF/services directory of the
 * compiled code.
 * 
 * @author kitfox
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface ServiceInst
{
    /**
     * The service type being implemented.  On compilation, the fully qualified 
     * name of the annotated class will be added to a text file who's 
     * name is equal to the fully qualified name of the class specified here.
     * 
     * The annotated class must extend or implement this class.
     * 
     * @return Class of service type being implemented.
     */
    public Class service();
}
