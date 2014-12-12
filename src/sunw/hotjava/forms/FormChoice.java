// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   FormChoice.java

package sunw.hotjava.forms;

import java.awt.*;
import java.awt.event.*;
import sunw.hotjava.misc.Globals;
import sunw.hotjava.misc.HJBProperties;

// Referenced classes of package sunw.hotjava.forms:
//            MoreChoicesDialog

public class FormChoice extends Choice
{

    public FormChoice()
    {
        firstItem = true;
        itemReplaced = false;
        if(lazyLoadingNotDone)
        {
            HJBProperties hjbproperties = HJBProperties.getHJBProperties("beanPropertiesKey");
            more = hjbproperties.getProperty("forms.choice.more.choices");
            lazyLoadingNotDone = false;
        }
        usingExtraStrings = false;
        ItemListener itemlistener = new ItemListener() {

            public void itemStateChanged(ItemEvent itemevent)
            {
                doItemEvent(itemevent);
            }

        }
;
        MouseAdapter mouseadapter = new MouseAdapter() {

            public void mousePressed(MouseEvent mouseevent)
            {
                doMouseEvent(mouseevent);
            }

        }
;
        addMouseListener(mouseadapter);
        addItemListener(itemlistener);
        currentHeight = 0;
    }

    public void addItem(String s)
    {
        if(firstItem)
        {
            firstItem = false;
            lastSelection = s;
        }
        super.addItem(s);
    }

    public void setLastSelectedIndex(int i)
    {
        lastSelectedIndex = i;
    }

    public int getSelectedIndex()
    {
        if(usingExtraStrings)
            return lastSelectedIndex;
        else
            return super.getSelectedIndex();
    }

    public String getItem(int i)
    {
        if(usingExtraStrings)
        {
            if(i > getItemCount() - 1)
                return extraStrings[i - (getItemCount() - 1)];
            else
                return super.getItem(i);
        } else
        {
            return super.getItem(i);
        }
    }

    public void doItemEvent(ItemEvent itemevent)
    {
        if(usingExtraStrings)
        {
            Object aobj[] = itemevent.getItemSelectable().getSelectedObjects();
            if((aobj[0] instanceof String) && ((String)aobj[0]).equals(more))
            {
                java.awt.Container container;
                for(container = getParent(); container != null; container = container.getParent())
                    if(container instanceof Frame)
                        break;

                if(container != null)
                {
                    MoreChoicesDialog morechoicesdialog = new MoreChoicesDialog(this, (Frame)container);
                    morechoicesdialog.pack();
                    Dimension dimension = container.getSize();
                    Dimension dimension1 = morechoicesdialog.getSize();
                    Point point = container.getLocationOnScreen();
                    morechoicesdialog.setLocation((dimension.width / 2 - dimension1.width / 2) + point.x, (dimension.height / 2 - dimension1.height / 2) + point.y);
                    morechoicesdialog.show();
                    morechoicesdialog.dispose();
                    return;
                }
            } else
            {
                lastSelection = ((Choice)itemevent.getItemSelectable()).getSelectedItem();
                lastSelectedIndex = super.getSelectedIndex();
            }
        }
    }

    public void doMouseEvent(MouseEvent mouseevent)
    {
        if(itemReplaced)
        {
            remove(getItemCount() - 2);
            insert(replacedItem, getItemCount() - 1);
            itemReplaced = false;
        }
    }

    public void replaceItem(int i, String s)
    {
        itemReplaced = true;
        replacedItem = getItem(i);
        remove(i);
        insert(s, i);
    }

    String[] getExtraStrings()
    {
        return extraStrings;
    }

    String getLastSelection()
    {
        return lastSelection;
    }

    void setLastSelection(String s)
    {
        lastSelection = s;
    }

    public void addNotify()
    {
        super.addNotify();
        Dimension dimension = getMinimumSize();
        int i = dimension.height * getItemCount();
        Dimension dimension1 = Toolkit.getDefaultToolkit().getScreenSize();
        if(i > dimension1.height)
        {
            int j = getSelectedIndex();
            usingExtraStrings = true;
            int k = dimension1.height / dimension.height - 1;
            extraStrings = new String[getItemCount() - k];
            int l = k;
            for(int i1 = 0; l < getItemCount(); i1++)
            {
                extraStrings[i1] = getItem(l);
                l++;
            }

            for(int j1 = getItemCount() - 1; j1 >= k; j1--)
                remove(j1);

            int k1 = getItemCount() - 1;
            if(j > k1)
            {
                String s = extraStrings[j - getItemCount()];
                replaceItem(k1, s);
                select(k1);
                setLastSelection(s);
                setLastSelectedIndex(j);
            }
            add(more);
        }
    }

    public void select(int i)
    {
        lastSelectedIndex = i;
        super.select(i);
    }

    private String extraStrings[];
    private boolean usingExtraStrings;
    private static String more = null;
    private boolean firstItem;
    private boolean itemReplaced;
    private int currentHeight;
    private String lastSelection;
    private String replacedItem;
    private int lastSelectedIndex;
    private static boolean lazyLoadingNotDone = true;

}
