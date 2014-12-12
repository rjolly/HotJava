// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   HotJavaBrowserBeanBeanInfo.java

package sunw.hotjava.bean;

import java.beans.*;
import java.io.PrintStream;
import java.util.Vector;

public class HotJavaBrowserBeanBeanInfo extends SimpleBeanInfo
{

    public PropertyDescriptor[] getPropertyDescriptors()
    {
        try
        {
            Vector vector = new Vector();
            Class class1 = sunw.hotjava.bean.HotJavaBrowserBean.class;
            Object obj = new PropertyDescriptor("currentDocument", class1, "getCurrentDocument", "setCurrentDocument");
            ((PropertyDescriptor) (obj)).setBound(true);
            ((PropertyDescriptor) (obj)).setConstrained(true);
            vector.addElement(obj);
            obj = new PropertyDescriptor("documentStack", class1, "getDocumentStack", null);
            ((PropertyDescriptor) (obj)).setBound(true);
            vector.addElement(obj);
            obj = new PropertyDescriptor("URLPoolManager", class1, "getURLPoolManager", "setURLPoolManager");
            ((PropertyDescriptor) (obj)).setBound(false);
            vector.addElement(obj);
            obj = new PropertyDescriptor("cookiesManager", class1, "getCookiesManager", "setCookiesManager");
            ((PropertyDescriptor) (obj)).setBound(false);
            vector.addElement(obj);
            obj = new PropertyDescriptor("imageCache", class1, "getImageCache", "setImageCache");
            ((PropertyDescriptor) (obj)).setBound(false);
            vector.addElement(obj);
            obj = new PropertyDescriptor("opener", class1, "getOpener", "setOpener");
            ((PropertyDescriptor) (obj)).setBound(true);
            vector.addElement(obj);
            obj = new PropertyDescriptor("documentString", class1, "getDocumentString", "setDocumentString");
            ((PropertyDescriptor) (obj)).setBound(true);
            vector.addElement(obj);
            obj = new PropertyDescriptor("documentURL", class1, "getDocumentURL", "setDocumentURL");
            ((PropertyDescriptor) (obj)).setBound(true);
            vector.addElement(obj);
            obj = new PropertyDescriptor("documentSource", class1, "getDocumentSource", "setDocumentSource");
            ((PropertyDescriptor) (obj)).setBound(true);
            vector.addElement(obj);
            obj = new PropertyDescriptor("documentTitle", class1, "getDocumentTitle", null);
            ((PropertyDescriptor) (obj)).setBound(true);
            vector.addElement(obj);
            obj = new PropertyDescriptor("errorMessage", class1, "getErrorMessage", null);
            ((PropertyDescriptor) (obj)).setBound(true);
            vector.addElement(obj);
            obj = new PropertyDescriptor("statusMessage", class1, "getStatusMessage", "setStatusMessage");
            ((PropertyDescriptor) (obj)).setBound(true);
            vector.addElement(obj);
            obj = new PropertyDescriptor("defaultStatusMessage", class1, "getDefaultStatusMessage", null);
            ((PropertyDescriptor) (obj)).setBound(true);
            vector.addElement(obj);
            obj = new PropertyDescriptor("secureConnection", class1, "isSecureConnection", "setSecureConnection");
            ((PropertyDescriptor) (obj)).setBound(true);
            vector.addElement(obj);
            obj = new IndexedPropertyDescriptor("frameList", class1, "getFrameList", null, "getFrameList", null);
            ((PropertyDescriptor) (obj)).setBound(true);
            vector.addElement(obj);
            obj = new PropertyDescriptor("selection", class1, "getSelection", null);
            ((PropertyDescriptor) (obj)).setBound(true);
            vector.addElement(obj);
            obj = new PropertyDescriptor("charset", class1, "getCharset", "setCharset");
            ((PropertyDescriptor) (obj)).setBound(true);
            vector.addElement(obj);
            obj = new PropertyDescriptor("loadingProgress", class1, "getLoadingProgress", null);
            ((PropertyDescriptor) (obj)).setBound(true);
            vector.addElement(obj);
            obj = new PropertyDescriptor("indicatedElement", class1, "getIndicatedElement", null);
            ((PropertyDescriptor) (obj)).setBound(true);
            vector.addElement(obj);
            obj = new PropertyDescriptor("documentReloadable", class1, "isDocumentReloadable", null);
            ((PropertyDescriptor) (obj)).setBound(true);
            vector.addElement(obj);
            obj = new PropertyDescriptor("selfRef", class1, "getSelfRef", null);
            ((PropertyDescriptor) (obj)).setBound(false);
            vector.addElement(obj);
            obj = new PropertyDescriptor("appletManager", class1, "getAppletManager", "setAppletManager");
            ((PropertyDescriptor) (obj)).setBound(false);
            vector.addElement(obj);
            obj = new PropertyDescriptor("selector", class1, "getSelector", null);
            ((PropertyDescriptor) (obj)).setBound(true);
            vector.addElement(obj);
            PropertyDescriptor apropertydescriptor[] = new PropertyDescriptor[vector.size()];
            for(int i = 0; i < vector.size(); i++)
                apropertydescriptor[i] = (PropertyDescriptor)vector.elementAt(i);

            return apropertydescriptor;
        }
        catch(IntrospectionException introspectionexception)
        {
            throw new Error(introspectionexception.toString());
        }
    }

