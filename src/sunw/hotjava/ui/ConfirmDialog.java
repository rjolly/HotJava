// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ConfirmDialog.java

package sunw.hotjava.ui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EventObject;
import sunw.hotjava.misc.HJBProperties;

// Referenced classes of package sunw.hotjava.ui:
//            UserDialog, ConfirmDialogPanel, MultiLineLabel, UIHJButton, 
//            UIHJButtonGroup, UserImageLabel

public class ConfirmDialog extends UserDialog
    implements ActionListener
{

    public ConfirmDialog(String s, Frame frame)
    {
        this(s, frame, 2, true, true);
    }

    public ConfirmDialog(String s, Frame frame, int i)
    {
        this(s, frame, i, true, true);
    }

    public ConfirmDialog(String s, Frame frame, int i, String as[])
    {
        this(s, frame, i, true, false, as);
    }

    public ConfirmDialog(String s, Frame frame, int i, boolean flag)
    {
        this(s, frame, i, flag, true);
    }

    public ConfirmDialog(String s, Frame frame, int i, boolean flag, boolean flag1)
    {
        super(s, frame, flag, HJBProperties.getHJBProperties("hjbrowser"));
        focused = false;
        properties = HJBProperties.getHJBProperties("hjbrowser");
        init(i, flag1, null);
    }

    public ConfirmDialog(String s, Frame frame, int i, boolean flag, boolean flag1, String as[])
    {
        super(s, frame, flag, HJBProperties.getHJBProperties("hjbrowser"));
        focused = false;
        properties = HJBProperties.getHJBProperties("hjbrowser");
        init(i, flag1, as);
    }

    public int getAnswer()
    {
        return answer;
    }

    public void setPrompt(String s)
    {
        prompt = s;
        promptLabel.setLabel(prompt);
        if(wrapper != null)
        {
            wrapper.setSize(wrapper.getPreferredSize());
            setSize(getPreferredSize());
        }
    }

    public Dimension getPromptSize()
    {
        return promptLabel.preferredSize();
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

    private void init(int i, boolean flag, String as[])
    {
        answer = 0;
        wrapper = new ConfirmDialogPanel();
        add("Center", wrapper);
        HJBProperties hjbproperties = HJBProperties.getHJBProperties("hjbrowser");
        btnGrouping = new UIHJButtonGroup(hjbproperties);
        String s = hjbproperties.getProperty("imagelabel.confirm." + getName());
        if(s != null)
        {
            UserImageLabel userimagelabel = new UserImageLabel("confirm." + getName(), properties);
            if(userimagelabel != null)
                wrapper.add("West", userimagelabel);
        }
        if(as != null)
            prompt = properties.getPropertyReplace("confirm." + getName() + ".replaceprompt", as);
        if(prompt == null)
            prompt = properties.getProperty("confirm." + getName() + ".prompt", "Which do you want to do?");
        promptLabel = new MultiLineLabel(prompt);
        wrapper.add("Center", promptLabel);
        initButtons(i, flag);
        addButtons(wrapper);
        String s1 = properties.getProperty("confirm." + getName() + ".title", "Confirm Dialog");
        setTitle(s1);
        pack();
        centerOnScreen();
    }

    private void initButtons(int i)
    {
        if(i == 2)
        {
            buttons = new UIHJButton[2];
            buttons[0] = new UIHJButton(getName() + ".no", properties);
            buttons[1] = new UIHJButton(getName() + ".yes", properties);
            return;
        }
        buttons = new UIHJButton[i];
        for(int j = 0; j < i; j++)
            buttons[j] = new UIHJButton(getName() + "." + j, properties);

    }

    private void initButtons(int i, boolean flag)
    {
        if(flag)
        {
            initButtons(i);
            return;
        }
        buttons = new UIHJButton[i];
        for(int j = 0; j < i; j++)
            buttons[j] = new UIHJButton(getName() + "." + j, properties);

    }

    private void addButtons(Panel panel)
    {
        Panel panel1 = new Panel();
        panel1.setLayout(new FlowLayout(1, 15, 15));
        for(int i = 0; i < buttons.length; i++)
        {
            panel1.add(buttons[i]);
            buttons[i].addActionListener(this);
            btnGrouping.addButtonToGroup(buttons[i]);
        }

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
                return;
            }
        }

        hide();
        super.processEvent(actionevent);
    }

    protected UIHJButton buttons[];
    protected String prompt;
    private boolean focused;
    MultiLineLabel promptLabel;
    UIHJButtonGroup btnGrouping;
    public static final int NO = 0;
    public static final int YES = 1;
    private ConfirmDialogPanel wrapper;
    private HJBProperties properties;
    private int answer;
}
