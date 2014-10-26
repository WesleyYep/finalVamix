package download;

import gui.DownloadPanel;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;
import popups.LoadingScreen;
import state.LanguageSelector;

/** 
 *  
 * This class is used to run the downloads of VAMIX in the background so the user
 * can continue without interruption
 * @author Matt Smith and Wesley Yep
 *
 */
public class DownloadWorker extends SwingWorker<Void, String>{

	private String _cmd;
	private JProgressBar prog;
	private LoadingScreen ls;
	private DownloadPanel panel;
	private Process p;
	/**
	 * This is the standard constructor
	 * @param progress the progress bar that should be updated as the download progresses
	 * @param button the button that must not be active until download is complete
	 */
	public DownloadWorker(String cmd, JProgressBar progress, LoadingScreen ls, DownloadPanel panel) {
		_cmd = cmd;
		prog = progress;
		this.ls = ls;
		this.panel = panel;
	}
	
	/** 
	 * Perform the download in the background
	 */
	@Override
	protected Void doInBackground() throws Exception {
		//create a process builder to run the java process
		ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", _cmd);
		p = builder.start();
		//we also need to get the input stream so we can get the progress of the download
		InputStream out = p.getInputStream();
		BufferedReader stdout = new BufferedReader(new InputStreamReader(out));
		String line;
		//use regex to determine the progress
		Pattern varPattern = Pattern.compile("([0-9]*)%");
		//keep reading until there is no more output from the process
		while ((line = stdout.readLine()) != null) {
			if (isCancelled() || DownloadPanel.isPaused) {
				p.destroy();
				return null;
			}
			Matcher matcher = varPattern.matcher(line);
		
			while (matcher.find()) {
				String var = matcher.group(1);
				publish (var); //update in the background
			}
		}
				
		return null;
	}
	
	/**
	 * As the download is going in the background this will update the 
	 * progress bar on the ED thread, so the user can see what's happening
	 */
	@Override
	protected void process(List<String> list) {
		if (!isCancelled()) {
			for (String cInt:list) {
				prog.setValue(Integer.parseInt(cInt));
			}
		}
		
	}
	
	/**
	 * When finished with the download a popup will tell the user it finished successfully
	 * The progress bar will disappear and the submit button will be activated
	 */
	@Override
	public void done() {
		try {
			p.waitFor();
		} catch (InterruptedException e1) { }
		//get the error code of the process
		int errorCode = p.exitValue();
		if (DownloadPanel.isPaused){
			return; //no need to display anything if it's just paused
		}
		try {
			ls.finishedQuite(); //remove the loading screen
			if (errorCode == 0) { //error code of 0 means that the download was successful
				prog.setValue(100);
				JOptionPane.showMessageDialog(null,
					    getString("downloadSuccess"), getString("done"), JOptionPane.DEFAULT_OPTION);
			} else {
				wgetError(errorCode); //check what error it was
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, getString("downloadCancel"),getString("cancel"), JOptionPane.WARNING_MESSAGE);
		}
		//remove the progress bar
		prog.setVisible(false);
		prog.setValue(0);
		panel.done();
	}
	
	/**
	 * This can be called to cancel the current process
	 */
	public void cancel(){
		p.destroy();
		panel.done();
		JOptionPane.showMessageDialog(null,	getString("downloadCancel"),getString("cancel"), JOptionPane.WARNING_MESSAGE);
	}
	
	/**
	 * This is used to display an error to the user that reflects what has actually gone wrong
	 * @param exitCode
	 */
	private void wgetError(int exitCode) {
		String msg4Error = getString("downloadFailed");
		if (exitCode == 1) {
			msg4Error = getString("downloadErr1");
		} else if (exitCode == 2) {
			msg4Error = getString("downloadErr2");
		} else if (exitCode == 3) {
			msg4Error = getString("downloadErr3");
		} else if (exitCode == 4) {
			msg4Error = getString("downloadErr4");
		} else if (exitCode == 5) {
			msg4Error = getString("downloadErr5");
		} else if (exitCode == 6) {
			msg4Error = getString("downloadErr6");
		} else if (exitCode == 7) {
			msg4Error = getString("downloadErr7");
		} else if (exitCode == 8) {
			msg4Error = getString("downloadErr8");
		} 
		JOptionPane.showMessageDialog(null, msg4Error, getString("error"), JOptionPane.ERROR_MESSAGE);
	}
	
	/**
	 * This method gets the string that is associated with each label, in the correct language
	 * @param label
	 * @return the string for this label
	 */
	private String getString(String label){
		return LanguageSelector.getString(label);
	}	
}
