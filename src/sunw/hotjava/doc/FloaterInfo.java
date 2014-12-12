// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   FloaterInfo.java

package sunw.hotjava.doc;


// Referenced classes of package sunw.hotjava.doc:
//            TagItem, DocStyle

public class FloaterInfo
{

    public int getStartY()
    {
        return startY;
    }

    public boolean inRange(int i)
    {
        return i >= startY && i < startY + height;
    }

    public String toString()
    {
        return "FloaterInfo[isLeft=" + isLeft + ",startY=" + startY + ",height=" + height + ",invalid=" + invalid;
    }

    public FloaterInfo()
    {
    }

    TagItem item;
    DocStyle style;
    boolean isLeft;
    int startY;
    int height;
    boolean invalid;
}
