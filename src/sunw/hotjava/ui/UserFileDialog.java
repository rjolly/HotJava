// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   UserFileDialog.java

package sunw.hotjava.ui;

import java.awt.*;
import sunw.hotjava.misc.HJBProperties;

public class UserFileDialog extends FileDialog
{

    public UserFileDialog(Frame frame, String s, int i)
    {
        super(frame, HJBProperties.getHJBProperties("hjbrowser").getProperty(s + ".text"), i);
        setName(s);
        String s1 = super.getDirectory();
        if(s1 == null)
            setDirectory(System.getProperty("user.dir"));
        else
        if(s1.startsWith("./"))
            setDirectory(System.getProperty("user.dir") + s1.substring(2));
        properties = HJBProperties.getHJBProperties("hjbrowser");
        java.awt.Font font = properties.getFont(s + ".font");
        if(font != null)
            setFont(font);
        java.awt.Color color = properties.getColor(s + ".background", null);
        if(color != null)
            setBackground(color);
        color = properties.getColor(s + ".foreground", null);
        if(color != null)
            setForeground(color);
    }

    public UserFileDialog(Frame frame, String s)
    {
        super(frame, HJBProperties.getHJBProperties("hjbrowser").getProperty(s + ".text"));
        setName(s);
        String s1 = super.getDirectory();
        if(s1 == null)
            setDirectory(System.getProperty("user.dir"));
        else
        if(s1.startsWith("./"))
            setDirectory(System.getProperty("user.dir") + s1.substring(2));
        properties = HJBProperties.getHJBProperties("hjbrowser");
        java.awt.Font font = properties.getFont(s + ".font");
        if(font != null)
            setFont(font);
        java.awt.Color color = properties.getColor(s + ".background", null);
        if(color != null)
            setBackground(color);
        color = properties.getColor(s + ".foreground", null);
        if(color != null)
            setForeground(color);
    }

    public Component add(Component component)
    {
        Component component1 = super.add(component);
        setColor(component);
        return component1;
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
            ((List)component).setBackground(properties.getColor("editcontrol.background", null));
        if(component instanceof TextField)
            ((TextField)component).setBackground(properties.getColor("editcontrol.background", null));
    }

    public boolean providesSaveConfirmation()
    {
        return !getPeer().getClass().getName().equals("sun.awt.motif.MFileDialogPeer");
    }

    private HJBProperties properties;
}
