package gui;

import java.awt.Component;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * This screen is used to download either video or audio
 * 
 * @author group 27
 */
public class DownloadPanel extends JPanel {

	/** An unused ID here only to get rid of the warning */
	private static final long serialVersionUID = 2890178349343322712L;

	private JLabel title = new JLabel ("Yeah, cool");
	
	DownloadPanel () {
		title.setAlignmentX(Component.CENTER_ALIGNMENT);
		title.setFont (new Font("Serif", Font.BOLD, 48));
		add(title);
	}
}
