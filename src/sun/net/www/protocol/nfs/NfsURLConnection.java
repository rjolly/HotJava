// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   NfsURLConnection.java

package sun.net.www.protocol.nfs;

import com.sun.xfile.XFile;
import com.sun.xfile.XFileInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.URL;
import sun.misc.Sort;
import sun.net.www.MessageHeader;
import sun.net.www.MimeEntry;
import sun.net.www.MimeTable;
import sun.net.www.URLConnection;

// Referenced classes of package sun.net.www.protocol.nfs:
//            StringCompare

public class NfsURLConnection extends URLConnection
{

    NfsURLConnection(URL url)
    {
        super(url);
        isConnected = false;
    }

    public void connect()
        throws IOException
    {
        String s = super.url.getHost();
        try
        {
            InetAddress.getByName(s);
        }
        catch(Exception _ex)
        {
            throw new IOException("Unknown Host");
        }
        if(isConnected)
            return;
        try
        {
            nfsFile = new XFile(super.url.toString());
        }
        catch(Exception _ex)
        {
            throw new IOException("Unable to open xfile");
        }
        isConnected = true;
    }

    public InputStream getInputStream()
        throws IOException
    {
        boolean flag = false;
        if(!isConnected)
            connect();
        if(!nfsFile.exists())
            throw new IOException("Cannot Access File " + nfsFile.getPath() + "!");
        if(!nfsFile.canRead())
            throw new IOException("Read Access Not Allowed for " + nfsFile.getPath());
        String s = super.url.getFile();
        if(s.equals("/"))
        {
            s = "/.";
            flag = true;
        } else
        if(s.charAt(0) == '/')
            s = s.substring(1, s.length());
        MessageHeader messageheader = new MessageHeader();
        MimeTable mimetable = MimeTable.getDefaultTable();
        if(nfsFile.isDirectory())
        {
            StringBuffer stringbuffer = new StringBuffer();
            int i = Integer.getInteger("hotjava.file.iconheight", 32).intValue();
            int j = Integer.getInteger("hotjava.file.iconwidth", 32).intValue();
            messageheader.add("content-type", "text/html");
            setProperties(messageheader);
            stringbuffer.append("<HTML>\n<HEAD>\n<TITLE>");
            stringbuffer.append(System.getProperty("file.dir.title", "Directory Listing"));
            stringbuffer.append("</TITLE>\n");
            stringbuffer.append("<BASE HREF=\"" + super.url.toString());
            if(super.url.toString().endsWith("/"))
                stringbuffer.append("\">");
            else
                stringbuffer.append("/\">");
            stringbuffer.append("</HEAD>\n<BODY>\n");
            if(flag)
                stringbuffer.append("<H1>\n/</H1>\n<HR>\n");
            else
                stringbuffer.append("<H1>\n" + s + "</H1>\n<HR>\n");
            if(!flag)
            {
                String s1 = super.url.toString();
                int k = s1.length() - 1;
                if(super.url.getFile() != null)
                {
                    if(s1.endsWith("/"))
                        k--;
                    s1 = s1.substring(0, s1.lastIndexOf('/', k));
                    stringbuffer.append("<A HREF=\"" + s1 + "\">");
                    stringbuffer.append("<H2>Go To Parent Directory</H2></A>\n<BR>\n");
                }
            }
            String as[] = nfsFile.list();
            if(as != null)
            {
                StringCompare stringcompare = new StringCompare();
                Sort.quicksort(as, stringcompare);
                boolean flag1 = Boolean.getBoolean("file.hidedotfiles");
                for(int l = 0; l < as.length; l++)
                    if(!as[l].equals("..") && !as[l].equals(".") && (!flag1 || as[l].charAt(0) != '.'))
                    {
                        stringbuffer.append("<IMG ALIGN=middle SRC=\"");
                        XFile xfile = new XFile(nfsFile, as[l]);
                        if(xfile.isDirectory())
                            stringbuffer.append("doc:/lib/images/ftp/directory.gif\" WIDTH=" + j + " HEIGHT=" + i + ">\n");
                        else
                        if(xfile.isFile())
                        {
                            String s2 = "doc:/lib/images/ftp/file.gif";
                            MimeEntry mimeentry = mimetable.findByFileName(as[l]);
                            if(mimeentry != null)
                            {
                                String s3 = mimeentry.getImageFileName();
                                if(s3 != null)
                                    s2 = s3;
                            }
                            stringbuffer.append(s2);
                            stringbuffer.append("\" WIDTH=" + j + " HEIGHT=" + i + ">\n");
                        } else
                        {
                            stringbuffer.append("doc:/lib/images/ftp/file.gif\" WIDTH=" + j + " HEIGHT=" + i + ">\n");
                        }
                        stringbuffer.append("<A HREF=\"" + as[l] + "\">");
                        stringbuffer.append(as[l] + "</A>\n<BR>");
                    }

            }
            stringbuffer.append("</BODY>\n</HTML>\n");
            is = new ByteArrayInputStream(stringbuffer.toString().getBytes());
        } else
        {
            MimeEntry mimeentry1 = mimetable.findByFileName(s);
            if(mimeentry1 != null)
                messageheader.add("content-type", mimeentry1.getType());
            setProperties(messageheader);
            if(!nfsFile.exists())
                throw new IOException("Cannot Access File " + nfsFile.getPath() + "!");
            is = new XFileInputStream(nfsFile);
            if(is == null)
                throw new IOException("Unable to Open InputStream for " + super.url.getFile());
        }
        return is;
    }

    InputStream is;
    XFileInputStream nis;
    XFile nfsFile;
    boolean isConnected;
}
