// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ClockFormat.java

package sunw.hotjava.misc;

import java.util.Locale;

// Referenced classes of package sunw.hotjava.misc:
//            HJBProperties

public class ClockFormat
{

    public static String getFormat(String s)
    {
        String s1 = Locale.getDefault().getLanguage();
        if(s1.equalsIgnoreCase("zh") || s1.equalsIgnoreCase("ja") || s1.equalsIgnoreCase("ko"))
        {
            if(s.indexOf("h:mm a") > -1)
                s = s.substring(0, s.indexOf("h:mm a")) + "a hmm" + s.substring(s.indexOf("h:mm a") + "h:mm a".length());
            if(s.indexOf("HH:mm") > -1)
                s = s.substring(0, s.indexOf("HH:mm")) + "HHmm" + s.substring(s.indexOf("HH:mm") + "HH:mm".length());
            if(s.indexOf("MMM") > -1)
                s = s.substring(0, s.indexOf('M') + 1) + s.substring(s.lastIndexOf('M'));
        }
        StringBuffer stringbuffer = new StringBuffer(s);
        int i = stringbuffer.toString().lastIndexOf('y');
        if(i > 0 && (i + 1 == stringbuffer.length() || stringbuffer.charAt(i + 1) != '\''))
            if(i + 1 == stringbuffer.length())
                stringbuffer.append(props.getProperty("hotjava.clock.format.year.text", ""));
            else
                stringbuffer.insert(i + 1, props.getProperty("hotjava.clock.format.year.text", ""));
        i = stringbuffer.toString().lastIndexOf('M');
        if(i > 0 && (i + 1 == stringbuffer.length() || stringbuffer.charAt(i + 1) != '\''))
            if(i + 1 == stringbuffer.length())
                stringbuffer.append(props.getProperty("hotjava.clock.format.month.text", ""));
            else
                stringbuffer.insert(i + 1, props.getProperty("hotjava.clock.format.month.text", ""));
        i = stringbuffer.toString().lastIndexOf('d');
        if(i > 0 && (i + 1 == stringbuffer.length() || stringbuffer.charAt(i + 1) != '\''))
            if(i + 1 == stringbuffer.length())
                stringbuffer.append(props.getProperty("hotjava.clock.format.day.text", ""));
            else
                stringbuffer.insert(i + 1, props.getProperty("hotjava.clock.format.day.text", ""));
        i = stringbuffer.toString().lastIndexOf('h');
        if(i < 0)
            i = stringbuffer.toString().lastIndexOf('H');
        if(i > 0 && (i + 1 == stringbuffer.length() || stringbuffer.charAt(i + 1) != '\''))
            if(i + 1 == stringbuffer.length())
                stringbuffer.append(props.getProperty("hotjava.clock.format.hour.text", ""));
            else
                stringbuffer.insert(i + 1, props.getProperty("hotjava.clock.format.hour.text", ""));
        i = stringbuffer.toString().lastIndexOf('m');
        if(i > 0 && (i + 1 == stringbuffer.length() || stringbuffer.charAt(i + 1) != '\''))
            if(i + 1 == stringbuffer.length())
                stringbuffer.append(props.getProperty("hotjava.clock.format.minute.text", ""));
            else
                stringbuffer.insert(i + 1, props.getProperty("hotjava.clock.format.minute.text", ""));
        i = stringbuffer.toString().lastIndexOf('s');
        if(i > 0 && (i + 1 == stringbuffer.length() || stringbuffer.charAt(i + 1) != '\''))
            if(i + 1 == stringbuffer.length())
                stringbuffer.append(props.getProperty("hotjava.clock.format.second.text", ""));
            else
                stringbuffer.insert(i + 1, props.getProperty("hotjava.clock.format.second.text", ""));
        s = stringbuffer.toString();
        return s;
    }

    public ClockFormat()
    {
    }

    private static HJBProperties props = HJBProperties.getHJBProperties("hjbrowser");

}
