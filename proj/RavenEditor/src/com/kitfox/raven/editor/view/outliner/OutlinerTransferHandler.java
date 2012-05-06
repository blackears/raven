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

package com.kitfox.raven.editor.view.outliner;

import com.kitfox.raven.util.tree.ChildWrapper;
import com.kitfox.raven.util.tree.ChildWrapperList;
import com.kitfox.raven.util.tree.NodeSymbol;
import com.kitfox.raven.util.tree.NodeObject;
import com.kitfox.raven.util.tree.NodeObjectTransferable;
import com.kitfox.raven.util.undo.History;
import com.kitfox.xml.schema.ravendocumentschema.NodeObjectType;
import com.kitfox.xml.schema.ravendocumentschema.RavenTransferableType;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.tree.TreePath;

/**
 *
 * @author kitfox
 */
public class OutlinerTransferHandler extends TransferHandler
{
    private static final long serialVersionUID = 0;

    private final OutlinerTreeModel model;
    
//    ArrayList<GameObjectWrapper> transferObjs = new ArrayList<GameObjectWrapper>();

    public OutlinerTransferHandler(OutlinerTreeModel tree)
    {
        this.model = tree;
    }

    @Override
    public boolean canImport(TransferHandler.TransferSupport info)
    {
        return info.isDataFlavorSupported(NodeObjectTransferable.FLAVOR)
                || info.isDataFlavorSupported(DataFlavor.stringFlavor);
    }

    @Override
    public boolean importData(TransferHandler.TransferSupport info)
    {
        // Check for String FLAVOR
//            if (!info.isDataFlavorSupported(LayerTransferable.FLAVOR))
//            {
//                return false;
//            }

        //Find (parent/insertion cursor) for import
        JTree treeCtrl = (JTree)info.getComponent();
        TreePath dropParentPath;
//        NodeObject dropNodeParent;
        int dropIndex;
        int dropAction = TransferHandler.NONE;
        if (info.isDrop())
        {
            //Drag & drop
            JTree.DropLocation dl = (JTree.DropLocation)info.getDropLocation();
            dropParentPath = dl.getPath();
            dropIndex = dl.getChildIndex();
            dropAction = info.getDropAction();
            
            
            OutlinerNode node = (OutlinerNode)dropParentPath.getLastPathComponent();
            if (!node.isLeaf())
            {
                if (dropIndex == -1)
                {
                    dropIndex = node.getChildCount();
                }
            }
            else
            {
                dropParentPath = dropParentPath.getParentPath();
                OutlinerNode parentNode = (OutlinerNode)dropParentPath.getLastPathComponent();
                dropIndex = parentNode.getIndexOfChild(node) + 1;
            }
        }
        else
        {
            //Paste
            dropParentPath = treeCtrl.getSelectionPath();
            OutlinerNode node = (OutlinerNode)dropParentPath.getLastPathComponent();
            if (!node.isLeaf())
            {
                dropIndex = node.getChildCount();
            }
            else
            {
                dropParentPath = dropParentPath.getParentPath();
                OutlinerNode parentNode = (OutlinerNode)dropParentPath.getLastPathComponent();
                dropIndex = parentNode.getIndexOfChild(node) + 1;
            }
        }

        //Get transferable payload
        RavenTransferableType xferLayers = null;

        try {
            if (info.isDataFlavorSupported(NodeObjectTransferable.FLAVOR))
            {
                Transferable xfer = info.getTransferable();
                xferLayers = (RavenTransferableType)xfer.getTransferData(NodeObjectTransferable.FLAVOR);
            }
            else if (info.isDataFlavorSupported(DataFlavor.stringFlavor))
            {
                NodeObjectTransferable xfer = new NodeObjectTransferable(
                        (String)info.getTransferable()
                        .getTransferData(DataFlavor.stringFlavor));

                xferLayers = (RavenTransferableType)xfer.getTransferData(NodeObjectTransferable.FLAVOR);
            }
            else
            {
                return false;
            }
        } catch (UnsupportedFlavorException ex) {
            Logger.getLogger(OutlinerTransferHandler.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } catch (IOException ex) {
            Logger.getLogger(OutlinerTransferHandler.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }

        //Import new nodes
        OutlinerNode dropNodeParent = (OutlinerNode)dropParentPath.getLastPathComponent();
        boolean success = dropNodeParent.paste(dropIndex, xferLayers);

        return success;
    }

    @Override
    public int getSourceActions(JComponent c)
    {
        return COPY_OR_MOVE;
    }

    @Override
    public Transferable createTransferable(JComponent c)
    {
        JTree tree = (JTree)c;

        RavenTransferableType xferLayers = new RavenTransferableType();

        TreePath[] paths = tree.getSelectionPaths();
        NEXT_PATH:
        for (TreePath path: paths)
        {
            OutlinerNode node = (OutlinerNode)path.getLastPathComponent();
            if (!(node.getParent() instanceof OutlinerNodeChildList))
            {
                //Node not child of a list
                continue;
            }

            for (TreePath otherPath: paths)
            {
                if (!path.equals(otherPath) && path.isDescendant(otherPath))
                {
                    continue NEXT_PATH;
                }
            }

            if (node instanceof OutlinerNodeNodeFixed)
            {
                xferLayers.getNodes().add(
                    ((OutlinerNodeNodeFixed)node).getNode().export());
            }
            else if(node instanceof OutlinerNodeNodeList)
            {
                xferLayers.getNodes().add(
                    ((OutlinerNodeNodeList)node).getNode().export());
            }
        }

        Transferable xfer = new NodeObjectTransferable(xferLayers);
        return xfer;
    }

    @Override
    public void exportDone(JComponent c, Transferable t, int action)
    {
        NodeObjectTransferable xfer = (NodeObjectTransferable)t;
        RavenTransferableType xferLayers = null;
        try {
            xferLayers = (RavenTransferableType) xfer.getTransferData(NodeObjectTransferable.FLAVOR);

        } catch (UnsupportedFlavorException ex) {
            Logger.getLogger(OutlinerTransferHandler.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(OutlinerTransferHandler.class.getName()).log(Level.SEVERE, null, ex);
        }


        NodeSymbol doc = model.getDocument();
        if (action == TransferHandler.MOVE)
        {
            History hist = doc.getHistory();
            hist.beginTransaction("Cut");

            for (NodeObjectType type: xferLayers.getNodes())
            {
                NodeObject node = doc.getNode(type.getUid());
                ChildWrapper parent = node.getParent();
                ((ChildWrapperList)parent).remove(node);
            }

            hist.commitTransaction();
        }
        else if (action == TransferHandler.COPY)
        {
        }

    }

}
