package Popups;

import editing.GetAttributes;
import editing.ProjectFile;
import editing.TextWorker;
import editing.ProjectFile.ProjectSettings;
import gui.EditorPanel;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.SpinnerDateModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.JSpinner.DateEditor;
import javax.swing.border.EtchedBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import net.miginfocom.swing.MigLayout;

/**
 * This panel contains all the controls and functionality for text manipulation
 * @author Mathew and Wesley
 *
 */
@SuppressWarnings("serial")
public class TextSection extends JPanel{
	
	private JTextArea textArea = new JTextArea(" -- Enter Text Here -- ", 20, 50);
	private JButton addTextBtn = new JButton("Add text to Video");
	private JButton previewBtn = new JButton("Preview");
	private JComboBox<String> titleOrCredits;
	private JComboBox<String> fontOption;
	private JComboBox<String> colourOption;
	private JSpinner fontSizeSpinner = new JSpinner();
	private JSpinner timeForTextSpinner = new JSpinner();
	private final JScrollPane textScroll = new JScrollPane(textArea);
	private String titleText = " -- Enter Text Here -- ";
	private String creditsText = "";
	private JSpinner xSpinner = new JSpinner();
	private JSpinner ySpinner = new JSpinner();
	private EditorPanel editorPanel;
	private LoadingScreen loadScreen = new LoadingScreen();

