package Popups;

import editing.GetAttributes;
import editing.TextWorker;
import gui.EditorPanel;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.SpinnerDateModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.JSpinner.DateEditor;
import javax.swing.border.EtchedBorder;

import net.miginfocom.swing.MigLayout;

/**
 * This panel contains all the controls and functionality for audio manipulation
 * @author Mathew and Wesley
 *
 */
@SuppressWarnings("serial")
public class TextSection extends JPanel{
	
	private JTextArea textArea = new JTextArea(" -- Enter Text Here -- ", 10, 50);
	private JButton addTextBtn = new JButton("Add text to Video");
	private JButton previewBtn = new JButton("Preview");
	private JComboBox<String> titleOrCredits;
	private JComboBox<String> fontOption;
	private JComboBox<String> colourOption;
	private JSpinner fontSizeSpinner = new JSpinner();
	private JSpinner timeForTextSpinner = new JSpinner();
	
	private JTextArea newTextArea = new JTextArea("Preview area", 10, 50);
	
	private JSpinner xSpinner = new JSpinner();
	private JSpinner ySpinner = new JSpinner();
	
//	private String titleText;
//	private String creditsText;
	
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
		fontOption = new JComboBox<String>(new String[]{"DejaVuSans", "Arial", "Comic Sans", "Times New Roman"});
        colourOption = new JComboBox<String>(new String[]{"Red", "Orange", "Yellow", "Green", "Blue"});
        fontSizeSpinner.setEditor(new JSpinner.NumberEditor(fontSizeSpinner , "00"));
        fontSizeSpinner.setModel(new SpinnerNumberModel(0, 0, 64, 1));
        fontSizeSpinner.setValue(18);
        
        xSpinner.setEditor(new JSpinner.NumberEditor(xSpinner , "00"));
        xSpinner.setModel(new SpinnerNumberModel(0, 0, 380, 1));
        xSpinner.setValue(18);
        ySpinner.setEditor(new JSpinner.NumberEditor(ySpinner , "00"));
        ySpinner.setModel(new SpinnerNumberModel(0, 0, 380, 1));
        ySpinner.setValue(18);
        
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
		newTextArea.setBorder(BorderFactory.createEtchedBorder());
		
		add(textArea, "cell 0 0 2 1, grow");
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
		add(newTextArea, "cell 0 9 2 1, grow");
		
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
		
		previewBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				addTextToVideo("avplay", "");
			}
		});
		
		

//        titleOrCredits.addActionListener(new ActionListener(){
//			@Override
//			public void actionPerformed(ActionEvent arg0) {
//				if (titleOrCredits.getSelectedItem().toString().equalsIgnoreCase("Title"))
//	            	textArea.setText(titleText);
//				else if (titleOrCredits.getSelectedItem().toString().equalsIgnoreCase("Credits"))
//	            	textArea.setText(creditsText);
//			}
//        });
		
	}
	
	public void addTextToVideo(String option, String outputFile){
		int dur = GetAttributes.getDuration(editorPanel.getMediaName());
    	int fps = GetAttributes.getFPS(editorPanel.getMediaName());
        String fontPath = getFontPath(fontOption.getSelectedItem().toString());
        String time = new DateEditor(timeForTextSpinner , "yy:mm:ss").getFormat().format(timeForTextSpinner.getValue());
        String[] timeArray = time.split(":");
        int timeInSecs = 60 * 60 *Integer.parseInt(timeArray[0]) + 60 * Integer.parseInt(timeArray[1]) + Integer.parseInt(timeArray[2]);
        String timeFunction;
        if (titleOrCredits.getSelectedItem().toString().equalsIgnoreCase("Title"))
        	timeFunction = "lt(t," + timeInSecs + ")";
        else
        	timeFunction = "gt(t," + (dur - timeInSecs) + ")";
        String cmd = option + " -i " + editorPanel.getMediaName() + " -vf \"drawtext=fontfile='" + fontPath + "':text='" + textArea.getText() +
        			"':x=" + xSpinner.getValue() + ":y=" + ySpinner.getValue() + ":fontsize=" + fontSizeSpinner.getValue() + ":fontcolor=" + colourOption.getSelectedItem() + 
        			":draw='" + timeFunction + "'\" -strict experimental -f mp4 -v debug " + outputFile;
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
		//use the locate command in linux
		String cmd = "locate " + fontName + ".ttf";
		ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd);
		try {
			Process process = builder.start();
			process.waitFor();
			InputStream stdout = process.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(stdout));
			return (br.readLine());		
		} catch (IOException e) {
			//
		} catch (InterruptedException e) {
			//
		}
		return "";
	}	
	

}
