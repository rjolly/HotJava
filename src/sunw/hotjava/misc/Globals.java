// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Globals.java

package sunw.hotjava.misc;

import java.awt.*;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.*;
import java.util.StringTokenizer;
import java.util.Vector;
import sun.net.www.http.HttpClient;
import sun.net.www.protocol.http.HttpURLConnection;
import sunw.hotjava.applet.ScriptingEngineLoader;
import sunw.hotjava.doc.DocError;
import sunw.hotjava.doc.TextItem;
import sunw.hotjava.script.ScriptingEngineInterface;
import sunw.html.DTD;

// Referenced classes of package sunw.hotjava.misc:
//            ColorNameTable, HJBProperties, HJColor, HJURLStreamHandlerFactory, 
//            RequestProcessor, StaticStateWatcher, TimeStampObject, URLCanonicalizer

public class Globals
{
    public static abstract class QuitHandler
    {

        public abstract void startQuitAction();

        public abstract void quitAction();

        public QuitHandler()
        {
        }
    }


    public static URL removeGETstring(URL url)
        throws MalformedURLException
    {
        String s = url.getFile();
        int i = s.lastIndexOf('?');
        if(i != -1)
            s = s.substring(0, i);
        return new URL(url.getProtocol(), url.getHost(), url.getPort(), s);
    }

    public static ScriptingEngineInterface getScriptingEngine()
    {
        HJBProperties hjbproperties = HJBProperties.getHJBProperties("beanPropertiesKey");
        String s = hjbproperties.getProperty("hotjava.appletsJS.enableJavaScript", "true");
        if(!"true".equals(s))
            return null;
        if(!loadengine)
            synchronized("com.sun.javascript.ScriptingEngine")
            {
                if(!loadengine)
                {
                    try
                    {
                        URL url = new URL("doc:/scriptingengine/");
                        ScriptingEngineLoader scriptingengineloader = ScriptingEngineLoader.createLoader(url);
                        String s2 = hjbproperties.getProperty("hotjava.appletsJS.engineJar");
                        InputStream inputstream;
                        if(s2 == null)
                            inputstream = ClassLoader.getSystemResourceAsStream("lib/js.jar");
                        else
                            inputstream = (new URL(s2)).openStream();
                        if(inputstream != null)
                            try
                            {
//                                scriptingengineloader.getResourceLoader().loadJar(url, inputstream);
                            }
                            finally
                            {
                                inputstream.close();
                            }
//                        engine = (ScriptingEngineInterface)scriptingengineloader.loadClass("com.sun.javascript.ScriptingEngine").newInstance();
                        engine = (ScriptingEngineInterface)(new URLClassLoader(new URL[] {ClassLoader.getSystemResource("lib/js.jar")})).loadClass("com.sun.javascript.ScriptingEngine").newInstance();
                    }
                    catch(IOException ioexception)
                    {
                        ioexception.printStackTrace();
                    }
                    catch(RuntimeException runtimeexception)
                    {
                        runtimeexception.printStackTrace();
                    }
                    catch(ClassNotFoundException _ex) { }
                    catch(InstantiationException _ex) { }
                    catch(IllegalAccessException _ex) { }
                    finally
                    {
                        loadengine = true;
                    }
                    if(engine == null)
                        System.out.println("[[[ Failed to load scripting engine ]]]");
                }
            }
        return engine;
    }

    public static void initProperties()
        throws IOException, Exception
    {
        String s = "/";
        String as[] = new String[2];
        as[0] = s + "lib" + s + "hotjavaBean.properties";
        as[1] = s + "lib" + s + "properties.";
        String as1[] = {
            "hjResourceBundle"
        };
        HJBProperties.createProperties("beanPropertiesKey", as, as1);
        initProtocolHandlerFactory();
        syncNetworkingWithProperties();
    }

