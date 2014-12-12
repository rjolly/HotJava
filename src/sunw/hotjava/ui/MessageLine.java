// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   MessageLine.java

package sunw.hotjava.ui;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Observable;
import java.util.Observer;
import sun.net.ProgressData;
import sun.net.ProgressEntry;
import sunw.hotjava.HJFrame;
import sunw.hotjava.misc.HJBProperties;
import sunw.hotjava.misc.RequestProcessor;

// Referenced classes of package sunw.hotjava.ui:
//            Message, MessageRequest, SecurityMonitorImageButton

public class MessageLine extends Panel
    implements Observer, PropertyChangeListener
{

    public MessageLine(HJFrame hjframe)
    {
        properties = HJBProperties.getHJBProperties("hjbrowser");
        fetching = properties.getProperty("hotjava.fetching");
        loadFailed = properties.getProperty("hotjava.loadfailed");
        contacting = properties.getProperty("hotjava.contacting.msg");
        contacted = properties.getProperty("hotjava.contacted.msg");
        done = properties.getProperty("hotjava.readingdone.msg");
        hjFrame = hjframe;
        setLayout(new BorderLayout());
        if(hjFrame.hasSSL())
        {
            monitor = new SecurityMonitorImageButton("security.state.monitor", hjFrame.getHTMLBrowsable());
            add("West", monitor);
        }
        add("Center", message = new Message());
        KeyAdapter keyadapter = new KeyAdapter() {

            public void keyTyped(KeyEvent keyevent)
            {
                if(keyevent.isActionKey())
                    processKeyActionEvent(keyevent);
            }

        }
;
        addKeyListener(keyadapter);
    }

    public void setMessage(String s)
    {
        message.setText(s);
    }

    public String getMessage()
    {
        return message.getText();
    }

    public void setColor(Color color)
    {
        int i = countComponents();
        if(i > 0)
        {
            Component acomponent[] = getComponents();
            for(int j = 0; j < acomponent.length; j++)
            {
                acomponent[j].setBackground(color);
                Graphics g = acomponent[j].getGraphics();
                if(g != null)
                {
                    acomponent[j].paint(g);
                    g.dispose();
                }
                if(acomponent[j] instanceof Container)
                {
                    Component acomponent1[] = ((Container)acomponent[j]).getComponents();
                    for(int k = 0; k < acomponent1.length; k++)
                    {
                        acomponent1[k].setBackground(color);
                        Graphics g1 = acomponent1[k].getGraphics();
                        if(g1 != null)
                        {
                            acomponent1[k].paint(g1);
                            g1.dispose();
                        }
                    }

                }
            }

        }
    }

    public void addNotify()
    {
        super.addNotify();
        ProgressData.pdata.addObserver(this);
    }

    public void removeNotify()
    {
        ProgressData.pdata.deleteObserver(this);
        super.removeNotify();
    }

    public void propertyChange(PropertyChangeEvent propertychangeevent)
    {
        if(propertychangeevent.getPropertyName().equals("loadingProgress"))
        {
            int i = Math.round(((Double)propertychangeevent.getNewValue()).floatValue() * 100F);
            if(i > 100)
                i = 100;
            statusmsg = properties.getPropertyReplace("hotjava.progress.msg", Integer.toString(i));
            RequestProcessor.getHJBeanQueue().postRequest(new MessageRequest(this, statusmsg));
        }
    }

    public synchronized void update(Observable observable, Object obj)
    {
        if(!isVisible())
            return;
        ProgressEntry progressentry = (ProgressEntry)obj;
        try
        {
            switch(progressentry.what)
            {
            case 0: // '\0'
                RequestProcessor.getHJBeanQueue().postRequest(new MessageRequest(this, contacting));
                return;

            case 1: // '\001'
                RequestProcessor.getHJBeanQueue().postRequest(new MessageRequest(this, contacted));
                return;

            case 3: // '\003'
                RequestProcessor.getHJBeanQueue().postRequest(new MessageRequest(this, done));
                return;
            }
        }
        catch(Exception exception)
        {
            exception.printStackTrace();
        }
    }

    public void processKeyActionEvent(KeyEvent keyevent)
    {
        switch(keyevent.getKeyCode())
        {
        case 33: // '!'
        case 34: // '"'
        case 35: // '#'
        case 36: // '$'
        case 37: // '%'
        case 38: // '&'
        case 39: // '\''
        case 40: // '('
        default:
            return;
        }
    }

    SecurityMonitorImageButton monitor;
    Message message;
    private HJBProperties properties;
    String fetching;
    String loadFailed;
    private String contacting;
    private String contacted;
    private String done;
    private String statusmsg;
    HJFrame hjFrame;
}
