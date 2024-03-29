package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import com.sun.jna.platform.WindowUtils;
import com.sun.awt.AWTUtilities;
import components.CustomButton;
import components.SmallColourPanel;
import models.ProjectFile;
import models.ProjectFile.ProjectSettings;
import net.miginfocom.swing.MigLayout;
import state.LanguageSelector;
import state.State;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.MediaPlayer;

/**
 * This is the main frame of the Vamix program. It consists of all the other editing panels and sections.
 * It contains the main method for Vamix. 
 * The images used in the buttons have been taken from: http://www.softicons.com/
 * 
 * @author Wesley Yep
 */
@SuppressWarnings("restriction")
public class Vamix implements MouseMotionListener{

	public static final int WIDTH = 1280;
	public static final int HEIGHT = 780;
	private final EmbeddedMediaPlayerComponent mediaPlayerComponent;
	private MediaPlayer mediaPlayer;
	private JButton openBtn = new JButton(getString("open"));
	private JTextField fileTextField = new JTextField(20);
    private JButton helpBtn = new JButton(getString("help"));
    private JButton showHideBtn = new JButton(getString("hide"));
    private JButton languageBtn = new JButton();
	private CustomButton loadBtn = new CustomButton(getString("load"), new ImageIcon(
			Vamix.class.getResource("/upload.png")), 25, 25);
	private JButton saveBtn = new CustomButton(getString("save"), new ImageIcon(
			Vamix.class.getResource("/download.png")), 25, 25);
	private ProjectFile projFile = ProjectFile.getInstance(this);
	private AudioSection audioSection;
	private TextSection textSection;
	private MainControlPanel mainControlPanel;
	private Frame f;
	private State state;
	private Window overlay;
	private JPanel bottomPanel;
	private EffectsSection effectsSection;
	private DownloadPanel downloadPanel;
	private static String language = "en NZ";

