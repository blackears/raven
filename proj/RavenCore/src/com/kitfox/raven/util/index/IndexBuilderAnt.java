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

package com.kitfox.raven.util.index;

import com.kitfox.raven.util.JAXBUtil;
import com.kitfox.xml.schema.indexlog.IndexBuilderLogType;
import com.kitfox.xml.schema.indexlog.IndexItemLogType;
import com.kitfox.xml.schema.indexlog.IndexLog;
import com.kitfox.xml.schema.indexlog.ObjectFactory;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ServiceLoader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBElement;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.types.Path;

/**
 *
 * @author kitfox
 */
public class IndexBuilderAnt extends Task
{
    public class ItemTuple
    {
        private final int id;
        private final String indexClass;

        public ItemTuple(int id, String indexClass) {
            this.id = id;
            this.indexClass = indexClass;
        }

        /**
         * @return the id
         */
        public int getId() {
            return id;
        }

        /**
         * @return the indexClass
         */
        public String getIndexClass() {
            return indexClass;
        }

        @Override
        public String toString() {
            return "" + id + " " + indexClass;
        }


    }

    class IndexItems
    {
        int nextId;
        final String indexClass;
        HashMap<String, ItemTuple> classMap = new HashMap<String, ItemTuple>();
//        final private ArrayList<String> items = new ArrayList<String>();

        ArrayList<ItemTuple> aliveTuples = new ArrayList<ItemTuple>();

        public IndexItems(String indexClass) {
            this.indexClass = indexClass;
        }

        /**
         * @return the items
         */
        public ArrayList<ItemTuple> getItems()
        {
            //return new ArrayList<ItemTuple>(classMap.values());
            return aliveTuples;
        }

        private void addInit(int index, String itemClass)
        {
            ItemTuple tuple = new ItemTuple(index, itemClass);
            classMap.put(tuple.getIndexClass(), tuple);
            nextId = Math.max(nextId, index);
        }

        private void add(String itemClass)
        {
            ItemTuple tuple = classMap.get(itemClass);
            if (tuple == null)
            {
                int index = nextId++;
                tuple = new ItemTuple(index, itemClass);
                classMap.put(tuple.getIndexClass(), tuple);
                nextId = Math.max(nextId, index);
            }
            aliveTuples.add(tuple);
        }
    }

    Path classpath;
//    private String indexClass;
    private File outDir;
    private File log;

    final Configuration cfg = new Configuration();
    Template ftlTemplate;
    final String TMPLT_NAME = "IndexTmplt.ftl";

