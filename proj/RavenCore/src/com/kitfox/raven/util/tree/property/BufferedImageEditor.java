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

package com.kitfox.raven.util.tree.property;

import com.kitfox.raven.util.service.ServiceInst;
import com.kitfox.raven.util.tree.PropertyCustomEditor;
import com.kitfox.raven.util.tree.PropertyData;
import com.kitfox.raven.util.tree.PropertyDataInline;
import com.kitfox.raven.util.tree.PropertyDataResource;
import com.kitfox.raven.util.tree.PropertyProvider;
import com.kitfox.raven.util.tree.PropertyProviderIndex;
import com.kitfox.raven.util.tree.PropertyWrapper;
import com.kitfox.raven.util.tree.PropertyWrapperEditor;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import javax.swing.JPopupMenu;
import sun.misc.UUDecoder;
import sun.misc.UUEncoder;

/**
 *
 * @author kitfox
 */
public class BufferedImageEditor extends PropertyWrapperEditor<BufferedImage>
{
    public BufferedImageEditor(PropertyWrapper wrapper)
    {
        super(wrapper);
    }

    @Override
    protected void buildPopupMenu(JPopupMenu menu)
    {
        appendDefaultMenu(menu);
    }

    @Override
    public boolean isPaintable()
    {
        return false;
    }

    @Override
    public void paintValue(Graphics gfx, Rectangle box)
    {
    }

    @Override
    public String getJavaInitializationString()
    {
        return null;
    }

    @Override
    public String getAsText()
    {
        PropertyData<BufferedImage> data = getValue();
        if (data instanceof PropertyDataInline)
        {
            BufferedImage img = data.getValue(null);
            PropertyProvider<BufferedImage> prov =
                    PropertyProviderIndex.inst().getProviderBest(BufferedImage.class);
            return prov.asText(img);
        }

        if (data instanceof PropertyDataResource)
        {
            URI uri = ((PropertyDataResource)data).getUri();
            return uri.toString();
        }

        return "";
    }

    @Override
    public void setAsText(String text) throws IllegalArgumentException
    {
        PropertyProvider<BufferedImage> prov =
                PropertyProviderIndex.inst().getProviderBest(BufferedImage.class);
        BufferedImage img = prov.fromText(text);

        if (img != null)
        {
            setValue(img);
            return;
        }

        try
        {
            URI uri = new URI(text);
            setValue(new PropertyDataResource<BufferedImage>(uri));
            return;
        } catch (URISyntaxException ex)
        {
            Logger.getLogger(BufferedImageEditor.class.getName()).log(Level.SEVERE, null, ex);
        }

//        setValue(null);
    }

    @Override
    public String[] getTags()
    {
        return null;
    }

    @Override
    public PropertyCustomEditor createCustomEditor()
    {
        return new BufferedImageCustomEditor(this);
    }

    @Override
    public boolean supportsCustomEditor()
    {
        return true;
    }

    //----------------------------

    @ServiceInst(service=PropertyProvider.class)
    public static class Provider extends PropertyProvider<BufferedImage>
    {
        public Provider()
        {
            super(BufferedImage.class);
        }

        @Override
        public PropertyWrapperEditor createEditor(PropertyWrapper wrapper)
        {
            return new BufferedImageEditor(wrapper);
        }

        @Override
        public String asText(BufferedImage value)
        {
            if (value == null)
            {
                return "";
            }

            try
            {
                ByteArrayOutputStream bout = new ByteArrayOutputStream();
                ImageIO.write(value, "png", bout);
                bout.close();

                UUEncoder enc = new UUEncoder();
                String uutext = enc.encode(bout.toByteArray());
                return "data:image/png;base64," + uutext;
            }
            catch (IOException ex)
            {
                Logger.getLogger(BufferedImageEditor.class.getName()).log(Level.SEVERE, null, ex);
            }

            return "";
        }

        @Override
        public BufferedImage fromText(String text)
        {
            Matcher m = Pattern.compile("$data:image/[a-zA-Z]{1,4};base64,").matcher(text);
            if (!m.find())
            {
                return null;
            }

            int idx = text.indexOf(',');
            String code = text.substring(idx + 1);
            UUDecoder dec = new UUDecoder();
            try
            {
                byte[] content = dec.decodeBuffer(code);
                ByteArrayInputStream bin = new ByteArrayInputStream(content);

                BufferedImage img = ImageIO.read(bin);
                return img;
            }
            catch (IOException ex)
            {
                Logger.getLogger(BufferedImageEditor.class.getName()).log(Level.SEVERE, null, ex);
            }

            return null;
        }
    }
}
