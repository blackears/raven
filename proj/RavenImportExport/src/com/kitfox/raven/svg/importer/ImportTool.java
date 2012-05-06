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

package com.kitfox.raven.svg.importer;

import com.kitfox.rabbit.parser.RabbitDocument;
import com.kitfox.rabbit.parser.RabbitUniverseDom;
import com.kitfox.raven.editor.node.scene.RavenNodeRoot;
import com.kitfox.raven.util.tree.NodeSymbol;
import com.kitfox.raven.util.undo.History;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author kitfox
 */
@Deprecated
public class ImportTool
//        implements WizardResultProducer
{
    public static final String PREF_IMPORT_BG_COL = "importBackgroundColor";
    public static final String PREF_FILE = "file";

    final NodeSymbol doc;
    final RavenNodeRoot root;

    Properties preferences;

    public ImportTool(Properties preferences, NodeSymbol doc)
    {
        this.doc = doc;
        this.preferences = preferences;
        this.root = (RavenNodeRoot)doc;

    }

//    @Override
//    public Object finish(Map map) throws WizardException
//    {
////        String fileStrn = (String)map.get("location");
//        String fileStrn = preferences.getProperty(PREF_FILE);
//        File file = new File(fileStrn);
//        if (!file.exists() || file.isDirectory() || !file.canRead())
//        {
//            return null;
//        }
//
//        try
//        {
//            RabbitUniverseDom universe = new RabbitUniverseDom();
//            RabbitDocument svgDoc = universe.getDocument(file.toURI().toURL());
//
//
//            History hist = doc.getHistory();
//            hist.beginTransaction("Import SVG");
//
//            //Build doc...
//
//            hist.commitTransaction();
//
//            root.getTrackLibrary().synchDocumentToFrame();
//
//        } catch (MalformedURLException ex)
//        {
//            Logger.getLogger(ImportTool.class.getName()).log(Level.SEVERE, null, ex);
//        }
//
//        return null;
//    }
//
//    @Override
//    public boolean cancel(Map map)
//    {
//        return true;
//    }
}
