// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   TimedMessage.java

package sunw.hotjava.ui;

import java.awt.*;
import sunw.hotjava.misc.HJBProperties;

// Referenced classes of package sunw.hotjava.ui:
//            TimedMessage

class MessageBox extends Window
{

    MessageBox(Frame frame, String s, Point point)
    {
        super(frame);
        properties = HJBProperties.getHJBProperties("hjbrowser");
        font = properties.getFont("hotjava.timedMessage.font", new Font("SansSerif", 0, 10));
        fm = getFontMetrics(font);
        message = s;
        location = point;
        if("".equals(message))
        {
            dispose();
            return;
        } else
        {
            Dimension dimension = measureMessage(s);
            dimension.width += 8;
            dimension.height += 8;
            setEnabled(false);
            setSize(dimension);
            setLocation(point.x, point.y);
            offScreenImage = frame.createImage(dimension.width, dimension.height);
            prepareMessage();
            setBackground(properties.getColor("hotjava.timedMessage.color", Color.yellow));
            setVisible(true);
            return;
        }
    }

    private Dimension measureMessage(String s)
    {
        Dimension dimension = new Dimension();
        dimension.width = fm.stringWidth(s);
        dimension.height = fm.getHeight();
        return dimension;
    }

    private void prepareMessage()
    {
        Graphics g = offScreenImage.getGraphics();
        Dimension dimension = getSize();
        try
        {
            g.setColor(properties.getColor("hotjava.timedMessage.color", Color.yellow));
            g.fillRect(0, 0, dimension.width, dimension.height);
            g.setColor(Color.black);
            g.drawRect(0, 0, dimension.width - 1, dimension.height - 1);
            g.setFont(font);
            g.drawString(message, 4, 4 + fm.getAscent());
        }
        finally
        {
            g.dispose();
        }
    }

    public void paint(Graphics g)
    {
        g.drawImage(offScreenImage, 0, 0, null);
    }

    private HJBProperties properties;
    private String message;
    private Point location;
    private Font font;
    private FontMetrics fm;
    private static final int hMargin = 4;
    private static final int vMargin = 4;
    private Image offScreenImage;
}
