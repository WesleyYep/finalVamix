package components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.io.File;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

/**
 * 
 * The code for this class was taken from:
 * http://stackoverflow.com/questions/13735987/how-to-set-transparent-background-for-jtree-cell
 *
 */
public class TreePane extends JPanel {

    private JTree tree;

    public TreePane() {
        // must be set this before creating any trees
        UIManager.put("Tree.rendererFillBackground", false);

        setLayout(new BorderLayout());
        tree = new JTree();
        tree.setBackground(new Color(0,0,0,10));

        File root = new File("/");
        DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(root.getName());
        for (File file : root.listFiles()) {
            rootNode.add(new DefaultMutableTreeNode(file.getName()));
        }
        DefaultTreeModel model = new DefaultTreeModel(rootNode);
        tree.setModel(model);

        add(new JScrollPane(tree));
    }
}
