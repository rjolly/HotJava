// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   DocPanel.java

package sunw.hotjava.doc;


// Referenced classes of package sunw.hotjava.doc:
//            Document

public interface DocPanel
{

    public abstract void activateSubItems();

    public abstract void start();

    public abstract void stop();

    public abstract void destroy();

    public abstract void interruptLoading();

    public abstract void notify(Document document, int i, int j, int k);

    public abstract void reformat();

    public abstract int findYFor(int i);

    public abstract void setObsolete(boolean flag);
}
