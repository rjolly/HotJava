// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   UserTextField.java

package sunw.hotjava.ui;

import java.awt.*;
import sunw.hotjava.misc.HJBProperties;

public class UserTextField extends TextField
{

    public UserTextField(String s)
    {
        super(HJBProperties.getHJBProperties("hjbrowser").getInteger(s + ".columns", 0));
        setName(s);
        HJBProperties hjbproperties = HJBProperties.getHJBProperties("hjbrowser");
        java.awt.Font font = hjbproperties.getFont(s + ".font");
        if(font != null)
            setFont(font);
        java.awt.Color color = hjbproperties.getColor(s + ".background", null);
        if(color != null)
            setBackground(color);
        color = hjbproperties.getColor(s + ".foreground", null);
        if(color != null)
            setForeground(color);
        String s1 = hjbproperties.getProperty(s + ".text");
        if(s1 != null)
            setText(s1);
        setEditable(hjbproperties.getProperty(s + ".readonly") == null);
    }
}
