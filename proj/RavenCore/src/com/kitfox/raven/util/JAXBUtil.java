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

package com.kitfox.raven.util;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

/**
 *
 * @author kitfox
 */
public class JAXBUtil
{
    public static <T> T loadJAXB(Class<T> jaxbClass, File file)
    {
        try
        {
            FileInputStream fin = new FileInputStream(file);
            T res = loadJAXB(jaxbClass, fin);
            fin.close(); 
            return res;
        } catch (IOException ex)
        {
            Logger.getLogger(JAXBUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static <T> T loadJAXB(Class<T> jaxbClass, InputStream is)
    {
        try {
            JAXBContext context = JAXBContext.newInstance(jaxbClass);
            StreamSource source = new StreamSource(is);
            Unmarshaller unmarshaller = context.createUnmarshaller();

            JAXBElement<T> ele = unmarshaller.unmarshal(
                    source, jaxbClass);

            return ele.getValue();
        } catch (JAXBException ex) {
            Logger.getLogger(JAXBUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static <T> T loadJAXB(Class<T> jaxbClass, Reader reader)
    {
        try {
            JAXBContext context = JAXBContext.newInstance(jaxbClass);
            StreamSource source = new StreamSource(reader);
            Unmarshaller unmarshaller = context.createUnmarshaller();

            JAXBElement<T> ele = unmarshaller.unmarshal(
                    source, jaxbClass);

            return ele.getValue();
        } catch (JAXBException ex) {
            Logger.getLogger(JAXBUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public static <T> void saveJAXB(JAXBElement<T> jaxbValue, File file)
    {
        FileOutputStream fout = null;
        try
        {
            fout = new FileOutputStream(file);
            saveJAXB(jaxbValue, fout);
            fout.close();
        } catch (IOException ex)
        {
            Logger.getLogger(JAXBUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static <T> void saveJAXB(JAXBElement<T> jaxbValue, OutputStream out)
    {
        try {

//            String packg = IndexBuilderLogType.class.getPackage().getName();
//            JAXBContext context = JAXBContext.newInstance(packg, IndexBuilderLogType.class.getClassLoader());
            JAXBContext context = JAXBContext.newInstance(jaxbValue.getDeclaredType());
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            marshaller.marshal(jaxbValue, out);
        } catch (JAXBException ex) {
            Logger.getLogger(JAXBUtil.class.getName()).log(Level.WARNING, null, ex);
        }
    }
    
    public static <T> void saveJAXB(JAXBElement<T> jaxbValue, Writer writer)
    {
        try {

//            String packg = IndexBuilderLogType.class.getPackage().getName();
//            JAXBContext context = JAXBContext.newInstance(packg, IndexBuilderLogType.class.getClassLoader());
            JAXBContext context = JAXBContext.newInstance(jaxbValue.getDeclaredType());
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            marshaller.marshal(jaxbValue, writer);
        } catch (JAXBException ex) {
            Logger.getLogger(JAXBUtil.class.getName()).log(Level.WARNING, null, ex);
        }
    }
}
