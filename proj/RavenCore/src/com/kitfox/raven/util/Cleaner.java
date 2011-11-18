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

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Automatically track objects and run cleanup code when they become
 * weakly reachable.
 *
 * @author kitfox
 */
public class Cleaner implements Runnable
{
    private static Cleaner instance = new Cleaner();

    ReferenceQueue queue = new ReferenceQueue();
    HashMap<Reference, Runnable> actionMap = new HashMap<Reference, Runnable>();

    Thread thread;

    private Cleaner()
    {
        thread = new Thread(this);
        thread.setDaemon(true);
        thread.setPriority(Thread.MIN_PRIORITY);
        thread.setName("Cleaner");
        thread.start();
    }

    public static Cleaner inst()
    {
        return instance;
    }

    synchronized public void postCleanup(Object obj, Runnable action)
    {
        WeakReference weak = new WeakReference(obj, queue);
        actionMap.put(weak, action);
    }

    public void run()
    {
        while (true)
        {
            try {
                synchronized (Cleaner.class)
                {
                    Reference ref = queue.remove();
                    Runnable action = actionMap.remove(ref);
                    action.run();
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(Cleaner.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
