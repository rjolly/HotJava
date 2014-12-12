// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ListItem.java

package sunw.hotjava.ui;

import java.awt.*;
import sunw.hotjava.misc.HJBProperties;

// Referenced classes of package sunw.hotjava.ui:
//            Folder, ListContainer

public class ListItem
    implements Cloneable
{

    public ListItem()
    {
        grabbed = false;
        icon_width = 20;
        icon_height = 20;
        text_width = -1;
        text_height = -1;
        selected = false;
    }

    public ListItem(ListContainer listcontainer, String s)
    {
        grabbed = false;
        icon_width = 20;
        icon_height = 20;
        text_width = -1;
        text_height = -1;
        selected = false;
        text = s;
        myContainer = listcontainer;
        location = new Point(0, 0);
    }

    public final Point getPoint()
    {
        return location;
    }

    public void mouseDownAction(int i, int j)
    {
        location = new Point(i, j);
    }

    public boolean doubleClick(int i, int j)
    {
        return false;
    }

    public void mouseDragAction(int i, int j)
    {
        location = new Point(i, j);
        int k = icon_width + text_width;
        int l = text_height >= icon_height ? text_height : icon_height;
        Rectangle rectangle = new Rectangle(i, j, k, l);
        Rectangle rectangle1 = rectangle.union(boundingBox);
        canvas.repaint(rectangle1.x, rectangle1.y, rectangle1.width + 1, rectangle1.height + 1);
        boundingBox = rectangle;
    }

    public void mouseUpAction(int i, int j)
    {
        mouseDragAction(i, j);
    }

    public void move(Point point, int i, int j)
    {
        if(boundingBox == null)
        {
            return;
        } else
        {
            int k = i - point.x;
            int l = j - point.y;
            Point point1 = location;
            point1.x = point1.x + k;
            point1.y = point1.y + l;
            int i1 = icon_width + text_width;
            int j1 = text_height >= icon_height ? text_height : icon_height;
            Rectangle rectangle = new Rectangle(point1.x, point1.y, i1, j1);
            point.x = i;
            point.y = j;
            Rectangle rectangle1 = rectangle.union(boundingBox);
            canvas.repaint(rectangle1.x, rectangle1.y, rectangle1.width + 1, rectangle1.height + 1);
            boundingBox = rectangle;
            return;
        }
    }

    public void drawItem(Graphics g)
    {
        Point point = location;
        int i = point.x;
        int j = point.y;
        showBackground(g);
        g.setColor(color);
        g.fillRect(i, j, icon_width, icon_height);
        g.setColor(Color.black);
        g.drawString(text, i + icon_width, j + icon_width);
        g.drawRect(boundingBox.x, boundingBox.y, boundingBox.width - 1, boundingBox.height - 1);
    }

    protected void showBackground(Graphics g)
    {
        if(selected || grabbed())
        {
            HJBProperties hjbproperties = HJBProperties.getHJBProperties("hjbrowser");
            String s = "hotlistframe.selection.color";
            g.setColor(hjbproperties.getColor(s, Color.yellow));
            g.fillRect(boundingBox.x + icon_width + 1, boundingBox.y, (boundingBox.width - icon_width) + 1, boundingBox.height);
        }
    }

    public void drawItemOnCanvas(Graphics g)
    {
        drawItem(g);
    }

    public void drawChildren(Graphics g)
    {
        drawItem(g);
    }

    public void setBoundingBox(Graphics g)
    {
        Point point = location;
        FontMetrics fontmetrics = g.getFontMetrics();
        text_height = fontmetrics.getHeight();
        text_width = fontmetrics.stringWidth(text);
        int i = point.x;
        int j = point.y;
        int k = icon_width + text_width;
        int l;
        if(text_height < icon_height)
        {
            l = icon_height + fontmetrics.getDescent();
            j += icon_height - text_height;
        } else
        {
            l = text_height;
        }
        boundingBox = new Rectangle(i, j, k, l);
    }

    public void setParentListContainer(ListContainer listcontainer)
    {
        myContainer = listcontainer;
    }

    public ListContainer getParentListContainer()
    {
        return myContainer;
    }

    public void removeMyself()
    {
        myContainer.removeItem(this);
        if(myContainer instanceof Folder)
        {
            Folder folder = (Folder)myContainer;
            if(folder.isTopList())
                folder.total_contents = folder.recalculatePositions(true);
            else
                folder.total_contents = folder.recalculatePositions();
        }
        myContainer.getCanvas().repaint(50L);
    }

    public void setSelected(boolean flag)
    {
        selected = flag;
    }

    public void toggleSelected()
    {
        selected = !selected;
    }

    public Object clone()
    {
        try
        {
            return super.clone();
        }
        catch(CloneNotSupportedException _ex)
        {
            throw new InternalError();
        }
    }

    public void setCanvas(Component component)
    {
        canvas = component;
    }

    public Component getCanvas()
    {
        return canvas;
    }

    public void setColor(Color color1)
    {
        color = color1;
    }

    public void mouseDown(int i, int j)
    {
        mouseDownAction(i, j);
    }

    public void mouseDrag(int i, int j)
    {
        if(grabbed)
        {
            move(grabbedAt, i, j);
            return;
        } else
        {
            mouseDragAction(i, j);
            return;
        }
    }

    public void mouseUp(int i, int j)
    {
        if(!grabbed)
            mouseUpAction(i, j);
    }

    public final boolean grabbed()
    {
        return grabbed;
    }

    public Point grabbedPoint()
    {
        if(grabbed)
            return grabbedAt;
        else
            return null;
    }

    public void grab(int i, int j)
    {
        grabbedAt = new Point(i, j);
        grabbed = true;
        canvas.repaint();
    }

    public void release()
    {
        grabbed = false;
    }

    public boolean inside(int i, int j)
    {
        if(boundingBox != null)
            return boundingBox.inside(i, j);
        else
            return false;
    }

    public boolean insideOnDrag(int i, int j)
    {
        if(boundingBox == null)
        {
            return false;
        } else
        {
            Rectangle rectangle = new Rectangle(boundingBox);
            rectangle.width = getCanvas().size().width;
            rectangle.x = 0;
            return rectangle.inside(i, j);
        }
    }

    public boolean intersects(Point point)
    {
        if(boundingBox == null)
        {
            return false;
        } else
        {
            int i = point.x;
            int j = point.y;
            return boundingBox.inside(i, j);
        }
    }

    public boolean intersectsOnDrop(Point point)
    {
        if(boundingBox == null)
        {
            return false;
        } else
        {
            Rectangle rectangle = new Rectangle(boundingBox);
            rectangle.width = getCanvas().size().width;
            rectangle.x = 0;
            int i = point.x;
            int j = point.y;
            return rectangle.inside(i, j);
        }
    }

    public final Rectangle getBoundingBox()
    {
        return boundingBox;
    }

    public int getHeight()
    {
        return icon_height;
    }

    public int getWidth()
    {
        return icon_width;
    }

    public String getTitle()
    {
        return text;
    }

    public void setTitle(String s)
    {
        text = s;
    }

    Color color;
    Component canvas;
    boolean grabbed;
    Point grabbedAt;
    Rectangle boundingBox;
    int icon_width;
    int icon_height;
    int text_width;
    int text_height;
    public Point location;
    ListContainer myContainer;
    boolean selected;
    String text;
}
