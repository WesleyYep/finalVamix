package editing;

import gui.Vamix;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JOptionPane;

import state.LanguageSelector;

/**
 * This class is used to store and load project settings in a text file.
 * Created initially by matt for a3. Developed further by wesley
 * @author matt and wesley
 *
 */
public class ProjectFile {
	
	private static ProjectFile instance;
	private String fileStarter = "#$%^";
	private Vamix vamix;
	
	/**
	 * use singleton design patter
	 * @param v the reference to the Vamix object
	 * @return the corresponding project file
	 */
	public static ProjectFile getInstance(Vamix v) {
		if (instance == null) {
			instance = new ProjectFile(v);
		}
		return instance;
	}
	private ProjectFile(Vamix ep) {
		vamix = ep;
	}
	
	/**
	 * Method to open up the stream to write to a file
	 * @param projSettings the settings to be written
	 * @param fileName name of file to be written to
	 */
	public void writeFile(ProjectSettings projSettings, String fileName) {
		try {
	          File projectFile = new File(fileName); 
	          BufferedWriter output = new BufferedWriter(new FileWriter(projectFile));
	          writeToFile(projSettings, output);
	          output.close();
	        } catch ( IOException e ) {
	           e.printStackTrace();
	        }
	}
	//this actually writes to the file through the stream
	private void writeToFile(ProjectSettings ps, BufferedWriter output) 
			throws IOException {
		output.write(fileStarter + "\n");
		output.write(ps._mediaFile + "\n");
		output.write(ps._audioFile + "\n");
		output.write(ps._titleText + "\n***\n"); // ** as it could be multiLine
		output.write(ps._creditsText + "\n***\n");
		output.write(ps._title_credits + "\n");
		output.write(ps._fontOption + "\n");
		output.write(ps._colour+ "\n");
		output.write(ps._x + "\n");
		output.write(ps._y + "\n");
		output.write(ps._fontSize + "\n");
		output.write(ps._duration + "\n");
		output.write(ps._downloadUrl + "\n");
		output.write(ps._speed + "\n");
		output.write(ps._effectsStartTime + "\n");
		output.write(ps._effectsEndTime + "\n");
		output.write(ps._createGif + "\n");
		output.write(ps._flipH + "\n");
		output.write(ps._flipV + "\n");
		output.write(ps._fadeS + "\n");
		output.write(ps._fadeE + "\n");
	}
	
	/**
	 * Method that reads the settings from an existing file
	 * @param file the name of file to read
	 */
	public void readFile(String file) {
		if (!vamix.isText(file)) {
			JOptionPane.showMessageDialog(null,
    				getString("notValidProj"), getString("error")
    				,JOptionPane.ERROR_MESSAGE);
			return;
		}
        try {
        	File projectFile = new File(file); // Need to change this
			BufferedReader input = new BufferedReader(new FileReader(projectFile));
			readTheFile(input);
			input.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null,
					getString("notValidProj"), getString("error")
    				,JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
		}
	}
	//converts the input to settings
	private void readTheFile(BufferedReader input) throws IOException {
		String temp;
		if (!(temp = input.readLine()).equals(fileStarter)) {
			JOptionPane.showMessageDialog(null,
					getString("notValidProj"), getString("error")
    				,JOptionPane.ERROR_MESSAGE);
			return;
		}
		String mediaName = input.readLine();
		String audioName = input.readLine();
		String titleText = "";
		String tempText;
		while (!(tempText = input.readLine()).equals("***")) {
			titleText += tempText + "\n";
		}
		String creditsText = "";
		while (!(tempText = input.readLine()).equals("***")) {
			creditsText += tempText + "\n";
		}
		String titleOrCredits = input.readLine();
		String font = input.readLine();
		String colour = input.readLine();
		String x = input.readLine();
		String y = input.readLine();
		String fontSize = input.readLine();
		
		String duration = input.readLine();
		
		String downloadUrl = input.readLine();
		String speed = input.readLine();
		String est = input.readLine();
		String eet = input.readLine();
		String createGif = input.readLine();
		String flipH = input.readLine();
		String flipV = input.readLine();
		String fadeS = input.readLine();
		String fadeE = input.readLine();
		
		ProjectSettings settings = new ProjectSettings( mediaName,  audioName,  titleText, 
				 creditsText,  Integer.parseInt(titleOrCredits),  Integer.parseInt(font),  colour, 
				 Integer.parseInt(x), Integer.parseInt(y), Integer.parseInt(fontSize),  duration);
		settings._speed = Integer.parseInt(speed);
		settings._effectsStartTime = est;
		settings._effectsEndTime = eet;
		settings._createGif = Boolean.parseBoolean(createGif);
		settings._flipH = Boolean.parseBoolean(flipH);
		settings._flipV = Boolean.parseBoolean(flipV);
		settings._fadeS = Boolean.parseBoolean(fadeS);
		settings._fadeE = Boolean.parseBoolean(fadeE);
		settings._downloadUrl = downloadUrl;

		vamix.loadSettings(settings);
	}
	
	/**
	 * This class is used to house all the project settings
	 * @author Matt Smith, Wesley Yep
	 *
	 */
	public class ProjectSettings {
		public String _mediaFile;
		public String _audioFile;
		public String _titleText;
		public String _creditsText;
		public int _title_credits;
		public int _fontOption;
		public String _colour;
		public int _x;
		public int _y;
		public Integer _fontSize;
		public String _duration;
		public String _downloadUrl;
		public int _speed;
		public String _effectsStartTime;
		public String _effectsEndTime;
		public boolean _createGif;
		public boolean _flipH;
		public boolean _flipV;
		public boolean _fadeS;
		public boolean _fadeE;
		
		public ProjectSettings(String mediaName, String audioName, String titleText, 
				String creditsText, int titleOrCredits, int fontOption, String string, 
				int x, int y, Integer fontSize, String duration) {
			_mediaFile = mediaName;
			_audioFile = audioName;
			_titleText = titleText;
			_creditsText = creditsText;
			 _title_credits = titleOrCredits;
			 _fontOption = fontOption;
			 _colour  = string;
			 _x = x;
			 _y = y;
			 _fontSize = fontSize;
			 _duration = duration;
		}			
	}
	
	private String getString(String label){
		return LanguageSelector.getString(label);
	}
}
