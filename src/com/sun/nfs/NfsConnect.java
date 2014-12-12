// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   NfsConnect.java

package com.sun.nfs;

import com.sun.rpc.*;
import java.io.IOException;
import java.io.PrintStream;
import java.net.UnknownHostException;
import java.util.Hashtable;

// Referenced classes of package com.sun.nfs:
//            Mount, Nfs, Nfs2, Nfs3, 
//            NfsException, NfsURL

public class NfsConnect
{

    NfsConnect(String s, int i, int j, String s1, boolean flag)
    {
        server = s;
        port = i;
        version = j;
        proto = s1;
        pub = flag;
    }

    static Nfs connect(String s)
        throws IOException
    {
        NfsURL nfsurl = new NfsURL(s);
        String s1 = nfsurl.getHost();
        int i = nfsurl.getPort();
        String s2 = nfsurl.getFile();
        int j = nfsurl.getVersion();
        String s3 = nfsurl.getProto();
        boolean flag = nfsurl.getPub();
        NfsConnect nfsconnect = null;
        if(j == 0 && s3 == null && flag)
            nfsconnect = cache_get(s1);
        if(nfsconnect == null)
            return connect(s1, s2, i, j, s3, flag);
        else
            return connect(s1, s2, nfsconnect.port, nfsconnect.version, nfsconnect.proto, nfsconnect.pub);
    }

    static Nfs connect(String s, int i, String s1)
        throws IOException
    {
        NfsConnect nfsconnect = cache_get(s);
        if(nfsconnect == null)
            return connect(s, s1, i, 0, null, true);
        else
            return connect(s, s1, nfsconnect.port, nfsconnect.version, nfsconnect.proto, nfsconnect.pub);
    }

    static Nfs connect(String s, String s1, int i, int j, String s2, boolean flag)
        throws IOException
    {
        if(i == 0)
            i = 2049;
        if(s1 == null || s1.length() == 0)
            s1 = ".";
        Nfs nfs = Nfs.cache_get(s, s1);
        if(nfs != null)
        {
            nfs.getattr();
            if(nfs.isSymlink())
                return followLink(nfs);
            else
                return nfs;
        }
        Object obj;
        if(s2 == null)
        {
            obj = Connection.getCache(s, i, "tcp");
            if(obj == null)
                obj = Connection.getCache(s, i, "udp");
            if(obj == null)
                try
                {
                    obj = new ConnectSocket(s, i, 32968);
                    Connection.putCache(((Connection) (obj)));
                    s2 = "tcp";
                }
                catch(UnknownHostException unknownhostexception)
                {
                    throw unknownhostexception;
                }
                catch(IOException _ex)
                {
                    obj = new ConnectDatagram(s, i, 32968);
                    Connection.putCache(((Connection) (obj)));
                    s2 = "udp";
                }
        } else
        if(s2.equals("tcp"))
        {
            obj = Connection.getCache(s, i, "tcp");
            if(obj == null)
            {
                obj = new ConnectSocket(s, i, 32968);
                Connection.putCache(((Connection) (obj)));
            }
        } else
        if(s2.equals("udp"))
        {
            obj = Connection.getCache(s, i, "udp");
            if(obj == null)
            {
                obj = new ConnectDatagram(s, i, 32968);
                Connection.putCache(((Connection) (obj)));
            }
        } else
        {
            throw new IOException("Unknown protocol: " + s2);
        }
        if(flag)
        {
            try
            {
                switch(j)
                {
                case 0: // '\0'
                    try
                    {
                        nfs = tryNfs(((Connection) (obj)), pubfh3, s1, 3, false);
                        j = 3;
                    }
                    catch(MsgAcceptedException msgacceptedexception)
                    {
                        if(((RpcException) (msgacceptedexception)).error != 2)
                            throw msgacceptedexception;
                        j = 2;
                        nfs = tryNfs(((Connection) (obj)), pubfh2, s1, 2, false);
                    }
                    break;

                case 2: // '\002'
                    nfs = tryNfs(((Connection) (obj)), pubfh2, s1, 2, false);
                    break;

                case 3: // '\003'
                    nfs = tryNfs(((Connection) (obj)), pubfh3, s1, 3, false);
                    break;
                }
            }
            catch(MsgAcceptedException msgacceptedexception1)
            {
                if(((RpcException) (msgacceptedexception1)).error != 4)
                    throw msgacceptedexception1;
                if(j == 0)
                    j = 3;
            }
            catch(NfsException nfsexception)
            {
                if(nfsexception.error != 70 && nfsexception.error != 10001)
                    throw nfsexception;
                if(j == 0)
                    j = 3;
            }
            if(nfs != null)
            {
                cache_put(new NfsConnect(s, i, j, s2, true));
                return nfs;
            }
        }
        if(s1.equals("."))
            s1 = "/";
        byte abyte0[] = (new Mount()).getFH(s, s1, j);
        cache_put(new NfsConnect(s, i, j, s2, false));
        return tryNfs(((Connection) (obj)), abyte0, s1, j, true);
    }

