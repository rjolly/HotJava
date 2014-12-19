// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   SystemIdentity.java

package sun.security.provider;

import java.io.Serializable;
import java.security.*;

public class SystemIdentity extends Identity
    implements Serializable
{

    public SystemIdentity(String s, IdentityScope identityscope)
        throws InvalidParameterException, KeyManagementException
    {
        super(s, identityscope);
        trusted = false;
        if(identityscope == null)
            throw new InvalidParameterException("scope cannot be null");
        else
            return;
    }

    public boolean isTrusted()
    {
        return trusted;
    }

    protected void setTrusted(boolean flag)
    {
        trusted = flag;
    }

    void setIdentityInfo(String s)
    {
        super.setInfo(s);
    }

    String getIndentityInfo()
    {
        return super.getInfo();
    }

    void setIdentityPublicKey(PublicKey publickey)
        throws KeyManagementException
    {
        setPublicKey(publickey);
    }

    void addIdentityCertificate(Certificate certificate)
        throws KeyManagementException
    {
        addCertificate(certificate);
    }

    void clearCertificates()
        throws KeyManagementException
    {
        Certificate acertificate[] = certificates();
        for(int i = 0; i < acertificate.length; i++)
            removeCertificate(acertificate[i]);

    }

    public String toString()
    {
        String s = "not trusted";
        if(trusted)
            s = "trusted";
        return super.toString() + "[" + s + "]";
    }

    boolean trusted;
    private String info;
}
