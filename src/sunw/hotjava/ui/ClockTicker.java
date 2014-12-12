// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ClockTicker.java

package sunw.hotjava.ui;

import java.util.Observable;

public class ClockTicker extends Observable
    implements Runnable
{

    ClockTicker()
    {
        ticker = new Thread(this, "HotJava Clock");
    }

    public void start()
    {
        if(ticker != null)
            ticker.start();
    }

    public void stop()
    {
        if(ticker != null && ticker.isAlive())
            ticker.stop();
        ticker = null;
    }

    public void run()
    {
        do
        {
            try
            {
                Thread.sleep(1000L);
            }
            catch(InterruptedException _ex) { }
            setChanged();
            notifyObservers();
        } while(true);
    }

    Thread ticker;
}
