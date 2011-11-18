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

package com.kitfox.raven.editor.test;

import com.kitfox.raven.editor.RavenEditor;
import com.kitfox.raven.util.text.SystemFontLibrary;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 *
 * @author kitfox
 */
public class RavenEditorMain
{

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        try {
                // Set System L&F
            UIManager.setLookAndFeel(
                UIManager.getSystemLookAndFeelClassName());
        }
        catch (UnsupportedLookAndFeelException e) {
           // handle exception
        }
        catch (ClassNotFoundException e) {
           // handle exception
        }
        catch (InstantiationException e) {
           // handle exception
        }
        catch (IllegalAccessException e) {
           // handle exception
        }

//        ImageCache.inst().setLoader(new ImageLoaderAWT(RavenEditorMain.class.getClassLoader()));
//        GeometryFactoryService.inst().setFactory(new GeometryFactoryAwt());

        //Start fonts loading
        SystemFontLibrary.inst();
        RavenEditor.inst().showEditor();
    }

}
