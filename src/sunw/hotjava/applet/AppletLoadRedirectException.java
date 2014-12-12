// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   AppletLoadRedirectException.java

package sunw.hotjava.applet;

import java.net.URL;
import sunw.hotjava.misc.HJBProperties;
import sunw.hotjava.security.SecurityGlobals;

// Referenced classes of package sunw.hotjava.applet:
//            AppletIOException

public class AppletLoadRedirectException extends AppletIOException
{

    public AppletLoadRedirectException(String s, URL url, URL url1)
    {
        super(s, url);
        target = url1;
    }

    public URL getTarget()
    {
        return target;
    }

    public String getLocalizedMessage()
    {
        HJBProperties hjbproperties = HJBProperties.getHJBProperties("securityProperties");
        String s = super.arg != null ? super.arg.toString() : "??";
        String s1 = target != null ? target.toString() : "??";
        return hjbproperties.getPropertyReplace(AppletIOException.propPrefix + super.key, s, s1);
    }

    private URL target;
}