    public int getDefaultPropertyIndex()
    {
        return 0;
    }

    public EventSetDescriptor[] getEventSetDescriptors()
    {
        try
        {
            Class class1 = sunw.hotjava.bean.HotJavaBrowserBean.class;
            EventSetDescriptor eventsetdescriptor = new EventSetDescriptor(class1, "propertyChange", java.beans.PropertyChangeListener.class, "propertyChange");
            eventsetdescriptor.setDisplayName("bound property change");
            EventSetDescriptor eventsetdescriptor1 = new EventSetDescriptor(class1, "vetoableChange", java.beans.VetoableChangeListener.class, "vetoableChange");
            eventsetdescriptor1.setDisplayName("constrained property change");
            EventSetDescriptor eventsetdescriptor2 = new EventSetDescriptor(class1, "Browser History Command", sunw.hotjava.bean.BrowserHistoryListener.class, "executeHistoryCommand");
            eventsetdescriptor2.setDisplayName("browser History commands");
            EventSetDescriptor aeventsetdescriptor[] = {
                eventsetdescriptor1, eventsetdescriptor, eventsetdescriptor2
            };
            return aeventsetdescriptor;
        }
        catch(IntrospectionException introspectionexception)
        {
            throw new Error(introspectionexception.toString());
        }
    }

