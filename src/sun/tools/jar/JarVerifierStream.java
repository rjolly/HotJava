// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   JarVerifierStream.java

package sun.tools.jar;

import java.io.*;
import java.security.*;
import java.util.*;
import java.util.zip.*;
import sun.misc.BASE64Decoder;
import sun.misc.CharacterDecoder;
import sun.net.www.MessageHeader;
import sun.security.pkcs.*;
import sun.security.provider.SystemIdentity;

// Referenced classes of package sun.tools.jar:
//            JarEntryVerifier, JarException, Manifest, SignatureFile

public class JarVerifierStream extends ZipInputStream
{

    public JarVerifierStream(InputStream inputstream)
        throws IOException, JarException
    {
        super(inputstream);
        parsingBlock = false;
        parsingMeta = true;
        parsingManifest = false;
        processed = false;
        jev = new JarEntryVerifier();
        signatures = new Hashtable();
        sigFileIdentities = new Hashtable();
        verifiedIdentities = new Hashtable();
        baos = new ByteArrayOutputStream();
        scope = IdentityScope.getSystemScope();
    }

    public synchronized ZipEntry getNextEntry()
        throws IOException
    {
        if(currentEntry != null)
            closeEntry();
        currentEntry = super.getNextEntry();
        if(currentEntry == null)
            return null;
        processed = false;
        String s = currentEntry.getName();
        String s1 = s.toUpperCase();
        if(parsingMeta && manifest == null && Manifest.isManifestName(s1))
        {
            parsingManifest = true;
            baos.reset();
            jev.setEntry(null, null);
            return currentEntry;
        }
        if(parsingMeta && (s1.startsWith("META-INF/") || s1.startsWith("/META-INF/") || s1.startsWith("META-INF\\") || s1.startsWith("\\META-INF\\") || s1.startsWith("/META-INF\\") || s1.startsWith("\\META-INF/")))
        {
            if(s1.endsWith(".DSA") || s1.endsWith(".RSA"))
            {
                parsingBlock = true;
                baos.reset();
                jev.setEntry(null, null);
            }
            return currentEntry;
        }
        if(parsingMeta)
            parsingMeta = false;
        if(currentEntry.isDirectory())
        {
            jev.setEntry(null, null);
            return currentEntry;
        }
        if(s.startsWith("./"))
            s = s.substring(2);
        if(sigFileIdentities.get(s) != null)
        {
            jev.setEntry(manifest, s);
            return currentEntry;
        } else
        {
            jev.setEntry(null, null);
            return currentEntry;
        }
    }

    public int read()
        throws IOException
    {
        int i = super.read();
        if(i != -1)
        {
            if(parsingBlock || parsingManifest)
                baos.write(i);
            else
                jev.update((byte)i);
        } else
        {
            processEntry();
        }
        return i;
    }

    public int read(byte abyte0[], int i, int j)
        throws IOException
    {
        int k = super.read(abyte0, i, j);
        if(k != -1)
        {
            if(parsingBlock || parsingManifest)
                baos.write(abyte0, i, k);
            else
                jev.update(abyte0, i, k);
        } else
        {
            processEntry();
        }
        return k;
    }

    private void processEntry()
        throws IOException
    {
        if(processed)
            return;
        processed = true;
        if(parsingManifest)
        {
            parsingManifest = false;
            manifest = new Manifest(baos.toByteArray());
            return;
        }
        if(parsingBlock)
        {
            parsingBlock = false;
            try
            {
                PKCS7 pkcs7 = new PKCS7(baos.toByteArray());
                byte abyte0[] = pkcs7.getContentInfo().getData();
                SignatureFile signaturefile = new SignatureFile(abyte0);
                signatures.put(signaturefile, pkcs7);
                processSignature(signaturefile, pkcs7);
                return;
            }
            catch(ParsingException _ex)
            {
                return;
            }
            catch(IOException _ex)
            {
                return;
            }
            catch(SignatureException _ex)
            {
                return;
            }
            catch(NoSuchAlgorithmException _ex)
            {
                return;
            }
        } else
        {
            jev.verify(verifiedIdentities, sigFileIdentities);
            return;
        }
    }

