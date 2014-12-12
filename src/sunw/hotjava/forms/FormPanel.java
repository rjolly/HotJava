// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   FormPanel.java

package sunw.hotjava.forms;

import java.awt.AWTEvent;
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Checkbox;
import java.awt.CheckboxGroup;
import java.awt.Choice;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FileDialog;
import java.awt.FontMetrics;
import java.awt.Frame;
import java.awt.List;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.TextComponent;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import sunw.hotjava.bean.HotJavaBrowserBean;
import sunw.hotjava.doc.DocItem;
import sunw.hotjava.doc.DocPanel;
import sunw.hotjava.doc.Document;
import sunw.hotjava.doc.DocumentEvent;
import sunw.hotjava.doc.DocumentFormatter;
import sunw.hotjava.doc.DocumentPanel;
import sunw.hotjava.doc.DocumentState;
import sunw.hotjava.doc.Formatter;
import sunw.hotjava.doc.TagItem;
import sunw.hotjava.doc.TextItem;
import sunw.hotjava.misc.Globals;
import sunw.hotjava.misc.HJBProperties;
import sunw.hotjava.misc.RequestProcessor;

// Referenced classes of package sunw.hotjava.forms:
//            ComboBox, FORM, FormTextField, HJEncoder, 
//            ImageCanvas, OPTION, SubmitThread

