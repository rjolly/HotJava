// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   PolicyScript.java

package sunw.hotjava.security;


// Referenced classes of package sunw.hotjava.security:
//            PolicyHigh, BasePolicy, CommonSecurity

public class PolicyScript extends PolicyHigh
{

    public static PolicyScript getPolicyScript()
    {
        if(singleton == null)
            singleton = new PolicyScript();
        return singleton;
    }

    protected PolicyScript()
    {
    }

    public boolean checkConnect(String s, int i)
    {
        if(getCSM().policyClassDepth("com.sun.javascript.ObjectType") == 5 && getCSM().policyClassDepth("java.net.InetAddress") == 3)
            return true;
        int j = getCSM().policyClassDepth("sunw.hotjava.applet.AppletImageRef");
        if(j == 4 || j == 5)
            return true;
        if(getCSM().policyClassDepth("java.util.ResourceBundle") == 6 && (getCSM().policyClassDepth("com.netscape.javascript.Context") == 9 || getCSM().policyClassDepth("com.sun.javascript.ScriptingEngine") == 9))
            return true;
        else
            return super.checkConnect(s, i);
    }

    public boolean checkTopLevelWindow(Object obj)
    {
        int i = getCSM().policyClassDepth("com.sun.javascript.WindowObject");
        if(i >= 9 && i <= 13)
            return true;
        if(getCSM().policyInClass("com.sun.javascript.WindowObject$JSDialog") || getCSM().policyClassDepth("sunw.hotjava.ui.CheckboxDialog") == 8 && getCSM().policyClassDepth("sunw.hotjava.bean.CookieJar") == 11 && getCSM().policyClassDepth("com.sun.javascript.DocumentObject") == 14 || getCSM().policyClassDepth("sunw.hotjava.ui.CheckboxDialog") == 9 && getCSM().policyClassDepth("sunw.hotjava.bean.CookieJar") == 12 && getCSM().policyClassDepth("com.sun.javascript.DocumentObject") == 15)
            return true;
        else
            return super.checkTopLevelWindow(obj);
    }

    public boolean checkPackageAccess(String s)
    {
        if(getCSM().policyClassDepth("com.netscape.javascript.Interpreter") != 5)
            return true;
        else
            return super.checkPackageAccess(s);
    }

    public boolean checkPrintJobAccess()
    {
        if(getCSM().policyClassDepth("com.sun.javascript.WindowObject") == 4)
            return true;
        else
            return super.checkPrintJobAccess();
    }

    private static PolicyScript singleton = null;

}
