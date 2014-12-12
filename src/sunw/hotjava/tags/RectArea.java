// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ImageMap.java

package sunw.hotjava.tags;

import java.net.URL;
import java.util.StringTokenizer;
import sunw.hotjava.doc.TagItem;

// Referenced classes of package sunw.hotjava.tags:
//            ImageArea, CircleArea, ImageMap, PolygonArea

class RectArea extends ImageArea
{

    RectArea(String s, URL url, boolean flag, String s1, String s2, TagItem tagitem)
    {
        super(url, s1, flag, s, s2, tagitem);
    }

    void updateCoords(int i, int j)
    {
        super.st = new StringTokenizer(super.coords, ", \t");
        left = computeCoord(i);
        top = computeCoord(j);
        right = computeCoord(i);
        bottom = computeCoord(j);
    }

    boolean inside(int i, int j)
    {
        return i >= left && i <= right && j >= top && j <= bottom;
    }

    public String toString()
    {
        return "[RectArea " + left + "," + top + "," + bottom + "," + right + "]";
    }

    int left;
    int right;
    int top;
    int bottom;
}
