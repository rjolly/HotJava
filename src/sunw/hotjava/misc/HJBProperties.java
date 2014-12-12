// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   HJBProperties.java

package sunw.hotjava.misc;

import java.awt.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.zip.GZIPInputStream;

// Referenced classes of package sunw.hotjava.misc:
//            ColorNameTable

public class HJBProperties extends Properties
{

    public static Object getScriptingEngineSyncObject()
    {
        return scriptingEngineSync;
    }

    public static void createProperties(String s, String as[], String as1[])
    {
        if(props.get(s) != null)
            return;
        HJBProperties hjbproperties = new HJBProperties(as, as1);
        props.put(s, hjbproperties);
        if(s.equals("hjbrowser"))
        {
            props.put("beanPropertiesKey", hjbproperties);
            props.put("securityProperties", hjbproperties);
        }
        System.setProperties(hjbproperties);
    }

    public static HJBProperties getHJBProperties(String s)
    {
        HJBProperties hjbproperties = (HJBProperties)props.get(s);
        if(hjbproperties != null)
        {
            return hjbproperties;
        } else
        {
            createProperties(s, null, null);
            return (HJBProperties)props.get(s);
        }
    }

    private HJBProperties(String as[], String as1[])
    {
        changed = false;
        bundles = new Vector(2);
        oldSystemProperties = System.getProperties();
        oldSystemProperties.put("java.version.applet", "true");
        oldSystemProperties.put("java.vendor.applet", "true");
        oldSystemProperties.put("java.vendor.url.applet", "true");
        oldSystemProperties.put("java.class.version.applet", "true");
        oldSystemProperties.put("os.name.applet", "true");
        oldSystemProperties.put("os.version.applet", "true");
        oldSystemProperties.put("os.arch.applet", "true");
        oldSystemProperties.put("file.separator.applet", "true");
        oldSystemProperties.put("path.separator.applet", "true");
        oldSystemProperties.put("line.separator.applet", "true");
        if(as != null && as.length > 0)
            initStaticProps(as);
        if(dynamicProperties == null)
        {
            initDynamicProps();
            super.defaults = dynamicProperties;
        }
        Locale locale = new Locale(Locale.ENGLISH.getLanguage(), Locale.ENGLISH.getCountry());
        if(dynamicProperties != null)
        {
            String s2 = dynamicProperties.getProperty("locale.variant");
            String s = dynamicProperties.getProperty("locale.language");
            String s1 = dynamicProperties.getProperty("locale.country");
            if(s != null && s1 != null)
                if(s2 != null)
                    locale = new Locale(s, s1, s2);
                else
                    locale = new Locale(s, s1);
        }
        if(as1 != null && as1.length > 0)
        {
            System.gc();
            initResourceBundle(as1, locale);
        }
    }

    private void initStaticProps(String as[])
    {
        staticProperties = new Properties();
        for(int i = 0; i < as.length; i++)
            if(as[i] != null)
            {
                Object obj = null;
                Object obj1 = null;
                try
                {
                    obj = (sunw.hotjava.misc.HJBProperties.class).getResourceAsStream(as[i]);
                    BufferedInputStream bufferedinputstream = new BufferedInputStream(((InputStream) (obj)));
                    staticProperties.load(bufferedinputstream);
                }
                catch(Exception _ex)
                {
                    try
                    {
                        obj = getJarPropertyInputStream(as[i]);
                        BufferedInputStream bufferedinputstream1 = new BufferedInputStream(((InputStream) (obj)));
                        staticProperties.load(bufferedinputstream1);
                    }
                    catch(IOException _ex2)
                    {
                        try
                        {
                            obj = new FileInputStream(as[i]);
                            BufferedInputStream bufferedinputstream2 = new BufferedInputStream(((InputStream) (obj)));
                            staticProperties.load(bufferedinputstream2);
                        }
                        catch(IOException _ex3)
                        {
                            try
                            {
                                obj = (new URL(as[i])).openStream();
                                BufferedInputStream bufferedinputstream3 = new BufferedInputStream(((InputStream) (obj)));
                                staticProperties.load(((InputStream) (obj)));
                            }
                            catch(Exception _ex4) { }
                        }
                    }
                }
                finally
                {
                    if(obj != null)
                        try
                        {
                            ((InputStream) (obj)).close();
                        }
                        catch(IOException _ex) { }
                }
            }

    }

    private void initDynamicProps()
    {
        String s = File.separator;
        String s1 = System.getProperty("user.home");
        if(s1 != null && !s1.endsWith(s))
            s1 = s1 + s;
        dynamicProperties = new Properties();
        String s2 = s1 + ".hotjava" + s + "properties";
        try
        {
            FileInputStream fileinputstream = new FileInputStream(s2);
            BufferedInputStream bufferedinputstream = new BufferedInputStream(fileinputstream);
            dynamicProperties.load(bufferedinputstream);
            bufferedinputstream.close();
            return;
        }
        catch(IOException _ex)
        {
            return;
        }
    }

