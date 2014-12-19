// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ProgressData.java

package sun.net;

import java.net.URL;
import java.util.Observable;

// Referenced classes of package sun.net:
//            ProgressEntry

public class ProgressData extends Observable
{

    public synchronized ProgressEntry[] getStreams()
    {
        return (ProgressEntry[])streams.clone();
    }

    public void register(ProgressEntry progressentry)
    {
        boolean flag = false;
        for(int i = 0; i < streams.length; i++)
            synchronized(this)
            {
                if(streams[i] == null)
                {
                    streams[i] = progressentry;
                    progressentry.what = 0;
                    progressentry.index = i;
                    flag = true;
                    break;
                }
            }

        if(flag)
        {
            setChanged();
            notifyObservers(progressentry);
        }
    }

    public void connected(URL url)
    {
    }

    public void unregister(ProgressEntry progressentry)
    {
        synchronized(this)
        {
            int i = progressentry.index;
            if(i < 0 || i > streams.length || streams[i] != progressentry)
                return;
            progressentry.what = 3;
            streams[i] = null;
            setChanged();
        }
        notifyObservers(progressentry);
    }

    public void update(ProgressEntry progressentry)
    {
        synchronized(this)
        {
            int i = progressentry.index;
            if(i < 0 || i > streams.length || streams[i] != progressentry)
                return;
            progressentry.what = 2;
            if(!progressentry.connected())
                progressentry.what = 1;
            if(progressentry.read >= progressentry.need && progressentry.read != 0)
            {
                streams[i] = null;
                progressentry.what = 3;
            }
            setChanged();
        }
        notifyObservers(progressentry);
    }

    public ProgressData()
    {
        streams = new ProgressEntry[20];
    }

    public static ProgressData pdata = new ProgressData();
    public static final int NEW = 0;
    public static final int CONNECTED = 1;
    public static final int UPDATE = 2;
    public static final int DELETE = 3;
    private ProgressEntry streams[];

}
