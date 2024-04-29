package httpclient.gui;

import httpclient.entity.Request;
import httpclient.entity.RequestGroup;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

/**
 * Implementation of a panel that placed at the left side of the frame and displays saved requests and request groups.
 */
class SavedRequestsPanel extends JPanel {
    /**
     * hierarchy of request groups and saved requests
     */
    private JTree requestTree;
    /**
     * new request group dialog
     */
    private NewGroupDialog newGroupDialog;
    /**
     * owner frame of the saved requests panel
     */
    private HttpClientGui httpClientGui;

    /**
     * Constructor of the saved requests panel.
     *
     * @param httpClientGui owner frame
     */
    SavedRequestsPanel(HttpClientGui httpClientGui) {
        this.httpClientGui = httpClientGui;
        newGroupDialog = new NewGroupDialog(httpClientGui);
        initUI();
    }

    /**
     * Initializes saved requests panel GUI.
     */
    private void initUI() {
        setLayout(new BorderLayout());
        JPanel newPanel = new JPanel();
        newPanel.setPreferredSize(new Dimension(-1, 65));

        //adding new group button
        JButton newGroupButton = new JButton("New Group");
        newPanel.add(newGroupButton);
        add(newPanel, BorderLayout.NORTH);
        //adding action of new group button
        newGroupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                newGroupDialog.setVisible(true);
            }
        });

        //creating tree hierarchy for request groups and saved requests
        requestTree = new JTree(new DefaultMutableTreeNode());
        //modifying tree cell renderer to show customized icon
        DefaultTreeCellRenderer renderer = new DefaultTreeCellRenderer() {
            @Override
            public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
                super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
                if (value instanceof DefaultMutableTreeNode) {
                    if (((DefaultMutableTreeNode) value).getUserObject() instanceof RequestGroup) {
                        setIcon(new ImageIcon("group.png"));
                    }
                }
                return this;
            }
        };
        renderer.setLeafIcon(null);
        renderer.setClosedIcon(null);
        renderer.setOpenIcon(null);
        requestTree.setCellRenderer(renderer);
        requestTree.setShowsRootHandles(true);
        requestTree.setRootVisible(false);
        requestTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

        JScrollPane scrollPane = new JScrollPane(requestTree);
        add(scrollPane);

        //adding double click action to load saved request
        requestTree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if (e.getClickCount() >= 2) {
                    doMouseDoubleClickedAction();
                }
            }
        });
    }

    /**
     * Action of double clicking on a saved request that loads that request in the request panel.
     */
    private void doMouseDoubleClickedAction() {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) requestTree.getSelectionPath().getLastPathComponent();
        if (node.getUserObject() instanceof Request) {
            httpClientGui.loadRequest((Request) node.getUserObject());
        }
    }

    /**
     * After selecting name of new group by user, adds new group to saves requests hierarchy.
     *
     * @param group new group
     */
    void addGroup(RequestGroup group) {
        addToRoot(group);
    }

    /**
     * Adds an object to saved requests hierarchy.
     *
     * @param item item to add
     */
    private void addToRoot(Object item) {
        DefaultTreeModel model = (DefaultTreeModel) requestTree.getModel();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) requestTree.getModel().getRoot();
        DefaultMutableTreeNode child = new DefaultMutableTreeNode(item);
        model.insertNodeInto(child, root, 0);
        requestTree.scrollPathToVisible(new TreePath(child.getPath()));
    }

    /**
     * Returns all groups that saved before.
     *
     * @return all groups
     */
    List<RequestGroup> getAllGroups() {
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) requestTree.getModel().getRoot();
        List<RequestGroup> result = new ArrayList<RequestGroup>();
        Enumeration children = root.children();
        while (children.hasMoreElements()) {
            Object child = children.nextElement();
            if (child instanceof DefaultMutableTreeNode) {
                if (((DefaultMutableTreeNode) child).getUserObject() instanceof RequestGroup) {
                    result.add((RequestGroup) ((DefaultMutableTreeNode) child).getUserObject());
                }
            }
        }
        return result;
    }

    /**
     * Adds a request to a group.
     *
     * @param request      new request
     * @param requestGroup group to add request
     */
    void addRequestItemToGroup(Request request, RequestGroup requestGroup) {
        //if no group specified, add request to level1 of hierarchy
        if (requestGroup == null) {
            addToRoot(request);
        } else {
            DefaultMutableTreeNode root = (DefaultMutableTreeNode) requestTree.getModel().getRoot();
            Enumeration children = root.children();
            //iterate all level1 nodes to find specified group
            while (children.hasMoreElements()) {
                Object child = children.nextElement();
                if (child instanceof DefaultMutableTreeNode) {
                    DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) child;
                    if (childNode.getUserObject() instanceof RequestGroup) {
                        if (childNode.getUserObject().equals(requestGroup)) {
                            DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(request);
                            DefaultTreeModel model = (DefaultTreeModel) requestTree.getModel();
                            model.insertNodeInto(newNode, childNode, 0);
                            requestTree.scrollPathToVisible(new TreePath(newNode.getPath()));
                        }
                    }
                }
            }
        }
    }

    /**
     * Checks if the specified saved request name exists in the specified group name of not.
     *
     * @param requestName      request name to check
     * @param requestGroupName group name to check
     * @return {@code true} if the request exists in the group, {@code false} otherwise
     */
    public boolean existsRequestInGroup(String requestName, String requestGroupName) {
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) requestTree.getModel().getRoot();
        Enumeration children = root.children();
        //iterate all level1 nodes to find specified group
        while (children.hasMoreElements()) {
            Object child = children.nextElement();
            if (child instanceof DefaultMutableTreeNode) {
                DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) child;
                if (childNode.getUserObject() instanceof RequestGroup) {
                    if (childNode.getUserObject().toString().equals(requestGroupName)) {
                        Enumeration<TreeNode> groupChildren = childNode.children();
                        while (groupChildren.hasMoreElements()) {
                            Object groupChild = groupChildren.nextElement();
                            if (groupChild instanceof DefaultMutableTreeNode) {
                                DefaultMutableTreeNode groupChildNode = (DefaultMutableTreeNode) groupChild;
                                if (groupChildNode.getUserObject() instanceof Request) {
                                    if (((Request) groupChildNode.getUserObject()).getName().equals(requestName)) {
                                        return true;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }
}

