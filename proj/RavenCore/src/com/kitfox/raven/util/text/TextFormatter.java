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

import java.util.ArrayList;

/**
 *
 * @author kitfox
 */
@Deprecated
abstract public class TextFormatter
{
    abstract protected float getWidth(String text);

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

    protected boolean isSkip(char ch)
    {
        return ch == '\r';
    }

    public ArrayList<WordPos> layout(String text, float wrapWidth, Justify justify)
    {
        ArrayList<TextFormatToken> tokens = tokenize(text);
        ArrayList<ArrayList<MeasuredToken>> lines = splitIntoLines(tokens, wrapWidth);

        //If we used no wrap width for splitting lines, calculate
        // min width for the layout step
        if (wrapWidth == 0)
        {
            int maxWidth = 0;
            for (ArrayList<MeasuredToken> line: lines)
            {
                int lineWidth = 0;
                for (MeasuredToken token: line)
                {
                    lineWidth += token.width;
                }
                maxWidth = Math.max(maxWidth, lineWidth);
            }
            wrapWidth = maxWidth;
        }

        return layout(lines, wrapWidth, justify);
    }

    private ArrayList<WordPos> layout(ArrayList<ArrayList<MeasuredToken>> lines,
            float wrapWidth,
            Justify justify)
    {
        ArrayList<WordPos> wordLayout = new ArrayList<WordPos>();

        for (int lineIdx = 0; lineIdx < lines.size(); ++lineIdx)
        {
            ArrayList<MeasuredToken> line = lines.get(lineIdx);

            //Calc component widths
            float totalWidth = 0;
            float textWidth = 0;
            int numTextTokens = 0;
            for (MeasuredToken token: line)
            {
                totalWidth += token.width;
                if (token.type == Type.TEXT)
                {
                    textWidth += token.width;
                    ++numTextTokens;
                }
            }

            if (numTextTokens == 0)
            {
                continue;
            }

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
                if (numTextTokens == 1)
                {
                    MeasuredToken token = line.get(0);
                    int numLetters = token.content.length();

                    if (numLetters == 1)
                    {
                        //One word only one letter long.  Just write it at left margin
                        wordLayout.add(new WordPos(cursor, token.width,
                                lineIdx, token.content, token.sourceIndex));
                    }
                    else
                    {
                        //Spread letters of word across justify gap
                        float justifyGap = (wrapWidth - token.width) / (numLetters - 1);
                        for (int i = 0; i < numLetters; ++i)
                        {
                            String word = "" + token.content.charAt(i);
                            float width = getWidth(word);

                            wordLayout.add(new WordPos(cursor, token.width,
                                    lineIdx, word, token.sourceIndex + i));
                            cursor += width + justifyGap;
                        }
                    }
                }
                else
                {
                    //Spread words evenly across wrapWidth
                    float justifyGap = (wrapWidth - textWidth) / (numTextTokens - 1);
                    for (MeasuredToken token: line)
                    {
                        if (token.type == Type.TEXT)
                        {
                            wordLayout.add(new WordPos(cursor, token.width,
                                    lineIdx, token.content, token.sourceIndex));
                            cursor += token.width + justifyGap;
                        }
                    }
                }
            }
            else
            {
                for (MeasuredToken token: line)
                {
                    wordLayout.add(new WordPos(cursor, token.width,
                            lineIdx, token.content, token.sourceIndex));
                    cursor += token.width;
                }
            }
        }

