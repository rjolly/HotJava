// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   SplashFrame.java

package sunw.hotjava.ui;

import java.awt.*;

// Referenced classes of package sunw.hotjava.ui:
//            SplashImage

public class SplashFrame extends Window
{

    public SplashFrame(String s)
    {
        super(new Frame());
        setSize(400, 300);
        image = new SplashImage("lib/images/hjbsplash.gif");
        add("Center", image);
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(dimension.width / 2 - 200, dimension.height / 2 - 150);
    }

    public void waitTillLoaded()
    {
        image.waitTillLoaded();
    }

    private static final String splashImagePath = "lib/images/hjbsplash.gif";
    private final int splashWidth = 400;
    private final int splashHeight = 300;
    private SplashImage image;
}
