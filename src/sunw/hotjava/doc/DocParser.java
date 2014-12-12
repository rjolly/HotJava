// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   DocParser.java

package sunw.hotjava.doc;

import java.awt.Color;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.CharConversionException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.PrintStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.NoRouteToHostException;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.net.UnknownServiceException;
import java.util.Date;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Vector;
import sun.awt.image.InputStreamImageSource;
import sun.io.ByteToCharConverter;
import sun.net.www.ApplicationLaunchException;
import sun.net.www.HeaderParser;
import sun.net.www.MimeEntry;
import sun.net.www.MimeTable;
import sun.net.www.content.text.PlainTextInputStream;
import sunw.hotjava.bean.CookieJarInterface;
import sunw.hotjava.bean.HotJavaBrowserBean;
import sunw.hotjava.forms.FORM;
import sunw.hotjava.misc.CharacterEncoding;
import sunw.hotjava.misc.ContentViewersManager;
import sunw.hotjava.misc.Globals;
import sunw.hotjava.misc.HJBProperties;
import sunw.hotjava.misc.RfcDateParser;
import sunw.hotjava.misc.URLConnector;
import sunw.hotjava.script.ScriptingEngineInterface;
import sunw.hotjava.tables.TABLE;
import sunw.hotjava.tags.FRAMESET;
import sunw.hotjava.tags.P;
import sunw.hotjava.tags.SCRIPT;
import sunw.html.Attributes;
import sunw.html.DTD;
import sunw.html.Element;
import sunw.html.IncorrectReaderException;
import sunw.html.Parser;
import sunw.html.Tag;
import sunw.html.TagStack;

// Referenced classes of package sunw.hotjava.doc:
//            DocBusyException, DocConstants, DocException, DocItem, 
//            DocReader, Document, DocumentCache, DocumentEvent, 
//            DocumentEventMulticaster, DocumentEventSource, DocumentFormatter, Formatter, 
//            TagItem, DocumentListener

