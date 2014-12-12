// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   PageInfo.java

package sunw.hotjava.doc;


class PageInfo
{

    PageInfo(int i, int j)
    {
        isFinished = false;
        startLine = i;
        verticalOffset = j;
    }

    void markFinished()
    {
        isFinished = true;
    }

    boolean isFinished()
    {
        return isFinished;
    }

    public String toString()
    {
        return "pageInfo[isFinished=" + isFinished + ", startLine=" + startLine + ", endLine=" + endLine + ", verticalOffset is " + verticalOffset;
    }

    int startLine;
    int verticalOffset;
    int endLine;
    private boolean isFinished;
}
