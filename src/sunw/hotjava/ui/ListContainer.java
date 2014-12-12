// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ListContainer.java

package sunw.hotjava.ui;

import java.awt.*;
import java.io.PrintStream;
import java.util.Vector;

// Referenced classes of package sunw.hotjava.ui:
//            ListItem, HotList, ListCanvas, ListContainerInfo, 
//            ListElement

public class ListContainer extends ListItem
{

    public ListContainer(ListCanvas listcanvas, ListContainer listcontainer, String s, Image image, Image image1)
    {
        super(listcontainer, s);
        closed = false;
        INDENT = 10;
        contents = new Vector(10);
        listCanvas = listcanvas;
        img_open = image;
        img_closed = image1;
        if(img_open != null)
        {
            super.icon_width = img_open.getWidth(super.canvas);
            super.icon_height = img_open.getHeight(super.canvas);
        }
    }

    public ListContainer(ListContainer listcontainer, String s)
    {
        super(listcontainer, s);
        closed = false;
        INDENT = 10;
        contents = new Vector(10);
    }

    public void setListContainerImages(Image image, Image image1)
    {
        img_open = image;
        img_closed = image1;
        if(img_open != null)
        {
            super.icon_width = img_open.getWidth(super.canvas);
            super.icon_height = img_open.getHeight(super.canvas);
        }
    }

    public boolean doubleClick(int i, int j)
    {
        Rectangle rectangle = new Rectangle(super.location.x, super.location.y + 3, super.icon_width, super.icon_height - 3);
        if(rectangle.inside(i, j))
            if(closed)
                closed = false;
            else
                closed = true;
        return true;
    }

    public int getTotalContents()
    {
        if(total_contents == null)
            return 0;
        else
            return total_contents.index;
    }

    public int getTotalWidth()
    {
        int i = 0;
        if(total_contents == null)
            return 0;
        for(int j = 0; j < contents.size(); j++)
        {
            ListItem listitem = (ListItem)contents.elementAt(j);
            Rectangle rectangle = listitem.getBoundingBox();
            if(rectangle != null)
            {
                int k = rectangle.x + rectangle.width + 10;
                if(k > i)
                    i = k;
            }
            if(listitem instanceof ListContainer)
            {
                ListContainer listcontainer = (ListContainer)listitem;
                if(!listcontainer.closed)
                {
                    int l = listcontainer.getTotalWidth();
                    if(l > i)
                        i = l;
                }
            }
        }

        return i;
    }

    public int getTotalHeight()
    {
        if(total_contents == null)
            return 0;
        else
            return total_contents.size;
    }

    public void addItem(ListItem listitem)
    {
        addItem(listitem, -5);
    }

    public void addItem(ListItem listitem, int i)
    {
        if(i == -5)
        {
            contents.addElement(listitem);
            return;
        } else
        {
            contents.insertElementAt(listitem, i);
            return;
        }
    }

    public ListElement addElement(String s)
    {
        return addElement(s, null);
    }

    public ListElement addElement(String s, Image image)
    {
        ListElement listelement = new ListElement(this, s, image);
        listelement.setCanvas(listCanvas);
        addItem(listelement);
        recalculatePositions();
        return listelement;
    }

    public ListContainer addContainer(String s)
    {
        return addContainer(s, img_open, img_closed);
    }

    public ListContainer addContainer(String s, Image image, Image image1)
    {
        ListContainer listcontainer = new ListContainer(listCanvas, this, s, image, image1);
        listcontainer.setCanvas(listCanvas);
        addItem(listcontainer);
        recalculatePositions();
        return listcontainer;
    }

    private void newLocation(ListItem listitem, int i)
    {
        int j = listitem.icon_height;
        if(listitem.getParentListContainer() != listCanvas.getMainContainer())
        {
            listitem.location = new Point(super.location.x + INDENT, (i + 1) * j + super.location.y);
            return;
        } else
        {
            listitem.location = new Point(super.location.x + INDENT, i * j + super.location.y);
            return;
        }
    }

    public void setContainerState(boolean flag)
    {
        closed = flag;
    }

    public final boolean closed()
    {
        return closed;
    }

