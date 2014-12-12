// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ComboBox.java

package sunw.hotjava.forms;

import java.awt.*;
import java.awt.event.*;
import java.io.PrintStream;
import java.util.Vector;

// Referenced classes of package sunw.hotjava.forms:
//            HighlightFilter

public class ComboBox extends Canvas
    implements MouseListener, ActionListener, ItemSelectable
{

    public ComboBox()
    {
        elemVector = new Vector();
        dirty = true;
        name = "";
        super.setName(name);
        addMouseListener(this);
        enableEvents(16L);
    }

    public void addNotify()
    {
        super.addNotify();
        calculateSize();
        if(focusColor == null || topShadowColor == null || bottomShadowColor == null)
            setComponentColor();
    }

    protected void setLabel(String s)
    {
        name = s;
        calculateSize();
        repaint();
    }

    protected void updateLabel()
    {
        if(elemVector.size() >= 1)
            setLabel((String)elemVector.elementAt(currIndex));
    }

    public synchronized void add(String s)
    {
        dirty = true;
        elemVector.addElement(s);
        updateLabel();
    }

    public synchronized void insert(String s, int i)
    {
        dirty = true;
        elemVector.insertElementAt(s, i);
        if(i == currIndex)
            updateLabel();
    }

    public synchronized void remove(int i)
    {
        dirty = true;
        try
        {
            elemVector.removeElementAt(i);
            if(i == currIndex)
            {
                updateLabel();
                return;
            }
        }
        catch(ArrayIndexOutOfBoundsException _ex) { }
    }

    public synchronized void remove(String s)
    {
        dirty = true;
        elemVector.removeElement(s);
        updateLabel();
    }

    public synchronized void removeAll()
    {
        dirty = true;
        elemVector.removeAllElements();
        elemVector.trimToSize();
        setLabel(" ");
    }

    public String getItem(int i)
    {
        String s = null;
        try
        {
            s = (String)elemVector.elementAt(i);
        }
        catch(ArrayIndexOutOfBoundsException _ex)
        {
            s = null;
        }
        return s;
    }

    public synchronized String getSelectedItem()
    {
        return currSelection;
    }

    public synchronized int getSelectedIndex()
    {
        return currIndex;
    }

    public int getItemCount()
    {
        return elemVector.size();
    }

    public synchronized void select(int i)
    {
        try
        {
            String s = (String)elemVector.elementAt(i);
            currSelection = s;
            setLabel(s);
            currIndex = i;
            return;
        }
        catch(ArrayIndexOutOfBoundsException _ex) { }
        if(elemVector.size() == 0)
        {
            currSelection = null;
            currIndex = 0;
            setLabel(" ");
        }
    }

    public synchronized void select(String s)
    {
        int i = elemVector.indexOf(s);
        if(i != -1)
            select(i);
    }

    public void mouseReleased(MouseEvent mouseevent)
    {
    }

    public void mouseEntered(MouseEvent mouseevent)
    {
    }

    public void mouseExited(MouseEvent mouseevent)
    {
    }

    public void mouseClicked(MouseEvent mouseevent)
    {
    }

    public void mousePressed(MouseEvent mouseevent)
    {
        showMenu(mouseevent.getX(), mouseevent.getY());
    }

    public synchronized void addItemListener(ItemListener itemlistener)
    {
        itemListener = AWTEventMulticaster.add(itemListener, itemlistener);
    }

    public synchronized Object[] getSelectedObjects()
    {
        Object aobj[] = new Object[1];
        aobj[0] = getSelectedItem();
        return aobj;
    }

    public synchronized void removeItemListener(ItemListener itemlistener)
    {
        itemListener = AWTEventMulticaster.remove(itemListener, itemlistener);
    }

    public void actionPerformed(ActionEvent actionevent)
    {
        setLabel(actionevent.getActionCommand());
        currSelection = actionevent.getActionCommand();
        currIndex = elemVector.indexOf(currSelection);
        ItemEvent itemevent = new ItemEvent(this, 701, currSelection, 1);
        processItemEvent(itemevent);
    }

    protected void processItemEvent(ItemEvent itemevent)
    {
        if(itemListener != null)
            itemListener.itemStateChanged(itemevent);
    }

    private void showMenu(int i, int j)
    {
        pr("showMenu() menu=" + menu + ", item cnt=" + getItemCount());
        if(getItemCount() <= 0)
            return;
        if(dirty)
            updateEntries();
        int k = i;
        int l = j;
        Dimension dimension = getSize();
        Font font = getFont();
        Font font1 = menu.getFont();
        if(font1 != font)
            menu.setFont(font);
        k = 0;
        l = dimension.height;
        menu.show(this, k, l);
    }

    private void createPopup()
    {
        if(menu != null)
            return;
        menu = new PopupMenu();
        java.awt.Container container;
        for(container = getParent(); !(container instanceof Frame); container = container.getParent());
        container.add(menu);
        pr("Create a popup menu = " + menu);
        menu.addActionListener(this);
    }

    private void updateEntries()
    {
        pr("updateEntries()");
        createPopup();
        menu.removeAll();
        String as[] = new String[elemVector.size()];
        elemVector.copyInto(as);
        boolean flag = false;
        int i = 0;
        Font font = null;
        java.awt.Container container = getParent();
        if(container != null)
            font = container.getFont();
        if(font != null)
        {
            FontMetrics fontmetrics = getFontMetrics(font);
            int j = fontmetrics.getHeight() + 6;
            Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
            i = dimension.height / j - 1;
            pr("items.length: " + as.length);
            pr("numItems: " + i);
            if(i < as.length)
            {
                flag = true;
                pr("we have more...");
            }
        }
        Object obj = null;
        if(flag)
        {
            for(int k = 0; k < i; k++)
            {
                MenuItem menuitem = new MenuItem(as[k]);
                if(font != null)
                    menuitem.setFont(font);
                menu.add(menuitem);
            }

            String as1[] = new String[as.length - i];
            int i1 = i;
            for(int j1 = 0; j1 < as1.length; j1++)
                as1[j1] = as[i1 + j1];

            Menu menu1 = createMenu(as1, i);
            menu1.addActionListener(this);
            menu.add(menu1);
        } else
        {
            for(int l = 0; l < as.length; l++)
            {
                MenuItem menuitem1 = new MenuItem(as[l]);
                if(font != null)
                    menuitem1.setFont(font);
                menu.add(menuitem1);
            }

        }
        dirty = false;
    }

    private Menu createMenu(String as[], int i)
    {
        Menu menu1 = new Menu("More...");
        Font font = getParent().getFont();
        if(as.length <= i)
        {
            for(int j = 0; j < as.length; j++)
            {
                MenuItem menuitem = new MenuItem(as[j]);
                if(font != null)
                    menuitem.setFont(font);
                menu1.add(menuitem);
            }

        } else
        if(as.length > i)
        {
            for(int k = 0; k < i; k++)
            {
                MenuItem menuitem1 = new MenuItem(as[k]);
                if(font != null)
                    menuitem1.setFont(font);
                menu1.add(menuitem1);
            }

            String as1[] = new String[as.length - i];
            for(int l = 0; l < as1.length; l++)
                as1[l] = as[i + l];

            menu1.add(createMenu(as1, i));
        }
        menu1.addActionListener(this);
        return menu1;
    }

    private MenuItem add(MenuItem menuitem)
    {
        createPopup();
        menuitem.addActionListener(this);
        return menu.add(menuitem);
    }

    public void paint(Graphics g)
    {
        update(g);
    }

    public void update(Graphics g)
    {
        Dimension dimension = getSize();
        Color color3 = getForeground();
        Color color4 = getBackground();
        Color color = topShadowColor;
        Color color1 = bottomShadowColor;
        Color color2 = color4;
        g.setColor(color4);
        g.fillRect(0, 0, dimension.width, dimension.height);
        drawShadow(g, color, color1, 0, 0, dimension.width, dimension.height, 1);
        leftMargin = 6;
        if(!isEnabled())
            color3 = disableColor;
        g.setColor(color3);
        g.drawString(name, leftMargin, topMargin);
        int k = dimension.width - 5 - 10;
        int ai[] = new int[3];
        ai[0] = xPts[0] + k;
        ai[1] = xPts[1] + k;
        ai[2] = xPts[2] + k;
        g.setColor(Color.lightGray);
        g.fillPolygon(ai, yPts, 3);
        g.setColor(topArrowShadowColor);
        for(int i = 0; i < 2; i++)
        {
            g.drawLine(ai[2] + i, yPts[2] + i, ai[0] - i, yPts[0] + i);
            g.drawLine(ai[2] + i, yPts[2], ai[1], yPts[1] - i);
        }

        g.setColor(bottomArrowShadowColor);
        for(int j = 0; j < 2; j++)
            g.drawLine(ai[1], yPts[1] - j, ai[0] - j, yPts[0]);

    }

    public void setFont(Font font)
    {
        super.setFont(font);
        if(menu != null)
            menu.setFont(font);
        calculateSize();
        repaint();
    }

    public Dimension getMinimumSize()
    {
        return getPreferredSize();
    }

    public Dimension getPreferredSize()
    {
        Dimension dimension = new Dimension(lineWidth + 10 + 2, lineHeight + 10 + 2);
        return dimension;
    }

    protected void drawShadow(Graphics g, Color color, Color color1, int i, int j, int k, int l, 
            int i1)
    {
        int k1 = (i + k) - 1;
        int l1 = (j + l) - 1;
        for(int j1 = 0; j1 < i1; j1++)
        {
            g.setColor(color1);
            g.drawLine(i + j1, l1 - j1, k1 - j1, l1 - j1);
            g.drawLine(k1 - j1, j + j1, k1 - j1, l1 - j1);
            g.setColor(color);
            g.drawLine(i + j1, j + j1, k1 - j1, j + j1);
            g.drawLine(i + j1, j + j1, i + j1, l1 - j1);
        }

    }

    protected void calculateSize()
    {
        Font font = getFont();
        if(font == null)
            return;
        FontMetrics fontmetrics = getFontMetrics(font);
        if(fontmetrics == null)
            return;
        lineWidth = 0;
        for(int i = 0; i < elemVector.size(); i++)
        {
            int j = fontmetrics.stringWidth((String)elemVector.elementAt(i));
            if(j > lineWidth)
                lineWidth = j;
        }

        lineHeight = fontmetrics.getHeight();
        lineAscent = fontmetrics.getMaxAscent();
        leftMargin = 5;
        topMargin = lineAscent + 5 + 1;
        lineWidth += 20;
        xPts = new int[3];
        yPts = new int[3];
        int k = 5 + lineHeight / 2;
        byte byte0 = 5;
        xPts[0] = 10;
        xPts[1] = byte0;
        xPts[2] = 0;
        yPts[0] = k - byte0;
        yPts[1] = k + byte0;
        yPts[2] = yPts[0];
    }

    private void setComponentColor()
    {
        Color color = getBackground();
        if(color != null)
        {
            HighlightFilter highlightfilter = new HighlightFilter(false, 20);
            int i = highlightfilter.filterRGB(0, 0, color.getRGB());
            focusColor = new Color(i);
            highlightfilter = new HighlightFilter(true, 50);
            i = highlightfilter.filterRGB(0, 0, getForeground().getRGB());
            disableColor = new Color(i);
            i = highlightfilter.filterRGB(0, 0, color.getRGB());
            topShadowColor = new Color(i);
            i = highlightfilter.filterRGB(0, 0, Color.lightGray.getRGB());
            topArrowShadowColor = new Color(i);
            highlightfilter = new HighlightFilter(false, 50);
            i = highlightfilter.filterRGB(0, 0, color.getRGB());
            bottomShadowColor = new Color(i);
            i = highlightfilter.filterRGB(0, 0, Color.lightGray.getRGB());
            bottomArrowShadowColor = new Color(i);
            return;
        } else
        {
            focusColor = color;
            topShadowColor = Color.white;
            bottomShadowColor = Color.black;
            return;
        }
    }

    public int getAscent()
    {
        Font font = getFont();
        if(font == null)
            return 0;
        FontMetrics fontmetrics = getFontMetrics(font);
        if(fontmetrics == null)
            return 0;
        else
            return fontmetrics.getMaxAscent() + 5 + 1;
    }

    public int getDescent()
    {
        int i = getAscent();
        if(i == 0)
            return 0;
        else
            return getSize().height - i;
    }

    private static void pr(String s)
    {
        if(debug)
            System.out.println(s);
    }

    private static boolean debug;
    protected Vector elemVector;
    transient ItemListener itemListener;
    protected String name;
    protected PopupMenu menu;
    protected boolean dirty;
    protected String currSelection;
    protected int currIndex;
    protected int lineAscent;
    protected int lineWidth;
    protected int lineHeight;
    protected int leftMargin;
    protected int topMargin;
    protected static Color disableColor = null;
    protected static Color focusColor = null;
    protected static Color topShadowColor = null;
    protected static Color bottomShadowColor = null;
    protected static Color topArrowShadowColor = null;
    protected static Color bottomArrowShadowColor = null;
    protected static final int shadowThickness = 1;
    protected static final int borderWidth = 5;
    protected static final int indentWidth = 10;
    protected static final int arrowWidth = 10;
    protected static final int arrowShadowThickness = 2;
    private int xPts[];
    private int yPts[];

}
