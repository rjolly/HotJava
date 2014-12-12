// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ExtPermissions.java

package sunw.hotjava.security;

import java.net.InetAddress;
import sunw.hotjava.misc.Set;

public class ExtPermissions
{

    void allowRead(String s)
    {
        if(readAccess == null)
            readAccess = new Set();
        readAccess.add(s);
    }

    void allowAllRead()
    {
        readAccess = null;
        readAllFiles = true;
    }

    boolean readOK(String s)
    {
        if(readAllFiles)
            return true;
        return readAccess != null && readAccess.isMember(s);
    }

    void allowDelete(String s)
    {
        if(deleteAccess == null)
            deleteAccess = new Set();
        deleteAccess.add(s);
    }

    void allowAllDelete()
    {
        deleteAccess = null;
        deleteAllFiles = true;
    }

    boolean deleteOK(String s)
    {
        if(deleteAllFiles)
            return true;
        return deleteAccess != null && deleteAccess.isMember(s);
    }

    void allowWrite(String s)
    {
        if(writeAccess == null)
            writeAccess = new Set();
        writeAccess.add(s);
    }

    void allowAllWrite()
    {
        writeAccess = null;
        writeAllFiles = true;
    }

    boolean writeOK(String s)
    {
        if(writeAllFiles)
            return true;
        return writeAccess != null && writeAccess.isMember(s);
    }

    boolean execOK()
    {
        return allowExec;
    }

    void allowAllExec()
    {
        allowExec = true;
    }

    void allowProp(String s)
    {
        if(propertiesAccess == null)
            propertiesAccess = new Set();
        propertiesAccess.add(s);
    }

    void allowAllProps()
    {
        propertiesAll = true;
        propertiesAccess = null;
    }

    boolean propOK(String s)
    {
        if(propertiesAll)
            return true;
        return propertiesAccess != null && propertiesAccess.isMember(s);
    }

    boolean listenOK(int i)
    {
        if(listenAll)
            return true;
        return netListen != null && netListen.isMember(new Integer(i));
    }

    void allowListen(int i)
    {
        if(netListen == null)
            netListen = new Set();
        netListen.add(new Integer(i));
    }

    void allowAllListen()
    {
        listenAll = true;
        netListen = null;
    }

    private String hostPortString(String s, int i)
    {
        return s + ":" + i;
    }

    boolean connectOK(String s, int i)
    {
        String s1 = hostPortString(s, i);
        if(connectAll)
            return true;
        return netConnect != null && netConnect.isMember(s1);
    }

    boolean multicastOK(InetAddress inetaddress)
    {
        return connectAll;
    }

    void allowConnect(String s, int i)
    {
        String s1 = hostPortString(s, i);
        if(netConnect == null)
            netConnect = new Set();
        netConnect.add(s1);
    }

    void allowAllConnect()
    {
        connectAll = true;
        netConnect = null;
    }

    boolean acceptOK(String s, int i)
    {
        String s1 = hostPortString(s, i);
        if(acceptAll)
            return true;
        return netAccept != null && netAccept.isMember(s1);
    }

    void allowAccept(String s, int i)
    {
        String s1 = hostPortString(s, i);
        if(netAccept == null)
            netAccept = new Set();
        netAccept.add(s1);
    }

    void allowAllAccept()
    {
        netAccept = null;
        acceptAll = true;
    }

    public ExtPermissions()
    {
        allowExec = false;
        readAllFiles = false;
        writeAllFiles = false;
        connectAll = false;
        acceptAll = false;
        listenAll = false;
        propertiesAll = false;
        deleteAllFiles = false;
    }

    private boolean allowExec;
    private boolean readAllFiles;
    private Set readAccess;
    private boolean writeAllFiles;
    private Set writeAccess;
    private boolean connectAll;
    private Set netConnect;
    private boolean acceptAll;
    private Set netAccept;
    private boolean listenAll;
    private Set netListen;
    private boolean propertiesAll;
    private Set propertiesAccess;
    private boolean deleteAllFiles;
    private Set deleteAccess;
}
