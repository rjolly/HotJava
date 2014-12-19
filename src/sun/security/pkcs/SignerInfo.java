// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   SignerInfo.java

package sun.security.pkcs;

import java.io.IOException;
import java.security.*;
import sun.security.util.*;
import sun.security.x509.*;

// Referenced classes of package sun.security.pkcs:
//            ContentInfo, PKCS7, ParsingException

public class SignerInfo
{

    public SignerInfo(X500Name x500name, BigInt bigint, AlgorithmId algorithmid, AlgorithmId algorithmid1, byte abyte0[])
    {
        version = new BigInt(1);
        issuerName = x500name;
        certificateSerialNumber = bigint;
        digestAlgorithmId = algorithmid;
        digestEncryptionAlgorithmId = algorithmid1;
        encryptedDigest = abyte0;
    }

    public SignerInfo(DerInputStream derinputstream)
        throws IOException, ParsingException
    {
        version = derinputstream.getInteger();
        DerValue adervalue[] = derinputstream.getSequence(2);
        byte abyte0[] = adervalue[0].toByteArray();
        issuerName = new X500Name(new DerValue((byte)48, abyte0));
        certificateSerialNumber = adervalue[1].getInteger();
        DerValue dervalue = derinputstream.getDerValue();
        digestAlgorithmId = AlgorithmId.parse(dervalue);
        derinputstream.getSet(0);
        dervalue = derinputstream.getDerValue();
        digestEncryptionAlgorithmId = AlgorithmId.parse(dervalue);
        encryptedDigest = derinputstream.getOctetString();
        derinputstream.getSet(0);
        if(derinputstream.available() != 0)
            throw new ParsingException("extra data at the end");
        else
            return;
    }

    public void encode(DerOutputStream deroutputstream)
        throws IOException
    {
        DerOutputStream deroutputstream1 = new DerOutputStream();
        deroutputstream1.putInteger(version);
        DerOutputStream deroutputstream2 = new DerOutputStream();
        issuerName.emit(deroutputstream2);
        deroutputstream2.putInteger(certificateSerialNumber);
        deroutputstream1.write((byte)48, deroutputstream2);
        digestAlgorithmId.encode(deroutputstream1);
        DerOutputStream deroutputstream3 = new DerOutputStream();
        deroutputstream1.write((byte)49, deroutputstream3);
        digestEncryptionAlgorithmId.encode(deroutputstream1);
        deroutputstream1.putOctetString(encryptedDigest);
        deroutputstream1.write((byte)49, deroutputstream3);
        deroutputstream.write((byte)48, deroutputstream1);
    }

    public X509Cert getCertificate(PKCS7 pkcs7)
    {
        return pkcs7.getCertificate(certificateSerialNumber, issuerName);
    }

    SignerInfo verify(PKCS7 pkcs7, byte abyte0[])
        throws NoSuchAlgorithmException, SignatureException
    {
        IdentityScope.getSystemScope();
        try
        {
            if(abyte0 == null)
            {
                ContentInfo contentinfo = pkcs7.getContentInfo();
                abyte0 = contentinfo.getContentBytes();
            }
            String s = getDigestEncryptionAlgorithmId().getName();
            Signature signature = Signature.getInstance(s);
            X509Cert x509cert = getCertificate(pkcs7);
            if(x509cert == null)
                throw new SignatureException("No cert for " + issuerName);
            java.security.PublicKey publickey = x509cert.getPublicKey();
            signature.initVerify(publickey);
            signature.update(abyte0);
            if(signature.verify(encryptedDigest))
                return this;
        }
        catch(IOException ioexception)
        {
            throw new SignatureException("IO error verifying signature:\n" + ioexception.getMessage());
        }
        catch(InvalidKeyException invalidkeyexception)
        {
            throw new SignatureException("InvalidKey: " + invalidkeyexception.getMessage());
        }
        return null;
    }

    SignerInfo verify(PKCS7 pkcs7)
        throws NoSuchAlgorithmException, SignatureException
    {
        return verify(pkcs7, null);
    }

    public BigInt getVersion()
    {
        return version;
    }

    public X500Name getIssuerName()
    {
        return issuerName;
    }

    public BigInt getCertificateSerialNumber()
    {
        return certificateSerialNumber;
    }

    public AlgorithmId getDigestAlgorithmId()
    {
        return digestAlgorithmId;
    }

    public AlgorithmId getDigestEncryptionAlgorithmId()
    {
        return digestEncryptionAlgorithmId;
    }

    public byte[] getEncryptedDigest()
    {
        return encryptedDigest;
    }

    public String toString()
    {
        String s = "";
        s = s + "Signer Info for (issuer): " + issuerName + "\n";
        s = s + "\tversion: " + version + "\n";
        s = s + "\tcertificateSerialNumber: " + certificateSerialNumber + "\n";
        s = s + "\tdigestAlgorithmId: " + digestAlgorithmId + "\n";
        s = s + "\tdigestEncryptionAlgorithmId: " + digestEncryptionAlgorithmId + "\n";
        s = s + "\tencryptedDigest: " + encryptedDigest + "\n";
        return s;
    }

    BigInt version;
    X500Name issuerName;
    BigInt certificateSerialNumber;
    AlgorithmId digestAlgorithmId;
    AlgorithmId digestEncryptionAlgorithmId;
    byte encryptedDigest[];
}
