// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   BagDialog.java

package sunw.hotjava.ui;

import java.awt.*;
import sunw.hotjava.misc.HJBProperties;

// Referenced classes of package sunw.hotjava.ui:
//            UserDialog, UserImageLabel, UIHJButton

public class BagDialog extends UserDialog
{

    public BagDialog(String s, Frame frame, HJBProperties hjbproperties)
    {
        super(s, frame, true, hjbproperties);
        hjbProperties = hjbproperties;
        setLayout(new GridBagLayout());
    }

    protected void addComponent(Component component, GridBagLayout gridbaglayout, int i, int j)
    {
        GridBagConstraints gridbagconstraints = new GridBagConstraints();
        gridbagconstraints.anchor = 17;
        gridbagconstraints.gridx = 0;
        gridbagconstraints.gridy = -1;
        gridbagconstraints.gridwidth = i;
        gridbagconstraints.gridheight = j;
        addComponent(component, gridbaglayout, gridbagconstraints);
    }

    protected boolean addIcon(GridBagConstraints gridbagconstraints)
    {
        return addIcon(gridbagconstraints, getName());
    }

    protected boolean addIcon(GridBagConstraints gridbagconstraints, String s)
    {
        icon = new UserImageLabel(s + ".icon", hjbProperties);
        if(icon != null)
        {
            GridBagLayout gridbaglayout = (GridBagLayout)getLayout();
            gridbaglayout.setConstraints(icon, gridbagconstraints);
            add(icon);
            return true;
        } else
        {
            return false;
        }
    }

    protected void addComponent(Component component, GridBagLayout gridbaglayout, GridBagConstraints gridbagconstraints)
    {
        gridbaglayout.setConstraints(component, gridbagconstraints);
        add(component);
    }

    protected String getProp(String s)
    {
        return hjbProperties.getProperty(getName() + "." + s);
    }

    protected String getProp(String s, String s1)
    {
        return hjbProperties.getProperty(getName() + "." + s, s1);
    }

    protected Label addText(String s, GridBagLayout gridbaglayout, GridBagConstraints gridbagconstraints)
    {
        Label label = new Label(s);
        gridbaglayout.setConstraints(label, gridbagconstraints);
        add(label);
        return label;
    }

    public boolean action(Event event, Object obj)
    {
        if(event.target == ok)
        {
            setVisible(false);
            return true;
        } else
        {
            return super.action(event, obj);
        }
    }

    public int handleDialog()
    {
        pack();
        invalidate();
        validate();
        setVisible(true);
        dispose();
        return result;
    }

    protected int result;
    protected UserImageLabel icon;
    protected UIHJButton ok;
    protected HJBProperties hjbProperties;
}
