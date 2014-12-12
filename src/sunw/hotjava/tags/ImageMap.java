// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ImageMap.java

package sunw.hotjava.tags;

import java.net.URL;
import java.util.Vector;
import sunw.hotjava.doc.NamedLink;
import sunw.hotjava.doc.TagItem;

// Referenced classes of package sunw.hotjava.tags:
//            CircleArea, ImageArea, PolygonArea, RectArea

public class ImageMap
{

    public ImageMap(URL url1)
    {
        url = url1;
        areas = new Vector(2);
    }

    public NamedLink map(int i, int j)
    {
        for(int k = 0; k < areas.size(); k++)
        {
            ImageArea imagearea = (ImageArea)areas.elementAt(k);
            if(imagearea.inside(i, j))
                return imagearea.getLink(i, j);
        }

        return null;
    }

    public String getMapAltText(int i, int j)
    {
        for(int k = 0; k < areas.size(); k++)
        {
            ImageArea imagearea = (ImageArea)areas.elementAt(k);
            if(imagearea.inside(i, j))
                return imagearea.alt;
        }

        return null;
    }

    public void updateAreaCoords(int i, int j)
    {
        for(int k = 0; k < areas.size(); k++)
        {
            ImageArea imagearea = (ImageArea)areas.elementAt(k);
            imagearea.updateCoords(i, j);
        }

    }

    public void addArea(URL url1, String s, String s1, String s2, boolean flag, boolean flag1, String s3, 
            String s4, TagItem tagitem)
    {
        ImageArea imagearea = ImageArea.create(url1, s, s1, s2, flag, flag1, s3, s4, tagitem);
        if(imagearea != null)
            areas.addElement(imagearea);
    }

    public void addArea(URL url1, String s, String s1, String s2, boolean flag, String s3, String s4, 
            TagItem tagitem)
    {
        addArea(url1, s, s1, s2, flag, false, s3, s4, tagitem);
    }

    public String toString()
    {
        String s = "";
        for(int i = 0; i < areas.size(); i++)
            s = s + areas.elementAt(i).toString();

        return "<ImageMap " + url + " " + s + ">";
    }

    URL url;
    Vector areas;
}
