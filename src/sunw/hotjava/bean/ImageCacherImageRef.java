// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ImageCacherImageRef.java

package sunw.hotjava.bean;

import java.net.URL;
import sun.misc.Ref;
import sunw.hotjava.doc.Document;

// Referenced classes of package sunw.hotjava.bean:
//            HotJavaBrowserBean, ImageCacher

public class ImageCacherImageRef extends Ref
{

    public ImageCacherImageRef(Document document, URL url1)
    {
        reload = false;
        url = url1;
        doc = document;
    }

    public void flush()
    {
        super.flush();
    }

    public Object get(boolean flag)
    {
        reload = flag;
        return super.get();
    }

    public int getReloadCount()
    {
        return reloadCount;
    }

    public Object reconstitute()
    {
        java.awt.Image image = null;
        reloadCount++;
        ImageCacher imagecacher = HotJavaBrowserBean.getImageCache();
        if(imagecacher != null)
        {
            URL url1 = null;
            if(doc != null)
                url1 = doc.getBaseURL();
            image = imagecacher.getImage(doc, url, url1);
        }
        return image;
    }

    private URL url;
    private Document doc;
    private int reloadCount;
    private boolean reload;
}