    public static void initProtocolHandlerFactory()
        throws Exception
    {
        try
        {
            URL.setURLStreamHandlerFactory(new HJURLStreamHandlerFactory());
            return;
        }
        catch(Throwable throwable)
        {
            HJBProperties hjbproperties = HJBProperties.getHJBProperties("beanPropertiesKey");
            String s = "hotjava.hide.protocol.warning";
            Object obj;
            if(hjbproperties.getBoolean(s))
            {
                obj = null;
            } else
            {
                String s1 = "hotjava.urlfactory.warning";
                String s2 = "Warning: Could not install HTML component protocol handler factor!";
                String s3 = hjbproperties.getProperty(s1, s2);
                System.out.println(s3);
            }
            return;
        }
    }

    public static void syncNetworkingWithProperties()
    {
        HJBProperties hjbproperties = HJBProperties.getHJBProperties("beanPropertiesKey");
        HttpClient.resetProperties();
//        if(hjbproperties.isPropertyExists("ftpProxySet"))
//            FtpClient.useFtpProxy = hjbproperties.getBoolean("ftpProxySet");
//        else
//            FtpClient.useFtpProxy = true;
//        FtpClient.ftpProxyHost = hjbproperties.getProperty("ftpProxyHost");
//        FtpClient.ftpProxyPort = hjbproperties.getInteger("ftpProxyPort", 80);
    }

    public static Object getAwtLock()
    {
        if(awtLock == null)
        {
            awtLock = (new Label()).getTreeLock();
            if(awtLock == null)
                awtLock = new Object();
        }
        return awtLock;
    }

    public static void initSystem()
        throws Exception
    {
        if(initSystemWasCalled())
            return;
        initProperties();
        HJBProperties hjbproperties = HJBProperties.getHJBProperties("beanPropertiesKey");
        String s = "hotjava.notifySystemInited";
        String s1 = hjbproperties.getProperty(s);
        if(s1 != null)
        {
            for(StringTokenizer stringtokenizer = new StringTokenizer(s1, "|"); stringtokenizer.hasMoreTokens();)
            {
                String s2 = stringtokenizer.nextToken();
                try
                {
                    Class.forName(s2).newInstance();
                }
                catch(Throwable _ex) { }
            }

        }
        TextItem.initProperties();
    }

    private static synchronized boolean initSystemWasCalled()
    {
        if(initSystemWasCalledVar)
        {
            return true;
        } else
        {
            initSystemWasCalledVar = true;
            return false;
        }
    }

    private static void ensureDirectoryPathExists(String s)
    {
        File file = new File(s);
        if(!file.isDirectory())
            file.mkdirs();
    }

    public static DTD getDefaultDTD()
    {
        if(defaultDTD == null)
            synchronized(sunw.hotjava.misc.Globals.class)
            {
                if(defaultDTD == null)
                {
                    HJBProperties hjbproperties = HJBProperties.getHJBProperties("beanPropertiesKey");
                    String s = hjbproperties.getProperty("dtd");
                    try
                    {
                        defaultDTD = DTD.getDTD(s);
                    }
                    catch(IOException _ex)
                    {
                        throw new DocError("could not get default dtd: " + s);
                    }
                    defaultDTD = createDTD(defaultDTD, s);
                }
            }
        return defaultDTD;
    }

    public static DTD createDTD(DTD dtd, String s)
    {
        String s1 = "/lib/" + s + ".bdtd";
        try
        {
            InputStream inputstream = getBeanResourceAsStream(s1);
            if(inputstream != null)
                try
                {
                    dtd.read(new DataInputStream(new BufferedInputStream(inputstream)));
                    DTD.putDTDHash(s, dtd);
                }
                finally
                {
                    inputstream.close();
                }
        }
        catch(Exception exception)
        {
            System.out.println(exception);
        }
        return dtd;
    }

    public static synchronized String canonicalize(String s)
    {
        return canonicalizer.canonicalize(s);
    }

    public static void setCharset(String s)
    {
        HJBProperties hjbproperties = HJBProperties.getHJBProperties("beanPropertiesKey");
        String s1 = hjbproperties.getProperty("hotjava.charset");
        if(s1 == null || !s1.equals(s))
        {
            hjbproperties.put("hotjava.charset", s);
            hjbproperties.save();
        }
    }

