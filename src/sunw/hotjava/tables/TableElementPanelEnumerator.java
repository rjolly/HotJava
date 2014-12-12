// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   TablePanel.java

package sunw.hotjava.tables;

import java.awt.Container;
import java.util.Enumeration;

// Referenced classes of package sunw.hotjava.tables:
//            TablePanel

class TableElementPanelEnumerator
    implements Enumeration
{

    TableElementPanelEnumerator(TablePanel tablepanel)
    {
        tp = tablepanel;
        index = 0;
        numComponents = tablepanel.getComponentCount();
    }

    public boolean hasMoreElements()
    {
        if(index < numComponents)
        {
            return true;
        } else
        {
            tp = null;
            return false;
        }
    }

    public Object nextElement()
    {
        if(index >= numComponents)
            return null;
        else
            return tp.getComponent(index++);
    }

    TablePanel tp;
    int index;
    int numComponents;
}
