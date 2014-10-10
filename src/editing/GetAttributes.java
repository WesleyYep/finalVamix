package editing;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

public class GetAttributes{ 
	
	public static int getDuration(String inputFile){
		//Pattern pattern = Pattern.compile("Duration: (\\d\\d):(\\d\\d):(\\d\\d)\\.\\d\\d, ");
		Pattern pattern = Pattern.compile("duration=(\\d+\\.\\d+)");
		try {
			return processLinuxCmd(inputFile, pattern, "dur");
		}catch (FileNotFoundException e) {
			return -1;
		}
	}
	
	public static int getFrames(String inputFile){
		//Pattern pattern = Pattern.compile(", (\\d+)(\\.\\d+)? fps");
		Pattern pattern = Pattern.compile("nb_frames=(\\d+)");
		try {
			return processLinuxCmd(inputFile, pattern, "fps");
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(null, "Unsupported input file", "Error", JOptionPane.DEFAULT_OPTION);
			return -1;
		}
	}
	
	
	private static int processLinuxCmd(String inputFile, Pattern p, String option) throws FileNotFoundException{
		String cmd = "avprobe -show_streams " + inputFile;
		ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd);
		try {	
			Process process = builder.start();
			//retrieve output from the errorstream
			InputStream stderr = process.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(stderr));
			String line;
			while ((line = br.readLine()) != null){
				Matcher m = p.matcher(line);
				if (m.find()){
					try{
						if (option.equals("dur")){
							int  dur = (int)Double.parseDouble(m.group(1));
							return dur;//duration
						}
						else if (option.equals("fps"))
							return Integer.parseInt(m.group(1));//fps
					}catch (NumberFormatException e){
						//
					}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		throw new FileNotFoundException();
	}
	
}