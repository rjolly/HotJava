// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   SecurityMonitorImageButton.java

package sunw.hotjava.ui;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.net.URL;
import sunw.hotjava.bean.CurrentDocument;
import sunw.hotjava.bean.HTMLBrowsable;
import sunw.hotjava.misc.HJBProperties;

public class SecurityMonitorImageButton extends Canvas
    implements PropertyChangeListener
{
    private final class ImageButtonMouseListener extends MouseAdapter
    {

        public void mousePressed(MouseEvent mouseevent)
        {
            processMouseEvent(mouseevent);
        }

        public void mouseReleased(MouseEvent mouseevent)
        {
            processMouseEvent(mouseevent);
        }

        public void mouseEntered(MouseEvent mouseevent)
        {
            processMouseEvent(mouseevent);
        }

        public void mouseExited(MouseEvent mouseevent)
        {
            processMouseEvent(mouseevent);
        }

        ImageButtonMouseListener()
        {
        }
    }


    public SecurityMonitorImageButton(String s, HTMLBrowsable htmlbrowsable)
    {
        isSecure = false;
        depressed = false;
        wasDepressed = false;
        props = HJBProperties.getHJBProperties("hjbrowser");
        locked = props.getImage(props.getProperty("button." + s + ".locked"));
        unlocked = props.getImage(props.getProperty("button." + s + ".unlocked"));
        addMouseListener(new ImageButtonMouseListener());
        htmlbrowsable.addPropertyChangeListener(this);
        support = new PropertyChangeSupport(this);
        try
        {
            Class class1 = Class.forName("sunw.hotjava.security.SecurityMonitorListener");
            Object obj = class1.newInstance();
            support.addPropertyChangeListener((PropertyChangeListener)obj);
            addActionListener((ActionListener)obj);
        }
        catch(Exception exception)
        {
            exception.printStackTrace();
        }
        setSize(props.getInteger("button." + s + ".width") + 6, props.getInteger("button." + s + ".height") + 4);
        repaintImage();
    }

    public synchronized void addActionListener(ActionListener actionlistener)
    {
        listeners = AWTEventMulticaster.add(listeners, actionlistener);
    }

    public void propertyChange(PropertyChangeEvent propertychangeevent)
    {
        if(propertychangeevent.getPropertyName().equals("secureConnection"))
        {
            repaintImage();
            support.firePropertyChange("secureConnection", propertychangeevent.getOldValue(), propertychangeevent.getNewValue());
            return;
        }
        if(propertychangeevent.getPropertyName().equals("currentDocument"))
        {
            if(((CurrentDocument)propertychangeevent.getNewValue()).documentURL.getProtocol().equals("https"))
            {
                isSecure = true;
                repaintImage();
            } else
            {
                isSecure = false;
                repaintImage();
            }
            support.firePropertyChange("currentDocument", propertychangeevent.getOldValue(), propertychangeevent.getNewValue());
        }
    }

    public void update(Graphics g)
    {
        Image image = getImage();
        int i = image.getWidth(this);
        int j = image.getHeight(this);
        Dimension dimension = getSize();
        g.setColor(OUTLINE);
        g.drawRect(0, 0, dimension.width - 2, dimension.height - 2);
        g.setColor(HIGHLIGHT);
        g.drawRect(1, 1, dimension.width - 2, dimension.height - 2);
        g.setColor(OVERLAP);
        g.drawLine(1, dimension.height - 2, 1, dimension.height - 2);
        g.drawLine(dimension.width - 2, 1, dimension.width - 2, 1);
        g.drawImage(image, (dimension.width - i) / 2, (dimension.height - j) / 2, getBackground(), this);
    }

    public void paint(Graphics g)
    {
        update(g);
    }

    protected void repaintImage()
    {
        Image image = getImage();
        if(image != old)
        {
            old = image;
            prepareImage(image, this);
            repaint();
        }
    }

    protected Image getImage()
    {
        Image image;
        if(isSecure)
            image = locked;
        else
            image = unlocked;
        return image;
    }

    public void processMouseEvent(MouseEvent mouseevent)
    {
        switch(mouseevent.getID())
        {
        case 503: 
        default:
            break;

        case 501: 
            wasDepressed = true;
            depressed = true;
            break;

        case 502: 
            if(wasDepressed)
            {
                wasDepressed = false;
                if(depressed)
                {
                    depressed = false;
                    ActionEvent actionevent = new ActionEvent(this, 1001, null, mouseevent.getModifiers());
                    listeners.actionPerformed(actionevent);
                    break;
                }
            }
            // fall through

        case 504: 
            if(wasDepressed)
                depressed = true;
            break;

        case 505: 
            if(wasDepressed)
                depressed = false;
            break;
        }
        redispatch(mouseevent);
    }

    private void redispatch(MouseEvent mouseevent)
    {
        mouseevent.getComponent().getParent().dispatchEvent(mouseevent);
    }

    private PropertyChangeSupport support;
    private boolean isSecure;
    private Image locked;
    private Image unlocked;
    private Image old;
    private boolean depressed;
    private boolean wasDepressed;
    private static Color OUTLINE = new Color(102, 102, 102);
    private static Color HIGHLIGHT;
    private static Color OVERLAP = new Color(204, 204, 204);
    private static final int hMargin = 6;
    private static final int vMargin = 4;
    private ActionListener listeners;
    private HJBProperties props;

    static 
    {
        HIGHLIGHT = Color.white;
    }
}
