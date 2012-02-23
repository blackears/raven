/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kitfox.coyote.shape.test;

import com.kitfox.coyote.shape.CyPath2d;
import com.kitfox.coyote.shape.CyStroke;

/**
 *
 * @author kitfox
 */
public class StrokeMain
{

    public StrokeMain()
    {
        CyStroke stroke = new CyStroke(100);
        CyPath2d path = new CyPath2d();
        
//        path.moveTo(27040, 9561);
//        path.cubicTo(27040, 9561, 27040, 9561, 27039, 9561);
//        path.moveTo(19366, 7482);
//        path.cubicTo(19366, 7482, 19366, 7482, 19366, 7482);
//        path.moveTo(9000.0, 13600.0);
//        path.cubicTo(8374.0, 13031.0, 7595.0, 12430.0, 6867.0, 11765.0);
        path.moveTo(18669.0, 12769.0);
        path.cubicTo(14616.0, 13711.0, 10566.0, 15024.0, 9000.0, 13600.0);
        
        System.err.println(path);
        CyPath2d outline = stroke.outlineShape(path);
        System.err.println(outline);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        new StrokeMain();
    }
}
