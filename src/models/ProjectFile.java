package models;

import gui.Vamix;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import state.LanguageSelector;

/**
 * This class is used to store and load project settings in a text file.
 * Created initially by Matt for a3. Developed further by Wesley
 * @author Matt and Wesley
 *
 */
public class ProjectFile {
	
	private static ProjectFile instance;
	private String fileStarter = "#$%^";
	private Vamix vamix;
	
	/**
	 * use singleton design pattern
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
	/**
	 * This is what actually writes to the file through the stream
	 * @param ps the project settings to write
	 * @param output the output writer
	 * @throws IOException
	 */
	private void writeToFile(ProjectSettings ps, BufferedWriter output) 
			throws IOException {
		output.write(fileStarter + "\n");
		output.write(ps._mediaFile + "\n");
		output.write(ps._audioFile + "\n");
		//need to add all of the text from the list
		for (int i = 0; i < ps._text.size(); i++){
			output.write("%%%\n");
			output.write(ps._text.get(i) + "\n***\n"); // ** as it could be multiLine
			output.write(ps._fontOption.get(i) + "\n");
			output.write(ps._colour.get(i) + "\n");
			output.write(ps._x.get(i) + "\n");
			output.write(ps._y.get(i) + "\n");
			output.write(ps._fontSize.get(i) + "\n");
			output.write(ps._startTime.get(i) + "\n");
			output.write(ps._endTime.get(i) + "\n");
		}
		output.write(ps._downloadUrl + "\n");
		output.write(ps._effectsStartTime + "\n");
		output.write(ps._effectsEndTime + "\n");
		output.write(ps._createGif + "\n");
		output.write(ps._resize + "\n");
		output.write(ps._flipH + "\n");
		output.write(ps._flipV + "\n");
		output.write(ps._inverse + "\n");
		output.write(ps._grayscale + "\n");
	}
	
	/**
	 * Method that reads the settings from an existing file
	 * @param file the name of file to read
	 */
	public void readFile(String file) {
		if (!vamix.isText(file)) {
			JOptionPane.showMessageDialog(null, getString("notValidProj"), getString("error"), JOptionPane.ERROR_MESSAGE);
			return;
		}
        try {
        	File projectFile = new File(file); 
			BufferedReader input = new BufferedReader(new FileReader(projectFile));
			readTheFile(input);
			input.close();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null,	getString("notValidProj"), getString("error") ,JOptionPane.ERROR_MESSAGE);
		}
	}
	/**
	 * Converts the input to project settings
	 * @param input
	 * @throws IOException
	 */
	private void readTheFile(BufferedReader input) throws IOException {
		//firstly check if the file is a valid Vamix project settings file
		if (!(input.readLine()).equals(fileStarter)) {
			JOptionPane.showMessageDialog(null,	getString("notValidProj"), getString("error") ,JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		//general textfields
		String mediaName = input.readLine();
		String audioName = input.readLine();
		
		//text section
		List<String> text = new ArrayList<String>();
		List<String> font = new ArrayList<String>();
		List<String> colour = new ArrayList<String>();
		List<String> x = new ArrayList<String>();
		List<String> y = new ArrayList<String>();
		List<String> fontSize = new ArrayList<String>();
		List<String> startTime = new ArrayList<String>();
		List<String> endTime = new ArrayList<String>();
		String dwURL;
		//go through all the text
		while ((dwURL = input.readLine()).equals("%%%")) {
			String thisText = "";
			String tempText;
			while (!(tempText = input.readLine()).equals("***")) {
				thisText += tempText + "\n";
			}
			text.add(thisText);
			font.add(input.readLine());
			colour.add(input.readLine());
			x.add(input.readLine());
			y.add(input.readLine());
			fontSize.add(input.readLine());
			startTime.add(input.readLine());
			endTime.add(input.readLine());
		}		
		//download section
		String downloadUrl = dwURL;
		
		//effects section
		String est = input.readLine();
		String eet = input.readLine();
		String createGif = input.readLine();
		String resize = input.readLine();
		String flipH = input.readLine();
		String flipV = input.readLine();
		String inverse = input.readLine();
		String grayscale = input.readLine();
		
		ProjectSettings settings = new ProjectSettings( mediaName,  audioName,  text, 
				 font,  colour, x, y, fontSize,  startTime, endTime);
		settings._effectsStartTime = est;
		settings._effectsEndTime = eet;
		settings._createGif = Boolean.parseBoolean(createGif);
		settings._flipH = Boolean.parseBoolean(flipH);
		settings._flipV = Boolean.parseBoolean(flipV);
		settings._resize = Boolean.parseBoolean(resize);
		settings._inverse = Boolean.parseBoolean(inverse);
		settings._grayscale = Boolean.parseBoolean(grayscale);
		settings._downloadUrl = downloadUrl;

		vamix.loadSettings(settings);
	}
	
	/**
	 * This class is the model for all the project settings
	 * @author Matt Smith, Wesley Yep
	 *
	 */
	public class ProjectSettings {
		public String _mediaFile;
		public String _audioFile;
		public List<String> _text;
		public List<String> _fontOption;
		public List<String> _colour;
		public List<String> _x;
		public List<String> _y;
		public List<String> _fontSize;
		public List<String> _startTime;
		public List<String> _endTime;
		public String _downloadUrl;
		public String _effectsStartTime;
		public String _effectsEndTime;
		public boolean _createGif;
		public boolean _resize;
		public boolean _inverse;
		public boolean _grayscale;
		public boolean _flipH;
		public boolean _flipV;
		
		public ProjectSettings(String mediaName, String audioName, List<String> text, List<String> fontOption, 
				List<String> colour, List<String> x, List<String> y, List<String> fontSize, 
				 List<String> startTime, List<String> endTime) {
			_text = text;
			_mediaFile = mediaName;
			_audioFile = audioName;
			 _fontOption = fontOption;
			 _colour  = colour;
			 _x = x;
			 _y = y;
			 _fontSize = fontSize;
			 _endTime = endTime;
			 _startTime = startTime;
		}			
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
