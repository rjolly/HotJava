// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   PageMargins.java

package sunw.hotjava.doc;


class PageMargins
{

    int getTopMargin()
    {
        return topMargin;
    }

    int getBottomMargin()
    {
        return bottomMargin;
    }

    int getLeftMargin()
    {
        return leftMargin;
    }

    int getVertMargin()
    {
        return topMargin + bottomMargin;
    }

    int getHorizMargin()
    {
        return leftMargin + rightMargin;
    }

    void setTopMargin(int i)
    {
        topMargin = i;
    }

    void setBottomMargin(int i)
    {
        bottomMargin = i;
    }

    void setLeftMargin(int i)
    {
        leftMargin = i;
    }

    void setRightMargin(int i)
    {
        rightMargin = i;
    }

    public String toString()
    {
        return "PageMargins[top=" + topMargin + ", left=" + leftMargin + ", bottom=" + bottomMargin + ", right=" + rightMargin + "]";
    }

    PageMargins()
    {
    }

    private int topMargin;
    private int bottomMargin;
    private int leftMargin;
    private int rightMargin;
}
