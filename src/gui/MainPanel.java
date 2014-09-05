package gui;

import java.awt.Component;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * This is the initial screen the user will see when they start up VAMIX
 * 
 * @author The Team of Awesome
 */
public class MainPanel extends JPanel {

	/** Another ID here purely to get rid of the warning */
	private static final long serialVersionUID = -8438576029794021570L;
	
	private JLabel title = new JLabel("Welcome to VAMIX");
	
	MainPanel() {
		title.setAlignmentX(Component.CENTER_ALIGNMENT);
		title.setFont (new Font("Serif", Font.BOLD, 48));
		add(title);
	}

}