    public void drawItem(Graphics g)
    {
        if(listCanvas.getMainContainer() != this)
            drawItemOnCanvas(g);
        if(grabbed())
            return;
        if(closed)
            return;
        for(int i = 0; i < contents.size(); i++)
        {
            ListItem listitem = (ListItem)contents.elementAt(i);
            listitem.drawItem(g);
        }

    }

    public void setAllBoundingBoxes(Graphics g)
    {
        for(int i = 0; i < contents.size(); i++)
        {
            ListItem listitem = (ListItem)contents.elementAt(i);
            listitem.setBoundingBox(g);
            if(listitem instanceof ListContainer)
                ((ListContainer)listitem).setAllBoundingBoxes(g);
        }

    }

    public void drawChildren(Graphics g)
    {
        setContainerState(true);
        for(int i = 0; i < contents.size(); i++)
        {
            ListItem listitem = (ListItem)contents.elementAt(i);
            listitem.drawItemOnCanvas(g);
        }

    }

    public void drawItemOnCanvas(Graphics g)
    {
        Point point = super.location;
        int i = point.x;
        int j = point.y;
        boolean flag = true;
        setBoundingBox(g);
        showBackground(g);
        if(img_closed == null || img_open == null)
            flag = false;
        g.setColor(Color.black);
        if(flag)
            if(closed)
                g.drawImage(img_closed, i, j, super.canvas);
            else
                g.drawImage(img_open, i, j, super.canvas);
        g.drawString(super.text, i + super.icon_width, j + super.icon_height);
    }

    public ListItem locateItem(int i, int j)
    {
        for(int k = 0; k < contents.size(); k++)
        {
            ListItem listitem = (ListItem)contents.elementAt(k);
            if(listitem.inside(i, j))
                return listitem;
            if(listitem instanceof ListContainer)
            {
                ListContainer listcontainer = (ListContainer)listitem;
                if(!listcontainer.closed())
                {
                    ListItem listitem1 = listcontainer.locateItem(i, j);
                    if(listitem1 != null)
                        return listitem1;
                }
            }
        }

        return null;
    }

    public ListItem locateItemOnDrag(int i, int j)
    {
        for(int k = 0; k < contents.size(); k++)
        {
            ListItem listitem = (ListItem)contents.elementAt(k);
            if(listitem.insideOnDrag(i, j))
                return listitem;
            if(listitem instanceof ListContainer)
            {
                ListContainer listcontainer = (ListContainer)listitem;
                if(!listcontainer.closed())
                {
                    ListItem listitem1 = listcontainer.locateItemOnDrag(i, j);
                    if(listitem1 != null)
                        return listitem1;
                }
            }
        }

        return null;
    }

    public boolean resolveItemLocation(ListItem listitem, Point point)
    {
        if(intersects(point) && listitem != this)
        {
            listitem.removeMyself();
            contents.insertElementAt(listitem, 0);
            total_contents = recalculatePositions();
            listitem.setParentListContainer(this);
            return true;
        }
        if(closed())
            return false;
        for(int i = 0; i < contents.size(); i++)
        {
            ListItem listitem1 = (ListItem)contents.elementAt(i);
            if(listitem1 instanceof ListContainer)
            {
                ListContainer listcontainer = (ListContainer)listitem1;
                if(listitem instanceof ListContainer)
                {
                    ListContainer listcontainer3 = (ListContainer)listitem;
                    if(checkParentage(listcontainer3, listcontainer))
                        continue;
                }
                if(listcontainer.resolveItemLocation(listitem, point))
                {
                    total_contents = recalculatePositions();
                    return true;
                }
            } else
            if(listitem1.intersects(point) && listitem != listitem1)
            {
                if(listitem instanceof ListContainer)
                {
                    ListContainer listcontainer1 = (ListContainer)listitem;
                    if(checkParentage(listcontainer1, this))
                    {
                        total_contents = recalculatePositions();
                        return true;
                    }
                }
                ListContainer listcontainer2 = listitem.getParentListContainer();
                if(listcontainer2 != this)
                {
                    contents.insertElementAt(listitem, i);
                    listitem.removeMyself();
                    listitem.setParentListContainer(this);
                } else
                {
                    int j = contents.indexOf(listitem);
                    if(j > i)
                    {
                        contents.insertElementAt(listitem, i);
                        contents.removeElementAt(j + 1);
                    } else
                    {
                        contents.insertElementAt(listitem, i + 1);
                        contents.removeElement(listitem);
                    }
                    listitem.setParentListContainer(this);
                }
                total_contents = recalculatePositions();
                return true;
            }
        }

        total_contents = recalculatePositions();
        return false;
    }