    public MethodDescriptor[] getMethodDescriptors()
    {
        Vector vector = new Vector();
        try
        {
            Class class1 = sunw.hotjava.bean.HotJavaBrowserBean.class;
            Class aclass[] = new Class[0];
            Class aclass1[] = {
                java.lang.String.class
            };
            Class aclass2[] = {
                java.net.URL.class
            };
            Class aclass3[] = {
                java.lang.Object.class
            };
            Class aclass4[] = {
                java.io.Reader.class
            };
            Class aclass5[] = {
                java.beans.PropertyChangeListener.class
            };
            Class aclass6[] = {
                java.beans.VetoableChangeListener.class
            };
            Class aclass7[] = {
                sunw.hotjava.bean.CurrentDocument.class
            };
            Class aclass8[] = {
                java.awt.Container.class, java.lang.String.class
            };
            Class aclass9[] = {
                Integer.TYPE
            };
            Class aclass10[] = {
                Boolean.TYPE
            };
            if(class$java$lang$String == null)
                class$java$lang$String = _mthclass$("java.lang.String");
            if(class$java$lang$Object == null)
                class$java$lang$Object = _mthclass$("java.lang.Object");
            Class aclass11[] = {
                Integer.TYPE, java.lang.String.class
            };
            Class aclass12[] = {
                java.awt.PrintJob.class
            };
            Class aclass13[] = {
                sunw.hotjava.bean.BrowserHistoryEvent.class
            };
            Class aclass14[] = {
                sunw.hotjava.bean.BrowserHistoryListener.class
            };
            Class aclass15[] = {
                java.io.FileOutputStream.class, java.util.Observer.class
            };
            Class aclass16[] = {
                java.net.URL.class, java.io.FileOutputStream.class, java.util.Observer.class
            };
            Class aclass17[] = {
                Integer.TYPE, java.lang.String.class, Boolean.TYPE
            };
            Class aclass18[] = {
                java.lang.String.class, sunw.hotjava.doc.Document.class, java.lang.String.class
            };
            Class aclass19[] = {
                sunw.hotjava.doc.DocPanel.class, java.net.URL.class, java.net.URL.class, Boolean.TYPE
            };
            Class aclass20[] = {
                sunw.hotjava.doc.DocPanel.class, sunw.hotjava.doc.Document.class, Boolean.TYPE
            };
            Class aclass21[] = {
                java.lang.String.class, java.lang.String.class, sunw.hotjava.doc.Document.class, Boolean.TYPE
            };
            Class aclass22[] = {
                java.lang.String.class, java.net.URL.class, java.lang.String.class, sunw.hotjava.doc.Document.class
            };
            Class aclass23[] = {
                sunw.hotjava.doc.DocFont.class
            };
            Class aclass24[] = {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, sunw.hotjava.doc.DocPanel.class, java.lang.String.class, java.lang.String.class
            };
            Class aclass25[] = {
                sunw.hotjava.bean.LinkListener.class
            };
            Class aclass26[] = {
                sunw.hotjava.bean.LinkEvent.class
            };
            Class aclass27[] = {
                java.io.Reader.class, sunw.hotjava.bean.FrameToken.class
            };
            Class aclass28[] = {
                sunw.hotjava.bean.FrameToken.class
            };
            Class aclass29[] = {
                sunw.hotjava.doc.DocumentEvent.class
            };
            Class aclass30[] = {
                sunw.hotjava.doc.DocumentEventSource.class
            };
            Class aclass31[] = {
                java.util.Observer.class, java.lang.Object.class
            };
            Class aclass32[] = {
                java.io.ObjectOutput.class
            };
            Class aclass33[] = {
                java.io.ObjectInput.class
            };
            Class aclass34[] = {
                sunw.hotjava.applet.AppletManager.class
            };
            Class aclass35[] = {
                sunw.hotjava.bean.CookieJarInterface.class
            };
            Class aclass36[] = {
                sunw.hotjava.bean.URLPooler.class
            };
            Class aclass37[] = {
                sunw.hotjava.bean.ImageCacher.class
            };
            java.lang.reflect.Method method = class1.getMethod("canForward", aclass);
            vector.addElement(new MethodDescriptor(method));
            method = class1.getMethod("forward", aclass);
            vector.addElement(new MethodDescriptor(method));
            method = class1.getMethod("back", aclass);
            vector.addElement(new MethodDescriptor(method));
            method = class1.getMethod("canBack", aclass);
            vector.addElement(new MethodDescriptor(method));
            method = class1.getMethod("writeExternal", aclass32);
            vector.addElement(new MethodDescriptor(method));
            method = class1.getMethod("readExternal", aclass33);
            vector.addElement(new MethodDescriptor(method));
            method = class1.getMethod("findDocumentPanel", aclass28);
            vector.addElement(new MethodDescriptor(method));
            method = class1.getMethod("findPanel", aclass8);
            vector.addElement(new MethodDescriptor(method));
            method = class1.getMethod("addTopDocListenerToSource", aclass30);
            vector.addElement(new MethodDescriptor(method));
            method = class1.getMethod("removeTopDocListenerToSource", aclass30);
            vector.addElement(new MethodDescriptor(method));
            method = class1.getMethod("setDocFont", aclass23);
            vector.addElement(new MethodDescriptor(method));
            method = class1.getMethod("setDocFontNotInPlace", aclass);
            vector.addElement(new MethodDescriptor(method));
            method = class1.getMethod("handleLinkEvent", aclass26);
            vector.addElement(new MethodDescriptor(method));
            method = class1.getMethod("handleNewHREF", aclass22);
            vector.addElement(new MethodDescriptor(method));
            method = class1.getMethod("handlePOST", aclass18);
            vector.addElement(new MethodDescriptor(method));
            method = class1.getMethod("handleJavaScriptURL", aclass24);
            vector.addElement(new MethodDescriptor(method));
            method = class1.getMethod("update", aclass31);
            vector.addElement(new MethodDescriptor(method));
            method = class1.getMethod("clone", aclass);
            vector.addElement(new MethodDescriptor(method));
            method = class1.getMethod("destroyAllApplets", aclass);
            vector.addElement(new MethodDescriptor(method));
            method = class1.getMethod("destroyAllApplets", aclass10);
            vector.addElement(new MethodDescriptor(method));
            method = class1.getMethod("addLinkListener", aclass25);
            vector.addElement(new MethodDescriptor(method));
            method = class1.getMethod("removeLinkListener", aclass25);
            vector.addElement(new MethodDescriptor(method));
            method = class1.getMethod("addPropertyChangeListener", aclass5);
            vector.addElement(new MethodDescriptor(method));
            method = class1.getMethod("removePropertyChangeListener", aclass5);
            vector.addElement(new MethodDescriptor(method));
            method = class1.getMethod("addBrowserHistoryListener", aclass14);
            vector.addElement(new MethodDescriptor(method));
            method = class1.getMethod("removeBrowserHistoryListener", aclass14);
            vector.addElement(new MethodDescriptor(method));
            method = class1.getMethod("addVetoableChangeListener", aclass6);
            vector.addElement(new MethodDescriptor(method));
            method = class1.getMethod("removeVetoableChangeListener", aclass6);
            vector.addElement(new MethodDescriptor(method));
            method = class1.getMethod("processDocumentEvent", aclass29);
            vector.addElement(new MethodDescriptor(method));
            method = class1.getMethod("processExtViewerURLInStack", aclass2);
            vector.addElement(new MethodDescriptor(method));
            method = class1.getMethod("getOpener", aclass);
            vector.addElement(new MethodDescriptor(method));
            method = class1.getMethod("setOpener", aclass3);
            vector.addElement(new MethodDescriptor(method));
            method = class1.getMethod("getAppletManager", aclass);
            vector.addElement(new MethodDescriptor(method));
            method = class1.getMethod("setAppletManager", aclass34);
            vector.addElement(new MethodDescriptor(method));
            method = class1.getMethod("getDocumentString", aclass);
            vector.addElement(new MethodDescriptor(method));
            method = class1.getMethod("setDocumentString", aclass1);
            vector.addElement(new MethodDescriptor(method));
            method = class1.getMethod("getDocumentStack", aclass);
            vector.addElement(new MethodDescriptor(method));
            method = class1.getMethod("getURLPoolManager", aclass);
            vector.addElement(new MethodDescriptor(method));
            method = class1.getMethod("setImageCache", aclass37);
            vector.addElement(new MethodDescriptor(method));
            method = class1.getMethod("getImageCache", aclass37);
            vector.addElement(new MethodDescriptor(method));
            method = class1.getMethod("setURLPoolManager", aclass36);
            vector.addElement(new MethodDescriptor(method));
            method = class1.getMethod("getCookiesManager", aclass);
            vector.addElement(new MethodDescriptor(method));
            method = class1.getMethod("setCookiesManager", aclass35);
            vector.addElement(new MethodDescriptor(method));
            method = class1.getMethod("flushLogicalStack", aclass10);
            vector.addElement(new MethodDescriptor(method));
            method = class1.getMethod("clear", aclass);
            vector.addElement(new MethodDescriptor(method));
            method = class1.getMethod("getDocumentURL", aclass);
            vector.addElement(new MethodDescriptor(method));
            method = class1.getMethod("getFrameURL", aclass);
            vector.addElement(new MethodDescriptor(method));
            method = class1.getMethod("setDocumentURL", aclass2);
            vector.addElement(new MethodDescriptor(method));
            method = class1.getMethod("getCurrentDocument", aclass);
            vector.addElement(new MethodDescriptor(method));
            method = class1.getMethod("setCurrentDocument", aclass7);
            vector.addElement(new MethodDescriptor(method));
            method = class1.getMethod("getDocumentSource", aclass);
            vector.addElement(new MethodDescriptor(method));
            method = class1.getMethod("getFrameSource", aclass);
            vector.addElement(new MethodDescriptor(method));
            method = class1.getMethod("getTopLevelFrame", aclass);
            vector.addElement(new MethodDescriptor(method));
            method = class1.getMethod("setDocumentSource", aclass4);
            vector.addElement(new MethodDescriptor(method));
            method = class1.getMethod("SetDocumentSource", aclass27);
            vector.addElement(new MethodDescriptor(method));
            method = class1.getMethod("saveDocumentSource", aclass15);
            vector.addElement(new MethodDescriptor(method));
            method = class1.getMethod("saveFrameSource", aclass15);
            vector.addElement(new MethodDescriptor(method));
            method = class1.getMethod("saveURLContent", aclass16);
            vector.addElement(new MethodDescriptor(method));
            method = class1.getMethod("getDocumentTitle", aclass);
            vector.addElement(new MethodDescriptor(method));
            method = class1.getMethod("getErrorMessage", aclass);
            vector.addElement(new MethodDescriptor(method));
            method = class1.getMethod("getStatusMessage", aclass);
            vector.addElement(new MethodDescriptor(method));
            method = class1.getMethod("setStatusMessage", aclass1);
            vector.addElement(new MethodDescriptor(method));
            method = class1.getMethod("isSecureConnection", aclass);
            vector.addElement(new MethodDescriptor(method));
            method = class1.getMethod("setSecureConnection", aclass2);
            vector.addElement(new MethodDescriptor(method));
            method = class1.getMethod("getFrameList", aclass);
            vector.addElement(new MethodDescriptor(method));
            method = class1.getMethod("getFrameList", aclass9);
            vector.addElement(new MethodDescriptor(method));
            method = class1.getMethod("getSelection", aclass);
            vector.addElement(new MethodDescriptor(method));
            method = class1.getMethod("getSelector", aclass);
            vector.addElement(new MethodDescriptor(method));
            method = class1.getMethod("getCharset", aclass);
            vector.addElement(new MethodDescriptor(method));
            method = class1.getMethod("setCharset", aclass1);
            vector.addElement(new MethodDescriptor(method));
            method = class1.getMethod("getLoadingProgress", aclass);
            vector.addElement(new MethodDescriptor(method));
            method = class1.getMethod("getIndicatedElement", aclass);
            vector.addElement(new MethodDescriptor(method));
            method = class1.getMethod("isDocumentReloadable", aclass);
            vector.addElement(new MethodDescriptor(method));
            method = class1.getMethod("canReload", aclass);
            vector.addElement(new MethodDescriptor(method));
            method = class1.getMethod("reload", aclass);
            vector.addElement(new MethodDescriptor(method));
            method = class1.getMethod("canStopLoading", aclass);
            vector.addElement(new MethodDescriptor(method));
            method = class1.getMethod("stopLoading", aclass);
            vector.addElement(new MethodDescriptor(method));
            method = class1.getMethod("clearImageCache", aclass);
            vector.addElement(new MethodDescriptor(method));
            method = class1.getMethod("find", aclass11);
            vector.addElement(new MethodDescriptor(method));
            method = class1.getMethod("find", aclass17);
            vector.addElement(new MethodDescriptor(method));
            method = class1.getMethod("print", aclass);
            vector.addElement(new MethodDescriptor(method));
            method = class1.getMethod("print", aclass12);
            vector.addElement(new MethodDescriptor(method));
            method = class1.getMethod("print", aclass10);
            vector.addElement(new MethodDescriptor(method));
            method = class1.getMethod("internalGoto", aclass19);
            vector.addElement(new MethodDescriptor(method));
            method = class1.getMethod("internalGoto", aclass20);
            vector.addElement(new MethodDescriptor(method));
            method = class1.getMethod("internalGoto", aclass21);
            vector.addElement(new MethodDescriptor(method));
            method = class1.getMethod("executeHistoryCommand", aclass13);
            vector.addElement(new MethodDescriptor(method));
            method = class1.getMethod("getSelfRef", aclass);
            vector.addElement(new MethodDescriptor(method));
            MethodDescriptor amethoddescriptor[] = new MethodDescriptor[vector.size()];
            for(int i = 0; i < vector.size(); i++)
                amethoddescriptor[i] = (MethodDescriptor)vector.elementAt(i);

            return amethoddescriptor;
        }
        catch(NoSuchMethodException nosuchmethodexception)
        {
            nosuchmethodexception.printStackTrace();
            System.err.println("NoSuchMethodException: " + nosuchmethodexception.getMessage());
            throw new Error(nosuchmethodexception.toString());
        }
    }

    public HotJavaBrowserBeanBeanInfo()
    {
    }
}
