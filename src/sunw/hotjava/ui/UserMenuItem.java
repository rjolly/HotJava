// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   UserMenuItem.java

package sunw.hotjava.ui;

import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

public class UserMenuItem extends MenuItem
    implements Cloneable
{

    public UserMenuItem(String s, String s1, ActionListener actionlistener)
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
            char c1 = c;
            if(c == '<')
                c1 = '%';
            else
            if(c == '>')
                c1 = '\'';
            i += 2;
            setShortcut(new MenuShortcut(c1, Character.isUpperCase(c)));
        }
        setName(s2.substring(i));
        enable(flag);
        addActionListener(actionlistener);
        setActionCommand(getName());
    }

    public boolean postEvent(Event event)
    {
        event.arg = getName();
        return super.postEvent(event);
    }

    public UserMenuItem clone(ActionListener actionlistener)
    {
        String s = getName();
        MenuShortcut menushortcut = getShortcut();
        if(menushortcut != null)
        {
            char c = (char)menushortcut.getKey();
            s = "~" + c + "~" + s;
        }
        if(!isEnabled())
            s = "*" + s;
        UserMenuItem usermenuitem = new UserMenuItem(s, getLabel(), actionlistener);
        return usermenuitem;
    }

    public void setShortcut(MenuShortcut menushortcut)
    {
        if(Character.toLowerCase((char)menushortcut.getKey()) == 'v' && "JavaOS".equalsIgnoreCase(System.getProperty("os.name")))
        {
            return;
        } else
        {
            super.setShortcut(menushortcut);
            return;
        }
    }
}
