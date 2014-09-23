package editing;

import gui.EditorPanel;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ProjectFile {
	
	private static ProjectFile instance;
	
	private EditorPanel editorP;
	
	public static ProjectFile getInstance(EditorPanel ep) {
		if (instance == null) {
			instance = new ProjectFile(ep);
		}
		return instance;
	}
	private ProjectFile(EditorPanel ep) {
		editorP = ep;
	}
	
	public void writeFile(ProjectSettings projSettings, String fileName) {
		try {
	          File projectFile = new File(fileName); // Need to change this
	          BufferedWriter output = new BufferedWriter(new FileWriter(projectFile));
	          writeToFile(projSettings, output);
	          output.close();
	        } catch ( IOException e ) {
	           e.printStackTrace();
	        }
	}
	private void writeToFile(ProjectSettings ps, BufferedWriter output) 
			throws IOException {
		output.write(ps._mediaFile + "\n");
		output.write(ps._audioFile + "\n");
		output.write(ps._titleText + "\n***\n"); // ** as it could be multiLine
		output.write(ps._creditsText + "\n***\n");
		output.write(ps._title_credits + "\n");
		output.write(ps._fontOption + "\n");
		output.write(ps._colourOption + "\n");
		output.write(ps._x + "\n");
		output.write(ps._y + "\n");
		output.write(ps._fontSize + "\n");
		output.write(ps._duration + "\n");
	}
	
	public void readFile(String file) {
		
        try {
        	File projectFile = new File(file); // Need to change this
			BufferedReader input = new BufferedReader(new FileReader(projectFile));
			readTheFile(input);
			input.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	private void readTheFile(BufferedReader input) throws IOException {
		// TODO
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
		
		ProjectSettings settings = new ProjectSettings( mediaName,  audioName,  titleText, 
				 creditsText,  Integer.parseInt(titleOrCredits),  Integer.parseInt(font),  Integer.parseInt(colour), 
				 Integer.parseInt(x), Integer.parseInt(y), Integer.parseInt(fontSize),  null);
		editorP.loadSettings(settings);
	}
	
	// TODO 
	/**
	 * This class is used to house all the project settings
	 * @author MonkeyMan707
	 *
	 */
	public class ProjectSettings {
		public String _mediaFile;
		public String _audioFile;
		public String _titleText;
		public String _creditsText;
		public int _title_credits;
		public int _fontOption;
		public int _colourOption;
		public int _x;
		public int _y;
		public Integer _fontSize;
		public String _duration;
		
		public ProjectSettings(String mediaName, String audioName, String titleText, 
				String creditsText, int titleOrCredits, int fontOption, int colourOption, 
				int x, int y, Integer fontSize, String duration) {
			_mediaFile = mediaName;
			_audioFile = audioName;
			_titleText = titleText;
			_creditsText = creditsText;
			 _title_credits = titleOrCredits;
			 _fontOption = fontOption;
			 _colourOption  = colourOption;
			 _x = x;
			 _y = y;
			 _fontSize = fontSize;
			 _duration = duration;
		}
		
		
		
	}
}
