// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   HotJavaBeanApplet.java

package sunw.hotjava.bean.applets;

import java.applet.Applet;
import java.awt.Component;
import java.awt.Frame;

public class HotJavaBeanApplet extends Applet
{

    protected Frame getEnclosingFrame()
    {
        java.awt.Container container;
        for(container = getParent(); container != null && !(container instanceof Frame); container = container.getParent());
        return (Frame)container;
    }

    protected String getParameter(String s, String s1)
    {
        String s2 = getParameter(s);
        if(s2 != null)
            return s2;
        else
            return s1;
    }

    public HotJavaBeanApplet()
    {
    }
}
