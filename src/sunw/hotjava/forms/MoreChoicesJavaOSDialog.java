// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   FormChoiceJavaOS.java

package sunw.hotjava.forms;

import java.awt.Choice;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Frame;
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
//            FormChoiceJavaOS

class MoreChoicesJavaOSDialog extends Dialog
{

    public MoreChoicesJavaOSDialog(Choice choice, int i, Frame frame)
    {
        super(frame);
        setModal(true);
        HJBProperties hjbproperties = HJBProperties.getHJBProperties("beanPropertiesKey");
        setTitle(hjbproperties.getProperty("forms.choice.more.dialog.title"));
        parentChoice = choice;
        listChoice = new List(15, false);
        UIHJButtonGroup uihjbuttongroup = new UIHJButtonGroup(hjbproperties);
        ok = new UIHJButton("forms.choice.more.ok", hjbproperties);
        String s = "forms.choice.more.cancel";
        cancel = new UIHJButton(s, hjbproperties);
        uihjbuttongroup.addButtonToGroup(ok);
        uihjbuttongroup.addButtonToGroup(cancel);
        for(int j = 0; j < parentChoice.getItemCount(); j++)
            listChoice.add(parentChoice.getItem(j));

        selectedIndex = i;
        listChoice.select(i);
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
            parentChoice.select(i);
        } else
        {
            parentChoice.select(selectedIndex);
        }
        hide();
    }

    private Choice parentChoice;
    private List listChoice;
    int selectedIndex;
    private UIHJButton ok;
    private UIHJButton cancel;
}
