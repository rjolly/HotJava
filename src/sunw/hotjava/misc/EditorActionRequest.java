// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   EditorActionRequest.java

package sunw.hotjava.misc;


public abstract class EditorActionRequest extends RequestProcessor.Request
{

    public void init(Object obj)
    {
        actor = obj;
    }

    public void init(Object obj, Object obj1)
    {
        actor = obj;
        datum = obj1;
    }

    public Object getActor()
    {
        return actor;
    }

    public void setActor(Object obj)
    {
        actor = obj;
    }

    public Object getDatum()
    {
        return datum;
    }

    public void setDatum(Object obj)
    {
        datum = obj;
    }

    public EditorActionRequest()
    {
    }

    Object actor;
    Object datum;
}
