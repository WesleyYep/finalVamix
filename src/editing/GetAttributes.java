package editing;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GetAttributes{ 
	
	public static int getDuration(String inputFile){
		Pattern pattern = Pattern.compile("Duration: (\\d\\d):(\\d\\d):(\\d\\d)\\.\\d\\d, ");
		return Integer.parseInt(processLinuxCmd(inputFile, pattern, "dur"));
	}
	
	public static int getFPS(String inputFile){
		Pattern pattern = Pattern.compile(", (\\d+)(\\.\\d+)? fps");
		return Integer.parseInt(processLinuxCmd(inputFile, pattern, "fps"));
	}
	
	public static String getTitle(String inputFile){
		Pattern pattern = Pattern.compile("title.*: (.*)");
		return processLinuxCmd(inputFile, pattern, "title");
	}
	
	public static String getCredits(String inputFile){
		Pattern pattern = Pattern.compile("comment.*: (.*)");
		return processLinuxCmd(inputFile, pattern, "credits");
	}
	
	private static String processLinuxCmd(String inputFile, Pattern p, String option){
		String cmd = "avconv -i " + inputFile;
		ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd);
		try {	
			Process process = builder.start();
			//retrieve output from the errorstream
			InputStream stderr = process.getErrorStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(stderr));
			String line;
			while ((line = br.readLine()) != null){
				Matcher m = p.matcher(line);
				if (m.find()){
					try{
						if (option.equals("dur")){
							int  fps = Integer.parseInt(m.group(1)) * 60 * 60 + Integer.parseInt(m.group(2)) * 60 
									+ Integer.parseInt(m.group(3));
							return Integer.toString(fps);//duration
						}
						else if (option.equals("fps"))
							return m.group(1);//fps
						else if (option.equals("title"))
							return m.group(1); //title
						else if (option.equals("credits"))
							return m.group(1); //credits
					}catch (NumberFormatException e){
						//
					}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
	
}