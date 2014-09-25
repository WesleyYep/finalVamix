package gui;

import java.awt.Component;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * This is the initial screen the user will see when they start up VAMIX
 * 
 * @author The Team of Awesome
 */
@SuppressWarnings("serial")
public class MainPanel extends JPanel {
	
	private JLabel title = new JLabel("Welcome to");
	private JLabel title2 = new JLabel("VAMIX");
	private JButton editingBtn = new JButton("Start Editing Video");
	private JButton downloadBtn = new JButton("Download Media");
	private MainFrame main;

	
	MainPanel(final MainFrame frame) {
		main = frame;
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		title.setAlignmentX(Component.CENTER_ALIGNMENT);
		title.setFont (new Font("Serif", Font.BOLD, 28));
		title2.setFont (new Font("Serif", Font.BOLD, 118));
		editingBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
		downloadBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
		title2.setAlignmentX(Component.CENTER_ALIGNMENT);
		GraphicsEnvironment e = GraphicsEnvironment.getLocalGraphicsEnvironment();
		Font[] fonts = e.getAllFonts();
		editingBtn.setFont (new Font(fonts[5].getName(), Font.BOLD, 18));
		downloadBtn.setFont (new Font(fonts[5].getName(), Font.BOLD, 18));
		add(Box.createVerticalStrut(20));
		add(title);
		add(title2);
		add(Box.createVerticalStrut(30));
		add(editingBtn);		
		add(Box.createVerticalStrut(10));
		add(downloadBtn);
		
		
		editingBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				main.setTab(1);
			}
		});
		
		downloadBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				main.setTab(2);
			}
		});
	}

}
