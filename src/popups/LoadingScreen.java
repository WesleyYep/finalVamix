package popups;

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
	
	public LoadingScreen() {
		super("Saving");
		setSize(300,150);
		setLocation(500, 250);
		setLayout(new MigLayout());
		add(title, "center, wrap");
		add(progBar, "growx, height 20:45:, width 60:300:, wrap");
		add(new JButton("Hide"));
		add(new JButton("Cancel"));
		progBar.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				if (progBar.getValue() == progBar.getMaximum() && progBar.getValue() != 0) {
					finishedQuite();
				}
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
		setVisible(true);
	}
	
}
