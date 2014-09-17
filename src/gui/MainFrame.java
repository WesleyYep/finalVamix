package gui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import gui.MainFrame;

public class MainFrame extends JFrame{
// Please do not hack the main frame

	/** This is an auto generated ID to get rid of the warning*/
	private static final long serialVersionUID = 4714864527745266449L;

	private JTabbedPane mainMenu;
	EditorPanel editorPanel;
	
	public MainFrame() {
		// Set up the main frame
		super("VAMIX: trailer editor");
		setSize(1000, 600);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		// This is the minimum size that all the components will fit on the screen nicely
		// TODO adjust this when we actually have a working gui
		this.setMinimumSize(new Dimension(600, 300));
		
		
		mainMenu = new JTabbedPane();
		JPanel mainPanel = new MainPanel();
		editorPanel = new EditorPanel();
		JPanel downloadPanel = new DownloadPanel();
		mainMenu.addTab("Main Menu", mainPanel);
		mainMenu.addTab("Editor", editorPanel);
		mainMenu.addTab("Download", downloadPanel);
		
		add(mainMenu);
	}
	
	public static void main(String[] args) {
		// First this sets the default fonts so that my GUI look really nice
		GraphicsEnvironment e = GraphicsEnvironment.getLocalGraphicsEnvironment();
	    Font[] fonts = e.getAllFonts();
		UIManager.put("Label.font", new Font(fonts[8].getName(), Font.PLAIN, 17));
		UIManager.put("Button.font", new Font(fonts[5].getName(), Font.BOLD, 15));
		
		// Display the gui (its not static so must use invoke later)
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				final MainFrame frame = new MainFrame();
				frame.setVisible(true);
				frame.addWindowListener(new WindowAdapter()  
				{  
				    // override only the method(s) you need  
					@Override
				public void windowClosing(WindowEvent e) {
						frame.editorPanel.stop();
				}
				}); 
			}
		});
		
	}

}
