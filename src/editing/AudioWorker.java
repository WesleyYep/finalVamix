package editing;

import java.io.BufferedReader;
import java.io.IOException;
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
 * This class is used to process audio editing commands in a background thread.
 * Commands for strip audio, strip video, replace, and overlay are used through this worker
 * @author Wesley
 *
 */
public class AudioWorker extends SwingWorker<Void, String> {
	private Process process;
	private String videoFile;
	private String audioFile;
	private String option;
	private String file;
	private JProgressBar progBar;
	private String message = "";
	private LoadingScreen ls;
	private boolean isCancelled;
	
	public AudioWorker(String videoFile, String audioFile, String option, String file, JProgressBar progressBar,
						int duration, int frames, LoadingScreen ls){
		this.videoFile = videoFile;
		this.audioFile = audioFile;
		this.option = option;
		this.file = file;
		this.progBar = progressBar;
		progBar.setMaximum(frames);
		this.ls = ls;
	}
	
	@Override
	protected Void doInBackground() {
    	String cmd;
    	//strip the audio and save the video without audio
    	if (option.equals("stripAudio")){
    		cmd = "avconv -y -i \"" + videoFile + "\" -an -c:v copy -f mp4 \"" + file + "\"";
    		message = getString("audioRemoved");
    	}
    	//strip, and save the audio separately
    	else  if (option.equals("stripVideo")){
    		cmd = "avconv -y -i \"" + videoFile + "\" -vn -c:v copy -f mp3 \"" + file + "\"";
    		message = getString("videoRemoved");

    	}
    	//overlay the separate audio onto the existing media
    	else if (option.equals("overlay")){
    		cmd = "avconv -y -i \"" +  videoFile + "\" -i \"" + audioFile +
    				"\" -filter_complex amix=inputs=2 -strict experimental -v debug -f mp4 \"" + file + "\"";
    		message = getString("videoOverlaid");
    	}
    	//replace the audio stream from the media with the separate audio
    	else{
    		cmd = "avconv -y -i \"" +  videoFile + "\" -i \"" + audioFile +
    				"\" -map 0:v -map 1:a -codec copy -f mp4 \"" + file + "\"";
    		message = getString("audioReplaced");
    	}
    	ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd);
		try {
			process = builder.start();
			//retrieve output from the errorstream
			InputStream stderr = process.getErrorStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(stderr));
			String line;
			while ((line = br.readLine()) != null){
				if (!isCancelled)
					publish(line);
				else
					process.destroy();
			}
		} catch (IOException e) {}
		return null;
	}
	
	@Override
	public void process(List<String> chunks){
		//use regex to update progress bar based on the current frame that has been processed
		Pattern pattern = Pattern.compile("frame= *(\\d+) fps=");
		for (String line : chunks){
			Matcher m = pattern.matcher(line);
			//search for frame number for use for progress bar
			if (m.find()){
				progBar.setValue(Integer.parseInt(m.group(1)));
			}
		}
	}

	@Override
    public void done() {
		try {
			process.waitFor();
			progBar.setValue(progBar.getMaximum());
			ls.finishedQuite(); //remove the loading screen
			//now generate the appropriate error message
			if (process.exitValue()==0)
				JOptionPane.showMessageDialog(null, message, "Done", JOptionPane.DEFAULT_OPTION);
			else if (option.equals("stripVideo"))
				JOptionPane.showMessageDialog(null, getString("noAudioTrack"), getString("error"), JOptionPane.WARNING_MESSAGE);
			else if (option.equals("stripAudio"))
				JOptionPane.showMessageDialog(null, getString("noVideoTrack"), getString("error"), JOptionPane.WARNING_MESSAGE);
			else
				JOptionPane.showMessageDialog(null, getString("errorOccurred"), getString("error"), JOptionPane.WARNING_MESSAGE);
			}
		catch (InterruptedException e) {
			JOptionPane.showMessageDialog(null, getString("errorOccurred"));
		}
    }	
	
	/**
	 * Other methods can call this to cancel the audio editing process thread.
	 */
	public void cancel(){
		isCancelled = true;
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
