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

import java.awt.event.KeyEvent;
import java.util.ArrayList;

/**
 *
 * @author kitfox
 */
public class TextDocumentEditor
{
    int cursorCol; //-1 indicates end-of-line
    int cursorLine;

    ArrayList<TextLine> lines = new ArrayList<TextLine>();

    public TextDocumentEditor()
    {
        //Start with one empty line
        lines.add(new TextLine());
    }

    public TextDocumentEditor(String text)
    {
        text = text.replaceAll("\\r", "");

        String[] textLines = text.split("\\n");
        for (String textVal: textLines)
        {
            lines.add(new TextLine(textVal));
        }
    }

    public void process(KeyEvent evt)
    {
        int mod = evt.getModifiersEx();
        boolean ctrl = (mod & KeyEvent.CTRL_DOWN_MASK) != 0;

        TextLine curLine = lines.get(cursorLine);

        switch (evt.getKeyCode())
        {
            case KeyEvent.VK_SHIFT:
            case KeyEvent.VK_ALT:
            case KeyEvent.VK_CONTROL:
//            case KeyEvent.VK_TAB:
//            case KeyEvent.VK_CANCEL:
//            case KeyEvent.VK_CLEAR:
//            case KeyEvent.VK_PAUSE:
//            case KeyEvent.VK_CAPS_LOCK:
//            case KeyEvent.VK_ESCAPE:
//            case KeyEvent.VK_PAGE_UP:
//            case KeyEvent.VK_PAGE_DOWN:
//            case KeyEvent.VK_NUM_LOCK:
//            case KeyEvent.VK_SCROLL_LOCK:
//            case KeyEvent.VK_F1:
//            case KeyEvent.VK_F2:
//            case KeyEvent.VK_F3:
//            case KeyEvent.VK_F4:
//            case KeyEvent.VK_F5:
//            case KeyEvent.VK_F6:
//            case KeyEvent.VK_F7:
//            case KeyEvent.VK_F8:
//            case KeyEvent.VK_F9:
//            case KeyEvent.VK_F10:
//            case KeyEvent.VK_F11:
//            case KeyEvent.VK_F12:
//            case KeyEvent.VK_F13:
//            case KeyEvent.VK_F14:
//            case KeyEvent.VK_F15:
//            case KeyEvent.VK_F16:
//            case KeyEvent.VK_F17:
//            case KeyEvent.VK_F18:
//            case KeyEvent.VK_F19:
//            case KeyEvent.VK_F20:
//            case KeyEvent.VK_F21:
//            case KeyEvent.VK_F22:
//            case KeyEvent.VK_F23:
//            case KeyEvent.VK_F24:
//            case KeyEvent.VK_PRINTSCREEN:
//            case KeyEvent.VK_HELP:
//            case KeyEvent.VK_META:
//            case KeyEvent.VK_KP_UP:
//            case KeyEvent.VK_KP_DOWN:
//            case KeyEvent.VK_KP_LEFT:
//            case KeyEvent.VK_KP_RIGHT:
//            case KeyEvent.VK_WINDOWS:
//            case KeyEvent.VK_CONTEXT_MENU:
//            case KeyEvent.VK_CONVERT:
//            case KeyEvent.VK_NONCONVERT:
//            case KeyEvent.VK_ACCEPT:
//            case KeyEvent.VK_MODECHANGE:
//            case KeyEvent.VK_KANA:
//            case KeyEvent.VK_KANJI:
                break;

            case KeyEvent.VK_END:
                cursorCol = -1;
                if (ctrl)
                {
                    cursorLine = lines.size() - 1;
                }
                break;
            case KeyEvent.VK_HOME:
                cursorCol = 0;
                if (ctrl)
                {
                    cursorLine = 0;
                }
                break;
            case KeyEvent.VK_BACK_SPACE:
            {
                if (cursorCol == 0)
                {
                    if (cursorLine != 0)
                    {
                        TextLine prevLine = lines.get(cursorLine - 1);
                        lines.remove(cursorLine);
                        cursorCol = prevLine.chars.size();
                        prevLine.chars.addAll(curLine.chars);
                        --cursorLine;
                    }
                }
                else
                {
                    cursorCol = Math.min(cursorCol, curLine.chars.size());
                    curLine.chars.remove(cursorCol - 1);
                    --cursorCol;
                }
                break;
            }
            case KeyEvent.VK_DELETE:
            {
                if (cursorCol == -1 || cursorCol >= curLine.chars.size())
                {
                    //Concatenate with following line
                    if (cursorLine + 1 != lines.size())
                    {
                        cursorCol = curLine.chars.size();
                        TextLine nextLine = lines.remove(cursorLine + 1);
                        curLine.chars.addAll(nextLine.chars);
                    }
                }
                else
                {
                    curLine.chars.remove(cursorCol);
                }
                break;
            }
            case KeyEvent.VK_LEFT:
                if (cursorCol == 0)
                {
                    if (cursorLine != 0)
                    {
                        --cursorLine;
                        TextLine prevLine = lines.get(cursorLine);
                        cursorCol = prevLine.chars.size();
                    }
                }
                else
                {
                    cursorCol = Math.min(cursorCol, curLine.chars.size());
                    --cursorCol;
                }
                break;
            case KeyEvent.VK_RIGHT:
            {
                if (cursorCol == -1 || cursorCol >= curLine.chars.size())
                {
                    //Concatenate with following line
                    if (cursorLine + 1 != lines.size())
                    {
                        ++cursorLine;
                        cursorCol = 0;
                    }
                }
                else
                {
                    ++cursorCol;
                }
                break;
            }
            case KeyEvent.VK_UP:
                if (cursorLine != 0)
                {
                    --cursorLine;
                }
                break;
            case KeyEvent.VK_DOWN:
                if (cursorLine != lines.size() - 1)
                {
                    ++cursorLine;
                }
                break;
            case KeyEvent.VK_ENTER:
                if (cursorCol == -1 || cursorCol >= curLine.chars.size())
                {
                    lines.add(cursorLine + 1, new TextLine());
                }
                else
                {
                    TextLine nextLine = new TextLine();
                    lines.add(cursorLine + 1, nextLine);

                    for (int i = cursorCol; i < curLine.chars.size(); ++i)
                    {
                        nextLine.chars.add(curLine.chars.get(i));
                    }

                    ArrayList<Character> split = new ArrayList<Character>();
                    for (int i = 0; i < cursorCol; ++i)
                    {
                        split.add(curLine.chars.get(i));
                    }
                    curLine.chars = split;
                }
                ++cursorLine;
                cursorCol = 0;
                break;
            default:
                cursorCol = Math.min(cursorCol, curLine.chars.size());
                curLine.chars.add(cursorCol, evt.getKeyChar());
                ++cursorCol;
                break;
        }

    }

