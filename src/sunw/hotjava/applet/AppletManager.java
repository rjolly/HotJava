// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   AppletManager.java

package sunw.hotjava.applet;

import java.applet.Applet;
import java.io.IOException;
import java.net.URL;

public interface AppletManager
{

    public abstract ClassLoader refClassLoader(URL url, String as[], Object obj);

    public abstract Integer deRefClassLoader(URL url, boolean flag);

    public abstract void destroyApplets(int i);

    public abstract ThreadGroup getThreadGroup(URL url);

    public abstract void removeThreadGroup(URL url);

    public abstract Applet createApplet(ClassLoader classloader, String s, String s1)
        throws ClassNotFoundException, IllegalAccessException, IOException, InstantiationException, InterruptedException;

    public abstract Class createScriptClass(ClassLoader classloader, String s, byte abyte0[]);

    public abstract boolean checkFrameAccess();
}
