// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   PrefAppletManager.java

package sunw.hotjava.applets;

import java.util.*;
import sunw.hotjava.misc.HJBProperties;

// Referenced classes of package sunw.hotjava.applets:
//            PrefApplet

public class PrefAppletManager
{

    public static void registerApplet(PrefApplet prefapplet)
    {
        String s = prefapplet.getName();
        if(applets == null)
            applets = new Hashtable();
        if(properties == null)
            properties = new Hashtable();
        Vector vector = (Vector)applets.get(s);
        if(vector == null)
        {
            vector = new Vector();
            applets.put(s, vector);
            Properties properties1 = new Properties();
            properties.put(s, properties1);
            String as[] = prefapplet.getPropertyNames();
            if(as != null)
            {
                for(int i = 0; i < as.length; i++)
                {
                    String s1 = hjbProps.getProperty(as[i]);
                    if(s1 == null)
                    {
                        s1 = beanProps.getProperty(as[i]);
                        if(s1 == null)
                            s1 = "";
                    }
                    properties1.put(as[i], s1);
                }

            }
        }
        vector.addElement(prefapplet);
    }

    public static void deregisterApplet(PrefApplet prefapplet)
    {
        String s = prefapplet.getName();
        Vector vector = (Vector)applets.get(s);
        if(vector != null)
        {
            vector.removeElement(prefapplet);
            if(vector.size() == 0)
            {
                applets.remove(s);
                properties.remove(s);
            }
        }
    }

    public static Properties getProperties(String s)
    {
        return (Properties)properties.get(s);
    }

    public static String getHJBProperty(String s)
    {
        return hjbProps.getProperty(s);
    }

    public static void applyApplet(String s)
    {
        if(s == null)
            return;
        Vector vector = (Vector)applets.get(s);
        if(vector == null)
            return;
        PrefApplet prefapplet = (PrefApplet)vector.firstElement();
        if(prefapplet != null)
            prefapplet.apply();
    }

    public static void resetApplet(String s)
    {
        if(s == null)
            return;
        Vector vector = (Vector)applets.get(s);
        if(vector == null)
            return;
        PrefApplet prefapplet = (PrefApplet)vector.firstElement();
        if(prefapplet != null)
            prefapplet.reset();
    }

    public static void updateApplet(String s)
    {
        if(s == null)
            return;
        Vector vector = (Vector)((Vector)applets.get(s)).clone();
        if(vector != null)
        {
            PrefApplet prefapplet;
            for(Enumeration enumeration = vector.elements(); enumeration.hasMoreElements(); prefapplet.updateFields())
                prefapplet = (PrefApplet)enumeration.nextElement();

        }
    }

    public PrefAppletManager()
    {
    }

    private static Hashtable applets;
    private static Hashtable properties;
    private static HJBProperties hjbProps = HJBProperties.getHJBProperties("hjbrowser");
    private static HJBProperties beanProps = HJBProperties.getHJBProperties("beanPropertiesKey");

}
