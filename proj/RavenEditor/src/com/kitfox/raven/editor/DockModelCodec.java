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

package com.kitfox.raven.editor;

/**
 *
 * @author kitfox
 */
@Deprecated
public class DockModelCodec
{
//	private static final String PROPERTY_CLASS = "dockModelClass";
//
//    public static Properties encodeDockModel(DockModel dockModel)
//    {
//		// Create the properties object.
//		Properties properties = new Properties();
//
//		// Save the class of the dock model.
//		PropertiesUtil.setString(properties, PROPERTY_CLASS, dockModel.getClass().getName());
//
//		// The mapping with the keys that will be used for saving the docks.
//		Map dockKeys = new HashMap();
//
//		dockModel.saveProperties("dockModel.", properties, dockKeys);
//
//		// Save the properties of the docking paths.
//		DockingPathModel dockingPathModel = DockingManager.getDockingPathModel();
//		if (dockingPathModel != null)
//		{
//			dockingPathModel.saveProperties("dockingPathModel.", properties, dockKeys);
//		}
//
//        return properties;
//    }
//
//
//	public static DockModel decode(Properties properties, Map dockablesMap, Map ownersMap, Map visualizersMap) throws IOException
//	{
//		// Create the dock model.
//		DockModel dockModel = createDockModel(properties);
//		DockingManager.setDockModel(dockModel);
//
//		// Load the properties in the model.
//		Map docks = new HashMap();
//		dockModel.loadProperties(null, "dockModel.", properties, dockablesMap, ownersMap, docks, visualizersMap);
//
//		// Create the docking paths.
//		DockingPathModel dockingPathModel = new DefaultDockingPathModel();
//		dockingPathModel.loadProperties("dockingPathModel.", properties, docks);
//		DockingManager.setDockingPathModel(dockingPathModel);
//
//		// Remove the empty docks.
//		for (int index = 0; index < dockModel.getOwnerCount(); index++)
//		{
//			Window owner = dockModel.getOwner(index);
//			Iterator rootDockKeys = dockModel.getRootKeys(owner);
//			while (rootDockKeys.hasNext())
//			{
//				String rootDockKey = (String)rootDockKeys.next();
//				Dock rootDock = dockModel.getRootDock(rootDockKey);
//
//				// Remove the empty children from the root dock.
//				if (rootDock instanceof CompositeDock)
//				{
//					DockingUtil.removeEmptyChildren((CompositeDock)rootDock);
//				}
//
//			}
//		}
//
//		return dockModel;
//	}
//
//	private static DockModel createDockModel(Properties properties) throws IOException
//	{
//
//		// Create the dock model object with the class name property.
//		String className = null;
//		className = PropertiesUtil.getString(properties, PROPERTY_CLASS, className);
//		Class clazz = null;
//		DockModel dockModel = null;
//		try
//		{
//			clazz = Class.forName(className);
//		}
//		catch (ClassNotFoundException classNotFoundException)
//		{
//			throw new IOException("Could not find class [" + className + "] (ClassNotFoundException).");
//		}
//		try
//		{
//			dockModel = (DockModel)clazz.newInstance();
//		}
//		catch (IllegalAccessException illegalAccessException)
//		{
//			throw new IOException("Illegal acces to class [" + className + "] (IllegalAccessException).");
//		}
//		catch (InstantiationException instantiationException)
//		{
//			throw new IOException("Could not instantiate class [" + className + "] (InstantiationException).");
//		}
//		catch (ClassCastException classCastException)
//		{
//			throw new IOException("Class [" + className + "] is not a Dock. (ClassCastException).");
//		}
//
//		return dockModel;
//	}

}
