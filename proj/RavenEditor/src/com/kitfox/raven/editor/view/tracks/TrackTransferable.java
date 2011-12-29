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

package com.kitfox.raven.editor.view.tracks;

import com.kitfox.raven.util.JAXBUtil;
import com.kitfox.xml.schema.ravendocumentschema.ObjectFactory;
import com.kitfox.xml.schema.ravendocumentschema.TrackTransferableType;
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
public class TrackTransferable implements Transferable
{
    String layerDefs;

    static final DataFlavor FLAVOR = new DataFlavor(TrackTransferable.class, null);


    public TrackTransferable()
    {
    }

    public TrackTransferable(String text)
    {
        layerDefs = text;
    }

    public TrackTransferable(TrackTransferableType track)
    {
        String text = null;

//        try {
            //Output
//            JAXBContext context = JAXBContext.newInstance(
//                    TrackTransferableType.class.getPackage().getName(),
//                    TrackTransferableType.class.getClassLoader());
//
//            Marshaller marshaller = context.createMarshaller();
//            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
//            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            ObjectFactory fact = new ObjectFactory();

            JAXBElement<TrackTransferableType> value = fact.createTrackTransferable(track);

            StringWriter sw = new StringWriter();
            JAXBUtil.saveJAXB(value, sw);
//            marshaller.marshal(value, sw);

            text = sw.toString();


//        } catch (JAXBException ex) {
//            Logger.getLogger(TrackTransferable.class.getName()).log(Level.SEVERE, null, ex);
//        }

        layerDefs = text;
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
        if (layerDefs == null)
        {
            return null;
        }

        if (DataFlavor.stringFlavor.equals(flavor))
        {
            return layerDefs;
        }

        if (FLAVOR.equals(flavor))
        {
            StringReader reader = new StringReader(layerDefs);
            return JAXBUtil.loadJAXB(TrackTransferableType.class, reader);
            
//            try {
//                JAXBContext context = JAXBContext.newInstance(
//                        TrackTransferableType.class.getPackage().getName(),
//                        TrackTransferableType.class.getClassLoader());
//                StringReader reader = new StringReader(layerDefs);
//                StreamSource source = new StreamSource(reader);
//                Unmarshaller unmarshaller = context.createUnmarshaller();
//
//                JAXBElement<TrackTransferableType> ele = unmarshaller.unmarshal(source, TrackTransferableType.class);
//
//                return ele.getValue();
//            } catch (JAXBException ex) {
//                Logger.getLogger(TrackTransferable.class.getName()).log(Level.SEVERE, null, ex);
//            }
        }

        return null;
    }

}
