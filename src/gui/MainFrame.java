//package gui;
//
//import java.awt.Dimension;
//import java.awt.Font;
//import java.awt.GraphicsEnvironment;
//import java.awt.event.WindowAdapter;
//import java.awt.event.WindowEvent;
//
//import javax.swing.ImageIcon;
//import javax.swing.JFrame;
//import javax.swing.JPanel;
//import javax.swing.JTabbedPane;
//import javax.swing.SwingUtilities;
//import javax.swing.UIManager;
//import javax.swing.event.ChangeEvent;
//import javax.swing.event.ChangeListener;
//
//import gui.MainFrame;
//
//@SuppressWarnings("serial")
//public class MainFrame extends JFrame{
//// Please do not hack the main frame
//
//	private JTabbedPane mainMenu;
//	private static MainFrame frame;
//	private EditorPanel editorPanel;
//	
//	public MainFrame() {
//		// Set up the main frame
//		super("VAMIX: trailer editor");
//		setSize(1000, 640);
//		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
//		// This is the minimum size that all the components will fit on the screen nicely
//		this.setMinimumSize(new Dimension(800, 600));
//		
//		setIconImage(new ImageIcon(getClass().getClassLoader().getResource("icon.png")).getImage());
//		
//		mainMenu = new JTabbedPane();
//		JPanel mainPanel = new MainPanel(this);
//		editorPanel = new EditorPanel();
//		JPanel downloadPanel = new DownloadPanel();
//		mainMenu.addTab("Main Menu", mainPanel);
//		mainMenu.addTab("Editor", editorPanel);
//		mainMenu.addTab("Download", downloadPanel);
//		mainMenu.addChangeListener(new ChangeListener() { //add the Listener
//			public void stateChanged(ChangeEvent e) {
//	            if(mainMenu.getSelectedIndex()==1){
//	            	System.out.println("wtf");
//	            	editorPanel.createOverlay(frame);
//	            	editorPanel.showPlayer();
//	            }
//	        }
//	    });
//		
//		add(mainMenu);
//	}
//	
//	public static void main(String[] args) {
//		// First this sets the default fonts so that my GUI look really nice
//		GraphicsEnvironment e = GraphicsEnvironment.getLocalGraphicsEnvironment();
//	    Font[] fonts = e.getAllFonts();
//		UIManager.put("Label.font", new Font(fonts[8].getName(), Font.PLAIN, 17));
//		UIManager.put("Button.font", new Font(fonts[5].getName(), Font.BOLD, 15));
//		
//		// Display the gui (its not static so must use invoke later)
//		SwingUtilities.invokeLater(new Runnable() {
//			@Override
//			public void run() {
//				frame = new MainFrame();
//				frame.setVisible(true);
//				frame.addWindowListener(new WindowAdapter()  
//				{  
//				    // override only the method(s) you need  
//					@Override
//				public void windowClosing(WindowEvent e) {
//						frame.editorPanel.stop();
//				}
//				}); 
//			}
//		});
//	}
//	
//	public void setTab(int i) {
//		mainMenu.setSelectedIndex(i);
//	}
//
//}
