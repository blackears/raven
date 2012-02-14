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
 * An SVG-like implementation of a path.  A path is a single strip of bezier
 * curves connected end to end.  A closed path is a shape.  A
 * shape may have holes, but no vertex may have more than two bezier segments
 * extending from it.  PathCurve is equivilent to Java2D's Path2D.
 *
 * All coordinates are expressed as integral numbers of centipixels.
 * (ie, 1/100 of a pixel).  This is to avoid round off error and so
 * that it is possible to determine if two points share the same
 * coordinate by using == to compare their (x, y) values.
 */
@Deprecated
package com.kitfox.raven.shape.path;
