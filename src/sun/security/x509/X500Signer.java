// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   X500Signer.java

package sun.security.x509;

import java.security.*;

// Referenced classes of package sun.security.x509:
//            AlgorithmId, X500Name

public final class X500Signer
{

    public void update(byte abyte0[], int i, int j)
        throws SignatureException
    {
        sig.update(abyte0, i, j);
    }

    public byte[] sign()
        throws SignatureException
    {
        return sig.sign();
    }

    public AlgorithmId getAlgorithmId()
    {
        return algid;
    }

    public X500Name getSigner()
    {
        return agent;
    }

    X500Signer(Signature signature, X500Name x500name)
    {
        if(signature == null || x500name == null)
            throw new IllegalArgumentException("null parameter");
        sig = signature;
        agent = x500name;
        try
        {
            algid = AlgorithmId.getAlgorithmId(signature.getAlgorithm());
            return;
        }
        catch(NoSuchAlgorithmException nosuchalgorithmexception)
        {
            throw new RuntimeException("internal error! " + nosuchalgorithmexception.getMessage());
        }
    }

    private Signature sig;
    private X500Name agent;
    private AlgorithmId algid;
}
