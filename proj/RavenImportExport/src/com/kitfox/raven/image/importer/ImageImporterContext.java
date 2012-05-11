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

package com.kitfox.raven.image.importer;

import com.kitfox.coyote.math.CyMatrix4d;
import com.kitfox.coyote.shape.CyPath2d;
import com.kitfox.coyote.shape.CyRectangle2i;
import com.kitfox.coyote.shape.CyStroke;
import com.kitfox.coyote.shape.outliner.image.ImageEdge;
import com.kitfox.coyote.shape.outliner.image.ImageOutliner;
import com.kitfox.raven.editor.node.scene.RavenNodeGroup;
import com.kitfox.raven.editor.node.scene.RavenNodeMesh;
import com.kitfox.raven.editor.node.scene.RavenSymbolRoot;
import com.kitfox.raven.paint.RavenPaint;
import com.kitfox.raven.paint.RavenPaintLayout;
import com.kitfox.raven.paint.RavenStroke;
import com.kitfox.raven.paint.common.RavenPaintColor;
import com.kitfox.raven.shape.network.NetworkDataEdge;
import com.kitfox.raven.shape.network.NetworkMesh;
import com.kitfox.raven.shape.network.keys.NetworkDataTypePaint;
import com.kitfox.raven.shape.network.keys.NetworkDataTypePaintLayout;
import com.kitfox.raven.shape.network.keys.NetworkDataTypeStroke;
import com.kitfox.raven.util.PropertiesData;
import com.kitfox.raven.util.Selection;
import com.kitfox.raven.util.tree.NodeSymbol;
import com.kitfox.raven.util.tree.NodeObject;
import com.kitfox.raven.util.tree.NodeObjectProviderIndex;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

/**
 *
 * @author kitfox
 */
public class ImageImporterContext
{
    public static final String PROP_SEQ_FILE = "seqFile";
    private String seqFile;
    
    public static final String PROP_USE_BACKGROUND = "smoothing";
    private float smoothing;
    
    private NodeSymbol sym;
    private PropertiesData pref;
    
    public ImageImporterContext(NodeSymbol doc, Properties preferences)
    {
        this.sym = doc;
        this.pref = new PropertiesData(preferences);
        
        seqFile = pref.getString(PROP_SEQ_FILE, "");
        smoothing = pref.getFloat(PROP_USE_BACKGROUND, 4);
    }
    
    public void savePreferences()
    {
        pref.setString(PROP_SEQ_FILE, seqFile);
        pref.setFloat(PROP_USE_BACKGROUND, smoothing);
    }

    private void errMessage(String message)
    {
        JOptionPane.showMessageDialog(sym.getDocument().getEnv().getSwingRoot(),
                message, 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
    }
    
    public void doImport()
    {
        File file = new File(seqFile);
        if (!file.canRead())
        {
            errMessage("Cannot open " + seqFile + " for reading");
            return;
        }

        BufferedImage img = null;
        try
        {
            img = ImageIO.read(file);
        } catch (IOException ex)
        {
            errMessage(ex.getLocalizedMessage());
            Logger.getLogger(ImageImporterContext.class.getName()).log(Level.WARNING, null, ex);
            return;
        }
        
        CyRectangle2i region = new CyRectangle2i(0, 0, 
                img.getWidth(), img.getHeight());
        ImageOutliner outliner = new ImageOutliner(
                new ImageSampler(img), 
                region, 0, smoothing);
        
        ArrayList<ImageEdge> edges = outliner.getEdges();
        CyRectangle2i boundsLocal = outliner.getBounds();
        createMeshNode(edges, boundsLocal);
    }
    
    private void createMeshNode(ArrayList<ImageEdge> edges, 
            CyRectangle2i boundsLocal)
    {
        CyMatrix4d scale = RavenNodeMesh.getMeshToLocal();
        scale.invert();

        NetworkMesh network = new NetworkMesh();
        
        RavenPaintLayout layout = new RavenPaintLayout(boundsLocal);
        RavenStroke stroke = new RavenStroke(new CyStroke(1));
        
        for (ImageEdge edge: edges)
        {
            CyPath2d path = edge.getPath();
            CyPath2d meshPath = path.createTransformedPath(scale);
        
            RavenPaint edgeColor = RavenPaintColor.BLACK;
            
            int lLeft = edge.getLevelLeft();
            int lRight = edge.getLevelRight();
            
            RavenPaint leftColor = lLeft == -1 
                    ? null
                    : new RavenPaintColor(lLeft);
            RavenPaint rightColor = lRight == -1 
                    ? null
                    : new RavenPaintColor(lRight);
            
            NetworkDataEdge data = new NetworkDataEdge();
            data.putLeft(NetworkDataTypePaint.class, leftColor);
            data.putLeft(NetworkDataTypePaintLayout.class, layout);

            data.putRight(NetworkDataTypePaint.class, rightColor);
            data.putRight(NetworkDataTypePaintLayout.class, layout);

            data.putEdge(NetworkDataTypePaint.class, edgeColor);
            data.putEdge(NetworkDataTypePaintLayout.class, layout);
            data.putEdge(NetworkDataTypeStroke.class, stroke);
            
            network.addEdge(meshPath, data);
        }
        
        //Create node
        RavenNodeMesh mesh = NodeObjectProviderIndex.inst().createNode(
                RavenNodeMesh.class, sym);
        mesh.setNetworkMesh(network, false);
        
        //Add to tree
        Selection<NodeObject> sel = sym.getSelection();
        RavenNodeGroup parGrp = sel.getTopSelected(RavenNodeGroup.class);
        
        if (parGrp != null)
        {
            parGrp.children.add(mesh);
        }
        else
        {
            RavenSymbolRoot root = (RavenSymbolRoot)sym.getRoot();
            root.getSceneGraph().children.add(mesh);
        }
    }

    //--------------

    /**
     * @return the doc
     */
    public NodeSymbol getDoc()
    {
        return sym;
    }

    /**
     * @param doc the doc to set
     */
    public void setDoc(NodeSymbol doc)
    {
        this.sym = doc;
    }

    /**
     * @return the pref
     */
    public PropertiesData getPref()
    {
        return pref;
    }

    /**
     * @param pref the pref to set
     */
    public void setPref(PropertiesData pref)
    {
        this.pref = pref;
    }

    /**
     * @return the seqFile
     */
    public String getSeqFile()
    {
        return seqFile;
    }

    /**
     * @param seqFile the seqFile to set
     */
    public void setSeqFile(String seqFile)
    {
        this.seqFile = seqFile;
    }

    /**
     * @return the smoothing
     */
    public float getSmoothing()
    {
        return smoothing;
    }

    /**
     * @param smoothing the smoothing to set
     */
    public void setSmoothing(float smoothing)
    {
        this.smoothing = smoothing;
    }
}
