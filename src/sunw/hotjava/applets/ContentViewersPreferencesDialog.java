// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   ContentViewersPreferencesDialog.java

package sunw.hotjava.applets;

import java.awt.BorderLayout;
import java.awt.Choice;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.FileDialog;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.List;
import java.awt.Panel;
import java.awt.TextComponent;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import sun.net.www.MimeEntry;
import sunw.hotjava.HJFrame;
import sunw.hotjava.HJWindowManager;
import sunw.hotjava.misc.ContentViewersManager;
import sunw.hotjava.misc.HJBProperties;
import sunw.hotjava.ui.ConfirmDialog;
import sunw.hotjava.ui.EnterKeyListener;
import sunw.hotjava.ui.KeyPressInterest;
import sunw.hotjava.ui.MultiLineLabel;
import sunw.hotjava.ui.UIHJButton;
import sunw.hotjava.ui.UIHJButtonGroup;
import sunw.hotjava.ui.UserChoice;
import sunw.hotjava.ui.UserDialog;
import sunw.hotjava.ui.UserFileDialog;
import sunw.hotjava.ui.UserLabel;
import sunw.hotjava.ui.UserTextField;

// Referenced classes of package sunw.hotjava.applets:
//            TypeSubtypeMap

public class ContentViewersPreferencesDialog extends UserDialog
    implements ActionListener, ItemListener, KeyPressInterest
{
    class ButtonPanel extends Panel
        implements ActionListener
    {

        public Dimension getMinimumSize()
        {
            return new Dimension(getParent().getSize().width, 50);
        }

        public Dimension getPreferredSize()
        {
            return getMinimumSize();
        }

        public void doDefaultAction()
        {
            if(defaultButton == null)
            {
                for(int i = 0; i < cmdButtons.length; i++)
                {
                    if(!cmdButtons[i].isDefault())
                        continue;
                    defaultButton = cmdButtons[i];
                    break;
                }

            }
            ActionEvent actionevent = new ActionEvent(defaultButton, 0, defaultButton.getActionCommand());
            actionPerformed(actionevent);
        }

        public void addButtons()
        {
            String s = "prefsdialog";
            UIHJButtonGroup uihjbuttongroup = new UIHJButtonGroup(props1);
            cmdButtons[0] = new UIHJButton(s + ".ok", props1);
            cmdButtons[0].setActionCommand("ok");
            cmdButtons[1] = new UIHJButton(s + ".apply", props1);
            cmdButtons[1].setActionCommand("apply");
            cmdButtons[2] = new UIHJButton(s + ".cancel", props1);
            cmdButtons[2].setActionCommand("cancel");
            cmdButtons[3] = new UIHJButton(s + ".help", props1);
            cmdButtons[3].setActionCommand("help");
            for(int i = 0; i < cmdButtons.length; i++)
            {
                ((Container)this).add(cmdButtons[i]);
                cmdButtons[i].addActionListener(this);
                uihjbuttongroup.addButtonToGroup(cmdButtons[i]);
            }

        }

        public void actionPerformed(ActionEvent actionevent)
        {
            if(actionevent.getActionCommand().equals("ok"))
            {
                contentViewer.ok();
                return;
            }
            if(actionevent.getActionCommand().equals("apply"))
            {
                contentViewer.apply();
                return;
            }
            if(actionevent.getActionCommand().equals("cancel"))
            {
                contentViewer.cancel();
                return;
            }
            if(actionevent.getActionCommand().equals("help"))
                contentViewer.help();
        }

        GridBagLayout gridbag;
        GridBagConstraints c;
        private ContentViewersPreferencesDialog contentViewer;
        private HJBProperties props1;
        private UIHJButton defaultButton;
        private final int OK = 0;
        private final int APPLY = 1;
        private final int CANCEL = 2;
        private final int HELP = 3;
        private UIHJButton cmdButtons[];

        public ButtonPanel(ContentViewersPreferencesDialog contentviewerspreferencesdialog1)
        {
            cmdButtons = new UIHJButton[4];
            props1 = HJBProperties.getHJBProperties("hjbrowser");
            contentViewer = contentviewerspreferencesdialog1;
            setLayout(new FlowLayout(2));
            addButtons();
        }
    }


    public static ContentViewersPreferencesDialog getSingleton()
    {
        if(singleton == null)
            singleton = new ContentViewersPreferencesDialog();
        singleton.toFront();
        singleton.requestFocus();
        return singleton;
    }

    private ContentViewersPreferencesDialog()
    {
        super("general.prefs.contentviewer.dialog", HJWindowManager.getHJWindowManager().getLastFocusHolder(), false, HJBProperties.getHJBProperties("hjbrowser"));
        editable = false;
        props = HJBProperties.getHJBProperties("hjbrowser");
        newLabel = props.getPropertyReplace("button.viewer.newbutton.text", "New");
        try
        {
            keylistener = new EnterKeyListener(this);
            setLayout(new BorderLayout());
            Panel panel = new Panel();
            panel.setLayout(new GridLayout(2, 1));
            Font font = Font.getFont("general.prefs.font.regular");
            Font font1 = Font.getFont("general.prefs.font.headings");
            MultiLineLabel multilinelabel = new MultiLineLabel(props.getProperty("general.prefs.contentviewer.title"), 10, 10, 0, font1);
            MultiLineLabel multilinelabel1 = new MultiLineLabel(props.getProperty("general.prefs.contentviewer.body"), 10, 0, 0, font);
            panel.add(multilinelabel);
            panel.add(multilinelabel1);
            add(panel, "North");
            Panel panel1 = new Panel();
            GridBagLayout gridbaglayout = new GridBagLayout();
            panel1.setLayout(gridbaglayout);
            Panel panel2 = new Panel();
            GridBagLayout gridbaglayout1 = new GridBagLayout();
            panel2.setLayout(gridbaglayout1);
            typesListLabel = new UserLabel("viewer.description.list.label");
            typesList = makeTypesList();
            typesList.addItemListener(this);
            actionLabel = new UserLabel("viewer.action.label");
            actionChoices = new UserChoice("viewer.actionchoice", props);
            actionChoices.addItemListener(this);
            descriptionLabel = new UserLabel("viewer.description.label");
            description = new UserTextField("viewer.description");
            description.addKeyListener(keylistener);
            applicationLabel = new UserLabel("viewer.application.label");
            application = new UserTextField("viewer.application");
            application.addKeyListener(keylistener);
            chooseApplication = new UIHJButton("viewer.chooseapplicationbutton", props);
            chooseApplication.addActionListener(this);
            extensionsLabel = new UserLabel("viewer.extensions.label");
            extensions = new UserTextField("viewer.extensions");
            extensions.addKeyListener(keylistener);
            UIHJButtonGroup uihjbuttongroup = new UIHJButtonGroup(props);
            Panel panel3 = new Panel();
            newDefinition = new UIHJButton("viewer.newbutton", props);
            newDefinition.addActionListener(this);
            delete = new UIHJButton("viewer.deletebutton", props);
            delete.addActionListener(this);
            uihjbuttongroup.addButtonToGroup(newDefinition);
            uihjbuttongroup.addButtonToGroup(delete);
            panel3.add(newDefinition);
            panel3.add(delete);
            newDefinition.setDefaultButton(false);
            editable = true;
            typeSubtypeMap = new TypeSubtypeMap();
            typeLabel = new UserLabel("viewer.type.label");
            typeChoices = new UserChoice("viewer.typechoice", props);
            typeChoices.enable(editable);
            subtypeLabel = new UserLabel("viewer.subtype.label");
            subtypeTextField = new UserTextField("viewer.subtype");
            subtypeTextField.addKeyListener(keylistener);
            subtypeTextField.setEditable(editable);
            subtypesList = new List(3);
            add(panel1, gridbaglayout, typesListLabel, 0, 0, 1, 1, 16, 0.0D, 0, 15, 0, 0, 0);
            add(panel1, gridbaglayout, typesList, 0, 1, 2, -1, 10, 1.0D, 1.0D, 1, 15, 0, 5, 0);
            add(panel1, gridbaglayout, panel3, 0, 7, 1, 0, 11, 0.0D, 2, 30, 5, 0, 0);
            add(panel2, gridbaglayout1, descriptionLabel, 2, 1, 1, 1, 13, 0.0D, 0, 0, 0);
            add(panel2, gridbaglayout1, description, 3, 1, 1, 1, 17, 1.0D, 2, 0, 5);
            add(panel2, gridbaglayout1, extensionsLabel, 2, 2, 1, 1, 13, 1.0D, 0, 0, 0, 5, 0);
            add(panel2, gridbaglayout1, extensions, 3, 2, 1, 1, 17, 1.0D, 2, 0, 5, 5, 0);
            add(panel2, gridbaglayout1, actionLabel, 2, 3, 1, 1, 13, 0.0D, 0, 0, 0, 5, 0);
            add(panel2, gridbaglayout1, actionChoices, 3, 3, 1, 1, 17, 1.0D, 0, 0, 5, 5, 0);
            add(panel2, gridbaglayout1, applicationLabel, 2, 4, 1, 1, 13, 0.0D, 0, 0, 0, 5, 0);
            add(panel2, gridbaglayout1, application, 3, 4, 1, 1, 17, 1.0D, 2, 0, 5, 5, 0);
            add(panel2, gridbaglayout1, chooseApplication, 4, 4, 1, 1, 17, 0.0D, 2, 0, 5, 5, 0);
            add(panel2, gridbaglayout1, typeLabel, 2, 5, 1, 1, 13, 0.0D, 0, 0, 0, 5, 0);
            add(panel2, gridbaglayout1, typeChoices, 3, 5, 1, 1, 17, 1.0D, 0, 0, 5, 5, 0);
            add(panel2, gridbaglayout1, subtypeLabel, 2, 6, 1, 1, 13, 0.0D, 0, 0, 0, 5, 0);
            add(panel2, gridbaglayout1, subtypeTextField, 3, 6, 1, 1, 17, 1.0D, 2, 0, 5, 5, 0);
            add(panel2, gridbaglayout1, subtypesList, 3, 7, 1, 1, 11, 1.0D, 2, 0, 5, 0, 0);
            add(panel1, "West");
            add(panel2, "Center");
            cmdRowPanel = new ButtonPanel(this);
            add(cmdRowPanel, "South");
        }
        catch(Exception exception)
        {
            System.err.println("ContentViewersPreferencesDialog.update exception");
            exception.printStackTrace();
        }
        reset();
        int i = Integer.getInteger("general.prefs.dialogwidth").intValue();
        int j = Integer.getInteger("general.prefs.dialogheight").intValue();
        if(i > 0 && j > 0)
        {
            setSize(i, j);
        } else
        {
            Toolkit toolkit = Toolkit.getDefaultToolkit();
            Dimension dimension = toolkit.getScreenSize();
            setSize(dimension.width / 4, dimension.height / 4);
        }
        centerOnScreen();
        show();
    }

    public void processEnterEvent(KeyEvent keyevent)
    {
        cmdRowPanel.doDefaultAction();
    }

    public void apply()
    {
        ContentViewersManager contentviewersmanager = ContentViewersManager.getManager();
        saved = (MimeEntry)contentviewersmanager.getCurrentViewer().clone();
        String s = description.getText();
        if(s == null || s.length() == 0)
        {
            HJFrame hjframe = HJWindowManager.getHJWindowManager().getLastFocusHolder();
            ConfirmDialog confirmdialog = new ConfirmDialog("contentviewer.description", hjframe, 1);
            confirmdialog.show();
        }
        contentviewersmanager.setDescription(s);
        contentviewersmanager.setAction(mapChoiceToAction(actionChoices.getSelectedIndex()));
        String s1 = application.getText();
        if(s1.length() > 0 && s1.indexOf("%s", 0) == -1)
            contentviewersmanager.setCommand(s1 + " %s");
        else
            contentviewersmanager.setCommand(s1);
        contentviewersmanager.setFileExtensions(extensions.getText());
        if(subtypeTextField.isEditable() && !"".equals(subtypeTextField.getText()))
        {
            String s2 = typeChoices.getSelectedItem().toLowerCase();
            String s3 = subtypeTextField.getText().trim().toLowerCase();
            contentviewersmanager.setType(s2 + "/" + s3);
        }
        descriptionToReplace = null;
        MimeEntry mimeentry = contentviewersmanager.getCurrentViewer();
        MimeEntry mimeentry1 = contentviewersmanager.getContentViewer(mimeentry.getType());
        if((definingNew || !mimeentry.getType().equals(saved.getType())) && mimeentry1 != null && mimeentry1.getType().equals(mimeentry.getType()))
        {
            HJFrame hjframe1 = HJWindowManager.getHJWindowManager().getLastFocusHolder();
            ConfirmDialog confirmdialog1 = new ConfirmDialog("contentviewer.replacetype", hjframe1, 2, true, false);
            confirmdialog1.show();
            if(confirmdialog1.getAnswer() == 0)
            {
                descriptionToReplace = mimeentry1.getDescription();
            } else
            {
                definingNew = false;
                contentviewersmanager.setCurrentViewer(saved);
                return;
            }
        }
        if(!definingNew && !mimeentry.getType().equals(saved.getType()))
            contentviewersmanager.remove(saved.getType());
        definingNew = false;
        contentviewersmanager.apply();
        contentviewersmanager.setCurrentViewer(mimeentry);
        if(!contentviewersmanager.save())
            System.out.println("ContentViewersButtonApplet: save failed!");
        HJWindowManager.getHJWindowManager();
        contentviewersmanager.notifyObservers("end-define-type");
        update();
    }

    public void ok()
    {
        apply();
        singleton = null;
        dispose();
    }

    public void cancel()
    {
        reset();
        singleton = null;
        dispose();
        delete.removeActionListener(this);
        chooseApplication.removeActionListener(this);
        newDefinition.removeActionListener(this);
        description.removeKeyListener(keylistener);
        application.removeKeyListener(keylistener);
        extensions.removeKeyListener(keylistener);
        subtypeTextField.removeKeyListener(keylistener);
    }

    public void help()
    {
        HJBProperties hjbproperties = HJBProperties.getHJBProperties("hjbrowser");
        try
        {
            String s = hjbproperties.getProperty("general.prefs.contentviewer.help");
            URL url = new URL(s);
            HJWindowManager.getHJWindowManager().openFrame("_hotjava_help", url);
            return;
        }
        catch(MalformedURLException malformedurlexception)
        {
            malformedurlexception.printStackTrace();
        }
    }

    public void reset()
    {
        ContentViewersManager contentviewersmanager = ContentViewersManager.getManager();
        contentviewersmanager.undoRemove();
        contentviewersmanager.notifyObservers();
        update();
        HJWindowManager.getHJWindowManager();
    }

    public void itemStateChanged(ItemEvent itemevent)
    {
        if(itemevent.getSource() == typesList)
        {
            selectViewer(((List)itemevent.getSource()).getSelectedItem());
            return;
        }
        if(itemevent.getSource() == actionChoices)
            setApplicationFieldEditState();
    }

    public void actionPerformed(ActionEvent actionevent)
    {
        String s = actionevent.getActionCommand();
        if(s.equals(delete.getActionCommand()))
            deleteType();
        if(s.equals(newDefinition.getActionCommand()))
            defineNewType();
        if(s.equals(chooseApplication.getActionCommand()))
            askForApplication();
    }

    public boolean handleEvent(Event event)
    {
        switch(event.id)
        {
        default:
            break;

        case 701: // Event.LIST_SELECT
            if(subtypesList == (List)event.target)
                handleSubtypeChoice();
            break;
        }
        return super.handleEvent(event);
    }

    public boolean action(Event event, Object obj)
    {
        if(event.target == typeChoices)
            return handleTypeChoice();
        if(event.target == subtypesList)
            return handleSubtypeChoice();
        else
            return false;
    }

    boolean deleteType()
    {
        ContentViewersManager contentviewersmanager = ContentViewersManager.getManager();
        int i = typesList.getSelectedIndex();
        typesList.getSelectedItem();
        contentviewersmanager.removeCurrentViewer();
        int j = typesList.getItemCount() - 1;
        if(i < j)
            contentviewersmanager.setCurrentViewerByDescription(typesList.getItem(i + 1));
        else
            contentviewersmanager.setCurrentViewerByDescription(typesList.getItem(j - 1));
        typesList.remove(i);
        contentviewersmanager.notifyObservers();
        update();
        return true;
    }

    boolean defineNewType()
    {
        ContentViewersManager contentviewersmanager = ContentViewersManager.getManager();
        contentviewersmanager.setCurrentViewerByType("application/other");
        contentviewersmanager.notifyObservers("begin-define-type");
        definingNew = true;
        update();
        return true;
    }

    boolean applyDefinition()
    {
        ContentViewersManager contentviewersmanager = ContentViewersManager.getManager();
        contentviewersmanager.setCurrentViewerByDescription(description.getText());
        contentviewersmanager.notifyObservers("end-define-type");
        return true;
    }

    boolean askForApplication()
    {
        File file = askUserForFile("viewer.chooseapplicationdialog", 0);
        if(file != null)
            application.setText(file.getAbsolutePath());
        return true;
    }

    boolean selectViewer(String s)
    {
        if(s != null)
        {
            ContentViewersManager contentviewersmanager = ContentViewersManager.getManager();
            contentviewersmanager.setCurrentViewerByDescription(s);
            contentviewersmanager.notifyObservers();
            update();
        }
        return true;
    }

    boolean setApplicationFieldEditState()
    {
        int i = actionChoices.getSelectedIndex();
        return setApplicationFieldEditState(i);
    }

    boolean setApplicationFieldEditState(int i)
    {
        switch(i)
        {
        case 0: // '\0'
        case 2: // '\002'
        case 3: // '\003'
            applicationLabel.setEnabled(false);
            application.setEditable(false);
            chooseApplication.setEnabled(false);
            break;

        case 1: // '\001'
            applicationLabel.setEnabled(true);
            application.setEditable(true);
            chooseApplication.setEnabled(true);
            break;
        }
        return true;
    }

    private void fillList(List list)
    {
        ContentViewersManager contentviewersmanager = ContentViewersManager.getManager();
        MimeEntry amimeentry[] = contentviewersmanager.getSortedViewers();
        for(int i = 0; i < amimeentry.length; i++)
            if(!"content/unknown".equals(amimeentry[i].getType()) && !"unknown/unknown".equals(amimeentry[i].getType()))
                list.add(amimeentry[i].getDescription());

    }

    private void insertIntoTypesList(MimeEntry mimeentry)
    {
        insertIntoTypesList(mimeentry.getDescription());
    }

    private void insertIntoTypesList(String s)
    {
        if(descriptionToReplace != null)
            typesList.remove(descriptionToReplace);
        int i = typesList.getItemCount();
        for(int j = 0; j < i; j++)
        {
            int k = typesList.getItem(j).compareTo(s);
            if(k == 0)
                return;
            if(k > 0)
            {
                typesList.add(s, j);
                return;
            }
        }

        typesList.add(s);
    }

    private void resetTypesList()
    {
        if(typesList != null)
        {
            typesList.removeAll();
            fillList(typesList);
        }
    }

    private void add(Panel panel, GridBagLayout gridbaglayout, Component component, int i, int j, int k, int l, 
            int i1, double d, int j1)
    {
        add(panel, gridbaglayout, component, i, j, k, l, i1, d, j1, 5, 5, 5, 5);
    }

    private void add(Panel panel, GridBagLayout gridbaglayout, Component component, int i, int j, int k, int l, 
            int i1, double d, int j1, int k1, int l1)
    {
        add(panel, gridbaglayout, component, i, j, k, l, i1, d, j1, k1, l1, 5, 5);
    }

    private void add(Panel panel, GridBagLayout gridbaglayout, Component component, int i, int j, int k, int l, 
            int i1, double d, int j1, int k1, int l1, int i2, 
            int j2)
    {
        GridBagConstraints gridbagconstraints = new GridBagConstraints();
        gridbagconstraints.insets = new Insets(i2, k1, j2, l1);
        gridbagconstraints.gridx = i;
        gridbagconstraints.gridy = j;
        gridbagconstraints.gridwidth = k;
        gridbagconstraints.gridheight = l;
        gridbagconstraints.anchor = i1;
        gridbagconstraints.weightx = d;
        gridbagconstraints.fill = j1;
        panel.add(component);
        gridbaglayout.setConstraints(component, gridbagconstraints);
    }

    private void add(Panel panel, GridBagLayout gridbaglayout, Component component, int i, int j, int k, int l, 
            int i1, double d, double d1, int j1, int k1, 
            int l1, int i2, int j2)
    {
        GridBagConstraints gridbagconstraints = new GridBagConstraints();
        gridbagconstraints.insets = new Insets(i2, k1, j2, l1);
        gridbagconstraints.gridx = i;
        gridbagconstraints.gridy = j;
        gridbagconstraints.gridwidth = k;
        gridbagconstraints.gridheight = l;
        gridbagconstraints.anchor = i1;
        gridbagconstraints.weightx = d;
        gridbagconstraints.weighty = d1;
        gridbagconstraints.fill = j1;
        panel.add(component);
        gridbaglayout.setConstraints(component, gridbagconstraints);
    }

    private List makeTypesList()
    {
        List list = new List(14);
        fillList(list);
        return list;
    }

    private void update()
    {
        if(!definingNew)
            resetTypesList();
        ContentViewersManager contentviewersmanager = ContentViewersManager.getManager();
        MimeEntry mimeentry = contentviewersmanager.getCurrentViewer();
        String s = mimeentry.getDescription();
        description.setText(s);
        if(!s.equalsIgnoreCase(typesList.getSelectedItem()))
            selectDescribedItem(s);
        if("text/plain".equalsIgnoreCase(mimeentry.getType()))
        {
            delete.setEnabled(false);
            if(actionChoices.getItemCount() > 2)
            {
                actionChoices.remove(2);
                actionChoices.remove(2);
            }
        } else
        {
            delete.setEnabled(true);
            if(actionChoices.getItemCount() < 3)
            {
                String s1 = props.getPropertyReplace("viewer.actionchoice.save", "Save");
                actionChoices.add(s1);
                s1 = props.getPropertyReplace("viewer.actionchoice.ask", "Always Ask");
                actionChoices.add(s1);
            }
        }
        int i = mapActionToChoice(mimeentry.getAction());
        if(i == -1)
            actionChoices.select(0);
        else
            actionChoices.select(i);
        if(!newDefinition.isEnabled())
            newDefinition.setEnabled(true);
        String s2 = mimeentry.getLaunchString() == null ? "" : mimeentry.getLaunchString();
        application.setText(s2);
        setApplicationFieldEditState();
        extensions.setText(mimeentry.getExtensionsAsList());
        String s3 = getViewerType(mimeentry);
        int j = mapTypeToChoice(s3);
        typeChoices.select(j);
        typeChoices.enable(definingNew);
        typeLabel.enable(definingNew);
        String s4 = getViewerSubtype(mimeentry);
        subtypeTextField.setText(s4);
        subtypeTextField.setEditable(definingNew || editable);
        subtypeLabel.enable(definingNew || editable);
        handleTypeChoice(j, s4);
        subtypesList.enable(definingNew || editable);
    }

    private void selectDescribedItem(String s)
    {
        int i = typesList.getItemCount();
        for(int j = 0; j < i; j++)
            if(s.equals(typesList.getItem(j)))
            {
                typesList.select(j);
                typesList.makeVisible(j);
                return;
            }

    }

    private boolean handleTypeChoice()
    {
        return handleTypeChoice(typeChoices.getSelectedIndex());
    }

    private boolean handleTypeChoice(int i)
    {
        return handleTypeChoice(typeChoices.getSelectedIndex(), null);
    }

    private boolean handleTypeChoice(int i, String s)
    {
        try
        {
            if(!typeSubtypeMap.typeHasSubtype(i, s))
                typeSubtypeMap.addSubtype(i, s);
            String as[] = typeSubtypeMap.getSubtypes(i);
            if(as == null)
                return true;
            subtypesList.removeAll();
            for(int j = 0; j < as.length; j++)
                subtypesList.add(as[j]);

            subtypesList.add("other");
            if(s != null)
            {
                String as1[] = subtypesList.getItems();
                for(int k = 0; k < as1.length; k++)
                    if(s.equals(as1[k]))
                    {
                        subtypesList.select(k);
                        subtypesList.makeVisible(k);
                    }

            }
        }
        catch(IndexOutOfBoundsException indexoutofboundsexception)
        {
            indexoutofboundsexception.printStackTrace();
        }
        return true;
    }

    private boolean handleSubtypeChoice()
    {
        String s = subtypesList.getSelectedItem();
        if("other".equalsIgnoreCase(s))
            subtypeTextField.setText("x-");
        else
            subtypeTextField.setText(s);
        return true;
    }

    private int mapTypeToChoice(String s)
    {
        int i = typeChoices.countItems();
        for(int j = 0; j < i; j++)
            if(s.equalsIgnoreCase(typeChoices.getItem(j)))
                return j;

        return 0;
    }

    private String getViewerType(MimeEntry mimeentry)
    {
        String s = mimeentry.getType();
        int i = s.indexOf("/");
        return s.substring(0, i);
    }

    private String getViewerSubtype(MimeEntry mimeentry)
    {
        String s = mimeentry.getType();
        int i = s.indexOf("/");
        return s.substring(i + 1);
    }

    public void clear()
    {
        int i = typesList.getSelectedIndex();
        if(i != -1)
            typesList.deselect(i);
        newDefinition.setEnabled(false);
        delete.setEnabled(false);
        actionChoices.select(1);
        description.setText("");
        extensions.setText("");
        application.setText("");
        application.setEditable(true);
        applicationLabel.setEnabled(true);
        chooseApplication.setEnabled(true);
    }

    public File askUserForFile(String s, int i)
    {
        return askUserForFile(s, i, null);
    }

    public File askUserForFile(String s, int i, String s1)
    {
        HJFrame hjframe = HJWindowManager.getHJWindowManager().getLastFocusHolder();
        File file = null;
        UserFileDialog userfiledialog = new UserFileDialog(hjframe, s, i);
        userfiledialog.setDirectory(getFileDialogDirectory());
        if(s1 != null)
            userfiledialog.setFile(s1);
        userfiledialog.show();
        hjframe.showStatus("");
        String s2 = userfiledialog.getDirectory();
        setFileDialogDirectory(s2);
        String s3 = userfiledialog.getFile();
        if(s3 != null)
        {
            if(s2.endsWith(File.separator))
                s2 = s2.substring(0, s2.lastIndexOf(File.separator));
            file = new File(s2, s3);
            if(i == 1 && !userfiledialog.providesSaveConfirmation() && file.exists())
            {
                ConfirmDialog confirmdialog = new ConfirmDialog("overwrite.file", hjframe);
                confirmdialog.show();
                if(confirmdialog.getAnswer() == 0)
                    file = null;
            }
        }
        return file;
    }

    private String getFileDialogDirectory()
    {
        if(fileDialogDirectory == null)
        {
            fileDialogDirectory = System.getProperty("hotjava.file.dialog.directory");
            if(fileDialogDirectory != null)
            {
                File file = new File(fileDialogDirectory);
                try
                {
                    if(!file.isDirectory())
                        fileDialogDirectory = null;
                }
                catch(SecurityException _ex)
                {
                    fileDialogDirectory = null;
                }
            }
            if(fileDialogDirectory == null)
                if(Boolean.getBoolean("hotjava.file.dialog.use.startup.dir"))
                    fileDialogDirectory = System.getProperty("user.dir");
                else
                    fileDialogDirectory = System.getProperty("user.home");
        }
        return fileDialogDirectory;
    }

    public static void setFileDialogDirectory(String s)
    {
        fileDialogDirectory = s;
    }

    protected int mapChoiceToAction(int i)
    {
        return choiceToActionMapping[i];
    }

    protected int mapActionToChoice(int i)
    {
        return actionToChoiceMapping[i];
    }

    private final String propname = "general.prefs.contentviewer";
    List typesList;
    UserChoice actionChoices;
    UserTextField description;
    UserTextField application;
    UserTextField extensions;
    UIHJButton delete;
    UIHJButton newDefinition;
    UIHJButton chooseApplication;
    UserLabel typesListLabel;
    UserLabel actionLabel;
    UserLabel descriptionLabel;
    UserLabel applicationLabel;
    UserLabel extensionsLabel;
    boolean editable;
    TypeSubtypeMap typeSubtypeMap;
    UserLabel typeLabel;
    UserLabel subtypeLabel;
    UserChoice typeChoices;
    UserTextField subtypeTextField;
    List subtypesList;
    ButtonPanel cmdRowPanel;
    EnterKeyListener keylistener;
    private String descriptionToReplace;
    private boolean definingNew;
    private MimeEntry saved;
    HJBProperties props;
    final String newLabel;
    private static ContentViewersPreferencesDialog singleton = null;
    static String fileDialogDirectory;
    protected static final int CHOICE_INVALID = -1;
    protected static final int CHOICE_VIEW_IN_BROWSER = 0;
    protected static final int CHOICE_VIEW_IN_APPLICATION = 1;
    protected static final int CHOICE_SAVE = 2;
    protected static final int CHOICE_ASK = 3;
    private static final int choiceToActionMapping[] = {
        1, 3, 2, 0
    };
    private static final int actionToChoiceMapping[] = {
        3, 0, 2, 1
    };

}
