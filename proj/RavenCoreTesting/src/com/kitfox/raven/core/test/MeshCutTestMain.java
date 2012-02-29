/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kitfox.raven.core.test;

import com.kitfox.coyote.shape.bezier.BezierLine2i;
import com.kitfox.coyote.shape.bezier.mesh.CutLoop;
import com.kitfox.raven.shape.network.NetworkMesh;
import java.util.ArrayList;

/**
 *
 * @author kitfox
 */
public class MeshCutTestMain
{
    public MeshCutTestMain()
    {
//        testTwoShapes();
//        testNestedShapes();
        testCoincidentShapes();
    }

    private void testCoincidentShapes()
    {
        NetworkMesh mesh = new NetworkMesh();
        
        mesh.addEdge(new BezierLine2i(0, 0, 100, 0), null);
        mesh.addEdge(new BezierLine2i(100, 0, 0, 100), null);
        mesh.addEdge(new BezierLine2i(0, 100, 0, 0), null);
        
        mesh.addEdge(new BezierLine2i(0, 0, 50, 0), null);
        mesh.addEdge(new BezierLine2i(50, 0, 0, 50), null);
        mesh.addEdge(new BezierLine2i(0, 50, 0, 0), null);
        
        
        ArrayList<CutLoop> loops = mesh.createFaces();
        for (CutLoop loop: loops)
        {
            loop.dump(System.err);
        }
    }

    private void testNestedShapes()
    {
        NetworkMesh mesh = new NetworkMesh();
        
        mesh.addEdge(new BezierLine2i(0, 0, 200, 0), null);
        mesh.addEdge(new BezierLine2i(200, 0, 100, 100), null);
        mesh.addEdge(new BezierLine2i(100, 100, 0, 0), null);
        
        mesh.addEdge(new BezierLine2i(20, 10, 180, 10), null);
        mesh.addEdge(new BezierLine2i(180, 10, 100, 90), null);
        mesh.addEdge(new BezierLine2i(100, 90, 20, 10), null);
        
        mesh.addEdge(new BezierLine2i(40, 20, 160, 20), null);
        mesh.addEdge(new BezierLine2i(160, 20, 100, 80), null);
        mesh.addEdge(new BezierLine2i(100, 80, 40, 20), null);
        
        
        ArrayList<CutLoop> loops = mesh.createFaces();
        for (CutLoop loop: loops)
        {
            loop.dump(System.err);
        }
    }
    
    private void testTwoShapes()
    {
        NetworkMesh mesh = new NetworkMesh();
        
        mesh.addEdge(new BezierLine2i(0, 10, 100, 10), null);
        mesh.addEdge(new BezierLine2i(100, 10, 50, 90), null);
        mesh.addEdge(new BezierLine2i(50, 90, 0, 10), null);
        
        mesh.addEdge(new BezierLine2i(0, 210, 100, 210), null);
        mesh.addEdge(new BezierLine2i(100, 210, 50, 290), null);
        mesh.addEdge(new BezierLine2i(50, 290, 0, 210), null);
        
        ArrayList<CutLoop> loops = mesh.createFaces();
        for (CutLoop loop: loops)
        {
            loop.dump(System.err);
        }
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        new MeshCutTestMain();
    }
}
