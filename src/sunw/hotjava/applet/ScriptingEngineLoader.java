// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ScriptingEngineLoader.java

package sunw.hotjava.applet;

import java.net.URL;
import sun.applet.AppletClassLoader;

public class ScriptingEngineLoader extends AppletClassLoader
{

    private ScriptingEngineLoader(URL url)
    {
        super(url);
    }

    public static ScriptingEngineLoader createLoader(URL url)
    {
        if(singleton == null)
            singleton = new ScriptingEngineLoader(url);
        return singleton;
    }

    public static ScriptingEngineLoader getLoader()
    {
        return singleton;
    }

    private static ScriptingEngineLoader singleton = null;

}
