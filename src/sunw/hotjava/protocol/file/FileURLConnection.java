// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   FileURLConnection.java

package sunw.hotjava.protocol.file;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import sun.io.ByteToCharConverter;
import sun.net.www.MessageHeader;
import sun.net.www.MimeEntry;
import sun.net.www.MimeTable;
import sun.net.www.URLConnection;
import sunw.hotjava.misc.Sort;

// Referenced classes of package sunw.hotjava.protocol.file:
//            StringCompare

public class FileURLConnection extends URLConnection
{

    FileURLConnection(URL url)
    {
        super(url);
    }

    public void connect()
        throws IOException
    {
        unitBytes = System.getProperty("file.dir.bytes", "bytes");
        unitKb = System.getProperty("file.dir.kb", "Kb");
        unitMb = System.getProperty("file.dir.mb", "Mb");
        MessageHeader messageheader = new MessageHeader();
        fn = decodeFileName(super.url.getFile());
        try
        {
            Class.forName("java.text.SimpleDateFormat");
            fmt = new SimpleDateFormat(System.getProperty("file.dir.datefmt", "hh:mm a dd, yyyy"));
        }
        catch(ClassNotFoundException _ex)
        {
            fmt = null;
        }
        iconHeight = Integer.getInteger("file.icon.height", 26).intValue();
        iconWidth = Integer.getInteger("file.icon.width", 24).intValue();
        mt = MimeTable.getDefaultTable();
        MimeEntry mimeentry = mt.findByFileName(fn);
        if(mimeentry != null)
            messageheader.add("content-type", mimeentry.getType());
        setProperties(messageheader);
        File file = new File(fn);
        String s = file.getCanonicalPath();
        String s1 = (new File(s)).getParent();
        lastModified = file.lastModified();
        if(file.isDirectory())
        {
            StringBuffer stringbuffer = new StringBuffer();
            String as[] = file.list();
            if(as == null)
                throw new FileNotFoundException(fn);
            StringCompare stringcompare = new StringCompare();
            Sort.quicksort(as, stringcompare);
            messageheader.add("content-type", "text/html");
            stringbuffer.append("<title>");
            stringbuffer.append(System.getProperty("file.dir.title", "Directory Listing"));
            stringbuffer.append("</title></head><body>\n");
            stringbuffer.append("<base href=\"");
            stringbuffer.append(super.url.toString().replace(File.separatorChar, '/'));
            if(stringbuffer.charAt(stringbuffer.length() - 1) != '/')
                stringbuffer.append("/");
            stringbuffer.append("\"><h1>");
            stringbuffer.append(fn.substring(fn.charAt(0) == File.separatorChar ? 0 : 1).replace('/', File.separatorChar));
            stringbuffer.append("</h1>\n");
            stringbuffer.append("<hr>\n");
            stringbuffer.append("<table border=0>");
            if(s1 != null)
                formatParentDirLine(System.getProperty("file.dir.parent", "Parent Directory"), s1, stringbuffer);
            for(int i = 0; i < as.length; i++)
                formatLine(as[i], as[i], stringbuffer);

            stringbuffer.append("</table></body></html>");
            is = new ByteArrayInputStream(stringbuffer.toString().getBytes());
        } else
        {
            if(file.exists())
            {
                messageheader.add("Content-length", String.valueOf(file.length()));
                messageheader.add("last-modified", (new Date(file.lastModified())).toString());
            }
            is = new BufferedInputStream(new FileInputStream(fn.replace('/', File.separatorChar)));
        }
        super.connected = true;
    }

    public synchronized InputStream getInputStream()
        throws IOException
    {
        if(!super.connected)
            connect();
        return is;
    }

