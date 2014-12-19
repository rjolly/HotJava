// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   PKCS7.java

package sun.security.pkcs;

import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.util.Vector;
import sun.security.util.*;
import sun.security.x509.*;

// Referenced classes of package sun.security.pkcs:
//            ContentInfo, ParsingException, SignerInfo

public class PKCS7
{

    public PKCS7(InputStream inputstream)
        throws ParsingException, IOException
    {
        debug = false;
        DataInputStream datainputstream = new DataInputStream(inputstream);
        byte abyte0[] = new byte[datainputstream.available()];
        datainputstream.readFully(abyte0);
        parse(new DerInputStream(abyte0));
    }

    public PKCS7(DerInputStream derinputstream)
        throws ParsingException
    {
        debug = false;
        parse(derinputstream);
    }

    public PKCS7(byte abyte0[])
        throws ParsingException
    {
        debug = false;
        DerInputStream derinputstream = new DerInputStream(abyte0);
        parse(derinputstream);
    }

    private void parse(DerInputStream derinputstream)
        throws ParsingException
    {
        try
        {
            ContentInfo contentinfo = new ContentInfo(derinputstream);
            contentType = contentinfo.contentType;
            if(contentType.equals(ContentInfo.SIGNED_DATA_OID))
            {
                parseSignedData(contentinfo.getContent());
                return;
            } else
            {
                throw new ParsingException("content type " + contentType + "not supported.");
            }
        }
        catch(IOException ioexception)
        {
            debug(ioexception);
        }
        throw new ParsingException("IOException.");
    }

    public PKCS7(AlgorithmId aalgorithmid[], ContentInfo contentinfo, X509Cert ax509cert[], SignerInfo asignerinfo[])
    {
        debug = false;
        version = new BigInt(1);
        digestAlgorithmIds = aalgorithmid;
        contentInfo = contentinfo;
        certificates = ax509cert;
        signerInfos = asignerinfo;
    }

    private void parseSignedData(DerValue dervalue)
        throws ParsingException, IOException
    {
        DerInputStream derinputstream = dervalue.toDerInputStream();
        version = derinputstream.getInteger();
        DerValue adervalue[] = derinputstream.getSet(1);
        int i = adervalue.length;
        digestAlgorithmIds = new AlgorithmId[i];
        try
        {
            for(int j = 0; j < i; j++)
            {
                DerValue dervalue1 = adervalue[j];
                digestAlgorithmIds[j] = AlgorithmId.parse(dervalue1);
            }

        }
        catch(IOException ioexception)
        {
            debug(ioexception);
            throw new ParsingException("error parsing digest AlgorithmId IDs");
        }
        contentInfo = new ContentInfo(derinputstream);
        DerValue adervalue1[] = derinputstream.getSet(2);
        i = adervalue1.length;
        certificates = new X509Cert[i];
        for(int k = 0; k < i; k++)
        {
            X509Cert x509cert = new X509Cert(adervalue1[k]);
            certificates[k] = x509cert;
        }

        derinputstream.getSet(0);
        DerValue adervalue2[] = derinputstream.getSet(1);
        i = adervalue2.length;
        signerInfos = new SignerInfo[i];
        for(int l = 0; l < i; l++)
        {
            DerInputStream derinputstream1 = adervalue2[l].toDerInputStream();
            signerInfos[l] = new SignerInfo(derinputstream1);
        }

    }

    public void encodeSignedData(OutputStream outputstream)
        throws IOException
    {
        DerOutputStream deroutputstream = new DerOutputStream();
        encodeSignedData(deroutputstream);
        outputstream.write(deroutputstream.toByteArray());
    }

