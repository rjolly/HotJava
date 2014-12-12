// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Formatter.java

package sunw.hotjava.doc;

import java.util.Enumeration;

// Referenced classes of package sunw.hotjava.doc:
//            Formatter, ProcessActivationQueueThread, ResponsibilityRangeEnumeration

class ResponsibilityEnumeration
    implements Enumeration
{

    ResponsibilityEnumeration(Formatter formatter, int i)
    {
        theFormatter = formatter;
        index = i;
    }

    public boolean hasMoreElements()
    {
        return theFormatter.countResponsiblities() > index;
    }

    public Object nextElement()
    {
        return theFormatter.responsibilityAt(index++);
    }

    int index;
    Formatter theFormatter;
}