    private static Nfs tryNfs(Connection connection, byte abyte0[], String s, int i, boolean flag)
        throws IOException
    {
        Rpc rpc = new Rpc(connection, 0x186a3, i);
        rpc.setCred(cred);
        rpc.setRpcHandler(rhandler);
        Object obj;
        if(i == 2)
            obj = new Nfs2(rpc, abyte0, s, null);
        else
            obj = new Nfs3(rpc, abyte0, s, null);
        if(s.equals("/."))
            return ((Nfs) (obj));
        if(flag)
        {
            ((Nfs) (obj)).getattr();
            Nfs.cache_put(((Nfs) (obj)));
            return ((Nfs) (obj));
        } else
        {
            return ((Nfs) (obj)).lookup(null);
        }
    }

    static Nfs followLink(Nfs nfs)
        throws IOException
    {
        String s = nfs.name;
        String s1 = nfs.readlink();
        String s2 = nfs.rpc.conn.server;
        int i = nfs.rpc.conn.port;
        String s3;
        if(s1.startsWith("nfs://"))
        {
            NfsURL nfsurl = new NfsURL(s1);
            s2 = nfsurl.getHost();
            i = nfsurl.getPort();
            s3 = nfsurl.getFile();
        } else
        if(s1.startsWith("/"))
        {
            s3 = s1;
        } else
        {
            int j = 0;
            int k = s.lastIndexOf('/');
            int l;
            for(l = s1.length(); s1.regionMatches(j, "..", 0, l - j) || s1.startsWith("../", j);)
            {
                if((j += 3) >= l)
                    break;
                k = s.lastIndexOf('/', k - 1);
            }

            if(j > l)
                j = l;
            if(k < 0)
                k = 0;
            else
                k++;
            s3 = s.substring(0, k) + s1.substring(j);
        }
        try
        {
            return connect(s2, i, s3);
        }
        catch(IOException ioexception)
        {
            System.err.println(ioexception + ": symbolic link: " + s + " -> " + s1);
        }
        return nfs;
    }

    private static void cache_put(NfsConnect nfsconnect)
    {
        cacheNfsConnect.put(nfsconnect.server, nfsconnect);
    }

    private static NfsConnect cache_get(String s)
    {
        return (NfsConnect)cacheNfsConnect.get(s);
    }

    public static CredUnix getCred()
    {
        return cred;
    }

    public static void setRpcHandler(RpcHandler rpchandler)
    {
        rhandler = rpchandler;
    }

    private static byte pubfh2[] = new byte[32];
    private static byte pubfh3[] = new byte[0];
    private static Hashtable cacheNfsConnect = new Hashtable();
    static final int NFS_PORT = 2049;
    static final int NFS_PROG = 0x186a3;
    static final int MAXBUF = 32968;
    String server;
    int port;
    int version;
    String proto;
    boolean pub;
    static CredUnix cred = new CredUnix();
    static RpcHandler rhandler;

}
