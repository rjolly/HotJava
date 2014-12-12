// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ProxiesPanel.java

package sunw.hotjava.applets;

import java.awt.*;
import java.awt.event.KeyListener;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Hashtable;
import java.util.StringTokenizer;
import sun.net.ftp.FtpClient;
import sun.net.www.http.HttpClient;
import sunw.hotjava.HJWindowManager;
import sunw.hotjava.applet.PrefsPanel;
import sunw.hotjava.misc.HJBProperties;
import sunw.hotjava.ui.ConfirmDialog;
import sunw.hotjava.ui.MultiLineLabel;

class ProxiesPanel extends Panel
    implements PrefsPanel
{

    private void addLine(int i, GridBagConstraints gridbagconstraints, String s, int j, String s1, String s2)
    {
        gridbagconstraints.insets = new Insets(5, 10, 5, 0);
        gridbagconstraints.gridy = i;
        gridbagconstraints.gridwidth = 1;
        gridbagconstraints.gridx = 0;
        MultiLineLabel multilinelabel = new MultiLineLabel(props.getProperty(propname + "." + s) + "     ");
        multilinelabel.setMarginWidth(0);
        multilinelabel.setFont(regular);
        add(multilinelabel, gridbagconstraints);
        gridbagconstraints.gridx = 1;
        gridbagconstraints.gridwidth = 5;
        fields[j] = new TextField(30);
        fields[j].setFont(regular);
        add(fields[j], gridbagconstraints);
        names[j] = s1;
        gridbagconstraints.gridx = 6;
        gridbagconstraints.gridwidth = 1;
        MultiLineLabel multilinelabel1 = new MultiLineLabel("        " + props.getProperty(propname + ".port.label"));
        multilinelabel1.setFont(regular);
        add(multilinelabel1, gridbagconstraints);
        gridbagconstraints.gridx = 7;
        gridbagconstraints.gridwidth = 1;
        gridbagconstraints.fill = 0;
        fields[j + 1] = new TextField(5);
        fields[j + 1].setFont(regular);
        add(fields[j + 1], gridbagconstraints);
        names[j + 1] = s2;
    }

    public ProxiesPanel(KeyListener keylistener1, String s, Font font, Font font1, Font font2)
    {
        fields = new TextField[10];
        names = new String[10];
        propname = "general.proxies.preferences";
        props = HJBProperties.getHJBProperties("hjbrowser");
        regular = font;
        regularBold = font1;
        headings = font2;
        keylistener = keylistener1;
        GridBagLayout gridbaglayout = new GridBagLayout();
        GridBagConstraints gridbagconstraints = new GridBagConstraints();
        setLayout(gridbaglayout);
        gridbagconstraints.gridheight = 1;
        gridbagconstraints.fill = 2;
        gridbagconstraints.insets = new Insets(10, 0, 0, 0);
        gridbagconstraints.gridx = 0;
        gridbagconstraints.gridy = 0;
        gridbagconstraints.gridwidth = 8;
        gridbagconstraints.anchor = 17;
        MultiLineLabel multilinelabel = new MultiLineLabel(s);
        multilinelabel.setFont(font2);
        multilinelabel.setMarginHeight(0);
        add(multilinelabel, gridbagconstraints);
        gridbagconstraints.gridy = 2;
        MultiLineLabel multilinelabel1 = new MultiLineLabel(props.getProperty(propname + ".lt1.label"));
        multilinelabel1.setFont(font);
        gridbagconstraints.insets = new Insets(10, 10, 0, 10);
        multilinelabel1.setMarginWidth(20);
        gridbagconstraints.gridwidth = 8;
        gridbagconstraints.fill = 0;
        add(multilinelabel1, gridbagconstraints);
        gridbagconstraints.insets = new Insets(10, 0, 0, 0);
        gridbagconstraints.weighty = 0.10000000000000001D;
        addLine(3, gridbagconstraints, "http.label", HTTP, "http.proxyHost", "http.proxyPort");
        addLine(4, gridbagconstraints, "security.label", SECURITY, "https.proxyHost", "https.proxyPort");
        addLine(5, gridbagconstraints, "ftp.label", FTP, "ftpProxyHost", "ftpProxyPort");
        addLine(6, gridbagconstraints, "gopher.label", GOPHER, "gopherProxyHost", "gopherProxyPort");
        gridbagconstraints.weighty = 1.0D;
        gridbagconstraints.gridx = 0;
        gridbagconstraints.gridy = 8;
        gridbagconstraints.gridwidth = 3;
        MultiLineLabel multilinelabel2 = new MultiLineLabel(props.getProperty(propname + ".bypass.label"));
        multilinelabel2.setFont(font);
        gridbagconstraints.insets = new Insets(0, 10, 5, 0);
        multilinelabel2.setMarginHeight(0);
        add(multilinelabel2, gridbagconstraints);
        gridbagconstraints.gridy = 9;
        gridbagconstraints.gridwidth = 8;
        MultiLineLabel multilinelabel3 = new MultiLineLabel(props.getProperty(propname + ".text"));
        gridbagconstraints.insets = new Insets(10, 10, 0, 10);
        gridbagconstraints.fill = 0;
        multilinelabel3.setMarginWidth(20);
        multilinelabel3.setFont(font);
        add(multilinelabel3, gridbagconstraints);
        gridbagconstraints.insets = new Insets(0, 10, 0, 10);
        gridbagconstraints.gridwidth = 8;
        bypassTA = new TextArea(3, 25);
        gridbagconstraints.gridy = 10;
        gridbagconstraints.fill = 2;
        add(bypassTA, gridbagconstraints);
        gridbagconstraints.weighty = 0.10000000000000001D;
        gridbagconstraints.fill = 0;
        addLine(12, gridbagconstraints, "socks.label", SOCKS, "socksProxyHost", "socksProxyPort");
        for(int i = 0; i < fields.length; i++)
            fields[i].addKeyListener(keylistener1);

        init();
    }

    private void decodeInto(TextArea textarea)
    {
        StringBuffer stringbuffer = new StringBuffer("");
        String s = props.getProperty("http.nonProxyHosts");
        if(s != null)
        {
            StringTokenizer stringtokenizer = new StringTokenizer(s, "|", false);
            while(stringtokenizer.hasMoreTokens()) 
            {
                String s1 = stringtokenizer.nextToken().toLowerCase();
                if(s1.charAt(0) == '*')
                    s1 = s1.substring(1);
                else
                if(s1.equals("localhost"))
                    continue;
                stringbuffer.append(s1);
                stringbuffer.append("\n");
            }
        }
        textarea.setText(stringbuffer.toString());
    }

    private String encodeFrom(TextArea textarea)
    {
        String s = textarea.getText();
        StringBuffer stringbuffer = new StringBuffer();
        StringTokenizer stringtokenizer = new StringTokenizer(s, " \t\n\r,;|", false);
        Hashtable hashtable = new Hashtable();
        stringbuffer.append("localhost");
        hashtable.put("localhost", new Object());
        while(stringtokenizer.hasMoreTokens()) 
        {
            String s1 = stringtokenizer.nextToken().toLowerCase();
            if(!hashtable.containsKey(s1))
            {
                hashtable.put(s1, new Object());
                stringbuffer.append('|');
                if(s1.charAt(0) == '.')
                    stringbuffer.append('*');
                stringbuffer.append(s1);
            }
        }
        return stringbuffer.toString();
    }

    private Frame getFrame()
    {
        return HJWindowManager.getHJWindowManager().getLastFocusHolder();
    }

    private boolean checkForErrors(TextArea textarea)
    {
        String s = textarea.getText();
        for(StringTokenizer stringtokenizer = new StringTokenizer(s, " \t\n\r,;|", false); stringtokenizer.hasMoreTokens();)
        {
            String s1 = stringtokenizer.nextToken();
            if(!validToken(s1))
            {
                showError("dontproxy.error", s1);
                return false;
            }
        }

        return true;
    }

    private boolean validToken(String s)
    {
        for(int i = 0; i < s.length(); i++)
        {
            char c = s.charAt(i);
            if((c < 'a' || c > 'z') && (c < 'A' || c > 'Z') && (c < '0' || c > '9') && c != '.' && c != '-' && c != '_')
                return false;
        }

        return true;
    }

    private void init()
    {
        props = HJBProperties.getHJBProperties("beanPropertiesKey");
        for(int i = 0; i < fields.length; i++)
            if(fields[i] != null)
                fields[i].setText(props.getProperty(names[i], ""));

        decodeInto(bypassTA);
    }

    protected boolean validHost(String s)
    {
        if(s == null || s.length() == 0)
            return true;
        InetAddress inetaddress = null;
        try
        {
            inetaddress = InetAddress.getByName(s);
        }
        catch(UnknownHostException _ex)
        {
            return false;
        }
        return true;
    }

    protected boolean validPort(String s)
    {
        if(s == null || s.length() == 0)
            return true;
        int i;
        try
        {
            i = Integer.parseInt(s);
        }
        catch(NumberFormatException _ex)
        {
            return false;
        }
        return i >= 1;
    }

    private void showError(String s, String s1)
    {
        String as[] = {
            s1
        };
        (new ConfirmDialog(s, getFrame(), 1, as)).setVisible(true);
    }

    protected boolean validateHostAndPort(String s, String s1)
    {
        if(!validHost(s))
        {
            showError("badproxy.error", s);
            return false;
        }
        if(!validPort(s1))
        {
            showError("badport.error", s1);
            return false;
        } else
        {
            return true;
        }
    }

    public void stop()
    {
        for(int i = 0; i < fields.length; i++)
            fields[i].removeKeyListener(keylistener);

    }

    public int apply()
    {
        boolean flag = false;
        for(int i = 0; i < fields.length; i += 2)
            if(!validateHostAndPort(fields[i].getText(), fields[i + 1].getText()))
                return 0;

        if(!checkForErrors(bypassTA))
            return 0;
        for(int j = 0; j < fields.length; j++)
            flag |= updateProp(names[j], fields[j].getText());

        flag |= updateProp("http.nonProxyHosts", encodeFrom(bypassTA));
        if(flag)
        {
            HttpClient.resetProperties();
            if(props.isPropertyExists("ftpProxySet"))
                FtpClient.useFtpProxy = props.getBoolean("ftpProxySet");
            else
                FtpClient.useFtpProxy = true;
            FtpClient.ftpProxyHost = props.getProperty("ftpProxyHost");
            FtpClient.ftpProxyPort = props.getInteger("ftpProxyPort", 80);
        }
        return 1;
    }

    private boolean updateProp(String s, String s1)
    {
        String s2 = props.getProperty(s);
        if(!s1.equals(s2))
        {
            if(s1.length() == 0)
                props.remove(s);
            else
                props.put(s, s1);
            return true;
        } else
        {
            return false;
        }
    }

    private TextField fields[];
    private String names[];
    private static int HTTP;
    private static int HTTPPORT = 1;
    private static int FTP = 2;
    private static int FTPPORT = 3;
    private static int GOPHER = 4;
    private static int GOPHERPORT = 5;
    private static int SOCKS = 6;
    private static int SOCKSPORT = 7;
    private static int SECURITY = 8;
    private static int SECURITYPORT = 9;
    private KeyListener keylistener;
    private TextArea bypassTA;
    private HJBProperties props;
    private String propname;
    private Font regular;
    private Font regularBold;
    private Font headings;

}
