// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ImageCache.java

package sunw.hotjava.bean;

import java.awt.*;
import java.awt.image.ImageObserver;
import java.net.URL;
import java.util.Enumeration;
import java.util.Hashtable;
import sunw.hotjava.applet.AppletImageRef;
import sunw.hotjava.misc.SelfCleaningHashtable;

// Referenced classes of package sunw.hotjava.bean:
//            ImageCacher

public class ImageCache
    implements ImageCacher
{
    class DocumentImages
        implements ImageObserver
    {

        public Object getKey()
        {
            return key;
        }

        public synchronized Image getImage(URL url, URL url1)
        {
            Object obj = null;
            Image image1 = (Image)images.get(url);
            if(image1 != null)
                return image1;
            synchronized(ImageCache.imgHash)
            {
                Image image = getImageHashImage(url, url1);
                images.put(url, image);
                imageIDs.put(new Integer(lastImageID), image);
                ImageCache.comp.prepareImage(image, this);
                mediaTracker.addImage(image, lastImageID);
                lastImageID++;
                for(Enumeration enumeration = imageIDs.keys(); enumeration.hasMoreElements();)
                {
                    Integer integer = (Integer)enumeration.nextElement();
                    int i = mediaTracker.statusID(integer.intValue(), true);
                    switch(i)
                    {
                    case 2: // '\002'
                    case 4: // '\004'
                    case 8: // '\b'
                        Image image3 = (Image)imageIDs.remove(integer);
                        mediaTracker.removeImage(image3);
                        break;
                    }
                }

                Image image2 = image;
                return image2;
            }
        }

        public boolean imageUpdate(Image image, int i, int j, int k, int l, int i1)
        {
            return (i & 0x20) != 0;
        }

        private Object key;
        private int myDocImageNumber;
        private Hashtable images;
        private Hashtable imageIDs;
        private MediaTracker mediaTracker;
        private int lastImageID;

        public DocumentImages(Object obj)
        {
            myDocImageNumber = ImageCache.docImageNumber++;
            key = obj;
            images = new Hashtable(20);
            imageIDs = new Hashtable(20);
            mediaTracker = new MediaTracker(ImageCache.comp);
        }
    }


    public ImageCache()
    {
        setReferenceDepth(1);
    }

    public void setReferenceDepth(int i)
    {
        synchronized(imgHash)
        {
            DocumentImages adocumentimages[] = documents;
            documents = new DocumentImages[i];
            for(int j = 0; j < i && j < adocumentimages.length; j++)
                documents[j] = adocumentimages[j];

        }
    }

    public Image getImage(Object obj, URL url, URL url1)
    {
        synchronized(imgHash)
        {
            if(documents.length == 0 || obj == null)
            {
                Image image = getImageHashImage(url, url1);
                return image;
            }
            for(int i = 0; i < documents.length; i++)
                if(documents[i] != null && documents[i].getKey().equals(obj))
                {
                    if(i != 0)
                    {
                        DocumentImages documentimages = documents[i];
                        System.arraycopy(documents, 0, documents, 1, i);
                        documents[0] = documentimages;
                    }
                    Image image1 = documents[0].getImage(url, url1);
                    return image1;
                }

            DocumentImages documentimages1 = new DocumentImages(obj);
            if(documents.length == 1)
            {
                documents[0] = documentimages1;
            } else
            {
                DocumentImages adocumentimages[] = new DocumentImages[documents.length];
                System.arraycopy(documents, 0, adocumentimages, 1, adocumentimages.length - 1);
                documents = adocumentimages;
                documents[0] = documentimages1;
            }
            Image image2 = documentimages1.getImage(url, url1);
            return image2;
        }
    }

    public void flushAllImages()
    {
        SelfCleaningHashtable selfcleaninghashtable = imgHash;
        synchronized(selfcleaninghashtable)
        {
            imgHash = emptyHash;
            documents = new DocumentImages[documents.length];
            emptyHash = new SelfCleaningHashtable(1);
        }
    }

    public void flushDocumentImages(Object obj)
    {
        int i = 0;
        synchronized(imgHash)
        {
            for(int j = 0; j < documents.length; j++)
                if(documents[j] != null && !documents[j].getKey().equals(obj))
                {
                    if(i < j)
                    {
                        documents[i] = documents[j];
                        documents[j] = null;
                    }
                    i++;
                } else
                {
                    documents[j] = null;
                }

        }
    }

    private Image getImageHashImage(URL url, URL url1)
    {
        synchronized(imgHash)
        {
            AppletImageRef appletimageref = (AppletImageRef)imgHash.get(url);
            if(appletimageref == null)
            {
                appletimageref = new AppletImageRef(url);
                appletimageref.get(url1);
                imgHash.put(url, appletimageref);
            }
            Object obj = appletimageref.get(url1);
            Image image1 = (Image)obj;
            Image image = image1;
            return image;
        }
    }

    private static SelfCleaningHashtable imgHash = new SelfCleaningHashtable(50);
    private static SelfCleaningHashtable emptyHash = new SelfCleaningHashtable(1);
    private static DocumentImages documents[] = new DocumentImages[0];
    private static Canvas comp = new Canvas();
    private static int docImageNumber;






}
