// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Formatter.java

package sunw.hotjava.doc;


// Referenced classes of package sunw.hotjava.doc:
//            Formatter, ResponsibilityEnumeration, ResponsibilityRangeEnumeration

class ProcessActivationQueueThread extends Thread
{

    ProcessActivationQueueThread(Formatter formatter1)
    {
        formatter = formatter1;
    }

    public void run()
    {
        formatter.processActivationQueue();
    }

    private Formatter formatter;
}