    public void encodeSignedData(DerOutputStream deroutputstream)
        throws IOException
    {
        DerOutputStream deroutputstream1 = new DerOutputStream();
        deroutputstream1.putInteger(version);
        DerOutputStream deroutputstream2 = new DerOutputStream();
        for(int i = 0; i < digestAlgorithmIds.length; i++)
            digestAlgorithmIds[i].encode(deroutputstream2);

        deroutputstream1.write((byte)49, deroutputstream2);
        contentInfo.encode(deroutputstream1);
        DerOutputStream deroutputstream3 = new DerOutputStream();
        for(int j = 0; j < certificates.length; j++)
            certificates[j].encode(deroutputstream3);

        deroutputstream1.write((byte)49, deroutputstream3);
        DerOutputStream deroutputstream4 = new DerOutputStream();
        deroutputstream1.write((byte)49, deroutputstream4);
        DerOutputStream deroutputstream5 = new DerOutputStream();
        for(int k = 0; k < signerInfos.length; k++)
            signerInfos[k].encode(deroutputstream5);

        deroutputstream1.write((byte)49, deroutputstream5);
        DerValue dervalue = new DerValue((byte)48, deroutputstream1.toByteArray());
        ContentInfo contentinfo = new ContentInfo(ContentInfo.SIGNED_DATA_OID, dervalue);
        contentinfo.encode(deroutputstream);
    }

    public SignerInfo verify(SignerInfo signerinfo, byte abyte0[])
        throws NoSuchAlgorithmException, SignatureException
    {
        return signerinfo.verify(this, abyte0);
    }

    public SignerInfo[] verify(byte abyte0[])
        throws NoSuchAlgorithmException, SignatureException
    {
        Vector vector = new Vector();
        for(int i = 0; i < signerInfos.length; i++)
        {
            SignerInfo signerinfo = verify(signerInfos[i], abyte0);
            if(signerinfo != null)
                vector.addElement(signerinfo);
        }

        if(vector.size() != 0)
        {
            SignerInfo asignerinfo[] = new SignerInfo[vector.size()];
            vector.copyInto(asignerinfo);
            return asignerinfo;
        } else
        {
            return null;
        }
    }

    public SignerInfo[] verify()
        throws NoSuchAlgorithmException, SignatureException
    {
        return verify(null);
    }

    public BigInt getVersion()
    {
        return version;
    }

    public AlgorithmId[] getDigestAlgorithmIds()
    {
        return digestAlgorithmIds;
    }

    public ContentInfo getContentInfo()
    {
        return contentInfo;
    }

    public X509Cert[] getCertificates()
    {
        return certificates;
    }

    public SignerInfo[] getSignerInfos()
    {
        return signerInfos;
    }

    public X509Cert getCertificate(BigInt bigint, X500Name x500name)
    {
        for(int i = 0; i < certificates.length; i++)
        {
            X509Cert x509cert = certificates[i];
            X500Name x500name1 = x509cert.getSubjectName();
            BigInt bigint1 = x509cert.getSerialNumber();
            if(bigint.equals(bigint1) && x500name.equals(x500name1))
                return x509cert;
        }

        return null;
    }

    public String toString()
    {
        String s = "";
        s = s + "PKCS7 :: version: " + version + "\n";
        s = s + "PKCS7 :: digest AlgorithmIds: \n";
        for(int i = 0; i < digestAlgorithmIds.length; i++)
            s = s + "\t" + digestAlgorithmIds[i] + "\n";

        s = s + contentInfo + "\n";
        s = s + "PKCS7 :: certificates: \n";
        for(int j = 0; j < certificates.length; j++)
            s = s + "\t" + j + ".   " + certificates[j] + "\n";

        s = s + "PKCS7 :: signer infos: \n";
        for(int k = 0; k < signerInfos.length; k++)
            s = s + "\t" + k + ".  " + signerInfos[k] + "\n";

        return s;
    }

    private void debug(Throwable throwable)
    {
        if(debug)
            throwable.printStackTrace();
    }

    private boolean debug;
    private ObjectIdentifier contentType;
    private BigInt version;
    private AlgorithmId digestAlgorithmIds[];
    private ContentInfo contentInfo;
    private X509Cert certificates[];
    private SignerInfo signerInfos[];
}
