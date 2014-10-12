package gui;

import editing.CheckFileExists;
import editing.GetAttributes;
import editing.ProjectFile;
import editing.VideoWorker;
import editing.ProjectFile.ProjectSettings;
import java.awt.event.MouseAdapter;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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
import state.LanguageSelector;
import state.State;
import net.miginfocom.swing.MigLayout;

/**
 * This panel contains all the controls and functionality for text manipulation
 * @author Wesley
 *
 */
@SuppressWarnings("serial")
public class TextSection extends JPanel implements MouseListener {
	
	private JTextArea textArea = new JTextArea(getString("defaultText"),5,15);
	private JButton addTextBtn = new JButton(getString("addTextToVideo"));
	private JButton previewBtn = new JButton(getString("preview"));
	private JButton colourBtn = new JButton();
	private JButton textPosBtn = new JButton(getString("setPosition"));
	private JComboBox<String> titleOrCredits;
	private JComboBox<String> fontOption;
	private ColourChooser cc = new ColourChooser(this);
	private CustomSpinner fontSizeSpinner;
	private CustomSpinner timeForTextSpinner;
	private final JScrollPane textScroll = new JScrollPane(textArea);
	private String titleText = getString("defaultText");
	private String creditsText = "";
	private CustomSpinner xSpinner;
	private CustomSpinner ySpinner;
	private DateEditor de;
	private MainControlPanel cp;
	private EditorPanel editorPanel;
	private boolean isSelecting = false;
	private static LoadingScreen loadScreen;
	private VideoWorker worker;

