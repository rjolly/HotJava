// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   DocumentStack.java

package sunw.hotjava.doc;

import java.awt.Component;
import java.awt.ScrollPane;
import java.io.PrintStream;
import java.net.URL;
import java.util.Vector;
import sunw.hotjava.bean.HotJavaBrowserBean;
import sunw.hotjava.misc.Globals;
import sunw.hotjava.misc.HJBProperties;

// Referenced classes of package sunw.hotjava.doc:
//            Document, DocumentEvent, DocumentFormatter, DocumentFormatterPresentation, 
//            DocumentFormatterRef, DocumentPanel, Formatter, DocFont

public class DocumentStack
{
    private class Node
    {

        DocumentPanel target;
        DocumentFormatterPresentation oldContents;
        DocumentFormatterPresentation newContents;
        DocumentFormatterRef parents[];
        boolean extViewer;

        Node()
        {
        }
    }


    public DocumentStack(HotJavaBrowserBean hotjavabrowserbean)
    {
        initCalled = false;
        nodes = new Vector();
        owner = hotjavabrowserbean;
    }

    private synchronized void init()
    {
        if(initCalled)
        {
            return;
        } else
        {
            initCalled = true;
            HJBProperties hjbproperties = HJBProperties.getHJBProperties("beanPropertiesKey");
            int i = hjbproperties.getInteger("hotjava.docstack.contents.depth", 10);
            setContentsDepth(i);
            i = hjbproperties.getInteger("hotjava.docstack.logical.depth", 100);
            setLogicalDepth(i);
            return;
        }
    }

    public synchronized void setContentsDepth(int i)
    {
        init();
        i = Math.max(i, 0) + 1;
        for(int j = purgeContentsThreshold; j >= i; j--)
        {
            enforceContentsThreshold(currentPos - j);
            enforceContentsThreshold(currentPos + j);
        }

        purgeContentsThreshold = i;
    }

    public int getContentsDepth()
    {
        init();
        return purgeContentsThreshold - 1;
    }

    public synchronized void setLogicalDepth(int i)
    {
        init();
        i = Math.max(i, 0) + 1;
        for(int j = purgeURLThreshold; j >= i; j--)
        {
            enforceURLThreshold(currentPos - purgeURLThreshold);
            enforceURLThreshold(currentPos + purgeURLThreshold);
        }

        purgeURLThreshold = i;
    }

    public synchronized void push(DocumentPanel documentpanel, DocumentFormatterRef documentformatterref)
    {
        init();
        DocumentFormatterRef documentformatterref1 = documentpanel.current;
        if(documentpanel.isLoadingNext())
            documentformatterref1 = documentpanel.next;
        if(documentformatterref1 == null)
            return;
        Node node = new Node();
        node.target = documentpanel;
        node.newContents = new DocumentFormatterPresentation(documentformatterref);
        node.oldContents = new DocumentFormatterPresentation(documentformatterref1);
        node.extViewer = false;
        if(!(documentpanel instanceof HotJavaBrowserBean))
        {
            Vector vector = new Vector();
            for(java.awt.Container container = documentpanel.getParent(); container != null; container = container.getParent())
            {
                if(!(container instanceof DocumentPanel))
                    continue;
                vector.addElement(((DocumentPanel)container).current);
                if(container instanceof HotJavaBrowserBean)
                    break;
            }

            node.parents = new DocumentFormatterRef[vector.size()];
            for(int j = 0; j < vector.size(); j++)
            {
                node.parents[j] = (DocumentFormatterRef)vector.elementAt(j);
                node.parents[j].addClient();
            }

        }
        node.oldContents.setScrollPosition(documentpanel.getScrollPosition());
        node.oldContents.getFormatterRef().offsetStored = true;
        node.oldContents.getFormatterRef().getFormatter().dispatchDocumentEvent(1040, null);
        pruneCurrentToTop();
        nodes.addElement(node);
        currentPos++;
        enforceContentsThreshold(currentPos - purgeContentsThreshold);
        enforceURLThreshold(currentPos - purgeURLThreshold);
        for(int i = 0; i < nodes.size(); i++)
        {
            Node node1 = (Node)nodes.elementAt(i);
            if(node1.extViewer)
            {
                node.oldContents = node1.oldContents;
                nodes.removeElementAt(i);
                currentPos--;
            }
        }

    }

