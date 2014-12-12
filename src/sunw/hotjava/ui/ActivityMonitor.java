// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ActivityMonitor.java

package sunw.hotjava.ui;

import java.awt.*;
import sunw.hotjava.HJFrame;
import sunw.hotjava.bean.HTMLBrowsable;

// Referenced classes of package sunw.hotjava.ui:
//            AnimationIcon

public class ActivityMonitor extends Panel
{

    public ActivityMonitor(HTMLBrowsable htmlbrowsable, HJFrame hjframe, boolean flag)
    {
        AnimationIcon animationicon = new AnimationIcon(flag);
        add(animationicon);
        setLayout(new FlowLayout(1, 0, 0));
        animationicon.addActionListener(hjframe.getActionListener());
    }
}