public class DocParser extends Parser
    implements Runnable, DocConstants, DocumentEventSource
{

    public DocParser(Document document)
        throws DocException
    {
        this(document, null, new Properties(), true, null, null);
    }

    public DocParser(Document document, boolean flag, HotJavaBrowserBean hotjavabrowserbean)
        throws DocException
    {
        this(document, null, new Properties(), flag, null, hotjavabrowserbean);
    }

    public DocParser(Document document, boolean flag)
        throws DocException
    {
        this(document, null, new Properties(), flag, null, null);
    }

    public DocParser(Document document, URLConnection urlconnection)
        throws DocException
    {
        this(document, urlconnection, new Properties(), true, null, null);
    }

    public DocParser(Document document, Properties properties)
        throws DocException
    {
        this(document, null, properties, true, null, null);
    }

    public DocParser(Document document, Reader reader)
        throws DocException
    {
        this(document, null, new Properties(), false, ((Reader) (new DocReader(reader, document.getWriter()))), null);
    }

    public DocParser(Document document, Reader reader, HotJavaBrowserBean hotjavabrowserbean)
        throws DocException
    {
        this(document, null, new Properties(), false, ((Reader) (new DocReader(reader, document.getWriter()))), hotjavabrowserbean);
    }

    public DocParser(Document document, URLConnection urlconnection, Properties properties, boolean flag)
        throws DocException
    {
        this(document, urlconnection, properties, flag, null, null);
    }

    public DocParser(Document document, URLConnection urlconnection, Properties properties, boolean flag, Reader reader, HotJavaBrowserBean hotjavabrowserbean)
        throws DocException
    {
        changes = new PropertyChangeSupport(this);
        abortRequested = false;
        canChangeCharSet = true;
        useSpecifiedCharSet = false;
        listeners = new DocumentEventMulticaster();
        modalConditionVar = new Object();
        doc = document;
        connection = urlconnection;
        super.props = properties;
        useCaches = flag;
        in = reader;
        hjbean = hotjavabrowserbean;
        HJBProperties hjbproperties = HJBProperties.getHJBProperties("beanPropertiesKey");
        canChangeCharSet = !Boolean.getBoolean("ignoreCharsetDirective");
        charSetName = hjbproperties.getProperty("hotjava.charset");
        try
        {
            ByteToCharConverter.getConverter(charSetName);
        }
        catch(UnsupportedEncodingException _ex)
        {
            throw new DocException("Conversion " + charSetName + " not found.");
        }
        scheduleParsing();
    }

    public DocParser(Document document, URL url)
        throws DocException
    {
        changes = new PropertyChangeSupport(this);
        abortRequested = false;
        canChangeCharSet = true;
        useSpecifiedCharSet = false;
        listeners = new DocumentEventMulticaster();
        modalConditionVar = new Object();
        doc = document;
        if(url != null)
            document.setProperty("referer", url);
        connection = null;
        useCaches = true;
        in = null;
        super.props = new Properties();
        HJBProperties hjbproperties = HJBProperties.getHJBProperties("beanPropertiesKey");
        charSetName = hjbproperties.getProperty("hotjava.charset");
        try
        {
            ByteToCharConverter.getConverter(charSetName);
            return;
        }
        catch(UnsupportedEncodingException _ex)
        {
            throw new DocException("Conversion " + charSetName + " not found.");
        }
    }

    public void addDocumentListener(DocumentListener documentlistener)
    {
        listeners.add(documentlistener);
    }

    public void removeDocumentListener(DocumentListener documentlistener)
    {
        listeners.remove(documentlistener);
    }

    public void parseTemplate(String s, boolean flag)
    {
        closeStream();
        try
        {
            URL url = doc.getURL();
            if(url != null && flag)
                DocumentCache.removeDocument(url);
            super.props.put("type", "hotjava/error");
            if(url == null)
            {
                if(super.props.get("url") == null)
                    super.props.put("url", "<unknown>");
            } else
            if(s.equals("viewer.unknown.html") && url.getProtocol().indexOf("doc") == -1)
                super.props.put("url", "doc://viewer.unknown/" + url.toExternalForm());
            else
                super.props.put("url", url.toExternalForm());
            super.props.put("date", (new Date()).toString());
            HJBProperties hjbproperties = HJBProperties.getHJBProperties("beanPropertiesKey");
            String s1 = hjbproperties.getProperty("browser.version");
            DocReader docreader = null;
            String s2 = null;
            s2 = hjbproperties.getProperty("template." + s, "/lib/templates/" + s);
            super.props.put("applet.package", hjbproperties.getProperty("applet.package", "sunw.hotjava.applets"));
            if(s1 == null)
            {
                inStream = Globals.getBeanResourceAsStream(s2);
                if(inStream == null)
                    throw new IOException();
            } else
            {
                URL url1 = new URL(s2);
                inStream = url1.openStream();
                doc.setProperty("template.src.url", s2);
            }
            if(!inStream.markSupported())
                inStream = new BufferedInputStream(inStream);
            inStream.mark(16384);
            docreader = makeReader(inStream);
            in = new BufferedReader(docreader);
            try
            {
                interruptsIgnored(true);
                super.parse(in, doc.getDTD());
            }
            catch(IncorrectReaderException _ex)
            {
                reparse();
            }
            finally
            {
                interruptsIgnored(false);
            }
            String s3 = (String)super.props.get("connection-error");
            if(s3 != null)
                doc.setProperty("connection-error", s3);
            if(flag)
            {
                doc.setExpirationDate(new Date());
                done(1);
            }
        }
        catch(DocException _ex) { }
        catch(MalformedURLException _ex) { }
        catch(IOException _ex) { }
        catch(ThreadDeath threaddeath)
        {
            try
            {
                done(3);
            }
            catch(DocException _ex) { }
            throw threaddeath;
        }
        finally
        {
            closeStream();
        }
    }

    public boolean probablyHTML(Reader reader)
        throws IOException
    {
        if(reader.markSupported())
        {
            reader.mark(32);
            char ac[] = new char[32];
            int i = reader.read(ac);
label0:
            for(int j = 0; j < i;)
                switch(ac[j])
                {
                default:
                    break label0;

                case 60: // '<'
                    if(j + 3 < i && (ac[j + 1] == 'M' || ac[j + 1] == 'm') && (ac[j + 2] == 'I' || ac[j + 2] == 'i') && (ac[j + 3] == 'F' || ac[j + 3] == 'f'))
                    {
                        reader.reset();
                        return false;
                    } else
                    {
                        reader.reset();
                        return true;
                    }

                case 9: // '\t'
                case 10: // '\n'
                case 13: // '\r'
                case 32: // ' '
                    j++;
                    break;
                }

            reader.reset();
        }
        return false;
    }

    public void run()
    {
        URL url = null;
        try
        {
            synchronized(activeParsers)
            {
                activeParsers.addElement(this);
            }
            doc.setCompleted(0);
            url = doc.getURL();
            if(in != null && connection == null)
            {
                if(url != null)
                    super.props.put("url", url.toExternalForm());
                canChangeCharSet = false;
                super.parse(in, doc.getDTD());
                if(Thread.interrupted())
                    throw new InterruptedIOException();
                done(0);
                return;
            }
            URL url1 = url;
            URL url2 = doc.getReferer();
            String s2 = null;
            MimeEntry mimeentry = null;
            MimeTable mimetable = ContentViewersManager.getManager().getMimeTable();
            CookieJarInterface cookiejarinterface = HotJavaBrowserBean.getCookiesManager();
            Thread thread = Thread.currentThread();
            doc.clearProperties();
            Exception exception2 = null;
            if("doc".equals(url.getProtocol()) && "unknown.protocol".equals(url.getHost()))
            {
                super.props.put("badurl", url.getFile().substring(1));
                parseTemplate("unknown.url.html", true);
                return;
            }
            try
            {
                try
                {
                    if(connection == null)
                    {
                        super.props.put("type", "");
                        if(url != null)
                            super.props.put("url", url.toExternalForm());
                        if(doc.getConnector() != null)
                        {
                            connection = doc.getConnector().openConnection(url);
                        } else
                        {
                            connection = url.openConnection();
                            if(cookiejarinterface != null)
                                cookiejarinterface.applyRelevantCookies(url, connection);
                            setCacheProps(url2);
                        }
                        if(thread.isInterrupted())
                            throw new InterruptedIOException();
                    }
                    boolean flag = false;
                    int i = 0;
                    do
                    {
                        Globals.tryToStopFollowRedirects(connection);
                        inStream = connection.getInputStream();
                        if(hjbean != null && url != null && url.getProtocol().equals("https"))
                            hjbean.setSecureConnection(url);
                        in = null;
                        flag = false;
                        String s7 = connection.getHeaderField("Pragma");
                        String s8 = connection.getHeaderField("Cache-Control");
                        if(s7 != null && s7.indexOf("no-cache") >= 0 || s8 != null && s8.indexOf("no-cache") >= 0)
                            doc.setPragma("no-cache");
                        else
                            doc.setPragma(null);
                        if(connection instanceof HttpURLConnection)
                        {
                            HttpURLConnection httpurlconnection = (HttpURLConnection)connection;
                            int k1 = httpurlconnection.getResponseCode();
                            if(k1 >= 300 && k1 <= 305 && k1 != 304)
                            {
                                String s10 = httpurlconnection.getHeaderField("Location");
                                if(s10 != null && i < 5)
                                {
                                    flag = true;
                                    url1 = new URL(httpurlconnection.getURL(), s10);
                                    doc.setProperty("url", url1);
                                    DocumentCache.putDocument(doc);
                                    closeStream();
                                    connection = url1.openConnection();
                                    if(cookiejarinterface != null)
                                        cookiejarinterface.applyRelevantCookies(url1, connection);
                                    setCacheProps(url2);
                                    i++;
                                }
                            }
                        }
                    } while(flag);
                    if(i > 0)
                    {
                        changes.firePropertyChange("documentString", url.toString(), url1.toString());
                        url = url1;
                    }
                    Date date = getExpirationDate(connection);
                    doc.setExpirationDate(date);
                    Date date1 = new Date(connection.getLastModified());
                    doc.setLastModifiedDate(date1);
                    if(thread.isInterrupted())
                        throw new InterruptedIOException();
                    HJBProperties hjbproperties2 = HJBProperties.getHJBProperties("beanPropertiesKey");
                    String s9 = hjbproperties2.getProperty("http.forbidden.show");
                    if(s9 == null || !s9.equalsIgnoreCase("false"))
                    {
                        String s11 = hjbproperties2.getProperty("browser.version");
                        if(s11 != null && (connection instanceof HttpURLConnection))
                        {
                            HttpURLConnection httpurlconnection1 = (HttpURLConnection)connection;
                            int i2 = httpurlconnection1.getResponseCode();
                            if(i2 == 403 || i2 == 401)
                            {
                                String s14 = "http.forbidden";
                                String s15 = hjbproperties2.getProperty(s14);
                                super.props.put("message", s15);
                                exception2 = new Exception();
                                throw exception2;
                            }
                        }
                    }
                    s2 = connection.getContentType();
                    if(s2 == null && (connection instanceof HttpURLConnection))
                        s2 = URLConnection.guessContentTypeFromStream(inStream);
                    if(s2 == null)
                        s2 = "content/unknown";
                    int l1 = s2.indexOf(";");
                    if(l1 > -1)
                    {
                        String s12 = s2.substring(l1);
                        s2 = s2.substring(0, l1).trim();
                        if(s2.toLowerCase().startsWith("text/") && canChangeCharSet)
                        {
                            String s13 = getCharsetFromContentTypeParameters(s12);
                            if(s13 != null)
                            {
                                try
                                {
                                    ByteToCharConverter.getConverter(s13);
                                    charSetName = s13;
                                }
                                catch(UnsupportedEncodingException _ex) { }
                                canChangeCharSet = false;
                                useSpecifiedCharSet = true;
                            }
                        }
                    }
                    mimeentry = mimetable.find(s2.toLowerCase());
                    if(mimeentry == null)
                        mimeentry = mimetable.find("content/unknown");
                }
                catch(InterruptedIOException interruptedioexception1)
                {
                    throw interruptedioexception1;
                }
                finally
                {
                    doc.clear();
                    doc.setState(12);
                    doc.setCompleted(0);
                    intitle = 0;
                    inbody = 0;
                    seentitle = false;
                    errors = null;
                    pos = 0;
                }
            }
            catch(UnknownServiceException _ex)
            {
                super.props.put("protocol", url.getProtocol() == null ? "<unknown>" : ((Object) (url.getProtocol())));
                parseTemplate("unknown.protocol.html", true);
                return;
            }
            catch(UnknownHostException _ex)
            {
                super.props.put("host", url.getHost() == null ? "<unknown>" : ((Object) (url.getHost())));
                parseTemplate("unknown.host.html", true);
                return;
            }
            catch(FileNotFoundException _ex)
            {
                if(connection != null)
                    url = connection.getURL();
                super.props.put("host", url.getHost() == null ? "<unknown>" : ((Object) (url.getHost())));
                super.props.put("file", url.getFile() == null ? "<unknown>" : ((Object) (url.getFile())));
                parseTemplate("unknown.file.html", true);
                return;
            }
            catch(MalformedURLException _ex)
            {
                super.props.put("badurl", "BOGUS");
                parseTemplate("unknown.url.html", true);
                return;
            }
            catch(InterruptedIOException interruptedioexception)
            {
                throw interruptedioexception;
            }
            catch(NoRouteToHostException noroutetohostexception)
            {
                super.props.put("host", url.getHost() == null ? "<unknown>" : ((Object) (url.getHost())));
                String s3 = noroutetohostexception.getMessage();
                if(s3 != null && s3.equals(""))
                    s3 = null;
                super.props.put("message", s3 != null ? ((Object) (s3)) : "(no details)");
                parseTemplate("unreachable.host.html", true);
                return;
            }
            catch(ConnectException connectexception)
            {
                super.props.put("connection-error", url.getProtocol());
                super.props.put("exception", connectexception.getClass().getName());
                String s4 = connectexception.getMessage();
                if(s4.equals(""))
                    s4 = null;
                super.props.put("message", s4 != null ? ((Object) (s4)) : "(no details)");
                parseTemplate("open.error.html", true);
                return;
            }
            catch(SocketException socketexception)
            {
                HJBProperties hjbproperties = HJBProperties.getHJBProperties("beanPropertiesKey");
                if(hjbproperties.getProperty("socksProxyHost") != null)
                {
                    super.props.put("host", url.getHost() == null ? "<unknown>" : ((Object) (url.getHost())));
                    parseTemplate("socks.open.html", true);
                    return;
                } else
                {
                    throw socketexception;
                }
            }
            catch(Exception exception4)
            {
                if(exception4 == exception2)
                {
                    parseTemplate("open.forbidden.html", true);
                    return;
                }
                exception4.printStackTrace();
                super.props.put("exception", exception4.getClass().getName());
                String s5 = exception4.getMessage();
                if(s5 != null && s5.equals(""))
                    s5 = null;
                super.props.put("message", s5 != null ? ((Object) (s5)) : "(no details)");
                parseTemplate("open.error.html", true);
                return;
            }
            catch(ThreadDeath threaddeath1)
            {
                super.props.put("baseurl", url == null ? "<unknown>" : ((Object) (url.toExternalForm())));
                parseTemplate("open.stop.html", true);
                throw threaddeath1;
            }
            if(Thread.interrupted())
                throw new InterruptedIOException();
            processDisposition(connection, super.props);
            if(!inStream.markSupported())
                inStream = new BufferedInputStream(inStream);
            inStream.mark(16384);
            if(mimeentry.matches("content/unknown"))
            {
                if(maybeLaunchAudioApplet(s2))
                    return;
                in = new BufferedReader(makeReader(inStream), 32);
                if(probablyHTML(in))
                {
                    try
                    {
                        super.parse(in, doc.getDTD());
                    }
                    catch(IncorrectReaderException _ex)
                    {
                        reparse();
                    }
                    if(Thread.interrupted())
                    {
                        Thread.currentThread().interrupt();
                        if(abortRequested)
                        {
                            handleAbortRequest();
                            return;
                        }
                        if(doc.getState() != 4)
                            parseTemplate("load.stop.html", true);
                    } else
                    {
                        done(0);
                    }
                } else
                {
                    parseTemplate("viewer.unknown.html", true);
                }
                return;
            }
            if(mimeentry.matches("text/html"))
            {
                in = makeReader(inStream);
                try
                {
                    super.parse(in, doc.getDTD());
                }
                catch(IncorrectReaderException _ex)
                {
                    reparse();
                }
                if(Thread.interrupted())
                    throw new InterruptedIOException();
                done(0);
                return;
            }
            Object obj = null;
            try
            {
                obj = mimeentry.launch(connection, inStream, mimetable);
                inStream = null;
                connection = null;
                in = null;
            }
            catch(ApplicationLaunchException applicationlaunchexception)
            {
                if(maybeLaunchAudioApplet(s2))
                    return;
                super.props.put("appname", applicationlaunchexception.getMessage());
                parseTemplate("app-launch-error.html", true);
                return;
            }
            if(Thread.interrupted())
                throw new InterruptedIOException();
            super.props.put("content", s2);
            if(obj == null)
            {
                if(maybeLaunchAudioApplet(s2))
                    return;
                parseTemplate("viewer.unknown.html", true);
            } else
            if(obj instanceof Thread)
            {
                if(hjbean != null)
                    hjbean.processExtViewerURLInStack(url);
                inStream = null;
                ((Thread)obj).start();
                parseTemplate("viewer.started.html", true);
            } else
            if(obj instanceof PlainTextInputStream)
            {
                pos = doc.insertTagPair(pos, "plaintext", null);
                PlainTextInputStream plaintextinputstream = (PlainTextInputStream)obj;
                DocReader docreader = makeReader(plaintextinputstream);
                HJBProperties hjbproperties1 = HJBProperties.getHJBProperties("beanPropertiesKey");
                int k = hjbproperties1.getInteger("hotjava.plaintext.chunksize");
                char ac[] = new char[k];
                for(int i1 = 0; (i1 = docreader.read(ac)) >= 0;)
                    pos = doc.insert(pos, ac, 0, i1);

                doc.setCompleted(doc.nitems - 1 << 12);
                docreader.close();
                done(1);
            } else
            if(obj instanceof InputStream)
            {
                String s6 = mimeentry.getType();
                if(s6.startsWith("image/"))
                {
                    parseTemplate("viewer.image.html", true);
                } else
                {
                    closeStream();
                    if(mimeentry.getAction() == 2)
                        parseTemplate("save.to.file.html", true);
                    else
                        parseTemplate("viewer.unknown.html", true);
                }
            } else
            if(obj instanceof InputStreamImageSource)
                parseTemplate("viewer.image.html", true);
            else
            if(obj instanceof String)
            {
                if(((String)obj).equals("x_x509_ca_cert handler"))
                {
                    closeStream();
                    hjbean.back();
                    return;
                }
                pos = doc.insertTagPair(pos, "plaintext", null);
                pos = doc.insert(pos, (String)obj);
                done(1);
            } else
            if(obj instanceof Document)
            {
                DocItem adocitem[] = ((Document)obj).items;
                Vector vector2 = new Vector(adocitem.length);
                for(int j = 0; j < adocitem.length; j++)
                    if(adocitem[j] != null)
                        vector2.addElement(adocitem[j]);

                DocItem adocitem1[] = new DocItem[vector2.size()];
                for(int l = 0; l < vector2.size(); l++)
                    adocitem1[l] = (DocItem)vector2.elementAt(l);

                doc.items = adocitem1;
                doc.nitems = doc.items.length - 1;
                doc.setDocumentTitle("PDF Viewer");
                for(int j1 = 0; j1 < doc.items.length; j1++)
                    System.out.println(j1 + " : " + doc.items[j1]);

                done(1);
            }
        }
        catch(CharConversionException charconversionexception)
        {
            if(doc.getState() != 15)
            {
                super.props.put("exception", charconversionexception.getClass().getName());
                String s = charconversionexception.getMessage();
                if(s != null && s.equals(""))
                    s = null;
                super.props.put("message", s != null ? ((Object) (s)) : "(no details)");
                if(!useSpecifiedCharSet)
                {
                    parseTemplate("charencode.error.html", true);
                } else
                {
                    super.props.put("incorrectCharset", charSetName);
                    parseTemplate("charencode.directive.error.html", true);
                }
            }
        }
        catch(InterruptedIOException _ex)
        {
            if(abortRequested)
            {
                handleAbortRequest();
                return;
            }
            if(doc.getState() != 4)
            {
                super.props.put("baseurl", url == null ? "<unknown>" : ((Object) (url.toExternalForm())));
                parseTemplate("open.stop.html", true);
            }
        }
        catch(Exception exception1)
        {
            if(doc.getState() != 15)
            {
                exception1.printStackTrace();
                super.props.put("exception", exception1.getClass().getName());
                String s1 = exception1.getMessage();
                if(s1 != null && s1.equals(""))
                    s1 = null;
                super.props.put("message", s1 != null ? ((Object) (s1)) : "(no details)");
                parseTemplate("load.error.html", true);
            }
        }
        catch(ThreadDeath threaddeath)
        {
            parseTemplate("load.stop.html", true);
            throw threaddeath;
        }
        finally
        {
            closeStream();
            synchronized(activeParsers)
            {
                activeParsers.removeElement(this);
            }
            parser = null;
            parserThreadCompleted();
        }
    }

    private boolean canRestart()
    {
        try
        {
            inStream.reset();
        }
        catch(IOException _ex)
        {
            return false;
        }
        return true;
    }

    private void reparse()
        throws IOException, DocException
    {
        super.resetParser();
        doc.clear();
        doc.setCompleted(0);
        intitle = 0;
        inbody = 0;
        seentitle = false;
        errors = null;
        pos = 0;
        in = makeReader(inStream);
        parse(in, doc.getDTD());
    }

    public void awaitDialogCompletion()
    {
        synchronized(modalConditionVar)
        {
            try
            {
                modalConditionVar.wait();
            }
            catch(InterruptedException _ex) { }
        }
    }

    public void dialogCompleted(boolean flag)
    {
        synchronized(modalConditionVar)
        {
            modalConditionVar.notify();
            dialogConfirmed = flag;
        }
    }

    private Color getColor(Attributes attributes, String s)
    {
        String s1 = attributes.get(s);
        if(s1 != null)
            return Globals.mapNamedColor(s1);
        else
            return null;
    }

    void done(int i)
        throws DocException
    {
        doc.setProperty("charset", charSetName);
        if(specifiedJISCharSet != null)
            doc.setProperty("specifiedjischarset", specifiedJISCharSet);
        doc.setCompleted(0x7fffffff);
        doc.zipSource();
        doc.setOwner(i, null, null);
        String s = (String)doc.getProperty("type");
        if(s == null || !s.startsWith("hotjava/"))
        {
            DocumentEvent documentevent = new DocumentEvent(this, 1008, doc);
            listeners.documentChanged(documentevent);
        }
        errors = null;
    }

    private void scheduleParsing()
    {
        synchronized(getClass())
        {
            if(numParserThreads >= 6)
            {
                if(parserQueue == null)
                    parserQueue = new Vector();
                parserQueue.addElement(this);
                return;
            }
            numParserThreads++;
        }
        startParsingThread();
    }

    private void parserThreadCompleted()
    {
        DocParser docparser = null;
        DocView docview = doc.getView();
        ScriptingEngineInterface scriptingengineinterface = Globals.getScriptingEngine();
        if(docview != null && (docview instanceof DocumentFormatter) && scriptingengineinterface != null)
        {
            DocumentFormatter documentformatter = (DocumentFormatter)docview;
            if(documentformatter.hasFrameSetPanel() && documentformatter.getScriptContext() == null)
                try
                {
                    documentformatter.setScriptContext(scriptingengineinterface.getContext(documentformatter));
                }
                catch(RuntimeException runtimeexception)
                {
                    System.out.println("[[[ DocParser(): could not instantiate script context: <" + runtimeexception + "> ]]]");
                    runtimeexception.printStackTrace();
                }
        }
        synchronized(getClass())
        {
            if(parserQueue != null && parserQueue.size() > 0)
            {
                docparser = (DocParser)parserQueue.elementAt(0);
                parserQueue.removeElementAt(0);
            } else
            {
                numParserThreads--;
            }
        }
        if(docparser != null)
            docparser.startParsingThread();
    }

    private void startParsingThread()
    {
        try
        {
            URL url = doc.getURL();
            parser = new Thread(this, "DocParser-" + (url != null ? url.toString() : "??"));
            doc.setOwner(11, parser, this);
            parser.setPriority(4);
            parser.start();
            return;
        }
        catch(DocBusyException docbusyexception)
        {
            docbusyexception.printStackTrace();
        }
    }

    protected Tag makeTag(Element element, Attributes attributes)
    {
        return doc.makeTag(element, attributes);
    }

    private void handleFormElement(Tag tag)
    {
        TagItem tagitem = (TagItem)getEnclosingForm();
        if(tagitem != null)
        {
            tagitem.addFormElement((TagItem)tag);
            ((DocItem)tag).setFormParent(tagitem);
            return;
        }
        if(lastSeenForm != null)
        {
            error("form.ended", tag.getElement().getName());
            lastSeenForm.addFormElement((TagItem)tag);
            ((DocItem)tag).setFormParent(lastSeenForm);
            return;
        } else
        {
            error("form.expected", tag.getElement().getName());
            return;
        }
    }

    protected boolean needToReopenTag(Tag tag, Tag tag1)
    {
        boolean flag = false;
        if(tag.getElement().getName().equals("p") && tag1.getElement().getName().equals("a"))
            flag = true;
        return flag;
    }

    protected void handleStartTag(Tag tag)
    {
        try
        {
            Element element = tag.getElement();
            if(element == super.dtd.body)
            {
                handleBodyTagAttributes(tag.getAttributes());
                inbody++;
                if(seenisindexInsideHead)
                    pos = doc.insertTag(pos, super.dtd.getElement("isindex"), isindexAtts);
            } else
            if(element != super.dtd.html)
                if(element == super.dtd.head)
                    inhead++;
                else
                if(element == super.dtd.title)
                    intitle++;
                else
                if(inbody != 0)
                {
                    pos = doc.insertTagPair(pos, (TagItem)tag);
                    if((element.getName().equals("select") || element.getName().equals("textarea") || element.getName().equals("option")) && (tag instanceof DocItem))
                        handleFormElement(tag);
                    if(element.getName().equals("form") && (tag instanceof FORM))
                        lastSeenForm = (FORM)tag;
                }
            if(tag.hasJavaScriptHandlers())
            {
                doc.addJavaScriptTag(tag);
                return;
            }
        }
        catch(DocException _ex) { }
    }

    protected void handleBodyTagAttributes(Attributes attributes)
    {
        if(attributes == null)
            return;
        try
        {
            String s = attributes.get("background");
            if(s != null)
                doc.setProperty("background.img", s);
            Color color;
            if((color = getColor(attributes, "bgcolor")) != null)
                doc.setProperty("background.color", color);
            if((color = getColor(attributes, "text")) != null)
                doc.setProperty("text.color", color);
            if((color = getColor(attributes, "link")) != null)
                doc.setProperty("link.color", color);
            if((color = getColor(attributes, "vlink")) != null)
                doc.setProperty("vlink.color", color);
            if((color = getColor(attributes, "alink")) != null)
                doc.setProperty("alink.color", color);
            doc.setProperty("atts", attributes);
            return;
        }
        catch(DocException _ex)
        {
            return;
        }
    }

    protected char[] handleScript(char ac[], String s)
    {
        int j = ac.length;
        int i;
        for(i = 0; i < j; i++)
            if(ac[i] == 0)
                break;

        String s1 = handleScript(new String(ac, 0, i), s);
        if(s1 == null)
            return new char[0];
        else
            return s1.toCharArray();
    }

    private String handleScript(String s, String s1)
    {
        return handleScript(s, s1, null);
    }

    private String handleScript(String s, String s1, URL url)
    {
        int i = 0;
        if(url == null)
        {
            url = doc.getURL();
            i = getCurrentLine();
        }
        DocView docview = doc.getView();
        if(docview instanceof DocumentFormatter)
        {
            DocumentFormatter documentformatter = (DocumentFormatter)docview;
            return Formatter.handleScript(documentformatter, s, url.toString(), i, s1);
        } else
        {
            return "";
        }
    }

    protected void handleCommentOrScript(char ac[])
    {
        if(isInContext("script"))
            scriptTextInComment = new String(ac);
    }

    protected void handleEmptyTag(Tag tag)
        throws IncorrectReaderException
    {
        HJBProperties.getHJBProperties("beanPropertiesKey");
        try
        {
            Element element = tag.getElement();
            if(element == super.dtd.meta)
            {
                Attributes attributes = tag.getAttributes();
                if(attributes != null)
                {
                    String s = attributes.get("content");
                    if("type".equalsIgnoreCase(attributes.get("name")))
                    {
                        if(s != null)
                        {
                            doc.setProperty("type", s.toLowerCase());
                            super.props.put("type", s.toLowerCase());
                        }
                    } else
                    if("expires".equalsIgnoreCase(attributes.get("http-equiv")))
                    {
                        if(s != null)
                        {
                            Date date;
                            if("0".equals(s))
                            {
                                date = new Date(System.currentTimeMillis());
                            } else
                            {
                                RfcDateParser rfcdateparser = new RfcDateParser(s);
                                date = rfcdateparser.getDate();
                            }
                            if(date != null)
                                doc.setExpirationDate(date);
                        }
                    } else
                    if("refresh".equalsIgnoreCase(attributes.get("http-equiv")))
                    {
                        if(s != null)
                            doc.setClientPullSpec(s);
                    } else
                    if("set-cookie".equalsIgnoreCase(attributes.get("http-equiv")))
                    {
                        if((connection instanceof HttpURLConnection) && s != null)
                        {
                            sun.net.www.protocol.http.HttpURLConnection httpurlconnection = (sun.net.www.protocol.http.HttpURLConnection)connection;
                            CookieJarInterface cookiejarinterface = HotJavaBrowserBean.getCookiesManager();
                            if(cookiejarinterface != null)
                                cookiejarinterface.recordCookie(httpurlconnection, s);
                        }
                    } else
                    if("content-type".equalsIgnoreCase(attributes.get("http-equiv")) && s != null && canChangeCharSet)
                    {
                        String s2 = getCharsetFromContentTypeParameters(s);
                        if(s2 != null)
                        {
                            canChangeCharSet = false;
                            if(!s2.equals(charSetName))
                                try
                                {
                                    ByteToCharConverter.getConverter(s2);
                                    if(canRestart())
                                    {
                                        charSetName = s2;
                                        useSpecifiedCharSet = true;
                                        throw new IncorrectReaderException();
                                    }
                                }
                                catch(UnsupportedEncodingException _ex) { }
                            else
                                useSpecifiedCharSet = true;
                        }
                    }
                }
            } else
            if(element == super.dtd.base)
            {
                Attributes attributes1 = tag.getAttributes();
                if(attributes1 != null)
                {
                    String s1 = attributes1.get("href");
                    if(s1 != null)
                        try
                        {
                            URL url = doc.getURL();
                            if(url != null)
                                doc.setProperty("base", new URL(url, s1));
                        }
                        catch(MalformedURLException _ex) { }
                    String s3 = attributes1.get("target");
                    if(s3 != null)
                        doc.setProperty("target", s3);
                }
            } else
            if(inbody != 0)
            {
                pos = doc.insertTag(pos, (TagItem)tag);
                if(element.getName().equals("input") && (tag instanceof DocItem))
                    handleFormElement(tag);
                doc.setCompleted(pos);
            } else
            if(element == super.dtd.isindex && inhead != 0)
            {
                seenisindexInsideHead = true;
                isindexAtts = tag.getAttributes();
            }
            if(tag.hasJavaScriptHandlers())
            {
                doc.addJavaScriptTag(tag);
                return;
            }
        }
        catch(DocException _ex) { }
    }

    protected void handleEndTag(Tag tag)
    {
        Element element = tag.getElement();
        if(element == super.dtd.body)
            inbody--;
        else
        if(element == super.dtd.title)
        {
            intitle--;
            seentitle = true;
        } else
        if(element == super.dtd.head)
        {
            inhead--;
            if(!seentitle)
                handleTitle(new char[0], tag);
        } else
        if(element != super.dtd.body && element != super.dtd.html)
            if(element == super.dtd.script)
            {
                String s = null;
                if(tag instanceof SCRIPT)
                {
                    SCRIPT script = (SCRIPT)tag;
                    s = script.getLanguage();
                    if(script.hasSource())
                        handleScript(script.getSource(), s, script.getSourceURL());
                }
                if(scriptTextInComment != null)
                {
                    handleScript(scriptTextInComment, s);
                    scriptTextInComment = null;
                } else
                {
                    handleScript(getText(), s);
                }
            } else
            if(element == super.dtd.frameset)
            {
                int i = pos >> 12;
                FRAMESET frameset = (FRAMESET)doc.getInnermostOpenStartTag();
                try
                {
                    for(int j = 0; j < frameset.getNeededFrames(doc); j++)
                        pos = doc.insertTag(pos, super.dtd.getElement("frame"), null);

                }
                catch(DocException docexception)
                {
                    docexception.printStackTrace();
                }
            }
        if(inbody != 0)
        {
            doc.endTagComplete((TagItem)tag, pos);
            pos += 4096;
            if(pos < 0)
                System.out.println("OVERFLOW: " + doc.nitems);
            try
            {
                doc.setCompleted(pos);
                return;
            }
            catch(DocException _ex)
            {
                return;
            }
        } else
        {
            return;
        }
    }

    protected void setTableSurroundedByForm(Tag tag)
    {
        if(tag instanceof TABLE)
        {
            ((TABLE)tag).setSurroundedTableByForm(true);
            return;
        } else
        {
            System.err.println("DocParser.getTableAndSetSurrounded() called without <TABLE> tag");
            return;
        }
    }

    protected void incrementNumberFormsToEnd(Tag tag)
    {
        if(tag instanceof TABLE)
        {
            ((TABLE)tag).incrementFormsToEnd();
            return;
        } else
        {
            System.err.println("DocParser.incrementNumberFormsToEnd() called without <TABLE> tag");
            return;
        }
    }

    protected void handleFormTagInTable(Tag tag)
    {
        TagItem tagitem = (TagItem)tag;
        int i = (pos >> 12) - 1;
        int j = i;
        for(DocItem docitem = doc.getItem(j); (!(docitem instanceof TagItem) || !((TagItem)docitem).getElement().getName().equals("table")) && j >= 0;)
        {
            if(docitem.offset < 0)
                j += docitem.offset;
            if(--j >= 0)
                docitem = doc.getItem(j);
        }

        if(j < 0)
        {
            System.err.println("DocParser.handleFormTagInTable() called with no enclosing table context!");
            return;
        }
        int k = j + doc.getItem(j).offset;
        int l = j << 12;
        int i1 = k << 12;
        lastSeenForm = (FORM)tag;
        try
        {
            doc.surround(l, i1, tagitem, pos);
            pos += 4096;
            return;
        }
        catch(DocException docexception)
        {
            System.out.println("[[[ DocParser.handleFormTag(): doc exception on surround: " + docexception + " ]]]");
        }
    }

    protected void checkForSurroundedTable(Element element, Tag tag)
    {
        if(element.getName().equals("table") && (tag instanceof TABLE))
        {
            savedTableTag = (TABLE)tag;
            return;
        } else
        {
            savedTableTag = null;
            return;
        }
    }

    protected void doNecessarySurroundedTableCleanup()
    {
        if(savedTableTag != null && savedTableTag.getSurroundedTableByForm() && savedTableTag.getFormsToEnd() > 0)
        {
            for(int i = 0; i < savedTableTag.getFormsToEnd(); i++)
                endTag(false);

        }
    }

    public void printImmediateDocContext(int i)
    {
        doc.printDocContext(pos, i);
    }

    protected void handleTitle(char ac[], Tag tag)
    {
        try
        {
            doc.setProperty("title", new String(ac));
            return;
        }
        catch(DocException _ex)
        {
            return;
        }
    }

    protected void handleText(char ac[], Tag tag)
    {
        try
        {
            if(ac != null)
            {
                if(inbody != 0)
                {
                    if(!super.isFramed && ac.length > 0)
                    {
                        if(tag != null && !tag.getElement().getName().equals("noscript") && tag.getElement() != super.dtd.script)
                            super.isNonFramed = true;
                        pos = doc.insert(pos, ac, 0, ac.length);
                        return;
                    }
                } else
                if(intitle != 0 && !seentitle)
                    handleTitle(ac, tag);
                return;
            }
        }
        catch(DocException _ex) { }
    }

    protected void endTag(boolean flag)
    {
        Tag tag = super.stack.getTag();
        if(tag instanceof P)
            ((P)tag).omittedEnd = flag;
        super.endTag(flag);
    }

    protected void handleError(int i, String s)
    {
        String s1 = "line " + i + ": " + s;
        changes.firePropertyChange("parseError", null, s1);
    }

    protected void error(String s, String s1, String s2, String s3)
    {
        HJBProperties hjbproperties = HJBProperties.getHJBProperties("beanPropertiesKey");
        String s4 = "htmlerr." + s;
        String s5 = hjbproperties.getPropertyReplace(s4, s1, s2, s3);
        if(s5 == null)
            s5 = s;
        handleError(getCurrentLine(), s5);
    }

    private void handleAbortRequest()
    {
        try
        {
            doc.setCompleted(0);
            doc.setOwner(0, null, null);
        }
        catch(DocBusyException _ex) { }
        parseTemplate("load.outofmemory.html", true);
    }

    private void abort()
    {
        abortRequested = true;
        parser.interrupt();
    }

    public static void stopActiveParsers()
    {
        synchronized(activeParsers)
        {
            for(int i = 0; i < activeParsers.size(); i++)
            {
                DocParser docparser = (DocParser)activeParsers.elementAt(i);
                docparser.abort();
            }

        }
        synchronized(sunw.hotjava.doc.DocParser.class)
        {
            parserQueue = null;
            numParserThreads = 0;
        }
    }

    private DocReader makeReader(InputStream inputstream)
    {
        try
        {
            ByteToCharConverter.getConverter(charSetName);
        }
        catch(UnsupportedEncodingException _ex)
        {
            System.out.println("Charset " + charSetName + " is not supported. Use 8859_1");
            charSetName = "8859_1";
        }
        try
        {
            return new DocReader(new InputStreamReader(inputstream, charSetName), doc.getWriter());
        }
        catch(UnsupportedEncodingException _ex)
        {
            return null;
        }
    }

    private void closeStream()
    {
        if(in != null)
            try
            {
                in.close();
            }
            catch(IOException _ex) { }
        else
        if(inStream != null)
            try
            {
                inStream.close();
            }
            catch(IOException _ex) { }
        else
        if(connection != null && (connection instanceof HttpURLConnection))
            ((HttpURLConnection)connection).disconnect();
        in = null;
        inStream = null;
        connection = null;
    }

    private void processDisposition(URLConnection urlconnection, Properties properties)
    {
        properties.put("targetFile", "");
        String s = urlconnection.getHeaderField("Content-disposition");
        if(s == null)
            return;
        int i = s.indexOf("filename");
        if(i < 0)
            return;
        String s1 = s.substring(i + "filename".length()).trim();
        if(!s1.startsWith("="))
            return;
        String s2 = s1.substring(1).trim();
        if(s2.startsWith("\""))
        {
            s2 = parseQuotedString(s2);
        } else
        {
            int j = s2.indexOf(';');
            if(j >= 0)
                s2 = s2.substring(0, j);
        }
        properties.put("targetFile", s2);
    }

    private String parseQuotedString(String s)
    {
        StringBuffer stringbuffer = new StringBuffer();
        int i = s.length();
        for(int j = 1; j < i; j++)
        {
            char c = s.charAt(j);
            if(c == '\\')
            {
                j++;
                c = s.charAt(j);
            } else
            if(c == '"')
                break;
            stringbuffer.append(c);
        }

        return stringbuffer.toString();
    }

    private Date getExpirationDate(URLConnection urlconnection)
    {
        HttpURLConnection httpurlconnection;
        if(urlconnection instanceof HttpURLConnection)
            httpurlconnection = (HttpURLConnection)urlconnection;
        else
            return null;
        Object obj = null;
        String s1 = null;
        String s2;
        for(int i = 1; (s2 = httpurlconnection.getHeaderFieldKey(i)) != null; i++)
        {
            String s;
            if(s2.equalsIgnoreCase("date"))
                s = httpurlconnection.getHeaderField(i);
            else
            if(s2.equalsIgnoreCase("expires") || s2.equalsIgnoreCase("expiration-date"))
                s1 = httpurlconnection.getHeaderField(i);
        }

        Date date = null;
        if(s1 != null)
        {
            RfcDateParser rfcdateparser = new RfcDateParser(s1);
            date = rfcdateparser.getDate();
        }
        return date;
    }

    private String getCharsetFromContentTypeParameters(String s)
    {
        String s1 = charSetName;
        Object obj = null;
        try
        {
            int i = s.indexOf(';');
            if(i > -1 && i < s.length() - 1)
                s = s.substring(i + 1);
            if(s.length() > 0)
            {
                HeaderParser headerparser = new HeaderParser(s);
                String s2 = headerparser.findValue("charset");
                if(s2 != null)
                {
                    String s3 = CharacterEncoding.aliasName(s2);
                    if(s3 != null)
                    {
                        if(charSetName.equalsIgnoreCase("JISAutoDetect") && (s3.equalsIgnoreCase("JIS") || s3.equalsIgnoreCase("SJIS") || s3.equalsIgnoreCase("EUCJIS")))
                        {
                            specifiedJISCharSet = s3;
                            return charSetName;
                        }
                        s1 = s3;
                    }
                }
            }
        }
        catch(IndexOutOfBoundsException _ex) { }
        catch(NullPointerException _ex) { }
        catch(Exception exception)
        {
            System.err.println("DocParser.getCharsetFromContentTypeParameters failed on: " + s);
            exception.printStackTrace();
        }
        return s1;
    }

    private void setCacheProps(URL url)
    {
        if(!useCaches)
        {
            connection.setRequestProperty("Pragma", "no-cache");
            connection.setRequestProperty("Cache-Control", "no-cache");
        }
        if(url != null)
            connection.setRequestProperty("Referer", url.toExternalForm());
    }

    public void addPropertyChangeListener(PropertyChangeListener propertychangelistener)
    {
        changes.addPropertyChangeListener(propertychangelistener);
    }

    public void removePropertyChangeListener(PropertyChangeListener propertychangelistener)
    {
        changes.removePropertyChangeListener(propertychangelistener);
    }

    public boolean matchesInterestedType()
    {
        if(super.props != null)
        {
            String s = super.props.getProperty("type");
            if(s != null)
                return s.startsWith("hotjava/");
        }
        return false;
    }

    private boolean maybeLaunchAudioApplet(String s)
    {
        if(s.regionMatches(true, 0, "audio", 0, 5))
        {
            parseTemplate("viewer.audio.html", true);
            return true;
        } else
        {
            return false;
        }
    }

    protected void timeStamp(String s)
    {
    }

    private static final String debugProp = "hotjava.debug.DocParser";
    private static final int MAX_PARSERS = 6;
    private static Vector parserQueue;
    private static int numParserThreads;
    private static Vector activeParsers = new Vector();
    private PropertyChangeSupport changes;
    private Document doc;
    private String ctype;
    private URLConnection connection;
    private Thread parser;
    private int pos;
    private int inbody;
    private int intitle;
    private int inhead;
    private boolean seentitle;
    private boolean seenisindexInsideHead;
    private boolean abortRequested;
    private boolean useCaches;
    private Document errors;
    private Attributes isindexAtts;
    private HotJavaBrowserBean hjbean;
    private FORM lastSeenForm;
    private TABLE savedTableTag;
    private String charSetName;
    private String specifiedJISCharSet;
    private boolean canChangeCharSet;
    private boolean useSpecifiedCharSet;
    private DocumentEventMulticaster listeners;
    private InputStream inStream;
    private Reader in;
    private final Object modalConditionVar;
    private boolean dialogConfirmed;
    private String scriptTextInComment;
    protected int numErrors;

}
