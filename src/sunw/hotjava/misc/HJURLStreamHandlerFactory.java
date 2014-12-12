// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   HJURLStreamHandlerFactory.java

package sunw.hotjava.misc;

import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;

public class HJURLStreamHandlerFactory
    implements URLStreamHandlerFactory
{

    public URLStreamHandler createURLStreamHandler(String s)
    {
        String s1 = "sunw.hotjava.protocol." + s + ".Handler";
        try
        {
            Class class1 = Class.forName(s1);
            if(class1 == null)
                return null;
            else
                return (URLStreamHandler)class1.newInstance();
        }
        catch(Exception _ex)
        {
            return null;
        }
    }

    public HJURLStreamHandlerFactory()
    {
    }
}
