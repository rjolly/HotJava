// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   HyperLinkListener.java

package sunw.hotjava.ui;

import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.net.URL;
import sunw.hotjava.HJFrame;
import sunw.hotjava.HJWindowManager;
import sunw.hotjava.bean.HTMLBrowsable;
import sunw.hotjava.doc.ElementInfo;

// Referenced classes of package sunw.hotjava.ui:
//            HJPopupMenu, TimedMessage

public class HyperLinkListener
    implements PropertyChangeListener
{

    public HyperLinkListener(HJFrame hjframe)
    {
        newElement = new ElementInfo();
        frame = hjframe;
        HTMLBrowsable htmlbrowsable = hjframe.getHTMLBrowsable();
        htmlbrowsable.addPropertyChangeListener(this);
        popupMenu = new HJPopupMenu(hjframe, hjframe.getActionListener(), hjframe.getDocumentStacker());
    }

    public void propertyChange(PropertyChangeEvent propertychangeevent)
    {
        if(propertychangeevent.getPropertyName().equals("selection"))
            frame.setCursor(0);
        if(propertychangeevent.getPropertyName().equals("indicatedElement"))
        {
            if(propertychangeevent.getNewValue() != null)
            {
                ElementInfo elementinfo = newElement;
                newElement = (ElementInfo)propertychangeevent.getNewValue();
                if(newElement.event.isPopupTrigger())
                    popupMenu.show(newElement);
                if(newElement.hrefURL == null && elementinfo.hrefURL != null)
                {
                    if(frame.getCursor().getType() != 0)
                        frame.setCursor(0);
                    if(newElement.setStatus)
                        frame.showStatus("");
                } else
                if(newElement.hrefURL != null)
                {
                    if(frame.getCursor().getType() != 12)
                        frame.setCursor(12);
                    if(newElement.setStatus)
                        frame.showStatus(newElement.hrefURL);
                }
                if(newElement.altText == null && elementinfo.altText != null)
                {
                    if(message != null)
                    {
                        message.killMessage();
                        message = null;
                        return;
                    }
                } else
                {
                    if(newElement.altText != null && elementinfo.altText == null)
                    {
                        postAlt(newElement);
                        return;
                    }
                    if(newElement.altText != null && elementinfo.altText != null && !newElement.altText.equals(elementinfo.altText))
                    {
                        if(message != null)
                        {
                            message.killMessage();
                            message = null;
                        }
                        postAlt(newElement);
                    }
                }
                return;
            }
            if(message != null)
                message.killMessage();
            if(frame.getCursor().getType() != 0)
                frame.setCursor(0);
            frame.showStatus("");
            return;
        }
        if(propertychangeevent.getPropertyName().equals("handleMailto"))
        {
            HJWindowManager hjwindowmanager = HJWindowManager.getHJWindowManager();
            hjwindowmanager.createNoDecorFrame((URL)propertychangeevent.getNewValue(), hjwindowmanager.getLastFocusHolder()).show();
        }
    }

    private void postAlt(ElementInfo elementinfo)
    {
        try
        {
            Point point = elementinfo.event.getComponent().getLocationOnScreen();
            Point point1 = elementinfo.event.getPoint();
            point.translate(point1.x - 10, point1.y + 15);
            message = new TimedMessage(frame, elementinfo.altText, point, elementinfo.event.getComponent());
            return;
        }
        catch(IllegalComponentStateException _ex)
        {
            return;
        }
    }

    private HJFrame frame;
    private TimedMessage message;
    private ElementInfo newElement;
    private HJPopupMenu popupMenu;
}
