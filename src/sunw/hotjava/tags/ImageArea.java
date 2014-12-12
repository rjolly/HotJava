// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ImageMap.java

package sunw.hotjava.tags;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.StringTokenizer;
import sunw.hotjava.doc.NamedLink;
import sunw.hotjava.doc.TagItem;
import sunw.hotjava.misc.Length;
import sunw.html.Attributes;

// Referenced classes of package sunw.hotjava.tags:
//            CircleArea, ImageMap, PolygonArea, RectArea

abstract class ImageArea
{

    ImageArea(URL url, String s, boolean flag, String s1, TagItem tagitem)
    {
        this(url, s, flag, s1, null, tagitem);
    }

    ImageArea(URL url, String s, boolean flag, String s1, String s2, TagItem tagitem)
    {
        href = url;
        alt = s;
        target = s2;
        ismap = flag;
        coords = s1;
        areatag = tagitem;
    }

    static ImageArea create(URL url, String s, String s1, String s2, boolean flag, boolean flag1, String s3, String s4, 
            TagItem tagitem)
    {
        String s5 = flag ? null : s2;
        URL url1 = null;
        try
        {
            if(s5 != null)
                url1 = new URL(url, s5);
        }
        catch(MalformedURLException _ex)
        {
            try
            {
                url1 = new URL("doc://unknown.protocol/" + s2);
            }
            catch(MalformedURLException _ex2)
            {
                return null;
            }
        }
        if(s == null)
            s = "rect";
        if(s.startsWith("circ"))
            return new CircleArea(s1, url1, flag1, s3, s4, tagitem);
        if(s.startsWith("poly"))
            return new PolygonArea(s1, url1, flag1, s3, s4, tagitem);
        if(s1 == null)
            s1 = "0,0," + 0x7fffffff + "," + 0x7fffffff;
        return new RectArea(s1, url1, flag1, s3, s4, tagitem);
    }

    abstract boolean inside(int i, int j);

    abstract void updateCoords(int i, int j);

    protected int computeCoord(int i)
    {
        int j = -1;
        if(st.hasMoreTokens())
        {
            Length length = new Length(st.nextToken());
            if(length.isSet())
            {
                if(length.isPercentage())
                    j = (int)(((double)length.getValue() / 100D) * (double)i);
                else
                    j = length.getValue();
            } else
            {
                j = -1;
            }
        }
        return j;
    }

    URL getURL(int i, int j)
    {
        if(areatag != null)
        {
            String s = areatag.getAttributes().get("href");
            try
            {
                URL url = s == null ? null : new URL(s);
                if(url != null)
                    href = url;
            }
            catch(MalformedURLException _ex) { }
        }
        if(ismap)
            try
            {
                return new URL(href.toString() + "?" + i + "," + j);
            }
            catch(MalformedURLException _ex)
            {
                return null;
            }
        else
            return href;
    }

    NamedLink getLink(int i, int j)
    {
        URL url = getURL(i, j);
        if(url != null)
            return new NamedLink(target, url, null, areatag);
        else
            return null;
    }

    URL href;
    String alt;
    String target;
    boolean ismap;
    String coords;
    static final String delims = ", \t";
    StringTokenizer st;
    TagItem areatag;
}
