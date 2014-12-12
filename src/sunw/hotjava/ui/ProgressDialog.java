// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ProgressDialog.java

package sunw.hotjava.ui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.*;
import sunw.hotjava.misc.HJBProperties;
import sunw.hotjava.misc.StreamCopier;

// Referenced classes of package sunw.hotjava.ui:
//            ActivityBar, BPSIndicator, BarCanvas, BytesTransferredInd, 
//            UIHJButton, ValueBar, ValueUpdateable

public class ProgressDialog extends Frame
    implements Observer, ActionListener
{

    public ProgressDialog(Frame frame, String s, URL url, String s1)
    {
        super(s);
        bytesTransferred = -1L;
        properties = HJBProperties.getHJBProperties("hjbrowser");
        java.awt.Color color = properties.getColor("hotjava.background", null);
        if(color != null)
            setBackground(color);
        GridBagLayout gridbaglayout = new GridBagLayout();
        setLayout(gridbaglayout);
        GridBagConstraints gridbagconstraints = new GridBagConstraints();
        gridbagconstraints.gridx = 0;
        gridbagconstraints.anchor = 17;
        gridbagconstraints.weightx = 1.0D;
        String s2 = properties.getProperty("progressDialog.url.label", "Source:");
        addComponent(gridbaglayout, gridbagconstraints, new Label(s2 + " " + url));
        s2 = properties.getProperty("progressDialog.saveInto.label", "Destination:");
        addComponent(gridbaglayout, gridbagconstraints, new Label(s2 + " " + s1));
        gridbagconstraints.ipady = 2;
        bytesTransferredPrefix = properties.getProperty("progressDialog.transfer.label", "Bytes transferred:") + " ";
        bytesTransferredInd = new BytesTransferredInd();
        addComponent(gridbaglayout, gridbagconstraints, bytesTransferredInd);
        bpsInd = new BPSIndicator();
        addComponent(gridbaglayout, gridbagconstraints, bpsInd);
        gridbagconstraints.weighty = 1.0D;
        gridbagconstraints.anchor = 15;
        gridbagconstraints.insets = new Insets(0, 0, 5, 0);
        stopButton = new UIHJButton("progressDialog.stop", true, properties);
        stopButton.addActionListener(this);
        addComponent(gridbaglayout, gridbagconstraints, stopButton);
        pack();
        centerOnScreen(frame);
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        try
        {
            URL url1 = ClassLoader.getSystemResource("lib/images/hotjava.icon.gif");
            java.awt.Image image = toolkit.getImage(url1);
            setIconImage(image);
            return;
        }
        catch(Exception _ex)
        {
            return;
        }
    }

    private void addComponent(GridBagLayout gridbaglayout, GridBagConstraints gridbagconstraints, Component component)
    {
        gridbaglayout.setConstraints(component, gridbagconstraints);
        add(component);
    }

    private void centerOnScreen(Frame frame)
    {
        Point point = frame.getLocationOnScreen();
        Dimension dimension = frame.getSize();
        Dimension dimension1 = getSize();
        point.x += (dimension.width - dimension1.width) / 2;
        point.y += (dimension.height - dimension1.height) / 2;
        Dimension dimension2 = Toolkit.getDefaultToolkit().getScreenSize();
        if(point.x < 0 || point.y < 0 || point.x + dimension1.width > dimension2.width || point.y + dimension1.height > dimension2.height)
        {
            point.x = (dimension2.width - dimension1.width) / 2;
            point.y = (dimension2.height - dimension1.height) / 2;
        }
        setLocation(point.x, point.y);
    }

    public void start()
    {
        transferStartTime = System.currentTimeMillis();
        previousTime = transferStartTime;
    }

    private void updateDisplay(long l)
    {
        if(bytesTransferred != l)
        {
            bytesTransferred = l;
            bytesTransferredInd.updateBytesTransferred(l);
        }
        updateTransferSpeed(l);
    }

    private void updateTransferSpeed(long l)
    {
        long l1 = System.currentTimeMillis();
        double d = 0.0D;
        if(l1 > transferStartTime)
            d = ((double)l * 1000D) / (double)(l1 - transferStartTime);
        bpsInd.updateBPS((long)(d + 0.5D));
        validate();
    }

    static String convertToReadableValue(long l)
    {
        String s = "";
        if(l >= 0x100000L)
        {
            l = (l * 10L) / 0x100000L;
            return format1decimalPlace(l) + "M";
        }
        if(l >= 1024L)
        {
            l = (l * 10L) / 1024L;
            return format1decimalPlace(l) + "K";
        } else
        {
            return l + s;
        }
    }

    private static String format1decimalPlace(long l)
    {
        long l1 = l % 10L;
        l /= 10L;
        return l + "." + l1;
    }

    public synchronized void setTotalBytes(long l)
    {
        totalBytes = l;
        bytesTransferredInd.setTotalBytes(l);
        validate();
        show();
    }

    public void actionPerformed(ActionEvent actionevent)
    {
        if(actionevent.getSource() == stopButton)
        {
            copier.stop();
            dispose();
        }
    }

    public void update(Graphics g)
    {
        paint(g);
    }

    public void update(Observable observable, Object obj)
    {
        if(observable instanceof StreamCopier)
        {
            if(copier == null)
                copier = (StreamCopier)observable;
            long l = System.currentTimeMillis();
            int i = ((Integer)obj).intValue();
            if(i < 0)
                dispose();
            if(l - previousTime >= 100L)
            {
                previousTime = l;
                updateDisplay(i);
            }
        }
    }

    private static final long kSleepInterval = 100L;
    private static final int k1K = 1024;
    private static final int k1M = 0x100000;
    private StreamCopier copier;
    private String bytesTransferredPrefix;
    private Label bytesTransferredLabel;
    private BytesTransferredInd bytesTransferredInd;
    private BPSIndicator bpsInd;
    private UIHJButton stopButton;
    private long totalBytes;
    private long bytesTransferred;
    private long transferStartTime;
    private long previousTime;
    private Thread myThread;
    private HJBProperties properties;
}
