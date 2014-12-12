// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   PeriodicSaveManager.java

package sunw.hotjava;

import java.awt.Dialog;
import java.beans.*;
import java.io.File;
import java.text.MessageFormat;
import java.util.Date;
import sunw.hotjava.bean.CookieJarInterface;
import sunw.hotjava.bean.URLPoolEvent;
import sunw.hotjava.bean.URLPoolListener;
import sunw.hotjava.bean.URLPooler;
import sunw.hotjava.misc.HJBProperties;
import sunw.hotjava.misc.HttpCookie;
import sunw.hotjava.ui.CheckboxDialog;
import sunw.hotjava.ui.HotList;
import sunw.hotjava.ui.HotListEvent;
import sunw.hotjava.ui.HotListListener;

// Referenced classes of package sunw.hotjava:
//            HJWindowManager

public class PeriodicSaveManager
    implements URLPoolListener, Runnable, VetoableChangeListener, HotListListener
{

    public static PeriodicSaveManager getPeriodicSaveManager()
    {
        if(psm == null)
            psm = new PeriodicSaveManager();
        return psm;
    }

    private PeriodicSaveManager()
    {
        properties = HJBProperties.getHJBProperties("hjbrowser");
        saveDelay = Integer.valueOf(properties.getProperty("urlpool.autosave", "120")).intValue();
        urlpool = HJWindowManager.getHJWindowManager().getURLPoolManager();
        int i = properties.getInteger("urlpool.expires", 14);
        urlpool.setURLExpirationAmt(i);
        urlpool.addURLPoolListener(this);
        urlpool.createURLPoolFromFile(getURLPoolPath());
        cookieList = HJWindowManager.getHJWindowManager().getCookiesManager();
        cookieList.addVetoableChangeListener(this);
        cookieList.loadCookieJarFromFile(getCookiePath());
        hotlist = HotList.getHotList();
        hotlist.addHotListListener(this);
    }

    private String getURLPoolPath()
    {
        if(poolPath == null)
        {
            poolPath = System.getProperty("user.home");
            if(poolPath != null && !poolPath.endsWith(File.separator))
                poolPath = poolPath + File.separator;
            poolPath = poolPath + ".hotjava" + File.separator + "urlpool";
        }
        return poolPath;
    }

    private String getCookiePath()
    {
        if(cookiePath == null)
        {
            cookiePath = System.getProperty("user.home");
            if(cookiePath != null && !cookiePath.endsWith(File.separator))
                cookiePath = cookiePath + File.separator;
            cookiePath = cookiePath + ".hotjava" + File.separator + "cookies";
        }
        return cookiePath;
    }

    public void urlPoolChanged(URLPoolEvent urlpoolevent)
    {
        poolChanged = true;
    }

    public void vetoableChange(PropertyChangeEvent propertychangeevent)
        throws PropertyVetoException
    {
        HJFrame hjframe = HJWindowManager.getHJWindowManager().getLastFocusHolder();
        if(hjframe != null)
        {
            String s = properties.getProperty("hotjava.cookie.acceptpolicy");
            if("notify".equals(s) && propertychangeevent.getNewValue() != null)
            {
                CheckboxDialog checkboxdialog = new CheckboxDialog(hjframe, "cookiedialog", properties, true);
                checkboxdialog.setPrompt(cookiePrompt((HttpCookie)propertychangeevent.getNewValue()));
                checkboxdialog.show();
                if(checkboxdialog.getCheckboxAnswer())
                    properties.put("hotjava.cookie.acceptpolicy", "all");
                if(checkboxdialog.getAnswer() == 1)
                    throw new PropertyVetoException("", propertychangeevent);
            }
        }
        cookiesChanged = true;
    }

    private String cookiePrompt(HttpCookie httpcookie)
    {
        String as[] = {
            httpcookie.getDomain(), httpcookie.getDomain(), httpcookie.getNameValue()
        };
        StringBuffer stringbuffer = new StringBuffer(MessageFormat.format(properties.getProperty("confirm.cookiedialog.prompt", "A cookie will be set.\n"), as));
        if(httpcookie != null && httpcookie.getExpirationDate() != null)
        {
            String s = httpcookie.getExpirationDate().toString();
            String as1[] = {
                s
            };
            stringbuffer.append("\n");
            stringbuffer.append(MessageFormat.format(properties.getProperty("confirm.cookiedialog.prompt1"), as1));
        }
        stringbuffer.append("\n");
        stringbuffer.append(properties.getProperty("confirm.cookiedialog.prompt2", "Do you want to accept it?\n"));
        return stringbuffer.toString();
    }

    public void hotlistChanged(HotListEvent hotlistevent)
    {
        hotlistChanged = true;
    }

    public void prepareToExit()
    {
        urlpool.removeURLPoolListener(this);
        cookieList.removeVetoableChangeListener(this);
        if(poolChanged)
        {
            urlpool.saveURLPoolToFile(getURLPoolPath());
            poolChanged = false;
        }
        if(cookiesChanged)
        {
            cookieList.saveCookieJarToFile(getCookiePath());
            cookiesChanged = false;
        }
        if(properties.isChanged())
        {
            properties.save();
            properties.setChanged(false);
        }
        if(hotlistChanged)
        {
            hotlist.exportHTMLFile();
            hotlistChanged = false;
        }
    }

    public void run()
    {
        do
        {
            do
            {
                try
                {
                    Thread.sleep(saveDelay * 1000);
                }
                catch(Exception _ex) { }
                if(poolChanged)
                {
                    urlpool.saveURLPoolToFile(getURLPoolPath());
                    poolChanged = false;
                }
                if(cookiesChanged)
                {
                    cookieList.saveCookieJarToFile(getCookiePath());
                    cookiesChanged = false;
                }
                if(properties.isChanged())
                {
                    properties.save();
                    properties.setChanged(false);
                }
            } while(!hotlistChanged);
            hotlist.exportHTMLFile();
            hotlistChanged = false;
        } while(true);
    }

    private static PeriodicSaveManager psm = null;
    private static boolean poolChanged;
    private static boolean cookiesChanged;
    private static boolean hotlistChanged;
    private static URLPooler urlpool;
    private static CookieJarInterface cookieList;
    private static HotList hotlist;
    private static final String poolClass = "sunw.hotjava.bean.URLPool";
    private static final String cookieClass = "sunw.hotjava.bean.CookieJar";
    private static String poolPath = null;
    private static String cookiePath = null;
    private static HJBProperties properties;
    private static final int ACCEPT = 0;
    private static final int REJECT = 1;
    private static int saveDelay;

}
