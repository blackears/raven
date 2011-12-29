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

package com.kitfox.raven.util.tree;

import com.kitfox.raven.util.JAXBUtil;
import com.kitfox.xml.schema.ravendocumentschema.ObjectFactory;
import com.kitfox.xml.schema.ravendocumentschema.RavenTransferableType;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import javax.xml.bind.JAXBElement;

/**
 *
 * @author kitfox
 */
public class NodeObjectTransferable implements Transferable
{
    private String textDefs;

    public static final DataFlavor FLAVOR =
            new DataFlavor(RavenTransferableType.class, null);

    public NodeObjectTransferable()
    {
    }

    public NodeObjectTransferable(String text)
    {
        textDefs = text;
    }

    public NodeObjectTransferable(RavenTransferableType xfer)
    {
        String text = null;

//        try {
            //Output
//            JAXBContext context = JAXBContext.newInstance(
//                    RavenTransferableType.class.getPackage().getName(),
//                    RavenTransferableType.class.getClassLoader());
//
//            Marshaller marshaller = context.createMarshaller();
//            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
//            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            ObjectFactory fact = new ObjectFactory();

            JAXBElement<RavenTransferableType> value = fact.createRavenTransferable(xfer);

            StringWriter sw = new StringWriter();
            JAXBUtil.saveJAXB(value, sw);
//            marshaller.marshal(value, sw);

            text = sw.toString();


//        } catch (JAXBException ex) {
//            Logger.getLogger(NodeObjectTransferable.class.getName()).log(Level.SEVERE, null, ex);
//        }

        textDefs = text;
    }

    @Override
    public DataFlavor[] getTransferDataFlavors()
    {
        return new DataFlavor[]{FLAVOR, DataFlavor.stringFlavor};
    }

    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor)
    {
        if (FLAVOR.equals(flavor) || DataFlavor.stringFlavor.equals(flavor))
        {
            return true;
        }
        return false;
    }

    @Override
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException
    {
        if (textDefs == null)
        {
            return null;
        }

        if (DataFlavor.stringFlavor.equals(flavor))
        {
            return textDefs;
        }

        if (FLAVOR.equals(flavor))
        {
            StringReader reader = new StringReader(textDefs);
            return JAXBUtil.loadJAXB(RavenTransferableType.class, reader);
            
//            try {
//                JAXBContext context = JAXBContext.newInstance(
//                        RavenTransferableType.class.getPackage().getName(),
//                        RavenTransferableType.class.getClassLoader());
//                StringReader reader = new StringReader(textDefs);
//                StreamSource source = new StreamSource(reader);
//                Unmarshaller unmarshaller = context.createUnmarshaller();
//
//                JAXBElement<RavenTransferableType> ele = unmarshaller.unmarshal(
//                        source, RavenTransferableType.class);
//
//                return ele.getValue();
//            } catch (JAXBException ex) {
//                Logger.getLogger(NodeObjectTransferable.class.getName()).log(Level.SEVERE, null, ex);
//            }
        }
        
        return null;
    }

}
