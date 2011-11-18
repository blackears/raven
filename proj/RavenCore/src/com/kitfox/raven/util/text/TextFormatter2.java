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

package com.kitfox.raven.util.text;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.Path2D.Double;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author kitfox
 */
abstract public class TextFormatter2
{
    abstract protected GlyphInfo createGlyph(char ch);
    abstract public int getLineHeight();
    abstract public int getLineAscent();


    protected boolean isWhitespace(char ch)
    {
        switch (ch)
        {
            case ' ':
            case '\t':
                return true;
        }
        return false;
    }

    protected boolean isNewline(char ch)
    {
        return ch == '\n';
    }

    /**
     * Separate text into text, whitespace and newline sections.
     *
     * @param text Text to parse
     * @param whitespaceChars Characters in text treated as whitespace
     * @param newlineChars Characters in text treated as newlines
     * @param skipChars Characters in text that will be skipped over
     * @return List of tokens
     */
    private ArrayList<WordToken> tokenize(String text)
    {
        ArrayList<WordToken> list = new ArrayList<WordToken>();

        ArrayList<GlyphToken> word = new ArrayList<GlyphToken>();
        Type type = Type.TEXT;
        for (int i = 0; i < text.length(); ++i)
        {
            char ch = text.charAt(i);

            Type nextType = Type.TEXT;
            if (isWhitespace(ch))
            {
                nextType = Type.SPACE;
            }
            else if (isNewline(ch))
            {
                nextType = Type.NEWLINE;
            }

            GlyphInfo info = createGlyph(ch);
            GlyphToken glyphToken = new GlyphToken(i, info, nextType);
//            glyphToken.advance = nextType == Type.NEWLINE
//                    ? 0 : getWidth(ch);

            if (nextType != type || nextType == Type.NEWLINE)
            {
                if (!word.isEmpty())
                {
                    list.add(new WordToken( 
                            word.toArray(new GlyphToken[word.size()]), type));
                }
                type = nextType;
                word.clear();
            }
            word.add(glyphToken);
        }

        if (!word.isEmpty())
        {
            list.add(new WordToken(
                    word.toArray(new GlyphToken[word.size()]), type));
        }

        return list;
    }

    /**
     * Splits tokenized text into (possibly) wrapped lines.
     *
     * @param tokens Tokens to split
     * @param wrapWidth Width to wrap lines.  If 0, lines are not wrapped
     * @param metrics Tools for measuring text layout size.
     * @return
     */
    private LineSetToken splitIntoLines(
            ArrayList<WordToken> tokens,
            float wrapWidth)
    {
        ArrayList<LineToken> lines =
                new ArrayList<LineToken>();

        ArrayList<WordToken> line = new ArrayList<WordToken>();
        float lineWidth = 0;
        for (int i = 0; i < tokens.size(); ++i)
        {
            WordToken token = tokens.get(i);
            switch (token.type)
            {
                case TEXT:
                {
                    float width = token.width;
                    if (wrapWidth != 0
                            && lineWidth + width > wrapWidth
                            && !line.isEmpty())
                    {
                        //This word takes us past wrap boundary.  Complete
                        // current line.
                        lines.add(new LineToken(lines.size(),
                                line.toArray(new WordToken[line.size()])));
                        line.clear();
                        lineWidth = 0;
                    }
                    line.add(token);
                    lineWidth += width;
                    break;
                }
                case SPACE:
                {
                    float width = token.width;
                    line.add(token);
                    lineWidth += width;
                    break;
                }
                case NEWLINE:
                {
                    lines.add(new LineToken(lines.size(), 
                            line.toArray(new WordToken[line.size()])));
                    line.clear();
                    line.add(token);
                    lineWidth = 0;
                    break;
                }
            }
        }

        if (!line.isEmpty())
        {
            lines.add(new LineToken(lines.size(),
                    line.toArray(new WordToken[line.size()])));
        }

//        for (LineToken curLine: lines)
        for (int i = 0; i < lines.size() - 1; ++i)
        {
            LineToken curLine = lines.get(i);
            curLine.supressTrailingSpace();
//            curLine.buildWidth();
        }

        return new LineSetToken(lines.toArray(new LineToken[lines.size()]));
    }

