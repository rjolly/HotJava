// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ScriptingEngineInterface.java

package sunw.hotjava.script;

import java.net.URL;
import sunw.hotjava.doc.DocumentFormatter;

// Referenced classes of package sunw.hotjava.script:
//            ScriptContextInterface, ScriptSecuritySupportInterface

public interface ScriptingEngineInterface
{

    public abstract ScriptContextInterface getContext(DocumentFormatter documentformatter);

    public abstract void registerListeners(DocumentFormatter documentformatter);

    public abstract void unregisterListeners(DocumentFormatter documentformatter);

    public abstract String evaluateString(ScriptContextInterface scriptcontextinterface, ScriptSecuritySupportInterface scriptsecuritysupportinterface, String s, String s1, int i, String s2);

    public abstract URL getCodebaseFromCaller();

    public abstract boolean isLanguageSupported(String s);

    public abstract Object preserveState(DocumentFormatter documentformatter);

    public abstract void unpreserveState(DocumentFormatter documentformatter, Object obj);
}
