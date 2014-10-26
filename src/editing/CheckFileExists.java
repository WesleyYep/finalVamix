package editing;

import java.io.IOException;
import javax.swing.JOptionPane;
import state.LanguageSelector;

/**
 * Simple class that is used statically to check if certain filenames are already in use
 * @author wesley
 *
 */
public class CheckFileExists {
	
	/**
	 * Method that checks if a file exists. Uses a linux process
	 * @param filename name of file to check
	 * @return true if exists, false otherwise
	 */
	public static boolean check(String filename){
		String cmd = "file \"" + filename + "\"";
		//create a processbuilder to deal with the process
		ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c", cmd);
		try {
			Process process = processBuilder.start();
			process.waitFor();
			//now return a boolean based on the outcome of the process
			return (process.exitValue()==0);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, getString("validMediaFile"));
		} catch (InterruptedException e) {
			JOptionPane.showMessageDialog(null, getString("validMediaFile"));
		}
		return false;
	}
	
	/**
	 * This method gets the string that is associated with each label, in the correct language
	 * @param label
	 * @return the string for this label
	 */
	private static String getString(String label){
		return LanguageSelector.getString(label);
	}
}
