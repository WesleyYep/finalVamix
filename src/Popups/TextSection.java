package Popups;

import editing.GetAttributes;
import editing.TextWorker;
import gui.EditorPanel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.JSpinner.DateEditor;

import net.miginfocom.swing.MigLayout;

/**
 * This panel contains all the controls and functionality for audio manipulation
 * @author Mathew and Wesley
 *
 */
@SuppressWarnings("serial")
public class TextSection extends JPanel{
	
	private JTextField textArea = new JTextField();
	private JButton addTextBtn = new JButton("Add text to Video");
	private JComboBox<String> titleOrCredits;
	private JComboBox<String> fontOption;
	private JComboBox<String> colourOption;
	private JSpinner fontSizeSpinner = new JSpinner();
	private JSpinner timeForTextSpinner = new JSpinner();
	private String titleText;
	private String creditsText;
	
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
		fontOption = new JComboBox<String>(new String[]{"DejaVuSans", "Arial", "Comic Sans", "Times New Roman"});
        colourOption = new JComboBox<String>(new String[]{"Red", "Orange", "Yellow", "Green", "Blue"});
        fontSizeSpinner.setEditor(new JSpinner.NumberEditor(fontSizeSpinner , "00"));
        fontSizeSpinner.setModel(new SpinnerNumberModel(0, 0, 64, 1));
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, 100); //to allow it to go up to 99, rather than stop at 24
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
        SpinnerDateModel timeModel = new SpinnerDateModel();
		timeModel.setValue(calendar.getTime());
		timeForTextSpinner.setModel(timeModel);
		timeForTextSpinner.setEditor(new DateEditor(timeForTextSpinner , "yy:mm:ss"));
		
		
		setLayout(new MigLayout());
		add(textArea, "grow, split 2");
		add(titleOrCredits, "wrap");
		add(new JLabel("Font: "), "split 4");
		add(fontOption);
		add(new JLabel("Colour: "));
		add(colourOption, "wrap");
		add(new JLabel("Size: "), "split 4");
		add(fontSizeSpinner);
		add(new JLabel("Duration: "));
		add(timeForTextSpinner, "wrap");
		add(addTextBtn, "grow, wrap");	
		
		addTextBtn.addActionListener(new ActionListener(){
        	@Override
        	public void actionPerformed(ActionEvent arg0) {
				final JFileChooser fc = new JFileChooser();
		        fc.showSaveDialog(fc);
		        if (fc.getSelectedFile() != null){
		        	int dur = GetAttributes.getDuration(editorPanel.getMediaName());
		        	int fps = GetAttributes.getFPS(editorPanel.getMediaName());
		            String outputFile = fc.getSelectedFile().getAbsolutePath().toString();
		            String fontPath = getFontPath(fontOption.getSelectedItem().toString());
		            String time = new DateEditor(timeForTextSpinner , "yy:mm:ss").getFormat().format(timeForTextSpinner.getValue());
		            String[] timeArray = time.split(":");
		            int timeInSecs = 60 * 60 *Integer.parseInt(timeArray[0]) + 60 * Integer.parseInt(timeArray[1]) + Integer.parseInt(timeArray[2]);
		            String timeFunction, metadata;
		            if (titleOrCredits.getSelectedItem().toString().equalsIgnoreCase("Title")){
		            	timeFunction = "lt(t," + timeInSecs + ")";
		            	metadata = "title=\"" + textArea.getText() + "\"";
		            }
		            else{
		            	timeFunction = "gt(t," + (dur - timeInSecs) + ")";
		            	metadata = "comment=\"" + textArea.getText() + "\"";
		            }
		            String cmd = "avconv -i " + editorPanel.getMediaName() + " -metadata " + metadata + " -vf \"drawtext=fontfile='" + fontPath + "':text='" + textArea.getText() +
		            			"':x=0:y=0:fontsize=" + fontSizeSpinner.getValue() + ":fontcolor=" + colourOption.getSelectedItem() + 
		            			":draw='" + timeFunction + "'\" -strict experimental -f mp4 -v debug " + outputFile;
        			loadScreen.prepare();
		            TextWorker worker = new TextWorker(cmd, loadScreen.getProgBar(), dur, fps);
		            worker.execute();
		        }
        	}
        });

        titleOrCredits.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (titleOrCredits.getSelectedItem().toString().equalsIgnoreCase("Title"))
	            	textArea.setText(titleText);
				else if (titleOrCredits.getSelectedItem().toString().equalsIgnoreCase("Credits"))
	            	textArea.setText(creditsText);
			}
        });
		
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
