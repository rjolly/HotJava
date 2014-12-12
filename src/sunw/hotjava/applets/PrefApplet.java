// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   PrefApplet.java

package sunw.hotjava.applets;

import java.applet.Applet;
import java.applet.AppletContext;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import sunw.hotjava.misc.HJBProperties;

// Referenced classes of package sunw.hotjava.applets:
//            HotJavaApplet, PrefAppletManager

public abstract class PrefApplet extends HotJavaApplet
{

    public void start()
    {
        PrefAppletManager.registerApplet(this);
        updateFields();
    }

    public void stop()
    {
        PrefAppletManager.deregisterApplet(this);
    }

    protected void showError(String s, String s1)
    {
        String s2 = "prefs-error-" + s + ".html";
        s = properties.getProperty("template." + s2, "doc:/lib/templates/" + s2);
        getAppletContext().showStatus("");
        try
        {
            properties.put("badness", s1);
            getAppletContext().showDocument(new URL(s));
            return;
        }
        catch(MalformedURLException _ex)
        {
            System.out.println(properties.getProperty("hotjava.unable") + s);
        }
    }

    public abstract void updateFields();

    public abstract String getName();

    public abstract String[] getPropertyNames();

    public abstract void apply();

    public abstract void reset();

    public PrefApplet()
    {
    }

    public static HJBProperties properties = HJBProperties.getHJBProperties("hjbrowser");

}
