// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   AnimationIcon.java

package sunw.hotjava.ui;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.ImageObserver;
import java.util.Observable;
import java.util.Observer;
import sun.net.ProgressData;
import sun.net.ProgressEntry;
import sunw.hotjava.misc.HJBProperties;
import sunw.hotjava.misc.RequestProcessor;

// Referenced classes of package sunw.hotjava.ui:
//            AnimationRequest, StopRequest, UserImageButton

public class AnimationIcon extends Canvas
    implements Observer
{
    private final class AnimationIconMouseListener extends MouseAdapter
    {

        public void mousePressed(MouseEvent mouseevent)
        {
            processMouseEvent(mouseevent);
        }

        public void mouseReleased(MouseEvent mouseevent)
        {
            processMouseEvent(mouseevent);
        }

        public void mouseEntered(MouseEvent mouseevent)
        {
            processMouseEvent(mouseevent);
        }

        public void mouseExited(MouseEvent mouseevent)
        {
            processMouseEvent(mouseevent);
        }

        AnimationIconMouseListener()
        {
        }
    }


    private synchronized void loadImages()
    {
        synchronized(sunw.hotjava.ui.AnimationIcon.class)
        {
            if(!loadedImages)
            {
                loadedImages = true;
                String s = "feedback.animation";
                String s1 = "feedback.rest";
                animImageLarge = properties.getImage(properties.getProperty(s));
                restImageLarge = properties.getImage(properties.getProperty(s1));
                animImageSmall = properties.getImage(properties.getProperty(s + ".small"));
                restImageSmall = properties.getImage(properties.getProperty(s1 + ".small"));
                MediaTracker mediatracker = new MediaTracker(this);
                mediatracker.addImage(animImageLarge, 0);
                mediatracker.addImage(animImageSmall, 1);
                mediatracker.addImage(restImageLarge, 2);
                mediatracker.addImage(restImageSmall, 3);
                try
                {
                    mediatracker.waitForAll();
                }
                catch(Exception _ex)
                {
                    loadedImages = false;
                    return;
                }
            }
        }
    }

    public AnimationIcon(boolean flag)
    {
        properties = HJBProperties.getHJBProperties("hjbrowser");
        restframe = true;
        imageLoaded = false;
        loadingError = false;
        stopAnimation = false;
        wasDepressed = false;
        depressed = false;
        entered = false;
        animWidth = -1;
        animHeight = -1;
        restWidth = -1;
        restHeight = -1;
        easterEggState = 0;
        bonusCount = -1;
        baseSound = "doc:/lib/hotjava/duke";
        loadImages();
        isLarge = flag;
        String s = flag ? "" : ".small";
        animation = properties.getProperty("feedback.animation" + s);
        resting = properties.getProperty("feedback.rest" + s);
        animImage = flag ? animImageLarge : animImageSmall;
        restImage = flag ? restImageLarge : restImageSmall;
        req = new AnimationRequest(this);
        initImage(animation);
        initImage(resting);
        if(animImage != null && restImage != null)
        {
            imageLoaded = true;
            loadingError = false;
        }
        addMouseListener(new AnimationIconMouseListener());
        action = properties.getProperty("hotjava.activitymonitor.action", "logopage");
    }

    public synchronized void checkCount()
    {
        if(count <= 0)
        {
            count = 0;
            restframe = true;
            postAnimationRequest();
        }
    }

    public synchronized boolean isLastRequest(StopRequest stoprequest)
    {
        return stoprequest == lastRequest;
    }

    public void processMouseEvent(MouseEvent mouseevent)
    {
        switch(mouseevent.getID())
        {
        case 503: 
        default:
            break;

        case 501: 
            wasDepressed = true;
            depressed = true;
            break;

        case 502: 
            if(wasDepressed)
            {
                wasDepressed = false;
                if(depressed)
                {
                    depressed = false;
                    try
                    {
                        dispatchActionEvent(mouseevent);
                    }
                    catch(Exception exception)
                    {
                        exception.printStackTrace();
                    }
                    break;
                }
            }
            // fall through

        case 504: 
            entered = true;
            if(wasDepressed)
                depressed = true;
            repaint();
            break;

        case 505: 
            entered = false;
            if(wasDepressed)
                depressed = false;
            repaint();
            break;
        }
        redispatch(mouseevent);
    }

    private void redispatch(MouseEvent mouseevent)
    {
        mouseevent.getComponent().getParent().dispatchEvent(mouseevent);
    }

    public void addNotify()
    {
        super.addNotify();
        ProgressData.pdata.addObserver(this);
    }

    public void removeNotify()
    {
        ProgressData.pdata.deleteObserver(this);
        super.removeNotify();
    }

    public Dimension preferredSize()
    {
        return new Dimension(animWidth + 6, animHeight + 6);
    }

    public Dimension minimumSize()
    {
        return preferredSize();
    }

    public void reshape(int i, int j, int k, int l)
    {
        super.reshape(i, j, k, l);
        size();
    }

    public synchronized void update(Observable observable, Object obj)
    {
        if(!isVisible())
            return;
        ProgressEntry progressentry = (ProgressEntry)obj;
        stopAnimation = false;
        restframe = false;
        try
        {
            switch(progressentry.what)
            {
            case 0: // '\0'
                if(count == 0)
                    postAnimationRequest();
                count++;
                return;

            case 1: // '\001'
            case 2: // '\002'
                postAnimationRequest();
                return;

            case 3: // '\003'
                count--;
                if(count <= 0)
                {
                    synchronized(this)
                    {
                        lastRequest = new StopRequest(this);
                        RequestProcessor.getHJBeanQueue().postRequest(lastRequest, 1000);
                    }
                    return;
                }
                break;
            }
        }
        catch(Exception exception)
        {
            exception.printStackTrace();
        }
    }

    public void postAnimationRequest()
    {
        if(req != null)
            RequestProcessor.getHJBeanQueue().postRequest(req);
    }

    void paintUpdate()
    {
        Graphics g = getGraphics();
        try
        {
            if(g != null && !imageLoaded && loadingError)
                g.clearRect(0, 0, size().width, size().height);
            if(!imageLoaded && !loadingError)
            {
                loadImages();
                animImage = isLarge ? animImageLarge : animImageSmall;
                restImage = isLarge ? restImageLarge : restImageSmall;
                initImage(animation);
                initImage(resting);
                if(animImage != null && restImage != null)
                {
                    imageLoaded = true;
                    loadingError = false;
                }
            }
            if(g != null)
            {
                g.setColor(Color.gray);
                if(imageLoaded && !loadingError)
                {
                    Dimension dimension = getSize();
                    if(!restframe)
                    {
                        if(entered)
                        {
                            g.setColor(UserImageButton.OUTLINE);
                            g.drawRect(0, 0, animWidth + 5, animHeight + 5);
                            g.setColor(UserImageButton.HIGHLIGHT);
                            g.drawRect(1, 1, animWidth + 5, animHeight + 5);
                            g.setColor(UserImageButton.OVERLAP);
                            g.drawLine(1, animWidth + 5, 1, animHeight + 5);
                            g.drawLine(animWidth + 5, 1, animHeight + 5, 1);
                            g.setColor(Color.gray);
                        } else
                        {
                            g.clearRect(0, 0, dimension.width, dimension.height);
                        }
                        g.draw3DRect(2, 2, animWidth + 1, animHeight + 1, false);
                        g.drawImage(animImage, 3, 3, animWidth, animHeight, this);
                    } else
                    {
                        if(entered)
                        {
                            g.setColor(UserImageButton.OUTLINE);
                            g.drawRect(0, 0, animWidth + 5, animHeight + 5);
                            g.setColor(UserImageButton.HIGHLIGHT);
                            g.drawRect(1, 1, animWidth + 5, animHeight + 5);
                            g.setColor(UserImageButton.OVERLAP);
                            g.drawLine(1, animWidth + 5, 1, animHeight + 5);
                            g.drawLine(animWidth + 5, 1, animHeight + 5, 1);
                            g.setColor(Color.gray);
                        } else
                        {
                            g.clearRect(0, 0, dimension.width, dimension.height);
                        }
                        g.draw3DRect(2, 2, restWidth + 1, restHeight + 1, false);
                        g.drawImage(restImage, 3, 3, restWidth, restHeight, this);
                        stopAnimation = true;
                    }
                }
            }
        }
        finally
        {
            g.dispose();
        }
    }

    public synchronized void addActionListener(ActionListener actionlistener)
    {
        listeners = AWTEventMulticaster.add(listeners, actionlistener);
    }

    public synchronized void removeActionListener(ActionListener actionlistener)
    {
        listeners = AWTEventMulticaster.remove(listeners, actionlistener);
    }

    protected void dispatchActionEvent(MouseEvent mouseevent)
    {
        ActionEvent actionevent = new ActionEvent(this, 1001, action, mouseevent.getModifiers());
        if(listeners != null)
            listeners.actionPerformed(actionevent);
    }

    public void update(Graphics g)
    {
        paint(g);
    }

    public void paint(Graphics g)
    {
        postAnimationRequest();
    }

    private void initImage(String s)
    {
        if(s.equals(animation))
            if(animImage == null)
            {
                return;
            } else
            {
                animWidth = animImage.getWidth(this);
                animHeight = animImage.getHeight(this);
                return;
            }
        if(restImage == null)
        {
            return;
        } else
        {
            restWidth = restImage.getWidth(this);
            restHeight = restImage.getHeight(this);
            return;
        }
    }

    void getImageDimensions(Image image, int i, int j, int k)
    {
        synchronized(this)
        {
            if((i & 1) == 0)
                j = image.getWidth(this);
            if((i & 2) == 0)
                k = image.getHeight(this);
            if(j < 0 || k < 0)
            {
                imageLoaded = false;
                return;
            }
            if(image == animImage)
            {
                animWidth = j;
                animHeight = k;
            } else
            {
                restWidth = j;
                restHeight = k;
            }
            imageLoaded = true;
            loadingError = false;
        }
        repaint();
    }

    public boolean imageUpdate(Image image, int i, int j, int k, int l, int i1)
    {
        if(image == animImage && stopAnimation)
            return false;
        if((i & 0x40) != 0)
        {
            imageLoaded = false;
            loadingError = true;
            return false;
        }
        if((i & 0x30) != 0)
            getImageDimensions(image, i, l, i1);
        return (i & 0xa0) == 0;
    }

    private HJBProperties properties;
    private static Image animImageLarge = null;
    private static Image animImageSmall = null;
    private static Image restImageLarge = null;
    private static Image restImageSmall = null;
    private static boolean loadedImages;
    private boolean isLarge;
    String animation;
    String resting;
    Image animImage;
    Image restImage;
    boolean restframe;
    boolean imageLoaded;
    boolean loadingError;
    boolean stopAnimation;
    private boolean wasDepressed;
    private boolean depressed;
    private boolean entered;
    private ActionListener listeners;
    private String action;
    int animWidth;
    int animHeight;
    int restWidth;
    int restHeight;
    sunw.hotjava.misc.RequestProcessor.Request req;
    private StopRequest lastRequest;
    private int count;
    private final int NORMAL_STATE = 0;
    private final int GOOD_LEFT_CLICK_STATE = 1;
    private final int BONUS_STATE = 2;
    private int easterEggState;
    private int bonusCount;
    private int numberOfBonus;
    private String baseSound;

}
