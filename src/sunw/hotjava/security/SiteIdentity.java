// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   SiteIdentity.java

package sunw.hotjava.security;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.Principal;
import sunw.hotjava.misc.HJBProperties;

// Referenced classes of package sunw.hotjava.security:
//            SecurityGlobals

public class SiteIdentity
    implements Principal, Serializable
{

    public SiteIdentity()
    {
    }

    public SiteIdentity(String s)
    {
        try
        {
            address = InetAddress.getByName(s);
            return;
        }
        catch(UnknownHostException _ex)
        {
            address = null;
        }
        name = s;
    }

    public boolean equals(Object obj)
    {
        if(!(obj instanceof SiteIdentity))
            return false;
        SiteIdentity siteidentity = (SiteIdentity)obj;
        if(address != null)
            return address.equals(siteidentity.address);
        else
            return name.equals(siteidentity.name);
    }

    public String toString()
    {
        if(address != null)
            return address.toString();
        else
            return name;
    }

    public int hashCode()
    {
        if(address != null)
            return address.hashCode();
        else
            return name.hashCode();
    }

    public String getName()
    {
        if(address != null)
            return address.getHostName();
        else
            return name;
    }

    public String getIP()
    {
        if(address != null)
        {
            return address.getHostAddress();
        } else
        {
            HJBProperties hjbproperties = HJBProperties.getHJBProperties("securityProperties");
            return hjbproperties.getProperty("hotjava.site.none.found", "none found");
        }
    }

    static final long serialVersionUID = 0xbad7ee48cdd7798dL;
    private InetAddress address;
    String name;
}
