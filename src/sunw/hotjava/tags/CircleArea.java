// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ImageMap.java

package sunw.hotjava.tags;

import java.net.URL;
import java.util.StringTokenizer;
import sunw.hotjava.doc.TagItem;

// Referenced classes of package sunw.hotjava.tags:
//            ImageArea, ImageMap, PolygonArea, RectArea

class CircleArea extends ImageArea
{

    CircleArea(String s, URL url, boolean flag, String s1, String s2, TagItem tagitem)
    {
        super(url, s1, flag, s, s2, tagitem);
        centerX = -1;
        centerY = -1;
    }

    void updateCoords(int i, int j)
    {
        super.st = new StringTokenizer(super.coords, ", \t");
        centerX = computeCoord(i);
        centerY = computeCoord(j);
        radius = computeCoord(i >= j ? j : i);
    }

    boolean inside(int i, int j)
    {
        int k = i - centerX;
        int l = j - centerY;
        return k * k + l * l <= radius * radius;
    }

    public String toString()
    {
        return "[CircleArea " + centerX + "@" + centerY + ":" + radius + "]";
    }

    int centerX;
    int centerY;
    int radius;
}
