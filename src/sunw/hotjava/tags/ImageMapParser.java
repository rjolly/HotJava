// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ImageMapParser.java

package sunw.hotjava.tags;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Hashtable;
import sunw.hotjava.doc.TagItem;
import sunw.html.*;

// Referenced classes of package sunw.hotjava.tags:
//            ImageMap

public class ImageMapParser extends Parser
    implements Runnable
{

    ImageMapParser(URL url, Hashtable hashtable)
    {
        docURL = url;
        maps = hashtable;
    }

    ImageMapParser(String s, Hashtable hashtable)
    {
        try
        {
            docURL = new URL(s);
        }
        catch(MalformedURLException _ex) { }
        maps = hashtable;
    }

    public void run()
    {
        Thread.currentThread().setName("ImageMapParser " + docURL);
        InputStreamReader inputstreamreader = null;
        try
        {
            java.io.InputStream inputstream = docURL.openStream();
            inputstreamreader = new InputStreamReader(inputstream);
            parse(inputstreamreader, DTD.getDTD("html32"));
        }
        catch(IOException _ex) { }
        finally
        {
            if(inputstreamreader != null)
                try
                {
                    inputstreamreader.close();
                }
                catch(IOException _ex) { }
        }
    }

    protected void handleStartTag(Tag tag)
    {
        Element element = tag.getElement();
        Attributes attributes = tag.getAttributes();
        if("map".equals(element.getName()))
        {
            String s = attributes.get("name");
            try
            {
                URL url = new URL(docURL, "#" + s);
                imap = new ImageMap(url);
                maps.put(url, imap);
                return;
            }
            catch(MalformedURLException _ex)
            {
                imap = null;
            }
            return;
        } else
        {
            return;
        }
    }

    protected void handleEndTag(Tag tag)
    {
        Element element = tag.getElement();
        if("map".equals(element.getName()))
            imap = null;
    }

    protected void handleEmptyTag(Tag tag)
    {
        Element element = tag.getElement();
        Attributes attributes = tag.getAttributes();
        if("area".equals(element.getName()) && imap != null)
        {
            String s = attributes.get("shape");
            String s1 = attributes.get("coords");
            String s2 = attributes.get("href");
            String s3 = attributes.get("nohref");
            String s4 = attributes.get("alt");
            String s5 = attributes.get("target");
            imap.addArea(docURL, s, s1, s2, s3 != null, s4, s5, (TagItem)tag);
        }
    }

    URL docURL;
    ImageMap imap;
    private Hashtable maps;
}