	/**
	 * The constructor sets up the GUI and adds listeners
	 * @param ep The EditorPanel that this is housed in. This is used to get the name
	 * 		of the currently playing media.
	 */
	public TextSection(EditorPanel ep) {
		
		editorPanel = ep;
		
		titleOrCredits = new JComboBox<String>(new String[]{"Title", "Credits"});
//		titleOrCredits.setSelectedIndex(1);
		fontOption = new JComboBox<String>(new String[]{"DejaVuSans", "DroidSans", "FreeSans", "LiberationSerif-Bold", "NanumGothic", "Padauk", 
														"TakaoPGothic", "TibetanMachineUni", "Ubuntu-C"});
        colourOption = new JComboBox<String>(new String[]{"Black", "White", "Red", "Orange", "Yellow", "Green", "Blue", "Purple"});
        fontSizeSpinner.setEditor(new JSpinner.NumberEditor(fontSizeSpinner , "00"));
        fontSizeSpinner.setModel(new SpinnerNumberModel(0, 0, 72, 1));
        fontSizeSpinner.setValue(18);
        
        xSpinner.setEditor(new JSpinner.NumberEditor(xSpinner , "00"));
        xSpinner.setModel(new SpinnerNumberModel(0, 0, 380, 1));
        xSpinner.setValue(10);
        ySpinner.setEditor(new JSpinner.NumberEditor(ySpinner , "00"));
        ySpinner.setModel(new SpinnerNumberModel(0, 0, 380, 1));
        ySpinner.setValue(10);
        
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, 100); //to allow it to go up to 99, rather than stop at 24
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 20);
        SpinnerDateModel timeModel = new SpinnerDateModel();
		timeModel.setValue(calendar.getTime());
		timeForTextSpinner.setModel(timeModel);
		timeForTextSpinner.setEditor(new DateEditor(timeForTextSpinner , "yy:mm:ss"));
		
		
		setLayout(new MigLayout());
		setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(EtchedBorder.LOWERED, 
				new Color(150, 150, 250, 250), new Color(50, 50, 150, 250)), "Text"));
		
		textArea.setBorder(BorderFactory.createEtchedBorder());
		textArea.setLineWrap(true);
		
		add(textScroll, "cell 0 0 2 1, span");
		add(titleOrCredits, "cell 0 1 2 1, grow");
		add(new JLabel("Font: "), "cell 0 2");
		add(fontOption, "cell 1 2, grow");
		add(new JLabel("Colour: "), "cell 0 3");
		add(colourOption, "cell 1 3, grow");
		add(new JLabel("Size: "), "cell 0 4");
		add(fontSizeSpinner, "cell 1 4, grow");
		add(new JLabel("X: "), "cell 0 5");
		add(xSpinner, "cell 1 5, grow");
		add(new JLabel("Y: "), "cell 0 6");
		add(ySpinner, "cell 1 6, grow");
		add(new JLabel("Duration: "), "cell 0 7");
		add(timeForTextSpinner, "cell 1 7, grow");
		add(previewBtn, "cell 0 8, grow");
		add(addTextBtn, "cell 1 8, grow");
		
		//prompt the user to enter an output filename. Then add the text and save it.
		addTextBtn.addActionListener(new ActionListener(){
			@Override
        	public void actionPerformed(ActionEvent arg0) {
				final JFileChooser fc = new JFileChooser();
		        fc.showSaveDialog(fc);
		        if (fc.getSelectedFile() != null){
		            String outputFile = fc.getSelectedFile().getAbsolutePath().toString();
					addTextToVideo("avconv", outputFile);
		        }
        	}
        });
		
		//preview the text using avplay
		previewBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				addTextToVideo("avplay", "");
			}
		});
		
		
		//display either the title text or credits text in the text area
        titleOrCredits.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (titleOrCredits.getSelectedItem().toString().equalsIgnoreCase("Title"))
	            	textArea.setText(titleText);
				else if (titleOrCredits.getSelectedItem().toString().equalsIgnoreCase("Credits"))
	            	textArea.setText(creditsText);
			}
        });
        
        textArea.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void changedUpdate(DocumentEvent arg0) {}

			@Override
			public void insertUpdate(DocumentEvent arg0) {
				if (titleOrCredits.getSelectedItem().toString().equalsIgnoreCase("Title")) {
	            	titleText = textArea.getText();
				} else if (titleOrCredits.getSelectedItem().toString().equalsIgnoreCase("Credits")) {
					creditsText = textArea.getText();
				}
			}
			@Override
			public void removeUpdate(DocumentEvent arg0) {
				if (titleOrCredits.getSelectedItem().toString().equalsIgnoreCase("Title")) {
	            	titleText = textArea.getText();
				} else if (titleOrCredits.getSelectedItem().toString().equalsIgnoreCase("Credits")) {
					creditsText = textArea.getText();
				}
			}
        	
        	
        });
		
	}
	
	/**
	 * This method is called when either the add text or preview text button is clicked
	 * This will use either avconv or avplay to add the text to the video.
	 * @oparam option - name of the command: either avconv or avplay
	 * @param outputFile - the user specified output file name
	 */
	public void addTextToVideo(String option, String outputFile){
		if (textArea.getText().split("\\s").length > 20){
			JOptionPane.showMessageDialog(null, "Input text exceeds the 20 word limit.", "Error", JOptionPane.DEFAULT_OPTION);
			return;
		}
		else if (Integer.parseInt(fontSizeSpinner.getValue().toString()) > 72){
			JOptionPane.showMessageDialog(null, "Font size exceeds the 72pt limit", "Error", JOptionPane.DEFAULT_OPTION);
			return;
		}
		//get the duration and attributes for use in the progress bar
		int dur = GetAttributes.getDuration(editorPanel.getMediaName());
    	int fps = GetAttributes.getFPS(editorPanel.getMediaName());
        String fontPath = getFontPath(fontOption.getSelectedItem().toString());
        //get the time from the spinner, to work out the length of the title/credits
        String time = new DateEditor(timeForTextSpinner , "yy:mm:ss").getFormat().format(timeForTextSpinner.getValue());
        String[] timeArray = time.split(":");
        int timeInSecs = 60 * 60 *Integer.parseInt(timeArray[0]) + 60 * Integer.parseInt(timeArray[1]) + Integer.parseInt(timeArray[2]);
        String timeFunction;
        if (titleOrCredits.getSelectedItem().toString().equalsIgnoreCase("Title"))
        	timeFunction = "lt(t," + timeInSecs + ")";
        else
        	timeFunction = "gt(t," + (dur - timeInSecs) + ")";
        //write text firstly to file, so special characters can be used
        try {
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(".text"), "utf-8"));
			writer.write(textArea.getText());
			writer.close();
		} catch (UnsupportedEncodingException e) {
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}
        //write the command
        String cmd = option + " -i " + editorPanel.getMediaName() + " -vf \"drawtext=fontfile='" + fontPath + "':textfile='" + "/.text" +
        			"':x=" + xSpinner.getValue() + ":y=" + ySpinner.getValue() + ":fontsize=" + fontSizeSpinner.getValue() + ":fontcolor=" + colourOption.getSelectedItem() + 
        			":draw='" + timeFunction + "'\" -strict experimental -f mp4 -v debug " + outputFile;
		//only carry out the command if the video file is valid
        if (dur > 0 && fps > 0){
	        if (option.equals("avconv"))
				loadScreen.prepare();
	        TextWorker worker = new TextWorker(cmd, loadScreen.getProgBar(), dur, fps);
	        worker.execute();
		}
	}
	
	/**
	 * This method is used to locate the input font file within the users computer
	 * @param fontName The font to be located
	 * @return Returns a string which is the path to the font file
	 */
	private String getFontPath(String fontName) {
		//use the fc-list command in linux
		String cmd = "fc-list | grep " + fontName + ".ttf";
		ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd);
		try {
			Process process = builder.start();
			process.waitFor();
			InputStream stdout = process.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(stdout));
			String line = br.readLine();
			return line.split(":")[0];
		} catch (IOException e) {
			//
		} catch (InterruptedException e) {
			//
		}
		return "";
	}	
	

	public ProjectSettings createProjectSettings() {

        String duration = new DateEditor(timeForTextSpinner , "yy:mm:ss").getFormat().format(timeForTextSpinner.getValue());
		
		return ProjectFile.getInstance(editorPanel).new ProjectSettings(null, null, titleText, 
				 creditsText,  titleOrCredits.getSelectedIndex(),  fontOption.getSelectedIndex(),  colourOption.getSelectedIndex(), 
				 (int)xSpinner.getValue(), (int)ySpinner.getValue(), 
				 (Integer)fontSizeSpinner.getValue(),  duration);
	}
	public void loadProjectSettings(ProjectSettings ps) {
		titleText = ps._titleText;
		creditsText = ps._creditsText;
		titleOrCredits.setSelectedIndex(ps._title_credits);
		fontOption.setSelectedIndex(ps._fontOption);
		colourOption.setSelectedIndex(ps._colourOption);
		xSpinner.setValue(ps._x);
		ySpinner.setValue(ps._y);
		fontSizeSpinner.setValue(ps._fontSize);
		
		System.out.println(ps._duration);
		
		String duration = ps._duration;
		String[] dur = duration.split(":");
		
		System.out.println(dur[0]);
		System.out.println(dur[1]);
		System.out.println(dur[2]);

		
		Calendar d = Calendar.getInstance();
		if (Integer.parseInt(dur[0]) == 0)
			d.set(Calendar.YEAR, Integer.parseInt(dur[0] + 100));
		else
			d.set(Calendar.YEAR, Integer.parseInt(dur[0]));
		d.set(Calendar.MINUTE, Integer.parseInt(dur[1]));
		d.set(Calendar.SECOND, Integer.parseInt(dur[2]));
		SpinnerDateModel timeModel = new SpinnerDateModel();
		timeModel.setValue(d.getTime());
		timeForTextSpinner.setModel(timeModel);
		timeForTextSpinner.setEditor(new DateEditor(timeForTextSpinner , "yy:mm:ss"));
	}
}
