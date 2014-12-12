// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   FormChoice.java

package sunw.hotjava.forms;

import java.awt.Choice;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.List;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import sunw.hotjava.misc.Globals;
import sunw.hotjava.misc.HJBProperties;
import sunw.hotjava.ui.UIHJButton;
import sunw.hotjava.ui.UIHJButtonGroup;

// Referenced classes of package sunw.hotjava.forms:
//            FormChoice

class MoreChoicesDialog extends Dialog
{

    public MoreChoicesDialog(FormChoice formchoice, Frame frame)
    {
        super(frame);
        setModal(true);
        HJBProperties hjbproperties = HJBProperties.getHJBProperties("beanPropertiesKey");
        setTitle(hjbproperties.getProperty("forms.choice.more.dialog.title"));
        parentChoice = formchoice;
        String as[] = formchoice.getExtraStrings();
        listChoice = new List(15, false);
        UIHJButtonGroup uihjbuttongroup = new UIHJButtonGroup(hjbproperties);
        ok = new UIHJButton("forms.choice.more.ok", hjbproperties);
        String s = "forms.choice.more.cancel";
        cancel = new UIHJButton(s, hjbproperties);
        uihjbuttongroup.addButtonToGroup(ok);
        uihjbuttongroup.addButtonToGroup(cancel);
        for(int i = 0; i < parentChoice.getItemCount() - 1; i++)
            listChoice.add(parentChoice.getItem(i));

        for(int j = 0; j < as.length; j++)
            listChoice.add(as[j]);

        GridBagLayout gridbaglayout = new GridBagLayout();
        GridBagConstraints gridbagconstraints = new GridBagConstraints();
        Insets insets = new Insets(2, 2, 2, 2);
        gridbagconstraints.anchor = 10;
        gridbagconstraints.ipadx = 2;
        gridbagconstraints.ipady = 2;
        gridbagconstraints.insets = insets;
        gridbagconstraints.fill = 1;
        gridbagconstraints.weightx = 1.0D;
        gridbagconstraints.weighty = 1.0D;
        setLayout(gridbaglayout);
        constrain(gridbaglayout, gridbagconstraints, listChoice, 0, 0, 2, 1);
        gridbagconstraints.anchor = 13;
        gridbagconstraints.fill = 0;
        gridbagconstraints.weighty = 0.0D;
        constrain(gridbaglayout, gridbagconstraints, ok, 0, 1, 1, 1);
        constrain(gridbaglayout, gridbagconstraints, cancel, 1, 1, 1, 1);
        add(listChoice);
        add(ok);
        add(cancel);
        ActionListener actionlistener = new ActionListener() {

            public void actionPerformed(ActionEvent actionevent)
            {
                processActionEvent(actionevent);
            }

        }
;
        ok.addActionListener(actionlistener);
        cancel.addActionListener(actionlistener);
    }

    private void constrain(GridBagLayout gridbaglayout, GridBagConstraints gridbagconstraints, Component component, int i, int j, int k, int l)
    {
        gridbagconstraints.gridx = i;
        gridbagconstraints.gridy = j;
        gridbagconstraints.gridwidth = k;
        gridbagconstraints.gridheight = l;
        gridbaglayout.setConstraints(component, gridbagconstraints);
    }

    public void processActionEvent(ActionEvent actionevent)
    {
        if(actionevent.getSource() == ok)
        {
            int i = listChoice.getSelectedIndex();
            if(i < parentChoice.getItemCount() - 2)
            {
                parentChoice.select(i);
            } else
            {
                int j = parentChoice.getItemCount() - 2;
                parentChoice.replaceItem(j, listChoice.getSelectedItem());
                parentChoice.select(j);
            }
            parentChoice.setLastSelection(listChoice.getSelectedItem());
            parentChoice.setLastSelectedIndex(i);
        } else
        if(actionevent.getSource() == cancel)
        {
            String s = parentChoice.getLastSelection();
            parentChoice.select(s);
        }
        hide();
    }

    public void paint(Graphics g)
    {
        setBackground(Color.white);
        setForeground(Color.black);
    }

    private FormChoice parentChoice;
    private List listChoice;
    private UIHJButton ok;
    private UIHJButton cancel;
}
