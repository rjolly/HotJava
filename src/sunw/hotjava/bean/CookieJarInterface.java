// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   CookieJarInterface.java

package sunw.hotjava.bean;

import java.beans.VetoableChangeListener;
import java.net.URL;
import java.net.URLConnection;
import sun.net.www.protocol.http.HttpURLConnection;
import sunw.hotjava.misc.HttpCookie;

public interface CookieJarInterface
{

    public abstract void addToCookieJar(HttpCookie ahttpcookie[]);

    public abstract void addToCookieJar(String s, URL url);

    public abstract void discardAllCookies();

    public abstract void loadCookieJarFromFile(String s);

    public abstract void saveCookieJarToFile(String s);

    public abstract HttpCookie[] getAllCookies();

    public abstract HttpCookie[] getCookiesForURL(URL url);

    public abstract void setCookieDisable(boolean flag);

    public abstract void purgeExpiredCookies();

    public abstract void recordAnyCookies(URLConnection urlconnection);

    public abstract void recordCookie(HttpURLConnection httpurlconnection, String s);

    public abstract void recordCookie(HttpCookie httpcookie);

    public abstract void applyRelevantCookies(URL url, URLConnection urlconnection);

    public abstract void addVetoableChangeListener(VetoableChangeListener vetoablechangelistener);

    public abstract void removeVetoableChangeListener(VetoableChangeListener vetoablechangelistener);
}
