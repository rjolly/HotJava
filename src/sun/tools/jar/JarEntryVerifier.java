// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   JarVerifierStream.java

package sun.tools.jar;

import java.io.IOException;
import java.io.PrintStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Hashtable;
import sun.misc.BASE64Decoder;
import sun.misc.CharacterDecoder;
import sun.net.www.MessageHeader;

// Referenced classes of package sun.tools.jar:
//            JarException, JarVerifierStream, Manifest

class JarEntryVerifier
{

    JarEntryVerifier()
        throws JarException
    {
        try
        {
            for(int i = 0; i < supportedDigest.length; i++)
                supportedDigest[i] = MessageDigest.getInstance(supportedDigestAlg[i]);

        }
        catch(NoSuchAlgorithmException _ex)
        {
            throw new SecurityException("unable to create MessageDigest objects");
        }
        decoder = new BASE64Decoder();
    }

    void setEntry(Manifest manifest, String s)
        throws IOException
    {
        digest[0] = null;
        digest[1] = null;
        if(manifest == null || s == null)
            return;
        MessageHeader messageheader = manifest.getEntry(s);
        if(messageheader == null)
        {
            messageheader = manifest.getEntry("./" + s);
            if(messageheader == null)
                return;
        }
        name = s;
        for(int i = 0; i < supportedDigest.length; i++)
        {
            String s1 = messageheader.findValue(supportedDigestHeader[i]);
            if(s1 != null)
            {
                digest[i] = supportedDigest[i];
                digest[i].reset();
                manifestHash[i] = decoder.decodeBuffer(s1);
            } else
            {
                digest[i] = null;
                manifestHash[i] = null;
            }
        }

    }

    void update(byte byte0)
    {
        for(int i = 0; i < digest.length; i++)
            if(digest[i] != null)
                digest[i].update(byte0);

    }

    void update(byte abyte0[], int i, int j)
    {
        for(int k = 0; k < digest.length; k++)
            if(digest[k] != null)
                digest[k].update(abyte0, i, j);

    }

    void verify(Hashtable hashtable, Hashtable hashtable1)
        throws JarException
    {
        boolean flag = false;
        for(int i = 0; i < digest.length; i++)
            if(digest[i] != null)
            {
                byte abyte0[] = digest[i].digest();
                if(!MessageDigest.isEqual(abyte0, manifestHash[i]))
                    throw new SecurityException(supportedDigestAlg[i] + " digest error for " + name);
                flag = true;
            }

        if(flag)
        {
            Object obj = hashtable1.remove(name);
            if(obj != null)
                hashtable.put(name, obj);
        }
    }

    private static final boolean debug = false;
    private String supportedDigestHeader[] = {
        "MD5-Digest", "SHA-Digest"
    };
    private String supportedDigestAlg[] = {
        "MD5", "SHA"
    };
    private MessageDigest supportedDigest[] = {
        null, null
    };
    private MessageDigest digest[] = {
        null, null
    };
    private byte manifestHash[][] = {
        null, null
    };
    private BASE64Decoder decoder;
    private String name;
}
