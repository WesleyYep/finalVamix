package popups;

import download.DownloadWorker;
import editing.AudioWorker;
import editing.VideoWorker;
import gui.Vamix;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;
import state.LanguageSelector;
import net.miginfocom.swing.MigLayout;

/**
 * Loading screen for all the time consuming tasks. Includes a progress bar, which can be hidden
 * @author Wesley and Matt
 *
 */
@SuppressWarnings("serial")
public class LoadingScreen extends JFrame{
	
	private JLabel title = new JLabel(getString("workingHard"));
	private JProgressBar progBar = new JProgressBar(0, 100);
	public boolean isClosed = false;
	private JButton hideBtn = new JButton(getString("hide"));
	private JButton cancelBtn = new JButton(getString("cancel"));
	private SwingWorker<Void, String> worker;
	private boolean hidden = false;
	private Vamix vamix;
	
	public LoadingScreen(final Vamix v) {
		super(getString("saving"));
		this.vamix = v;
		
		//set up the frame
		setSize(300,150);
		setLocation(500, 250);
		setLayout(new MigLayout());
		add(title, "center, wrap");
		add(progBar, "growx, height 20:45:, width 60:300:, wrap");
		add(hideBtn, "align center");
		add(cancelBtn, "align center");
		
		//hide the loading screen and put the progress bar on the main screen
		hideBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				setVisible(false);
				vamix.addToBottomPanel(progBar);
				hidden = true;
			}
		});
		
		//cancel the work
		cancelBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				setVisible(false);
				cancel();
			}
		});
		
		this.addWindowListener(new java.awt.event.WindowAdapter() {
		    @Override
		    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
		        isClosed = true;
		    }
		});
		
	}
	
	/**
	 * This method is used to cancel the current work
	 * It will call the cancel method of the respective worker object
	 */
	public void cancel(){
		isClosed = true;
		vamix.removeFromBottomPanel(progBar);
		if (worker instanceof VideoWorker){
			((VideoWorker)worker).cancel();
		}else if (worker instanceof AudioWorker){
			((AudioWorker)worker).cancel();
		}else if (worker instanceof DownloadWorker){
			((DownloadWorker)worker).cancel();
		}
	}
	
	/**
	 * Gets the progress bar of the loading screen
	 */
	public JProgressBar getProgBar() {
		return progBar;
	}
	
	/**
	 * Checks if the loading screen is hidden.
	 * For download panel only
	 * @return true if hidden, false if not hidden
	 */
	public boolean isHidden(){
		return hidden;
	}
	
	/**
	 * This is called when the worker is finished
	 * It will remove the progress bar and make the loading screen invisible
	 */
	public void finishedQuite() {
		vamix.removeFromBottomPanel(progBar);
		setVisible(false);
	}
	
	/**
	 * This is called before work begins
	 * It sets the value of the progress bar, and makes the progress bar appear
	 */
	public void prepare() {
		progBar.setValue(0);
		setAlwaysOnTop(true);
		setVisible(true);
	}
	
	/**
	 * This method gets the string that is associated with each label, in the correct language
	 * @param label
	 * @return the string for this label
	 */
	private static  String getString(String label){
		return LanguageSelector.getString(label);
	}

	/**
	 * This method sets the worker that is being used for this loading screen
	 * @param worker either a download worker, audio worker, or video worker
	 */
	public void setWorker(SwingWorker<Void,String> worker) {
		this.worker = worker;
	}
}
