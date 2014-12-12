// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   NamedLink.java

package sunw.hotjava.doc;

import java.net.URL;
import sunw.hotjava.misc.URLConnector;

// Referenced classes of package sunw.hotjava.doc:
//            TagItem

public class NamedLink
{

    public NamedLink(String s, URL url1)
    {
        this(s, url1, null, null);
    }

    public NamedLink(String s, URL url1, URL url2)
    {
        this(s, url1, url2, null);
    }

    public NamedLink(String s, URL url1, URL url2, TagItem tagitem)
    {
        isJS = false;
        name = s;
        url = url1;
        referer = url2;
        tag = tagitem;
        if(url1 != null && url1.toExternalForm().toLowerCase().startsWith("doc://unknown.protocol/javascript:"))
            isJS = true;
    }

    public String toString()
    {
        return getClass().getName() + "[name=" + name + ",url=" + url + ",referer=" + referer + ",tag=" + tag + "]";
    }

    public String name;
    public URL url;
    public URL referer;
    public URLConnector connector;
    public boolean isJS;
    public TagItem tag;
}
