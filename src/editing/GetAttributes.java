package editing;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JOptionPane;

import state.LanguageSelector;

/**
 * This class is used to get certain important media file attributes.
 * It uses linux process commands
 * @author wesley
 *
 */
public class GetAttributes{ 
	
	/**
	 * Get duration of media file
	 * @param inputFile a media file
	 * @return duration of media in seconds
	 */
	public static int getDuration(String inputFile){
		Pattern pattern = Pattern.compile("duration=(\\d+\\.\\d+)");
		try {
			return processLinuxCmd(inputFile, pattern, "dur");
		}catch (FileNotFoundException e) {
			return -1;
		}
	}
	
	/**
	 * Get number of frames of media file
	 * @param inputFile a media file
	 * @return number of frames
	 * 
	 */
	public static int getFrames(String inputFile){
		Pattern pattern = Pattern.compile("nb_frames=(\\d+)");
		try {
			return processLinuxCmd(inputFile, pattern, "fps");
		} catch (FileNotFoundException e) {
			return -1;
		}
	}
	
	/**
	 * Get width of video file
	 * @param inputFile a video file
	 * @return width of video in pixels
	 */
	public static int getWidth(String inputFile)
	{
		Pattern pattern = Pattern.compile("width=(\\d+)");
		try {
			return processLinuxCmd(inputFile, pattern, "fps");
		} catch (FileNotFoundException e) {
			return -1;
		}
	}
	
	/**
	 * Get height of video file
	 * @param inputFile a video file
	 * @return height of video in pixels
	 */
	public static int getHeight(String inputFile)
	{
		Pattern pattern = Pattern.compile("height=(\\d+)");
		try {
			return processLinuxCmd(inputFile, pattern, "fps");
		} catch (FileNotFoundException e) {
			return -1;
		}
	}
	
	/**
	 * processes a linux command and returns certain output
	 * @param inputFile the name of input file
	 * @param p the pattern to match
	 * @param option either frames, duration, width, or height
	 * @return the corresponding output
	 * @throws FileNotFoundException
	 */
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
		} catch (IOException e) {}
		throw new FileNotFoundException();
	}
	
}