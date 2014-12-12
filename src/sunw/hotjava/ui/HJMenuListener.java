// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   HJMenuListener.java

package sunw.hotjava.ui;

import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.io.*;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.EventObject;
import sunw.hotjava.HJFrame;
import sunw.hotjava.HJWindowManager;
import sunw.hotjava.bean.CurrentDocument;
import sunw.hotjava.bean.HTMLBrowsable;
import sunw.hotjava.misc.HJBProperties;

// Referenced classes of package sunw.hotjava.ui:
//            ConfirmDialog, GoToPlaceDialog, HotList, HotListFrame, 
//            UserFileDialog, UserFrame, UserMenuItem

public class HJMenuListener
    implements ActionListener, ItemListener, WindowListener
{

    public HJMenuListener(HJFrame hjframe)
    {
        frame = hjframe;
        properties = HJBProperties.getHJBProperties("hjbrowser");
    }

    public void actionPerformed(ActionEvent actionevent)
    {
        int i = actionevent.getModifiers() & 1;
        boolean flag = i != 0;
        String s = actionevent.getActionCommand();
        if(!s.equals("") && !s.startsWith("entered"))
        {
            if(s.equals("quit"))
                if(!frame.confirmExit(true))
                {
                    return;
                } else
                {
                    frame.saveStateToProperties();
                    frame.setVisible(false);
                    HJWindowManager.getHJWindowManager().quit();
                    return;
                }
            if(s.equals("forward"))
            {
                frame.nextDocument();
                return;
            }
            if(s.equals("back"))
            {
                frame.previousDocument();
                return;
            }
            if(s.equals("clonewin"))
            {
                try
                {
                    URL url = null;
                    if(properties.getBoolean("hotjava.gohome"))
                        url = new URL(properties.getProperty("user.homepage"));
                    HJWindowManager.getHJWindowManager().cloneFrame(frame, url);
                    return;
                }
                catch(MalformedURLException _ex)
                {
                    HJWindowManager.getHJWindowManager().cloneFrame(frame, null);
                }
                return;
            }
            if(s.equals("home"))
                try
                {
                    URL url1 = new URL(properties.getProperty("user.homepage"));
                    if(flag)
                    {
                        HJWindowManager.getHJWindowManager().cloneFrame(frame, url1);
                        return;
                    } else
                    {
                        frame.getHTMLBrowsable().setDocumentURL(url1);
                        return;
                    }
                }
                catch(MalformedURLException _ex)
                {
                    return;
                }
            if(s.equals("searchinternet"))
                try
                {
                    URL url2 = new URL(properties.getProperty("hotjava.searchinternet.html"));
                    frame.getHTMLBrowsable().setDocumentURL(url2);
                    return;
                }
                catch(MalformedURLException _ex)
                {
                    return;
                }
            if(s.equals("logopage"))
                try
                {
                    URL url3 = new URL(properties.getProperty("hotjava.activitymonitor.html"));
                    frame.getHTMLBrowsable().setDocumentURL(url3);
                    return;
                }
                catch(MalformedURLException _ex)
                {
                    return;
                }
            if(s.equals("reload"))
            {
                if(flag)
                {
                    try
                    {
                        HTMLBrowsable htmlbrowsable = frame.getHTMLBrowsable();
                        String s8 = htmlbrowsable.getDocumentString();
                        if(s8.indexOf("doc:/lib/hotjava/generated") < 0)
                        {
                            URL url12 = new URL(s8);
                            HJWindowManager.getHJWindowManager().cloneFrame(frame, url12);
                            return;
                        }
                    }
                    catch(MalformedURLException _ex)
                    {
                        HJWindowManager.getHJWindowManager().cloneFrame(frame, null);
                        return;
                    }
                } else
                {
                    String s1 = frame.getHTMLBrowsable().getDocumentString();
                    if(s1.indexOf("doc:/lib/hotjava/generated") < 0)
                        frame.getHTMLBrowsable().reload();
                }
                return;
            }
            if(s.equals("sethomepage"))
            {
                String s2 = frame.getHTMLBrowsable().getDocumentString();
                properties.put("user.homepage", s2);
                return;
            }
            if(s.equals("showconsole"))
            {
                String s3 = properties.getProperty("hotjava.consoleClass");
                if(s3 != null)
                    try
                    {
                        Class.forName(s3).newInstance();
                        return;
                    }
                    catch(Throwable _ex)
                    {
                        return;
                    }
            } else
            if(s.equals("copy"))
            {
                String s4 = frame.getSelection();
                if(s4 != null)
                {
                    StringSelection stringselection = new StringSelection(s4);
                    frame.getToolkit().getSystemClipboard().setContents(stringselection, null);
                    return;
                }
            } else
            if(s.equals("paste"))
            {
                String s5 = null;
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                try
                {
                    Transferable transferable = clipboard.getContents(this);
                    s5 = (String)transferable.getTransferData(DataFlavor.stringFlavor);
                }
                catch(Exception _ex) { }
                if(s5 != null)
                {
                    frame.paste(s5);
                    return;
                }
            } else
            {
                if(s.startsWith("print"))
                {
                    HTMLBrowsable htmlbrowsable1 = frame.getHTMLBrowsable();
                    htmlbrowsable1.print(s.endsWith("frame"));
                    return;
                }
                if(s.equals("hotlistframe"))
                {
                    HotListFrame hotlistframe = HotListFrame.getHotListFrame(frame);
                    String s9 = properties.getProperty("hotlist.defaultlist");
                    hotlistframe.openList(s9);
                    return;
                }
                if(s.equals("openlist"))
                {
                    HotListFrame hotlistframe1 = HotListFrame.getHotListFrame(frame);
                    UserMenuItem usermenuitem = (UserMenuItem)actionevent.getSource();
                    String s10 = usermenuitem.getLabel();
                    int j = s10.indexOf("...");
                    if(j != -1)
                        s10 = s10.substring(0, j);
                    hotlistframe1.openList(s10);
                    return;
                }
                if(s.equals("closewin"))
                    if(!frame.confirmExit(false))
                    {
                        return;
                    } else
                    {
                        frame.closeBrowser();
                        return;
                    }
                if(s.equals("stop"))
                {
                    frame.stopLoading();
                    return;
                }
                if(s.equals("showfinddialog"))
                {
                    frame.findPrompt();
                    return;
                }
                if(s.equals("showgotoplacedialog"))
                {
                    HTMLBrowsable htmlbrowsable2 = frame.getHTMLBrowsable();
                    if(htmlbrowsable2.getDocumentString() == null)
                        return;
                    if(gotoDialog == null)
                        gotoDialog = new GoToPlaceDialog("gotoplacedialog", frame, properties);
                    gotoDialog.setVisible(true);
                    return;
                }
                if(s.startsWith("addgoto"))
                {
                    HTMLBrowsable htmlbrowsable3 = frame.getHTMLBrowsable();
                    URL url8 = htmlbrowsable3.getCurrentDocument().documentURL;
                    String s11 = htmlbrowsable3.getDocumentTitle();
                    String s12 = s;
                    if(s.length() > "addgoto ".length())
                        s12 = s.substring("addgoto ".length());
                    HotList hotlist1 = HotList.getHotList();
                    hotlist1.addCurrent(url8, s11, s12);
                    hotlist1.exportHTMLFile();
                    return;
                }
                if(s.startsWith("clonego "))
                {
                    try
                    {
                        URL url4 = new URL(s.substring("clonego ".length()));
                        HJWindowManager.getHJWindowManager().cloneFrame(frame, url4);
                        return;
                    }
                    catch(MalformedURLException malformedurlexception)
                    {
                        malformedurlexception.printStackTrace();
                    }
                    return;
                }
                if(s.startsWith("remember "))
                {
                    try
                    {
                        String s6 = s.substring("remember ".length());
                        URL url9 = new URL(s6);
                        HotList hotlist = HotList.getHotList();
                        hotlist.addCurrent(url9, s6, null);
                        hotlist.exportHTMLFile();
                        return;
                    }
                    catch(MalformedURLException malformedurlexception1)
                    {
                        malformedurlexception1.printStackTrace();
                    }
                    return;
                }
                if(s.startsWith("savefile"))
                {
                    HTMLBrowsable htmlbrowsable4 = frame.getHTMLBrowsable();
                    URL url10 = htmlbrowsable4.getCurrentDocument().documentURL;
                    saveSource(url10, s, false);
                    return;
                }
                if(s.startsWith("saveframe"))
                {
                    HTMLBrowsable htmlbrowsable5 = frame.getHTMLBrowsable();
                    URL url11 = htmlbrowsable5.getFrameURL();
                    saveSource(url11, s, true);
                    return;
                }
                if(s.startsWith("clonego "))
                {
                    try
                    {
                        URL url5 = new URL(s.substring("clonego ".length()));
                        HJWindowManager.getHJWindowManager().cloneFrame(frame, url5);
                        return;
                    }
                    catch(MalformedURLException malformedurlexception2)
                    {
                        malformedurlexception2.printStackTrace();
                    }
                    return;
                }
                if(s.startsWith("go!"))
                    try
                    {
                        URL url6 = new URL(s.substring(s.indexOf(' ') + 1));
                        HJWindowManager hjwindowmanager = HJWindowManager.getHJWindowManager();
                        HJFrame hjframe = hjwindowmanager.createNoDecorFrame(url6, hjwindowmanager.getLastFocusHolder());
                        try
                        {
                            if(s.charAt(3) != ' ')
                            {
                                int k = Integer.parseInt(s.substring(3, s.indexOf(',')));
                                int l = Integer.parseInt(s.substring(s.indexOf(',') + 1, s.indexOf(' ')));
                                hjframe.setSize(k, l);
                            }
                        }
                        catch(Exception exception1)
                        {
                            exception1.printStackTrace();
                        }
                        hjframe.setServiceFrame(true);
                        hjframe.show();
                        return;
                    }
                    catch(MalformedURLException _ex)
                    {
                        return;
                    }
                if(s.startsWith("go "))
                    try
                    {
                        URL url7 = new URL(s.substring(3));
                        if(flag)
                        {
                            HJWindowManager.getHJWindowManager().cloneFrame(frame, url7);
                            return;
                        } else
                        {
                            frame.getHTMLBrowsable().setDocumentURL(url7);
                            return;
                        }
                    }
                    catch(MalformedURLException _ex)
                    {
                        return;
                    }
                if(s.equals("showhistory"))
                {
                    showHistory();
                    return;
                }
                if(s.equalsIgnoreCase("removemenubar"))
                {
                    properties.put("hotjava.useMenuBar", "false");
                    frame.setShowMenus(false);
                    return;
                }
                if(s.equalsIgnoreCase("restoremenubar"))
                {
                    properties.put("hotjava.useMenuBar", "true");
                    frame.setShowMenus(true);
                    return;
                }
                if(s.equals("flush"))
                {
                    String s7 = "hotjava.cache.flush.start.msg";
                    frame.showStatus(properties.getProperty(s7));
                    HTMLBrowsable htmlbrowsable8 = frame.getHTMLBrowsable();
                    htmlbrowsable8.clearImageCache();
                    s7 = "hotjava.cache.flush.done.msg";
                    frame.showStatus(properties.getProperty(s7));
                    return;
                }
                if(s.equals("source"))
                {
                    HTMLBrowsable htmlbrowsable6 = frame.getHTMLBrowsable();
                    BufferedReader bufferedreader = new BufferedReader(htmlbrowsable6.getDocumentSource());
                    showSource(bufferedreader, htmlbrowsable6.getDocumentURL());
                    return;
                }
                if(s.equals("framesource"))
                {
                    HTMLBrowsable htmlbrowsable7 = frame.getHTMLBrowsable();
                    BufferedReader bufferedreader1 = new BufferedReader(htmlbrowsable7.getFrameSource());
                    showSource(bufferedreader1, htmlbrowsable7.getFrameURL());
                    return;
                }
                if(s.startsWith("getSingleton"))
                {
                    try
                    {
                        Class.forName(s.substring(14)).getMethod("getSingleton", null).invoke(null, null);
                        return;
                    }
                    catch(Exception exception)
                    {
                        exception.printStackTrace();
                    }
                    return;
                }
            }
        }
    }

    private void showHistory()
    {
        properties.getProperty("history.title", "History");
        String s = properties.getProperty("history.prefix", "<p>");
        String s1 = properties.getProperty("history.prefix", "</p>");
        String s2 = properties.getProperty("history.pageprefix", "");
        for(int i = 0; i < frame.getHistSize(); i++)
        {
            String s3 = frame.getHistURL(i).toString();
            if(s3.indexOf("doc:/lib/hotjava/generated") == -1)
            {
                s2 = s2 + s;
                s2 = s2 + "<a href=\"";
                s2 = s2 + frame.getHistURL(i).toString();
                s2 = s2 + "\">";
                s2 = s2 + frame.getHistTitle(i);
                s2 = s2 + s1;
            }
        }

        s2 = s2 + "</body></html>";
        frame.getHTMLBrowsable().setDocumentSource(new StringReader(s2));
    }

    public void windowClosing(WindowEvent windowevent)
    {
        windowevent.getWindow().dispose();
    }

    public void windowClosed(WindowEvent windowevent)
    {
    }

    public void windowOpened(WindowEvent windowevent)
    {
    }

    public void windowIconified(WindowEvent windowevent)
    {
    }

    public void windowDeiconified(WindowEvent windowevent)
    {
    }

    public void windowActivated(WindowEvent windowevent)
    {
    }

    public void windowDeactivated(WindowEvent windowevent)
    {
    }

    public void itemStateChanged(ItemEvent itemevent)
    {
        CheckboxMenuItem checkboxmenuitem = (CheckboxMenuItem)itemevent.getSource();
        if(checkboxmenuitem == null)
            return;
        String s = checkboxmenuitem.getName();
        if(s.startsWith("charset "))
        {
            HTMLBrowsable htmlbrowsable = frame.getHTMLBrowsable();
            String s5 = "hotjava.charset";
            String s6 = "charset " + properties.getProperty(s5);
            if(!s.equals(s6))
            {
                CheckboxMenuItem checkboxmenuitem1 = (CheckboxMenuItem)frame.getMenuItem(s6);
                if(checkboxmenuitem1 != null)
                    checkboxmenuitem1.setState(false);
                htmlbrowsable.setCharset(s.substring("charset ".length()));
                htmlbrowsable.reload();
            }
            return;
        }
        if(s.equals("delayapplets"))
        {
            String s1 = checkboxmenuitem.getState() ? "true" : "false";
            properties.put("delayAppletLoading", s1);
            return;
        }
        if(s.equals("delayimages"))
        {
            String s2 = checkboxmenuitem.getState() ? "true" : "false";
            properties.put("delayImageLoading", s2);
            return;
        }
        if(s.equals("displayBackgroundImages"))
        {
            String s3 = checkboxmenuitem.getState() ? "true" : "false";
            properties.put("displayBackgroundImages", s3);
            HJFrame ahjframe[] = HJWindowManager.getHJWindowManager().getAllFrames();
            for(int i = 0; i < ahjframe.length; i++)
                ahjframe[i].reset();

            return;
        }
        if(s.equals("ignoreCharsetDirective"))
        {
            String s4 = checkboxmenuitem.getState() ? "true" : "false";
            if(s4 != null)
            {
                properties.put("ignoreCharsetDirective", s4);
                HJWindowManager.getHJWindowManager().getLastFocusHolder().getHTMLBrowsable().reload();
            }
            return;
        } else
        {
            return;
        }
    }

    private Reader prepareSourceString(URL url, Reader reader)
    {
        BufferedReader bufferedreader = new BufferedReader(reader);
        CharArrayWriter chararraywriter = new CharArrayWriter();
        String s = "<html><head><title>Source of: " + url + "</title></head>";
        s = s + "<body><pre>\n";
        chararraywriter.write(s, 0, s.length());
        char ac[] = new char[256];
        char ac1[] = new char[1280];
        boolean flag = false;
        try
        {
            for(int j = bufferedreader.read(ac, 0, 256); j != -1; j = bufferedreader.read(ac, 0, 256))
            {
                int i = 0;
                for(int k = 0; k < j; k++)
                    switch(ac[k])
                    {
                    case 38: // '&'
                        ac1[i++] = '&';
                        ac1[i++] = '#';
                        ac1[i++] = '3';
                        ac1[i++] = '8';
                        ac1[i++] = ';';
                        break;

                    case 60: // '<'
                        ac1[i++] = '&';
                        ac1[i++] = 'l';
                        ac1[i++] = 't';
                        ac1[i++] = ';';
                        break;

                    case 62: // '>'
                        ac1[i++] = '&';
                        ac1[i++] = 'g';
                        ac1[i++] = 't';
                        ac1[i++] = ';';
                        break;

                    default:
                        ac1[i++] = ac[k];
                        break;
                    }

                chararraywriter.write(ac1, 0, i);
            }

        }
        catch(IOException ioexception)
        {
            ioexception.printStackTrace();
        }
        s = "</pre></body></html>";
        chararraywriter.write(s, 0, s.length());
        return new CharArrayReader(chararraywriter.toCharArray());
    }

    private void showSource(BufferedReader bufferedreader, URL url)
    {
        Object obj = null;
        if(bufferedreader != null && url != null)
        {
            HJWindowManager hjwindowmanager = HJWindowManager.getHJWindowManager();
            HJFrame hjframe = hjwindowmanager.createNoDecorFrame(null, hjwindowmanager.getLastFocusHolder());
            hjframe.show();
            Reader reader = prepareSourceString(url, bufferedreader);
            hjframe.getHTMLBrowsable().setDocumentSource(reader);
        }
    }

    private void saveSource(URL url, String s, boolean flag)
    {
        UserFileDialog userfiledialog = new UserFileDialog(frame, "hotjava.savedialog", 1);
        userfiledialog.setDirectory(fileDialogDirectory);
        URL url1 = url;
        boolean flag1 = s.length() > "savefile ".length();
        try
        {
            if(flag1)
                url1 = new URL(s.substring("savefile ".length()));
        }
        catch(MalformedURLException malformedurlexception)
        {
            malformedurlexception.printStackTrace();
        }
        String s1 = url1.getFile();
        String s2 = "index.html";
        if(s1 != null)
        {
            int i = s1.lastIndexOf("/");
            if(i < s1.length() - 1)
                s2 = s1.substring(i + 1);
            int j = s2.indexOf("?");
            if(j != -1)
                s2 = s2.substring(0, j);
            if(s2.indexOf(".") == -1)
                s2 = s2 + ".html";
        }
        userfiledialog.setFile(s2);
        userfiledialog.setVisible(true);
        frame.showStatus("");
        String s3 = userfiledialog.getFile();
        if(s3 == null || s3.equals(""))
            return;
        String s4 = userfiledialog.getDirectory();
        boolean flag2 = userfiledialog.providesSaveConfirmation();
        userfiledialog.dispose();
        fileDialogDirectory = s4;
        File file = new File(s4, s3);
        if(file.exists() && !flag2)
        {
            String s5 = "overwrite.file";
            ConfirmDialog confirmdialog = new ConfirmDialog(s5, frame);
            confirmdialog.setVisible(true);
            if(confirmdialog.getAnswer() == 0)
                return;
        }
        String s6 = file.getAbsolutePath();
        String s7 = "progressDialog.title.label";
        String s8 = "Saving to: ";
        String s9 = properties.getProperty(s7, s8) + s6;
        frame.save(url1, s6, s9, flag1, flag);
    }

    private HJFrame frame;
    private static final String propName = "hotjava";
    private String fileDialogDirectory;
    private HJBProperties properties;
    GoToPlaceDialog gotoDialog;
}
