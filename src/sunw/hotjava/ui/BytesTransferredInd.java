// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ProgressDialog.java

package sunw.hotjava.ui;

import java.awt.*;
import java.text.Format;
import java.text.MessageFormat;
import sunw.hotjava.misc.HJBProperties;

// Referenced classes of package sunw.hotjava.ui:
//            ActivityBar, BPSIndicator, BarCanvas, ProgressDialog, 
//            ValueBar, ValueUpdateable

class BytesTransferredInd extends Panel
{

    BytesTransferredInd()
    {
        argsArray = new Object[3];
        layout = new GridBagLayout();
        properties = HJBProperties.getHJBProperties("hjbrowser");
        setLayout(layout);
        add(new Label(""));
    }

    private void addComponent(GridBagLayout gridbaglayout, GridBagConstraints gridbagconstraints, Component component)
    {
        gridbaglayout.setConstraints(component, gridbagconstraints);
        add(component);
    }

    public Insets getInsets()
    {
        return myInsets;
    }

    void setTotalBytes(long l)
    {
        remove(getComponent(0));
        Object obj;
        if(l < 0L)
        {
            ActivityBar activitybar = new ActivityBar(100, 20, 0);
            maxValueStr = "";
            indicator = activitybar;
            obj = activitybar;
        } else
        {
            ValueBar valuebar = new ValueBar(100, 20, 0);
            valuebar.setMaxValue(l);
            indicator = valuebar;
            obj = valuebar;
            String s = ProgressDialog.convertToReadableValue(l);
            maxValueStr = properties.getPropertyReplace("progressDialog.BytesTransferredInd.maxSizeTemplate", s);
        }
        GridBagConstraints gridbagconstraints = new GridBagConstraints();
        gridbagconstraints.gridy = 0;
        gridbagconstraints.anchor = 17;
        gridbagconstraints.weightx = 1.0D;
        gridbagconstraints.weighty = 1.0D;
        addComponent(layout, gridbagconstraints, ((Component) (obj)));
        valueTemplate = new MessageFormat(properties.getProperty("progressDialog.BytesTransferredInd.label", "Bytes transferred: {0}{1}"));
        argsArray[0] = "0";
        argsArray[1] = maxValueStr;
        valueLabel = new Label(valueTemplate.format(((Object) (argsArray))));
        addComponent(layout, gridbagconstraints, valueLabel);
    }

    void updateBytesTransferred(long l)
    {
        if(indicator != null)
            indicator.updateValue(l);
        String s = ProgressDialog.convertToReadableValue(l);
        argsArray[0] = s;
        argsArray[1] = maxValueStr;
        if(valueLabel != null)
        {
            valueLabel.setText(valueTemplate.format(((Object) (argsArray))));
            valueLabel.invalidate();
        }
    }

    public void update(Graphics g)
    {
        paint(g);
    }

    private Label valueLabel;
    private MessageFormat valueTemplate;
    private Object argsArray[];
    private String maxValueStr;
    private static final Insets myInsets = new Insets(2, 2, 2, 2);
    private GridBagLayout layout;
    ValueUpdateable indicator;
    private HJBProperties properties;

}
