// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   LinkEvent.java

package sunw.hotjava.bean;

import java.awt.Event;

// Referenced classes of package sunw.hotjava.bean:
//            HTMLBrowsable

public class LinkEvent extends Event
{

    public LinkEvent(String s, String s1, String s2)
    {
        super(s, 0, s2);
        frameSource = s;
        target = s1;
        href = s2;
    }

    public LinkEvent(String s, String s1, String s2, HTMLBrowsable htmlbrowsable)
    {
        this(s, s1, s2);
        browser = htmlbrowsable;
    }

    public String getFrameSource()
    {
        return frameSource;
    }

    public String getTarget()
    {
        return target;
    }

    public String getHREF()
    {
        return href;
    }

    public HTMLBrowsable getBrowser()
    {
        return browser;
    }

    private String frameSource;
    private String target;
    private String href;
    private HTMLBrowsable browser;
}