        return wordLayout;
    }

    /**
     * Splits tokenized text into (possibly) wrapped lines.
     *
     * @param tokens Tokens to split
     * @param wrapWidth Width to wrap lines.  If 0, lines are not wrapped
     * @param metrics Tools for measuring text layout size.
     * @return
     */
    private ArrayList<ArrayList<MeasuredToken>> splitIntoLines(ArrayList<TextFormatToken> tokens,
            float wrapWidth)
    {
        ArrayList<ArrayList<MeasuredToken>> lines =
                new ArrayList<ArrayList<MeasuredToken>>();

        ArrayList<MeasuredToken> line = new ArrayList<MeasuredToken>();
        float lineWidth = 0;
        for (int i = 0; i < tokens.size(); ++i)
        {
            TextFormatToken token = tokens.get(i);
            switch (token.type)
            {
                case TEXT:
                {
                    float width = getWidth(token.content);
                    if (wrapWidth != 0 
                            && lineWidth + width > wrapWidth
                            && !line.isEmpty())
                    {
                        lines.add(line);
                        line = new ArrayList<MeasuredToken>();
                        lineWidth = 0;
                    }
                    line.add(new MeasuredToken(token, width));
                    lineWidth += width;
                    break;
                }
                case SPACE:
                {
                    if (!line.isEmpty())
                    {
                        float width = getWidth(token.content);
                        line.add(new MeasuredToken(token, width));
                        lineWidth += width;
                    }
                    break;
                }
                case NEWLINE:
                {
                    lines.add(line);
                    line = new ArrayList<MeasuredToken>();
                    lineWidth = 0;
                    break;
                }
            }
        }
        lines.add(line);

        //Remove trailing whitespace
        for (ArrayList<MeasuredToken> curLine: lines)
        {
            while (!curLine.isEmpty()
                    && curLine.get(curLine.size() - 1).type == Type.SPACE)
            {
                curLine.remove(curLine.size() - 1);
            }
        }

        return lines;
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
    private ArrayList<TextFormatToken> tokenize(String text)
    {
        ArrayList<TextFormatToken> list = new ArrayList<TextFormatToken>();

        StringBuilder word = new StringBuilder();
        Type type = Type.TEXT;
        int indexLast = 0;
        for (int i = 0; i < text.length(); ++i)
        {
            char ch = text.charAt(i);
            if (isSkip(ch))
            {
                continue;
            }

            Type nextType = Type.TEXT;
            if (isWhitespace(ch))
            {
                nextType = Type.SPACE;
            }
            else if (isNewline(ch))
            {
                nextType = Type.NEWLINE;
            }

            if (nextType != type || nextType == Type.NEWLINE)
            {
                if (word.length() != 0)
                {
                    list.add(new TextFormatToken(type, word.toString(), indexLast));
                }
                indexLast = i;
                type = nextType;
                word = new StringBuilder();
            }
            word.append(ch);
        }
        
        if (word.length() != 0)
        {
            list.add(new TextFormatToken(type, word.toString(), indexLast));
        }

        return list;
    }

    //-----------------------------
    
    public static class WordPos
    {
        private final float cursorX;
        private final float width;
        private final int line;
        private final String text;
        private final int sourceIndex;

        public WordPos(float cursorX, float width, int line, String text, int sourceIndex)
        {
            this.cursorX = cursorX;
            this.width = width;
            this.line = line;
            this.text = text;
            this.sourceIndex = sourceIndex;
        }

        /**
         * @return the cursorX
         */
        public float getCursorX() {
            return cursorX;
        }

        /**
         * @return the line
         */
        public int getLine() {
            return line;
        }

        /**
         * @return the text
         */
        public String getText() {
            return text;
        }

        /**
         * @return the sourceIndex
         */
        public int getSourceIndex()
        {
            return sourceIndex;
        }

        /**
         * @return the width
         */
        public float getWidth()
        {
            return width;
        }
    }

    class MeasuredToken extends TextFormatToken
    {
        final float width;

//        public MeasuredToken(Type type, String content, int index, float width)
//        {
//            super(type, content, index);
//            this.width = width;
//        }

        public MeasuredToken(TextFormatToken token, float width)
        {
            super(token.type, token.content, token.sourceIndex);
            this.width = width;
        }
    }

    class TextFormatToken
    {
        final Type type;
        final String content;
        final int sourceIndex;

        public TextFormatToken(Type type, String content, int index)
        {
            this.type = type;
            this.content = content;
            this.sourceIndex = index;
        }
    }

    public static enum Type { TEXT, SPACE, NEWLINE };

}
