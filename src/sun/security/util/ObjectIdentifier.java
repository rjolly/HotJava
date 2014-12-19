// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ObjectIdentifier.java

package sun.security.util;

import java.io.*;

// Referenced classes of package sun.security.util:
//            DerInputStream, DerOutputStream, DerValue, DerInputBuffer

public final class ObjectIdentifier
    implements Serializable
{

    public ObjectIdentifier(int ai[])
    {
        try
        {
            components = (int[])ai.clone();
            componentLen = ai.length;
            return;
        }
        catch(Throwable _ex)
        {
            System.out.println("X509.ObjectIdentifier(), no cloning!");
        }
    }

    ObjectIdentifier(DerInputStream derinputstream)
        throws IOException
    {
        byte byte0 = (byte)derinputstream.getByte();
        if(byte0 != 6)
            throw new IOException("X509.ObjectIdentifier() -- data isn't an object ID (tag = " + byte0 + ")");
        int i = derinputstream.available() - derinputstream.getLength() - 1;
        if(i < 0)
        {
            throw new IOException("X509.ObjectIdentifier() -- not enough data");
        } else
        {
            initFromEncoding(derinputstream, i);
            return;
        }
    }

    ObjectIdentifier(DerInputBuffer derinputbuffer)
        throws IOException
    {
        initFromEncoding(new DerInputStream(derinputbuffer), 0);
    }

    private void initFromEncoding(DerInputStream derinputstream, int i)
        throws IOException
    {
        boolean flag = true;
        components = new int[5];
        componentLen = 0;
        while(derinputstream.available() > i) 
        {
            int j = getComponent(derinputstream);
            if(flag)
            {
                byte byte0;
                if(j < 40)
                    byte0 = 0;
                else
                if(j < 80)
                    byte0 = 1;
                else
                    byte0 = 2;
                int k = j - byte0 * 40;
                components[0] = byte0;
                components[1] = k;
                componentLen = 2;
                flag = false;
            } else
            {
                if(componentLen >= components.length)
                {
                    int ai[] = new int[components.length + 5];
                    System.arraycopy(components, 0, ai, 0, components.length);
                    components = ai;
                }
                components[componentLen++] = j;
            }
        }
        if(derinputstream.available() != i)
            throw new IOException("X509.ObjectIdentifier() -- malformed input data");
        else
            return;
    }

    void emit(DerOutputStream deroutputstream)
        throws IOException
    {
        DerOutputStream deroutputstream1 = new DerOutputStream();
        deroutputstream1.write(components[0] * 40 + components[1]);
        for(int i = 2; i < componentLen; i++)
            putComponent(deroutputstream1, components[i]);

        deroutputstream.write((byte)6, deroutputstream1);
    }

    private static int getComponent(DerInputStream derinputstream)
        throws IOException
    {
        int j = 0;
        int i = 0;
        for(; j < 4; j++)
        {
            i <<= 7;
            int k = derinputstream.getByte();
            i |= k & 0x7f;
            if((k & 0x80) == 0)
                return i;
        }

        throw new IOException("X509.OID, component value too big");
    }

    private static void putComponent(DerOutputStream deroutputstream, int i)
        throws IOException
    {
        byte abyte0[] = new byte[4];
        int j;
        for(j = 0; j < 4; j++)
        {
            abyte0[j] = (byte)(i & 0x7f);
            i >>>= 7;
            if(i == 0)
                break;
        }

        for(; j > 0; j--)
            deroutputstream.write(abyte0[j] | 0x80);

        deroutputstream.write(abyte0[0]);
    }

    public boolean precedes(ObjectIdentifier objectidentifier)
    {
        if(objectidentifier == this || componentLen < objectidentifier.componentLen)
            return false;
        if(objectidentifier.componentLen < componentLen)
            return true;
        for(int i = 0; i < componentLen; i++)
            if(objectidentifier.components[i] < components[i])
                return true;

        return false;
    }

    public boolean equals(Object obj)
    {
        if(obj instanceof ObjectIdentifier)
            return equals((ObjectIdentifier)obj);
        else
            return false;
    }

    public boolean equals(ObjectIdentifier objectidentifier)
    {
        if(objectidentifier == this)
            return true;
        if(componentLen != objectidentifier.componentLen)
            return false;
        for(int i = 0; i < componentLen; i++)
            if(components[i] != objectidentifier.components[i])
                return false;

        return true;
    }

    public int hashCode()
    {
        return toString().hashCode();
    }

    public String toString()
    {
        int i = 0;
        String s = "";
        for(; i < componentLen; i++)
        {
            if(i != 0)
                s = s + ".";
            s = s + components[i];
        }

        return s;
    }

    private int components[];
    private int componentLen;
    private static final int allocationQuantum = 5;
}
