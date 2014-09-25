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

public class AudioWorker extends SwingWorker<Void, String> {
	private Process process;
	private String videoFile;
	private String audioFile;
	private String option;
	private String file;
	private JProgressBar progBar;
	private String message = "";
	
	public AudioWorker(String videoFile, String audioFile, String option, String file, JProgressBar progressBar,
						int duration, int fps){
		this.videoFile = videoFile;
		this.audioFile = audioFile;
		this.option = option;
		this.file = file;
		this.progBar = progressBar;
		progBar.setMaximum(fps*duration);
	}
	
	@Override
	protected Void doInBackground() {
    	String cmd;
    	if (option.equals("stripAudio")){
    		cmd = "avconv -i " + videoFile + " -an -c:v copy -f mp4 " + file;
    		message = "Audio track removed";
    	}
    	else  if (option.equals("stripVideo")){
    		cmd = "avconv -i " + videoFile + " -vn -c:v copy -f mp3 " + file;
    		message = "Video track removed";

    	}	
    	else if (option.equals("overlay")){
    		cmd = "avconv -i " +  videoFile + " -i " + audioFile +
    				" -filter_complex amix=inputs=2 -strict experimental -v debug -f mp4 " + file;
    		message = "Audio and video overlaid.";
    	}
    	else{
    		cmd = "avconv -i " +  videoFile + " -i " + audioFile +
    				" -map 0:v -map 1:a -codec copy -f mp4 " + file;
    		message = "Audio track replaced";
    	}
        	ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd);
			try {
				process = builder.start();
				//retrieve output from the errorstream
				InputStream stderr = process.getErrorStream();
				BufferedReader br = new BufferedReader(new InputStreamReader(stderr));
				String line;
				while ((line = br.readLine()) != null){
					publish(line);
				}
			} catch (IOException e) {
				JOptionPane.showMessageDialog(null, "Error occurred.");
			}
		return null;
	}
	
	@Override
	public void process(List<String> chunks){
		Pattern pattern = Pattern.compile("frame= *(\\d+) fps=");
		for (String line : chunks){
			Matcher m = pattern.matcher(line);
			//search for frame number
			if (m.find()){
				progBar.setValue(Integer.parseInt(m.group(1)));
//				gettingMax = false;
			}
		}
	}

	@Override
    public void done() {
		try {
			process.waitFor();
			progBar.setValue(progBar.getMaximum());
		if (process.exitValue()==0)
			JOptionPane.showMessageDialog(null, message, "Done", JOptionPane.DEFAULT_OPTION);
		else if (option.equals("stripVideo"))
			JOptionPane.showMessageDialog(null, "This media file has no audio track.", "Error", JOptionPane.WARNING_MESSAGE);
		else if (option.equals("stripAudio"))
			JOptionPane.showMessageDialog(null, "This media file has no video track.", "Error", JOptionPane.WARNING_MESSAGE);
		else
			JOptionPane.showMessageDialog(null, "Error occurred.", "Error", JOptionPane.WARNING_MESSAGE);
		} catch (InterruptedException e) {
			JOptionPane.showMessageDialog(null, "Error occurred.");
		}
		
    }	
	
}
