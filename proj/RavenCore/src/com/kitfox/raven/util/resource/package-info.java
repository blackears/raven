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

/**
 * Resources provide a way for Raven to access files on disk.  These
 * may be simple things such as images and sounds, or complex structred
 * data such as Collada files.
 *
 * Each resource will be served by a ResourceProvider.  This provider
 * will return an object that represents the 'loaded' state of the object.
 * This object will be treated as if it is immutable, so that it may be
 * cached and served to other object properties that request the same
 * resource.
 *
 * Resources will be identified by their URI.  This URI may be
 * absolute or relative.  If relative, it will be resolved against
 * the project root.  The project root is identified as the first
 * ancestor directory from the working Raven document that contains
 * the project.xml file.
 */

package com.kitfox.raven.util.resource;