    public synchronized void replace(DocumentPanel documentpanel, DocumentFormatterRef documentformatterref)
    {
        init();
        boolean flag = false;
        for(int i = currentPos - 1; !flag && i >= 0; i--)
        {
            Node node = (Node)nodes.elementAt(i);
            if(node.target == documentpanel)
            {
                node.newContents.flush();
                node.newContents = new DocumentFormatterPresentation(documentformatterref);
                for(i++; !flag && i < nodes.size(); i++)
                {
                    Node node1 = (Node)nodes.elementAt(i);
                    if(node1.target == documentpanel)
                    {
                        node1.oldContents.flush();
                        node1.oldContents = new DocumentFormatterPresentation(documentformatterref);
                        flag = true;
                    }
                }

                flag = true;
            }
        }

        documentpanel.show(documentformatterref);
    }

    public boolean canForward()
    {
        return currentPos < nodes.size();
    }

    public boolean canBack()
    {
        return currentPos > 0;
    }

    public synchronized void forward()
    {
        if(!canForward())
        {
            return;
        } else
        {
            Node node = (Node)nodes.elementAt(currentPos);
            currentPos++;
            node.oldContents.setScrollPosition(node.target.getScrollPosition());
            node.oldContents.getFormatterRef().offsetStored = true;
            node.oldContents.getFormatterRef().getFormatter().dispatchDocumentEvent(1040, null);
            node.newContents.getFormatterRef().updateDocument();
            node.newContents.showIn(node.target);
            enforceContentsThreshold(currentPos - purgeContentsThreshold);
            enforceURLThreshold(currentPos - purgeURLThreshold);
            return;
        }
    }

    public synchronized void back()
    {
        if(!canBack())
            return;
        currentPos--;
        Node node = (Node)nodes.elementAt(currentPos);
        node.newContents.setScrollPosition(node.target.getScrollPosition());
        node.newContents.getFormatterRef().offsetStored = true;
        node.newContents.getFormatterRef().getFormatter().getDocument().interruptOwnerWaitCompletion();
        node.newContents.getFormatterRef().getFormatter().dispatchDocumentEvent(1040, null);
        node.oldContents.getFormatterRef().updateDocument();
        node.oldContents.showIn(node.target);
        enforceContentsThreshold(currentPos + purgeContentsThreshold);
        enforceURLThreshold(currentPos + purgeURLThreshold);
        for(int i = 0; i < nodes.size(); i++)
        {
            Node node1 = (Node)nodes.elementAt(i);
            if(node1.extViewer)
                nodes.removeElementAt(i);
        }

        node.target.dispatchDocumentEvent(1006, node.oldContents.getFormatterRef().getDocument());
    }

    public int getSize()
    {
        return nodes.size();
    }

    public URL getURLAtPosition(int i)
    {
        URL url = null;
        if(currentPos == 0)
            owner.getDocumentURL();
        else
        if(canGo(i))
            if(i >= 0)
            {
                Node node = (Node)nodes.elementAt((currentPos + i) - 1);
                url = node.newContents.getURL();
            } else
            {
                Node node1 = (Node)nodes.elementAt(currentPos + i);
                url = node1.oldContents.getURL();
            }
        return url;
    }

    public boolean canGo(int i)
    {
        int j = currentPos + i;
        return j >= 0 && j <= nodes.size();
    }

    public void go(int i)
    {
        if(i == 0)
        {
            owner.forceRelayout();
            return;
        } else
        {
            goToURL(getURLAtPosition(i));
            return;
        }
    }

    private void goToURL(URL url)
    {
        if(url != null && owner != null)
            owner.handleLinkEventWithDefault(owner.getName(), url, owner.getName(), null);
    }

