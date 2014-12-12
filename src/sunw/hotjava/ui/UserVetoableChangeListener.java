// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   UserVetoableChangeListener.java

package sunw.hotjava.ui;

import java.beans.*;
import java.net.URL;
import sunw.hotjava.bean.CurrentDocument;
import sunw.hotjava.misc.HJBProperties;

// Referenced classes of package sunw.hotjava.ui:
//            SecureSiteWarnDialog

public class UserVetoableChangeListener
    implements VetoableChangeListener
{

    public void vetoableChange(PropertyChangeEvent propertychangeevent)
        throws PropertyVetoException
    {
        HJBProperties hjbproperties = HJBProperties.getHJBProperties("hjbrowser");
        Object obj = null;
        Object obj1 = null;
        Object obj2 = null;
        Object obj3 = null;
        if(propertychangeevent.getPropertyName().equals("currentDocument") && (propertychangeevent.getOldValue() instanceof CurrentDocument) && (propertychangeevent.getNewValue() instanceof CurrentDocument))
        {
            URL url = ((CurrentDocument)propertychangeevent.getOldValue()).documentURL;
            String s;
            if(url != null)
                s = url.getProtocol();
            else
                return;
            URL url1 = ((CurrentDocument)propertychangeevent.getNewValue()).documentURL;
            if(lastUrl != null && lastUrl != url)
            {
                lastUrl = url1;
                return;
            }
            lastUrl = url1;
            String s1 = url1.getProtocol();
            if(hjbproperties.getProperty("security.preference.tosecuresite.warn").equals("true") && !s.equals("https") && s1.equals("https"))
            {
                SecureSiteWarnDialog securesitewarndialog = new SecureSiteWarnDialog(true);
                securesitewarndialog.handleDialog();
            }
            if(hjbproperties.getProperty("security.preference.fromsecuresite.warn").equals("true") && s.equals("https") && !s1.equals("https"))
            {
                SecureSiteWarnDialog securesitewarndialog1 = new SecureSiteWarnDialog(false);
                securesitewarndialog1.handleDialog();
            }
        }
    }

    public UserVetoableChangeListener()
    {
    }

    private static URL lastUrl;
}
