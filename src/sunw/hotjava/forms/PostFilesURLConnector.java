// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   PostFilesURLConnector.java

package sunw.hotjava.forms;

import java.io.*;
import java.net.*;
import java.util.Hashtable;
import sunw.hotjava.bean.CookieJarInterface;
import sunw.hotjava.bean.HotJavaBrowserBean;
import sunw.hotjava.misc.Globals;
import sunw.hotjava.misc.URLConnector;

public class PostFilesURLConnector
    implements URLConnector
{

    public PostFilesURLConnector(URL url, String s, Hashtable hashtable)
    {
        referer = url;
        formData = s;
        formFiles = hashtable;
    }

    public URLConnection openConnection(URL url)
        throws IOException
    {
        CookieJarInterface cookiejarinterface = HotJavaBrowserBean.getCookiesManager();
        URLConnection urlconnection = url.openConnection();
        if(cookiejarinterface != null)
            cookiejarinterface.applyRelevantCookies(url, urlconnection);
        String s = "-----------------------------" + System.currentTimeMillis();
        urlconnection.setRequestProperty("Content-type", "multipart/form-data");
        urlconnection.setRequestProperty("boundary", s);
        urlconnection.setDoOutput(true);
        if(referer != null)
            urlconnection.setRequestProperty("Referer", referer.toExternalForm());
        PrintWriter printwriter = null;
        try
        {
            printwriter = new PrintWriter(new OutputStreamWriter(urlconnection.getOutputStream()));
            for(boolean flag = false; !flag;)
            {
                int i = formData.indexOf("&");
                String s1;
                if(i == -1)
                {
                    s1 = formData;
                    flag = true;
                } else
                {
                    s1 = formData.substring(0, i);
                }
                int j = s1.indexOf("=");
                String s2 = s1.substring(0, j);
                String s3 = s1.substring(j + 1);
                printwriter.println(s);
                File file = (File)formFiles.get(s2);
                if(file == null)
                {
                    printwriter.println("Content-Disposition: form-data; name=\"" + s2 + "\"");
                    printwriter.println("");
                    printwriter.println(s3);
                } else
                {
                    String s4 = file.getName();
                    FileNameMap filenamemap = Globals.getFileNameMap(urlconnection);
                    String s5 = filenamemap.getContentTypeFor(s4);
                    if(s5 == null)
                        s5 = "content/unknown";
                    printwriter.print("Content-Disposition: form-data; name=\"" + s2 + "\"; ");
                    printwriter.println("filename=\"" + s4 + "\"");
                    printwriter.println("Content-Type: " + s5);
                    printwriter.println("");
                    writeFileToPrintWriter(file, printwriter);
                    printwriter.println("");
                }
                formData = formData.substring(i + 1);
            }

            printwriter.println(s + "--");
            printwriter.flush();
        }
        finally
        {
            if(printwriter != null)
                printwriter.close();
        }
        return urlconnection;
    }

    private void writeFileToPrintWriter(File file, PrintWriter printwriter)
        throws FileNotFoundException, IOException
    {
        BufferedReader bufferedreader = new BufferedReader(new FileReader(file));
        char ac[] = new char[500];
        for(int i = bufferedreader.read(ac, 0, 500); i != -1; i = bufferedreader.read(ac, 0, 500))
            printwriter.write(ac, 0, i);

    }

    private URL referer;
    private String formData;
    private Hashtable formFiles;
}
