// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   FileURLConnection.java

package sunw.hotjava.protocol.file;

import java.text.Collator;
import sunw.hotjava.misc.Compare;

// Referenced classes of package sunw.hotjava.protocol.file:
//            FileURLConnection

class StringCompare
    implements Compare
{

    public StringCompare()
    {
        try
        {
            Class.forName("java.text.Collator");
            collator = Collator.getInstance();
            return;
        }
        catch(ClassNotFoundException _ex)
        {
            collator = null;
        }
    }

    public int doCompare(Object obj, Object obj1)
    {
        String s = (String)obj;
        String s1 = (String)obj1;
        if(collator != null)
            return collator.compare(s, s1);
        else
            return s.compareTo(s1);
    }

    Collator collator;
}
