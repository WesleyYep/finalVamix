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
	
	public void writeFile(ProjectSettings projSettings) {
		try {
	          File projectFile = new File("example.txt"); // Need to change this
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
		
		String mediaName = input.readLine();
		
		
		ProjectSettings settings = new ProjectSettings( mediaName,  null,  null, 
				 null,  1,  0,  3, 
				 null,  null);
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
		public String _fontSize;
		public String _duration;
		
		public ProjectSettings(String mediaName, String audioName, String titleText, 
				String creditsText, int titleOrCredits, int fontOption, int colourOption, 
				String fontSize, String duration) {
			_mediaFile = mediaName;
			_audioFile = audioName;
			_titleText = titleText;
			_creditsText = creditsText;
			 _title_credits = titleOrCredits;
			 _fontOption = fontOption;
			 _colourOption  = colourOption;
			 _fontSize = fontSize;
			 _duration = duration;
		}
		
		
		
	}
}
