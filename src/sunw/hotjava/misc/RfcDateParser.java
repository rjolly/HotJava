// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   RfcDateParser.java

package sunw.hotjava.misc;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class RfcDateParser
{

    public RfcDateParser(String s)
    {
        isGMT = false;
        dateString = s.trim();
        if(dateString.indexOf("GMT") != -1)
            isGMT = true;
        try
        {
            Class.forName("java.text.SimpleDateFormat");
            usingJDK = true;
            return;
        }
        catch(ClassNotFoundException _ex)
        {
            return;
        }
    }

    public Date getDate()
    {
        if(usingJDK)
        {
            int i = isGMT ? gmtStandardFormats.length : standardFormats.length;
            for(int j = 0; j < i; j++)
            {
                Date date = null;
                if(isGMT)
                    date = tryParsing(gmtStandardFormats[j]);
                else
                    date = tryParsing(standardFormats[j]);
                if(date != null)
                    return date;
            }

            return null;
        } else
        {
            return parseNoJDKDate();
        }
    }

    private Date tryParsing(String s)
    {
        SimpleDateFormat simpledateformat = new SimpleDateFormat(s, Locale.US);
        if(isGMT)
            simpledateformat.setTimeZone(TimeZone.getTimeZone("GMT"));
        try
        {
            return simpledateformat.parse(dateString);
        }
        catch(Exception _ex)
        {
            return null;
        }
    }

    private Date parseNoJDKDate()
    {
        Date date = null;
        try
        {
            date = new Date(dateString);
        }
        catch(Exception _ex) { }
        if(date == null)
        {
            String s = new String();
            StringTokenizer stringtokenizer = new StringTokenizer(dateString, " ");
            if(stringtokenizer.countTokens() >= 3)
            {
                s = s.concat(stringtokenizer.nextToken());
                String s1 = stringtokenizer.nextToken();
                int i = s1.lastIndexOf('-');
                String s2 = s1.substring(0, i);
                if(i >= 0)
                {
                    String s3 = s1.substring(i + 1);
                    if(s3.length() == 2)
                        try
                        {
                            int j = Integer.parseInt(s3);
                            if(j < 70)
                            {
                                j += 2000;
                                String s4 = Integer.toString(j);
                                s = s.concat(" " + s2 + "-" + s4);
                            } else
                            {
                                return null;
                            }
                        }
                        catch(Exception _ex)
                        {
                            return null;
                        }
                    else
                        return null;
                } else
                {
                    return null;
                }
                while(stringtokenizer.hasMoreTokens()) 
                    s = s.concat(" " + stringtokenizer.nextToken());
                try
                {
                    date = new Date(s);
                }
                catch(Exception _ex) { }
            } else
            {
                return null;
            }
        }
        return date;
    }

    private static final String debugProp = "hotjava.debug.RfcDateParser";
    private boolean isGMT;
    private static boolean usingJDK;
    static final String standardFormats[] = {
        "EEEE', 'dd-MMM-yy HH:mm:ss z", "EEEE', 'dd-MMM-yy HH:mm:ss", "EEE', 'dd-MMM-yyyy HH:mm:ss z", "EEE', 'dd MMM yyyy HH:mm:ss z", "EEEE', 'dd MMM yyyy HH:mm:ss z", "EEE', 'dd MMM yyyy hh:mm:ss z", "EEEE', 'dd MMM yyyy hh:mm:ss z", "EEE MMM dd HH:mm:ss z yyyy", "EEE MMM dd HH:mm:ss yyyy", "EEE', 'dd-MMM-yy HH:mm:ss", 
        "EEE', 'dd-MMM-yyyy HH:mm:ss"
    };
    static final String gmtStandardFormats[] = {
        "EEEE',' dd-MMM-yy HH:mm:ss 'GMT'", "EEE',' dd-MMM-yyyy HH:mm:ss 'GMT'", "EEE',' dd MMM yyyy HH:mm:ss 'GMT'", "EEEE',' dd MMM yyyy HH:mm:ss 'GMT'", "EEE',' dd MMM yyyy hh:mm:ss 'GMT'", "EEEE',' dd MMM yyyy hh:mm:ss 'GMT'", "EEE MMM dd HH:mm:ss 'GMT' yyyy"
    };
    String dateString;

}
