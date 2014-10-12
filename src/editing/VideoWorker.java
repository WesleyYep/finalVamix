package editing;

import gui.TextSection;

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

import popups.LoadingScreen;
import state.LanguageSelector;

/** 
 * The swing worker used to perform the text manipulation in the background
 * @author Mathew and Wesley
 *
 */
public class VideoWorker extends SwingWorker<Void, String> {
	private Process process;
	private JProgressBar progBar;
	private String cmd;
	private String option;
	private LoadingScreen ls;
	private String type;
	private boolean isCancelled = false;
	
	public VideoWorker(String cmd, JProgressBar progressBar, int fps, String option, String type, LoadingScreen ls) {
		this.cmd = cmd;
		this.progBar = progressBar;
		this.option = option;
		this.ls = ls;
		this.type = type;
		progBar.setMaximum(fps);
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
					if (!isCancelled){
						publish(line);
					}
					else{
						process.destroy();
					}
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
		Pattern pattern = Pattern.compile("frame= ?(\\d+)");
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
			ls.finishedQuite();

			if (process.exitValue()==0){
				if (type.equals("Effects")){
					JOptionPane.showMessageDialog(null, getString("effectSuccess"), getString("done"), JOptionPane.DEFAULT_OPTION);
				}else{
					JOptionPane.showMessageDialog(null, getString("textSuccess"), getString("done"), JOptionPane.DEFAULT_OPTION);
				}
			}else if (process.exitValue() > 0)
				JOptionPane.showMessageDialog(null, getString("errorOccurred"), getString("error"), JOptionPane.WARNING_MESSAGE);
	    	//remove the temp text file
			Path currentRelativePath = Paths.get("");
	    	String currentAbsPath = currentRelativePath.toAbsolutePath().toString();
			File file = new File(currentAbsPath + "/.text");
	        file.delete();
		} catch (InterruptedException e) {
			JOptionPane.showMessageDialog(null, getString("errorOccurred"), getString("error"), JOptionPane.WARNING_MESSAGE);
		} 
    }	
	
	public void cancel(){
		isCancelled = true;
	}
	
	private String getString(String label){
		return LanguageSelector.getString(label);
	}
}
