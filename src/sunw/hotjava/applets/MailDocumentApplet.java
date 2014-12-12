// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   MailDocumentApplet.java

package sunw.hotjava.applets;

import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.URL;
import java.text.DateFormat;
import java.util.*;
import sun.awt.VariableGridLayout;
import sun.io.ByteToCharConverter;
import sun.misc.BASE64Encoder;
import sun.misc.CharacterEncoder;
import sun.net.smtp.SmtpClient;
import sunw.hotjava.HJWindowManager;
import sunw.hotjava.bean.CurrentDocument;
import sunw.hotjava.misc.*;
import sunw.hotjava.ui.*;

// Referenced classes of package sunw.hotjava.applets:
//            HotJavaApplet

public class MailDocumentApplet extends HotJavaApplet
    implements ActionListener, KeyPressInterest
{

    public void init()
    {
        doc = HJWindowManager.getHJWindowManager().getPrevDocument();
        ekl = new EnterKeyListener(this);
        if(mimePreamble == null)
        {
            mimePreamble = props.getProperty("maildoc.mime.preamble");
            mimeEpilogue = props.getProperty("maildoc.mime.epilogue");
            boundaryPrefix = props.getProperty("maildoc.mime.boundary.prefix");
        }
        toParam = getParameter("to", "");
        subjectParam = getParameter("subject");
        setLayout(new BorderLayout());
        Panel panel = new Panel();
        VariableGridLayout variablegridlayout = new VariableGridLayout(1, 2);
        variablegridlayout.setColFraction(0, 0.14999999999999999D);
        variablegridlayout.setColFraction(1, 0.84999999999999998D);
        panel.setLayout(variablegridlayout);
        add("North", panel);
        Panel panel1 = new Panel();
        panel1.setLayout(new GridLayout(0, 1));
        panel.add(panel1);
        Panel panel2 = new Panel();
        panel2.setLayout(new GridLayout(0, 1));
        panel.add(panel2);
        header(panel1, "tolabel");
        panel2.add(toAddressField = new UserTextField("maildoc.headfield"));
        toAddressField.addActionListener(this);
        header(panel1, "fromlabel");
        panel2.add(fromAddressField = new UserTextField("maildoc.headfield"));
        fromAddressField.addActionListener(this);
        header(panel1, "subjectlabel");
        panel2.add(subjectField = new UserTextField("maildoc.headfield"));
        subjectField.addActionListener(this);
        header(panel1, "composearea.title");
        panel2.add(new Label(""));
        compositionArea = new UserTextArea("maildoc.composearea", props);
        add("Center", compositionArea);
        GridBagLayout gridbaglayout = new GridBagLayout();
        Panel panel3 = new Panel();
        panel3.setLayout(gridbaglayout);
        GridBagConstraints gridbagconstraints = new GridBagConstraints();
        gridbagconstraints.gridwidth = 2;
        gridbagconstraints.gridheight = 1;
        gridbagconstraints.gridx = 0;
        gridbagconstraints.gridy = 0;
        gridbagconstraints.fill = 2;
        add("South", panel3);
        panel = new Panel();
        panel.setLayout(new FlowLayout());
        panel.add(insertURLButton = new UIHJButton("maildoc.inserturl", props));
        insertURLButton.addActionListener(this);
        panel.add(insertDocToggle = new Checkbox(props.getProperty("maildoc.attach.text")));
        insertDocToggle.addKeyListener(ekl);
        gridbaglayout.setConstraints(panel, gridbagconstraints);
        panel3.add(panel);
        panel = new Panel();
        panel.setLayout(new FlowLayout(2, 5, 0));
        UIHJButtonGroup uihjbuttongroup = new UIHJButtonGroup(props);
        panel.add(sendButton = new UIHJButton("maildoc.send", props));
        panel.add(cancelButton = new UIHJButton("maildoc.cancel", props));
        uihjbuttongroup.addButtonToGroup(sendButton);
        uihjbuttongroup.addButtonToGroup(cancelButton);
        sendButton.addActionListener(this);
        cancelButton.addActionListener(this);
        gridbagconstraints.gridx = 0;
        gridbagconstraints.gridy = 1;
        gridbagconstraints.anchor = 13;
        gridbagconstraints.weightx = 1.0D;
        gridbaglayout.setConstraints(panel, gridbagconstraints);
        panel3.add(panel);
        fetchDocState();
        resetFields();
    }

    void resetFields()
    {
        didInsert = false;
        resetPreferences();
        toAddressField.setText(toParam);
        loadSubject();
        compositionArea.setText(getParameter("body", ""));
        insertDocToggle.setState(false);
    }

    void resetPreferences()
    {
        userMailAddress = getParameter("from", getMailAddress());
        fromAddressField.setText(userMailAddress);
    }

    public void start()
    {
        fetchDocState();
        resetPreferences();
        loadSubject();
        prepare();
    }

    private void fetchDocState()
    {
        URL url1 = doc.documentURL;
        if(url1 != null)
            url = url1.toExternalForm();
    }

    private void loadSubject()
    {
        if(subjectParam == null)
        {
            if(url != null)
            {
                subjectField.setText(doc.documentTitle);
                return;
            } else
            {
                subjectField.setText("");
                return;
            }
        } else
        {
            subjectField.setText(subjectParam);
            return;
        }
    }

    void header(Container container, String s)
    {
        container.add(new UserLabel("maildoc." + s));
    }

    void prepare()
    {
        sendButton.setEnabled(true);
        insertURLButton.setEnabled(canInsertURL());
    }

    boolean canInsert()
    {
        return doc != null && !didInsert;
    }

    boolean canInsertURL()
    {
        return url != null;
    }

    private Frame getFrame()
    {
        return HJWindowManager.getHJWindowManager().getLastFocusHolder();
    }

    public void processEnterEvent(KeyEvent keyevent)
    {
        actionPerformed(new ActionEvent(sendButton, 0, ""));
    }

    public void actionPerformed(ActionEvent actionevent)
    {
        Object obj = actionevent.getSource();
        if(obj == sendButton || (obj instanceof UserTextField))
        {
            sendMail();
            return;
        }
        if(obj == insertURLButton)
        {
            insertURL();
            prepare();
            return;
        }
        if(obj == cancelButton)
        {
            HJWindowManager.getHJWindowManager().closeFrame(HJWindowManager.getHJWindowManager().getLastFocusHolder());
            return;
        } else
        {
            return;
        }
    }

    void insertURL()
    {
        compositionArea.insert(url, compositionArea.getSelectionStart());
    }

    public void sendMail()
    {
        String s = toAddressField.getText().trim();
        if(s.length() == 0)
        {
            (new ConfirmDialog("maildoc.error", getFrame(), 1)).setVisible(true);
            return;
        }
        String s1 = fromAddressField.getText().trim();
        if(s1.length() == 0)
        {
            (new ConfirmDialog("maildoc.error1", getFrame(), 1)).setVisible(true);
            return;
        }
        String s2 = subjectField.getText().trim();
        String s3 = CharacterEncoding.getNetworkName(getCharSet());
        if(s3 == null)
            s3 = getCharSet();
        String s4 = props.getProperty("maildoc.headers.transfer.encoding", "quoted-printable");
        char c = s4.charAt(0);
        s4 = props.getProperty("maildoc.body.transfer.encoding", "quoted-printable");
        char c1 = s4.charAt(0);
        try
        {
            SmtpClient smtpclient = new SmtpClient();
            smtpclient.from(s1);
            smtpclient.to(s);
            PrintStream printstream = smtpclient.startMessage();
            if(s1.length() > 0)
                printstream.println("From: " + s1);
            printstream.println("To: " + s);
            if(s2.length() > 0)
            {
                String s5 = getCharSet();
                encodeHeader("Subject", s2, s5, c, printstream);
            }
            if(url != null)
                printstream.println("X-URL: " + url);
            printstream.println("Mime-version: 1.0");
            if(insertDocToggle.getState())
                insertMultipartHTMLMessage(printstream, s3, c1);
            else
                insertSimpleMessage(printstream, s3, c1);
            smtpclient.closeServer();
            HJWindowManager.getHJWindowManager().closeFrame(HJWindowManager.getHJWindowManager().getLastFocusHolder());
            return;
        }
        catch(IOException ioexception)
        {
            ioexception.printStackTrace();
            showStatus(props.getPropertyReplace("mailto.error.message", ioexception.toString()));
            return;
        }
        catch(Exception exception)
        {
            exception.printStackTrace();
            showStatus(props.getPropertyReplace("mailto.error.message", exception.toString()));
            return;
        }
    }

    private String getBoundaryString(Date date)
    {
        DateFormat dateformat = DateFormat.getDateTimeInstance(2, 2, Locale.US);
        String s = dateformat.format(date);
        s = s.replace(' ', '_');
        s = s.replace(':', '_');
        return boundaryPrefix + s;
    }

    private void insertDoc(PrintStream printstream, String s, char c)
        throws IOException
    {
        StringWriter stringwriter = new StringWriter();
        PrintWriter printwriter = new PrintWriter(stringwriter);
        char ac[] = new char[4096];
        for(int i = -1; (i = doc.documentSource.read(ac, 0, 4096)) != -1;)
            printwriter.write(ac, 0, i);

        printwriter.flush();
        printwriter.close();
        printstream.println("Content-type: text/html; charset=" + s + "; name=\"" + doc.documentURL.getFile() + "\"");
        transferEncodeContent(stringwriter.toString(), printstream, c);
    }

    private void insertSimpleMessage(PrintStream printstream, String s, char c)
        throws IOException
    {
        printstream.println("Content-type: text/plain; charset=" + s);
        transferEncodeContent(compositionArea.getText(), printstream, c);
    }

    private void insertMultipartHTMLMessage(PrintStream printstream, String s, char c)
        throws IOException
    {
        String s1 = getBoundaryString(new Date());
        printstream.println("Content-type: multipart/mixed; boundary=" + s1);
        s1 = "--" + s1;
        printstream.println();
        printstream.println(mimePreamble);
        printstream.println();
        printstream.println(s1);
        insertSimpleMessage(printstream, s, c);
        printstream.println();
        printstream.println(s1);
        insertDoc(printstream, s, c);
        printstream.println(s1 + "--");
        printstream.println();
        printstream.println(mimeEpilogue);
        printstream.println();
    }

    private void transferEncodeContent(String s, PrintStream printstream, char c)
        throws IOException
    {
        String s1 = null;
        if(!s.endsWith("\n"))
            s = s + "\n";
        Object obj;
        switch(c)
        {
        case 81: // 'Q'
        case 113: // 'q'
            s1 = "quoted-printable";
            obj = new QPEncoder(false, false);
            break;

        case 66: // 'B'
        case 98: // 'b'
            s1 = "base64";
            obj = new BASE64Encoder();
            break;

        case 78: // 'N'
        case 110: // 'n'
            printstream.println();
            OutputStreamWriter outputstreamwriter = new OutputStreamWriter(printstream);
            outputstreamwriter.write(s, 0, s.length());
            outputstreamwriter.flush();
            return;

        default:
            s1 = "base64";
            obj = new BASE64Encoder();
            break;
        }
        if(s1 != null)
            printstream.println("Content-transfer-encoding: " + s1);
        printstream.println();
        byte abyte0[] = s.getBytes(getCharSet());
        ByteArrayInputStream bytearrayinputstream = new ByteArrayInputStream(abyte0);
        ((CharacterEncoder) (obj)).encodeBuffer(bytearrayinputstream, printstream);
        printstream.flush();
    }

    private void encodeHeader(String s, String s1, String s2, char c, PrintStream printstream)
        throws IOException
    {
        String s3 = CharacterEncoding.getNetworkName(s2);
        if(s3 == null)
            s3 = getCharSet();
        Object obj;
        switch(c)
        {
        case 81: // 'Q'
        case 113: // 'q'
            obj = new QPHeaderEncoder(s, s3);
            break;

        case 66: // 'B'
        case 98: // 'b'
            obj = new Base64HeaderEncoder(s, s3);
            break;

        case 78: // 'N'
        case 110: // 'n'
            obj = new VerbatimHeaderEncoder(s);
            break;

        default:
            obj = new VerbatimHeaderEncoder(s);
            break;
        }
        ((CharacterEncoder) (obj)).encodeBuffer(s1.getBytes(s2), printstream);
    }

    private String getCharSet()
    {
        String s = props.getProperty("maildoc.charset");
        if(s == null)
            s = props.getProperty("hotjava.charset", "8859_1");
        if(s.equals("JISAutoDetect"))
            s = "EUCJIS";
        try
        {
            ByteToCharConverter.getConverter(s);
        }
        catch(UnsupportedEncodingException _ex)
        {
            System.out.println("Charset " + s + " is not supported. Use 8859_1");
            s = "8859_1";
        }
        return s;
    }

    public MailDocumentApplet()
    {
        props = HJBProperties.getHJBProperties("hjbrowser");
    }

    static final String propName = "maildoc";
    static String mimePreamble = null;
    static String mimeEpilogue = null;
    static String boundaryPrefix = null;
    String userMailAddress;
    String toParam;
    String subjectParam;
    TextField fromAddressField;
    TextField toAddressField;
    TextField subjectField;
    TextArea compositionArea;
    UIHJButton sendButton;
    UIHJButton cancelButton;
    UIHJButton insertURLButton;
    EnterKeyListener ekl;
    Checkbox insertDocToggle;
    String url;
    boolean didInsert;
    HJBProperties props;
    CurrentDocument doc;

}
