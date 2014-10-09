package gui;

import editing.GetAttributes;
import editing.ProjectFile;
import editing.TextWorker;
import editing.ProjectFile.ProjectSettings;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.SpinnerDateModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.JSpinner.DateEditor;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import components.CustomSpinner;
import components.TransparentLabel;
import popups.ColourChooser;
import popups.LoadingScreen;
import state.State;
import net.miginfocom.swing.MigLayout;

/**
 * This panel contains all the controls and functionality for text manipulation
 * @author Mathew and Wesley
 *
 */
@SuppressWarnings("serial")
public class TextSection extends JPanel{
	
	private JTextArea textArea = new JTextArea(" -- Enter Text Here -- ",5,15);
	private JButton addTextBtn = new JButton("Add text to Video");
	private JButton previewBtn = new JButton("Preview");
	private JButton colourBtn = new JButton();
	private JComboBox<String> titleOrCredits;
	private JComboBox<String> fontOption;
	private ColourChooser cc = new ColourChooser(this);
	private CustomSpinner fontSizeSpinner;
	private CustomSpinner timeForTextSpinner;
	private final JScrollPane textScroll = new JScrollPane(textArea);
	private String titleText = " -- Enter Text Here -- ";
	private String creditsText = "";
	private CustomSpinner xSpinner;
	private CustomSpinner ySpinner;
	private DateEditor de;
	private MainControlPanel cp;
	private EditorPanel editorPanel;
	private static LoadingScreen loadScreen = new LoadingScreen();

	/**
	 * The constructor sets up the GUI and adds listeners
	 * @param ep The EditorPanel that this is housed in. This is used to get the name
	 * 		of the currently playing media.
	 */
	public TextSection(EditorPanel ep, final MainControlPanel cp) {
		
		editorPanel = ep;
		this.cp = cp;
		titleOrCredits = new JComboBox<String>(new String[]{"Title", "Credits"});
		fontOption = new JComboBox<String>(new String[]{"DejaVuSans", "DroidSans", "FreeSans", "LiberationSerif-Bold", "NanumGothic", "Padauk", 
														"TakaoPGothic", "TibetanMachineUni", "Ubuntu-C"});
       
		fontSizeSpinner = new CustomSpinner(18, new SpinnerNumberModel(0, 0, 72, 1));
		xSpinner = new CustomSpinner(10, new SpinnerNumberModel(0,0,380,1));
		ySpinner = new CustomSpinner(10, new SpinnerNumberModel(0,0,380,1));
		timeForTextSpinner = new CustomSpinner(20);
		State.getState().addSpinnerListeners(fontSizeSpinner, xSpinner, ySpinner, timeForTextSpinner);
        
		setLayout(new MigLayout());
		TitledBorder border = BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(EtchedBorder.LOWERED, 
				new Color(150, 150, 250, 250), new Color(50, 50, 150, 250)), "Text");
		border.setTitleColor(new Color(100,0,0));
		setBorder(border);
        State.getState().addBorderListeners(border);

		
		textArea.setBorder(BorderFactory.createEtchedBorder());
		textArea.setLineWrap(true);
		textArea.setFont( textArea.getFont().deriveFont(Float.parseFloat(fontSizeSpinner.getValue().toString())) );

		colourBtn.setBackground(Color.black);
		
		State.getState().addColourListeners(textArea, textScroll, titleOrCredits, fontOption, colourBtn, fontSizeSpinner,
		xSpinner, ySpinner,	timeForTextSpinner,	previewBtn,	addTextBtn);
		
		add(textArea, "cell 0 0 2 1, grow");
		add(titleOrCredits, "cell 0 1 2 1, grow");
		TransparentLabel fontLbl, colourLbl, sizeLbl, xLbl, yLbl, durLbl;
		add(fontLbl = new TransparentLabel("Font: "), "cell 0 2");
		add(fontOption, "cell 1 2, grow");
		add(colourLbl = new TransparentLabel("Colour: "), "cell 0 3");
		add(colourBtn, "cell 1 3, grow");
		add(sizeLbl = new TransparentLabel("Size: "), "cell 0 4");
		add(fontSizeSpinner, "cell 1 4, grow");
		add(xLbl = new TransparentLabel("X: "), "cell 0 5");
		add(xSpinner, "cell 1 5, grow");
		add(yLbl = new TransparentLabel("Y: "), "cell 0 6");
		add(ySpinner, "cell 1 6, grow");
		add(durLbl = new TransparentLabel("Duration: "), "cell 0 7");
		add(timeForTextSpinner, "cell 1 7, grow");
		add(previewBtn, "cell 0 8, grow");
		add(addTextBtn, "cell 1 8, grow");
		
		State.getState().addColourListeners(fontLbl, colourLbl, sizeLbl, xLbl, yLbl, durLbl);
		
