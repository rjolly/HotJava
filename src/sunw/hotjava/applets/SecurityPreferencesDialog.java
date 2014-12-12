// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   SecurityPreferencesDialog.java

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
//            secButtonPanel

public class SecurityPreferencesDialog extends UserDialog
    implements ItemListener, MouseListener, KeyPressInterest
{

    public static SecurityPreferencesDialog getSingleton()
    {
        if(singleton == null)
            singleton = new SecurityPreferencesDialog();
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
        return getProp("security.preference", s);
    }

    private SecurityPreferencesDialog()
    {
        super("security.preference", HJWindowManager.getHJWindowManager().getLastFocusHolder(), false, properties);
        indexPanel = new Panel();
        modePanel = new NPanel(1);
        linePanel = new NPanel(2);
        contentDeck = new Panel();
        basicIndexPanel = new Panel();
        advancedIndexPanel = new Panel();
        westPanel = new Panel();
        indexPanelLayout = new CardLayout();
        contentPanelLayout = new CardLayout();
        cbg = new CheckboxGroup();
        basicIndicesVector = new Vector();
        advancedIndicesVector = new Vector();
        sModes = new Vector();
        indexTags = new Vector();
        currentPanelDisplayed = "";
        regular = Font.decode(getProp("security.preference.font.", "regular"));
        regularBold = Font.decode(getProp("security.preference.font.", "regularBold"));
        headings = Font.decode(getProp("security.preference.font.", "headings"));
        buttonPanel = new secButtonPanel(contentDeck, properties, "security.preference", 0, this);
        normalBackground = properties.getColor("security.preference.indexpanel.normalback", null);
        normalForeground = properties.getColor("security.preference.indexpanel.normalfore", null);
        selectedBackground = properties.getColor("security.preference.indexpanel.selectedback", null);
        selectedForeground = properties.getColor("security.preference.indexpanel.selectedfore", null);
        mouseoverForeground = properties.getColor("security.preference.indexpanel.mousefore", null);
        Dimension dimension = new Dimension(640, 480);
        String s = getProp(".dialogwidth");
        String s1 = getProp(".dialogheight");
        if(s != null && s1 != null)
        {
            dimension.width = Integer.parseInt(s);
            dimension.height = Integer.parseInt(s1);
        } else
        {
            Toolkit toolkit = Toolkit.getDefaultToolkit();
            dimension = toolkit.getScreenSize();
            dimension.width *= 0;
            dimension.height *= 0;
        }
        setSize(dimension.width >= 640 ? dimension.width : 640, dimension.height >= 480 ? dimension.height : 480);
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
        String s = getProp("", "hotjava.securitymodes");
        keylistener = new EnterKeyListener(this);
        for(StringTokenizer stringtokenizer = new StringTokenizer(s, "|"); stringtokenizer.hasMoreTokens(); sModes.addElement(stringtokenizer.nextToken()));
        add(modePanel, "North");
        modePanel.setSize(containerWidth, (int)((double)containerHeight * 0.10000000000000001D));
        MultiLineLabel multilinelabel = new MultiLineLabel(getProp("", "hotjava.securityModeLable"));
        multilinelabel.setFont(regular);
        modePanel.add(multilinelabel);
        basicMode = getProp("", "hotjava.securitymode").equals("Basic");
        modePanel.add(basic = new Checkbox("  " + (String)sModes.elementAt(0), cbg, basicMode));
        modePanel.add(advanced = new Checkbox("  " + (String)sModes.elementAt(1), cbg, !basicMode));
        basic.setFont(regular);
        advanced.setFont(regular);
        basic.addItemListener(this);
        advanced.addItemListener(this);
        add(westPanel, "West");
        westPanel.setLayout(new BorderLayout());
        westPanel.add(indexPanel, "West");
        indexPanel.setSize((int)((double)containerWidth * 0.20000000000000001D), containerHeight);
        indexPanel.setLayout(indexPanelLayout);
        indexPanel.add(basicIndexPanel, "Basic");
        indexPanel.add(advancedIndexPanel, "Advanced");
        indexPanelLayout.show(indexPanel, basicMode ? "Basic" : "Advanced");
        basicIndexPanel.setLayout(new GridLayout(15, 1));
        advancedIndexPanel.setLayout(new GridLayout(15, 1));
        String s1 = getProp(".basicindices");
        String s2 = getProp(".advancedindices");
        StringTokenizer stringtokenizer1 = new StringTokenizer(s1, "|");
        Object obj = null;
        Dimension dimension = basicIndexPanel.size();
        int i = 0;
        boolean flag2 = true;
        while(stringtokenizer1.hasMoreTokens()) 
        {
            String s5 = stringtokenizer1.nextToken();
            StringTokenizer stringtokenizer2 = new StringTokenizer(s5, ".");
            boolean flag = true;
            while(stringtokenizer2.hasMoreTokens()) 
            {
                String s6 = "";
                if(!flag)
                    s6 = "    ";
                else
                    flag = !flag;
                String s3 = stringtokenizer2.nextToken();
                try
                {
                    i++;
                    Class.forName(getProp(".class.tag" + i));
                    Label label;
                    basicIndexPanel.add(label = new Label("    " + s6 + s3));
                    label.setFont(headings);
                    label.setForeground(normalForeground);
                    label.setBackground(normalBackground);
                    LabelTag labeltag = new LabelTag();
                    labeltag.label = s3;
                    labeltag.tag = "tag" + i;
                    indexTags.addElement(labeltag);
                    label.setSize(dimension.width - 5, dimension.height);
                    basicIndicesVector.addElement(label);
                    label.setVisible(true);
                    label.addMouseListener(this);
                    if(flag2)
                    {
                        flag2 = !flag2;
                        obj = label;
                        basicDefault = label;
                    }
                }
                catch(Exception _ex) { }
            }
        }
        dimension = advancedIndexPanel.size();
        stringtokenizer1 = new StringTokenizer(s2, "|");
        flag2 = true;
        int j = indexTags.size();
        while(stringtokenizer1.hasMoreTokens()) 
        {
            String s7 = stringtokenizer1.nextToken();
            StringTokenizer stringtokenizer3 = new StringTokenizer(s7, ".");
            boolean flag1 = true;
            while(stringtokenizer3.hasMoreTokens()) 
            {
                String s8 = "";
                if(!flag1)
                    s8 = "    ";
                else
                    flag1 = !flag1;
                String s4 = stringtokenizer3.nextToken();
                boolean flag3 = false;
                Object obj1 = null;
                for(int k = 0; !flag3 && k < j; k++)
                {
                    LabelTag labeltag1 = (LabelTag)indexTags.elementAt(k);
                    flag3 = labeltag1.label.equals(s4);
                }

                try
                {
                    i++;
                    if(!flag3)
                        Class.forName(getProp(".class.tag" + i));
                    Label label1;
                    advancedIndexPanel.add(label1 = new Label("    " + s8 + s4));
                    label1.setFont(headings);
                    label1.setForeground(normalForeground);
                    label1.setBackground(normalBackground);
                    LabelTag labeltag2 = new LabelTag();
                    labeltag2.label = s4;
                    labeltag2.tag = "tag" + i;
                    indexTags.addElement(labeltag2);
                    label1.setSize(dimension.width - 5, dimension.height);
                    advancedIndicesVector.addElement(label1);
                    label1.setVisible(true);
                    label1.addMouseListener(this);
                    if(flag2)
                    {
                        advancedDefault = label1;
                        flag2 = !flag2;
                        if(!basicMode)
                            obj = label1;
                    }
                }
                catch(Exception _ex) { }
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
        mouseClicked(new MouseEvent(((Component) (obj)), 0, 0L, 0, 0, 0, 0, true));
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
            Class class1 = Class.forName(getProp(".class." + s));
            Panel panel = null;
            Class aclass[] = new Class[1];
            aclass[0] = java.awt.event.KeyListener.class;
            Constructor constructor = class1.getConstructor(aclass);
            Object aobj[] = new Object[1];
            aobj[0] = keylistener;
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

    public void itemStateChanged(ItemEvent itemevent)
    {
        String s = cbg.getSelectedCheckbox().getLabel().trim();
        indexPanelLayout.show(indexPanel, s);
        mouseClicked(new MouseEvent(s.equals("Basic") ? ((Component) (basicDefault)) : ((Component) (advancedDefault)), 0, 0L, 0, 0, 0, 0, true));
        if(s.equals("Advanced"))
            s = "Administrator";
        properties.put("hotjava.securitymode", s);
    }

    public static HJBProperties properties = HJBProperties.getHJBProperties("hjbrowser");
    private static final String propsName = "security.preference";
    private Panel indexPanel;
    private NPanel modePanel;
    private NPanel linePanel;
    private Panel contentDeck;
    private Panel basicIndexPanel;
    private Panel advancedIndexPanel;
    private Panel westPanel;
    private secButtonPanel buttonPanel;
    private CardLayout indexPanelLayout;
    private CardLayout contentPanelLayout;
    private CheckboxGroup cbg;
    private Checkbox basic;
    private Checkbox advanced;
    private Color normalBackground;
    private Color normalForeground;
    private Color selectedBackground;
    private Color selectedForeground;
    private Color mouseoverBackground;
    private Color mouseoverForeground;
    private Vector basicIndicesVector;
    private Vector advancedIndicesVector;
    private Vector sModes;
    private int containerWidth;
    private int containerHeight;
    private Label selected;
    private Label lastSelected;
    private Vector indexTags;
    private Font regular;
    private Font regularBold;
    private Font headings;
    private boolean basicMode;
    private Label basicDefault;
    private Label advancedDefault;
    private EnterKeyListener keylistener;
    public String currentPanelDisplayed;
    public static SecurityPreferencesDialog singleton = null;

}
