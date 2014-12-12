// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   URLApplicationLauncher.java

package sunw.hotjava.misc;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import sun.net.www.ApplicationLaunchException;

// Referenced classes of package sunw.hotjava.misc:
//            StreamCopier

public class URLApplicationLauncher
    implements Runnable
{
    public static class ErrorEvent extends EventObject
    {

        public int getId()
        {
            return id;
        }

        int id;

        public ErrorEvent(Object obj, int i)
        {
            super(obj);
            id = i;
        }
    }

    public interface ErrorListener
        extends EventListener
    {

        public abstract void launcherErrorOccurred(ErrorEvent errorevent);
    }


    public URLApplicationLauncher(URL url1, String s)
    {
        this(url1, s, null);
    }

    public URLApplicationLauncher(URL url1, String s, String s1)
    {
        this(url1, s, s1, null);
    }

    public URLApplicationLauncher(URL url1, String s, String s1, String s2)
    {
        errorListeners = new Vector(1);
        url = url1;
        application = s;
        tempFile = s1;
        if(s2 == null)
        {
            tempFileTemplate = getTempFileTemplate();
            return;
        } else
        {
            tempFileTemplate = s2;
            return;
        }
    }

    public int getStatus()
    {
        return exitStatus;
    }

    public void start()
    {
        launcher = new Thread(this);
        launcher.start();
    }

    public void stop()
    {
        launcher.interrupt();
    }

    public URL getURL()
    {
        return url;
    }

    public String getApplication()
    {
        return application;
    }

    public String getTempFileName()
    {
        String s = tempFileTemplate;
        int i = s.lastIndexOf("%s");
        String s1 = s.substring(0, i);
        String s2 = "";
        if(i < s.length() - 2)
            s2 = s.substring(i + 2);
        long l = System.currentTimeMillis() / 1000L;
        for(int j = 0; (j = s1.indexOf("%s")) >= 0;)
            s1 = s1.substring(0, j) + l + s1.substring(j + 2);

        String s3 = getFileNameFromURL(url);
        String s4 = "";
        int k = s3.lastIndexOf('.');
        if(k >= 0)
        {
            s4 = s3.substring(k);
            s3 = s3.substring(0, k);
        }
        File file = new File(s3.replace('/', File.separatorChar));
        s3 = file.getName();
        s = s1 + s3 + l + s4 + s2;
        return s;
    }

    private String getFileNameFromURL(URL url1)
    {
        if(url1 != null)
        {
            String s = url1.getFile();
            if(s != null)
            {
                int i = s.indexOf('?');
                if(i >= 0)
                    s = s.substring(0, i);
                int j = s.lastIndexOf('/');
                if(j >= 0)
                    s = s.substring(j + 1);
                if(s.length() > 0)
                    return s;
            }
        }
        return null;
    }

    public String getExecutablePath(String s)
        throws ApplicationLaunchException
    {
        return getExecutablePath(s, null);
    }

    public String getExecutablePath(String s, String s1)
        throws ApplicationLaunchException
    {
        int i = s.indexOf(' ');
        String s2;
        if(i != -1)
            s2 = s.substring(0, i);
        else
            s2 = s;
        File file = new File(s2);
        if(file.isFile())
            return s;
        String s3 = System.getProperty("exec.path");
        if(s3 == null)
            throw new ApplicationLaunchException("No path to search for " + s);
        String s4 = null;
        for(StringTokenizer stringtokenizer = new StringTokenizer(s3, "|"); stringtokenizer.hasMoreElements();)
        {
            String s5 = (String)stringtokenizer.nextElement();
            String s6 = s5 + File.separator + s2;
            File file1 = new File(s6);
            if(file1.isFile())
            {
                s4 = s5 + File.separator + s;
                break;
            }
        }

        if(s4 == null)
            throw new ApplicationLaunchException("Not on exec.path: " + s);
        int j;
        if(s1 != null)
            while((j = s4.indexOf("%t")) >= 0) 
                s4 = s4.substring(0, j) + s1 + s4.substring(j + 2);
        return s4;
    }

    public synchronized void run()
    {
        if(tempFile == null)
            tempFile = getTempFileName();
        try
        {
            InputStream inputstream = null;
            BufferedOutputStream bufferedoutputstream = null;
            try
            {
                URLConnection urlconnection = url.openConnection();
                inputstream = urlconnection.getInputStream();
                bufferedoutputstream = new BufferedOutputStream(new FileOutputStream(tempFile));
            }
            catch(IOException ioexception1)
            {
                ioexception1.printStackTrace();
            }
            StreamCopier streamcopier = new StreamCopier(inputstream, bufferedoutputstream, null);
            streamcopier.transfer();
            String s = getExecutablePath(application) + " " + tempFile;
            Process process = Runtime.getRuntime().exec(s);
            if(showChildOutput)
            {
                InputStream inputstream1 = process.getErrorStream();
                int i;
                while((i = inputstream1.read()) != -1) 
                    System.out.print((char)i);
                inputstream1.close();
            }
            try
            {
                process.waitFor();
            }
            catch(InterruptedException _ex) { }
            exitStatus = process.exitValue();
            if(exitStatus != 0)
            {
                dispatchLauncherError(2003);
                return;
            }
        }
        catch(ApplicationLaunchException applicationlaunchexception)
        {
            System.err.println("URLApplicationLauncher run failed: " + applicationlaunchexception);
            dispatchLauncherError(2002);
        }
        catch(IOException ioexception)
        {
            System.err.println("URLApplicationLauncher run failed: " + ioexception);
            dispatchLauncherError(2002);
        }
        finally
        {
            try
            {
                if(tempFile != null)
                {
                    File file = new File(tempFile);
                    if(!Boolean.getBoolean("externalviewer.keep.tmpfile") && !file.delete())
                        System.err.println("Could not delete file: " + tempFile);
                }
            }
            catch(SecurityException securityexception)
            {
                System.err.println("Could not delete temp file: " + securityexception);
            }
        }
    }

    String getTempFileTemplate()
    {
        String s = null;
        String s1 = System.getProperty("user.home");
        if(s1 != null && !s1.endsWith(File.separator))
            s1 = s1 + File.separator;
        s1 = s1 + ".hotjava" + File.separator + "temp";
        File file = new File(s1);
        if(!file.exists() && !file.mkdirs())
        {
            s1 = System.getProperty("hotjava.home") + File.separator + "temp";
            File file1 = new File(s1);
            if(!file1.exists() && !file1.mkdirs())
                return "%s";
        }
        s = s1 + File.separator + "%s";
        return s;
    }

    public void addListener(ErrorListener errorlistener)
    {
        synchronized(errorListeners)
        {
            errorListeners.addElement(errorlistener);
        }
    }

    private void dispatchLauncherError(int i)
    {
        synchronized(errorListeners)
        {
            int j = errorListeners.size();
            if(j != 0)
            {
                ErrorEvent errorevent = new ErrorEvent(this, i);
                for(int k = 0; k < j; k++)
                {
                    ErrorListener errorlistener = (ErrorListener)errorListeners.elementAt(k);
                    errorlistener.launcherErrorOccurred(errorevent);
                }

            }
        }
    }

    private static final int ID_BASE = 2000;
    public static final int FILE_NOT_FOUND = 2001;
    public static final int FILE_NOT_EXECUTABLE = 2002;
    public static final int LAUNCH_FAILED = 2003;
    private static final boolean showChildOutput = Boolean.getBoolean("urlapplicationlauncher.showchildoutput");
    private URL url;
    private String application;
    private String tempFileTemplate;
    private String tempFile;
    private Thread launcher;
    private int exitStatus;
    private Vector errorListeners;

}
