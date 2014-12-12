// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   BrowserSecurityManager.java

package sunw.hotjava.applet;

import java.security.Identity;
import sunw.hotjava.misc.HJBProperties;
import sunw.hotjava.security.*;

// Referenced classes of package sunw.hotjava.applet:
//            HotJavaSecurityManager, ScriptingEngineLoader

public class BrowserSecurityManager extends CommonSecurity
    implements HotJavaSecurityManager
{

    public BrowserSecurityManager()
    {
        this(0);
    }

    public BrowserSecurityManager(int i)
    {
        super.untrusted = PolicyUntrusted.getPolicyUntrusted();
        super.high = PolicyHigh.getPolicyHigh();
        super.medium = PolicyMedium.getPolicyMedium();
        super.low = PolicyLow.getPolicyLow();
        super.none = PolicyNone.getPolicyNone();
        CommonSecurity.depthAdjustor = i;
    }

    private BasePolicy handleSigned(Object aobj[])
    {
        TrustManager trustmanager = TrustManager.getTrustManager();
        if(trustmanager != null)
        {
            Object obj = null;
            Object obj1 = null;
            int i = 0;
            CertificateTrust acertificatetrust[] = trustmanager.getTempCerts();
            CertificateTrust acertificatetrust1[] = trustmanager.getSessionCerts();
            for(int j = 0; j < aobj.length; j++)
                if(aobj[j] instanceof Identity)
                {
                    java.security.Certificate acertificate[] = ((Identity)aobj[j]).certificates();
                    for(int k = 0; k < acertificate.length; k++)
                    {
                        java.security.Certificate certificate = acertificate[k];
                        CertificateTrust certificatetrust = trustmanager.getTrustForCert(certificate);
                        int l = 0;
                        boolean flag = false;
                        for(l = 0; l < acertificatetrust.length; l++)
                        {
                            if(certificatetrust == null || acertificatetrust[l] == null || !certificatetrust.getName().equalsIgnoreCase(acertificatetrust[l].getName()))
                                continue;
                            flag = true;
                            if(i < acertificatetrust[l].getMode())
                                i = acertificatetrust[l].getMode();
                            break;
                        }

                        if(!flag)
                            for(l = 0; l < acertificatetrust1.length; l++)
                            {
                                if(certificatetrust == null || acertificatetrust1[l] == null || !certificatetrust.getName().equalsIgnoreCase(acertificatetrust1[l].getName()))
                                    continue;
                                if(i < acertificatetrust1[l].getMode())
                                    i = acertificatetrust1[l].getMode();
                                break;
                            }

                        if(l == acertificatetrust1.length && certificatetrust != null && i < certificatetrust.getMode() && certificatetrust.getMode() != 9)
                            i = certificatetrust.getMode();
                    }

                }

            if(i == 9)
                return super.untrusted;
            if(i == 10)
                return super.high;
            if(i == 11)
                return super.medium;
            if(i == 12)
                return super.low;
            if(i == 13)
                return getDefaultPermission("hotjava.default.signed.security", "Medium");
        }
        return null;
    }

    protected synchronized BasePolicy findPolicy(ClassLoader classloader)
    {
        if(classloader == null)
            return super.none;
        if(inClassAbove())
            return super.none;
        TrustManager trustmanager = TrustManager.getTrustManager();
        if(trustmanager != null)
        {
            CertificateTrust acertificatetrust[] = trustmanager.getSessionSites();
            int i = 0;
            String s = getSiteName();
            for(int j = 0; j < acertificatetrust.length; j++)
            {
                if(!s.equalsIgnoreCase(acertificatetrust[j].getName()))
                    continue;
                i = acertificatetrust[j].getMode();
                break;
            }

            if(i == 9)
                return super.untrusted;
            if(i == 10)
                return super.high;
            if(i == 11)
                return super.medium;
            if(i == 12)
                return super.low;
            if(i == 13)
                return getDefaultPermission("hotjava.default.signed.security", "Medium");
        }
        Class class1 = currentLoadedClass();
        Object aobj[] = class1.getSigners();
        if(aobj != null && aobj.length > 0)
        {
            BasePolicy basepolicy = handleSigned(aobj);
            if(basepolicy != null)
                return basepolicy;
        }
        HJBProperties hjbproperties = HJBProperties.getHJBProperties("hjbrowser");
        String s1 = getClassName();
        String s2 = hjbproperties.getProperty("scriptPackage");
        if((classloader instanceof ScriptingEngineLoader) || s1 != null && s2 != null && s1.startsWith(s2))
            return PolicyScript.getPolicyScript();
        if(Boolean.getBoolean("hotjava.appletsJS.enableApplets"))
            return getDefaultPermission("hotjava.default.security", "high");
        else
            return super.untrusted;
    }

    public Class[] getClassContext()
    {
        return super.getClassContext();
    }
}
