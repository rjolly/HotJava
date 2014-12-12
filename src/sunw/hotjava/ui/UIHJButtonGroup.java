// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   UIHJButtonGroup.java

package sunw.hotjava.ui;

import java.awt.Dimension;
import java.util.Enumeration;
import java.util.Vector;
import sunw.hotjava.misc.HJBProperties;

// Referenced classes of package sunw.hotjava.ui:
//            UIHJButton

public class UIHJButtonGroup
{

    public UIHJButtonGroup(HJBProperties hjbproperties)
    {
        buttons = new Vector(2, 2);
        props = hjbproperties;
    }

    public void addButtonToGroup(UIHJButton uihjbutton)
    {
        boolean flag = false;
        buttons.addElement(uihjbutton);
        int i = buttons.size();
        if(uihjbutton.isDefault() && i != 1)
        {
            defaultButton = uihjbutton;
            UIHJButton uihjbutton1 = (UIHJButton)buttons.firstElement();
            uihjbutton1.setDefaultButton(false);
        } else
        if(i == 1)
        {
            defaultButton = uihjbutton;
            uihjbutton.setDefaultButton(true);
        }
        Dimension dimension = uihjbutton.getLabelSize();
        if(dimension.width > maxX)
        {
            flag = true;
            maxX = dimension.width;
        }
        if(dimension.height > maxY)
        {
            flag = true;
            maxY = dimension.height;
        }
        uihjbutton.setLabelSize(maxX, maxY);
        if(flag)
        {
            UIHJButton uihjbutton2;
            for(Enumeration enumeration = buttons.elements(); enumeration.hasMoreElements(); uihjbutton2.setLabelSize(maxX, maxY))
                uihjbutton2 = (UIHJButton)enumeration.nextElement();

        }
    }

    public UIHJButton getDefaultButton()
    {
        return defaultButton;
    }

    Vector buttons;
    int maxX;
    int maxY;
    HJBProperties props;
    UIHJButton defaultButton;
}
