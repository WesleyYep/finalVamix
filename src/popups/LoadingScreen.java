package popups;

import gui.EditorPanel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.miginfocom.swing.MigLayout;

@SuppressWarnings("serial")
public class LoadingScreen extends JFrame{

	
	private JLabel title = new JLabel("Working hard...");
	private JProgressBar progBar = new JProgressBar(0, 100);
	public boolean isClosed = false;
	private JButton hideBtn = new JButton("Hide");
	private JButton cancelBtn = new JButton("Cancel");
	private EditorPanel ep;
	
	public LoadingScreen(final EditorPanel ep) {
		super("Saving");
		this.ep = ep;
		setSize(300,150);
		setLocation(500, 250);
		setLayout(new MigLayout());
		add(title, "center, wrap");
		add(progBar, "growx, height 20:45:, width 60:300:, wrap");
		add(hideBtn, "align center");
		add(cancelBtn, "align center");
		
		hideBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				setVisible(false);
				ep.addToBottomPanel(progBar);
			}
		});
		
		cancelBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				setVisible(false);
				isClosed = true;
			}
		});
		
		this.addWindowListener(new java.awt.event.WindowAdapter() {
		    @Override
		    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
		        isClosed = true;
		    }
		});
		
	}
	
	public JProgressBar getProgBar() {
		return progBar;
	}
	
//	public void finished() {
//		title.setText("Finished!");
//		try {
//			Thread.sleep(1000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
//		finishedQuite();
//	}
	
	public void finishedQuite() {
		setVisible(false);
	}
	
	public void prepare() {
		progBar.setValue(0);
		setAlwaysOnTop(true);
		setVisible(true);
	}
	
}
