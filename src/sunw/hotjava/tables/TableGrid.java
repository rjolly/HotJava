// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   TableGrid.java

package sunw.hotjava.tables;

import java.awt.Container;
import java.util.Vector;
import sunw.hotjava.doc.DocumentState;

// Referenced classes of package sunw.hotjava.tables:
//            TableElementPanel, TablePanel

class TableGrid
{

    TableGrid()
    {
        rows = new Vector(5);
    }

    TableGrid(TableGrid tablegrid, TablePanel tablepanel, DocumentState documentstate)
    {
        rows = new Vector(5);
        int i = tablegrid.rows.size();
        rows = new Vector(i);
        for(int j = 0; j < i; j++)
        {
            Vector vector = (Vector)tablegrid.rows.elementAt(j);
            int k = vector.size();
            for(int l = 0; l < k; l++)
                if(tablegrid.isUpperLeft(l, j))
                {
                    TableElementPanel tableelementpanel = tablegrid.elementAt(l, j);
                    if(tableelementpanel != null)
                    {
                        int i1 = tableelementpanel.getColSpan();
                        int j1 = tableelementpanel.getRowSpan();
                        TableElementPanel tableelementpanel1 = new TableElementPanel(tableelementpanel, tablepanel, documentstate);
                        addElement(tableelementpanel1, l, j, i1, j1);
                        tablepanel.add(tableelementpanel1);
                    }
                }

        }

    }

    void addElement(TableElementPanel tableelementpanel, int i, int j, int k, int l)
    {
        int i1 = j + l;
        int j1 = i + k;
        for(int k1 = rows.size(); k1 < i1; k1++)
            rows.addElement(new Vector(j1));

        for(int l1 = j; l1 < i1; l1++)
        {
            Vector vector = (Vector)rows.elementAt(l1);
            padColsTo(vector, j1);
            for(int i2 = i; i2 < j1; i2++)
                vector.setElementAt(tableelementpanel, i2);

        }

    }

    void insertCaption(TableElementPanel tableelementpanel, boolean flag, int i)
    {
        Vector vector = new Vector();
        padColsTo(vector, i);
        for(int j = 0; j < i; j++)
            vector.setElementAt(tableelementpanel, j);

        if(flag)
        {
            rows.insertElementAt(vector, 0);
            return;
        } else
        {
            rows.addElement(vector);
            return;
        }
    }

    void removeColumn(int i)
    {
        for(int j = 0; j < rows.size(); j++)
        {
            Vector vector = (Vector)rows.elementAt(j);
            if(i < vector.size())
                vector.removeElementAt(i);
        }

    }

    void removeRow(int i)
    {
        if(i >= rows.size() || i < 0)
        {
            return;
        } else
        {
            rows.removeElementAt(i);
            return;
        }
    }

    int findNextFreeCol(int i)
    {
        if(i >= rows.size())
            return 0;
        Vector vector = (Vector)rows.elementAt(i);
        for(int j = 0; j < vector.size(); j++)
            if(vector.elementAt(j) == null)
                return j;

        return vector.size();
    }

    TableElementPanel elementAt(int i, int j)
    {
        if(j < 0 || j >= rows.size())
            return null;
        Vector vector = (Vector)rows.elementAt(j);
        if(i < 0 || i >= vector.size())
            return null;
        else
            return (TableElementPanel)vector.elementAt(i);
    }

    boolean isUpperLeft(int i, int j)
    {
        TableElementPanel tableelementpanel = elementAt(i, j);
        if(i > 0 && elementAt(i - 1, j) == tableelementpanel)
            return false;
        return j <= 0 || elementAt(i, j - 1) != tableelementpanel;
    }

    private void padColsTo(Vector vector, int i)
    {
        for(; vector.size() < i; vector.addElement(null));
    }

    Vector rows;
}