    public static void registerFrame(Frame frame)
    {
        if(frame != null)
        {
            frm = frame;
            return;
        } else
        {
            frm = null;
            return;
        }
    }

    public static FontMetrics getFontMetrics(Font font)
    {
        if(frm != null)
            return frm.getFontMetrics(font);
        else
            return null;
    }

    public static Image createImage(int i, int j)
    {
        if(frm != null)
            return frm.createImage(i, j);
        else
            return null;
    }

    public static Frame getRegisteredFrame()
    {
        return frm;
    }

    public static Color getVisible3DColor(Color color)
    {
        HJColor hjcolor = new HJColor(color);
        HJColor hjcolor1 = (HJColor)hjcolor.brighter();
        if(hjcolor1.similar(hjcolor))
            return hjcolor.brighter(-0.4F);
        HJColor hjcolor2 = (HJColor)hjcolor.darker();
        if(hjcolor2.similar(hjcolor))
            return hjcolor.brighter(0.4F);
        else
            return hjcolor;
    }

    public static Color mapNamedColor(String s)
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

    public static FileNameMap getFileNameMap(URLConnection urlconnection)
    {
        Class class1 = urlconnection.getClass();
        Class aclass[] = new Class[0];
        Object aobj[] = new Object[0];
        try
        {
            Method method = class1.getMethod("getFileNameMap", aclass);
            return (FileNameMap)method.invoke(urlconnection, aobj);
        }
        catch(Exception _ex) { }
        try
        {
            Field field = class1.getField("fileNameMap");
            return (FileNameMap)field.get(urlconnection);
        }
        catch(Exception _ex)
        {
            return null;
        }
    }

    public static void tryToStopFollowRedirects(URLConnection urlconnection)
    {
        if(!(urlconnection instanceof HttpURLConnection))
            return;
        Class class1 = urlconnection.getClass();
        Class aclass[] = new Class[1];
        Object aobj[] = new Object[1];
        aclass[0] = Boolean.TYPE;
        aobj[0] = new Boolean(false);
        try
        {
            Method method = class1.getMethod("setInstanceFollowRedirects", aclass);
            method.invoke(urlconnection, aobj);
            return;
        }
        catch(Exception _ex)
        {
            System.out.println("Cookies with redirection probably won't work");
        }
    }

    public static RequestProcessor getInternalEventsQueue()
    {
        if(internalEventQueue == null)
            synchronized(sunw.hotjava.misc.Globals.class)
            {
                if(internalEventQueue == null)
                    internalEventQueue = new RequestProcessor("Internal events queue");
            }
        return internalEventQueue;
    }

    public static void setFrameExported()
    {
        frameExported = true;
    }

    public static synchronized void registerStaticStateWatcher(StaticStateWatcher staticstatewatcher)
    {
        synchronized(sunw.hotjava.misc.Globals.class)
        {
            Vector vector;
            if(staticStateWatchers == null)
                vector = new Vector();
            else
                vector = (Vector)staticStateWatchers.clone();
            vector.addElement(staticstatewatcher);
            staticStateWatchers = vector;
        }
    }

    public static void unregisterStaticStateWatcher(StaticStateWatcher staticstatewatcher)
    {
        synchronized(sunw.hotjava.misc.Globals.class)
        {
            Vector vector = staticStateWatchers;
            if(vector != null)
            {
                vector = (Vector)vector.clone();
                vector.removeElement(staticstatewatcher);
                staticStateWatchers = vector;
            }
        }
    }

    public static InputStream getBeanResourceAsStream(String s)
    {
        return (sunw.hotjava.misc.Globals.class).getResourceAsStream(s);
    }

