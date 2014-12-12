// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   UserMenu.java

package sunw.hotjava.ui;

import java.awt.*;
import java.awt.event.ActionListener;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.StringTokenizer;
import sun.io.ByteToCharConverter;
import sunw.hotjava.misc.HJBProperties;

// Referenced classes of package sunw.hotjava.ui:
//            UserCheckboxMenuItem, UserMenuItem

public class UserMenu extends Menu
    implements Cloneable
{

    public UserMenu(String s, ActionListener actionlistener)
    {
        super(title(s));
        setName(s);
        construct(s, actionlistener);
    }

    static String title(String s)
    {
        HJBProperties hjbproperties = HJBProperties.getHJBProperties("hjbrowser");
        String s1 = hjbproperties.getProperty(s);
        if(s1 == null)
            return s;
        int i = s1.indexOf('|');
        if(i >= 0)
            return s1.substring(0, i);
        else
            return s1;
    }

    void construct(String s, ActionListener actionlistener)
    {
        HJBProperties hjbproperties = HJBProperties.getHJBProperties("hjbrowser");
        String s1 = hjbproperties.getProperty(s);
        boolean flag = s.endsWith("charsets");
        if(s1 == null)
            return;
        int i = s1.indexOf('|');
        if(i < 0)
            return;
        s1 = s1.substring(i + 1);
        for(StringTokenizer stringtokenizer = new StringTokenizer(s1, "|"); stringtokenizer.hasMoreTokens();)
        {
            String s2 = stringtokenizer.nextToken();
            String s3 = s2;
            String s4 = s2;
            int j = s2.indexOf('=');
            if(j >= 0)
            {
                s3 = s2.substring(0, j);
                s4 = s2.substring(j + 1);
            }
            if(s4.startsWith("-"))
                addSeparator();
            else
            if(s4.startsWith("@"))
            {
                s4 = s4.substring(1).trim();
                add(new UserMenu(s4, actionlistener));
            } else
            {
                s3 = s3.trim();
                s4 = s4.trim();
                if(s3.endsWith("?"))
                {
                    UserCheckboxMenuItem usercheckboxmenuitem = new UserCheckboxMenuItem(s3.substring(0, s3.length() - 1), s4, actionlistener);
                    add(usercheckboxmenuitem);
                    if(flag)
                    {
                        String s5 = usercheckboxmenuitem.getName().substring("charset ".length());
                        try
                        {
                            ByteToCharConverter.getConverter(s5);
                        }
                        catch(UnsupportedEncodingException _ex)
                        {
                            usercheckboxmenuitem.setEnabled(false);
                            System.out.println("Charset " + s5 + " is not supported. Disable it in menu");
                        }
                    }
                } else
                {
                    add(new UserMenuItem(s3, s4, actionlistener));
                }
            }
        }

    }

    public MenuItem getItem(String s)
    {
        return getItem(s, true);
    }

    public MenuItem getItem(String s, boolean flag)
    {
        int i = countItems();
        for(int j = 0; j < i; j++)
        {
            MenuItem menuitem = getItem(j);
            if(menuitem instanceof UserMenuItem)
            {
                if(((UserMenuItem)menuitem).getName().equals(s))
                    return menuitem;
            } else
            if(menuitem instanceof UserCheckboxMenuItem)
            {
                if(((UserCheckboxMenuItem)menuitem).getName().equals(s))
                    return menuitem;
            } else
            if(menuitem instanceof UserMenu)
            {
                if(menuitem.getName().equals(s))
                    return menuitem;
                if(flag)
                {
                    menuitem = ((UserMenu)menuitem).getItem(s);
                    if(menuitem != null)
                        return menuitem;
                }
            }
        }

        return null;
    }

    public void removeFirstSeparator()
    {
        int i = countItems();
        for(int j = 0; j < i; j++)
        {
            MenuItem menuitem = getItem(j);
            if(menuitem.getLabel().equals("-"))
            {
                remove(j);
                return;
            }
        }

    }

    public int getItemPosition(String s)
    {
        int i = countItems();
        for(int j = 0; j < i; j++)
        {
            MenuItem menuitem = getItem(j);
            if(menuitem.getName().equals(s))
                return j;
        }

        return -1;
    }

    public UserMenu clone(ActionListener actionlistener)
    {
        UserMenu usermenu = new UserMenu(getName(), actionlistener);
        int i = getItemCount();
        for(int j = 0; j < i; j++)
        {
            MenuItem menuitem = getItem(j);
            if(menuitem instanceof UserMenu)
            {
                UserMenu usermenu1 = (UserMenu)menuitem;
                UserMenu usermenu2 = usermenu1.clone(actionlistener);
                usermenu.add(usermenu2);
            } else
            if(menuitem instanceof UserMenuItem)
            {
                UserMenuItem usermenuitem = (UserMenuItem)menuitem;
                UserMenuItem usermenuitem1 = usermenuitem.clone(actionlistener);
                usermenu.add(usermenuitem1);
            } else
            if("-".equals(menuitem.getLabel()))
                usermenu.addSeparator();
        }

        return usermenu;
    }
}
