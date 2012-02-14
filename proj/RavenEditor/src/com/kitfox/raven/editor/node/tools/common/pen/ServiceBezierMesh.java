/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.kitfox.raven.editor.node.tools.common.pen;

import com.kitfox.raven.editor.node.tools.ToolService;
import com.kitfox.raven.shape.network.NetworkMesh;

/**
 *
 * @author kitfox
 */
public interface ServiceBezierMesh extends ToolService
{
    public NetworkMesh getNetworkMesh();
    public void setNetworkMesh(NetworkMesh mesh, boolean history);
}
