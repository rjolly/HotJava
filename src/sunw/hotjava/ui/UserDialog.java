// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   UserDialog.java

package sunw.hotjava.ui;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import sunw.hotjava.misc.HJBProperties;

public class UserDialog extends Dialog
{

    public UserDialog(String s, Frame frame, boolean flag, HJBProperties hjbproperties)
    {
        super(frame, hjbproperties.getProperty(s + ".title"), flag);
        props = hjbproperties;
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
        int i = hjbproperties.getInteger(s + ".width", -1);
        int j = hjbproperties.getInteger(s + ".height", -1);
        Dimension dimension1;
        if(i != -1 && j != -1)
        {
            Dimension dimension = new Dimension(i, j);
            setSize(dimension);
        } else
        {
            dimension1 = getSize();
        }
        int k = hjbproperties.getInteger(s + ".x", -1);
        int l = hjbproperties.getInteger(s + ".y", -1);
        if(k != -1 && l != -1)
            setLocation(k, l);
        WindowAdapter windowadapter = new WindowAdapter() {

            public void windowClosing(WindowEvent windowevent)
            {
                hide();
            }

        }
;
        addWindowListener(windowadapter);
    }

    public UserDialog(String s, Frame frame, HJBProperties hjbproperties)
    {
        this(s, frame, true, hjbproperties);
    }

    public Dimension getPreferredSize()
    {
        String s = getName();
        int i = props.getInteger(s + ".width", -1);
        int j = props.getInteger(s + ".height", -1);
        if(i != -1 && j != -1)
            return new Dimension(i, j);
        else
            return super.getPreferredSize();
    }

    public synchronized Component add(Component component, int i)
    {
        Component component1 = super.add(component, i);
        setColor(component);
        return component1;
    }

    public synchronized Component add(String s, Component component)
    {
        Component component1 = super.add(s, component);
        setColor(component);
        return component1;
    }

    private void setColor(Component component)
    {
        if(component instanceof List)
            ((List)component).setBackground(props.getColor("editcontrol.background", null));
        if(component instanceof TextField)
            ((TextField)component).setBackground(props.getColor("editcontrol.background", null));
    }

    protected void centerOnScreen()
    {
        String s = getName();
        Dimension dimension = getSize();
        Point point = null;
        boolean flag = false;
        int i = props.getInteger(s + ".x", -1);
        int j = props.getInteger(s + ".y", -1);
        if(i != -1 && j != -1)
            return;
        Container container = getParent();
        flag = container.isShowing();
        if(flag)
        {
            point = container.getLocationOnScreen();
            Dimension dimension1 = container.getSize();
            point.x += (dimension1.width - dimension.width) / 2;
            point.y += (dimension1.height - dimension.height) / 2;
        } else
        {
            point = new Point(0, 0);
        }
        Dimension dimension2 = Toolkit.getDefaultToolkit().getScreenSize();
        if(!flag || point.x < 0 || point.y < 0 || point.x + dimension.width > dimension2.width || point.y + dimension.height > dimension2.height)
        {
            point.x = (dimension2.width - dimension.width) / 2;
            point.y = (dimension2.height - dimension.height) / 2;
        }
        setLocation(point.x, point.y);
    }

    HJBProperties props;
}
