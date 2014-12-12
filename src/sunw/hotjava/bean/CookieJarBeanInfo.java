// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   CookieJarBeanInfo.java

package sunw.hotjava.bean;

import java.beans.MethodDescriptor;
import java.beans.SimpleBeanInfo;
import java.io.PrintStream;
import java.util.Vector;

public class CookieJarBeanInfo extends SimpleBeanInfo
{

    public MethodDescriptor[] getMethodDescriptors()
    {
        Vector vector = new Vector();
        try
        {
            Class class1 = sunw.hotjava.bean.CookieJar.class;
            Class aclass[] = new Class[0];
            Class aclass1[] = {
                java.lang.String.class
            };
            Class aclass2[] = {
                java.lang.String.class, java.net.URL.class
            };
            Class aclass3[] = {
                java.beans.VetoableChangeListener.class
            };
            Class aclass4[] = {
                Boolean.TYPE
            };
            Class aclass5[] = {
                java.net.URL.class
            };
            Class aclass6[] = {
                sunw.hotjava.misc.HttpCookie[].class
            };
            java.lang.reflect.Method method = class1.getMethod("addToCookieJar", aclass2);
            vector.addElement(new MethodDescriptor(method));
            method = class1.getMethod("addToCookieJar", aclass6);
            vector.addElement(new MethodDescriptor(method));
            method = class1.getMethod("addVetoableChangeListener", aclass3);
            vector.addElement(new MethodDescriptor(method));
            method = class1.getMethod("discardAllCookies", aclass);
            vector.addElement(new MethodDescriptor(method));
            method = class1.getMethod("getAllCookies", aclass);
            vector.addElement(new MethodDescriptor(method));
            method = class1.getMethod("getCookiesForURL", aclass5);
            vector.addElement(new MethodDescriptor(method));
            method = class1.getMethod("loadCookieJarFromFile", aclass1);
            vector.addElement(new MethodDescriptor(method));
            method = class1.getMethod("removeVetoableChangeListener", aclass3);
            vector.addElement(new MethodDescriptor(method));
            method = class1.getMethod("saveCookieJarToFile", aclass1);
            vector.addElement(new MethodDescriptor(method));
            method = class1.getMethod("setCookieDisable", aclass4);
            vector.addElement(new MethodDescriptor(method));
            MethodDescriptor amethoddescriptor[] = new MethodDescriptor[vector.size()];
            for(int i = 0; i < vector.size(); i++)
                amethoddescriptor[i] = (MethodDescriptor)vector.elementAt(i);

            return amethoddescriptor;
        }
        catch(NoSuchMethodException nosuchmethodexception)
        {
            nosuchmethodexception.printStackTrace();
            System.err.println("NoSuchMethodException: " + nosuchmethodexception.getMessage());
            throw new Error(nosuchmethodexception.toString());
        }
    }

    public CookieJarBeanInfo()
    {
    }
}
