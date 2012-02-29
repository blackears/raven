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

package com.kitfox.coyote.shape.test;

import com.kitfox.coyote.math.CyMatrix4d;
import com.kitfox.coyote.shape.*;
import com.kitfox.coyote.shape.bezier.path.cut.Coord;
import com.kitfox.coyote.shape.tessellator2.CtrLoop;
import com.kitfox.coyote.shape.tessellator2.PathTessellator2;
import java.util.ArrayList;

/**
 *
 * @author kitfox
 */
public class TessMain
{

    public TessMain()
    {
        //testSquare();
//        testSelfIsect();
//        testHole();
//        testOverlap();
//        testCircle();
//        testSquareOutline();
//        testCircleOutline();
//        testSquareTess();
//        testSquareOutlineTess();
//        testCircleScaled();
        testOutlineCurve();
    }
    
    private void testSquare()
    {
        CyPath2d path = new CyPath2d();
        path.moveTo(0, 0);
        path.lineTo(100, 0);
        path.lineTo(100, 100);
        path.lineTo(0, 100);
        path.close();
        
        PathTessellator2 tess = new PathTessellator2();
        tess.feedShape(path);
        
        tess.dump(System.err);
    }
    
    private void testSelfIsect()
    {
        CyPath2d path = new CyPath2d();
        path.moveTo(0, 0);
        path.lineTo(100, 0);
        path.lineTo(0, 100);
        path.lineTo(100, 100);
        path.close();
        
        PathTessellator2 tess = new PathTessellator2();
        tess.feedShape(path);
        
        tess.dump(System.err);
    }
    
    private void testHole()
    {
        CyPath2d path = new CyPath2d();
        path.moveTo(0, 0);
        path.lineTo(100, 0);
        path.lineTo(100, 100);
        path.lineTo(0, 100);
        path.close();
        
        path.moveTo(20, 20);
        path.lineTo(20, 80);
        path.lineTo(80, 80);
        path.lineTo(80, 20);
        path.close();
        
        PathTessellator2 tess = new PathTessellator2();
        tess.feedShape(path);
        
        tess.dump(System.err);
    }
    
    private void testOverlap()
    {
        CyPath2d path = new CyPath2d();
        path.moveTo(0, 0);
        path.lineTo(70, 0);
        path.lineTo(70, 70);
        path.lineTo(0, 70);
        path.close();
        
        path.moveTo(30, 30);
        path.lineTo(100, 30);
        path.lineTo(100, 100);
        path.lineTo(30, 100);
        path.close();
        
        PathTessellator2 tess = new PathTessellator2();
        tess.feedShape(path);
        
        tess.dump(System.err);
    }
    
    private void testCircle()
    {
        CyEllipse2d path = new CyEllipse2d(0, 0, 100, 100);

        PathTessellator2 tess = new PathTessellator2();
        PathFlattener flat = new PathFlattener(tess);
        flat.feedShape(path);
        
        tess.dump(System.err);
    }
    
    private void testSquareOutline()
    {
        CyRectangle2d ellipse = new CyRectangle2d(0, 0, 100, 100);
        CyStroke stroke = new CyStroke(10);
        CyPath2d path = stroke.outlineShape(ellipse);

        PathTessellator2 tess = new PathTessellator2();
        PathFlattener flat = new PathFlattener(tess);
        flat.feedShape(path);
        
        tess.dump(System.err);
        tess.dumpScilab(System.err);
    }
    
    private void testCircleOutline()
    {
        CyEllipse2d ellipse = new CyEllipse2d(0, 0, 1000, 1000);
        CyStroke stroke = new CyStroke(100);
        CyPath2d path = stroke.outlineShape(ellipse);

        PathTessellator2 tess = new PathTessellator2();
        PathFlattener flat = new PathFlattener(tess);
        flat.feedShape(path);
        
        tess.dump(System.err);
        tess.dumpScilab(System.err);
    }
    
    private void testSquareTess()
    {
        CyRectangle2d path = new CyRectangle2d(0, 0, 100, 100);

        PathTessellator2 tess = new PathTessellator2();
        PathFlattener flat = new PathFlattener(tess);
        flat.feedShape(path);

        for (CtrLoop loop: tess.getContours())
        {
            if (loop.getWinding() == 0)
            {
                continue;
            }
            
            ArrayList<Coord> triList = new ArrayList<Coord>();
            loop.buildTriangles(triList);
            dumpTris(triList);
        }
    }
    
    private void testSquareOutlineTess()
    {
        CyRectangle2d ellipse = new CyRectangle2d(0, 0, 100, 100);
        CyStroke stroke = new CyStroke(10);
        CyPath2d path = stroke.outlineShape(ellipse);

        PathTessellator2 tess = new PathTessellator2();
        PathFlattener flat = new PathFlattener(tess);
        flat.feedShape(path);

        for (CtrLoop loop: tess.getContours())
        {
            if (loop.getWinding() == 0)
            {
                continue;
            }
            
            ArrayList<Coord> triList = new ArrayList<Coord>();
            loop.buildTriangles(triList);
            dumpTris(triList);
        }
    }

    private void dumpTris(ArrayList<Coord> triList)
    {
        for (int i = 0; i < triList.size(); i += 3)
        {
            Coord c0 = triList.get(i);
            Coord c1 = triList.get(i + 1);
            Coord c2 = triList.get(i + 2);

            System.err.println(c0 + " " + c1 + " " + c2);
        }
    }

    private void testCircleScaled()
    {
        CyEllipse2d ellipse = new CyEllipse2d(0, 0, 100, 100);

        CyMatrix4d m = CyMatrix4d.createIdentity();
        m.scale(100, 100, 1);
        CyPath2d path = ellipse.createTransformedPath(m);

        path.dump(System.err);
        
        PathTessellator2 tess = new PathTessellator2();
        PathFlattener flat = new PathFlattener(tess, 10000);
        flat.feedShape(path);
        
        tess.dump(System.err);
    }
    
    private void testOutlineCurve()
    {
        CyPath2d path = new CyPath2d();
        path.moveTo(14300.0, 17400.0);
        path.cubicTo(14300.0, 17400.0, 21000.0, 16700.0, 22300.0, 15400.0);

System.err.println("EdgeLayout In " + path.toString());

        CyStroke stroke = new CyStroke(100);
        path = stroke.outlineShape(path);
        
System.err.println("EdgeLayout Out " + path.toString());
    }
        
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        new TessMain();
    }
}
