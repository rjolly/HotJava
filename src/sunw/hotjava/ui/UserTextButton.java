// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   UserTextButton.java

package sunw.hotjava.ui;

import java.awt.*;
import sunw.hotjava.misc.HJBProperties;

public class UserTextButton extends Button
{

    public UserTextButton(String s, HJBProperties hjbproperties)
    {
        props = hjbproperties;
        setName(s);
        java.awt.Font font = hjbproperties.getFont("button." + s + ".font");
        if(font != null)
            setFont(font);
        java.awt.Color color = hjbproperties.getColor("button." + s + ".background", null);
        if(color != null)
            setBackground(color);
        color = hjbproperties.getColor("button." + s + ".foreground", null);
        if(color != null)
            setForeground(color);
        setUserLabel(s);
    }

    public void setUserLabel(String s)
    {
        String s1 = "button." + s + ".text";
        String s2 = props.getProperty(s1);
        if(s2 != null)
        {
            setLabel(s2);
            return;
        } else
        {
            setLabel(s1);
            return;
        }
    }

    public boolean handleEvent(Event event)
    {
        if(event.id == 1001)
        {
            java.awt.Container container = getParent();
            event.arg = getName();
            container.postEvent(event);
            return true;
        } else
        {
            return false;
        }
    }

    HJBProperties props;
}
