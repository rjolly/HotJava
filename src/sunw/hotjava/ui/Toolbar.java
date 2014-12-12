// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Toolbar.java

package sunw.hotjava.ui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;
import java.util.StringTokenizer;
import sun.awt.OrientableFlowLayout;
import sunw.hotjava.HJFrame;
import sunw.hotjava.misc.HJBProperties;

// Referenced classes of package sunw.hotjava.ui:
//            DelegatingPanel, TimedMessageContainer, UpdateableComponent, UserImageButton

public class Toolbar extends Panel
    implements ActionListener
{

    public static Toolbar getDefaultToolbar(HJFrame hjframe)
    {
        return new Toolbar("2.0.default.toolbar", hjframe);
    }

    public Toolbar(String s, HJFrame hjframe)
    {
        frm = hjframe;
        setName(s);
        toolDictionary = new Hashtable(7);
        toolEventsListener = hjframe.getActionListener();
        init();
    }

    protected void init()
    {
        invalidate();
        HJBProperties hjbproperties = HJBProperties.getHJBProperties("hjbrowser");
        String s = hjbproperties.getProperty("hotjava." + getName() + ".buttonorder");
        setLayout(new OrientableFlowLayout(0, 0, 1, 0, 0, 0, 0));
        StringTokenizer stringtokenizer = new StringTokenizer(s, "|");
        System.gc();
        System.runFinalization();
        String s1;
        for(; stringtokenizer.hasMoreTokens(); addTool(s1))
            s1 = stringtokenizer.nextToken();

        System.gc();
        System.runFinalization();
        doLayout();
        validate();
    }

    public Component getTool(String s)
    {
        return (Component)toolDictionary.get(s);
    }

    public void orientHorizontally()
    {
        invalidate();
        java.awt.LayoutManager layoutmanager = getLayout();
        if(layoutmanager instanceof OrientableFlowLayout)
            ((OrientableFlowLayout)layoutmanager).orientHorizontally();
        doLayout();
        validate();
    }

    public void orientVertically()
    {
        invalidate();
        java.awt.LayoutManager layoutmanager = getLayout();
        if(layoutmanager instanceof OrientableFlowLayout)
            ((OrientableFlowLayout)layoutmanager).orientVertically();
        doLayout();
        validate();
    }

    protected Component makeTool(String s)
    {
        UserImageButton userimagebutton = new UserImageButton(s);
        userimagebutton.addActionListener(toolEventsListener);
        userimagebutton.addActionListener(this);
        String s1 = properties.getProperty("button." + s + ".message");
        if(s1 != null)
        {
            TimedMessageContainer timedmessagecontainer = new TimedMessageContainer(s1);
            timedmessagecontainer.add(userimagebutton);
            return timedmessagecontainer;
        } else
        {
            return userimagebutton;
        }
    }

    public void addTool(String s)
    {
        Component component = makeTool(s);
        addTool(s, component);
    }

    public void addTool(String s, int i)
    {
        Component component = makeTool(s);
        addTool(s, component, i);
    }

    public void addTool(String s, Component component)
    {
        addTool(s, component, -1);
    }

    public synchronized void addTool(String s, Component component, int i)
    {
        invalidate();
        add(component, i);
        component.setVisible(true);
        validate();
        toolDictionary.put(s, component);
    }

    public synchronized Component removeTool(String s)
    {
        Component component = (Component)toolDictionary.get(s);
        if(component != null)
        {
            invalidate();
            remove(component);
            validate();
            toolDictionary.remove(s);
        }
        return component;
    }

    public void enableTool(String s, boolean flag)
    {
        Component component = (Component)toolDictionary.get(s);
        if(component != null)
            component.setEnabled(flag);
    }

    public void enableTool(String s)
    {
        enableTool(s, true);
    }

    public void disableTool(String s)
    {
        enableTool(s, false);
    }

    public synchronized void resetButtonAppearance()
    {
        Component acomponent[] = getComponents();
        for(int i = 0; i < acomponent.length; i++)
            if(acomponent[i] instanceof TimedMessageContainer)
            {
                TimedMessageContainer timedmessagecontainer = (TimedMessageContainer)acomponent[i];
                Component acomponent1[] = timedmessagecontainer.getComponents();
                for(int j = 0; j < acomponent1.length; j++)
                    ((UpdateableComponent)acomponent1[j]).updateAppearance();

            } else
            if(acomponent[i] instanceof UpdateableComponent)
                ((UpdateableComponent)acomponent[i]).updateAppearance();

    }

    public void actionPerformed(ActionEvent actionevent)
    {
        String s = actionevent.getActionCommand();
        if(s.startsWith("entered"))
        {
            String s1 = s.substring("entered".length() + 1);
            String s2 = properties.getProperty("button." + s1 + ".message");
            frm.showStatus(s2);
            return;
        }
        if(s.equals(""))
            frm.showStatus("");
    }

    private ActionListener toolEventsListener;
    private HJFrame frm;
    protected Hashtable toolDictionary;
    private static HJBProperties properties = HJBProperties.getHJBProperties("hjbrowser");

}
