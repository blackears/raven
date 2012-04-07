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

import java.util.Arrays;

/**
 *
 * @author kitfox
 */
public class Grid<T>
{
    Object[] data = new Object[0];
    private int width;
    private int height;
    private int offsetX;
    private int offsetY;

    public Grid()
    {
    }

    public Grid(Grid<T> grid)
    {
        this(grid.offsetX, grid.offsetY, grid.width, grid.height, grid.data.clone());
    }

    public Grid(int offsetX, int offsetY, int width, int height, Object[] data)
    {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.width = width;
        this.height = height;
        this.data = data;
    }

    public Grid(int offsetX, int offsetY, int width, int height, T defaultValue)
    {
        resize(offsetX, offsetY, width, height, defaultValue);
    }

    public void set(Grid<T> grid)
    {
        set(grid.offsetX, grid.offsetY, grid.width, grid.height, (T[])grid.data.clone());
    }

    public void set(int offsetX, int offsetY, int gridWidth, int gridHeight, T[] gridData)
    {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.width = gridWidth;
        this.height = gridHeight;
        this.data = gridData;
    }

    public void resize(int x, int y, int w, int h, T defaultValue)
    {
        Object[] arr = new Object[w * h];
        Arrays.fill(arr, defaultValue);

        for (int j = 0; j < getHeight(); ++j)
        {
            int dy = j + offsetY - y;
            if (dy < 0 || dy >= h)
            {
                continue;
            }

            for (int i = 0; i < getWidth(); ++i)
            {
                int dx = i + offsetX - x;
                if (dx < 0 || dx >= w)
                {
                    continue;
                }

                arr[dx + dy * w] = data[i + j * getWidth()];
            }
        }

        data = arr;
        offsetX = x;
        offsetY = y;
        width = w;
        height = h;
    }

    public T getValue(int x, int y)
    {
        x -= offsetX;
        y -= offsetY;
        if (x < 0 || x >= width || y < 0 || y >= height)
        {
            return null;
        }
        return (T)data[x + y * width];
    }

    public void setValue(int x, int y, T value)
    {
        x -= offsetX;
        y -= offsetY;
        if (x < 0 || x >= width || y < 0 || y >= height)
        {
            return;
        }
        data[x + y * width] = value;
    }

    /**
     * @return the width
     */
    public int getWidth()
    {
        return width;
    }

    /**
     * @return the height
     */
    public int getHeight()
    {
        return height;
    }

    /**
     * @return the width
     */
    public int getOffsetX()
    {
        return offsetX;
    }

    /**
     * @return the height
     */
    public int getOffsetY()
    {
        return offsetY;
    }

    public Object[] getData()
    {
        return data.clone();
    }

    public T[] getData(T[] arr)
    {
        System.arraycopy(data, 0, arr, 0, data.length);
        return arr;
    }

    public int getSize()
    {
        return data.length;
    }

    public void crop(T spaceValue)
    {
        int minRow = 0;
        int minCol = 0;
        int maxRow = height - 1;
        int maxCol = width - 1;

        while (minRow < height && isBlankRow(minRow, spaceValue))
        {
            ++minRow;
        }
        while (maxRow >= 0 && isBlankRow(maxRow, spaceValue))
        {
            --maxRow;
        }
        while (minCol < width && isBlankCol(minCol, spaceValue))
        {
            ++minCol;
        }
        while (maxCol >= 0 && isBlankCol(maxCol, spaceValue))
        {
            --maxCol;
        }

        if (maxRow < minRow)
        {
            width = height = 0;
            data = new Object[0];
            return;
        }

        resize(minCol, minRow, maxCol - minCol + 1, maxRow - minRow + 1, spaceValue);
    }

    public boolean isBlankRow(int row, T value)
    {
        for (int i = 0; i < width; ++i)
        {
            if (getValue(i, row) != value)
            {
                return false;
            }
        }
        return true;
    }

    public boolean isBlankCol(int col, T value)
    {
        for (int i = 0; i < height; ++i)
        {
            if (getValue(col, i) != value)
            {
                return false;
            }
        }
        return true;
    }

    public void includeRegion(int x, int y, int w, int h, T value)
    {
        if (x >= offsetX && y >= offsetY
                && x + w <= offsetX + width
                && y + h <= offsetY + height)
        {
            //Region already included
            return;
        }

        if (width == 0 || height == 0)
        {
            resize(x, y, w, h, value);
            return;
        }

        int minX = Math.min(x, offsetX);
        int minY = Math.min(y, offsetY);
        int maxX = Math.max(x + w - 1, offsetX + width - 1);
        int maxY = Math.max(y + h - 1, offsetY + height - 1);

        resize(minX, minY, maxX - minX + 1, maxY - minY + 1, value);
    }

    public boolean isEmpty()
    {
        return width == 0 || height == 0;
    }

    public void clear()
    {
        data = new Object[0];
        offsetX = offsetY = width = height = 0;
    }

}