    public static void main(final String[] args) throws Exception {
    	//check the language on initialisation
    	if (args.length!=0){
    		LanguageSelector.setLanguage(args[0], args[1]);
    		language = args[0] + " " + args[1];
    	}else{
    		//default to English
    		LanguageSelector.setLanguage("en", "NZ");
    	}
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Vamix();
            }
        });
    }

	Vamix () {
		//set up the frame
        f = new Frame("Vamix");
		f.setIconImage(new ImageIcon(getClass().getClassLoader().getResource("icon.png")).getImage());
        f.setSize(WIDTH, HEIGHT);
        f.setBackground(Color.black);
        f.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
		f.setLayout(new BorderLayout());
		
		//the media player probably needs to be the first thing that gets created
		mediaPlayerComponent = new EmbeddedMediaPlayerComponent();
      	mediaPlayer = mediaPlayerComponent.getMediaPlayer();
		mediaPlayerComponent.setFont(new Font("Arial", 24, Font.BOLD));

      	registerListeners();

        //set up some the control panel	
        mainControlPanel = new MainControlPanel(mediaPlayerComponent.getMediaPlayer(), this);
		mainControlPanel.enableStopButton();
		
		//position the load and save buttons
        loadBtn.setVerticalTextPosition(SwingConstants.CENTER);
        loadBtn.setHorizontalTextPosition(SwingConstants.RIGHT);
        saveBtn.setVerticalTextPosition(SwingConstants.CENTER);
        saveBtn.setHorizontalTextPosition(SwingConstants.RIGHT);
        
        //set the text of the language button based on the language that is currently chosen
        if(language.equals("en NZ")){
        	languageBtn.setText(getString("maori"));
        }else{
        	languageBtn.setText(getString("english"));
        }
		
        //set all the buttons on the bottom panel
        f.add(mediaPlayerComponent, BorderLayout.CENTER);
        bottomPanel = new JPanel();
        helpBtn.setBackground(Color.BLACK);
        helpBtn.setForeground(Color.LIGHT_GRAY);
        showHideBtn.setBackground(Color.BLACK);
        showHideBtn.setForeground(Color.LIGHT_GRAY);
        languageBtn.setBackground(Color.BLACK);
        languageBtn.setForeground(Color.LIGHT_GRAY);
        
        bottomPanel.add(helpBtn);
        bottomPanel.add(languageBtn);
        bottomPanel.add(showHideBtn);
        bottomPanel.add(new SmallColourPanel(this));
        bottomPanel.setBackground(Color.BLACK);
        f.add(bottomPanel, BorderLayout.SOUTH);
        
        f.setVisible(true);
        state = State.getState();
        
		//set up the overlay
        overlay = new Overlay(f,this);
		overlay.addMouseMotionListener(this);
      	mediaPlayerComponent.getMediaPlayer().setOverlay(overlay);
      	mediaPlayerComponent.getMediaPlayer().enableOverlay(true);
      	
		//finally check if any hidden settings files exist'
		File file = new File(".settings");
		if(file.exists() && !file.isDirectory()) {
    		//System.out.println("yolo");
			projFile.readFile(".settings");
    		file.delete();
		}
	}

	private void registerListeners() {
    	//When the open button is clicked the file chooser appears
        openBtn.addActionListener(new ActionListener(){
        	@Override
        	public void actionPerformed(ActionEvent arg0) {
				mainControlPanel.stopPlaying();
        		chooseFile();
        	}
        });
        
        //this will write to the project file
        saveBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String fileName;
				final JFileChooser fc = new JFileChooser();
		        fc.showSaveDialog(fc);
		        if (fc.getSelectedFile() != null){
		        	fileName = fc.getSelectedFile().getAbsolutePath().toString();
		        	projFile.writeFile(createProjectSettings(), fileName);
		        }
			}
        });
        
        //load all settings from the project file
        loadBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String fileName;
				final JFileChooser fc = new JFileChooser();
		        fc.showOpenDialog(fc);
		        if (fc.getSelectedFile() != null){
			        fileName = fc.getSelectedFile().getAbsolutePath().toString();
					projFile.readFile(fileName);
				    audioSection.enableButtons();
		        }
			}
        });
        
        //this will toggle the visibility of all the editing components
        showHideBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (showHideBtn.getText().equalsIgnoreCase(getString("hide"))){
					showHideBtn.setText(getString("show"));
					state.setVisibility(false);
				}else if (showHideBtn.getText().equalsIgnoreCase(getString("show"))){
					showHideBtn.setText(getString("hide"));
					state.setVisibility(true);
				}
			}
        });
        
        //this will restart the application in a different language, but the setting will be saved
        languageBtn.addActionListener(new ActionListener(){
        	@Override
			public void actionPerformed(ActionEvent e) {
				if (languageBtn.getText().equalsIgnoreCase(getString("english"))){
					restartApplication("en", "NZ");
				}else if (languageBtn.getText().equalsIgnoreCase(getString("maori"))){
					restartApplication("en", "MA");
				}
			}
        });
        
        //bring up the user manual if the help button is clicked
        helpBtn.addActionListener(new ActionListener(){
        	@Override
			public void actionPerformed(ActionEvent e) {
        		//use the file command in linux
        		ProcessBuilder processBuilder = new ProcessBuilder("xdg-open", "userManual.pdf");
        		try {
        			Process process = processBuilder.start();
        			process.waitFor();
        			if (process.exitValue() != 0){
            			JOptionPane.showMessageDialog(mediaPlayerComponent, getString("helpNotFound"));
        			}
        		} catch (IOException | InterruptedException ex) {
        			JOptionPane.showMessageDialog(mediaPlayerComponent, getString("helpNotFound"));
        		}
			}
        });
        
	}
	
	/**The code for this method comes mostly from http://stackoverflow.com/questions/4159802/how-can-i-restart-a-java-application
	 * 
	 * @param code1 language code 1
	 * @param code2 language code 2
	 */
	public void restartApplication(String code1, String code2)
	{
		final String javaBin = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
		File currentJar;
		try {
			currentJar = new File(Vamix.class.getProtectionDomain().getCodeSource().getLocation().toURI());
			if(!currentJar.getName().endsWith(".jar")){
				JOptionPane.showMessageDialog(mediaPlayerComponent, "Language features only work when program is run from jar file");
				return;
			}
			//firstly save project settings to a hidden file
			projFile.writeFile(createProjectSettings(), ".settings");
			/* Build command: java -jar Vamix.jar en NZ*/
			final ArrayList<String> command = new ArrayList<String>();
			command.add(javaBin);
			command.add("-jar");
			command.add(currentJar.getPath());
			command.add(code1);
			command.add(code2);
			System.out.println(command);
		
			final ProcessBuilder builder = new ProcessBuilder(command);
			builder.start();
			System.exit(0);
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (IOException e) {e.printStackTrace();}
	}

	//show the mouse controls in case the overlay window is hidden, if the mouse is moved or dragged
	@Override
	public void mouseDragged(MouseEvent e) {
		state.showMouseControls();
	}
	@Override
	public void mouseMoved(MouseEvent e) {
		state.showMouseControls();
	}

	/**
	 * Make it fullscreen just by maximising the size of the frame
	 */
	public void setFullScreen(){
	    f.setExtendedState(JFrame.MAXIMIZED_BOTH);
	}
	
	/**
	 * Exit fullscreen just by making the size of the frame back to the initial size
	 */
	public void exitFullScreen() {
		f.setExtendedState(JFrame.NORMAL);
        f.setSize(WIDTH, HEIGHT);
	}
	
	/** 
	 * Used to choose a media file to play
	 * Used from both open and play button
	 */
	private void chooseFile() {
		final JFileChooser fc = new JFileChooser();
        fc.showOpenDialog(fc);
        if (fc.getSelectedFile() != null){
        	if (isMediaFile(fc.getSelectedFile().getAbsolutePath().toString())){
        		fileTextField.setText(fc.getSelectedFile().getAbsolutePath().toString());
        		effectsSection.setTimeDefault();
        	}
        	else
        		JOptionPane.showMessageDialog(mediaPlayerComponent, 
        				getString("validMediaFile"));
        }
	}

	/** This method is to be called by the mainframe when it is exiting so that 
	 * the video is stopped (otherwise you will continue to hear the audio playing 
	 */
	public void stop() {
		mediaPlayerComponent.getMediaPlayer().stop();
	}
	
	/** This method checks if a input file is a media file that can be played
	 * @param input - the input file absolute file location
	 */
	public boolean isMediaFile(String input){
		return isFileType(input, "Media")
	        	|| isFileType(input, "media")
	        	|| isFileType(input, "Audio")
	        	|| isFileType(input, "video");
	}
	
	/**
	 * This method is used to check if the current loaded media file is an audio file
	 * @return true if it is an audio file, false otherwise
	 */
	public boolean isAudioFile(){
		return isFileType(getMediaName(), "Audio");
	}
	
	/**
	 * This method is used to check that the file is a valid file type
	 * @return true if the user input matches expected file type, false if not
	 */
	public boolean isFileType(String file, String expected) {
		//use the file command in linux
		ProcessBuilder processBuilder = new ProcessBuilder("file", "-b", file);
		try {
			Process process = processBuilder.start();
			process.waitFor();
			InputStream stdout = process.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(stdout));
			return (br.readLine().contains(expected));		
		} catch (IOException e) {
			JOptionPane.showMessageDialog(mediaPlayerComponent, getString("validMediaFile"));
		} catch (InterruptedException e) {
			JOptionPane.showMessageDialog(mediaPlayerComponent, getString("validMediaFile"));
		}
		return false;
	}	
	
	/**
	 * this is used by LoadingScreen to move the progress bar onto the bottom panel
	 * @param comp any component, but usually will be the progress bar
	 */
	public void addToBottomPanel(JComponent comp){
		bottomPanel.add(comp);
	}
	
	/**
	 * this is used by LoadingScreen to remove the progress bar off the bottom panel
	 * @param comp, the progress bar to be removed from the bottom panel
	 */
	public void removeFromBottomPanel(JComponent comp){
		bottomPanel.remove(comp);
	}
	
	/**
	 * changes the cursor to a crosshair if the textsection needs to get a certain position
	 * @param bool true if the cursor should be the crosshair, false if it should be normal
	 */
	public void setCursorOnOverlay(boolean bool){
		if (bool)
			overlay.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
		else
			overlay.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}
	
	/**
	 *  Used by the Audio/text sections to allow them to get the media file being played
	 * @return The string which comes straight from the filetextfield
	 */
	public String getMediaName() {
		return fileTextField.getText();
	}
	
	/**
	 * Used for the media adjust panel in the effects section
	 * @return
	 */
	public MediaPlayer getMediaPlayer(){
		return mediaPlayer;
	}
	
	/**
	 * Used by other editing section to get the current dimension of the frame
	 * @return the current dimension of the frame
	 */
	public Dimension getFrameDimensions(){
		return f.getBounds().getSize();
	}
	
	/** 
	 * Loads the settings into the user display
	 * @param projSettings the settings to be loaded
	 */
	public void loadSettings(ProjectSettings projSettings) {
		mediaPlayerComponent.getMediaPlayer().stop();
		fileTextField.setText(projSettings._mediaFile);
		downloadPanel.setUrl(projSettings._downloadUrl);
		audioSection.setAudioString(projSettings._audioFile);
		textSection.loadProjectSettings(projSettings);
		effectsSection.loadProjectSettings(projSettings);
	}
	/**
	 * 
	 * @return the project settings object containing all the settings currently in the 
	 * project
	 */
	private ProjectSettings createProjectSettings() {
		ProjectSettings settings = textSection.createProjectSettings();
		settings._mediaFile = fileTextField.getText();
		settings._audioFile = audioSection.getAudioString();
		settings = effectsSection.createProjectSettings(settings);
		settings._downloadUrl = downloadPanel.getUrl();
		return settings;
	}
	/**
	 * Checks if the file parameter is a text file which could then be a project settings
	 * file
	 * @param file The file to be checked
	 * @return true if it is a text file, false otherwise
	 */
	public Boolean isText(String file) {
		return (isFileType(file, "ASCII text"));
	}
	  
	/**
	 * This method gets the string that is associated with each label, in the correct language
	 * @param label
	 * @return the string for this label
	 */
	private String getString(String label){
		return LanguageSelector.getString(label);
	}
	
	/**
	 * This class represents the overlay window.
	 * It incorporates all the other sections for editing
	 * It is also transparent, and can be hidden or made to change foreground colour.
	 * @author Wesley
	 *
	 */
	  private class Overlay extends Window{

	        private static final long serialVersionUID = 1L;
	    	private MigLayout myLayout = new MigLayout("", "10 [] [] [grow] 10", "5 [] [grow]push[]5");

			public Overlay(Window owner, Vamix ep) {
	            super(owner, WindowUtils.getAlphaCompatibleGraphicsConfiguration());
	            //if this line has an error, go >project>properties>Java compiler>Errors/Warnings
	            AWTUtilities.setWindowOpaque(this, false);
	            setLayout(myLayout);
	            repaint(500, 0, 0, 1200, 1200);
           
	            //firstly add the load/save buttons
	            JPanel projectPanel = new JPanel();
	            TitledBorder border = BorderFactory.createTitledBorder(
	    				BorderFactory.createEtchedBorder(EtchedBorder.LOWERED, 
	    				new Color(150, 150, 250, 250), new Color(50, 50, 150, 250)), getString("project"));
	            border.setTitleColor(new Color(150, 150, 250, 250));
	    		border.setTitleFont(new Font("Sans Serif", Font.BOLD, 24));
	            projectPanel.setBorder(border);
	            projectPanel.setLayout(new MigLayout());
	            projectPanel.add(loadBtn);
	            projectPanel.add(new CustomButton("   "), "grow");
	            projectPanel.add(saveBtn, "right");
	            
	            //now we add the entire left side pane
	            JPanel rightSidePane = new JPanel();
	            rightSidePane.setLayout(new MigLayout());
	            audioSection = new AudioSection(ep, mediaPlayerComponent);
	            rightSidePane.add(audioSection, "wrap,grow");
	            textSection = new TextSection(ep, mainControlPanel);
	            rightSidePane.add(textSection, "wrap, grow");
	            //leftSidePane.add(projectPanel, "grow");
	            add(rightSidePane, "dock east");

	    		this.addMouseListener(textSection);

	    		//now we add the download panel, followed by the right side pane
	    		downloadPanel = new DownloadPanel(ep);
	            JPanel leftSidePane = new JPanel();
	            leftSidePane.setLayout(new MigLayout());
	            leftSidePane.add(downloadPanel, "wrap");
	            effectsSection = new EffectsSection(ep, mainControlPanel);
	            leftSidePane.add(effectsSection, "growx, wrap");
	            leftSidePane.add(projectPanel, "grow");
	            add(leftSidePane, "cell 0 0 1 3, grow");

	            //finally, the open panel which goes on the top
	            JPanel openPanel = new JPanel();
	            openPanel.add(fileTextField);
	            openPanel.add(openBtn);
	            add(openPanel, "cell 1 0 2 1");
	            setFocusable(true);
	            add(mainControlPanel, "cell 1 2 1 1, gap before 8%, grow");
	    		state.addColourListeners(rightSidePane, textSection, audioSection, mainControlPanel, projectPanel, leftSidePane,
	            		saveBtn, loadBtn, openPanel, openBtn, fileTextField);
	          	state.addStateListeners(rightSidePane, openPanel, projectPanel, mainControlPanel, leftSidePane);
	    		state.addMouseListeners(mainControlPanel);
	            
	    		//now set everything transparent
	            state.setTransparent();
	            
	        }
	    }
}