    private void initResourceBundle(String as[], Locale locale)
    {
        for(int i = 0; i < as.length; i++)
        {
            ResourceBundle resourcebundle = null;
            try
            {
                String s = "/";
                resourcebundle = ResourceBundle.getBundle("lib" + s + as[i], locale);
            }
            catch(MissingResourceException _ex)
            {
                resourcebundle = null;
            }
            if(resourcebundle != null)
                bundles.addElement(resourcebundle);
        }

    }

    private InputStream getJarPropertyInputStream(String s)
        throws IOException
    {
        InputStream inputstream = (sunw.hotjava.misc.HJBProperties.class).getResourceAsStream(s + ".gz");
        if(inputstream == null)
            throw new IOException("Unable to find a property file : " + s);
        else
            return new GZIPInputStream(inputstream);
    }

    public int getInteger(String s)
    {
        return getInteger(s, 0);
    }

    public int getInteger(String s, int i)
    {
        String s1 = getProperty(s);
        if(s1 == null)
            return i;
        try
        {
            Integer integer = Integer.decode(s1);
            return integer.intValue();
        }
        catch(NumberFormatException _ex)
        {
            return i;
        }
    }

    public boolean getBoolean(String s)
    {
        String s1 = getProperty(s);
        return Boolean.valueOf(s1).booleanValue();
    }

    public boolean isPropertyExists(String s)
    {
        String s1 = dynamicProperties.getProperty(s, null);
        for(int i = 0; i < bundles.size(); i++)
        {
            ResourceBundle resourcebundle = (ResourceBundle)bundles.elementAt(i);
            if(resourcebundle == null || s1 != null)
                continue;
            try
            {
                s1 = resourcebundle.getString(s);
                if(s1 != null)
                    break;
            }
            catch(MissingResourceException _ex) { }
        }

        if(s1 == null)
            s1 = staticProperties.getProperty(s, null);
        return s1 != null;
    }

    public String getProperty(String s)
    {
        return getProperty(s, null);
    }

    public String getProperty(String s, String s1)
    {
        String s2 = dynamicProperties.getProperty(s, null);
        if(s2 == null)
        {
            for(int i = 0; i < bundles.size(); i++)
            {
                ResourceBundle resourcebundle = (ResourceBundle)bundles.elementAt(i);
                try
                {
                    s2 = resourcebundle.getString(s);
                    if(s2 != null)
                        break;
                }
                catch(MissingResourceException _ex) { }
            }

        }
        if(s2 == null && staticProperties != null)
            s2 = staticProperties.getProperty(s, null);
        int j = 0;
        boolean flag = false;
        if(s2 != null)
            while((j = s2.indexOf('&', j)) >= 0) 
            {
                int k = s2.indexOf(';', j);
                if(k < 0)
                    k = s2.length();
                String s3 = s2.substring(j + 1, k);
                String s4 = s2.substring(0, j);
                String s5;
                if(k == s2.length())
                    s5 = "";
                else
                    s5 = s2.substring(k + 1);
                String s6 = getProperty(s3);
                String s7 = System.getProperty("os.name");
                if(s3.equals("contentviewerspreference") && s7 != null && getProperty("contentviewers.platform.notsupported").toLowerCase().indexOf(s7.toLowerCase()) != -1)
                    s6 = " ";
                if(s6 == null)
                    j++;
                else
                    s2 = s4 + s6 + s5;
            }
        if(s2 != null && !s2.equals(""))
            return s2.replace('\t', ' ');
        else
            return oldSystemProperties.getProperty(s, s1);
    }

    public String getPropertyReplace(String s, String s1)
    {
        String as[] = {
            s1
        };
        return getPropertyReplace(s, as);
    }

    public String getPropertyReplace(String s, String s1, String s2)
    {
        String as[] = {
            s1, s2
        };
        return getPropertyReplace(s, as);
    }

    public String getPropertyReplace(String s, String s1, String s2, String s3)
    {
        String as[] = {
            s1, s2, s3
        };
        return getPropertyReplace(s, as);
    }

    public String getPropertyReplace(String s, String as[])
    {
        String s1 = null;
        if(s1 == null)
            s1 = getProperty(s);
        if(s1 == null)
            return null;
        for(int i = 0; i < as.length; i++)
            if(as[i] == null)
                as[i] = "null";

        String s2 = null;
        try
        {
            s2 = paramStr(s1, as);
        }
        catch(NullPointerException nullpointerexception)
        {
            System.err.println("ParamStr.subst(): Exception occured: " + nullpointerexception);
        }
        return s2;
    }

    public Object put(Object obj, Object obj1)
    {
        changed = true;
        return dynamicProperties.put(obj, obj1);
    }

    public void put(String s, String s1)
    {
        put((Object)s, (Object)s1);
    }

