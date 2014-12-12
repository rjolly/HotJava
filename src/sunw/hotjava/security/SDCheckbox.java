// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   SecurityDialog.java

package sunw.hotjava.security;

import java.awt.*;
import sunw.hotjava.misc.HJBProperties;
import sunw.hotjava.ui.EnterKeyListener;

// Referenced classes of package sunw.hotjava.security:
//            SecurityDialog

class SDCheckbox extends Checkbox
{

    public SDCheckbox(String s, boolean flag, CheckboxGroup checkboxgroup, int i, EnterKeyListener enterkeylistener)
    {
        super(getName(s), flag, checkboxgroup);
        value = i;
        addKeyListener(enterkeylistener);
    }

    private static String getName(String s)
    {
        HJBProperties hjbproperties = HJBProperties.getHJBProperties("hjbrowser");
        String s1 = hjbproperties.getProperty("hotjava.auth." + s);
        return s1;
    }

    public int value;
}
