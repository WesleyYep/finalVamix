package download;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

/** 
 * This has been taken from my assignment 2 build of VAMIX but should still work with 
 * this assignment (perhaps with a few modifications)
 * 
 * This class is used to run the downloads of VAMIX in the background so the user
 * can continue without interruption
 * @author Mathew Smith, msmi498
 *
 */
public class Bubba extends SwingWorker<Integer, String>{

	private String _cmd;
	private JProgressBar prog;
	private JButton submitBtn;
	
	/**
	 * This is the standard constructor for Bubba
	 * @param cmd this is the bash command to execute
	 * @param progress the progress bar that should be updated as the download progresses
	 * @param button the button that must not be active until download is complete
	 */
	public Bubba(String cmd, JProgressBar progress, JButton button) {
		_cmd = cmd;
		prog = progress;
		submitBtn = button;
	}
	
	/** 
	 * Perform the download in the background
	 */
	@Override
	protected Integer doInBackground() throws Exception {
		
		ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", _cmd);
		Process process = builder.start();
		
		InputStream out = process.getInputStream();
		BufferedReader stdout = new BufferedReader(new InputStreamReader(out));
		String line;
		Pattern varPattern = Pattern.compile("([0-9]*)%");
		while ((line = stdout.readLine()) != null) {
			if (isCancelled()) {
				process.destroy();
				return null;
			}
			Matcher matcher = varPattern.matcher(line);
		
			while (matcher.find()) {
				String var = matcher.group(1);
				
				publish (var);
			}
		}
		
		int errorCode = process.waitFor();
		
		return errorCode;
	}
	
	/**
	 * As the download is going in the background this will update the 
	 * progress bar so the user can see what's happening
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
			if (get() == 0) {
				prog.setValue(100);
				JOptionPane.showMessageDialog(new JFrame(),
					    "Download successful",
					    "Done",
					    JOptionPane.DEFAULT_OPTION);
			
			} else {
				wgetError(get());
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(new JFrame(),
				    "Download Cancelled",
				    "Cancel",
				    JOptionPane.WARNING_MESSAGE);
		}
		
		prog.setVisible(false);
		prog.setValue(0);
		submitBtn.setText("Start Download");
	}
	
	/**
	 * This is used to display an error to the user that reflects what has actually gone wrong
	 * @param exitCode
	 */
	private void wgetError(int exitCode) {
		String msg4Error = "Download failed";
		if (exitCode == 1) {
			msg4Error = "Failed to download - Generic error";
		} else if (exitCode == 2) {
			msg4Error = "Failed to download - Parse error";
		} else if (exitCode == 3) {
			msg4Error = "Failed to download - File error";
		} else if (exitCode == 4) {
			msg4Error = "Failed to download - Network failure";
		} else if (exitCode == 5) {
			msg4Error = "Failed to download - Verification error";
		} else if (exitCode == 6) {
			msg4Error = "Failed to download - Authentication failure";
		} else if (exitCode == 7) {
			msg4Error = "Failed to download - Protocol error";
		} else if (exitCode == 8) {
			msg4Error = "Failed to download - Server error";
		} 
		JOptionPane.showMessageDialog(new JFrame(),
					    msg4Error,
					    "Error",
					    JOptionPane.ERROR_MESSAGE);
	}
		
}