    public void go(String s)
    {
        if(currentPos == 0)
            return;
        boolean flag = false;
        URL url = getURLAtPosition(0);
        if(url.toExternalForm().indexOf(s) != -1)
            flag = true;
        boolean flag1 = true;
        boolean flag2 = true;
        for(int i = 1; !flag && (flag1 || flag2); i++)
        {
            if(flag1 && (flag1 = canGo(-i)))
            {
                url = getURLAtPosition(-i);
                if(url.toExternalForm().indexOf(s) != -1)
                {
                    flag = true;
                    break;
                }
            }
            if(!flag2 || !(flag2 = canGo(i)))
                continue;
            url = getURLAtPosition(i);
            if(url.toExternalForm().indexOf(s) == -1)
                continue;
            flag = true;
            break;
        }

        if(flag)
            goToURL(url);
    }

    public synchronized void flushDocumentsContainingCodebases(Vector vector)
    {
        int i = Math.max(0, (1 + currentPos) - purgeContentsThreshold);
        int j = Math.min(nodes.size() - 1, (currentPos + purgeContentsThreshold) - 1);
        for(int k = i; k <= j; k++)
        {
            Node node = (Node)nodes.elementAt(k);
            flushForCodebase(node.oldContents, vector);
            flushForCodebase(node.newContents, vector);
        }

    }

    private void flushForCodebase(DocumentFormatterPresentation documentformatterpresentation, Vector vector)
    {
        if(!documentformatterpresentation.getIsFlushed())
        {
            DocumentFormatter documentformatter = documentformatterpresentation.getFormatterRef().getFormatter();
            Vector vector1 = new Vector();
            documentformatter.getAppletPanels(vector1, true);
            for(int i = 0; i < vector1.size(); i++)
                if(vector.contains(vector1.elementAt(i)))
                {
                    documentformatterpresentation.flush();
                    return;
                }

        }
    }

    public void addAppletPanels(Vector vector)
    {
        Vector vector1 = new Vector();
        int i = Math.max(0, (1 + currentPos) - purgeContentsThreshold);
        int j = Math.min(nodes.size() - 1, (currentPos + purgeContentsThreshold) - 1);
        for(int k = i; k <= j; k++)
        {
            Node node = (Node)nodes.elementAt(k);
            if(!node.oldContents.getIsFlushed())
            {
                DocumentFormatter documentformatter = node.oldContents.getFormatterRef().getFormatter();
                documentformatter.getAppletPanels(vector1, true);
            }
            if(!node.newContents.getIsFlushed())
            {
                DocumentFormatter documentformatter1 = node.newContents.getFormatterRef().getFormatter();
                documentformatter1.getAppletPanels(vector1, true);
            }
            if(node.parents != null)
            {
                for(int i1 = 0; i1 < node.parents.length; i1++)
                {
                    DocumentFormatter documentformatter2 = node.parents[i1].getFormatter();
                    documentformatter2.getAppletPanels(vector1, true);
                }

            }
        }

        for(int l = 0; l < vector1.size(); l++)
        {
            Object obj = vector1.elementAt(l);
            if(!vector.contains(obj))
                vector.addElement(obj);
        }

    }

    public synchronized void flush(boolean flag)
    {
        if(flag)
        {
            for(int i = 0; i < nodes.size(); i++)
            {
                Node node = (Node)nodes.elementAt(i);
                flushNode(node);
            }

            nodes = new Vector();
            currentPos = 0;
            return;
        } else
        {
            int j = getContentsDepth();
            setContentsDepth(0);
            setContentsDepth(j);
            return;
        }
    }

    public void setDocFont(DocFont docfont)
    {
        int i = Math.max(0, (1 + currentPos) - purgeContentsThreshold);
        int j = Math.min(nodes.size() - 1, (currentPos + purgeContentsThreshold) - 1);
        for(int k = i; k <= j; k++)
        {
            Node node = (Node)nodes.elementAt(k);
            if(!node.oldContents.getIsFlushed())
            {
                DocumentFormatter documentformatter = node.oldContents.getFormatterRef().getFormatter();
                documentformatter.setDocFont(docfont);
            }
            if(!node.newContents.getIsFlushed())
            {
                DocumentFormatter documentformatter1 = node.newContents.getFormatterRef().getFormatter();
                documentformatter1.setDocFont(docfont);
            }
            if(node.parents != null)
            {
                for(int l = 0; l < node.parents.length; l++)
                {
                    DocumentFormatter documentformatter2 = node.parents[l].getFormatter();
                    documentformatter2.setDocFont(docfont);
                }

            }
        }

    }

