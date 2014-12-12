// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   EdgeInfo.java

package sunw.hotjava.tags;


class EdgeInfo
{

    EdgeInfo(boolean flag, int i)
    {
        isVertical = flag;
        edgeValue = i;
    }

    int getValue()
    {
        return edgeValue;
    }

    boolean isVertical()
    {
        return isVertical;
    }

    int edgeValue;
    boolean isVertical;
}
