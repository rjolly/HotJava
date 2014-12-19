// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   X509Key.java

package sun.security.x509;

import java.io.*;
import java.security.*;
import java.util.Properties;
import sun.misc.CharacterEncoder;
import sun.misc.HexDumpEncoder;
import sun.security.util.*;

// Referenced classes of package sun.security.x509:
//            AlgorithmId

public class X509Key
    implements PublicKey
{

    protected X509Key()
    {
    }

    private X509Key(AlgorithmId algorithmid, byte abyte0[])
        throws InvalidKeyException
    {
        algid = algorithmid;
        key = abyte0;
        encode();
    }

    public static X509Key parse(DerValue dervalue)
        throws IOException
    {
        if(dervalue.tag != 48)
            throw new IOException("corrupt subject key");
        AlgorithmId algorithmid = AlgorithmId.parse(dervalue.data.getDerValue());
        X509Key x509key;
        try
        {
            x509key = buildX509Key(algorithmid, dervalue.data.getBitString());
        }
        catch(InvalidKeyException invalidkeyexception)
        {
            throw new IOException("subject key, " + invalidkeyexception.toString());
        }
        if(dervalue.data.available() != 0)
            throw new IOException("excess subject key");
        else
            return x509key;
    }

    protected void parseKeyBits()
        throws IOException, InvalidKeyException
    {
        encode();
    }

    static String getSunProperty(String s)
    {
        java.security.Provider provider = Security.getProvider("SUN");
        return provider.getProperty(s);
    }

    static X509Key buildX509Key(AlgorithmId algorithmid, byte abyte0[])
        throws IOException, InvalidKeyException
    {
        String s = getSunProperty("PublicKey.X.509." + algorithmid.getName());
        if(s == null)
            s = "sun.security.x509.X509Key";
        try
        {
            Class class1 = Class.forName(s);
            Object obj = class1.newInstance();
            if(obj instanceof X509Key)
            {
                X509Key x509key = (X509Key)obj;
                x509key.algid = algorithmid;
                x509key.key = abyte0;
                x509key.parseKeyBits();
                return x509key;
            } else
            {
                System.err.println("Misconfiguration:  faulty key config, " + s);
                return new X509Key(algorithmid, abyte0);
            }
        }
        catch(ClassNotFoundException _ex)
        {
            System.err.println("Misconfiguration:  unknown key class, " + s);
            return new X509Key(algorithmid, abyte0);
        }
        catch(IllegalAccessException illegalaccessexception)
        {
            illegalaccessexception.printStackTrace();
            throw new IOException(s + " [internal error]");
        }
        catch(InstantiationException _ex)
        {
            System.err.println("Misconfiguration:  faulty key class, " + s);
        }
        return new X509Key(algorithmid, abyte0);
    }

    public String getAlgorithm()
    {
        return algid.getName();
    }

    public AlgorithmId getAlgorithmId()
    {
        return algid;
    }

    public final void emit(DerOutputStream deroutputstream)
        throws IOException
    {
        DerOutputStream deroutputstream1 = new DerOutputStream();
        algid.emit(deroutputstream1);
        deroutputstream1.putBitString(key);
        deroutputstream.write((byte)48, deroutputstream1);
    }

    public synchronized byte[] getEncoded()
    {
        if(encodedKey == null)
            try
            {
                encode();
            }
            catch(InvalidKeyException _ex) { }
        return encodedKey;
    }

    public String getFormat()
    {
        return "X.509";
    }

    public byte[] encode()
        throws InvalidKeyException
    {
        if(encodedKey == null)
            try
            {
                DerOutputStream deroutputstream = new DerOutputStream();
                emit(deroutputstream);
                encodedKey = deroutputstream.toByteArray();
            }
            catch(IOException ioexception)
            {
                throw new InvalidKeyException("IOException : " + ioexception.getMessage());
            }
        return encodedKey;
    }

    public String toString()
    {
        HexDumpEncoder hexdumpencoder = new HexDumpEncoder();
        return "algorithm = " + algid.toString() + ", unparsed keybits = \n" + hexdumpencoder.encodeBuffer(key);
    }

    public void decode(InputStream inputstream)
        throws InvalidKeyException
    {
        try
        {
            DerValue dervalue = new DerValue(inputstream);
            if(dervalue.tag != 48)
                throw new InvalidKeyException("invalid key format");
            algid = AlgorithmId.parse(dervalue.data.getDerValue());
            key = dervalue.data.getBitString();
            parseKeyBits();
            if(dervalue.data.available() != 0)
                throw new InvalidKeyException("excess key data");
        }
        catch(IOException ioexception)
        {
            throw new InvalidKeyException(ioexception.toString());
        }
    }

    public void decode(byte abyte0[])
        throws InvalidKeyException
    {
        decode(((InputStream) (new ByteArrayInputStream(abyte0))));
    }

    private synchronized void writeObject(ObjectOutputStream objectoutputstream)
        throws IOException
    {
        objectoutputstream.write(getEncoded());
    }

    private synchronized void readObject(ObjectInputStream objectinputstream)
        throws IOException
    {
        try
        {
            decode(objectinputstream);
            return;
        }
        catch(InvalidKeyException invalidkeyexception)
        {
            invalidkeyexception.printStackTrace();
            throw new IOException("deserialized key is invalid: " + invalidkeyexception.getMessage());
        }
    }

    public boolean equals(Object obj)
    {
        if(this == obj)
            return true;
        if(obj instanceof Key)
        {
            Key key1 = (Key)obj;
            byte abyte0[] = getEncoded();
            byte abyte1[] = key1.getEncoded();
            return MessageDigest.isEqual(abyte0, abyte1);
        } else
        {
            return false;
        }
    }

    protected AlgorithmId algid;
    protected byte key[];
    private byte encodedKey[];
}