    private void formatParentDirLine(String s, String s1, StringBuffer stringbuffer)
    {
        String s2 = super.url.toString();
        int i = s2.lastIndexOf('/', s2.length() - 2);
        int j = s2.lastIndexOf(File.separatorChar, s2.length() - 2);
        s2 = s2.substring(0, i <= j ? j + 1 : i + 1);
        Date date = new Date((new File(s1)).lastModified());
        String s3;
        if(fmt == null)
            s3 = date.toString();
        else
            s3 = fmt.format(date);
        stringbuffer.append("<tr><td> <img align=middle src=\"");
        stringbuffer.append("doc:/lib/images/ftp/directory.gif\" width=" + iconWidth + " height=" + iconHeight + ">\n");
        stringbuffer.append("</td><td><a href=\"");
        stringbuffer.append(s2);
        stringbuffer.append("\">");
        stringbuffer.append(s);
        stringbuffer.append("</a></td>");
        stringbuffer.append("<td align=\"right\"></td><td>dir</td><td>");
        stringbuffer.append(s3);
        stringbuffer.append("</td></tr>");
    }

    private void formatLine(String s, String s1, StringBuffer stringbuffer)
    {
        stringbuffer.append("<tr><td> <img align=middle src=\"");
        File file = new File(fn + "/" + s1);
        String s2;
        String s3;
        if(file.isDirectory())
        {
            stringbuffer.append("doc:/lib/images/ftp/directory.gif\" width=" + iconWidth + " height=" + iconHeight + ">\n");
            s2 = "";
            s3 = "dir";
        } else
        {
            long l = file.length();
            if(l < 1000L)
                s3 = unitBytes;
            else
            if(l < 0xf4240L)
            {
                l /= 1000L;
                s3 = unitKb;
            } else
            {
                l /= 0xf4240L;
                s3 = unitMb;
            }
            s2 = Long.toString(l);
            String s5 = "doc:/lib/images/ftp/file.gif";
            MimeEntry mimeentry = mt.findByFileName(s1);
            if(mimeentry != null)
            {
                String s6 = mimeentry.getImageFileName();
                if(s6 != null)
                    s5 = s6;
            }
            stringbuffer.append(s5);
            stringbuffer.append("\" width=" + iconWidth + " height=" + iconHeight + ">\n");
        }
        Date date = new Date(file.lastModified());
        String s4;
        if(fmt == null)
            s4 = date.toString();
        else
            s4 = fmt.format(date);
        stringbuffer.append("</td><td><a href=\"");
        stringbuffer.append(URLEncoder.encode(s1));
        stringbuffer.append("\">");
        stringbuffer.append(s);
        stringbuffer.append("</a></td>");
        stringbuffer.append("<td align=\"right\">");
        stringbuffer.append(s2);
        stringbuffer.append("</td><td>");
        stringbuffer.append(s3);
        stringbuffer.append("</td><td>");
        stringbuffer.append(s4);
        stringbuffer.append("</td></tr>");
    }

    private String decodeFileName(String s)
    {
        String s1 = System.getProperty("hotjava.charset");
        try
        {
            ByteToCharConverter.getConverter(s1);
        }
        catch(UnsupportedEncodingException _ex)
        {
            System.out.println("Charset " + s1 + " is not supported. Use 8859_1");
            s1 = "8859_1";
        }
        StringBuffer stringbuffer = new StringBuffer();
        int i = s.length();
        for(int j = 0; j < i; j++)
        {
            boolean flag = false;
            char c = s.charAt(j);
            if(c == '+')
            {
                c = ' ';
            } else
            {
                ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
                for(; c == '%'; c = s.charAt(j))
                {
                    flag = true;
                    String s2 = s.substring(j + 1, j + 1 + 2);
                    bytearrayoutputstream.write(Integer.parseInt(s2, 16));
                    if((j += 3) >= i)
                        break;
                }

                if(flag)
                    j--;
                try
                {
                    stringbuffer.append(new String(bytearrayoutputstream.toByteArray(), s1));
                }
                catch(Exception exception)
                {
                    exception.printStackTrace();
                }
            }
            if(!flag)
                stringbuffer.append(c);
        }

        return stringbuffer.toString();
    }

    public long getLastModified()
    {
        return lastModified;
    }

    private InputStream is;
    private int iconWidth;
    private int iconHeight;
    private String fn;
    private SimpleDateFormat fmt;
    private MimeTable mt;
    private String unitBytes;
    private String unitKb;
    private String unitMb;
    private long lastModified;
}