    @Override
    public String toString()
    {
        StringBuilder sb = null;
        for (TextLine line: lines)
        {
            if (sb == null)
            {
                sb = new StringBuilder();
            }
            else
            {
                sb.append('\n');
            }
            sb.append(line.toString());
        }
        return sb == null ? null : sb.toString();
    }

    public int getCursorTextOffset()
    {
        int offset = cursorCol == -1
                ? lines.get(cursorLine).chars.size()
                : cursorCol;
        for (int i = 0; i < cursorLine; ++i)
        {
            offset += lines.get(i).chars.size() + 1;
        }
        return offset;
    }

    public void setCursorTextOffset(int index)
    {
        for (int i = 0; i < lines.size(); ++i)
        {
            TextLine line = lines.get(i);
            if (index <= line.chars.size())
            {
                cursorCol = index;
                cursorLine = i;
                return;
            }

            index -= line.chars.size() + 1;
        }
        
    }

    //-------------------------------------
    class TextLine
    {
        ArrayList<Character> chars = new ArrayList<Character>();

        public TextLine()
        {
        }

        private TextLine(String textVal)
        {
            for (int i = 0; i < textVal.length(); ++i)
            {
                chars.add(textVal.charAt(i));
            }
        }

        @Override
        public String toString()
        {
            char[] list = new char[chars.size()];
            for (int i = 0; i < chars.size(); ++i)
            {
                list[i] = chars.get(i);
            }
            return new String(list);
        }


    }


}
