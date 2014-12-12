// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Formatter.java

package sunw.hotjava.doc;

import java.util.Enumeration;

// Referenced classes of package sunw.hotjava.doc:
//            Formatter, ProcessActivationQueueThread, ResponsibilityEnumeration

class ResponsibilityRangeEnumeration
    implements Enumeration
{

    ResponsibilityRangeEnumeration(Formatter formatter, int i, int j)
    {
        theFormatter = formatter;
        index = i;
        endIndex = j;
    }

    public boolean hasMoreElements()
    {
        return index <= endIndex && index >= 0;
    }

    public Object nextElement()
    {
        return theFormatter.responsibilityAt(index++);
    }

    int index;
    int endIndex;
    Formatter theFormatter;
}
