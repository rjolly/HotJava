// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   TimedMessageContainer.java

package sunw.hotjava.ui;

import java.awt.*;
import java.awt.event.MouseEvent;

// Referenced classes of package sunw.hotjava.ui:
//            DelegatingPanel, TimedMessage, TimedMessageOwner

public class TimedMessageContainer extends DelegatingPanel
    implements TimedMessageOwner
{

    TimedMessageContainer(String s)
    {
        message = s;
    }

    public void processMouseEvent(MouseEvent mouseevent)
    {
        switch(mouseevent.getID())
        {
        default:
            break;

        case 504: 
            if(tm == null)
            {
                tm = makeNewMessage();
                return;
            }
            break;

        case 501: 
        case 502: 
        case 503: 
        case 505: 
        case 506: 
            if(tm != null)
            {
                tm.processMouseEvent(mouseevent);
                return;
            }
            break;
        }
    }

    public void removeTimedMessage()
    {
        tm = null;
    }

    private TimedMessage makeNewMessage()
    {
        Rectangle rectangle = getBounds();
        int i = rectangle.width / 2;
        int j = rectangle.height + 5;
        Frame frame = findFrame();
        Point point = new Point(i, j);
        Point point1 = getLocationOnScreen();
        point.x += point1.x;
        point.y += point1.y;
        return new TimedMessage(frame, message, point, this);
    }

    private Frame findFrame()
    {
        Object obj = this;
        do
        {
            if(obj instanceof Frame)
                return (Frame)obj;
            obj = ((Component) (obj)).getParent();
        } while(true);
    }

    private String message;
    private TimedMessage tm;
    private static final int vertDisplacement = 5;
}
