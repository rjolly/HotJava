// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   TimeStampObject.java

package sunw.hotjava.misc;


public class TimeStampObject
{

    public TimeStampObject(Object obj, String s)
    {
        id = obj.hashCode();
        posMarker = s;
        timeStamp = System.currentTimeMillis();
    }

    public String getPosition()
    {
        return posMarker;
    }

    public long getTimeStamp()
    {
        return timeStamp;
    }

    public String printElapsedTime(long l)
    {
        return "<TimeStamp id = " + Long.toHexString(id) + " position = \"" + posMarker + "\" time = " + (timeStamp - l) + " />";
    }

    public String toString()
    {
        return "<TimeStamp id = " + Long.toHexString(id) + " position = \"" + posMarker + "\" time = " + timeStamp + " />";
    }

    String posMarker;
    long id;
    long timeStamp;
}
