// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ImageToolTip.java

package sunw.hotjava.ui;

import java.awt.Component;
import java.awt.Frame;

// Referenced classes of package sunw.hotjava.ui:
//            TimedMessageOwner, TimedMessage

public class ImageToolTip
    implements TimedMessageOwner
{

    public ImageToolTip(String s)
    {
        message = s;
    }

    public void removeTimedMessage()
    {
        tm = null;
    }

    private Frame findFrame(Component component)
    {
        Object obj = component;
        do
        {
            if(obj instanceof Frame)
                return (Frame)obj;
            obj = ((Component) (obj)).getParent();
        } while(true);
    }

    private String message;
    private TimedMessage tm;
    private static int vertShift = 20;
    private static int horizShift;
    public static final int TOOLTIP_STAY = 0;
    public static final int TOOLTIP_OFF = 1;
    public static final int TOOLTIP_ON = 2;

}