    private void processSignature(SignatureFile signaturefile, PKCS7 pkcs7)
        throws JarException, SignatureException, NoSuchAlgorithmException
    {
        Vector vector = null;
        if(manifest == null)
            return;
        SignerInfo asignerinfo[] = pkcs7.verify();
        if(asignerinfo == null)
            return;
        BASE64Decoder base64decoder = new BASE64Decoder();
        MessageDigest messagedigest = MessageDigest.getInstance("SHA");
        MessageDigest messagedigest1 = MessageDigest.getInstance("MD5");
        vector = getIdentities(asignerinfo, pkcs7);
        for(Enumeration enumeration = signaturefile.entries(); enumeration.hasMoreElements();)
        {
            MessageHeader messageheader = (MessageHeader)enumeration.nextElement();
            String s;
            if((s = messageheader.findValue("Name")) != null)
            {
                MessageHeader messageheader1 = manifest.getEntry(s);
                if(messageheader1 != null)
                {
                    ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
                    PrintStream printstream = new PrintStream(bytearrayoutputstream);
                    messageheader1.print(printstream);
                    byte abyte0[] = bytearrayoutputstream.toByteArray();
                    boolean flag = verifySigFileEntry(messageheader, "SHA-Digest", abyte0, messagedigest, base64decoder, s);
                    boolean flag1 = verifySigFileEntry(messageheader, "MD5-Digest", abyte0, messagedigest1, base64decoder, s);
                    if(flag || flag1)
                        addIdentities(s, vector);
                }
            }
        }

    }

    private boolean verifySigFileEntry(MessageHeader messageheader, String s, byte abyte0[], MessageDigest messagedigest, BASE64Decoder base64decoder, String s1)
        throws JarException
    {
        String s2 = messageheader.findValue(s);
        if(s2 == null)
            return false;
        byte abyte1[];
        try
        {
            abyte1 = base64decoder.decodeBuffer(s2);
        }
        catch(IOException _ex)
        {
            throw new SecurityException("unable to decode base64 digest");
        }
        messagedigest.reset();
        byte abyte2[] = messagedigest.digest(abyte0);
        if(!MessageDigest.isEqual(abyte2, abyte1))
            throw new SecurityException("invalid " + messagedigest.getAlgorithm() + " signature file digest for " + s1);
        else
            return true;
    }

    private Vector getIdentities(SignerInfo asignerinfo[], PKCS7 pkcs7)
    {
        Vector vector = null;
        if(scope == null)
            return null;
        for(int i = 0; i < asignerinfo.length; i++)
        {
            SignerInfo signerinfo = asignerinfo[i];
            sun.security.x509.X509Cert x509cert = signerinfo.getCertificate(pkcs7);
            java.security.PublicKey publickey = x509cert.getPublicKey();
            Object obj = scope.getIdentity(publickey);
            if(obj == null)
                try
                {
                    String s = x509cert.getPrincipal().getName();
                    obj = new SystemIdentity(s, scope);
                    ((Identity) (obj)).setPublicKey(publickey);
                    ((Identity) (obj)).addCertificate(x509cert);
                }
                catch(KeyManagementException _ex) { }
            if(obj != null)
            {
                if(vector == null)
                    vector = new Vector();
                vector.addElement(obj);
            }
        }

        return vector;
    }

    private void addIdentities(String s, Vector vector)
    {
        if(vector == null)
            return;
        if(s.startsWith("./"))
            s = s.substring(2);
        Vector vector1 = (Vector)sigFileIdentities.get(s);
        if(vector1 == null)
        {
            vector1 = new Vector();
            sigFileIdentities.put(s, vector1);
        }
        Object obj;
        for(Enumeration enumeration = vector.elements(); enumeration.hasMoreElements(); vector1.addElement(obj))
            obj = enumeration.nextElement();

    }

    public Hashtable getVerifiedSignatures()
    {
        return verifiedIdentities;
    }

    public Enumeration getBlocks()
    {
        return signatures.elements();
    }

    public Hashtable getNameToHash()
    {
        return null;
    }

    public Manifest getManifest()
    {
        return manifest;
    }

    public Identity[] getIdentities(String s)
    {
        Vector vector = (Vector)verifiedIdentities.get(s);
        if(vector == null)
        {
            return null;
        } else
        {
            Identity aidentity[] = new Identity[vector.size()];
            vector.copyInto(aidentity);
            return aidentity;
        }
    }

    public static String toHex(byte abyte0[])
    {
        StringBuffer stringbuffer = new StringBuffer(abyte0.length * 2);
        for(int i = 0; i < abyte0.length; i++)
        {
            stringbuffer.append(hexc[abyte0[i] >> 4 & 0xf]);
            stringbuffer.append(hexc[abyte0[i] & 0xf]);
        }

        return stringbuffer.toString();
    }

    private static final boolean debug = false;
    private ZipEntry currentEntry;
    private Hashtable signatures;
    private Hashtable verifiedIdentities;
    private Hashtable sigFileIdentities;
    private boolean parsingBlock;
    private boolean parsingMeta;
    private boolean parsingManifest;
    private boolean processed;
    private Manifest manifest;
    private ByteArrayOutputStream baos;
    JarEntryVerifier jev;
    private IdentityScope scope;
    private static final char hexc[] = {
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 
        'a', 'b', 'c', 'd', 'e', 'f'
    };

}
