// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   CurrentDocument.java

package sunw.hotjava.bean;

import java.io.Reader;
import java.io.Serializable;
import java.net.URL;

public class CurrentDocument
    implements Serializable
{

    public CurrentDocument()
    {
    }

    static final long serialVersionUID = 0xc0fc71691f8ab702L;
    public String documentString;
    public String frameName;
    public URL documentURL;
    public String documentTitle;
    public Reader documentSource;
    public boolean externalHint;
}