    private LineSetToken layout(LineSetToken lineSet,
            float wrapWidth,
            Justify justify)
    {
        for (int lineIdx = 0; lineIdx < lineSet.lines.length; ++lineIdx)
        {
            LineToken line = lineSet.lines[lineIdx];

            //Calc component widths
            float totalWidth = 0;
            float textWidth = 0;
            int numTextTokens = 0;
            int textTokenFirst = -1;
            int textTokenLast = -1;

            for (int i = 0; i < line.words.length; ++i)
            {
                WordToken token = line.words[i];
                totalWidth += token.width;
                if (token.type == Type.TEXT)
                {
                    if (textTokenFirst == -1)
                    {
                        textTokenFirst = i;
                    }
                    textTokenLast = i;
                    textWidth += token.width;
                    ++numTextTokens;
                }
            }

//            if (numTextTokens == 0)
//            {
//                continue;
//            }

            //Layout
            float cursor;
            switch (justify)
            {
                default:
                    cursor = 0;
                    break;
                case RIGHT:
                    cursor = wrapWidth - totalWidth;
                    break;
                case CENTER:
                    cursor = (wrapWidth - totalWidth) / 2;
                    break;
            }

            if (justify == Justify.JUSTIFY)
            {
                if (numTextTokens == 0)
                {
                    for (int i = 0; i < line.words.length; ++i)
                    {
                        line.words[i].setOffset(cursor);
                        line.words[i].supress();
                    }
                }
                else
                {
                    for (int i = 0; i < textTokenFirst; ++i)
                    {
                        line.words[i].supress();
                    }
                    for (int i = textTokenLast + 1; i < line.words.length; ++i)
                    {
                        line.words[i].supress();
                    }

                    if (numTextTokens == 1)
                    {
                        WordToken token = line.words[textTokenFirst];
                        int numLetters = token.glyphs.length;

                        if (numLetters == 1)
                        {
                            //One word only one letter long.  Just write it at left margin
                            token.glyphs[0].offset = cursor;
                            token.glyphs[0].advance = wrapWidth;
                        }
                        else
                        {
                            //Spread letters of word across justify gap
                            float justifyGap = (wrapWidth - token.width) / (numLetters - 1);
                            for (int i = 0; i < numLetters; ++i)
                            {
                                token.glyphs[i].offset = cursor;
                                token.glyphs[i].advance += justifyGap;

                                cursor += token.glyphs[i].advance;
                            }
                        }
                    }
                    else
                    {
                        //Spread words evenly across wrapWidth
                        float justifyGap = (wrapWidth - textWidth) / (numTextTokens - 1);
                        //for (MeasuredToken token: line)
                        for (int i = textTokenFirst; i <= textTokenLast; ++i)
                        {
                            WordToken token = line.words[i];
                            if (token.type == Type.TEXT)
                            {
                                token.setOffset(cursor);
                                cursor += token.width;
                            }
                            else
                            {
                                token.setOffset(cursor);
                                token.distribute(justifyGap);
                                cursor += justifyGap;
                            }
                        }
                    }
                }
            }
            else
            {
                for (int i = 0; i < line.words.length; ++i)
                {
                    WordToken token = line.words[i];
                    token.setOffset(cursor);
                    cursor += token.width;
                }
            }
        }

        return lineSet;
    }

    public LineSetToken layout(String text, float wrapWidth, Justify justify)
    {
        ArrayList<WordToken> tokens = tokenize(text);
        LineSetToken lineSet = splitIntoLines(tokens, wrapWidth);

        //If we used no wrap width for splitting lines, calculate
        // min width for the layout step
        if (wrapWidth == 0)
        {
            int maxWidth = 0;
            for (LineToken line: lineSet.lines)
            {
                int lineWidth = 0;
                for (WordToken token: line.words)
                {
                    lineWidth += token.width;
                }
                maxWidth = Math.max(maxWidth, lineWidth);
            }
            wrapWidth = maxWidth;
        }

        layout(lineSet, wrapWidth, justify);

        lineSet.buildGlyphCursorBounds();
//        for (LineToken line: lineSet.lines)
//        {
//            line.buildGlyphCursorBounds();
//        }
        return lineSet;
    }


    //-----------------------------------

    public class LineSetToken
    {
        LineToken[] lines;
        private Rectangle2D.Double endCursorBounds;

        public LineSetToken(LineToken[] lines)
        {
            this.lines = lines;
        }

        private void buildGlyphCursorBounds()
        {
            for (LineToken line: lines)
            {
                line.buildGlyphCursorBounds();
            }

            if (lines.length == 0)
            {
                endCursorBounds = new Rectangle2D.Double(0, -getLineAscent(), 0, getLineHeight());
            }
            else
            {
                Rectangle2D.Double bounds =
                        getLastLine().getLastWord().getLastGlyph().getBounds();

                endCursorBounds = new Rectangle2D.Double(
                        bounds.x + bounds.width, bounds.y, 0, bounds.height);
            }
        }


        public LineToken getLastLine()
        {
            if (lines.length == 0)
            {
                return null;
            }
            return lines[lines.length - 1];
        }

        /**
         * @return the endCursorBounds
         */
        public Rectangle2D.Double getEndCursorBounds()
        {
            return endCursorBounds;
        }

        public void append(Double path)
        {
            for (LineToken line: lines)
            {
                line.append(path);
            }
        }

        public HashMap<Integer, GlyphToken> getGlyphIndexMap()
        {
            HashMap<Integer, GlyphToken> map = new HashMap<Integer, GlyphToken>();

            for (LineToken line: lines)
            {
                line.getGlyphIndexMap(map);
            }

            return map;
        }
    }

