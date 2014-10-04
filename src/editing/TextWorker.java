package editing;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JSlider;
import javax.swing.SwingWorker;

import Popups.TextSection;

/** 
 * The swing worker used to perform the text manipulation in the background
 * @author Mathew and Wesley
 *
 */
public class TextWorker extends SwingWorker<Void, String> {
	private Process process;
	private JProgressBar progBar;
	private String cmd;
	private String option;
	
	public TextWorker(String cmd, JProgressBar progressBar, int dur, int fps, String option) {
		this.cmd = cmd;
		this.progBar = progressBar;
		this.option = option;
		progBar.setMaximum(dur*fps);
	}
	
	@Override
	protected Void doInBackground() {
        	ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd);
			try {
				process = builder.start();
				//retrieve output from the error stream
				InputStream stderr = process.getErrorStream();
				BufferedReader br = new BufferedReader(new InputStreamReader(stderr));
				String line;
				while ((line = br.readLine()) != null){
					if (!TextSection.loadCancelled())
						publish(line);
					else
						process.destroy();
				}
			} catch (IOException e) {}
		return null;
	}
	
	@Override
	public void process(List<String> chunks){
		//no need to do anything if it was just a preview
		if (option.equals("preview"))
			return;
		//get the frame number for use in the progress bar
		Pattern pattern = Pattern.compile("frame= *(\\d+) fps=");
		for (String line : chunks){
			Matcher m = pattern.matcher(line);
			if (m.find()){
				progBar.setValue(Integer.parseInt(m.group(1)));
			}
		}
	}

	@Override
    public void done() {
		//no need to do anything if it was just a preview
		if (option.equals("preview"))
			return;
		try {
			process.waitFor();
			progBar.setValue(progBar.getMaximum());
		if (process.exitValue()==0)
			JOptionPane.showMessageDialog(null, "Text added successfully", "Done", JOptionPane.DEFAULT_OPTION);
		else if (process.exitValue() > 0)
			JOptionPane.showMessageDialog(null, "Error occurred.", "Error", JOptionPane.WARNING_MESSAGE);
    	//remove the temp text file
		Path currentRelativePath = Paths.get("");
    	String currentAbsPath = currentRelativePath.toAbsolutePath().toString();
		File file = new File(currentAbsPath + "/.text");
        file.delete();
		} catch (InterruptedException e) {
			JOptionPane.showMessageDialog(null, "Error occurred.");

		}
    }	
	
}
