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
//            ActivityBar, BarCanvas, BytesTransferredInd, ProgressDialog, 
//            ValueBar, ValueUpdateable

class BPSIndicator extends Panel
{

    BPSIndicator()
    {
        argsArray = new Object[3];
        properties = HJBProperties.getHJBProperties("hjbrowser");
        GridBagLayout gridbaglayout = new GridBagLayout();
        setLayout(gridbaglayout);
        GridBagConstraints gridbagconstraints = new GridBagConstraints();
        gridbagconstraints.gridy = 0;
        gridbagconstraints.anchor = 17;
        gridbagconstraints.weightx = 1.0D;
        gridbagconstraints.weighty = 1.0D;
        valueBar = new ValueBar(100, 20, 0);
        addComponent(gridbaglayout, gridbagconstraints, valueBar);
        valueTemplate = new MessageFormat(properties.getProperty("progressDialog.BPSIndicator.bps", "{0} (max {1}) bps"));
        argsArray[0] = "0";
        argsArray[1] = "1";
        valueLabel = new Label(valueTemplate.format(((Object) (argsArray))) + "                    ");
        Dimension dimension = valueLabel.getSize();
        valueLabel.setSize(dimension.width + 200, dimension.height);
        addComponent(gridbaglayout, gridbagconstraints, valueLabel);
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

    void updateBPS(long l)
    {
        String s = ProgressDialog.convertToReadableValue(l);
        if(l > maxBPS)
        {
            maxBPS = l;
            valueBar.setMaxValue(l);
        }
        String s1 = ProgressDialog.convertToReadableValue(maxBPS);
        valueBar.updateValue(l);
        argsArray[0] = s;
        argsArray[1] = s1;
        valueLabel.invalidate();
        valueLabel.setText(valueTemplate.format(((Object) (argsArray))));
        valueLabel.validate();
    }

    public void update(Graphics g)
    {
        paint(g);
    }

    private long maxBPS;
    private ValueBar valueBar;
    private Label valueLabel;
    private MessageFormat valueTemplate;
    private Object argsArray[];
    private Label maxLabel;
    private String maxTemplate;
    private static final Insets myInsets = new Insets(2, 2, 2, 2);
    private HJBProperties properties;

}
