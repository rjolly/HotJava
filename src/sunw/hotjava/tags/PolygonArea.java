// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ImageMap.java

package sunw.hotjava.tags;

import java.awt.Polygon;
import java.net.URL;
import java.util.StringTokenizer;
import sunw.hotjava.doc.TagItem;

// Referenced classes of package sunw.hotjava.tags:
//            ImageArea, CircleArea, ImageMap, RectArea

class PolygonArea extends ImageArea
{

    PolygonArea(String s, URL url, boolean flag, String s1, String s2, TagItem tagitem)
    {
        super(url, s1, flag, s, s2, tagitem);
    }

    void updateCoords(int i, int j)
    {
        super.st = new StringTokenizer(super.coords, ", \t");
        poly = new Polygon();
        int k;
        int l;
        for(; super.st.hasMoreTokens(); poly.addPoint(k, l))
        {
            k = computeCoord(i);
            l = computeCoord(j);
        }

    }

    boolean inside(int i, int j)
    {
        return poly.inside(i, j);
    }

    public String toString()
    {
        return "[PolygonArea pts=" + poly.npoints + "]";
    }

    Polygon poly;
}
