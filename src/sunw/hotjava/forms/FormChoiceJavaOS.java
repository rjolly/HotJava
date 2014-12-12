// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   FormChoiceJavaOS.java

package sunw.hotjava.forms;

import java.awt.*;
import java.awt.event.*;
import sunw.hotjava.misc.Globals;
import sunw.hotjava.misc.HJBProperties;

// Referenced classes of package sunw.hotjava.forms:
//            MoreChoicesJavaOSDialog

public class FormChoiceJavaOS extends Choice
{

    public FormChoiceJavaOS()
    {
        if(lazyLoadingNotDone)
        {
            HJBProperties hjbproperties = HJBProperties.getHJBProperties("beanPropertiesKey");
            more = hjbproperties.getProperty("forms.choice.more.choices");
            lazyLoadingNotDone = false;
        }
        ItemListener itemlistener = new ItemListener() {

            public void itemStateChanged(ItemEvent itemevent)
            {
                doItemEvent(itemevent);
            }

        }
;
        addItemListener(itemlistener);
        enableEvents(16L);
        firstVisible = -1;
        lastVisible = -1;
        store = null;
    }

    public void doItemEvent(ItemEvent itemevent)
    {
        Object aobj[] = itemevent.getItemSelectable().getSelectedObjects();
        if((aobj[0] instanceof String) && ((String)aobj[0]).equals(more))
        {
            removeSpecialItems();
            select(selectedIndex);
            java.awt.Container container;
            for(container = getParent(); container != null; container = container.getParent())
                if(container instanceof Frame)
                    break;

            if(container != null)
            {
                MoreChoicesJavaOSDialog morechoicesjavaosdialog = new MoreChoicesJavaOSDialog(this, selectedIndex, (Frame)container);
                morechoicesjavaosdialog.pack();
                Dimension dimension = container.getSize();
                Dimension dimension1 = morechoicesjavaosdialog.getSize();
                Point point = container.getLocationOnScreen();
                morechoicesjavaosdialog.setLocation((dimension.width / 2 - dimension1.width / 2) + point.x, (dimension.height / 2 - dimension1.height / 2) + point.y);
                morechoicesjavaosdialog.show();
                morechoicesjavaosdialog.dispose();
            }
        }
    }

    protected void processEvent(AWTEvent awtevent)
    {
        if((awtevent instanceof MouseEvent) && awtevent.getID() == 504)
            prepareItems(getLocationOnScreen());
        super.processEvent(awtevent);
    }

    private void prepareItems(Point point)
    {
        removeSpecialItems();
        selectedIndex = getSelectedIndex();
        int i = Toolkit.getDefaultToolkit().getScreenSize().height;
        int j = getMinimumSize().height;
        firstVisible = selectedIndex - Math.round(point.y / j);
        if(firstVisible <= 0 || firstVisible >= selectedIndex)
            firstVisible = -1;
        lastVisible = (selectedIndex - 1) + Math.round((i - point.y) / j);
        int k = (i - point.y) % j;
        if(k > 8)
            lastVisible++;
        if(lastVisible >= getItemCount() || lastVisible <= selectedIndex)
            lastVisible = -1;
        addSpecialItems();
    }

    private void removeSpecialItems()
    {
        if(store == null)
        {
            firstVisible = -1;
            lastVisible = -1;
            return;
        }
        int i = getSelectedIndex();
        if(lastVisible != -1 && more.equals(getItem(lastVisible)))
        {
            remove(lastVisible);
            insert(store, lastVisible);
            lastVisible = -1;
            store = null;
        } else
        if(firstVisible != -1 && more.equals(getItem(firstVisible)))
        {
            remove(firstVisible);
            insert(store, firstVisible);
            firstVisible = -1;
            store = null;
        }
        select(i);
    }

    private void addSpecialItems()
    {
        if(lastVisible != -1)
        {
            store = getItem(lastVisible);
            remove(lastVisible);
            insert(more, lastVisible);
            firstVisible = -1;
            return;
        }
        if(firstVisible != -1)
        {
            store = getItem(firstVisible);
            remove(firstVisible);
            insert(more, firstVisible);
        }
    }

    private static String more = null;
    private static boolean lazyLoadingNotDone = true;
    int firstVisible;
    int lastVisible;
    String store;
    int selectedIndex;

}
