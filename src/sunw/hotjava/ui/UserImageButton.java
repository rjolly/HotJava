// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   UserImageButton.java

package sunw.hotjava.ui;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.ImageObserver;
import sunw.hotjava.misc.HJBProperties;

// Referenced classes of package sunw.hotjava.ui:
//            TimedMessageContainer, UpdateableComponent

public class UserImageButton extends Canvas
    implements UpdateableComponent
{
    private final class UserImageButtonMouseListener extends MouseAdapter
        implements MouseMotionListener
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

        public void mouseDragged(MouseEvent mouseevent)
        {
            processMouseEvent(mouseevent);
        }

        public void mouseMoved(MouseEvent mouseevent)
        {
            processMouseEvent(mouseevent);
        }

        UserImageButtonMouseListener()
        {
        }
    }


    public UserImageButton(String s)
    {
        enabled = true;
        btnImgs = new Image[5];
        imagesLoaded = false;
        labelInitialized = false;
        properties = HJBProperties.getHJBProperties("hjbrowser");
        labelSize = new Dimension();
        imageSize = new Dimension();
        setName(s);
        if(style == 0)
        {
            String s1 = properties.getProperty("hotjava.buttonappearance", "ImageAndText");
            style = propToBtnType(s1);
        }
        if(style == 1 || style == 3)
        {
            labelSize = getLabel();
            if(maxLabelSize.width < labelSize.width)
                maxLabelSize.width = labelSize.width;
        }
        imageSize = loadImages();
        if(style == 1 || style == 2)
        {
            if(maxImageSize.width < imageSize.width)
                maxImageSize.width = imageSize.width;
            if(maxImageSize.height < imageSize.height)
                maxImageSize.height = imageSize.height;
        }
        action = properties.getProperty("button." + s + ".action", s);
        int i = (maxLabelSize.width <= imageSize.width ? imageSize.width : maxLabelSize.width) + 8;
        int j = imageSize.height + labelSize.height + 8;
        setSize(i, j);
        Color color = properties.getColor(s + ".background", null);
        if(color != null)
            setBackground(color);
        setCursor(Cursor.getPredefinedCursor(12));
        addMouseListener(new UserImageButtonMouseListener());
        addMouseMotionListener(new UserImageButtonMouseListener());
        FocusAdapter focusadapter = new FocusAdapter() {

            public void focusLost(FocusEvent focusevent)
            {
                processLostFocusEvent(focusevent);
            }

        }
;
        addFocusListener(focusadapter);
    }

    private int propToBtnType(String s)
    {
        if(s.equals("TextOnly"))
            return 3;
        return !s.equals("ImageOnly") ? 1 : 2;
    }

    public void updateAppearance()
    {
        String s = properties.getProperty("hotjava.buttonappearance", "ImageAndText");
        style = propToBtnType(s);
        if(style == 3 || style == 1)
        {
            labelSize = getLabel();
            if(maxLabelSize.width < labelSize.width)
                maxLabelSize.width = labelSize.width;
        }
        if(style == 2 || style == 1)
        {
            imageSize = loadImages();
            if(maxImageSize.width < imageSize.width)
                maxImageSize.width = imageSize.width;
            if(maxImageSize.height < imageSize.height)
                maxImageSize.height = imageSize.height;
        }
        for(int i = 0; i < 5; i++)
            btnImgs[i] = null;

    }

    private Dimension loadImages()
    {
        if(!imagesLoaded)
        {
            imagesLoaded = true;
            Dimension dimension = new Dimension();
            String s = getName();
            dimension.width = properties.getInteger("button." + s + ".width", 20);
            dimension.height = properties.getInteger("button." + s + ".height", 20);
            tracker = new MediaTracker(this);
            active = properties.getImage(properties.getProperty("button." + s + ".up"));
            if(active != null)
                tracker.addImage(active, 1);
            dis = properties.getImage(properties.getProperty("button." + s + ".disabled"));
            if(dis != null)
                tracker.addImage(dis, 3);
            over = properties.getImage(properties.getProperty("button." + s + ".mouseover"));
            if(over != null)
                tracker.addImage(over, 4);
            try
            {
                tracker.waitForAll();
            }
            catch(InterruptedException _ex) { }
            return dimension;
        } else
        {
            return imageSize;
        }
    }

    private Dimension getLabel()
    {
        if(!labelInitialized)
        {
            labelInitialized = true;
            Dimension dimension = new Dimension();
            if(fm == null)
            {
                Toolkit toolkit = Toolkit.getDefaultToolkit();
                font = properties.getFont("hotjava.buttonlabel.font", new Font("SansSerif", 0, 8));
                fm = toolkit.getFontMetrics(font);
                maxLabelSize.height = fm.getHeight();
            }
            label = properties.getProperty("button." + getName() + ".label", "");
            dimension.width = fm.stringWidth(label);
            dimension.height = maxLabelSize.height;
            return dimension;
        } else
        {
            return labelSize;
        }
    }

    public Image createButtonImage(Image image, boolean flag)
    {
        Image image1 = null;
        Dimension dimension = getSize();
        image1 = createImage(dimension.width, dimension.height);
        if(image1 != null)
        {
            Graphics g = image1.getGraphics();
            try
            {
                int i1 = 0;
                int j1 = 0;
                if(image != null)
                {
                    i1 = image.getWidth(this);
                    j1 = image.getHeight(this);
                }
                if(depressed)
                {
                    g.setColor(Color.gray);
                    g.fillRect(1, 1, dimension.width - 2, dimension.height - 2);
                } else
                {
                    g.setColor(getBackground());
                    g.fillRect(0, 0, dimension.width, dimension.height);
                }
                if(flag)
                {
                    g.setColor(OUTLINE);
                    g.drawRect(0, 0, dimension.width - 2, dimension.height - 2);
                    g.setColor(HIGHLIGHT);
                    g.drawRect(1, 1, dimension.width - 2, dimension.height - 2);
                    g.setColor(OVERLAP);
                    g.drawLine(1, dimension.height - 2, 1, dimension.height - 2);
                    g.drawLine(dimension.width - 2, 1, dimension.width - 2, 1);
                }
                g.setColor(Color.black);
                g.setFont(font);
                g.setColor(Color.black);
                switch(style)
                {
                case 1: // '\001'
                    int i = (dimension.width - fm.stringWidth(label)) / 2;
                    int k = dimension.height - fm.getDescent() - 4;
                    if(!isButtonEnabled())
                    {
                        g.setColor(Color.white);
                        g.drawString(label, i + 1, k + 1);
                        g.setColor(Color.gray);
                    }
                    g.drawString(label, i, k);
                    if(image != null)
                        g.drawImage(image, (dimension.width - i1) / 2, (k - fm.getAscent() - j1) / 2, this);
                    break;

                case 3: // '\003'
                    int j = (dimension.width - fm.stringWidth(label)) / 2;
                    int l = dimension.height - fm.getDescent() - 4;
                    if(!isButtonEnabled())
                    {
                        g.setColor(Color.white);
                        g.drawString(label, j + 1, l + 1);
                        g.setColor(Color.gray);
                    }
                    g.drawString(label, j, l);
                    break;

                case 2: // '\002'
                    if(image != null)
                        g.drawImage(image, (dimension.width - i1) / 2, (dimension.height - j1) / 2, this);
                    break;
                }
            }
            finally
            {
                g.dispose();
            }
        }
        return image1;
    }

    public synchronized void addActionListener(ActionListener actionlistener)
    {
        listeners = AWTEventMulticaster.add(listeners, actionlistener);
    }

    public synchronized void removeActionListener(ActionListener actionlistener)
    {
        listeners = AWTEventMulticaster.remove(listeners, actionlistener);
    }

    protected void dispatchActionEvent(MouseEvent mouseevent, String s)
    {
        ActionEvent actionevent = new ActionEvent(this, 1001, s, mouseevent.getModifiers());
        if(listeners != null)
            listeners.actionPerformed(actionevent);
    }

    private void redispatch(MouseEvent mouseevent)
    {
        Point point = mouseevent.getComponent().getLocation();
        mouseevent.translatePoint(point.x, point.y);
        if(getParent() instanceof TimedMessageContainer)
        {
            ((TimedMessageContainer)getParent()).processMouseEvent(mouseevent);
            return;
        } else
        {
            mouseevent.getComponent().getParent().dispatchEvent(mouseevent);
            return;
        }
    }

    private void redispatch(FocusEvent focusevent)
    {
        focusevent.getComponent().getParent().dispatchEvent(focusevent);
    }

    public void processLostFocusEvent(FocusEvent focusevent)
    {
        redispatch(focusevent);
    }

    public void processMouseEvent(MouseEvent mouseevent)
    {
        switch(mouseevent.getID())
        {
        case 503: 
        default:
            break;

        case 501: 
            if(isButtonEnabled())
                wasDepressed = depressed = true;
            repaintImage();
            break;

        case 502: 
            if(isButtonEnabled())
            {
                wasDepressed = false;
                if(depressed)
                {
                    depressed = false;
                    try
                    {
                        dispatchActionEvent(mouseevent, action);
                    }
                    catch(Exception exception)
                    {
                        exception.printStackTrace();
                    }
                }
            }
            repaintImage();
            break;

        case 504: 
            entered = true;
            if(wasDepressed)
                depressed = true;
            repaintImage();
            try
            {
                dispatchActionEvent(mouseevent, "entered " + action);
            }
            catch(Exception exception1)
            {
                exception1.printStackTrace();
            }
            break;

        case 505: 
            entered = false;
            if(wasDepressed)
                depressed = false;
            repaintImage();
            try
            {
                dispatchActionEvent(mouseevent, "");
            }
            catch(Exception exception2)
            {
                exception2.printStackTrace();
            }
            break;
        }
        redispatch(mouseevent);
    }

    private void setNewSize()
    {
        switch(style)
        {
        case 1: // '\001'
            int i = (maxLabelSize.width <= maxImageSize.width ? maxImageSize.width : maxLabelSize.width) + 8;
            super.setSize(i, fm.getHeight() + maxImageSize.height + 8);
            return;

        case 3: // '\003'
            super.setSize(maxLabelSize.width + 8, maxLabelSize.height + 8);
            return;

        case 2: // '\002'
            super.setSize(imageSize.width + 8, maxImageSize.height + 8);
            return;
        }
    }

    public Dimension getSize()
    {
        setNewSize();
        return super.getSize();
    }

    public Dimension getMinimumSize()
    {
        return getSize();
    }

    public Dimension getPreferredSize()
    {
        return getSize();
    }

    public void paint(Graphics g)
    {
        update(g);
    }

    protected void repaintImage()
    {
        Image image = getImage();
        if(image != null && image != old)
        {
            old = image;
            prepareImage(image, this);
            repaint();
        }
    }

    protected Image getImage()
    {
        Object obj = null;
        Image image;
        if(isButtonEnabled())
        {
            if(depressed)
            {
                if(btnImgs[1] == null)
                    btnImgs[1] = createButtonImage(active, false);
                image = btnImgs[1];
            } else
            if(entered)
            {
                if(btnImgs[3] == null)
                {
                    Image image1 = over != null ? over : active;
                    btnImgs[3] = createButtonImage(image1, true);
                }
                image = btnImgs[3];
            } else
            {
                if(btnImgs[0] == null)
                    btnImgs[0] = createButtonImage(active, false);
                image = btnImgs[0];
            }
        } else
        {
            if(btnImgs[2] == null)
            {
                Image image2 = dis != null ? dis : active;
                btnImgs[2] = createButtonImage(image2, false);
            }
            image = btnImgs[2];
        }
        return image;
    }

    public void update(Graphics g)
    {
        Image image = getImage();
        g.drawImage(image, 0, 0, getBackground(), this);
    }

    public boolean imageUpdate(Image image, int i, int j, int k, int l, int i1)
    {
        Image image1 = getImage();
        if(image == image1 && (i & 0x38) != 0)
            repaint((i & 0x30) != 0 ? 0 : 100);
        return image == image1 && (i & 0xa0) == 0;
    }

    public void setEnabled(boolean flag)
    {
        if(isButtonEnabled() != flag)
        {
            enabled = flag;
            byte byte0 = ((byte)(flag ? 12 : 0));
            setCursor(Cursor.getPredefinedCursor(byte0));
            repaintImage();
        }
    }

    public boolean isButtonEnabled()
    {
        return enabled;
    }

    private boolean depressed;
    private boolean wasDepressed;
    private boolean entered;
    private Image active;
    private Image dis;
    private Image over;
    private Image old;
    private String action;
    private boolean enabled;
    private ActionListener listeners;
    private static Font font;
    private static int maxSize;
    private static FontMetrics fm;
    private static final int NUM_IMGS = 5;
    private Image btnImgs[];
    public static Color OUTLINE = new Color(102, 102, 102);
    public static Color HIGHLIGHT;
    public static Color OVERLAP = new Color(204, 204, 204);
    private String label;
    MediaTracker tracker;
    private boolean imagesLoaded;
    private boolean labelInitialized;
    protected HJBProperties properties;
    private Dimension labelSize;
    private static Dimension maxLabelSize = new Dimension();
    private static Dimension maxImageSize = new Dimension();
    private Dimension imageSize;
    private static int style;
    private static final int TEXTIMAGE = 1;
    private static final int IMAGEONLY = 2;
    private static final int TEXTONLY = 3;
    private static final int vMargin = 8;
    private static final int hMargin = 8;

    static 
    {
        HIGHLIGHT = Color.white;
    }
}