    public ListContainerInfo recalculatePositions()
    {
        ListContainerInfo listcontainerinfo = null;
        try
        {
            listcontainerinfo = new ListContainerInfo();
            for(int i = 0; i < contents.size(); i++)
            {
                ListItem listitem = (ListItem)contents.elementAt(i);
                newLocation(listitem, listcontainerinfo.index);
                if(listitem instanceof ListContainer)
                {
                    ListContainer listcontainer = (ListContainer)listitem;
                    if(listcontainer.closed())
                    {
                        listcontainerinfo.index++;
                        listcontainerinfo.size += listitem.getHeight();
                    } else
                    {
                        listcontainer.INDENT = INDENT + 10;
                        ListContainerInfo listcontainerinfo1 = listcontainer.recalculatePositions();
                        listcontainerinfo.index += listcontainerinfo1.index + 1;
                        listcontainerinfo.size += listcontainerinfo1.size + listitem.getHeight();
                    }
                } else
                {
                    listcontainerinfo.index++;
                    listcontainerinfo.size += listitem.getHeight();
                }
            }

            total_contents = listcontainerinfo;
        }
        catch(Exception exception)
        {
            System.out.println("recalc: " + exception);
        }
        return listcontainerinfo;
    }

    public ListContainerInfo recalculatePositions(boolean flag)
    {
        ListContainerInfo listcontainerinfo = null;
        try
        {
            listcontainerinfo = new ListContainerInfo();
            for(int i = 0; i < contents.size(); i++)
            {
                ListItem listitem = (ListItem)contents.elementAt(i);
                newLocation(listitem, listcontainerinfo.index);
                if(listitem instanceof ListContainer)
                {
                    ListContainer _tmp = (ListContainer)listitem;
                    listcontainerinfo.index++;
                    listcontainerinfo.size += listitem.getHeight();
                } else
                {
                    listcontainerinfo.index++;
                    listcontainerinfo.size += listitem.getHeight();
                }
            }

            total_contents = listcontainerinfo;
        }
        catch(Exception exception)
        {
            System.out.println("recalc: " + exception);
        }
        return listcontainerinfo;
    }

    public boolean checkParentage(ListContainer listcontainer, ListContainer listcontainer1)
    {
        for(ListContainer listcontainer2 = listcontainer1; listcontainer2 != null; listcontainer2 = listcontainer2.getParentListContainer())
            if(listcontainer == listcontainer2)
                return true;

        return false;
    }

    public void setClosedValue(boolean flag)
    {
        closed = flag;
    }

    public boolean removeItem(ListItem listitem)
    {
        if(contents.removeElement(listitem))
            return true;
        for(int i = 0; i < contents.size(); i++)
        {
            ListItem _tmp = (ListItem)contents.elementAt(i);
            if(listitem instanceof ListContainer)
            {
                ListContainer listcontainer = (ListContainer)listitem;
                if(listcontainer.removeItem(listitem))
                    return true;
            }
        }

        return false;
    }

    public void setListCanvas(ListCanvas listcanvas)
    {
        listCanvas = listcanvas;
    }

    public Vector getContents()
    {
        return contents;
    }

    public String toString()
    {
        return "ListContainer [text=" + super.text + ", boundingBox = " + super.boundingBox + ", closed=" + closed + ", location =" + super.location + "]";
    }

    boolean closed;
    int INDENT;
    Rectangle folderRect;
    Image img_open;
    Image img_closed;
    public ListContainerInfo total_contents;
    public ListCanvas listCanvas;
    public Vector contents;
}
