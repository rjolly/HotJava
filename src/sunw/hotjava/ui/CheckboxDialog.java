// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   CheckboxDialog.java

package sunw.hotjava.ui;

import java.awt.*;
import java.awt.event.*;
import java.util.EventObject;
import sunw.hotjava.misc.HJBProperties;

// Referenced classes of package sunw.hotjava.ui:
//            BagDialog, EnterKeyListener, KeyPressInterest, MultiLineLabel, 
//            UIHJButton, UIHJButtonGroup, UserDialog, UserImageLabel

public class CheckboxDialog extends BagDialog
    implements ActionListener, KeyPressInterest
{

    public CheckboxDialog(Frame frame, String s, HJBProperties hjbproperties)
    {
        this(frame, s, hjbproperties, false);
    }

    public CheckboxDialog(Frame frame, String s, HJBProperties hjbproperties, boolean flag)
    {
        super(s, frame, hjbproperties);
        hasCheckbox = flag;
        enterKeyListener = new EnterKeyListener(this);
        GridBagLayout gridbaglayout = (GridBagLayout)getLayout();
        GridBagConstraints gridbagconstraints = new GridBagConstraints();
        gridbagconstraints.gridwidth = 1;
        gridbagconstraints.gridheight = 1;
        gridbagconstraints.fill = 0;
        gridbagconstraints.gridx = 0;
        gridbagconstraints.gridy = -1;
        UserImageLabel userimagelabel = new UserImageLabel("securitydialog.icon.quest", hjbproperties);
        gridbagconstraints.anchor = 18;
        gridbagconstraints.insets = new Insets(10, 20, 0, 40);
        addComponent(userimagelabel, gridbaglayout, gridbagconstraints);
        gridbagconstraints.insets = new Insets(10, 10, 5, 10);
        gridbagconstraints.anchor = 10;
        if(prompt == null)
            prompt = hjbproperties.getProperty("confirm." + getName() + ".prompt", "Which do you want to do?");
        mlabel = new MultiLineLabel(prompt);
        addComponent(mlabel, gridbaglayout, gridbagconstraints);
        if(hasCheckbox)
        {
            checkbox = new Checkbox(hjbproperties.getProperty("confirm." + getName() + ".checkbox", "Checkbox label"));
            addComponent(checkbox, gridbaglayout, gridbagconstraints);
            checkbox.addKeyListener(enterKeyListener);
        }
        Panel panel = new Panel();
        ((FlowLayout)panel.getLayout()).setAlignment(2);
        UIHJButtonGroup uihjbuttongroup = new UIHJButtonGroup(hjbproperties);
        buttons = new UIHJButton[2];
        buttons[0] = new UIHJButton(getName() + ".0", hjbproperties);
        buttons[1] = new UIHJButton(getName() + ".1", hjbproperties);
        uihjbuttongroup.addButtonToGroup(buttons[0]);
        uihjbuttongroup.addButtonToGroup(buttons[1]);
        panel.add(buttons[0]);
        panel.add(buttons[1]);
        buttons[0].addActionListener(this);
        buttons[1].addActionListener(this);
        gridbagconstraints.anchor = 13;
        gridbagconstraints.insets = new Insets(0, 0, 0, 0);
        addComponent(panel, gridbaglayout, gridbagconstraints);
        String s1 = hjbproperties.getProperty("confirm." + getName() + ".title", "Confirm Dialog");
        setTitle(s1);
        pack();
        centerOnScreen();
    }

    public void setPrompt(String s)
    {
        prompt = s;
        mlabel.setLabel(prompt);
        setSize(getPreferredSize());
    }

    public void processEnterEvent(KeyEvent keyevent)
    {
        for(int i = 0; i < buttons.length; i++)
        {
            buttons[i].removeActionListener(this);
            if(buttons[i].isDefault())
            {
                actionPerformed(new ActionEvent(buttons[i], 0, ""));
                return;
            }
        }

    }

    public void actionPerformed(ActionEvent actionevent)
    {
        boolean flag = false;
        for(int i = 0; i < buttons.length; i++)
        {
            buttons[i].removeActionListener(this);
            if((UIHJButton)actionevent.getSource() == buttons[i])
            {
                answer = i;
                cboxanswer = checkbox.getState();
                hide();
                flag = true;
            }
        }

        if(flag)
        {
            return;
        } else
        {
            hide();
            super.processEvent(actionevent);
            return;
        }
    }

    public int getAnswer()
    {
        return answer;
    }

    public boolean getCheckboxAnswer()
    {
        return cboxanswer;
    }

    private Checkbox checkbox;
    private UIHJButton buttons[];
    private boolean hasCheckbox;
    private boolean cboxanswer;
    private int answer;
    private String prompt;
    private MultiLineLabel mlabel;
    private EnterKeyListener enterKeyListener;
}
