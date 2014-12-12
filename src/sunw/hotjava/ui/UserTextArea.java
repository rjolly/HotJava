// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   UserTextArea.java

package sunw.hotjava.ui;

import java.awt.Component;
import java.awt.TextArea;
import sunw.hotjava.misc.HJBProperties;

public class UserTextArea extends TextArea
{

    public UserTextArea(String s, HJBProperties hjbproperties)
    {
        super(hjbproperties.getInteger(s + ".rows", 24), hjbproperties.getInteger(s + ".columns", 80));
        setName(s);
        java.awt.Font font = hjbproperties.getFont(s + ".font");
        if(font != null)
            setFont(font);
        java.awt.Color color = hjbproperties.getColor(s + ".background", null);
        if(color != null)
            setBackground(color);
        color = hjbproperties.getColor(s + ".foreground", null);
        if(color != null)
            setForeground(color);
    }
}
