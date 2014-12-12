// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   pdf.java

package sunw.hotjava.content.application;

import java.net.*;
import sun.net.www.MimeEntry;
import sunw.hotjava.doc.Document;
import sunw.hotjava.misc.ContentViewersManager;
import sunw.html.Attributes;

public class pdf extends ContentHandler
{

    public Object getContent(URLConnection urlconnection)
    {
        try
        {
            Document document = new Document();
            int i = 0;
            i = document.insertTagPair(i, "html", null);
            i = document.insertTagPair(i, "body", null);
            Attributes attributes = new Attributes();
            attributes.put("code", "adobe.Acrobat.Viewer");
            ContentViewersManager contentviewersmanager = ContentViewersManager.getManager();
            MimeEntry mimeentry = contentviewersmanager.getContentViewer("application/pdf");
            URL url = new URL(mimeentry.getLaunchString());
            String s = url.getPort() > 0 ? url.getProtocol() + "://" + url.getHost() + ":" + url.getPort() : url.getProtocol() + "://" + url.getHost();
            attributes.put("codebase", s);
            attributes.put("archive", mimeentry.getLaunchString());
            attributes.put("width", "100%");
            attributes.put("height", "100%");
            i = document.insertTagPair(i, "applet", attributes);
            attributes = new Attributes();
            attributes.put("name", "SRC");
            attributes.put("value", urlconnection.getURL().toString());
            i = document.insertTag(i, "param", attributes);
            attributes = new Attributes();
            attributes.put("name", "DEBUG");
            attributes.put("value", "1");
            i = document.insertTag(i, "param", attributes);
            return document;
        }
        catch(Exception exception)
        {
            exception.printStackTrace();
        }
        return null;
    }

    public pdf()
    {
    }
}
