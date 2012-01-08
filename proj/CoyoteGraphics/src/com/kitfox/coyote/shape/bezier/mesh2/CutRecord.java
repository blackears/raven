/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.kitfox.coyote.shape.bezier.mesh2;

/**
 *
 * @author kitfox
 */
public class CutRecord
{
    CutSegment[] splitCur;
    CutSegment[] splitOth;

    public CutRecord(CutSegment[] splitCur, CutSegment[] splitOth)
    {
        this.splitCur = splitCur;
        this.splitOth = splitOth;
    }
}
