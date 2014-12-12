// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   DocumentFormatterPanel.java

package sunw.hotjava.doc;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

// Referenced classes of package sunw.hotjava.doc:
//            DocumentFormatter, DocumentFormatterRef, DocumentPanel, Formatter, 
//            FormatterOwner

public class DocumentFormatterPanel extends Panel
    implements FormatterOwner
{

    public DocumentFormatterPanel()
    {
        completed = false;
        setLayout(null);
        KeyAdapter keyadapter = new KeyAdapter() {

            public void keyPressed(KeyEvent keyevent)
            {
                if(docPanel != null)
                {
                    docPanel.processKeyEvent(keyevent);
                    requestFocus();
                    return;
                } else
                {
                    return;
                }
            }

        }
;
        addKeyListener(keyadapter);
    }

    void setDocumentPanel(DocumentPanel documentpanel)
    {
        docPanel = documentpanel;
        width = height = 0;
    }

    public void paint(Graphics g)
    {
        DocumentFormatter documentformatter = docPanel.getFormatter();
        if(documentformatter != null)
        {
            if(documentformatter.getDocument() == null && (((Formatter) (documentformatter)).docWidth == 0 || ((Formatter) (documentformatter)).docHeight == 0))
                return;
            Point point = location();
            Dimension dimension = docPanel.size();
            int i = docPanel.getHorizontalInset(false);
            int j = docPanel.getVerticalInset(false);
            if(((Formatter) (documentformatter)).docWidth <= dimension.width)
                documentformatter.setDocumentX(0);
            else
            if((point.x + dimension.width) - i > ((Formatter) (documentformatter)).docWidth)
                documentformatter.setDocumentX((((Formatter) (documentformatter)).docWidth - dimension.width) + i);
            else
                documentformatter.setDocumentX(-point.x);
            if(((Formatter) (documentformatter)).docHeight <= dimension.height)
                documentformatter.setDocumentY(0);
            else
            if((point.y + dimension.height) - j > ((Formatter) (documentformatter)).docHeight)
                documentformatter.setDocumentY((((Formatter) (documentformatter)).docHeight - dimension.height) + j);
            else
                documentformatter.setDocumentY(-point.y);
            safeToPaint = true;
            documentformatter.paint(g, true);
        }
    }

    public void update(Graphics g)
    {
        if(!docPanel.isLoadingNext())
            paint(g);
    }

    public Dimension getPreferredSize()
    {
        return getSize();
    }

    public Formatter getFormatter()
    {
        if(docPanel.current != null)
            return docPanel.current.getFormatter();
        if(docPanel.next != null)
            return docPanel.next.getFormatter();
        else
            return null;
    }

    public boolean isCompleted()
    {
        return completed;
    }

    public boolean clearCompleted()
    {
        boolean flag = completed;
        completed = false;
        return flag;
    }

    void setCompleted(boolean flag)
    {
        completed = flag;
        if(flag && !isVisible())
        {
            safeToPaint = false;
            setVisible(true);
        }
    }

    public DocumentPanel getDocumentPanel()
    {
        return docPanel;
    }

    boolean isSafeToPaint()
    {
        return safeToPaint;
    }

    DocumentPanel docPanel;
    int width;
    int height;
    private boolean safeToPaint;
    private boolean completed;
}
