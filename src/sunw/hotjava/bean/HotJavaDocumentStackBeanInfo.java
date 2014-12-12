// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   HotJavaDocumentStackBeanInfo.java

package sunw.hotjava.bean;

import java.beans.*;
import java.io.PrintStream;
import java.util.Vector;

public class HotJavaDocumentStackBeanInfo extends SimpleBeanInfo
{

    public PropertyDescriptor[] getPropertyDescriptors()
    {
        try
        {
            Vector vector = new Vector();
            PropertyDescriptor propertydescriptor = new PropertyDescriptor("contentsDepth", beanClass, "getContentsDepth", "setContentsDepth");
            vector.addElement(propertydescriptor);
            propertydescriptor = new PropertyDescriptor("logicalDepth", beanClass, "getLogicalDepth", "setLogicalDepth");
            vector.addElement(propertydescriptor);
            propertydescriptor = new PropertyDescriptor("nextDocumentAvailable", beanClass, "isNextAvailable", "setNextAvailable");
            propertydescriptor.setBound(true);
            vector.addElement(propertydescriptor);
            propertydescriptor = new PropertyDescriptor("previousDocumentAvailable", beanClass, "isPreviousAvailable", "setPreviousAvailable");
            propertydescriptor.setBound(true);
            vector.addElement(propertydescriptor);
            PropertyDescriptor apropertydescriptor[] = new PropertyDescriptor[vector.size()];
            for(int i = 0; i < vector.size(); i++)
                apropertydescriptor[i] = (PropertyDescriptor)vector.elementAt(i);

            return apropertydescriptor;
        }
        catch(IntrospectionException introspectionexception)
        {
            throw new Error(introspectionexception.toString());
        }
    }

    public int getDefaultPropertyIndex()
    {
        return 0;
    }

    public EventSetDescriptor[] getEventSetDescriptors()
    {
        try
        {
            EventSetDescriptor eventsetdescriptor = new EventSetDescriptor(sunw.hotjava.bean.HotJavaDocumentStack.class, "propertyChange", java.beans.PropertyChangeListener.class, "propertyChange");
            eventsetdescriptor.setDisplayName("bound property change");
            EventSetDescriptor eventsetdescriptor1 = new EventSetDescriptor(sunw.hotjava.bean.HotJavaDocumentStack.class, "Browser History Command", sunw.hotjava.bean.BrowserHistoryListener.class, "executeHistoryCommand");
            eventsetdescriptor1.setDisplayName("browser history commands");
            EventSetDescriptor aeventsetdescriptor[] = {
                eventsetdescriptor, eventsetdescriptor1
            };
            return aeventsetdescriptor;
        }
        catch(IntrospectionException _ex)
        {
            return null;
        }
    }

    public MethodDescriptor[] getMethodDescriptors()
    {
        Vector vector = new Vector();
        try
        {
            Class aclass[] = new Class[0];
            Class aclass1[] = {
                java.beans.PropertyChangeListener.class
            };
            Class aclass2[] = {
                Boolean.TYPE
            };
            Class aclass3[] = {
                sunw.hotjava.bean.BrowserHistoryEvent.class
            };
            java.lang.reflect.Method method = beanClass.getMethod("previousDocument", aclass);
            vector.addElement(new MethodDescriptor(method));
            method = beanClass.getMethod("nextDocument", aclass);
            vector.addElement(new MethodDescriptor(method));
            method = beanClass.getMethod("eraseDocumentHistory", aclass);
            vector.addElement(new MethodDescriptor(method));
            method = beanClass.getMethod("addPropertyChangeListener", aclass1);
            vector.addElement(new MethodDescriptor(method));
            method = beanClass.getMethod("removePropertyChangeListener", aclass1);
            vector.addElement(new MethodDescriptor(method));
            method = beanClass.getMethod("isNextAvailable", aclass);
            vector.addElement(new MethodDescriptor(method));
            method = beanClass.getMethod("setNextAvailable", aclass2);
            vector.addElement(new MethodDescriptor(method));
            method = beanClass.getMethod("isPreviousAvailable", aclass);
            vector.addElement(new MethodDescriptor(method));
            method = beanClass.getMethod("setPreviousAvailable", aclass2);
            vector.addElement(new MethodDescriptor(method));
            method = beanClass.getMethod("executeHistoryCommand", aclass3);
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

    public HotJavaDocumentStackBeanInfo()
    {
    }

    private static final Class beanClass;

    static 
    {
        beanClass = sunw.hotjava.bean.HotJavaDocumentStack.class;
    }
}
