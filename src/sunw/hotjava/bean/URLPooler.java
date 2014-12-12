// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   URLPooler.java

package sunw.hotjava.bean;

import java.util.Date;

// Referenced classes of package sunw.hotjava.bean:
//            URLPoolListener, PoolEntry

public interface URLPooler
{

    public abstract void addURLPoolListener(URLPoolListener urlpoollistener);

    public abstract void removeURLPoolListener(URLPoolListener urlpoollistener);

    public abstract void add(String s);

    public abstract boolean createURLPoolFromFile(String s);

    public abstract void saveURLPoolToFile(String s);

    public abstract void addToURLPool(PoolEntry apoolentry[]);

    public abstract PoolEntry[] getURLPoolEntries();

    public abstract void setURLExpirationAmt(int i);

    public abstract void discardAllURLs();

    public abstract void purgeExpiredURLs();

    public abstract Date get(String s);
}