    public boolean isChanged()
    {
        return changed;
    }

    public void setChanged(boolean flag)
    {
        changed = flag;
    }

    public void remove(String s)
    {
        dynamicProperties.remove(s);
    }

    public Color getColor(String s, Color color)
    {
        if(s == null)
            return color;
        String s1 = getProperty(s);
        Color color1 = null;
        if(s1 != null)
            color1 = mapNamedColor(s1.trim());
        if(color1 == null)
            color1 = mapNamedColor(s);
        if(color1 == null)
            return color;
        else
            return color1;
    }

    private Color mapNamedColor(String s)
    {
        if(s == null)
            return null;
        Color color = ColorNameTable.getColor(s);
        if(color == null)
        {
            if(s.indexOf('#') < 0 && s.indexOf("0x") < 0)
                s = "#" + s;
            try
            {
                color = Color.decode(s);
            }
            catch(NumberFormatException _ex)
            {
                color = null;
            }
        }
        return color;
    }

    public Font getFont(String s)
    {
        return getFont(s, null);
    }

    public Font getFont(String s, Font font)
    {
        String s1 = getProperty(s);
        Font font1 = null;
        if(s1 != null)
            font1 = Font.decode(s1);
        if(font1 == null)
            return font;
        else
            return font1;
    }

    public Image getImage(String s)
    {
        if(s == null)
            return null;
        if(imgBase == null)
            try
            {
                String s1 = getProperty("images.url");
                imgBase = new URL(s1);
            }
            catch(MalformedURLException _ex) { }
        try
        {
            String s2 = getProperty(s);
            URL url = null;
            if(s2 == null)
                url = new URL(imgBase, s);
            else
                url = new URL(s2);
            Toolkit toolkit = Toolkit.getDefaultToolkit();
            Image image = toolkit.getImage(url);
            return image;
        }
        catch(MalformedURLException _ex)
        {
            return null;
        }
    }

    public Properties getDynamicProperties()
    {
        return dynamicProperties;
    }

    public synchronized void save()
    {
        String s = File.separator;
        String s1 = System.getProperty("user.home");
        if(s1 != null && !s1.endsWith(s))
            s1 = s1 + s;
        try
        {
            File file = new File(s1 + ".hotjava" + s);
            file.mkdirs();
            File file1 = new File(file, "properties");
            System.out.println("Saving: " + file1.getPath());
            FileOutputStream fileoutputstream = new FileOutputStream(file1);
            dynamicProperties.save(fileoutputstream, "# HotJava Properties");
            return;
        }
        catch(IOException _ex)
        {
            return;
        }
    }

    private String paramStr(String s, Object aobj[])
    {
        int i = s.length();
        StringBuffer stringbuffer = new StringBuffer(i + 40);
        int j = 0;
        while(j < i) 
        {
            int k = s.indexOf("{", j);
            if(k != -1)
            {
                if(k > j)
                    stringbuffer.append(s.substring(j, k));
                int l = k + 2;
                if(l < i)
                {
                    if(s.charAt(l) == '}')
                    {
                        char c = s.charAt(k + 1);
                        byte byte0;
                        switch(c)
                        {
                        case 48: // '0'
                            byte0 = 0;
                            break;

                        case 49: // '1'
                            byte0 = 1;
                            break;

                        case 50: // '2'
                            byte0 = 2;
                            break;

                        case 51: // '3'
                            byte0 = 3;
                            break;

                        case 52: // '4'
                            byte0 = 4;
                            break;

                        case 53: // '5'
                            byte0 = 5;
                            break;

                        case 54: // '6'
                            byte0 = 6;
                            break;

                        case 55: // '7'
                            byte0 = 7;
                            break;

                        case 56: // '8'
                            byte0 = 8;
                            break;

                        case 57: // '9'
                            byte0 = 9;
                            break;

                        default:
                            stringbuffer.append(s.substring(k, l + 1));
                            j = l + 1;
                            continue;
                        }
                        stringbuffer.append(aobj[byte0].toString());
                        j = l + 1;
                    } else
                    {
                        stringbuffer.append("{");
                        j = k + 1;
                    }
                    continue;
                }
                stringbuffer.append(s.substring(k, i));
            } else
            {
                stringbuffer.append(s.substring(j, i));
            }
            break;
        }
        return stringbuffer.toString();
    }

    public static void setLastFocusHolder(Frame frame)
    {
        lastFocusHolder = frame;
    }

    public static Frame getLastFocusHolder()
    {
        return lastFocusHolder;
    }

    private static Hashtable props = new Hashtable(5);
    private boolean changed;
    private static Object scriptingEngineSync = new Object();
    private static Properties dynamicProperties;
    private Vector bundles;
    private Properties oldSystemProperties;
    private Properties staticProperties;
    private URL imgBase;
    static Frame lastFocusHolder = null;

}
