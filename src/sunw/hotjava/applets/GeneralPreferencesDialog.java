// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   GeneralPreferencesDialog.java

package sunw.hotjava.applets;

import java.awt.*;
import java.awt.event.*;
import java.lang.reflect.Constructor;
import java.util.StringTokenizer;
import java.util.Vector;
import sunw.hotjava.HJWindowManager;
import sunw.hotjava.misc.HJBProperties;
import sunw.hotjava.ui.*;

// Referenced classes of package sunw.hotjava.applets:
//            genButtonPanel

public class GeneralPreferencesDialog extends UserDialog
    implements MouseListener, KeyPressInterest
{

    public static GeneralPreferencesDialog getSingleton()
    {
        if(singleton == null)
            singleton = new GeneralPreferencesDialog();
        singleton.toFront();
        singleton.requestFocus();
        return singleton;
    }

    public static GeneralPreferencesDialog getSingleton(String s)
    {
        override = true;
        if(singleton == null)
            singleton = new GeneralPreferencesDialog();
        singleton.toFront();
        singleton.requestFocus();
        return singleton;
    }

    private String getProp(String s, String s1)
    {
        String s2 = properties.getProperty(s + s1);
        return s2;
    }

    private String getProp(String s)
    {
        return getProp("general.prefs", s);
    }

    private GeneralPreferencesDialog()
    {
        super("general.prefs", HJWindowManager.getHJWindowManager().getLastFocusHolder(), false, properties);
        d = new Dimension(640, 480);
        westPanel = new Panel();
        indexPanel = new Panel();
        linePanel = new NPanel(2);
        contentDeck = new Panel();
        contentPanelLayout = new CardLayout();
        indicesVector = new Vector();
        indexTags = new Vector();
        currentPanelDisplayed = "";
        regular = Font.decode(getProp("security.preference.font", ".regular"));
        regularBold = Font.decode(getProp("security.preference.font", ".regularBold"));
        headings = Font.decode(getProp("security.preference.font", ".headings"));
        buttonPanel = new genButtonPanel(contentDeck, properties, "general.prefs", 0, this);
        normalBackground = properties.getColor("security.preference.indexpanel.normalback", null);
        normalForeground = properties.getColor("security.preference.indexpanel.normalfore", null);
        selectedBackground = properties.getColor("security.preference.indexpanel.selectedback", null);
        selectedForeground = properties.getColor("security.preference.indexpanel.selectedfore", null);
        mouseoverForeground = properties.getColor("security.preference.indexpanel.mousefore", null);
        String s = getProp(".dialogwidth");
        String s1 = getProp(".dialogheight");
        if(s != null && s1 != null)
        {
            d.width = Integer.parseInt(s);
            d.height = Integer.parseInt(s1);
        } else
        {
            Toolkit toolkit = Toolkit.getDefaultToolkit();
            d = toolkit.getScreenSize();
            d.width *= 0;
            d.height *= 0;
        }
        setSize(d.width >= 640 ? d.width : 640, d.height >= 480 ? d.height : 480);
        containerWidth = size().width;
        containerHeight = size().height;
        setResizable(false);
        setLayout(new BorderLayout());
        centerOnScreen();
        preInit();
        show();
    }

    private void preInit()
    {
        keylistener = new EnterKeyListener(this);
        westPanel.setLayout(new BorderLayout());
        add(westPanel, "West");
        westPanel.add(indexPanel, "West");
        indexPanel.setSize((int)((double)containerWidth * 0.20000000000000001D), containerHeight);
        indexPanel.setLayout(new GridLayout(15, 1));
        StringTokenizer stringtokenizer = new StringTokenizer(getProp(".subpanels"), "|");
        int i = 1;
        Object obj = null;
        Dimension dimension = indexPanel.getSize();
        while(stringtokenizer.hasMoreTokens()) 
        {
            String s1 = stringtokenizer.nextToken();
            StringTokenizer stringtokenizer1 = new StringTokenizer(s1, ".");
            boolean flag = true;
            Label label;
            for(; stringtokenizer1.hasMoreTokens(); label.addMouseListener(this))
            {
                String s2 = "";
                if(!flag)
                    s2 = "    ";
                else
                    flag = !flag;
                String s = stringtokenizer1.nextToken();
                if(obj == null && !override)
                    obj = new Label(s);
                else
                if(override)
                    obj = new Label(s);
                indexPanel.add(label = new Label("    " + s2 + s));
                label.setFont(headings);
                label.setForeground(normalForeground);
                label.setBackground(normalBackground);
                LabelTag labeltag = new LabelTag();
                labeltag.label = s;
                labeltag.tag = "tag" + i++;
                indexTags.addElement(labeltag);
                label.setSize(dimension.width - 5, dimension.height);
                indicesVector.addElement(label);
                label.setVisible(true);
            }

        }
        add(buttonPanel, "South");
        buttonPanel.setSize(containerWidth, (int)((double)containerHeight * 0.10000000000000001D));
        contentDeck.setLayout(contentPanelLayout);
        add(contentDeck, "Center");
        Panel panel = new Panel();
        panel.add(new Label(""));
        contentDeck.add(panel, "default");
        westPanel.setSize((int)(0.80000000000000004D * (double)containerHeight), (int)(0.20000000000000001D * (double)containerWidth));
        linePanel.setSize(5, westPanel.size().height);
        westPanel.add(linePanel, "East");
        if(override)
            override = false;
        mouseClicked(new MouseEvent(((Component) (obj)), 0, 0L, 0, 0, 0, 0, true));
    }

    public void dispose()
    {
        singleton = null;
        super.dispose();
    }

    public void processEnterEvent(KeyEvent keyevent)
    {
        buttonPanel.doDefaultAction();
    }

    public void mousePressed(MouseEvent mouseevent)
    {
    }

    public void mouseReleased(MouseEvent mouseevent)
    {
    }

    public void mouseEntered(MouseEvent mouseevent)
    {
        mouseevent.getComponent().setForeground(mouseoverForeground);
        mouseevent.getComponent().setCursor(new Cursor(12));
        mouseevent.getComponent().getParent().dispatchEvent(mouseevent);
    }

    public void mouseExited(MouseEvent mouseevent)
    {
        mouseevent.getComponent().setForeground(normalForeground);
        mouseevent.getComponent().setCursor(Cursor.getDefaultCursor());
        mouseevent.getComponent().getParent().dispatchEvent(mouseevent);
    }

    public void mouseClicked(MouseEvent mouseevent)
    {
        lastSelected = selected;
        selected = (Label)mouseevent.getComponent();
        LabelTag labeltag = null;
        int i = 0;
        int j = indexTags.size();
        String s = selected.getText().trim();
        if(lastSelected != null)
            lastSelected.setBackground(normalBackground);
        selected.setBackground(selectedBackground);
        selected.setForeground(selectedForeground);
        for(int k = 0; k < j; k++)
        {
            labeltag = (LabelTag)indexTags.elementAt(k);
            if(!labeltag.label.equals(s))
                continue;
            s = labeltag.tag;
            i = k;
            break;
        }

        currentPanelDisplayed = s;
        if(labeltag.loaded)
        {
            contentPanelLayout.show(contentDeck, s);
            return;
        }
        labeltag.loaded = true;
        indexTags.setElementAt(labeltag, i);
        try
        {
            String s1 = getProp("general.panel.class", "." + s);
            Class class1 = Class.forName(s1);
            Panel panel = null;
            Class aclass[] = new Class[5];
            aclass[0] = java.awt.event.KeyListener.class;
            aclass[1] = java.lang.String.class;
            aclass[2] = java.awt.Font.class;
            aclass[3] = java.awt.Font.class;
            aclass[4] = java.awt.Font.class;
            Constructor constructor = class1.getConstructor(aclass);
            Object aobj[] = new Object[5];
            aobj[0] = keylistener;
            aobj[1] = selected.getText().trim();
            aobj[2] = regular;
            aobj[3] = regularBold;
            aobj[4] = headings;
            panel = (Panel)constructor.newInstance(aobj);
            contentDeck.add(panel, s);
            contentPanelLayout.show(contentDeck, s);
            return;
        }
        catch(Exception _ex)
        {
            contentPanelLayout.show(contentDeck, "default");
        }
    }

    public static HJBProperties properties = HJBProperties.getHJBProperties("hjbrowser");
    private Dimension d;
    private static final String propsName = "general.prefs";
    private Panel westPanel;
    private Panel indexPanel;
    private NPanel linePanel;
    private Panel contentDeck;
    private genButtonPanel buttonPanel;
    private CardLayout contentPanelLayout;
    private Color normalBackground;
    private Color normalForeground;
    private Color selectedBackground;
    private Color selectedForeground;
    private Color mouseoverBackground;
    private Color mouseoverForeground;
    private Vector indicesVector;
    private int containerWidth;
    private int containerHeight;
    private Label selected;
    private Label lastSelected;
    private Vector indexTags;
    public Font regular;
    public Font regularBold;
    public Font headings;
    public static boolean override;
    private EnterKeyListener keylistener;
    public String currentPanelDisplayed;
    public static GeneralPreferencesDialog singleton = null;

}
