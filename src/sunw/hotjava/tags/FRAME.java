// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   FRAME.java

package sunw.hotjava.tags;

import java.net.MalformedURLException;
import java.net.URL;
import sunw.hotjava.doc.*;
import sunw.html.Attributes;

public class FRAME extends EmptyTagItem
{

    public void init(Document document)
    {
        byReloaded = document.wasReloaded();
        if(super.atts != null)
        {
            String s = super.atts.get("src");
            if(s != null && !s.equals(""))
                try
                {
                    URL url = (URL)document.getProperty("base");
                    if(url != null)
                        src = new URL(url, s);
                    else
                        src = new URL(document.getURL(), s);
                }
                catch(MalformedURLException _ex)
                {
                    try
                    {
                        src = new URL("doc://unknown.protocol/" + s);
                    }
                    catch(MalformedURLException _ex2) { }
                }
        }
        frameIndex = document.addFrame(this);
    }

    public boolean format(Formatter formatter, FormatState formatstate, FormatState formatstate1)
    {
        formatstate.pos += 4096;
        return false;
    }

    protected boolean isByReloaded()
    {
        return byReloaded;
    }

    public URL getSrc()
    {
        return src;
    }

    public Document getSourceDocument()
    {
        return DocumentCache.getDocument(src);
    }

    public int getFrameIndex()
    {
        return frameIndex;
    }

    public FRAME()
    {
        byReloaded = false;
    }

    URL src;
    boolean byReloaded;
    private int frameIndex;
}
