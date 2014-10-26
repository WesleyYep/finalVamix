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
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import state.LanguageSelector;
import net.miginfocom.swing.MigLayout;

/**
 * Loading screen for all the time consuming tasks. Includes a progress bar, and can be hidden
 * @author wesley and matt
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
				vamix.addToBottomPanel(progBar);
				hidden = true;
			}
		});
		
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

	public JProgressBar getProgBar() {
		return progBar;
	}
	
	//for download panel only
	public boolean isHidden(){
		return hidden;
	}
	
	public void finishedQuite() {
		vamix.removeFromBottomPanel(progBar);
		setVisible(false);
	}
	
	public void prepare() {
		progBar.setValue(0);
		setAlwaysOnTop(true);
		setVisible(true);
	}
	
	private static  String getString(String label){
		return LanguageSelector.getString(label);
	}

	public void setWorker(SwingWorker<Void,String> worker) {
		this.worker = worker;
	}
}