    private void pruneCurrentToTop()
    {
        do
        {
            int i = nodes.size() - 1;
            if(i >= currentPos)
            {
                flushNode((Node)nodes.elementAt(i));
                nodes.removeElementAt(i);
            } else
            {
                return;
            }
        } while(true);
    }

    private void enforceContentsThreshold(int i)
    {
        if(i >= 0 && i < nodes.size())
        {
            Node node = (Node)nodes.elementAt(i);
            if(node.parents != null)
            {
                if(i + 1 < nodes.size())
                {
                    Node node1 = (Node)nodes.elementAt(i + 1);
                    if(node1.target == node.target)
                    {
                        DocumentFormatterPresentation documentformatterpresentation = node1.oldContents;
                        node1.oldContents = node.oldContents;
                        node.oldContents = documentformatterpresentation;
                    }
                }
                flushNode(node);
                nodes.removeElementAt(i);
                if(i <= currentPos)
                {
                    currentPos--;
                    return;
                }
            } else
            {
                flushNode(node);
            }
        }
    }

    private void enforceURLThreshold(int i)
    {
        if(i >= 0 && i < nodes.size())
        {
            Node node = (Node)nodes.elementAt(i);
            flushNode(node);
            nodes.removeElementAt(i);
            if(i <= currentPos)
                currentPos--;
        }
    }

    private void flushNode(Node node)
    {
        node.oldContents.flush();
        node.newContents.flush();
        if(node.parents != null)
        {
            for(int i = 0; i < node.parents.length; i++)
                node.parents[i].removeClient();

        }
        node.parents = null;
    }

    public void setViewer(URL url)
    {
        Object obj = null;
        for(int i = 0; i < nodes.size(); i++)
        {
            Node node = (Node)nodes.elementAt(i);
            if(node.newContents.getURL().equals(url))
                node.extViewer = true;
        }

    }

    public void printOutDocStack()
    {
        System.out.println("=======================================");
        System.out.println("=       Current document stack");
        System.out.println("=       Current position = " + currentPos);
        System.out.println("=");
        for(int i = 0; i < nodes.size(); i++)
        {
            Node node = (Node)nodes.elementAt(i);
            System.out.println("= Node " + i + ":");
            System.out.println("=    target: " + node.target.getName());
            System.out.println("=    old contents: " + node.oldContents.getURL());
            System.out.println("=        scroll position: " + node.oldContents.getScrollPosition());
            System.out.println("=        flushed: " + node.oldContents.getIsFlushed());
            System.out.println("=    new contents: " + node.newContents.getURL());
            System.out.println("=        scroll position: " + node.newContents.getScrollPosition());
            System.out.println("=        flushed: " + node.newContents.getIsFlushed());
            System.out.println("=    parents (length " + (node.parents != null ? node.parents.length : 0) + "):");
            if(node.parents != null)
            {
                for(int j = 0; j < node.parents.length; j++)
                {
                    System.out.print("=       " + node.parents[j].getURL());
                    DocumentFormatter documentformatter = node.parents[j].getFormatterIfNotRemoved();
                    if(documentformatter != null)
                        System.out.println(", " + ((DocumentPanel)documentformatter.getScrollPane()).getName());
                    else
                        System.out.println("");
                }

            }
            System.out.println("=    extViewer: " + node.extViewer);
            System.out.println("=");
        }

        System.out.println("=======================================");
    }

    private int purgeContentsThreshold;
    private int purgeURLThreshold;
    private boolean initCalled;
    private HotJavaBrowserBean owner;
    Vector nodes;
    private int currentPos;
}
