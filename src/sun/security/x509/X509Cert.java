// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   X509Cert.java

package sun.security.x509;

import java.io.*;
import java.security.*;
import java.util.Date;
import sun.security.util.*;

// Referenced classes of package sun.security.x509:
//            AlgorithmId, CertException, CertParseError, X500Name, 
//            X500Signer, X509Key

public class X509Cert
    implements Certificate, Serializable
{

    public X509Cert()
    {
        parsed = false;
    }

    public X509Cert(byte abyte0[])
        throws IOException
    {
        parsed = false;
        DerValue dervalue = new DerValue(abyte0);
        parse(dervalue);
        if(dervalue.data.available() != 0)
        {
            throw new CertParseError("garbage at end");
        } else
        {
            signedCert = abyte0;
            return;
        }
    }

    public X509Cert(byte abyte0[], int i, int j)
        throws IOException
    {
        parsed = false;
        DerValue dervalue = new DerValue(abyte0, i, j);
        parse(dervalue);
        if(dervalue.data.available() != 0)
        {
            throw new CertParseError("garbage at end");
        } else
        {
            signedCert = new byte[j];
            System.arraycopy(abyte0, i, signedCert, 0, j);
            return;
        }
    }

    public X509Cert(DerValue dervalue)
        throws IOException
    {
        parsed = false;
        parse(dervalue);
        if(dervalue.data.available() != 0)
        {
            throw new CertParseError("garbage at end");
        } else
        {
            signedCert = dervalue.toByteArray();
            return;
        }
    }

    public X509Cert(X500Name x500name, X509Key x509key, Date date, Date date1)
        throws CertException
    {
        parsed = false;
        subject = x500name;
        if(!(x509key instanceof PublicKey))
        {
            throw new CertException(9, "Doesn't implement PublicKey interface");
        } else
        {
            pubkey = x509key;
            notbefore = date;
            notafter = date1;
            version = 0;
            return;
        }
    }

    public void decode(InputStream inputstream)
        throws IOException
    {
        DerValue dervalue = new DerValue(inputstream);
        parse(dervalue);
        signedCert = dervalue.toByteArray();
    }

    public void encode(OutputStream outputstream)
        throws IOException
    {
        outputstream.write(getSignedCert());
    }

    public boolean equals(Object obj)
    {
        if(obj instanceof X509Cert)
            return equals((X509Cert)obj);
        else
            return false;
    }

    public boolean equals(X509Cert x509cert)
    {
        if(this == x509cert)
            return true;
        if(signedCert == null || x509cert.signedCert == null)
            return false;
        if(signedCert.length != x509cert.signedCert.length)
            return false;
        for(int i = 0; i < signedCert.length; i++)
            if(signedCert[i] != x509cert.signedCert[i])
                return false;

        return true;
    }

    public String getFormat()
    {
        return "X.509";
    }

    public Principal getGuarantor()
    {
        return getIssuerName();
    }

    public Principal getPrincipal()
    {
        return getSubjectName();
    }

    public void verify(PublicKey publickey)
        throws CertException
    {
        Date date = new Date();
        if(date.before(notbefore))
            throw new CertException(3);
        if(date.after(notafter))
            throw new CertException(4);
        if(signedCert == null)
            throw new CertException(1, "?? certificate is not signed yet ??");
        String s = null;
        try
        {
            Signature signature1 = null;
            s = issuerSigAlg.getName();
            signature1 = Signature.getInstance(s);
            signature1.initVerify(publickey);
            signature1.update(rawCert, 0, rawCert.length);
            if(!signature1.verify(signature))
                throw new CertException(1, "Signature ... by <" + issuer + "> for <" + subject + ">");
        }
        catch(NoSuchAlgorithmException _ex)
        {
            throw new CertException(1, "Unsupported signature algorithm (" + s + ")");
        }
        catch(InvalidKeyException _ex)
        {
            throw new CertException(9, "Algorithm (" + s + ") rejected public key");
        }
        catch(SignatureException _ex)
        {
            throw new CertException(1, "Signature by <" + issuer + "> for <" + subject + ">");
        }
    }

    public byte[] encodeAndSign(BigInt bigint, X500Signer x500signer)
        throws IOException, SignatureException
    {
        rawCert = null;
        version = 0;
        serialnum = bigint;
        issuer = x500signer.getSigner();
        issuerSigAlg = x500signer.getAlgorithmId();
        if(subject == null || pubkey == null || notbefore == null || notafter == null)
        {
            throw new IOException("not enough cert parameters");
        } else
        {
            rawCert = DERencode();
            signedCert = sign(x500signer, rawCert);
            return signedCert;
        }
    }

    public X500Signer getSigner(AlgorithmId algorithmid, PrivateKey privatekey)
        throws NoSuchAlgorithmException, InvalidKeyException
    {
        String s;
        if(privatekey instanceof Key)
        {
            PrivateKey privatekey1 = privatekey;
            s = privatekey1.getAlgorithm();
        } else
        {
            throw new InvalidKeyException("private key not a key!");
        }
        Signature signature1 = Signature.getInstance(algorithmid.getName());
        if(!pubkey.getAlgorithm().equals(s))
        {
            throw new InvalidKeyException("Private key algorithm " + s + " incompatible with certificate " + pubkey.getAlgorithm());
        } else
        {
            signature1.initSign(privatekey);
            return new X500Signer(signature1, subject);
        }
    }

    public Signature getVerifier(String s)
        throws NoSuchAlgorithmException, InvalidKeyException
    {
        Signature signature1 = Signature.getInstance(s);
        signature1.initVerify(pubkey);
        return signature1;
    }

    public byte[] getSignedCert()
    {
        return (byte[])signedCert.clone();
    }

    public BigInt getSerialNumber()
    {
        return serialnum;
    }

    public X500Name getSubjectName()
    {
        return subject;
    }

    public X500Name getIssuerName()
    {
        return issuer;
    }

    public AlgorithmId getIssuerAlgorithmId()
    {
        return issuerSigAlg;
    }

    public Date getNotBefore()
    {
        return new Date(notbefore.getTime());
    }

    public Date getNotAfter()
    {
        return new Date(notafter.getTime());
    }

    public PublicKey getPublicKey()
    {
        return pubkey;
    }

    public int getVersion()
    {
        return version;
    }

    public int hashCode()
    {
        int i = 0;
        for(int j = 0; j < signedCert.length; j++)
            i += signedCert[j] * j;

        return i;
    }

    public String toString()
    {
        if(subject == null || pubkey == null || notbefore == null || notafter == null || issuer == null || issuerSigAlg == null || serialnum == null)
        {
            throw new NullPointerException("X.509 cert is incomplete");
        } else
        {
            String s = "  X.509v" + (version + 1) + " certificate,\n";
            s = s + "  Subject is " + subject + "\n";
            s = s + "  Key:  " + pubkey;
            s = s + "  Validity <" + notbefore + "> until <" + notafter + ">\n";
            s = s + "  Issuer is " + issuer + "\n";
            s = s + "  Issuer signature used " + issuerSigAlg.toString() + "\n";
            s = s + "  Serial number = " + serialnum + "\n";
            return "[\n" + s + "]";
        }
    }

    public String toString(boolean flag)
    {
        return toString();
    }

    private void parse(DerValue dervalue)
        throws IOException
    {
        if(parsed)
            throw new IOException("Certificate already parsed");
        DerValue adervalue[] = new DerValue[3];
        adervalue[0] = dervalue.data.getDerValue();
        adervalue[1] = dervalue.data.getDerValue();
        adervalue[2] = dervalue.data.getDerValue();
        if(dervalue.data.available() != 0)
            throw new CertParseError("signed overrun, bytes = " + dervalue.data.available());
        if(adervalue[0].tag != 48)
            throw new CertParseError("signed fields invalid");
        rawCert = adervalue[0].toByteArray();
        issuerSigAlg = AlgorithmId.parse(adervalue[1]);
        signature = adervalue[2].getBitString();
        if(adervalue[1].data.available() != 0)
            throw new CertParseError("algid field overrun");
        if(adervalue[2].data.available() != 0)
            throw new CertParseError("signed fields overrun");
        DerInputStream derinputstream = adervalue[0].data;
        version = 0;
        DerValue dervalue1 = derinputstream.getDerValue();
        if(dervalue1.isConstructed() && dervalue1.isContextSpecific())
        {
            version = dervalue1.data.getInteger().toInt();
            if(dervalue1.data.available() != 0)
                throw new IOException("X.509 version, bad format");
            dervalue1 = derinputstream.getDerValue();
        }
        serialnum = dervalue1.getInteger();
        dervalue1 = derinputstream.getDerValue();
        AlgorithmId algorithmid = AlgorithmId.parse(dervalue1);
        if(!algorithmid.equals(issuerSigAlg))
            throw new CertParseError("CA Algorithm mismatch!");
        algid = algorithmid;
        issuer = new X500Name(derinputstream);
        dervalue1 = derinputstream.getDerValue();
        if(dervalue1.tag != 48)
            throw new CertParseError("corrupt validity field");
        notbefore = dervalue1.data.getUTCTime();
        notafter = dervalue1.data.getUTCTime();
        if(dervalue1.data.available() != 0)
        {
            throw new CertParseError("excess validity data");
        } else
        {
            subject = new X500Name(derinputstream);
            DerValue dervalue2 = derinputstream.getDerValue();
            pubkey = X509Key.parse(dervalue2);
            derinputstream.available();
            parsed = true;
            return;
        }
    }

    private byte[] DERencode()
        throws IOException
    {
        DerOutputStream deroutputstream = new DerOutputStream();
        encode(deroutputstream);
        return deroutputstream.toByteArray();
    }

    private void encode(DerOutputStream deroutputstream)
        throws IOException
    {
        DerOutputStream deroutputstream1 = new DerOutputStream();
        deroutputstream1.putInteger(serialnum);
        issuerSigAlg.emit(deroutputstream1);
        issuer.emit(deroutputstream1);
        DerOutputStream deroutputstream2 = new DerOutputStream();
        deroutputstream2.putUTCTime(notbefore);
        deroutputstream2.putUTCTime(notafter);
        deroutputstream1.write((byte)48, deroutputstream2);
        subject.emit(deroutputstream1);
        pubkey.emit(deroutputstream1);
        deroutputstream.write((byte)48, deroutputstream1);
    }

    private byte[] sign(X500Signer x500signer, byte abyte0[])
        throws IOException, SignatureException
    {
        DerOutputStream deroutputstream = new DerOutputStream();
        DerOutputStream deroutputstream1 = new DerOutputStream();
        deroutputstream1.write(abyte0);
        x500signer.getAlgorithmId().emit(deroutputstream1);
        x500signer.update(abyte0, 0, abyte0.length);
        signature = x500signer.sign();
        deroutputstream1.putBitString(signature);
        deroutputstream.write((byte)48, deroutputstream1);
        return deroutputstream.toByteArray();
    }

    private synchronized void writeObject(ObjectOutputStream objectoutputstream)
        throws IOException
    {
        encode(objectoutputstream);
    }

    private synchronized void readObject(ObjectInputStream objectinputstream)
        throws IOException
    {
        decode(objectinputstream);
    }

    private transient boolean parsed;
    protected AlgorithmId algid;
    private byte rawCert[];
    private byte signature[];
    private byte signedCert[];
    private X500Name subject;
    private X509Key pubkey;
    private Date notafter;
    private Date notbefore;
    private int version;
    private BigInt serialnum;
    private X500Name issuer;
    private AlgorithmId issuerSigAlg;
}
