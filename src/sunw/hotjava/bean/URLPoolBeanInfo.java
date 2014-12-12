// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   URLPoolBeanInfo.java

package sunw.hotjava.bean;

import java.beans.MethodDescriptor;
import java.beans.SimpleBeanInfo;
import java.io.PrintStream;
import java.util.Vector;

public class URLPoolBeanInfo extends SimpleBeanInfo
{

    public MethodDescriptor[] getMethodDescriptors()
    {
        Vector vector = new Vector();
        try
        {
            Class class1 = sunw.hotjava.bean.URLPool.class;
            Class aclass[] = new Class[0];
            Class aclass1[] = {
                sunw.hotjava.bean.URLPoolListener.class
            };
            Class aclass2[] = {
                sunw.hotjava.bean.URLPoolEvent.class
            };
            Class aclass3[] = {
                java.lang.String.class
            };
            if(array$Lsunw$hotjava$bean$PoolEntry == null)
                array$Lsunw$hotjava$bean$PoolEntry = _mthclass$("[Lsunw.hotjava.bean.PoolEntry;");
            Class aclass4[] = {
                Integer.TYPE
            };
            java.lang.reflect.Method method = class1.getMethod("addURLPoolListener", aclass1);
            vector.addElement(new MethodDescriptor(method));
            method = class1.getMethod("add", aclass3);
            vector.addElement(new MethodDescriptor(method));
            method = class1.getMethod("createURLPoolFromFile", aclass3);
            vector.addElement(new MethodDescriptor(method));
            method = class1.getMethod("getURLPoolEntries", aclass);
            vector.addElement(new MethodDescriptor(method));
            method = class1.getMethod("get", aclass3);
            vector.addElement(new MethodDescriptor(method));
            method = class1.getMethod("getURLExpirationAmt", aclass);
            vector.addElement(new MethodDescriptor(method));
            method = class1.getMethod("setURLExpirationAmt", aclass4);
            vector.addElement(new MethodDescriptor(method));
            method = class1.getMethod("discardAllURLs", aclass);
            vector.addElement(new MethodDescriptor(method));
            method = class1.getMethod("purgeExpiredURLs", aclass);
            vector.addElement(new MethodDescriptor(method));
            method = class1.getMethod("saveURLPoolToFile", aclass3);
            vector.addElement(new MethodDescriptor(method));
            method = class1.getMethod("removeURLPoolListener", aclass1);
            vector.addElement(new MethodDescriptor(method));
            method = class1.getMethod("fireURLPoolEvent", aclass2);
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

    public URLPoolBeanInfo()
    {
    }

    static Class array$Lsunw$hotjava$bean$PoolEntry; /* synthetic field */
}
