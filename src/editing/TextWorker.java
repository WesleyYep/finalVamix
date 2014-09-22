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

/** 
 * The swing worker used to perform the text manipulation in the background
 * @author Mathew and Wesley
 *
 */
public class TextWorker extends SwingWorker<Void, String> {
	private Process process;
	private JProgressBar progBar;
	private String cmd;
	
	public TextWorker(String cmd, JProgressBar progressBar, int dur, int fps) {
		this.cmd = cmd;
		this.progBar = progressBar;
		progBar.setMaximum(dur*fps);
	}
	
	@Override
	protected Void doInBackground() {
        	ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd);
			try {
				System.out.println(cmd);
				process = builder.start();
				//retrieve output from the error stream
				InputStream stderr = process.getErrorStream();
				BufferedReader br = new BufferedReader(new InputStreamReader(stderr));
				String line;
				while ((line = br.readLine()) != null){
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
			Pattern pattern = Pattern.compile("frame= *(\\d+) fps=");
			for (String line : chunks){
				Matcher m = pattern.matcher(line);
				//search for frame number
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
		if (process.exitValue()==0)
			JOptionPane.showMessageDialog(null, "Text added successfully", "Done", JOptionPane.DEFAULT_OPTION);
		else
			JOptionPane.showMessageDialog(null, "Error occurred.", "Error", JOptionPane.WARNING_MESSAGE);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }	
	
}
