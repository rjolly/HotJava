// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Console.java

package sunw.hotjava.ui;

import java.io.PrintStream;
import sunw.hotjava.HJFrame;
import sunw.hotjava.HJWindowManager;
import sunw.hotjava.misc.HJBProperties;

// Referenced classes of package sunw.hotjava.ui:
//            ConsoleFrame, ConsoleOutput

public class Console
{

    public Console()
    {
        if(theConsole == null)
            theConsole = new ConsoleFrame();
        theConsole.toFront();
        HJFrame.alertConsoleListeners(true);
    }

    static void close()
    {
        theConsole.dispose();
        theConsole = null;
        HJFrame.alertConsoleListeners(false);
    }

    static void clear()
    {
        theConsole.clear();
    }

    static void showStatus(String s)
    {
        String s1 = "javaconsole.";
        String s2;
        if(s == null)
        {
            s2 = "";
        } else
        {
            String s3 = "hjbrowser";
            HJBProperties hjbproperties = HJBProperties.getHJBProperties(s3);
            s2 = hjbproperties.getProperty(s1 + s);
        }
        System.err.println(s2);
        HJWindowManager.getHJWindowManager().showStatusAll(s2);
    }

    static ConsoleFrame theConsole = null;

}
