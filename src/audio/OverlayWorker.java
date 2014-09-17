package audio;

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

public class OverlayWorker extends SwingWorker<Void, String> {
	private Process process;
	private String videoFile;
	private String audioFile;
	private String option;
	private String file;
	private JProgressBar progBar;
	private String message = "";
	private int fps = 0;
	private int secs = 0;
	
	public OverlayWorker(String videoFile, String audioFile, String option, String file, JProgressBar progBar) {
		this.videoFile = videoFile;
		this.audioFile = audioFile;
		this.option = option;
		this.file = file;
		this.progBar = progBar;
	}
	
	@Override
	protected Void doInBackground() {
    	String cmd;
    	if (option.equals("strip")){
    		cmd = "avconv -i " + videoFile + " -an -c:v copy " + file;
    		message = "Audio track removed";
    	}
    	else if (option.equals("overlay")){
    		cmd = "avconv -i " +  videoFile + " -i " + audioFile +
    				" -filter_complex amix=inputs=2 -strict experimental " + file;
    		message = "Audio and video overlaid.";
    	}
    	else{
    		cmd = "avconv -i " +  videoFile + " -i " + audioFile +
    				" -map 0:0 -map 1:0 -codec copy -v debug " + file;
    		message = "Audio track replaced";
    	}
    //	System.out.println(cmd);
        	ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd);
			try {
				process = builder.start();
				//retrieve output from the errorstream
				InputStream stderr = process.getErrorStream();
				BufferedReader br = new BufferedReader(new InputStreamReader(stderr));
				String line;
				while ((line = br.readLine()) != null){
		//			System.out.println(line);
					publish(line);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return null;
	}
	
	@Override
	public void process(List<String> chunks){
		//get the maximum progress bar by multiplying fps by seconds
		Pattern pattern = Pattern.compile("(\\d+) fps");
		Pattern pattern1 = Pattern.compile("(\\d\\d):(\\d\\d):(\\d\\d)\\.\\d\\d,");
		Pattern pattern2 = Pattern.compile("frame= *(\\d+) fps=");
		boolean gettingMax = true;
		for (String line : chunks){
			Matcher m = pattern.matcher(line);
			Matcher m1 = pattern1.matcher(line);
			Matcher m2 = pattern2.matcher(line);
			//search for fps
			if (gettingMax && fps == 0 && m.find()){
			    fps = Integer.parseInt(m.group(1));
			    progBar.setMaximum(fps*secs);
			}
			//search for seconds, the duration of the Video
			else if (gettingMax && m1.find()){
				int newSecs = Integer.parseInt(m1.group(1)) * 60 * 60 + Integer.parseInt(m1.group(2)) * 60 
						+ Integer.parseInt(m1.group(3));
				if (newSecs > secs){
					secs = newSecs;
					progBar.setMaximum(fps*secs);
				}
			}
			//search for frame number
			if (m2.find()){
				progBar.setValue(Integer.parseInt(m2.group(1)));
				gettingMax = false;
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
		else
			JOptionPane.showMessageDialog(null, "Error occurred.", "Error", JOptionPane.WARNING_MESSAGE);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }	
	
}
