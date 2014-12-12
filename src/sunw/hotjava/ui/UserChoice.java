// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   UserChoice.java

package sunw.hotjava.ui;

import java.awt.Choice;
import java.awt.Component;
import java.util.StringTokenizer;
import java.util.Vector;
import sunw.hotjava.misc.HJBProperties;

public class UserChoice extends Choice
{

    public UserChoice(String s, HJBProperties hjbproperties)
    {
        data = new Vector(3);
        setName(s);
        construct(s + ".items", hjbproperties);
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

    void construct(String s, HJBProperties hjbproperties)
    {
        String s1 = hjbproperties.getProperty(s);
        for(StringTokenizer stringtokenizer = new StringTokenizer(s1, "|"); stringtokenizer.hasMoreTokens();)
        {
            String s2 = stringtokenizer.nextToken();
            int i = s2.indexOf('=');
            if(i > 0)
            {
                data.addElement(s2.substring(0, i));
                addItem(s2.substring(i + 1));
            } else
            {
                data.addElement(s2);
                addItem(s2);
            }
        }

        select(hjbproperties.getInteger(s + ".select", 0));
    }

    public void selectData(String s)
    {
        if(data != null)
        {
            int i = data.indexOf(s);
            if(i != -1)
                select(i);
        }
    }

    public String getSelectedData()
    {
        if(data != null)
        {
            int i = getSelectedIndex();
            if(i != -1)
                return (String)data.elementAt(i);
        }
        return null;
    }

    String name;
    Vector data;
}
