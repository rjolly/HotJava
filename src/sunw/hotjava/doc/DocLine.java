// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   DocLine.java

package sunw.hotjava.doc;


// Referenced classes of package sunw.hotjava.doc:
//            DocConstants

public class DocLine
    implements DocConstants
{

    public int getAbove()
    {
        return baseline - lnascent;
    }

    public int getBelow()
    {
        return height - baseline - lndescent;
    }

    public DocLine(int i, int j, int k, int l, int i1, int j1, int k1, 
            int l1, int i2, int j2)
    {
        start = i;
        end = j;
        tail = k;
        margin = l;
        width = i1;
        lnascent = l1;
        lndescent = i2;
        baseline = j1 + l1;
        height = j1 + k1 + l1 + i2;
        textAscent = j2;
    }

    public String toString()
    {
        return (start >> 12) + "/" + (start & 0xfff) + " - " + (end >> 12) + "/" + (end & 0xfff) + " - " + (tail >> 12) + "/" + (tail & 0xfff) + " " + y + "/" + height + "/" + baseline + "{" + textAscent + "}" + (updated ? " *" : "");
    }

    public boolean updated;
    public int y;
    public int start;
    public int end;
    public int tail;
    public int height;
    public int baseline;
    public int lnascent;
    public int lndescent;
    public int margin;
    public int width;
    public int textAscent;
}
