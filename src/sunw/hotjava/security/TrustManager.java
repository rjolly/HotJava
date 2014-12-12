// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   TrustManager.java

package sunw.hotjava.security;

import java.security.Certificate;
import java.util.Date;

// Referenced classes of package sunw.hotjava.security:
//            CertificateTrust

public abstract class TrustManager
{

    public static TrustManager getTrustManager()
    {
        return tm;
    }

    public static void setTrustManager(TrustManager trustmanager)
    {
        tm = trustmanager;
    }

    public abstract boolean isTrustedFor(Certificate acertificate[][], String s, Date date);

    public abstract CertificateTrust[] getSessionCerts();

    public abstract CertificateTrust[] getTempCerts();

    public abstract CertificateTrust[] getSessionSites();

    public abstract boolean isTrustedCert(Certificate certificate);

    public abstract CertificateTrust getTrustForCert(Certificate certificate);

    public abstract void setTrustForCert(CertificateTrust certificatetrust);

    public abstract CertificateTrust getTrustForSite(String s);

    public abstract void setTrustForSite(CertificateTrust certificatetrust);

    public abstract void removeSite(CertificateTrust certificatetrust);

    public abstract boolean isTrustedForSoftware(Certificate certificate);

    public abstract void deleteCertificate(Certificate certificate);

    public abstract void apply();

    public abstract void reset();

    public TrustManager()
    {
    }

    private static TrustManager tm;
}
