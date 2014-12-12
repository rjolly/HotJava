// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   UserCheckbox.java

package sunw.hotjava.ui;

import java.awt.*;
import sunw.hotjava.misc.HJBProperties;

public class UserCheckbox extends Checkbox
{

    public UserCheckbox(String s, HJBProperties hjbproperties)
    {
        this(s, null, hjbproperties);
    }

    public UserCheckbox(String s, CheckboxGroup checkboxgroup, HJBProperties hjbproperties)
    {
        super(hjbproperties.getProperty(s + ".text"), checkboxgroup, hjbproperties.getBoolean(s + ".state"));
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
