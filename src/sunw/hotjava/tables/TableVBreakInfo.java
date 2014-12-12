// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   TableVBreakInfo.java

package sunw.hotjava.tables;

import sunw.hotjava.doc.CompoundVBreakInfo;

class TableVBreakInfo extends CompoundVBreakInfo
{

    int getStartRow()
    {
        return startRow;
    }

    void setStartRow(int i)
    {
        startRow = i;
    }

    int getEndRow()
    {
        return endRow;
    }

    void setEndRow(int i)
    {
        endRow = i;
    }

    TableVBreakInfo()
    {
    }

    private int startRow;
    private int endRow;
}
