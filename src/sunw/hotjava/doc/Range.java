// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Range.java

package sunw.hotjava.doc;


class Range
{

    public Range()
    {
    }

    public Range(int i, int j)
    {
        minVal = i;
        maxVal = j;
    }

    public String toString()
    {
        return "Range[" + minVal + "," + maxVal + "]";
    }

    public int minVal;
    public int maxVal;
}
