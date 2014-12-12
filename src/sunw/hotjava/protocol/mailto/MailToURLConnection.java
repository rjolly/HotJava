// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   MailToURLConnection.java

package sunw.hotjava.protocol.mailto;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;
import sun.net.www.MessageHeader;
import sun.net.www.URLConnection;
import sunw.hotjava.misc.CharacterEncoding;

public class MailToURLConnection extends URLConnection
{

    MailToURLConnection(URL url)
    {
        super(url);
        MessageHeader messageheader = new MessageHeader();
        messageheader.add("content-type", "text/html");
        setProperties(messageheader);
    }

    public void connect()
        throws IOException
    {
    }

    public synchronized OutputStream getOutputStream()
        throws IOException
    {
        throw new IOException("Using mailto output stream: not implemented");
    }

    public synchronized InputStream getInputStream()
        throws IOException
    {
        if(is != null)
            return is;
        String s = super.url.getFile();
        Hashtable hashtable = new Hashtable(3);
        int i = s.indexOf('?');
        if(i >= 0)
        {
            String s1 = s.substring(i + 1);
            s = s.substring(0, i);
            for(StringTokenizer stringtokenizer = new StringTokenizer(s1, "&"); stringtokenizer.hasMoreTokens();)
            {
                String s3 = stringtokenizer.nextToken();
                int j = s3.indexOf('=');
                if(j >= 0)
                {
                    String s4 = s3.substring(0, j);
                    String s5 = s3.substring(j + 1);
                    hashtable.put(s4, s5);
                }
            }

        }
        hashtable.put("to", s);
        hashtable.put("url", super.url.toExternalForm());
        String s2 = getTemplate();
        StringBuffer stringbuffer = substituteFields(s2, hashtable);
        if(charset != null)
            is = new ByteArrayInputStream(stringbuffer.toString().getBytes(charset));
        else
            is = new ByteArrayInputStream(stringbuffer.toString().getBytes());
        return is;
    }

    private void addFields(StringBuffer stringbuffer, Hashtable hashtable)
    {
        for(Enumeration enumeration = hashtable.keys(); enumeration.hasMoreElements(); stringbuffer.append("\">\n"))
        {
            String s = (String)enumeration.nextElement();
            String s1 = (String)hashtable.get(s);
            stringbuffer.append("<param name=");
            stringbuffer.append(s);
            stringbuffer.append(" value=\"");
            stringbuffer.append(s1);
        }

    }

    private String getTemplate()
    {
        String s = null;
        try
        {
            URL url = new URL("doc:/lib/templates/mailto.html");
            InputStream inputstream = url.openConnection().getInputStream();
            byte abyte0[] = new byte[1024];
            int i = 0;
            ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
            while((i = inputstream.read(abyte0)) > 0) 
                bytearrayoutputstream.write(abyte0, 0, i);
            s = new String(bytearrayoutputstream.toByteArray());
            int j = 0;
            if((j = s.indexOf("CHARSET=")) > 0)
            {
                charset = s.substring(j + 8, s.indexOf("\"", j));
                if(CharacterEncoding.getNetworkName(charset) != null)
                    charset = CharacterEncoding.getNetworkName(charset);
                s = new String(bytearrayoutputstream.toByteArray(), charset);
            }
        }
        catch(Exception exception)
        {
            exception.printStackTrace();
        }
        return s;
    }

    private StringBuffer substituteFields(String s, Hashtable hashtable)
    {
        StringBuffer stringbuffer = new StringBuffer(1200);
        int k;
        for(int i = 0; i < s.length(); i = k + 1)
        {
            int j = s.indexOf('&', i);
            if(j == -1)
            {
                stringbuffer.append(s.substring(i));
                break;
            }
            k = s.indexOf(';', j);
            if(k == -1)
            {
                stringbuffer.append(s.substring(i));
                break;
            }
            stringbuffer.append(s.substring(i, j));
            String s1 = s.substring(j + 1, k);
            if(s1.equals("params"))
            {
                addFields(stringbuffer, hashtable);
            } else
            {
                String s2 = (String)hashtable.get(s1);
                if(s2 == null)
                {
                    stringbuffer.append('&');
                    stringbuffer.append(s1);
                    stringbuffer.append(';');
                } else
                {
                    stringbuffer.append(s2);
                }
            }
        }

        return stringbuffer;
    }

    InputStream is;
    String charset;
}
