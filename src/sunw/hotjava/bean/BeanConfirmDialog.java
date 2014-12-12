// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   BeanConfirmDialog.java

package sunw.hotjava.bean;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Label;
import java.awt.List;
import java.awt.Panel;
import java.awt.Point;
import java.awt.TextField;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import sunw.hotjava.misc.HJBProperties;
import sunw.hotjava.ui.UIHJButton;
import sunw.hotjava.ui.UIHJButtonGroup;

// Referenced classes of package sunw.hotjava.bean:
//            ConfirmDialogPanel

public class BeanConfirmDialog extends Dialog
    implements ActionListener
{

    public BeanConfirmDialog(String s, Frame frame)
    {
        this(s, frame, 2, true, true);
    }

    public BeanConfirmDialog(String s, Frame frame, int i)
    {
        this(s, frame, i, true, true);
    }

    public BeanConfirmDialog(String s, Frame frame, int i, boolean flag)
    {
        this(s, frame, i, flag, true);
    }

    public BeanConfirmDialog(String s, Frame frame, int i, boolean flag, boolean flag1)
    {
        super(frame, flag);
        focused = false;
        properties = HJBProperties.getHJBProperties("hjbrowser");
        setTitle(properties.getProperty(s + ".title"));
        setName(s);
        java.awt.Font font = properties.getFont(s + ".font");
        if(font != null)
            setFont(font);
        java.awt.Color color = properties.getColor(s + ".background", null);
        if(color != null)
            setBackground(color);
        color = properties.getColor(s + ".foreground", null);
        if(color != null)
            setForeground(color);
        int j = properties.getInteger(s + ".width", -1);
        int k = properties.getInteger(s + ".height", -1);
        Dimension dimension1;
        if(j != -1 && k != -1)
        {
            Dimension dimension = new Dimension(j, k);
            setSize(dimension);
        } else
        {
            dimension1 = getSize();
        }
        int l = properties.getInteger(s + ".x", -1);
        int i1 = properties.getInteger(s + ".y", -1);
        if(l != -1 && i1 != -1)
            setLocation(l, i1);
        WindowAdapter windowadapter = new WindowAdapter() {

            public void windowClosing(WindowEvent windowevent)
            {
                hide();
            }

        }
;
        addWindowListener(windowadapter);
        init(i, flag1);
    }

    public Dimension getPreferredSize()
    {
        String s = getName();
        int i = properties.getInteger(s + ".width", -1);
        int j = properties.getInteger(s + ".height", -1);
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
            ((List)component).setBackground(properties.getColor("editcontrol.background", null));
        if(component instanceof TextField)
            ((TextField)component).setBackground(properties.getColor("editcontrol.background", null));
    }

    protected void centerOnScreen()
    {
        String s = getName();
        int i = properties.getInteger(s + ".x", -1);
        int j = properties.getInteger(s + ".y", -1);
        if(i != -1 && j != -1)
            return;
        Container container = getParent();
        Point point = container.getLocationOnScreen();
        Dimension dimension = container.getSize();
        Dimension dimension1 = getSize();
        point.x += (dimension.width - dimension1.width) / 2;
        point.y += (dimension.height - dimension1.height) / 2;
        Dimension dimension2 = Toolkit.getDefaultToolkit().getScreenSize();
        if(point.x < 0 || point.y < 0 || point.x + dimension1.width > dimension2.width || point.y + dimension1.height > dimension2.height)
        {
            point.x = (dimension2.width - dimension1.width) / 2;
            point.y = (dimension2.height - dimension1.height) / 2;
        }
        setLocation(point.x, point.y);
    }

    public int getAnswer()
    {
        return answer;
    }

    public void setPrompt(String s)
    {
        prompt = s;
        promptLabel.setText(prompt);
    }

    public boolean gotFocus(Event event, Object obj)
    {
        if(!focused)
        {
            buttons[0].requestFocus();
            focused = true;
        }
        return true;
    }

    public void setFocus(int i)
    {
        buttons[1].requestFocus();
    }

    private void init(int i, boolean flag)
    {
        answer = 0;
        ConfirmDialogPanel confirmdialogpanel = new ConfirmDialogPanel();
        add("Center", confirmdialogpanel);
        prompt = properties.getProperty("confirm." + getName() + ".prompt", "Which do you want to do?");
        promptLabel = new Label(prompt);
        confirmdialogpanel.add("Center", promptLabel);
        initButtons(i, flag);
        addButtons(confirmdialogpanel);
        String s = properties.getProperty("confirm." + getName() + ".title", "Confirm Dialog");
        setTitle(s);
        pack();
        centerOnScreen();
    }

    private void initButtons(int i)
    {
        UIHJButtonGroup uihjbuttongroup = new UIHJButtonGroup(properties);
        if(i == 2)
        {
            buttons = new UIHJButton[2];
            buttons[0] = new UIHJButton(getName() + ".yes", properties);
            buttons[1] = new UIHJButton(getName() + ".no", properties);
            buttons[0].addActionListener(this);
            buttons[1].addActionListener(this);
            uihjbuttongroup.addButtonToGroup(buttons[0]);
            uihjbuttongroup.addButtonToGroup(buttons[1]);
            return;
        }
        buttons = new UIHJButton[i];
        for(int j = 0; j < i; j++)
        {
            buttons[j] = new UIHJButton(getName() + "." + j, properties);
            buttons[j].addActionListener(this);
            uihjbuttongroup.addButtonToGroup(buttons[j]);
        }

    }

    private void initButtons(int i, boolean flag)
    {
        UIHJButtonGroup uihjbuttongroup = new UIHJButtonGroup(properties);
        if(flag)
        {
            initButtons(i);
            return;
        }
        buttons = new UIHJButton[i];
        for(int j = 0; j < i; j++)
        {
            buttons[j] = new UIHJButton(getName() + "." + j, properties);
            buttons[j].addActionListener(this);
            uihjbuttongroup.addButtonToGroup(buttons[j]);
        }

    }

    private void addButtons(Panel panel)
    {
        Panel panel1 = new Panel();
        panel1.setLayout(new FlowLayout(1, 15, 15));
        for(int i = 0; i < buttons.length; i++)
            panel1.add(buttons[i]);

        panel.add("South", panel1);
    }

    public void actionPerformed(ActionEvent actionevent)
    {
        for(int i = 0; i < buttons.length; i++)
        {
            buttons[i].removeActionListener(this);
            if((UIHJButton)actionevent.getSource() == buttons[i])
            {
                answer = i;
                hide();
            }
        }

        hide();
        super.processEvent(actionevent);
    }

    protected UIHJButton buttons[];
    protected String prompt;
    private boolean focused;
    Label promptLabel;
    public static final int NO = 1;
    public static final int YES = 0;
    private HJBProperties properties;
    private int answer;
}
