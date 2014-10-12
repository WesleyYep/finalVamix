package editing;

import java.io.IOException;
import javax.swing.JOptionPane;
import state.LanguageSelector;

public class CheckFileExists {
	
	public static boolean check(String filename){
		String cmd = "file \"" + filename + "\"";
		ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c", cmd);
		try {
			Process process = processBuilder.start();
			process.waitFor();
			return (process.exitValue()==0);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, getString("validMediaFile"));
		} catch (InterruptedException e) {
			JOptionPane.showMessageDialog(null, getString("validMediaFile"));
		}
		return false;
	}
	
	private static String getString(String label){
		return LanguageSelector.getString(label);
	}
}
