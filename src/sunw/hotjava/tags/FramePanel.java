// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   FramePanel.java

package sunw.hotjava.tags;

import java.awt.*;
import java.awt.event.*;
import java.io.StringReader;
import java.net.URL;
import sunw.hotjava.bean.HotJavaBrowserBean;
import sunw.hotjava.doc.*;
import sunw.hotjava.misc.RequestProcessor;
import sunw.html.Attributes;

// Referenced classes of package sunw.hotjava.tags:
//            FRAME, FrameSetPanel, Resizable, ResizeRequestEvent

public class FramePanel extends Panel
    implements DocPanel, MouseListener, Resizable
{
    class FramePanelJSInit extends sunw.hotjava.misc.RequestProcessor.Request
    {

        public void execute()
        {
            String s = Formatter.handleScript(docPanel.getFormatter(), script, referer, 0, null);
            if(s != null)
                docPanel.setDocumentSource(new StringReader(s));
            doc = docPanel.getDocument();
            finishInitialization(frameItem, border);
        }

        private String script;
        private Formatter jsFormatter;
        private String referer;
        private TagItem frameItem;
        private int border;

        public FramePanelJSInit(Formatter formatter, String s, String s1, TagItem tagitem, int i)
        {
            jsFormatter = formatter;
            script = s;
            referer = s1;
            frameItem = tagitem;
            border = i;
        }
    }


    FramePanel(Formatter formatter, TagItem tagitem, int i, Container container)
    {
        selected = false;
        borderWidth = 4;
        setLayout(null);
        parentFormatter = (DocumentFormatter)formatter;
        borderWidth = i;
        borderEvaluated = false;
        Attributes attributes = tagitem.getAttributes();
        frameTag = (FRAME)tagitem;
        URL url = frameTag.src;
        String s = getAttribute("scrolling", attributes, "auto");
        byte byte0;
        if(url != null)
            byte0 = 0;
        else
            byte0 = 2;
        if("yes".equalsIgnoreCase(s))
            byte0 = 1;
        else
        if("no".equalsIgnoreCase(s))
            byte0 = 2;
        else
        if("auto".equalsIgnoreCase(s))
            byte0 = 0;
        container.add(this);
        docPanel = new DocumentPanel(formatter.getDocFont(), byte0, this);
        docPanel.addMouseListener(this);
        add(docPanel);
        if(attributes != null)
            name = attributes.get("name");
        if(name != null)
            docPanel.setName(name);
        else
            docPanel.setName("XXUnnamedFramePanel" + count++);
        if(url == null)
        {
            String s1 = getAttribute("noresize", attributes, null);
            resizable = !"noresize".equalsIgnoreCase(s1);
            return;
        }
        Container container1 = container;
        do
        {
            if((container1 instanceof FrameSetPanel) && url.equals(((FrameSetPanel)container1).doc.getURL()))
            {
                url = null;
                return;
            }
            container1 = container1.getParent();
        } while(container1 != null);
        URL url1 = formatter.getDocument().getURL();
        if(url.toExternalForm().toLowerCase().startsWith("doc://unknown.protocol/javascript:"))
        {
            String s2 = url.toExternalForm().substring("doc://unknown.protocol/javascript:".length());
            docPanel.setDocumentSource(new StringReader("<html></html>"));
            RequestProcessor requestprocessor = RequestProcessor.getHJBeanQueue();
            FramePanelJSInit framepaneljsinit = new FramePanelJSInit(docPanel.getFormatter(), s2, url1 + " (FRAME SRC)", tagitem, i);
            requestprocessor.postRequest(framepaneljsinit);
            return;
        } else
        {
            boolean flag = ((FRAME)tagitem).isByReloaded();
            doc = DocumentCache.getDocument(url, url1, flag);
            doc.setReloaded(flag);
            docPanel.show(doc);
            finishInitialization(tagitem, i);
            return;
        }
    }

    private void finishInitialization(TagItem tagitem, int i)
    {
        Attributes attributes = tagitem.getAttributes();
        String s = getAttribute("noresize", attributes, null);
        resizable = !"noresize".equalsIgnoreCase(s);
        int j = getMarginValue("marginwidth", attributes, 20);
        int k = getMarginValue("marginheight", attributes, 10);
        docPanel.setMargins(j, k);
        addMouseListener(this);
        String s1 = attributes.get("frameborder");
        if(s1 != null)
        {
            if(s1.equalsIgnoreCase("no") || s1.equals("0"))
                borderWidth = 0;
            if((s1.equalsIgnoreCase("yes") || s1.equals("1")) && i == 0)
                borderWidth = 3;
        }
    }

    public FRAME getFrameTag()
    {
        return frameTag;
    }

    protected Document getDocument()
    {
        return doc;
    }

    public String getName()
    {
        return name;
    }

    public void setResizeListener(FrameSetPanel framesetpanel, Point point)
    {
        resizeListener = framesetpanel;
        gridLoc = point;
        if(!resizable && framesetpanel != null)
            framesetpanel.setNotResizable(point);
    }

    public void mousePressed(MouseEvent mouseevent)
    {
        boolean flag = false;
        int i = 0;
        int j = computeMouseEdge(mouseevent);
        if(j < 0)
            return;
        if(j == 2 && resizeListener != null)
        {
            flag = true;
            i = gridLoc.y;
        } else
        if(j == 3 && resizeListener != null)
        {
            flag = true;
            i = gridLoc.y + 1;
        } else
        if(j == 0 && resizeListener != null)
            i = gridLoc.x;
        else
        if(j == 1 && resizeListener != null)
            i = gridLoc.x + 1;
        else
            return;
        ResizeRequestEvent resizerequestevent = new ResizeRequestEvent(mouseevent.getComponent(), mouseevent.getPoint(), flag, i);
        if(resizeListener != null)
            resizeListener.handleResizeEvent(resizerequestevent);
    }

    public void mouseReleased(MouseEvent mouseevent)
    {
    }

    public void mouseClicked(MouseEvent mouseevent)
    {
    }

    public void mouseEntered(MouseEvent mouseevent)
    {
    }

    public void mouseExited(MouseEvent mouseevent)
    {
    }

    private int computeMouseEdge(MouseEvent mouseevent)
    {
        int i = mouseevent.getX();
        if(isCloseTo(i, 0))
            return 0;
        int j = mouseevent.getY();
        if(isCloseTo(j, 0))
            return 2;
        Dimension dimension = getSize();
        if(isCloseTo(i, dimension.width))
            return 1;
        return !isCloseTo(j, dimension.height) ? -1 : 3;
    }

    public boolean isCloseTo(int i, int j)
    {
        return Math.abs(i - j) <= BORDER_SLOP;
    }

    public void update(Graphics g)
    {
        paint(g);
    }

    public void paint(Graphics g)
    {
        if(shouldPaintBorder())
            paintBorder(g);
        super.paint(g);
    }

    private void paintBorder(Graphics g)
    {
        Dimension dimension = getSize();
        g.setColor(Color.gray);
        for(int i = 0; i < borderWidth; i++)
            g.draw3DRect(i, i, dimension.width - (2 * i + 1), dimension.height - (2 * i + 1), false);

        if(selected)
        {
            g.setColor(Color.black);
            int j = borderWidth - 1;
            g.drawRect(j - 1, j - 1, (dimension.width - 2 * j) + 1, (dimension.height - 2 * j) + 1);
            g.drawRect(j, j, dimension.width - (2 * j + 1), dimension.height - (2 * j + 1));
        }
    }

    private String getAttribute(String s, Attributes attributes, String s1)
    {
        String s2 = s1;
        if(attributes != null)
        {
            s2 = attributes.get(s);
            if(s2 == null)
                s2 = s1;
        }
        return s2;
    }

    private int getMarginValue(String s, Attributes attributes, int i)
    {
        String s1 = getAttribute(s, attributes, null);
        if(s1 != null)
        {
            try
            {
                return Integer.parseInt(s1);
            }
            catch(NumberFormatException _ex) { }
            if("o".equalsIgnoreCase(s1))
                return 0;
        }
        return i;
    }

    private boolean shouldPaintBorder()
    {
        if(!borderEvaluated)
        {
            shouldPaintBorder = decideBorderPaintability();
            borderEvaluated = true;
        }
        return shouldPaintBorder;
    }

    private boolean decideBorderPaintability()
    {
        Insets insets = getInsets();
        BORDER_SLOP = borderWidth;
        borderWidth = borderWidth - insets.left;
        return borderWidth > 0;
    }

    public void setBounds(int i, int j, int k, int l)
    {
        super.setBounds(i, j, k, l);
        Component component = getComponent(0);
        if(k != 0)
            component.setBounds(borderWidth, borderWidth, k - 2 * borderWidth, l - 2 * borderWidth);
    }

    public void setBounds(Rectangle rectangle)
    {
        setBounds(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
    }

    public void activateSubItems()
    {
        DocumentFormatter documentformatter = getFormatter();
        if(documentformatter != null)
            documentformatter.activateSubItems();
    }

    public void start()
    {
        DocumentFormatter documentformatter = getFormatter();
        if(documentformatter == null)
            return;
        Color color = documentformatter.getFormatterBackgroundColor();
        if(color != null)
            setBackground(color);
        documentformatter.start();
    }

    public void stop()
    {
        DocumentFormatter documentformatter = getFormatter();
        if(documentformatter != null)
            documentformatter.stop();
    }

    public void destroy()
    {
        if(docPanel != null)
            docPanel.destroy();
    }

    public void setObsolete(boolean flag)
    {
        if(docPanel != null)
            docPanel.setObsolete(flag);
    }

    public void interruptLoading()
    {
        DocumentFormatter documentformatter = getFormatter();
        if(documentformatter != null)
            documentformatter.interruptLoading();
    }

    public void notify(Document document, int i, int j, int k)
    {
        DocumentFormatter documentformatter = getFormatter();
        if(documentformatter != null)
            documentformatter.notify(document, i, j, k);
    }

    public void reformat()
    {
        DocumentFormatter documentformatter = getFormatter();
        if(documentformatter != null)
            documentformatter.reformat();
    }

    public int findYFor(int i)
    {
        DocumentFormatter documentformatter = getFormatter();
        if(documentformatter != null)
            return documentformatter.findYFor(i);
        else
            return 0;
    }

    public void reload()
    {
        docPanel.reload();
    }

    public void updateDocument()
    {
        if(doc != null)
        {
            if(doc.isExpired())
                reload();
            else
                getFormatter().dispatchDocumentEvent(1042, null);
            setSelected(true);
            docPanel.dispatchDocumentEvent(1041, this);
        }
    }

    public DocumentFormatter getFormatter()
    {
        return docPanel.getFormatter();
    }

    public void print(PrintJob printjob, HotJavaBrowserBean hotjavabrowserbean)
        throws DocBusyException
    {
        docPanel.print(printjob, hotjavabrowserbean);
    }

    public void removeNotify()
    {
        stop();
        docPanel.removeMouseListener(this);
        docPanel.removePanel(docPanel);
        super.removeNotify();
    }

    public void setSelected(boolean flag)
    {
        selected = flag;
        repaint();
    }

    public int find(String s, boolean flag)
    {
        DocumentFormatter documentformatter = docPanel.getFormatter();
        documentformatter.showSelection(false);
        documentformatter.select(currentPosition, currentPosition);
        if(docPanel.find(s, flag))
            return currentPosition = documentformatter.getSelectEnd();
        else
            return -1;
    }

    public DocumentPanel getDocumentPanel()
    {
        return docPanel;
    }

    public Formatter getParentFormatter()
    {
        return parentFormatter;
    }

    public int getFrameIndex()
    {
        return frameTag.getFrameIndex();
    }

    private int BORDER_SLOP;
    private static int count;
    private Document doc;
    private DocumentPanel docPanel;
    private boolean resizable;
    private boolean selected;
    private int currentPosition;
    private int borderWidth;
    private boolean borderEvaluated;
    private boolean shouldPaintBorder;
    private FrameSetPanel resizeListener;
    private Point gridLoc;
    private DocumentFormatter parentFormatter;
    private String name;
    private FRAME frameTag;




}
