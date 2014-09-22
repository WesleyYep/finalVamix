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
@SuppressWarnings("serial")
public class MainPanel extends JPanel {
	
	private JLabel title = new JLabel("Welcome to VAMIX");
	
	MainPanel() {
		title.setAlignmentX(Component.CENTER_ALIGNMENT);
		title.setFont (new Font("Serif", Font.BOLD, 48));
		add(title);
	}

}
