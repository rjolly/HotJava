// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   TimedMessage.java

package sunw.hotjava.ui;

import java.awt.*;
import java.awt.event.MouseEvent;
import sunw.hotjava.misc.HJBProperties;
import sunw.hotjava.misc.RequestProcessor;

// Referenced classes of package sunw.hotjava.ui:
//            MessageBox, TimedMessageOwner, ImageToolTip

public class TimedMessage
{
    private class TimedMessageRequest extends sunw.hotjava.misc.RequestProcessor.Request
    {

        public void execute()
        {
            checkTime();
        }

        TimedMessageRequest()
        {
        }
    }

    private class MessageBoxKillRequest extends sunw.hotjava.misc.RequestProcessor.Request
    {

        public void execute()
        {
            if(!finishTiming)
            {
                finishTiming = true;
                hideMessageBox();
                if(messageOwner != null)
                    messageOwner.removeTimedMessage();
            }
        }

        MessageBoxKillRequest()
        {
        }
    }


    public TimedMessage(Frame frame, String s, Point point, Component component)
    {
        showing = false;
        finishTiming = false;
        properties = HJBProperties.getHJBProperties("hjbrowser");
        parentFrame = frame;
        message = s;
        location = point;
        initiator = component;
        if(component instanceof TimedMessageOwner)
            messageOwner = (TimedMessageOwner)component;
        origPos = component.getLocationOnScreen();
        String s1 = properties.getProperty("hotjava.timedMessage.delay", "1");
        waitTime = getMillisecondProperty(s1, 1000L);
        setTargetTime();
        RequestProcessor.getHJBeanQueue().postRequest(new TimedMessageRequest(), 50);
    }

    public TimedMessage(Frame frame, String s, Point point, Component component, ImageToolTip imagetooltip)
    {
        this(frame, s, point, component);
        messageOwner = imagetooltip;
    }

    public void processMouseEvent(MouseEvent mouseevent)
    {
        switch(mouseevent.getID())
        {
        case 501: 
        case 502: 
            killMessage();
            return;

        case 503: 
        case 506: 
            setTargetTime();
            return;

        case 505: 
            killMessage();
            return;

        case 504: 
        default:
            return;
        }
    }

    public void killMessage()
    {
        finishTiming = true;
        hideMessageBox();
        if(messageOwner != null)
            messageOwner.removeTimedMessage();
    }

    public void checkTime()
    {
        boolean flag = false;
        try
        {
            boolean flag1;
            synchronized(this)
            {
                boolean flag2 = System.currentTimeMillis() >= targetTime;
                flag1 = !showing && flag2;
                if(initiator.isShowing())
                {
                    Point point = initiator.getLocationOnScreen();
                    if(!point.equals(origPos))
                    {
                        finishTiming = true;
                        hideMessageBox();
                    }
                }
            }
            if(!finishTiming && flag1)
                showMessageBox();
            if(!finishTiming)
                RequestProcessor.getHJBeanQueue().postRequest(new TimedMessageRequest(), 50);
        }
        finally
        {
            if(finishTiming)
                hideMessageBox();
        }
    }

    private synchronized long getMillisecondProperty(String s, long l)
    {
        float f = 1.0F;
        try
        {
            f = Float.valueOf(s).floatValue();
        }
        catch(NumberFormatException _ex)
        {
            return l;
        }
        return (long)(f * 1000F);
    }

    private synchronized void showMessageBox()
    {
        showing = true;
        messageBox = new MessageBox(parentFrame, message, location);
        if(isJavaOS)
            messageBox.toFront();
        String s = properties.getProperty("hotjava.timedMessage.destroydelay", "5");
        long l = getMillisecondProperty(s, 5000L);
        if(l > 0L)
            RequestProcessor.getHJBeanQueue().postRequest(new MessageBoxKillRequest(), (int)l);
    }

    private synchronized void hideMessageBox()
    {
        showing = false;
        if(messageBox != null)
        {
            messageBox.setVisible(false);
            messageBox.dispose();
            messageBox = null;
        }
    }

    private synchronized void setTargetTime()
    {
        targetTime = System.currentTimeMillis() + waitTime;
    }

    private Frame parentFrame;
    private String message;
    private Point location;
    private long waitTime;
    private long targetTime;
    private Point origPos;
    private Component initiator;
    private TimedMessageOwner messageOwner;
    private MessageBox messageBox;
    private boolean showing;
    private Thread thread;
    private boolean finishTiming;
    private HJBProperties properties;
    private static boolean isJavaOS;

    static 
    {
        try
        {
            isJavaOS = System.getProperty("os.name").equalsIgnoreCase("javaos");
        }
        catch(Exception _ex) { }
    }




}
