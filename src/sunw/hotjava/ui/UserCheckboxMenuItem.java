// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   UserCheckboxMenuItem.java

package sunw.hotjava.ui;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ItemListener;

public class UserCheckboxMenuItem extends CheckboxMenuItem
{

    public UserCheckboxMenuItem(String s, String s1, ActionListener actionlistener)
    {
        super(s1);
        boolean flag = true;
        String s2 = s;
        int i = 0;
        if(s2.charAt(i) == '*')
        {
            flag = false;
            i++;
        }
        if(s2.charAt(i) == '~')
        {
            char c = s.charAt(++i);
            i += 2;
            setShortcut(new MenuShortcut(c, Character.isUpperCase(c)));
        }
        setName(s2.substring(i));
        setState(Boolean.getBoolean(s));
        enable(flag);
        ItemListener itemlistener = (ItemListener)actionlistener;
        addItemListener(itemlistener);
    }

    public boolean postEvent(Event event)
    {
        event.arg = getName();
        return super.postEvent(event);
    }

    public String paramString()
    {
        return super.paramString() + ",name=" + getName();
    }
}
