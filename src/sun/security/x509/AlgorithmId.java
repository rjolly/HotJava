// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   AlgorithmId.java

package sun.security.x509;

import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import sun.security.util.*;

public class AlgorithmId
    implements Serializable
{

    /**
     * @deprecated Method getAlgorithmId is deprecated
     */

    public static AlgorithmId getAlgorithmId(String s)
        throws NoSuchAlgorithmException
    {
        return get(s);
    }

    public static AlgorithmId get(String s)
        throws NoSuchAlgorithmException
    {
        ObjectIdentifier objectidentifier = algOID(s);
        if(objectidentifier == null)
            throw new NoSuchAlgorithmException("unrecognized algorithm name: " + s);
        else
            return new AlgorithmId(objectidentifier);
    }

    public static AlgorithmId parse(DerValue dervalue)
        throws IOException
    {
        if(dervalue.tag != 48)
            throw new IOException("algid parse error, not a sequence");
        DerInputStream derinputstream = dervalue.toDerInputStream();
        ObjectIdentifier objectidentifier = derinputstream.getOID();
        DerValue dervalue1;
        if(derinputstream.available() == 0)
        {
            dervalue1 = null;
        } else
        {
            dervalue1 = derinputstream.getDerValue();
            if(dervalue1.tag == 5)
                dervalue1 = null;
        }
        AlgorithmId algorithmid = buildAlgorithmId(objectidentifier, dervalue1);
        algorithmid.decodeParams();
        return algorithmid;
    }

    private static AlgorithmId buildAlgorithmId(ObjectIdentifier objectidentifier, DerValue dervalue)
        throws IOException
    {
        String s = Security.getAlgorithmProperty(objectidentifier.toString(), "Class");
        if(debug)
            System.out.println("dynamic class name for [" + objectidentifier.toString() + "|Class]: " + s);
        if(s == null || s.equals(""))
            return new AlgorithmId(objectidentifier, dervalue);
        try
        {
            Class class1 = Class.forName(s);
            Object obj = class1.newInstance();
            if(!(obj instanceof AlgorithmId))
            {
                System.err.println("Misconfiguration:  faulty algorithm config, " + s);
                return new AlgorithmId(objectidentifier, dervalue);
            } else
            {
                AlgorithmId algorithmid = (AlgorithmId)obj;
                algorithmid.algid = objectidentifier;
                algorithmid.params = dervalue;
                algorithmid.decodeParams();
                return algorithmid;
            }
        }
        catch(ClassNotFoundException _ex)
        {
            System.err.println("Misconfiguration:  unknown algorithm class, " + s);
            return new AlgorithmId(objectidentifier, dervalue);
        }
        catch(IllegalAccessException _ex)
        {
            throw new IOException(s + " [internal error]");
        }
        catch(InstantiationException _ex)
        {
            System.err.println("Misconfiguration:  faulty algorithm class, " + s);
        }
        return new AlgorithmId(objectidentifier, dervalue);
    }

    public AlgorithmId(ObjectIdentifier objectidentifier)
    {
        algid = objectidentifier;
    }

    private AlgorithmId(ObjectIdentifier objectidentifier, DerValue dervalue)
        throws IOException
    {
        algid = objectidentifier;
        params = dervalue;
        decodeParams();
    }

    public AlgorithmId()
    {
    }

    protected void decodeParams()
        throws IOException
    {
    }

    public final void emit(DerOutputStream deroutputstream)
        throws IOException
    {
        DerOutputStream deroutputstream1 = new DerOutputStream();
        deroutputstream1.putOID(algid);
        if(params == null)
            deroutputstream1.putNull();
        else
            deroutputstream1.putDerValue(params);
        deroutputstream.write((byte)48, deroutputstream1);
    }

    /**
     * @deprecated Method encode is deprecated
     */

    public final void encode(OutputStream outputstream)
        throws IOException
    {
        DerOutputStream deroutputstream = new DerOutputStream();
        emit(deroutputstream);
        outputstream.write(deroutputstream.toByteArray());
    }

    /**
     * @deprecated Method encode is deprecated
     */

    public final byte[] encode()
        throws IOException
    {
        DerOutputStream deroutputstream = new DerOutputStream();
        emit(deroutputstream);
        return deroutputstream.toByteArray();
    }

    private static ObjectIdentifier algOID(String s)
    {
        if(s.equals("MD5"))
            return MD5_oid;
        if(s.equals("MD2"))
            return MD2_oid;
        if(s.equals("SHA") || s.equals("SHA1") || s.equals("SHA-1"))
            return SHA_oid;
        if(s.equals("RSA"))
            return RSA_oid;
        if(s.equals("Diffie-Hellman") || s.equals("DH"))
            return DH_oid;
        if(s.equals("DSA"))
            return DSA_oid;
        if(s.equals("MD5withRSA") || s.equals("MD5/RSA"))
            return md5WithRSAEncryption_oid;
        if(s.equals("MD2withRSA") || s.equals("MD2/RSA"))
            return md2WithRSAEncryption_oid;
        if(s.equals("SHAwithDSA") || s.equals("SHA/DSA"))
            return shaWithDSA_oid;
        else
            return null;
    }

    private String algName()
    {
        if(algid.equals(MD5_oid))
            return "MD5";
        if(algid.equals(MD2_oid))
            return "MD2";
        if(algid.equals(SHA_oid))
            return "SHA";
        if(algid.equals(RSAEncryption_oid) || algid.equals(RSA_oid))
            return "RSA";
        if(algid.equals(DH_oid))
            return "Diffie-Hellman";
        if(algid.equals(DSA_oid))
            return "DSA";
        if(algid.equals(md5WithRSAEncryption_oid))
            return "MD5withRSA";
        if(algid.equals(md2WithRSAEncryption_oid))
            return "MD2withRSA";
        if(algid.equals(shaWithDSA_oid))
            return "SHA1withDSA";
        else
            return "OID." + algid.toString();
    }

    public final ObjectIdentifier getOID()
    {
        return algid;
    }

    public String getName()
    {
        return algName();
    }

    public String toString()
    {
        return "[" + algName() + paramsToString() + "]";
    }

    protected String paramsToString()
    {
        if(params == null)
            return "";
        else
            return ", params unparsed";
    }

    public boolean equals(AlgorithmId algorithmid)
    {
        if(!algid.equals(algorithmid.algid))
            return false;
        if(params == null && algorithmid.params == null)
            return true;
        if(params == null)
            return false;
        else
            return params.equals(algorithmid.params);
    }

    public boolean equals(Object obj)
    {
        if(obj instanceof AlgorithmId)
            return equals((AlgorithmId)obj);
        if(obj instanceof ObjectIdentifier)
            return equals((ObjectIdentifier)obj);
        else
            return false;
    }

    public final boolean equals(ObjectIdentifier objectidentifier)
    {
        return algid.equals(objectidentifier);
    }

    private static boolean debug;
    private ObjectIdentifier algid;
    protected DerValue params;
    private static final int MD2_data[] = {
        1, 2, 840, 0x1bb8d, 2, 2
    };
    private static final int MD5_data[] = {
        1, 2, 840, 0x1bb8d, 2, 5
    };
    private static final int SHA1_data[] = {
        1, 3, 14, 3, 2, 26
    };
    public static final ObjectIdentifier MD2_oid = new ObjectIdentifier(MD2_data);
    public static final ObjectIdentifier MD5_oid = new ObjectIdentifier(MD5_data);
    public static final ObjectIdentifier SHA_oid = new ObjectIdentifier(SHA1_data);
    private static final int DH_data[] = {
        1, 2, 840, 0x1bb8d, 1, 3, 1
    };
    private static final int dsa_data[] = {
        1, 3, 14, 3, 2, 12
    };
    private static final int RSA_data[] = {
        1, 2, 5, 8, 1, 1
    };
    private static final int RSAEncryption_data[] = {
        1, 2, 840, 0x1bb8d, 1, 1, 1
    };
    public static final ObjectIdentifier DH_oid = new ObjectIdentifier(DH_data);
    public static final ObjectIdentifier DSA_oid = new ObjectIdentifier(dsa_data);
    public static final ObjectIdentifier RSA_oid = new ObjectIdentifier(RSA_data);
    public static final ObjectIdentifier RSAEncryption_oid = new ObjectIdentifier(RSAEncryption_data);
    private static final int md2WithRSAEncryption_data[] = {
        1, 2, 840, 0x1bb8d, 1, 1, 2
    };
    private static final int md5WithRSAEncryption_data[] = {
        1, 2, 840, 0x1bb8d, 1, 1, 4
    };
    private static final int shaWithDSA_data[] = {
        1, 3, 14, 3, 2, 13
    };
    public static final ObjectIdentifier md2WithRSAEncryption_oid = new ObjectIdentifier(md2WithRSAEncryption_data);
    public static final ObjectIdentifier md5WithRSAEncryption_oid = new ObjectIdentifier(md5WithRSAEncryption_data);
    public static final ObjectIdentifier shaWithDSA_oid = new ObjectIdentifier(shaWithDSA_data);

}