	/**
	 * The constructor sets up the GUI and adds listeners
	 * @param ep The EditorPanel that this is housed in. This is used to get the name
	 * 		of the currently playing media.
	 */
	public TextSection(final EditorPanel ep, final MainControlPanel cp){
		
		editorPanel = ep;
		this.cp = cp;
		titleOrCredits = new JComboBox<String>(new String[]{getString("title"), getString("credits"), getString("thisPoint")});
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
				new Color(150, 150, 250, 250), new Color(50, 50, 150, 250)), getString("text"));
		border.setTitleFont(new Font("Sans Serif", Font.BOLD, 24));
		border.setTitleColor(new Color(150, 150, 250, 250));
		setBorder(border);

		
		textArea.setBorder(BorderFactory.createEtchedBorder());
		textArea.setLineWrap(true);
		textArea.setFont( textArea.getFont().deriveFont(Float.parseFloat(fontSizeSpinner.getValue().toString())) );

		colourBtn.setBackground(Color.RED);
		
		State.getState().addColourListeners(textArea, textScroll, titleOrCredits, fontOption, fontSizeSpinner,
		xSpinner, ySpinner,	timeForTextSpinner,	previewBtn,	addTextBtn, textPosBtn);
		
		add(textArea, "cell 0 0 2 1, grow");
		add(titleOrCredits, "cell 0 1 2 1, grow");
		TransparentLabel fontLbl, colourLbl, sizeLbl, xLbl, yLbl, durLbl;
		add(fontLbl = new TransparentLabel(getString("font")), "cell 0 2");
		add(fontOption, "cell 1 2, grow");
		add(colourLbl = new TransparentLabel(getString("colour")), "cell 0 3, split 2");
		add(colourBtn, "cell 0 3, grow");
		add(textPosBtn, "cell 1 3, grow");
		add(sizeLbl = new TransparentLabel(getString("size")), "cell 0 4");
		add(fontSizeSpinner, "cell 1 4, grow");
		add(xLbl = new TransparentLabel("X: "), "cell 0 5");
		add(xSpinner, "cell 1 5, grow");
		add(yLbl = new TransparentLabel("Y: "), "cell 0 6");
		add(ySpinner, "cell 1 6, grow");
		add(durLbl = new TransparentLabel(getString("duration")), "cell 0 7");
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
					JOptionPane.showMessageDialog(null, getString("fontUnavailable"));
				}
			}
		});
		
		fontSizeSpinner.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent arg0) {
				textArea.setFont( textArea.getFont().deriveFont(Float.parseFloat(fontSizeSpinner.getValue().toString())) );
			}
		});
		
		textPosBtn.addActionListener(new ActionListener(){
			@Override
        	public void actionPerformed(ActionEvent arg0) {
				isSelecting = true;
				editorPanel.setCursorOnOverlay(true);
        	}
		});
		
		//prompt the user to enter an output filename. Then add the text and save it.
		addTextBtn.addActionListener(new ActionListener(){
			@Override
        	public void actionPerformed(ActionEvent arg0) {
				final JFileChooser fc = new JFileChooser();
		        fc.showSaveDialog(fc);
		        if (fc.getSelectedFile() != null){
		        	if (CheckFileExists.check(fc.getSelectedFile().getAbsolutePath().toString())){
						if (JOptionPane.showConfirmDialog((Component) null, "File already exists. Do you wish to overwrite?",
						        "alert", JOptionPane.OK_CANCEL_OPTION) != 0){
							JOptionPane.showMessageDialog(null, getString("notOverwritten"));
							return;
						}
		        	}
		            String outputFile = fc.getSelectedFile().getAbsolutePath().toString();
					addTextToVideo("conv", outputFile);
		        }
        	}
        });
		
		//preview the text using a stream
		previewBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				cp.stopPlaying();
				addTextToVideo("preview", "udp://localhost:1234");
				cp.playPreview();
			}
		});
		
		//display either the title text or credits text in the text area
        titleOrCredits.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (titleOrCredits.getSelectedItem().toString().equalsIgnoreCase(getString("title")))
	            	textArea.setText(titleText);
				else if (titleOrCredits.getSelectedItem().toString().equalsIgnoreCase(getString("credits")))
	            	textArea.setText(creditsText);
			}
        });
        
        textArea.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void changedUpdate(DocumentEvent arg0) {}

			@Override
			public void insertUpdate(DocumentEvent arg0) {
				if (titleOrCredits.getSelectedItem().toString().equalsIgnoreCase(getString("title"))) {
	            	titleText = textArea.getText();
				} else if (titleOrCredits.getSelectedItem().toString().equalsIgnoreCase(getString("credits"))) {
					creditsText = textArea.getText();
				}
			}
			@Override
			public void removeUpdate(DocumentEvent arg0) {
				if (titleOrCredits.getSelectedItem().toString().equalsIgnoreCase(getString("title"))) {
	            	titleText = textArea.getText();
				} else if (titleOrCredits.getSelectedItem().toString().equalsIgnoreCase(getString("credits"))) {
					creditsText = textArea.getText();
				}
			}
        });
	}
	
	public void changeColour(Color colour){
		colourBtn.setBackground(colour);
		textArea.setForeground(colour);
	}
	
	public void cancelPreview(){
		try{
			worker.cancel();
		}
		catch (NullPointerException ex){}
	}
	
	/**
	 * This method is called when either the add text or preview text button is clicked
	 * This will use either avconv or avplay to add the text to the video.
	 * @oparam option - either convert or preview
	 * @param output - the user specified output file name
	 */
	public void addTextToVideo(String option, String output){
		if (textArea.getText().split("\\s").length > 20){
			JOptionPane.showMessageDialog(null, getString("tooMuchText"), "Error", JOptionPane.DEFAULT_OPTION);
			return;
		}
		else if (Integer.parseInt(fontSizeSpinner.getValue().toString()) > 72){
			JOptionPane.showMessageDialog(null, "tooBigText", "Error", JOptionPane.DEFAULT_OPTION);
			return;
		}
		//get the duration and attributes for use in the progress bar
		int dur = GetAttributes.getDuration(editorPanel.getMediaName());
    	int frames = GetAttributes.getFrames(editorPanel.getMediaName());
        String fontPath = getFontPath(fontOption.getSelectedItem().toString());
        //get the time from the spinner, to work out the length of the title/credits
        String time = new DateEditor(timeForTextSpinner , "yy:mm:ss").getFormat().format(timeForTextSpinner.getValue());
        String[] timeArray = time.split(":");
        int timeInSecs = 60 * 60 *Integer.parseInt(timeArray[0]) + 60 * Integer.parseInt(timeArray[1]) + Integer.parseInt(timeArray[2]);
        String timeFunction;
        if (titleOrCredits.getSelectedItem().toString().equalsIgnoreCase(getString("title")))
        	timeFunction = "lt(t," + timeInSecs + ")";
        else if (titleOrCredits.getSelectedItem().toString().equalsIgnoreCase(getString("credits")))
        	timeFunction = "gt(t," + (dur - timeInSecs) + ")";
        else
        	timeFunction = "gt(t," + cp.getTime()/1000 + ")*" + "lt(t," + (cp.getTime()/1000 + timeInSecs) + ")";
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
	        cmd = "avconv -y -i " + editorPanel.getMediaName() + " -vf \"drawtext=fontfile='" + fontPath + "':textfile='" + currentAbsPath + "/.text" +
	        			"':x=" + xSpinner.getValue() + ":y=" + ySpinner.getValue() + ":fontsize=" + fontSizeSpinner.getValue() + ":fontcolor=" + colour + 
	        			":draw='" + timeFunction + "'\" -strict experimental -f mp4 -v debug " + output;
    	}else if (option.equals("preview")){
    		 cmd = "avconv -re -i " + editorPanel.getMediaName() + " -vf \"drawtext=fontfile='" + fontPath + "':textfile='" + currentAbsPath + "/.text" +
	        			"':x=" + xSpinner.getValue() + ":y=" + ySpinner.getValue() + ":fontsize=" + fontSizeSpinner.getValue() + ":fontcolor=" + colour + 
	        			":draw='" + timeFunction + "'\" -strict experimental -f mpegts -v debug " + output;
    		 cp.setDuration(dur*1000);
    		 cp.setIsPreviewing(true);
    	}
        //only carry out the command if the video file is valid
        if (dur > 0 && frames > 0){
    		loadScreen = new LoadingScreen(editorPanel);
	        worker = new VideoWorker(cmd, loadScreen.getProgBar(), frames, option, "Text", loadScreen);
	        if (option.equals("conv")){
				loadScreen.prepare();
				loadScreen.setWorker(worker);
	        }
	        worker.execute();
		}
        else{
			JOptionPane.showMessageDialog(null, getString("unsupportedFile"), getString("error"), JOptionPane.DEFAULT_OPTION);
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
			JOptionPane.showMessageDialog(null, getString("errorOccurred"));
		} catch (InterruptedException e) {
			JOptionPane.showMessageDialog(null, getString("errorOccurred"));

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
		SimpleDateFormat format = new SimpleDateFormat("yy:mm:ss");
		try {
			Date d = (java.util.Date)format.parse(duration);
			java.sql.Time time = new java.sql.Time(d.getTime());
		    timeForTextSpinner.setValue(time);
		} catch (ParseException e) {
			JOptionPane.showMessageDialog(null, getString("invalidSettings"));
		}

	}
	
	private String getString(String label){
		return LanguageSelector.getString(label);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (isSelecting){
			int x = GetAttributes.getWidth(editorPanel.getMediaName());
			int y = GetAttributes.getHeight(editorPanel.getMediaName());
			xSpinner.setValue(e.getPoint().x*x/EditorPanel.WIDTH);
			ySpinner.setValue(e.getPoint().y*y/EditorPanel.HEIGHT);
			isSelecting = false;
			editorPanel.setCursorOnOverlay(false);
		}
	}

	@Override
	public void mousePressed(MouseEvent e){ 	}

	@Override
	public void mouseReleased(MouseEvent e) {	}

	@Override
	public void mouseEntered(MouseEvent e) {	}

	@Override
	public void mouseExited(MouseEvent e) {	}
}
