// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ContentViewerUnknownApplet.java

package sunw.hotjava.bean.applets;

import java.applet.Applet;
import java.applet.AppletContext;
import java.awt.*;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import sunw.hotjava.misc.Globals;
import sunw.hotjava.misc.HJBProperties;
import sunw.hotjava.ui.UserTextButton;

// Referenced classes of package sunw.hotjava.bean.applets:
//            HotJavaBeanApplet

public class ContentViewerUnknownApplet extends HotJavaBeanApplet
{

    public ContentViewerUnknownApplet()
    {
        setName("contentviewerunknown");
    }

    public void init()
    {
        super.init();
        setLayout(new FlowLayout());
        try
        {
            url = new URL(getParameter("url"));
        }
        catch(MalformedURLException _ex)
        {
            System.err.println("saveFile: bad url " + getParameter("url"));
        }
        add(view = new UserTextButton("viewerunknown.view", HJBProperties.getHJBProperties("beanPropertiesKey")));
    }

    public boolean action(Event event, Object obj)
    {
        String s = (String)obj;
        if(s.equals(view.getName()))
            return viewInBrowser();
        else
            return false;
    }

    boolean viewInBrowser()
    {
        URL url1 = null;
        try
        {
            url1 = new URL(getParameter("url"));
        }
        catch(MalformedURLException malformedurlexception)
        {
            System.err.println("ContentViewerUnknownApplet bad URL: " + malformedurlexception);
        }
        try
        {
            URL url2 = new URL("verbatim:" + url1.toExternalForm());
            getAppletContext().showDocument(url2);
        }
        catch(MalformedURLException malformedurlexception1)
        {
            System.err.println("ContentViewerUnknownApplet view as-is exception: " + malformedurlexception1);
        }
        return true;
    }

    URL url;
    UserTextButton view;
}