    public class LineToken
    {
        int index;
        WordToken[] words;
        float width;

        public LineToken(int index, WordToken[] glyphs)
        {
            this.index = index;
            this.words = glyphs;
            buildWidth();
        }

        public WordToken getLastWord()
        {
            return words[words.length - 1];
        }

        public boolean containsText()
        {
            for (int i = 0; i < words.length; ++i)
            {
                if (words[i].type == Type.TEXT)
                {
                    return true;
                }
            }
            return false;
        }

        public void supressTrailingSpace()
        {
            if (!containsText())
            {
                //Do not supress if only space
                return;
            }

            WordToken lastWord = words[words.length - 1];
            if (words.length > 1 && lastWord.type == Type.SPACE)
            {
                lastWord.supress();
            }
            buildWidth();
        }

        private void buildWidth()
        {
            width = 0;
            for (int i = 0; i < words.length; ++i)
            {
                if (!words[i].supressWidth)
                {
                    width += words[i].width;
                }
            }
        }

        public WordToken[] getWords()
        {
            return words.clone();
        }

        public void append(Path2D.Double textShape)
        {
            for (WordToken word: words)
            {
                word.append(textShape, getLineHeight() * index);
            }
        }

        void getGlyphIndexMap(HashMap<Integer, GlyphToken> map)
        {
            for (WordToken word: words)
            {
                word.getGlyphIndexMap(map);
            }
        }

        private void buildGlyphCursorBounds()
        {
            for (WordToken word: words)
            {
                word.buildGlyphCursorBounds(index);
            }
        }
    }

    public class WordToken
    {
        GlyphToken[] glyphs;
        Type type;

        float width;
        boolean supressWidth;

        public WordToken(GlyphToken[] glyphs, Type type)
        {
            this.glyphs = glyphs;
            this.type = type;

            for (int i = 0; i < glyphs.length; ++i)
            {
                width += glyphs[i].advance;
            }
        }

        public GlyphToken getLastGlyph()
        {
            return glyphs[glyphs.length - 1];
        }

        public GlyphToken[] getGlyphs()
        {
            return glyphs.clone();
        }

        private void supress()
        {
            width = 0;
            supressWidth = true;
            for (GlyphToken glyph: glyphs)
            {
                glyph.supress();
            }
        }

        private void setOffset(float cursor)
        {
            for (int i = 0; i < glyphs.length; ++i)
            {
                glyphs[i].offset = cursor;
                cursor += glyphs[i].advance;
            }
        }

        private void distribute(float justifyGap)
        {
            float delta = justifyGap / glyphs.length;
            for (int i = 0; i < glyphs.length; ++i)
            {
                glyphs[i].advance = delta;
            }

            //Align all glyph offsets to end of previous glyph
            setOffset(glyphs[0].offset);
        }

        private void append(Path2D.Double textShape, int ypos)
        {
            if (type != Type.TEXT)
            {
                return;
            }

            AffineTransform xform = new AffineTransform();
            for (int i = 0; i < glyphs.length; ++i)
            {
                GlyphToken token = glyphs[i];
                xform.setToTranslation(token.offset, ypos);
                Shape glyphShape = xform.createTransformedShape(token.info.shape);
                textShape.append(glyphShape, false);
            }
        }

        private void getGlyphIndexMap(HashMap<Integer, GlyphToken> map)
        {
            for (int i = 0; i < glyphs.length; ++i)
            {
                GlyphToken token = glyphs[i];
                map.put(token.index, token);
            }
        }

        private void buildGlyphCursorBounds(int lineIndex)
        {
            for (int i = 0; i < glyphs.length; ++i)
            {
                GlyphToken token = glyphs[i];
                token.buildGlyphCursorBounds(lineIndex);
            }
        }
    }

    public class GlyphToken
    {
        int index;
        GlyphInfo info;
        //char ch;
        Type type;
        float advance;
        float offset;
        private Rectangle2D.Double bounds;

        public GlyphToken(int index, GlyphInfo info, Type type)
        {
            this.index = index;
            this.info = info;
            this.type = type;
            this.advance = info.advance;
        }

        private void supress()
        {
            advance = 0;
        }

        private void buildGlyphCursorBounds(int lineIndex)
        {
            bounds = new Rectangle2D.Double(
                    offset, lineIndex * getLineHeight() - getLineAscent(),
                    advance, getLineHeight());
        }

        /**
         * @return the bounds
         */
        public Rectangle2D.Double getBounds()
        {
            return bounds;
        }

    }

    public class GlyphInfo
    {
        final char ch;
        final Shape shape;
        final float advance;

        public GlyphInfo(char ch, Shape shape, float advance)
        {
            this.ch = ch;
            this.shape = shape;
            this.advance = advance;
        }


    }

    public static enum Type { TEXT, SPACE, NEWLINE };

}
