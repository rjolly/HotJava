// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   HJFrame.java

package sunw.hotjava;

import java.net.URL;
import sun.misc.BASE64Encoder;
import sun.misc.CharacterEncoder;

// Referenced classes of package sunw.hotjava:
//            HJFrame, PasswordCache

class PasswordCacheEntry
{

    PasswordCacheEntry(URL url1, String s)
    {
        url = url1;
        userPasswordPair = s;
        creationTime = System.currentTimeMillis();
    }

    String getEncodedRepresentation()
    {
        byte abyte0[] = userPasswordPair.getBytes();
        return (new BASE64Encoder()).encode(abyte0);
    }

    URL url;
    String userPasswordPair;
    long creationTime;
}
