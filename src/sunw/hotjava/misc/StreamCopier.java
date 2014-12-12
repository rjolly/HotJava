// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   StreamCopier.java

package sunw.hotjava.misc;

import java.io.*;
import java.util.Observable;
import java.util.Observer;

public class StreamCopier extends Observable
    implements Runnable
{

    public StreamCopier(InputStream inputstream, OutputStream outputstream, Observer observer)
    {
        saveURL = false;
        interrupted = false;
        in = inputstream;
        out = outputstream;
        o = observer;
        saveURL = true;
        addObserver(observer);
    }

    public StreamCopier(BufferedReader bufferedreader, OutputStreamWriter outputstreamwriter, Observer observer)
    {
        saveURL = false;
        interrupted = false;
        in = bufferedreader;
        out = outputstreamwriter;
        o = observer;
        addObserver(observer);
    }

    public void transfer()
    {
        run();
    }

    public void run()
    {
        boolean flag = true;
        int j = 0;
        byte abyte0[] = null;
        char ac[] = null;
        if(saveURL)
            abyte0 = new byte[2048];
        else
            ac = new char[2048];
        setChanged();
        notifyObservers(new Integer(0));
        try
        {
            while(!interrupted) 
            {
                int i;
                if(saveURL)
                    i = ((InputStream)in).read(abyte0);
                else
                    i = ((BufferedReader)in).read(ac);
                if(i < 0)
                    break;
                if(saveURL)
                    ((OutputStream)out).write(abyte0, 0, i);
                else
                    ((OutputStreamWriter)out).write(ac, 0, i);
                j += i;
                setChanged();
                notifyObservers(new Integer(j));
            }
        }
        catch(Exception _ex)
        {
            System.err.println("Exception while copying streams. " + j + " bytes transferred.");
        }
        setChanged();
        notifyObservers(new Integer(-1));
        if(o != null)
            deleteObserver(o);
        if(saveURL)
        {
            try
            {
                ((InputStream)in).close();
            }
            catch(Exception _ex) { }
            try
            {
                ((OutputStream)out).close();
                return;
            }
            catch(Exception _ex)
            {
                return;
            }
        }
        try
        {
            ((BufferedReader)in).close();
        }
        catch(Exception _ex) { }
        try
        {
            ((OutputStreamWriter)out).close();
            return;
        }
        catch(Exception _ex)
        {
            return;
        }
    }

    public void stop()
    {
        interrupted = true;
    }

    private Object in;
    private Object out;
    private boolean saveURL;
    private boolean interrupted;
    private Observer o;
}
