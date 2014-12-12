// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   AppletObjectInputStream.java

package sunw.hotjava.applet;

import java.io.*;
import java.lang.reflect.Array;

// Referenced classes of package sunw.hotjava.applet:
//            AppletIllegalArgumentException, BasicAppletManager

class AppletObjectInputStream extends ObjectInputStream
{

    public AppletObjectInputStream(InputStream inputstream, ClassLoader classloader)
        throws IOException, StreamCorruptedException
    {
        super(inputstream);
        if(classloader == null)
        {
            throw new AppletIllegalArgumentException("appletillegalargumentexception.objectinputstream");
        } else
        {
            loader = classloader;
            return;
        }
    }

    private Class primitiveType(char c)
    {
        switch(c)
        {
        case 66: // 'B'
            return Byte.TYPE;

        case 67: // 'C'
            return Character.TYPE;

        case 68: // 'D'
            return Double.TYPE;

        case 70: // 'F'
            return Float.TYPE;

        case 73: // 'I'
            return Integer.TYPE;

        case 74: // 'J'
            return Long.TYPE;

        case 83: // 'S'
            return Short.TYPE;

        case 90: // 'Z'
            return Boolean.TYPE;
        }
        return null;
    }

    protected Class resolveClass(ObjectStreamClass objectstreamclass)
        throws IOException, ClassNotFoundException
    {
        String s = objectstreamclass.getName();
        if(s.startsWith("["))
        {
            int i;
            for(i = 1; s.charAt(i) == '['; i++);
            Class class1;
            if(s.charAt(i) == 'L')
            {
                String s1 = s.substring(i + 1, s.length() - 1);
                BasicAppletManager.checkNewAppletPackageAccess(s1);
                class1 = loader.loadClass(s1);
            } else
            {
                if(s.length() != i + 1)
                    throw new ClassNotFoundException(s);
                class1 = primitiveType(s.charAt(i));
            }
            int ai[] = new int[i];
            for(int j = 0; j < i; j++)
                ai[j] = 0;

            return Array.newInstance(class1, ai).getClass();
        } else
        {
            BasicAppletManager.checkNewAppletPackageAccess(s);
            return loader.loadClass(s);
        }
    }

    private ClassLoader loader;
}