public class FormPanel extends Panel
    implements DocPanel
{
    class UserActionListener
        implements ActionListener
    {

        public void actionPerformed(ActionEvent actionevent)
        {
            processUserAction(actionevent);
        }

        UserActionListener()
        {
        }
    }

    private class FormCheckboxGroup extends CheckboxGroup
    {

        public int getCount()
        {
            return count;
        }

        public void incrementCount()
        {
            count++;
        }

        public void decrementCount()
        {
            count--;
        }

        int count;

        public FormCheckboxGroup()
        {
            count = 0;
        }
    }

    private final class FormMouseListener extends MouseAdapter
        implements MouseMotionListener, Serializable
    {

        public void mouseExited(MouseEvent mouseevent)
        {
            processMouseAction(mouseevent);
        }

        public void mousePressed(MouseEvent mouseevent)
        {
            processMouseAction(mouseevent);
        }

        public void mouseReleased(MouseEvent mouseevent)
        {
            processMouseAction(mouseevent);
        }

        public void mouseDragged(MouseEvent mouseevent)
        {
            processMouseAction(mouseevent);
        }

        public void mouseMoved(MouseEvent mouseevent)
        {
            processMouseAction(mouseevent);
        }

        public FormMouseListener()
        {
        }
    }

    private final class FormFocusListener
        implements FocusListener
    {

        public void focusGained(FocusEvent focusevent)
        {
            processFocusEvent();
        }

        public void focusLost(FocusEvent focusevent)
        {
            processFocusEvent();
        }

        FormFocusListener()
        {
        }
    }


    public Formatter getFormatter()
    {
        return win;
    }

    public Component getComponent()
    {
        return comp;
    }

    public String getContainingDocumentPanelName()
    {
        for(Container container = getParent(); container != null; container = container.getParent())
            if(container instanceof DocumentPanel)
                return container.getName();

        return null;
    }

    public int getSizeAttribute()
    {
        return sizeAttribute;
    }

    public void unselectTextInOtherComponent()
    {
        Container container;
        for(container = win.getParent(); !(container instanceof HotJavaBrowserBean); container = container.getParent());
        Object obj = ((HotJavaBrowserBean)container).getSelector();
        if(obj != null)
        {
            if(obj instanceof DocumentFormatter)
            {
                ((DocumentFormatter)obj).select(0, 0);
                return;
            }
            if((Component)obj != getComponent())
                ((TextComponent)obj).select(0, 0);
        }
    }

    protected void processMouseAction(MouseEvent mouseevent)
    {
        if(mouseevent.getID() == 501)
        {
            unselectTextInOtherComponent();
            return;
        }
        if(mouseevent.getID() == 502)
        {
            if(!isSelectedTextExists())
                docSel = "";
            else
                docSel = ((TextComponent)comp).getSelectedText();
            getFormatter().dispatchDocumentEvent(1007, this);
        }
    }

    public void dispatchDocumentEvent(int i)
    {
        getFormatter().dispatchDocumentEvent(i, this);
    }

    public void processKeyEvent(KeyEvent keyevent)
    {
        if(isSelectedTextExists())
        {
            unselectTextInOtherComponent();
            docSel = ((TextComponent)comp).getSelectedText();
            getFormatter().dispatchDocumentEvent(1007, this);
        }
    }

    protected void processFocusEvent()
    {
        if(comp != null)
            switch(type)
            {
            default:
                break;

            case 1: // '\001'
            case 9: // '\t'
                if(!isSelectedTextExists() && docSel != null && !docSel.equals(""))
                {
                    docSel = null;
                    getFormatter().dispatchDocumentEvent(1007, this);
                }
                break;
            }
    }

    private void registerListeners(FormMouseListener formmouselistener, FormFocusListener formfocuslistener, KeyListener keylistener)
    {
        comp.addMouseListener(formmouselistener);
        comp.addMouseMotionListener(formmouselistener);
        comp.addFocusListener(formfocuslistener);
        comp.addKeyListener(keylistener);
    }

    private void registerListeners(FormMouseListener formmouselistener, FormFocusListener formfocuslistener)
    {
        comp.addMouseListener(formmouselistener);
        comp.addMouseMotionListener(formmouselistener);
        comp.addFocusListener(formfocuslistener);
    }

    public String getSelectedText()
    {
        String s = null;
        switch(type)
        {
        case 1: // '\001'
        case 9: // '\t'
            s = ((TextComponent)comp).getSelectedText();
            break;
        }
        return s;
    }

    public boolean isHidden()
    {
        return type == 8;
    }

    public FORM getFormItem()
    {
        return (FORM)item.getFormParent();
    }

    String getData(DocItem docitem)
    {
        String s = "";
        int i = docitem.getIndex();
        for(int j = i + docitem.getOffset(); i++ < j;)
        {
            docitem = doc.getItem(i);
            if(docitem.isText())
                s = s + ((TextItem)docitem).getText();
        }

        return s;
    }

    java.util.Vector getOptionList()
    {
        java.util.Vector vector = new java.util.Vector();
        int i = item.getIndex();
        for(int j = i + item.getOffset(); i++ < j;)
        {
            DocItem docitem = doc.getItem(i);
            if(docitem instanceof OPTION)
            {
                vector.addElement(getData(docitem).trim());
                if(((OPTION)docitem).selected())
                    defaultSelectedIndex = vector.size() - 1;
                i += docitem.getOffset();
            }
        }

        return vector;
    }

    java.util.Vector getOptionValueList()
    {
        java.util.Vector vector = new java.util.Vector();
        int i = item.getIndex();
        for(int j = i + item.getOffset(); i++ < j;)
        {
            DocItem docitem = doc.getItem(i);
            if(docitem instanceof OPTION)
            {
                OPTION option = (OPTION)docitem;
                String s = option.getAttribute("value");
                if(s == null)
                    s = getData(docitem).trim();
                vector.addElement(s != null ? ((Object) (s)) : "");
                i += docitem.getOffset();
            }
        }

        return vector;
    }

    public synchronized void addOption(String s, String s1, int i)
    {
        if(type != 10 && type != 11)
            return;
        optionStrings.insertElementAt(s, i);
        optionValues.insertElementAt(s1, i);
        switch(type)
        {
        case 10: // '\n'
            if(usingComboBox)
            {
                ((ComboBox)comp).insert(s, i);
                return;
            } else
            {
                ((Choice)comp).insert(s, i);
                return;
            }

        case 11: // '\013'
            ((List)comp).add(s, i);
            return;
        }
    }

    public synchronized void removeOption(int i)
    {
        if(type != 10 && type != 11)
            return;
        optionStrings.removeElementAt(i);
        optionValues.removeElementAt(i);
        switch(type)
        {
        case 10: // '\n'
            if(usingComboBox)
            {
                ((ComboBox)comp).remove(i);
                return;
            } else
            {
                ((Choice)comp).remove(i);
                return;
            }

        case 11: // '\013'
            ((List)comp).remove(i);
            return;
        }
    }

    public synchronized void changeOptionText(String s, int i)
    {
        if(type != 10 && type != 11)
            return;
        optionStrings.setElementAt(s, i);
        switch(type)
        {
        case 10: // '\n'
            if(usingComboBox)
            {
                ComboBox combobox = (ComboBox)comp;
                combobox.remove(i);
                combobox.insert(s, i);
                return;
            } else
            {
                Choice choice = (Choice)comp;
                choice.remove(i);
                choice.insert(s, i);
                return;
            }

        case 11: // '\013'
            ((List)comp).remove(i);
            ((List)comp).add(s, i);
            return;
        }
    }

    public synchronized void changeOptionValue(String s, int i)
    {
        if(type != 10 && type != 11)
        {
            return;
        } else
        {
            optionValues.setElementAt(s, i);
            return;
        }
    }

    public int getDefaultSelectedIndex()
    {
        return defaultSelectedIndex;
    }

    public synchronized void setDefaultSelectedIndex(int i)
    {
        defaultSelectedIndex = i;
    }

    public String getOptionString(int i)
    {
        if(type != 10 && type != 11)
            return null;
        else
            return (String)optionStrings.elementAt(i);
    }

    public String getOptionValue(int i)
    {
        if(type != 10 && type != 11)
            return null;
        else
            return (String)optionValues.elementAt(i);
    }

    String getStringAttribute(String s, String s1)
    {
        String s2 = item.getAttribute(s);
        if(s2 != null)
            return s2;
        else
            return s1;
    }

    int getIntAttribute(String s, int i)
    {
        String s1 = item.getAttribute(s);
        if(s1 != null)
            try
            {
                return Integer.parseInt(s1);
            }
            catch(NumberFormatException _ex) { }
        return i;
    }

    int getAscent()
    {
        switch(type)
        {
        case 1: // '\001'
        case 2: // '\002'
        case 12: // '\f'
            return 19;

        case 10: // '\n'
            if(comp == null || !usingComboBox)
                return 19;
            else
                return ((ComboBox)comp).getAscent();

        case 9: // '\t'
        case 11: // '\013'
            return getPreferredSize().height;
        }
        return 10;
    }

    int getDescent()
    {
        switch(type)
        {
        case 10: // '\n'
            if(comp == null || !usingComboBox)
                return 0;
            else
                return ((ComboBox)comp).getDescent();
        }
        return 0;
    }

    void ampersand(StringBuffer stringbuffer)
    {
        if(stringbuffer.length() > 0)
            stringbuffer.append('&');
    }

    public String getValue()
    {
        StringBuffer stringbuffer = new StringBuffer();
        value(stringbuffer);
        return stringbuffer.toString();
    }

    private String getCharset()
    {
        String s = (String)doc.getProperty("charset");
        if(s != null && s.equals("JISAutoDetect"))
        {
            s = (String)doc.getProperty("specifiedjischarset");
            if(s == null)
                s = (String)doc.getProperty("charset");
        }
        if(s == null)
            s = "iso-8859-1";
        return s;
    }

    void value(StringBuffer stringbuffer)
    {
        String s = getCharset();
        String s1 = getStringAttribute("name", null);
        if(s1 != null && s != null)
            s1 = HJEncoder.encode(s1, s);
        String s2 = null;
        switch(type)
        {
        case 1: // '\001'
        case 2: // '\002'
        case 12: // '\f'
            s2 = ((TextField)comp).getText();
            break;

        case 3: // '\003'
        case 4: // '\004'
            if(!((Checkbox)comp).getState())
                return;
            s2 = getStringAttribute("value", "on");
            break;

        case 5: // '\005'
        case 8: // '\b'
        case 13: // '\r'
            s2 = getStringAttribute("value", "");
            break;

        case 9: // '\t'
            s2 = canonicalizeNewlines(((TextArea)comp).getText());
            break;

        case 10: // '\n'
            int i = -1;
            if(usingComboBox)
                i = ((ComboBox)comp).getSelectedIndex();
            else
                i = ((Choice)comp).getSelectedIndex();
            if(optionValues.size() > 0)
            {
                ampersand(stringbuffer);
                stringbuffer.append(s1);
                stringbuffer.append("=");
                String s4 = HJEncoder.encode((String)optionValues.elementAt(i), s);
                stringbuffer.append(s4);
            }
            break;

        case 11: // '\013'
            int ai[] = ((List)comp).getSelectedIndexes();
            if(optionValues.size() > 0 && ai.length > 0)
            {
                ampersand(stringbuffer);
                stringbuffer.append(s1);
                stringbuffer.append('=');
                String s5 = HJEncoder.encode((String)optionValues.elementAt(ai[0]), s);
                stringbuffer.append(s5);
                for(int j = 1; j < ai.length; j++)
                {
                    stringbuffer.append('&');
                    stringbuffer.append(s1);
                    stringbuffer.append('=');
                    String s6 = HJEncoder.encode((String)optionValues.elementAt(ai[j]), s);
                    stringbuffer.append(s6);
                }

            }
            break;

        case 7: // '\007'
            if(imageSubmitClickLoc != null)
            {
                if(stringbuffer.length() != 0)
                    ampersand(stringbuffer);
                stringbuffer.append(imageSubmitClickLoc);
                imageSubmitClickLoc = null;
            }
            break;
        }
        if(s1 != null && s2 != null)
        {
            ampersand(stringbuffer);
            stringbuffer.append(s1);
            stringbuffer.append('=');
            String s3 = HJEncoder.encode(s2, s);
            stringbuffer.append(s3);
        }
    }

    String canonicalizeNewlines(String s)
    {
        StringBuffer stringbuffer = new StringBuffer(s);
        for(int i = 0; i < stringbuffer.length(); i++)
            if(i != 0 && stringbuffer.charAt(i) == '\n' && stringbuffer.charAt(i - 1) != '\r')
            {
                stringbuffer.insert(i, '\r');
                i++;
            }

        return stringbuffer.toString();
    }

    public void resetAll()
    {
        FORM form = (FORM)item.getFormParent();
        if(!form.aboutToReset())
            return;
        FormPanel formpanel;
        for(java.util.Enumeration enumeration = form.getFormElements(); enumeration.hasMoreElements(); formpanel.reset())
        {
            TagItem tagitem = (TagItem)enumeration.nextElement();
            formpanel = getFormPanelFrom(tagitem);
        }

    }

    void reset()
    {
        switch(type)
        {
        case 1: // '\001'
        case 2: // '\002'
        case 12: // '\f'
            ((TextField)comp).setText(getStringAttribute("value", ""));
            return;

        case 3: // '\003'
            ((Checkbox)comp).setState(getStringAttribute("checked", null) != null);
            return;

        case 4: // '\004'
            FormCheckboxGroup formcheckboxgroup = (FormCheckboxGroup)((Checkbox)comp).getCheckboxGroup();
            if(formcheckboxgroup.getCount() == 1)
            {
                FormCheckboxGroup formcheckboxgroup1 = new FormCheckboxGroup();
                ((Checkbox)comp).setCheckboxGroup(formcheckboxgroup1);
                formcheckboxgroup1.incrementCount();
            }
            ((Checkbox)comp).setState(getStringAttribute("checked", null) != null);
            return;

        case 9: // '\t'
            ((TextArea)comp).setText(getData(item));
            return;

        case 10: // '\n'
            if(usingComboBox)
            {
                ComboBox combobox = (ComboBox)comp;
                if(defaultSelectedIndex < combobox.getItemCount())
                {
                    combobox.select(defaultSelectedIndex);
                    return;
                }
            } else
            {
                Choice choice = (Choice)comp;
                if(defaultSelectedIndex < choice.getItemCount())
                    choice.select(defaultSelectedIndex);
            }
            return;

        case 11: // '\013'
            List list = (List)comp;
            for(int i = 0; i < list.getItemCount(); i++)
                if(i == defaultSelectedIndex)
                {
                    list.deselect(i);
                    list.select(i);
                } else
                if(list.isIndexSelected(i))
                    list.deselect(i);

            return;
        }
    }

    public void forceResize()
    {
        switch(type)
        {
        case 10: // '\n'
            comp.invalidate();
            return;

        case 11: // '\013'
            maxOptionString = "";
            List list = (List)comp;
            for(int i = list.getItemCount() - 1; i >= 0; i--)
            {
                String s = list.getItem(i);
                if(s.length() > maxOptionString.length())
                    maxOptionString = s;
            }

            comp.invalidate();
            return;
        }
    }

    public FormPanel(Formatter formatter, Document document, TagItem tagitem, String s)
    {
        usingComboBox = false;
        submitsAllowed = true;
        maxOptionString = "";
        sizeAttribute = 1;
        win = formatter;
        doc = document;
        item = tagitem;
        formMouseListener = new FormMouseListener();
        formFocusListener = new FormFocusListener();
        KeyAdapter keyadapter = new KeyAdapter() {

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

        }
;
        if(s == null)
            s = getStringAttribute("type", "text").toLowerCase();
        if(s.equals("text"))
        {
            type = 1;
            sizeAttribute = getIntAttribute("size", 20);
            comp = new FormTextField(getIntAttribute("maxlength", 0x7fffffff), this);
            registerListeners(formMouseListener, formFocusListener);
            ((TextField)comp).addActionListener(new UserActionListener());
            ((TextField)comp).setFont(tagitem.getFont());
        } else
        if(s.equals("file"))
        {
            type = 12;
            setLayout(new BorderLayout(0, 3));
            sizeAttribute = getIntAttribute("size", 20);
            comp = new FormTextField(getIntAttribute("maxlength", 0x7fffffff), this);
            registerListeners(formMouseListener, formFocusListener);
            ((TextField)comp).addActionListener(new UserActionListener());
            HJBProperties hjbproperties = HJBProperties.getHJBProperties("beanPropertiesKey");
            String s3 = "forms.file.input.button.title";
            String s6 = "Choose File...";
            String s7 = hjbproperties.getProperty(s3, s6);
            Button button = new Button(s7);
            button.addActionListener(new UserActionListener());
            add(button, "West");
        } else
        if(s.equals("password"))
        {
            type = 2;
            sizeAttribute = getIntAttribute("size", 20);
            comp = new FormTextField(getIntAttribute("maxlength", 0x7fffffff), sizeAttribute, this);
            registerListeners(formMouseListener, formFocusListener);
            ((TextField)comp).setEchoCharacter('*');
            ((TextField)comp).setFont(tagitem.getFont());
        } else
        if(s.equals("checkbox"))
        {
            type = 3;
            comp = new Checkbox();
        } else
        if(s.equals("radio"))
        {
            type = 4;
            comp = new Checkbox();
            FormCheckboxGroup formcheckboxgroup = null;
            String s4 = tagitem.getAttribute("name");
            if(s4 == null)
                s4 = tagitem.getAttribute("NAME");
            FORM form = (FORM)tagitem.getFormParent();
            java.util.Enumeration enumeration2 = form.getFormElements();
            while(enumeration2.hasMoreElements()) 
            {
                TagItem tagitem1 = (TagItem)enumeration2.nextElement();
                FormPanel formpanel = getFormPanelFrom(tagitem1);
                if(formpanel == null)
                    continue;
                formpanel.item.getAttributes();
                String s8 = formpanel.item.getAttribute("name");
                if(s8 == null)
                    s8 = formpanel.item.getAttribute("NAME");
                if(formpanel.type != 4 || s8 == null || !s8.equalsIgnoreCase(s4))
                    continue;
                formcheckboxgroup = (FormCheckboxGroup)((Checkbox)formpanel.comp).getCheckboxGroup();
                break;
            }
            if(formcheckboxgroup == null)
                formcheckboxgroup = new FormCheckboxGroup();
            ((Checkbox)comp).setCheckboxGroup(formcheckboxgroup);
            formcheckboxgroup.incrementCount();
        } else
        if(s.equals("submit"))
        {
            type = 5;
            comp = new Button(getStringAttribute("value", "Submit Query"));
            ((Button)comp).addActionListener(new UserActionListener());
        } else
        if(s.equals("reset"))
        {
            type = 6;
            comp = new Button(getStringAttribute("value", "Reset"));
            ((Button)comp).addActionListener(new UserActionListener());
        } else
        if(s.equals("button"))
        {
            type = 13;
            comp = new Button(getStringAttribute("value", "Button"));
        } else
        if(s.equals("image"))
        {
            type = 7;
            String s1 = getStringAttribute("border", "");
            URL url = null;
            try
            {
                url = new URL(document.getBaseURL(), getStringAttribute("src", ""));
            }
            catch(MalformedURLException _ex) { }
            comp = new ImageCanvas(document, url, s1);
            int i = getIntAttribute("width", -1);
            int j = getIntAttribute("height", -1);
            if(i != -1 && j != -1)
                ((ImageCanvas)comp).setDimension(i, j);
            ((ImageCanvas)comp).addActionListener(new UserActionListener());
        } else
        if(s.equals("hidden"))
            type = 8;
        else
        if(s.equals("select"))
        {
            optionStrings = getOptionList();
            optionValues = getOptionValueList();
            if(getStringAttribute("multiple", null) != null || getIntAttribute("size", 1) > 1)
            {
                type = 11;
                sizeAttribute = getListSizeAttribute(document, tagitem);
                comp = new List(getIntAttribute("size", 1), false);
                if(getStringAttribute("multiple", null) != null)
                    ((List)comp).setMultipleSelections(true);
                String s5;
                for(java.util.Enumeration enumeration = getOptionList().elements(); enumeration.hasMoreElements(); ((List)comp).addItem(s5))
                {
                    s5 = (String)enumeration.nextElement();
                    if(s5.length() > maxOptionString.length())
                        maxOptionString = s5;
                }

            } else
            {
                type = 10;
                String s2 = System.getProperty("os.name");
                if(s2.indexOf("Windows") >= 0)
                {
                    comp = new Choice();
                } else
                {
                    usingComboBox = true;
                    comp = new ComboBox();
                }
                for(java.util.Enumeration enumeration1 = getOptionList().elements(); enumeration1.hasMoreElements();)
                    if(usingComboBox)
                        ((ComboBox)comp).add((String)enumeration1.nextElement());
                    else
                        ((Choice)comp).addItem((String)enumeration1.nextElement());

            }
        } else
        if(s.equals("textarea"))
        {
            type = 9;
            comp = new TextArea();
            registerListeners(formMouseListener, formFocusListener, keyadapter);
        }
        if(comp != null)
            add(comp, "Center");
        setVisible(false);
        reset();
        DocumentState documentstate = formatter.getDocumentState();
        setBackground(documentstate.background);
    }

    protected FormPanel getFormPanelFrom(DocItem docitem)
    {
        Formatter formatter = win;
        java.util.Vector vector = new java.util.Vector();
        Formatter formatter1;
        while((formatter1 = formatter.getParentFormatter()) != null) 
            formatter = formatter1;
        getFormPanelDeepSearch(docitem, formatter, vector);
        if(vector.size() == 0)
            return null;
        else
            return (FormPanel)vector.elementAt(0);
    }

    private void getFormPanelDeepSearch(DocItem docitem, Formatter formatter, java.util.Vector vector)
    {
        docitem.getFormPanel(formatter, vector);
        if(vector.size() > 0)
            return;
        java.util.Vector vector1 = formatter.getChildFormatters();
        if(vector1 != null)
        {
            for(int i = 0; i < vector1.size(); i++)
            {
                Formatter formatter1 = (Formatter)vector1.elementAt(i);
                getFormPanelDeepSearch(docitem, formatter1, vector);
                if(vector.size() > 0)
                    return;
            }

        }
    }

    public Dimension getPreferredSize()
    {
        if(comp == null)
            return new Dimension(0, 0);
        switch(type)
        {
        case 1: // '\001'
        case 2: // '\002'
            return ((TextField)comp).getPreferredSize(sizeAttribute);

        case 12: // '\f'
            Dimension dimension = ((TextField)comp).getPreferredSize(sizeAttribute);
            return new Dimension(dimension.width * 2, dimension.height);

        case 9: // '\t'
            return ((TextArea)comp).getPreferredSize(getIntAttribute("rows", 20), getIntAttribute("cols", 60));

        case 11: // '\013'
            Dimension dimension1 = ((List)comp).getPreferredSize(sizeAttribute);
            java.awt.Font font = comp.getFont();
            if(font == null)
            {
                return dimension1;
            } else
            {
                FontMetrics fontmetrics = comp.getFontMetrics(font);
                int i = fontmetrics.stringWidth(maxOptionString);
                i += 30;
                dimension1.width = i;
                return dimension1;
            }

        case 8: // '\b'
            return new Dimension(0, 0);

        case 7: // '\007'
        default:
            return comp.getPreferredSize();
        }
    }

    public Dimension minimumSize()
    {
        return getPreferredSize();
    }

    public void layout()
    {
        if(comp != null)
        {
            if(type == 12)
            {
                super.layout();
                return;
            }
            Dimension dimension = getSize();
            comp.setSize(dimension.width, dimension.height);
        }
    }

    public void activateSubItems()
    {
    }

    public void start()
    {
    }

    public void stop()
    {
    }

    public void destroy()
    {
    }

    public void interruptLoading()
    {
    }

    public void notify(Document document, int i, int j, int k)
    {
    }

    public void reformat()
    {
    }

    public int findYFor(int i)
    {
        return 0;
    }

    void imageSubmit(String s, ActionEvent actionevent)
    {
        StringBuffer stringbuffer = new StringBuffer();
        String s1 = actionevent.getActionCommand();
        int i = s1.indexOf(':');
        String s2 = s1.substring(0, i);
        String s3 = s1.substring(++i);
        if(s == null || s.equals(""))
        {
            imageSubmitClickLoc = "x=" + s2 + "&y=" + s3;
        } else
        {
            s = HJEncoder.encode(s, getCharset());
            imageSubmitClickLoc = s + ".x" + "=" + s2 + "&" + s + ".y" + "=" + s3;
        }
        submit(stringbuffer, actionevent);
    }

    public void submit(ActionEvent actionevent)
    {
        submit(new StringBuffer(), actionevent);
    }

    void submit(StringBuffer stringbuffer, ActionEvent actionevent)
    {
        synchronized(this)
        {
            if(!submitsAllowed)
                return;
            FORM form = (FORM)item.getFormParent();
            if(form == null)
                return;
            if(!form.aboutToSubmit())
                return;
            java.util.Hashtable hashtable = new java.util.Hashtable();
            int i = 0;
            for(java.util.Enumeration enumeration = form.getFormElements(); enumeration.hasMoreElements();)
            {
                i++;
                TagItem tagitem = (TagItem)enumeration.nextElement();
                FormPanel formpanel1 = getFormPanelFrom(tagitem);
                if(formpanel1 != null && (formpanel1.type != 5 || formpanel1 == this) && formpanel1.type != 7)
                {
                    formpanel1.value(stringbuffer);
                    if(formpanel1.type == 12)
                    {
                        String s = ((TextField)formpanel1.comp).getText();
                        File file = new File(s);
                        if(file.isFile())
                        {
                            String s1 = formpanel1.getStringAttribute("name", null);
                            if(s1 != null)
                                hashtable.put(s1, file);
                        }
                    }
                }
            }

            if(type == 7)
                value(stringbuffer);
            submitsAllowed = false;
            boolean flag = (actionevent.getModifiers() & 1) != 0;
            SubmitThread submitthread = new SubmitThread(this, stringbuffer.toString(), flag, hashtable);
            submitthread.start();
        }
    }

    public synchronized void setSubmitsAllowed(boolean flag)
    {
        submitsAllowed = flag;
    }

    public void processUserAction(final ActionEvent evt)
    {
        sunw.hotjava.misc.RequestProcessor.Request request = new sunw.hotjava.misc.RequestProcessor.Request() {

            public void execute()
            {
                processUserActionInternal(evt);
            }

        }
;
        Globals.getInternalEventsQueue().postRequest(request);
    }

    private void processUserActionInternal(ActionEvent actionevent)
    {
        Object obj = null;
        switch(type)
        {
        case 6: // '\006'
            resetAll();
            return;

        case 7: // '\007'
            imageSubmit(getStringAttribute("name", null), actionevent);
            return;

        case 12: // '\f'
            HJBProperties hjbproperties = HJBProperties.getHJBProperties("beanPropertiesKey");
            String s = "forms.file.input.button.title";
            String s1 = "Choose File...";
            String s3 = hjbproperties.getProperty(s, s1);
            if(actionevent.getActionCommand().equals(s3))
            {
                DocumentFormatter documentformatter = win.getTopFormatter();
                Container container;
                for(container = documentformatter.getParent(); !(container instanceof Frame); container = container.getParent());
                String s4 = "forms.file.input.dialog.title";
                String s2 = "Choose File";
                String s5 = hjbproperties.getPropertyReplace(s4, s2);
                FileDialog filedialog = new FileDialog((Frame)container, s5, 0);
                filedialog.show();
                if(filedialog.getFile() != null)
                {
                    String s6 = filedialog.getDirectory() + filedialog.getFile();
                    ((TextField)comp).setText(s6);
                }
                comp.requestFocus();
                ((TextField)comp).selectAll();
                return;
            }
            // fall through

        case 1: // '\001'
        case 2: // '\002'
            boolean flag = false;
            boolean flag1 = false;
            FORM form = (FORM)item.getFormParent();
            for(java.util.Enumeration enumeration = form.getFormElements(); enumeration.hasMoreElements();)
            {
                TagItem tagitem = (TagItem)enumeration.nextElement();
                FormPanel formpanel = getFormPanelFrom(tagitem);
                int i = 0;
                if(formpanel != null)
                    i = formpanel.getComponentCount();
                for(int j = 0; j < i; j++)
                {
                    Component component = formpanel.getComponent(j);
                    if(!flag1)
                    {
                        if(actionevent.getSource() == component)
                            flag1 = true;
                        continue;
                    }
                    if(!(component instanceof FormTextField))
                        continue;
                    flag = true;
                    break;
                }

                if(flag)
                    break;
            }

            if(flag)
            {
                transferFocus();
                return;
            } else
            {
                submit(actionevent);
                return;
            }

        case 5: // '\005'
            submit(actionevent);
            return;

        default:
            return;
        }
    }

    public int getType()
    {
        return type;
    }

    protected HotJavaBrowserBean getContainingHotJavaBrowserBean()
    {
        return HotJavaBrowserBean.getContainingHotJavaBrowserBean(this);
    }

    private int getListSizeAttribute(Document document, TagItem tagitem)
    {
        int i = getIntAttribute("size", -1);
        if(i <= 0)
        {
            i = 0;
            int j = tagitem.getIndex() + tagitem.getOffset();
            for(int k = tagitem.getIndex(); k < j; k++)
                if(document.items[k] instanceof OPTION)
                    i++;

        }
        if(i > 20)
            i = 20;
        else
        if(i < 1)
            i = 1;
        return i;
    }

    public void addAWTListener(ComponentListener componentlistener)
    {
        if(comp != null)
            comp.addComponentListener(componentlistener);
    }

    public void addAWTListener(FocusListener focuslistener)
    {
        if(comp != null)
            comp.addFocusListener(focuslistener);
    }

    public void addAWTListener(KeyListener keylistener)
    {
        if(comp != null)
            comp.addKeyListener(keylistener);
    }

    public void addAWTListener(MouseListener mouselistener)
    {
        if(comp != null)
            comp.addMouseListener(mouselistener);
    }

    public void addAWTListener(MouseMotionListener mousemotionlistener)
    {
        if(comp != null)
            comp.addMouseMotionListener(mousemotionlistener);
    }

    private boolean isSelectedTextExists()
    {
        return ((TextComponent)comp).getSelectionStart() != ((TextComponent)comp).getSelectionEnd();
    }

    public void setObsolete(boolean flag)
    {
    }

    static final int TEXT = 1;
    static final int PASSWORD = 2;
    static final int CHECKBOX = 3;
    static final int RADIO = 4;
    static final int SUBMIT = 5;
    static final int RESET = 6;
    static final int IMAGE = 7;
    static final int HIDDEN = 8;
    static final int TEXTAREA = 9;
    static final int CHOICE = 10;
    static final int LIST = 11;
    static final int FILE = 12;
    static final int BUTTON = 13;
    Formatter win;
    Document doc;
    TagItem item;
    Component comp;
    private java.util.Vector optionStrings;
    private java.util.Vector optionValues;
    private int defaultSelectedIndex;
    private boolean usingComboBox;
    private boolean submitsAllowed;
    String maxOptionString;
    int type;
    int sizeAttribute;
    String imageSubmitClickLoc;
    String docSel;
    FormMouseListener formMouseListener;
    FormFocusListener formFocusListener;

}
