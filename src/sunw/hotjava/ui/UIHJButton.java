// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   UIHJButton.java

package sunw.hotjava.ui;

import java.awt.*;
import java.awt.event.*;
import sunw.hotjava.misc.HJBProperties;

public class UIHJButton extends Canvas
{
    private final class HJButtonMouseListener extends MouseAdapter
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

        HJButtonMouseListener()
        {
        }
    }

    private final class HJButtonFocusListener extends FocusAdapter
    {

        public void focusGained(FocusEvent focusevent)
        {
            processFocusEvent(focusevent);
        }

        public void focusLost(FocusEvent focusevent)
        {
            processFocusEvent(focusevent);
        }

        HJButtonFocusListener()
        {
        }
    }

    private final class HJButtonKeyListener extends KeyAdapter
    {

        public void keyPressed(KeyEvent keyevent)
        {
            processKeyEvent(keyevent);
        }

        public void keyReleased(KeyEvent keyevent)
        {
            processKeyEvent(keyevent);
        }

        public void keyTyped(KeyEvent keyevent)
        {
            processKeyEvent(keyevent);
        }

        HJButtonKeyListener()
        {
        }
    }


    public UIHJButton(String s, boolean flag, HJBProperties hjbproperties)
    {
        hasFocus = false;
        props = hjbproperties;
        setName(s);
        isDefault = flag;
        baseName = getBaseName(getName());
        action = hjbproperties.getProperty("button." + s + ".action");
        if(action == null)
            action = hjbproperties.getProperty("button." + s + ".text");
        addMouseListener(new HJButtonMouseListener());
        addFocusListener(new HJButtonFocusListener());
        addKeyListener(new HJButtonKeyListener());
        font = getButtonFont(hjbproperties, baseName);
        Toolkit.getDefaultToolkit().getFontMetrics(font);
        labelSize = null;
        labelSize = getLabelSize();
        btnSize = new Dimension(labelSize.width + 23, labelSize.height + 10);
        setSize(btnSize);
        buttons = new Image[6];
        setFont(font);
        setBackground(new Color(0xd4d4d4));
    }

    public UIHJButton(String s, HJBProperties hjbproperties)
    {
        this(s, false, hjbproperties);
    }

    private static String getBaseName(String s)
    {
        int i = s.lastIndexOf(".");
        if(i >= 0)
            return s.substring(0, i);
        else
            return null;
    }

    private static Font getButtonFont(HJBProperties hjbproperties, String s)
    {
        String s1 = hjbproperties.getProperty("button." + s + ".font");
        if(s1 != null)
            return Font.decode(s1);
        else
            return Font.decode("hotjava.font");
    }

    public String getActionCommand()
    {
        return action;
    }

    public void setActionCommand(String s)
    {
        action = s;
    }

    private String getLabel(String s)
    {
        String s1 = null;
        if(label == null)
        {
            s1 = "button." + s + ".text";
            label = props.getProperty(s1);
        }
        if(label != null)
            return label;
        else
            return s1;
    }

    public Dimension getLabelSize()
    {
        if(labelSize == null)
        {
            labelSize = new Dimension();
            Font font1 = null;
            font1 = getButtonFont(props, baseName);
            FontMetrics fontmetrics = Toolkit.getDefaultToolkit().getFontMetrics(font1);
            labelSize.width = fontmetrics.stringWidth(props.getProperty("button." + getName() + ".text", "button." + getName() + ".text"));
            labelSize.height = fontmetrics.getHeight();
        }
        return labelSize;
    }

    public void setLabelSize(int i, int j)
    {
        if(labelSize == null)
        {
            labelSize = new Dimension(i, j);
        } else
        {
            labelSize.width = i;
            labelSize.height = j;
        }
        btnSize.width = i + 23;
        btnSize.height = j + 10;
        setSize(btnSize);
    }

    public void processMouseEvent(MouseEvent mouseevent)
    {
        switch(mouseevent.getID())
        {
        case 501: 
            isPressed = true;
            wasPressed = true;
            requestFocus();
            repaintImage();
            return;

        case 502: 
            wasPressed = false;
            if(isPressed)
            {
                isPressed = false;
                try
                {
                    dispatchActionEvent(mouseevent, action);
                }
                catch(Exception exception)
                {
                    exception.printStackTrace();
                }
            }
            repaintImage();
            return;

        case 505: 
            if(wasPressed)
                isPressed = wasPressed = false;
            repaintImage();
            return;

        case 503: 
        case 504: 
        default:
            return;
        }
    }

    public boolean isDefault()
    {
        return isDefault;
    }

    public void setDefaultButton(boolean flag)
    {
        isDefault = flag;
    }

    public void processKeyEvent(KeyEvent keyevent)
    {
        if(keyevent.getID() == 401 && keyevent.getKeyCode() == 10)
        {
            ActionEvent actionevent = new ActionEvent(this, 0, action);
            if(listeners != null)
                listeners.actionPerformed(actionevent);
            return;
        } else
        {
            return;
        }
    }

    public void processFocusEvent(FocusEvent focusevent)
    {
        switch(focusevent.getID())
        {
        case 1004: 
            hasFocus = true;
            repaintImage();
            return;

        case 1005: 
            hasFocus = false;
            repaintImage();
            return;
        }
    }

    protected void repaintImage()
    {
        getImage();
        repaint();
    }

    public Image createButtonImage(int i)
    {
        Image image = null;
        Dimension dimension = getSize();
        java.awt.Container container = getParent();
        if(container != null)
        {
            Color color = container.getBackground();
            float af[] = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
            Color color1 = new Color(af[2], af[2], af[2]);
            setBackground(color1);
        }
        image = createImage(dimension.width, dimension.height);
        if(image != null)
        {
            Graphics g = image.getGraphics();
            try
            {
                g.setColor(getBackground());
                g.fillRect(1, 1, dimension.width - 2, dimension.height - 2);
                if(isDefault)
                {
                    g.setColor(HIGHLIGHT);
                    g.drawRect(2, 2, dimension.width - 3, dimension.height - 3);
                    g.setColor(OUTLINE);
                    g.drawRect(0, 0, dimension.width - 2, dimension.height - 2);
                    g.drawRect(1, 1, dimension.width - 4, dimension.height - 4);
                    g.setColor(getBackground());
                    g.drawLine(2, dimension.height - 3, 2, dimension.height - 3);
                    g.drawLine(dimension.width - 3, 2, dimension.width - 3, 2);
                    if(i != 0)
                    {
                        if(i == 1)
                        {
                            g.setColor(Color.gray);
                            g.fillRect(2, 2, dimension.width - 5, dimension.height - 5);
                            g.drawLine(2, dimension.height - 3, 2, dimension.height - 3);
                            g.drawLine(dimension.width - 3, 2, dimension.width - 3, 2);
                        }
                        g.setColor(FOCUS);
                        g.drawRect(6, 3, dimension.width - 14, dimension.height - 8);
                    }
                } else
                {
                    g.setColor(OUTLINE);
                    g.drawRect(0, 0, dimension.width - 2, dimension.height - 2);
                    g.setColor(HIGHLIGHT);
                    g.drawRect(1, 1, dimension.width - 2, dimension.height - 2);
                    g.setColor(getBackground());
                    g.drawLine(1, dimension.height - 2, 1, dimension.height - 2);
                    g.drawLine(dimension.width - 2, 1, dimension.width - 2, 1);
                    if(i != 3)
                    {
                        if(i == 4)
                        {
                            g.setColor(Color.gray);
                            g.fillRect(1, 1, dimension.width - 3, dimension.height - 3);
                            g.drawLine(1, dimension.height - 2, 1, dimension.height - 2);
                            g.drawLine(dimension.width - 2, 1, dimension.width - 2, 1);
                        }
                        g.setColor(FOCUS);
                        g.drawRect(6, 3, dimension.width - 14, dimension.height - 8);
                    }
                }
                FontMetrics fontmetrics = g.getFontMetrics();
                int j = (dimension.width - fontmetrics.stringWidth(getLabel(getName()))) / 2;
                int k = dimension.height - fontmetrics.getDescent() - 5;
                g.setColor(Color.black);
                g.drawString(getLabel(getName()), j, k);
            }
            finally
            {
                g.dispose();
            }
        }
        return image;
    }

    public Image getImage()
    {
        Image image;
        if(isDefault)
        {
            if(buttons[0] == null)
                buttons[0] = createButtonImage(0);
            image = buttons[0];
            if(hasFocus)
            {
                if(buttons[2] == null)
                    buttons[2] = createButtonImage(2);
                image = buttons[2];
            }
            if(isPressed)
            {
                if(buttons[1] == null)
                    buttons[1] = createButtonImage(1);
                image = buttons[1];
            }
        } else
        {
            if(buttons[3] == null)
                buttons[3] = createButtonImage(3);
            image = buttons[3];
            if(hasFocus)
            {
                if(buttons[5] == null)
                    buttons[5] = createButtonImage(5);
                image = buttons[5];
            }
            if(isPressed)
            {
                if(buttons[4] == null)
                    buttons[4] = createButtonImage(4);
                image = buttons[4];
            }
        }
        return image;
    }

    public void paint(Graphics g)
    {
        update(g);
    }

    public void update(Graphics g)
    {
        Image image = getImage();
        g.drawImage(image, 0, 0, getBackground(), this);
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

    public Dimension getPreferredSize()
    {
        return btnSize;
    }

    public Dimension getMaximumSize()
    {
        return btnSize;
    }

    public Dimension getMinimumSize()
    {
        return btnSize;
    }

    public Dimension getSize()
    {
        return btnSize;
    }

    public boolean isFocusTraversable()
    {
        return true;
    }

    public static Color OUTLINE = new Color(102, 102, 102);
    public static Color HIGHLIGHT;
    public static Color OVERLAP = new Color(204, 204, 204);
    public static Color FOCUS = new Color(102, 102, 153);
    private static final int DEFAULT = 0;
    private static final int DEFAULT_FP = 1;
    private static final int DEFAULT_F = 2;
    private static final int NORMAL = 3;
    private static final int NORMAL_FP = 4;
    private static final int NORMAL_F = 5;
    private String baseName;
    private Image buttons[];
    private boolean isPressed;
    private boolean wasPressed;
    private boolean hasFocus;
    private boolean isDefault;
    private static final int vMargin = 10;
    private static final int hMargin = 23;
    private Dimension btnSize;
    private HJBProperties props;
    private ActionListener listeners;
    private String label;
    private String action;
    private Font font;
    private Dimension labelSize;

    static 
    {
        HIGHLIGHT = Color.white;
    }
}
