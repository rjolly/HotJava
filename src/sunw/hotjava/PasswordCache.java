// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   HJFrame.java

package sunw.hotjava;

import java.net.URL;
import java.util.Vector;

// Referenced classes of package sunw.hotjava:
//            HJFrame, PasswordCacheEntry

class PasswordCache
{

    synchronized PasswordCacheEntry getCacheEntry(URL url)
    {
        for(int i = 0; i < entries.size(); i++)
        {
            PasswordCacheEntry passwordcacheentry = (PasswordCacheEntry)entries.elementAt(i);
            if(url.equals(passwordcacheentry.url))
                return passwordcacheentry;
        }

        return null;
    }

    synchronized PasswordCacheEntry registerPassword(URL url, String s)
    {
        PasswordCacheEntry passwordcacheentry = new PasswordCacheEntry(url, s);
        for(int i = 0; i < entries.size(); i++)
        {
            PasswordCacheEntry passwordcacheentry1 = (PasswordCacheEntry)entries.elementAt(i);
            if(url.equals(passwordcacheentry1.url))
            {
                entries.setElementAt(passwordcacheentry, i);
                return passwordcacheentry;
            }
        }

        entries.addElement(passwordcacheentry);
        return passwordcacheentry;
    }

    synchronized void removeEntry(PasswordCacheEntry passwordcacheentry)
    {
        for(int i = 0; i < entries.size(); i++)
            if(passwordcacheentry == entries.elementAt(i))
            {
                entries.removeElementAt(i);
                return;
            }

    }

    PasswordCache()
    {
        entries = new Vector();
    }

    private Vector entries;
}