    @Override
    public void execute() throws BuildException
    {
        //Logger.getLogger(ServicesTask.class.getName()).log(Level.INFO, null, ex);

        if (outDir == null)
        {
            throw new BuildException("Output directory not specified");
        }
        if (log == null)
        {
            throw new BuildException("Log file not specified");
        }
//        if (indexClass == null)
//        {
//            throw new BuildException("serviceClass not specified");
//        }

        HashMap<String, IndexItems> map = loadLog(log);
        System.err.println("Loaded map size " + map.size());

        //Make entire classpath available for finding services
        ArrayList<URL> urls = new ArrayList<URL>();
        if (classpath != null)
        {
            for (String cp: classpath.list())
            {
                try {
                    File file = new File(cp);
                    URI uri = file.toURI();
                    urls.add(uri.toURL());
                } catch (MalformedURLException ex) {
                    Logger.getLogger(IndexBuilderAnt.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        URLClassLoader loader = new URLClassLoader(urls.toArray(new URL[urls.size()]), getClass().getClassLoader());
//        Class servClass = null;
//        try {
//            servClass = loader.loadClass(indexClass);
//        } catch (ClassNotFoundException ex) {
//            Logger.getLogger(IndexBuilderAnt.class.getName()).log(Level.SEVERE, null, ex);
//            return;
//        }

        ServiceLoader<IndexStub> servLoader = ServiceLoader.load(IndexStub.class, loader);

//        ArrayList<String> availClasses = new ArrayList<String>();
        for (Iterator<IndexStub> it = servLoader.iterator(); it.hasNext();)
        {
            IndexStub stub = it.next();

            String indexCls = stub.getIndexClass().getCanonicalName();
//            availClasses.add(indexCls);
            IndexItems items = map.get(indexCls);
            if (items == null)
            {
                items = new IndexItems(indexCls);
                map.put(indexCls, items);
            }

            String itemName = stub.getIndexItemClass().getCanonicalName();
            items.add(itemName);
//            System.err.println("Found class " + stub);
        }


        saveLog(log, map);

        //Write indices
        cfg.setClassForTemplateLoading(getClass(), "");
        cfg.setObjectWrapper(new DefaultObjectWrapper());
        try {
            ftlTemplate = cfg.getTemplate(TMPLT_NAME);
        } catch (IOException ex) {
            Logger.getLogger(IndexApt.class.getName()).log(Level.SEVERE, null, ex);
        }

        for (IndexItems ii: map.values())
        {
            Class indexCls;
            try {
                indexCls = loader.loadClass(ii.indexClass);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(IndexBuilderAnt.class.getName()).log(Level.SEVERE, null, ex);
                continue;
            }

            String packageName = indexCls.getPackage().getName();
            String simpleName = indexCls.getSimpleName();

            HashMap<String, Object> data = new HashMap<String, Object>();
            data.put("author", "kitfox");
            data.put("date", new Date());
            data.put("package", packageName);
            data.put("className", simpleName);
            data.put("classQname", indexCls.getCanonicalName());
            data.put("tuples", ii.getItems());

            try {
                File file = new File(outDir, packageName.replace('.', '/') 
                        + "/" + simpleName + "Index.java");
                file.getParentFile().mkdirs();
                
System.err.println("Index file " + file);
                FileWriter fw = new FileWriter(file);

                ftlTemplate.process(data, fw);
                fw.close();
            } catch (Exception ex) {
                Logger.getLogger(IndexBuilderAnt.class.getName()).log(Level.SEVERE, null, ex);
            }
            
        }


//        System.err.println("Found class " + cls);

//        DirectoryScanner ds = classesToIndex.getDirectoryScanner();
//        String[] files = ds.getIncludedFiles();
//        //File basedir = ds.getBasedir();
//
//        outDir.mkdirs();
//        File outFile = new File(outDir, indexClass);
//        FileWriter fw;
//        try {
//            fw = new FileWriter(outFile);
//        } catch (IOException ex) {
//            Logger.getLogger(IndexBuilderAnt.class.getName()).log(Level.SEVERE, null, ex);
//            return;
//        }
//        PrintWriter pw = new PrintWriter(fw);
//
//        for (String filename: files)
//        {
//            if (!filename.endsWith(".class"))
//            {
//                continue;
//            }
//            filename = filename.substring(0, filename.length() - 6);
//            String clsName = filename.replaceAll("[/\\\\]", "\\.");
////            System.err.println("^" + clsName);
//            Class cls;
//            try {
//                cls = loader.loadClass(clsName);
//            } catch (ClassNotFoundException ex) {
//                //Logger.getLogger(ServicesTask.class.getName()).log(Level.SEVERE, null, ex);
//                continue;
//            }
//
////            System.err.println(" " + servClass + " " + cls);
//            if (servClass.isAssignableFrom(cls))
//            {
////                System.err.println("extends " + clsName);
//                pw.println(clsName);
//            }
//        }
//
//        pw.close();
    }

    public Path createClasspath()
    {
        classpath = new Path(getProject());
        return classpath;
    }

//    /**
//     * @return the serviceClass
//     */
//    public String getIndexClass() {
//        return indexClass;
//    }
//
//    /**
//     * @param indexClass the serviceClass to set
//     */
//    public void setIndexClass(String indexClass) {
//        this.indexClass = indexClass;
//    }

    /**
     * @return the destDir
     */
    public File getOutDir() {
        return outDir;
    }

    /**
     * @param destDir the destDir to set
     */
    public void setOutDir(File destDir) {
        this.outDir = destDir;
    }

    /**
     * @return the log
     */
    public File getLog() {
        return log;
    }

    /**
     * @param log the log to set
     */
    public void setLog(File log) {
        this.log = log;
    }


    private HashMap<String, IndexItems> loadLog(File log)
    {
        HashMap<String, IndexItems> map = new HashMap<String, IndexItems>();
        IndexBuilderLogType logTable = null;
        if (log.exists())
        {
            IndexBuilderLogType logType = JAXBUtil.loadJAXB(IndexBuilderLogType.class, log);


//                FileReader is = new FileReader(log);
//
//                JAXBContext context = JAXBContext.newInstance(IndexBuilderLogType.class);
//                StreamSource source = new StreamSource(is);
//                Unmarshaller unmarshaller = context.createUnmarshaller();
//
//                JAXBElement<IndexBuilderLogType> ele = unmarshaller.unmarshal(source, IndexBuilderLogType.class);

            for (IndexLog idx: logType.getIndex())
            {
                String cls = idx.getClazz();
                IndexItems indexItems = new IndexItems(cls);
                map.put(cls, indexItems);

                for (IndexItemLogType item: idx.getItem())
                {
                    indexItems.addInit(item.getIndex(), item.getItemClass());
                }
            }
        }

        return map;
    }

    private void saveLog(File log, HashMap<String, IndexItems> map)
    {
        IndexBuilderLogType type = new IndexBuilderLogType();
        ObjectFactory fact = new ObjectFactory();
        JAXBElement<IndexBuilderLogType> value = fact.createIndexBuilderLog(type);

        for (String cls: map.keySet())
        {
            IndexItems items = map.get(cls);

            IndexLog indexLog = new IndexLog();
            indexLog.setClazz(cls);
            type.getIndex().add(indexLog);

            for (ItemTuple tup: items.getItems())
            {
                IndexItemLogType iilt = new IndexItemLogType();
                indexLog.getItem().add(iilt);
                iilt.setIndex(tup.getId());
                iilt.setItemClass(tup.getIndexClass());
            }
        }

        JAXBUtil.saveJAXB(value, log);
//        try {
//            
//            FileWriter writer = new FileWriter(log);
//
////            String packg = IndexBuilderLogType.class.getPackage().getName();
////            JAXBContext context = JAXBContext.newInstance(packg, IndexBuilderLogType.class.getClassLoader());
//            JAXBContext context = JAXBContext.newInstance(IndexBuilderLogType.class);
//            Marshaller marshaller = context.createMarshaller();
//            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
//            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
//
//            marshaller.marshal(value, writer);
//        } catch (IOException ex) {
//            Logger.getLogger(IndexBuilderAnt.class.getName()).log(Level.WARNING, null, ex);
//        } catch (JAXBException ex) {
//            Logger.getLogger(IndexBuilderAnt.class.getName()).log(Level.WARNING, null, ex);
//        }
        
    }
}
