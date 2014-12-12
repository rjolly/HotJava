// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   StaticStateWatcher.java

package sunw.hotjava.misc;

import java.util.Date;
import java.util.Hashtable;

// Referenced classes of package sunw.hotjava.misc:
//            HttpCookie

public class StaticStateWatcher
{
    public static abstract class Action
    {

        public abstract void doit(StaticStateWatcher staticstatewatcher);

        public Action()
        {
        }
    }


    public boolean vetoCookieAdd(Hashtable hashtable, HttpCookie httpcookie, HttpCookie httpcookie1)
    {
        return false;
    }

    public void notifyCookieAdd(Hashtable hashtable, HttpCookie httpcookie, HttpCookie httpcookie1)
    {
    }

    public boolean vetoCookieListChange(Hashtable hashtable, Hashtable hashtable1)
    {
        return false;
    }

    public void notifyCookieListChange(Hashtable hashtable, Hashtable hashtable1)
    {
    }

    public void notifyURLPoolAdd(Hashtable hashtable, String s, Date date, Date date1)
    {
    }

    public void notifyURLPoolReplace(Hashtable hashtable, Hashtable hashtable1)
    {
    }

    public void notifyPropertySet(Object obj, Object obj1)
    {
    }

    public void notifyNewUserProperties(Hashtable hashtable, Hashtable hashtable1)
    {
    }

    public StaticStateWatcher()
    {
    }
}
