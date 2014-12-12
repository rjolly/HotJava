// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   XFileExtensionAccessor.java

package com.sun.nfs;

import com.sun.rpc.CredUnix;
import com.sun.xfile.XFile;
import java.io.IOException;
import java.net.UnknownHostException;

// Referenced classes of package com.sun.nfs:
//            Mount, NfsConnect, NfsURL, NfsHandler

public class XFileExtensionAccessor extends com.sun.xfile.XFileExtensionAccessor
{

    public XFileExtensionAccessor(XFile xfile)
    {
        super(xfile);
        if(!xfile.getFileSystemName().equals("nfs"))
        {
            throw new IllegalArgumentException("Invalid argument");
        } else
        {
            xf = xfile;
            return;
        }
    }

    public boolean loginPCNFSD(String s, String s1, String s2)
    {
        return NfsConnect.getCred().fetchCred(s, s1, s2);
    }

    public void logoutPCNFSD()
    {
        NfsConnect.getCred().setCred();
    }

    public void loginUGID(int i, int j, int ai[])
    {
        NfsConnect.getCred().setCred(i, j, ai);
    }

    public void logoutUGID()
    {
        NfsConnect.getCred().setCred();
    }

    public void setNfsHandler(NfsHandler nfshandler)
    {
        NfsConnect.setRpcHandler(nfshandler);
    }

    public String[] getExports()
        throws UnknownHostException, IOException
    {
        new Mount();
        return Mount.getExports((new NfsURL(xf.toString())).getHost());
    }

    XFile xf;
}
