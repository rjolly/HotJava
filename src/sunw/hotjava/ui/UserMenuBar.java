// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   UserMenuBar.java

package sunw.hotjava.ui;

import java.awt.*;
import java.awt.event.ActionListener;
import java.util.StringTokenizer;
import sunw.hotjava.misc.HJBProperties;

// Referenced classes of package sunw.hotjava.ui:
//            UserMenu

public class UserMenuBar extends MenuBar
{

    public UserMenuBar(String s, ActionListener actionlistener)
    {
        setName(s);
        construct(s, actionlistener);
    }

    void construct(String s, ActionListener actionlistener)
    {
        HJBProperties hjbproperties = HJBProperties.getHJBProperties("hjbrowser");
        String s1 = hjbproperties.getProperty(s);
        if(s1 == null)
            return;
        for(StringTokenizer stringtokenizer = new StringTokenizer(s1, ":"); stringtokenizer.hasMoreTokens();)
        {
            String s2 = stringtokenizer.nextToken();
            if(s2.charAt(0) == '?')
            {
                s2 = s2.substring(1);
                UserMenu usermenu = new UserMenu(s2, actionlistener);
                setHelpMenu(usermenu);
            } else
            {
                add(new UserMenu(s2, actionlistener));
            }
        }

    }

    public UserMenu getMenu(String s)
    {
        int i = countMenus();
        for(int j = 0; j < i; j++)
        {
            UserMenu usermenu = (UserMenu)getMenu(j);
            if(usermenu.getName().equals(s))
                return usermenu;
        }

        return null;
    }

    public UserMenu getSubMenu(Menu menu, String s)
    {
        int i = menu.countItems();
        for(int j = 0; j < i; j++)
        {
            MenuItem menuitem = menu.getItem(j);
            if(menuitem instanceof UserMenu)
            {
                UserMenu usermenu = (UserMenu)menuitem;
                if(usermenu.getName().equals(s))
                    return usermenu;
            }
        }

        return null;
    }

    public MenuItem getMenuItem(String s)
    {
        int i = countMenus();
        for(int j = 0; j < i; j++)
        {
            UserMenu usermenu = (UserMenu)getMenu(j);
            MenuItem menuitem = usermenu.getItem(s);
            if(menuitem != null)
                return menuitem;
        }

        return null;
    }
}
