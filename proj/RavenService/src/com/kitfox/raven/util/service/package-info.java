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

/**
 * <p>Services are a convenient way to provide plugin like functionallity in
 * Java.  You start by creating a regular class or interface that will act
 * as the service type.  You can then create any number of subclasses of
 * it that have no-argument constructors.  Each of these subclasses is a
 * service.  At runtime, you can create instances of these services.  This
 * way your program can search for all instances of service type and
 * present them as a list to the program.</p>
 *
 * <p>Jars specify the services they provide by listing them in appropriately
 * named files in the /META-INF/services directory.  (More info about services
 * can be found in the javadoc for java.util.ServiceLoader).  Maintaining these
 * lists can be tedious and error prone.  RavenService provides a convenient
 * way to generate lists automatically using annotations.</p>
 *
 * <p>To mark a class for inclusion in service generation, just add a
 * ServiceList annotation:</p>
 *
 * <code>
 * @ServiceInst(service=MyService.class)
 * public class MyServiceImplementation extends MyService
 * {
 *     ...
 * }
 * </code>
 *
 * <p>Then when you compile your code, make sure to add
 * com.kitfox.raven.util.service.ServiceApt to your list of annotation
 * processors.</p>
 */

package com.kitfox.raven.util.service;
