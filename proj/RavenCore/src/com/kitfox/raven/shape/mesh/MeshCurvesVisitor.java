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

package com.kitfox.raven.shape.mesh;

/**
 *
 * @author kitfox
 */
@Deprecated
public interface MeshCurvesVisitor
{
    public void paintLeft(int id);
    public void paintRight(int id);
    public void paintLine(int id);
    public void strokeLine(int id);

    public void moveTo(int px, int py);
    public void lineTo(int ex, int ey);
    public void quadTo(int kx0, int ky0, int ex, int ey);
    public void cubicTo(int kx0, int ky0, int kx1, int ky1, int ex, int ey);
}