    public static Image getImageFromBeanJar_DontUseThisMethod(String s)
    {
        s = s.replace(File.separatorChar, '/');
        if(imageBaseStr == null)
        {
            HJBProperties hjbproperties = HJBProperties.getHJBProperties("beanPropertiesKey");
            imageBaseStr = hjbproperties.getProperty("images.loc");
        }
        String s1;
        if(s.startsWith(imageBaseStr))
            s1 = s;
        else
            s1 = imageBaseStr + s;
        Object obj = getBeanResourceAsStream(s1);
        if(obj != null)
        {
            byte abyte0[] = new byte[8192];
            int i = 0;
            try
            {
                obj = new BufferedInputStream(((InputStream) (obj)));
                int j;
                while((j = ((InputStream) (obj)).read(abyte0, i, abyte0.length - i)) >= 0) 
                {
                    i += j;
                    if(i >= abyte0.length)
                    {
                        byte abyte1[] = new byte[abyte0.length * 2];
                        System.arraycopy(abyte0, 0, abyte1, 0, abyte0.length);
                        abyte0 = abyte1;
                    }
                }
                ((InputStream) (obj)).close();
                byte abyte2[] = new byte[i];
                System.arraycopy(abyte0, 0, abyte2, 0, i);
                return Toolkit.getDefaultToolkit().createImage(abyte2);
            }
            catch(IOException _ex)
            {
                return null;
            }
        } else
        {
            return null;
        }
    }

    public static void forEachWatcher(StaticStateWatcher.Action action)
    {
        Vector vector = staticStateWatchers;
        if(vector != null)
        {
            for(int i = 0; i < vector.size(); i++)
            {
                StaticStateWatcher staticstatewatcher = (StaticStateWatcher)vector.elementAt(i);
                action.doit(staticstatewatcher);
            }

        }
    }

    public static void registerTimeStamp(Object obj, String s)
    {
        if(timeStamps == null)
            timeStamps = new Vector();
        timeStamps.addElement(new TimeStampObject(obj, s));
    }

    public static void registerTimeStamp(TimeStampObject timestampobject)
    {
        if(timeStamps == null)
            timeStamps = new Vector();
        timeStamps.addElement(timestampobject);
    }

    public static void flushTimeStamps()
    {
        timeStamps.removeAllElements();
        timeStamps = null;
    }

    public static Vector getTimeStamps()
    {
        return timeStamps;
    }

    public static boolean isEmulatingNetscape()
    {
        return emulateNetscape;
    }

    public static void setEmulatingNetscape(boolean flag)
    {
        emulateNetscape = flag;
    }

    public static void printTimeStamps()
    {
        Vector vector = getTimeStamps();
        long l = ((TimeStampObject)vector.elementAt(0)).getTimeStamp();
        System.out.println("************************************");
        System.out.println("************************************");
        System.out.println("* From beginning\n*\n");
        for(int i = 0; i < vector.size(); i++)
            System.out.println(((TimeStampObject)vector.elementAt(i)).printElapsedTime(l));

        System.out.println("\n\n\n************************************");
        System.out.println("************************************");
        System.out.println("* From last time stamp \n* ");
        long l1 = ((TimeStampObject)vector.elementAt(0)).getTimeStamp();
        for(int j = 0; j < vector.size(); j++)
        {
            System.out.println(((TimeStampObject)vector.elementAt(j)).printElapsedTime(l1));
            l1 = ((TimeStampObject)vector.elementAt(j)).getTimeStamp();
        }

        System.out.println("\n\n\n");
    }

    public Globals()
    {
    }

    private static boolean emulateNetscape = true;
    public static final String beanProps = "beanPropertiesKey";
    private static boolean initSystemWasCalledVar;
    public static Frame frm;
    private static final double FACTOR = 0.65000000000000002D;
    private static QuitHandler quitHandler = null;
    private static Vector staticStateWatchers = null;
    private static Vector timeStamps = null;
    private static Object awtLock = null;
    private static boolean loadengine;
    private static ScriptingEngineInterface engine = null;
    private static final String scriptingEngineClass = "com.sun.javascript.ScriptingEngine";
    private static DTD defaultDTD;
    private static URLCanonicalizer canonicalizer = new URLCanonicalizer();
    private static String fileDialogDirectory;
    private static String fileDialogFile = "";
    private static RequestProcessor internalEventQueue = null;
    private static boolean frameExported;
    static String imageBaseStr = null;
    public static final boolean weAreTimeStamping = false;

}