		//bring up the JColorChooser
		colourBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				cc.setVisible(true);
			}
		});
		
		fontOption.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Font font;
				try {
			        String fontPath = getFontPath(fontOption.getSelectedItem().toString());
					font = Font.createFont(Font.TRUETYPE_FONT, new File(fontPath)).deriveFont(Float.parseFloat(fontSizeSpinner.getValue().toString()));
					textArea.setFont(font);
				} catch (FontFormatException | IOException e) {
					JOptionPane.showMessageDialog(null, "Font is unavailable.");
				}
			}
		});
		
		fontSizeSpinner.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent arg0) {
				textArea.setFont( textArea.getFont().deriveFont(Float.parseFloat(fontSizeSpinner.getValue().toString())) );
			}
		});
		
		//prompt the user to enter an output filename. Then add the text and save it.
		addTextBtn.addActionListener(new ActionListener(){
			@Override
        	public void actionPerformed(ActionEvent arg0) {
				final JFileChooser fc = new JFileChooser();
		        fc.showSaveDialog(fc);
		        if (fc.getSelectedFile() != null){
		            String outputFile = fc.getSelectedFile().getAbsolutePath().toString();
					addTextToVideo("conv", outputFile);
		        }
        	}
        });
		
		//preview the text using a stream
		previewBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				addTextToVideo("preview", "udp://localhost:1234");
				cp.playPreview();
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
	
	public void changeColour(Color colour){
		colourBtn.setBackground(colour);
		textArea.setForeground(colour);
	}
	
	/**
	 * This method is called when either the add text or preview text button is clicked
	 * This will use either avconv or avplay to add the text to the video.
	 * @oparam option - name of the command: either avconv or avplay
	 * @param output - the user specified output file name
	 */
	public void addTextToVideo(String option, String output){
		loadScreen = new LoadingScreen();
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
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(".text", false), "utf-8"));
			writer.write(textArea.getText());
			writer.close();
		} catch (UnsupportedEncodingException e) {
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}
    	Path currentRelativePath = Paths.get("");
    	String currentAbsPath = currentRelativePath.toAbsolutePath().toString();
    	String colour = toHexString(colourBtn.getBackground());
        //write the command
    	String cmd = "";
    	if (option.equals("conv")){
	        cmd = "avconv -i " + editorPanel.getMediaName() + " -vf \"drawtext=fontfile='" + fontPath + "':textfile='" + currentAbsPath + "/.text" +
	        			"':x=" + xSpinner.getValue() + ":y=" + ySpinner.getValue() + ":fontsize=" + fontSizeSpinner.getValue() + ":fontcolor=" + colour + 
	        			":draw='" + timeFunction + "'\" -strict experimental -f mp4 -v debug " + output;
    	}else if (option.equals("preview")){
    		 cmd = "avconv -re -i " + editorPanel.getMediaName() + " -vf \"drawtext=fontfile='" + fontPath + "':textfile='" + currentAbsPath + "/.text" +
	        			"':x=" + xSpinner.getValue() + ":y=" + ySpinner.getValue() + ":fontsize=" + fontSizeSpinner.getValue() + ":fontcolor=" + colour + 
	        			":draw='" + timeFunction + "'\" -strict experimental -f mpegts " + output;
    		 cp.setDuration(dur*1000);
    		 cp.setIsPreviewing(true);
    	}
        //only carry out the command if the video file is valid
        if (dur > 0 && fps > 0){
	        if (option.equals("avconv"))
				loadScreen.prepare();
	        TextWorker worker = new TextWorker(cmd, loadScreen.getProgBar(), dur, fps, option);
	        worker.execute();
		}
	}
	/**Method comes from http://www.javacreed.com/how-to-get-the-hex-value-from-color/
	 * 
	 * @param colour
	 * @return the hex string of the colour
	 */
	public final static String toHexString(Color colour){
		  String hexColour = Integer.toHexString(colour.getRGB() & 0xffffff);
		  if (hexColour.length() < 6) {
		    hexColour = "000000".substring(0, 6 - hexColour.length()) + hexColour;
		  }
		  return "0x" + hexColour;
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
			JOptionPane.showMessageDialog(null, "Error occurred.");
		} catch (InterruptedException e) {
			JOptionPane.showMessageDialog(null, "Error occurred.");

		}
		return "";
	}	
	

	public ProjectSettings createProjectSettings() {

        String duration = new DateEditor(timeForTextSpinner , "yy:mm:ss").getFormat().format(timeForTextSpinner.getValue());
		
		return ProjectFile.getInstance(editorPanel).new ProjectSettings(null, null, titleText, 
				 creditsText,  titleOrCredits.getSelectedIndex(),  fontOption.getSelectedIndex(), toHexString(colourBtn.getBackground()), 
				 (int)xSpinner.getValue(), (int)ySpinner.getValue(), 
				 (Integer)fontSizeSpinner.getValue(),  duration);
	}
	public void loadProjectSettings(ProjectSettings ps) {
		titleText = ps._titleText;
		creditsText = ps._creditsText;
		titleOrCredits.setSelectedIndex(ps._title_credits);
		fontOption.setSelectedIndex(ps._fontOption);
		colourBtn.setBackground(Color.decode(ps._colour));
		xSpinner.setValue(ps._x);
		ySpinner.setValue(ps._y);
		fontSizeSpinner.setValue(ps._fontSize);
		String duration = ps._duration;
		String[] dur = duration.split(":");
		
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
		timeForTextSpinner.setEditor(de);
	}
	
	/**
	 * This method checks if the loading screen is cancelled
	 * @return true if cancelled, false otherwise
	 */
	public static boolean loadCancelled(){
		return loadScreen.isClosed;
	}
}
