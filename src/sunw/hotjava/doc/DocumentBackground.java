// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   DocumentBackground.java

package sunw.hotjava.doc;

import java.awt.*;
import java.awt.image.ImageObserver;
import java.net.URL;
import java.util.*;
import sun.awt.ScreenUpdater;
import sun.awt.UpdateClient;
import sunw.hotjava.bean.HotJavaBrowserBean;
import sunw.hotjava.bean.ImageCacher;
import sunw.hotjava.misc.Globals;
import sunw.hotjava.misc.HJBProperties;

// Referenced classes of package sunw.hotjava.doc:
//            Document

public class DocumentBackground
    implements ImageObserver, UpdateClient
{

    private DocumentBackground(URL url1, Color color, Document document1)
    {
        url = url1;
        col = color;
        document = document1;
    }

    public synchronized void addClient(Component component)
    {
        if(clients == null)
            clients = new Vector();
        if(clients.indexOf(component) < 0)
            clients.addElement(component);
    }

    public synchronized void removeClient(Component component)
    {
        if(clients != null)
        {
            clients.removeElement(component);
            if(clients.size() == 0)
            {
                clients = null;
                removeFromCache(this);
            }
        }
    }

    public boolean imageUpdate(Image image, int i, int j, int k, int l, int i1)
    {
        if(tile == null && (i & 3) != 0)
            makeTile(image);
        if((i & 0x20) != 0)
            ScreenUpdater.updater.notify(this, 0L, image);
        return (i & 0xa0) == 0;
    }

    public void updateClient(Object obj)
    {
        loadTile((Image)obj);
    }

    private synchronized void makeTile(Image image)
    {
        int i = image.getWidth(this);
        int j = image.getHeight(this);
        if(i <= 0 || j <= 0 || tile != null)
            return;
        int k = i >= 200 ? i : i * (200 / i + 1);
        int l = j >= 100 ? j : j * (100 / j + 1);
        if(Globals.frm != null)
            tile = Globals.frm.createImage(k, l);
        loadTile(image);
    }

    private synchronized void loadTile(Image image)
    {
        boolean flag = true;
        int i = image.getWidth(this);
        int j = image.getHeight(this);
        if(i <= 0 || j <= 0 || tile == null)
            return;
        Graphics g = tile.getGraphics();
        try
        {
            int k = tile.getWidth(null);
            int l = tile.getHeight(null);
            g.setColor(col);
            g.fillRect(0, 0, k, l);
            for(int i1 = 0; i1 < l; i1 += j)
            {
                for(int j1 = 0; j1 < k; j1 += i)
                {
                    boolean flag1 = g.drawImage(image, j1, i1, this);
                    flag = !flag1 ? false : flag;
                }

            }

        }
        finally
        {
            g.dispose();
            if(flag)
                fillTile(image);
            else
                ScreenUpdater.updater.notify(this, 500L, image);
        }
    }

    private synchronized void fillTile(Image image)
    {
        int i = image.getWidth(this);
        int j = image.getHeight(this);
        if(i <= 0 || j <= 0)
            return;
        Graphics g = tile.getGraphics();
        try
        {
            int k = tile.getWidth(null);
            int l = tile.getHeight(null);
            g.setColor(col);
            g.fillRect(0, 0, k, l);
            for(int i1 = 0; i1 < k; i1 += j)
            {
                for(int j1 = 0; j1 < k; j1 += i)
                    g.drawImage(image, j1, i1, null);

            }

        }
        finally
        {
            g.dispose();
        }
        if(clients != null)
        {
            Component component;
            for(Enumeration enumeration = clients.elements(); enumeration.hasMoreElements(); component.repaint())
                component = (Component)enumeration.nextElement();

        }
    }

    public synchronized void paint(Graphics g, int i, int j, int k, int l, int i1, int j1)
    {
        if(i1 <= 0 || j1 <= 0)
            return;
        HJBProperties hjbproperties = HJBProperties.getHJBProperties("beanPropertiesKey");
        if(!hjbproperties.getBoolean("displayBackgroundImages"))
        {
            g.setColor(col);
            g.fillRect(k, l, i1, j1);
            return;
        }
        Graphics g1 = g.create();
        Rectangle rectangle = g.getClipRect();
        if(rectangle != null)
            g1.clipRect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
        try
        {
            if(tile == null)
            {
                g.setColor(col);
                g.fillRect(k, l, i1, j1);
                ImageCacher imagecacher = HotJavaBrowserBean.getImageCache();
                Image image = null;
                if(imagecacher != null)
                {
                    URL url1 = null;
                    if(document != null)
                        url1 = document.getBaseURL();
                    image = imagecacher.getImage(document, url, url1);
                }
                if(image != null)
                    makeTile(image);
                return;
            }
            int k1 = tile.getWidth(null);
            int l1 = tile.getHeight(null);
            g1.clipRect(k, l, i1, j1);
            for(int i2 = l - (l - j) % l1; i2 < l + j1; i2 += l1)
            {
                for(int j2 = k - (k - i) % k1; j2 < k + i1; j2 += k1)
                    g1.drawImage(tile, j2, i2, k1, l1, null);

            }

        }
        finally
        {
            g1.dispose();
        }
    }

    public int hashCode()
    {
        return url.hashCode() ^ col.hashCode();
    }

    public boolean equals(Object obj)
    {
        if(obj instanceof DocumentBackground)
        {
            DocumentBackground documentbackground = (DocumentBackground)obj;
            return url.equals(documentbackground.url) && col.equals(col);
        } else
        {
            return false;
        }
    }

    public static synchronized DocumentBackground getDocumentBackground(URL url1, Color color, Document document1)
    {
        DocumentBackground documentbackground = new DocumentBackground(url1, color, document1);
        DocumentBackground documentbackground1 = hash == null ? null : (DocumentBackground)hash.get(documentbackground);
        if(documentbackground1 != null)
            return documentbackground1;
        if(hash == null)
            hash = new Hashtable(11);
        hash.put(documentbackground, documentbackground);
        ImageCacher imagecacher = HotJavaBrowserBean.getImageCache();
        if(imagecacher != null)
        {
            URL url2 = null;
            if(document1 != null)
                url2 = document1.getBaseURL();
            Image image = imagecacher.getImage(document1, url1, url2);
            if(image != null)
                documentbackground.makeTile(image);
        }
        return documentbackground;
    }

    public static synchronized void flushCache()
    {
        hash = null;
    }

    private static synchronized void removeFromCache(DocumentBackground documentbackground)
    {
        Hashtable hashtable = hash;
        if(hashtable != null)
            hashtable.remove(documentbackground);
    }

    private Color col;
    private URL url;
    private Image tile;
    private Vector clients;
    private Document document;
    private static Hashtable hash;
    private static final int MIN_WIDTH = 200;
    private static final int MIN_HEIGHT = 100;
}
