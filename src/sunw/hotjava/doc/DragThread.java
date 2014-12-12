// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   DocumentFormatter.java

package sunw.hotjava.doc;

import java.awt.event.MouseEvent;

// Referenced classes of package sunw.hotjava.doc:
//            DocumentFormatter

class DragThread extends Thread
{

    DragThread(int i, int j, int k, DocumentFormatter documentformatter, MouseEvent mouseevent)
    {
        waiter = new Object();
        deltaX = i;
        deltaY = j;
        delay = k;
        df = documentformatter;
        evt = mouseevent;
    }

    void notifyDragThread(int i, int j)
    {
        deltaX = i;
        deltaY = j;
        synchronized(waiter)
        {
            waiter.notify();
        }
    }

    public void run()
    {
        do
        {
            try
            {
                synchronized(waiter)
                {
                    waiter.wait(delay);
                }
            }
            catch(InterruptedException _ex)
            {
                continue;
            }
            evt.translatePoint(deltaX, deltaY);
            df.handleMouseDrag(evt);
        } while(true);
    }

    int deltaX;
    int deltaY;
    int delay;
    DocumentFormatter df;
    MouseEvent evt;
    Object waiter;
}
