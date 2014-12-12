// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ElementInfo.java

package sunw.hotjava.doc;

import java.awt.event.MouseEvent;

public class ElementInfo
    implements Cloneable
{

    public Object clone()
    {
        ElementInfo elementinfo = new ElementInfo();
        elementinfo.imageURL = imageURL;
        elementinfo.hrefURL = hrefURL;
        elementinfo.altText = altText;
        elementinfo.event = event;
        elementinfo.setStatus = setStatus;
        elementinfo.eiID = eiID;
        elementinfo.frameURL = frameURL;
        return elementinfo;
    }

    public String toString()
    {
        return "[Element " + eiID + " status = " + setStatus + "]";
    }

    public ElementInfo()
    {
        setStatus = true;
        eiID = numInfos++;
    }

    private static int numInfos;
    public String imageURL;
    public String hrefURL;
    public String altText;
    public String frameURL;
    public MouseEvent event;
    public boolean setStatus;
    public int eiID;
}
