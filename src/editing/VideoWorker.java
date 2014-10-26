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
import javax.swing.SwingWorker;
import popups.LoadingScreen;
import state.LanguageSelector;

/** 
 * The swing worker used to perform both the text manipulation and the addition of video effects in the background.
 * It can add multiple text at once, it just depends on the command that it receives.
 * It is also used to add effects such as trim, resize, gif creation, flips, and colour inverse/grayscale
 * The worker will then send progress details to the loading screen
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
	private int numberOfTextFiles = 0;
	
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
				//keep reading the inputstream until there is nothing more to read
				while ((line = br.readLine()) != null){
					if (!isCancelled){ //don't change the progress bar if the job gets cancelled
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
			//if successful, check the type so we know which message to display
			if (process.exitValue()==0){
				if (type.equals("Effects")){
					JOptionPane.showMessageDialog(null, getString("effectSuccess"), getString("done"), JOptionPane.DEFAULT_OPTION);
				}else{
					JOptionPane.showMessageDialog(null, getString("textSuccess"), getString("done"), JOptionPane.DEFAULT_OPTION);
				}
			}else if (process.exitValue() > 0)
				JOptionPane.showMessageDialog(null, getString("errorOccurred"), getString("error"), JOptionPane.WARNING_MESSAGE);
	    	//remove the temp text files that were used to add text
			for (int i = 0; i < numberOfTextFiles; i++){
				Path currentRelativePath = Paths.get("");
		    	String currentAbsPath = currentRelativePath.toAbsolutePath().toString();
				File file = new File(currentAbsPath + "/.text" + i);
		        file.delete();
			}
		} catch (InterruptedException e) {
			JOptionPane.showMessageDialog(null, getString("errorOccurred"), getString("error"), JOptionPane.WARNING_MESSAGE);
		} 
    }	
	
	/**
	 * This method is used by others to cancel the process thread
	 */
	public void cancel(){
		isCancelled = true;
	}
	
	/**
	 * This method is used by TextSection to tell the videoworker how many temporary text files there are
	 * @param number the number of temporary hidden text files
	 */
	public void setNumberOfTextFiles(int number){
		numberOfTextFiles = number;
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
