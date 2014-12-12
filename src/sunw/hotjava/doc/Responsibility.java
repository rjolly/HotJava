// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Responsibility.java

package sunw.hotjava.doc;


// Referenced classes of package sunw.hotjava.doc:
//            DocConstants, DocPanel

public class Responsibility
    implements DocConstants
{

    Responsibility(DocPanel docpanel, int i, int j)
    {
        target = docpanel;
        startIndex = i;
        endIndex = j;
    }

    public DocPanel getTarget()
    {
        return target;
    }

    public int getStartPos()
    {
        return startIndex << 12;
    }

    public int getLength()
    {
        return endIndex - startIndex;
    }

    public String toString()
    {
        return "Responsibility[startIndex=" + startIndex + ", endIndex=" + endIndex + ", target=" + target + "]";
    }

    int startIndex;
    int endIndex;
    DocPanel target;
}
