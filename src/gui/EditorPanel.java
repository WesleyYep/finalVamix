package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
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
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import com.sun.jna.platform.WindowUtils;
import com.sun.awt.AWTUtilities;

import components.SmallColourPanel;
import editing.ProjectFile;
import editing.ProjectFile.ProjectSettings;
import net.miginfocom.swing.MigLayout;
import state.LanguageSelector;
import state.State;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;

/**
 * This screen is used for all your video editing needs
 * The images used in the buttons have been taken from: http://www.softicons.com/
 * The timing services including the executor service and UpdateRunnable class have been taken from
 * http://vlcj.googlecode.com/svn-history/r412/trunk/vlcj/src/test/java/uk/co/caprica/vlcj/test/PlayerControlsPanel.java
 * 
 * @author Wesley
 */
@SuppressWarnings("serial")
public class EditorPanel implements MouseMotionListener{

	private final EmbeddedMediaPlayerComponent mediaPlayerComponent;
	private MediaPlayer mediaPlayer;
	private JButton openBtn = new JButton(getString("open"));
	private JTextField fileTextField = new JTextField(20);
    private JButton showHideBtn = new JButton(getString("hide"));
    private JButton languageBtn = new JButton();

	private CustomButton loadBtn = new CustomButton(getString("load"), new ImageIcon(
			EditorPanel.class.getResource("/upload.png")), 25, 25);
	private JButton saveBtn = new CustomButton(getString("save"), new ImageIcon(
			EditorPanel.class.getResource("/download.png")), 25, 25);
	private ProjectFile projFile = ProjectFile.getInstance(this);
	private AudioSection audioSection;
	private TextSection textSection;
	private MainControlPanel mainControlPanel;
	private Frame f;
	private State state;
	private Window overlay;
	private JPanel bottomPanel;
	private EffectsSection effectsSection;
	private static String language = "en NZ";

    public static void main(final String[] args) throws Exception {
    	if (args.length!=0){
    		LanguageSelector.setLanguage(args[0], args[1]);
    		language = args[0] + " " + args[1];
    	}else{
    		LanguageSelector.setLanguage("en", "NZ");
    	}
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new EditorPanel();
            }
        });
    }

	EditorPanel () {
        f = new Frame("Vamix");
        f.setSize(1280, 750);
        f.setBackground(Color.black);
        f.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
		f.setLayout(new BorderLayout());
		mediaPlayerComponent = new EmbeddedMediaPlayerComponent();
      	mediaPlayer = mediaPlayerComponent.getMediaPlayer();
      	
      	registerListeners();

        //set up some stuff		
        mainControlPanel = new MainControlPanel(mediaPlayerComponent.getMediaPlayer(), this);
		mainControlPanel.enableStopButton();

		mediaPlayerComponent.setFont(new Font("Arial", 24, Font.BOLD));
        loadBtn.setVerticalTextPosition(SwingConstants.CENTER);
        loadBtn.setHorizontalTextPosition(SwingConstants.RIGHT);
        saveBtn.setVerticalTextPosition(SwingConstants.CENTER);
        saveBtn.setHorizontalTextPosition(SwingConstants.RIGHT);
        
        if(language.equals("en NZ")){
        	languageBtn.setText(getString("maori"));
        }else{
        	languageBtn.setText(getString("english"));
        }
		
        f.add(mediaPlayerComponent, BorderLayout.CENTER);
        
        bottomPanel = new JPanel();
        showHideBtn.setBackground(Color.BLACK);
        showHideBtn.setForeground(Color.LIGHT_GRAY);
        languageBtn.setBackground(Color.BLACK);
        languageBtn.setForeground(Color.LIGHT_GRAY);
        bottomPanel.add(languageBtn, BorderLayout.EAST);
        bottomPanel.add(showHideBtn);
        bottomPanel.add(new SmallColourPanel(this));
        bottomPanel.setBackground(Color.BLACK);
        f.add(bottomPanel, BorderLayout.SOUTH);
        
        f.setVisible(true);
        state = State.getState();
        
        overlay = new Overlay(f,this);
		overlay.addMouseMotionListener(this);

      	mediaPlayerComponent.getMediaPlayer().setOverlay(overlay);
      	mediaPlayerComponent.getMediaPlayer().enableOverlay(true);
	}
	

	private void registerListeners() {
		
		mediaPlayer.addMediaPlayerEventListener(new MediaPlayerEventAdapter() {
		      @Override
		      public void playing(MediaPlayer mediaPlayer) {
		    	  mainControlPanel.updateVolume(mediaPlayer.getVolume());
		      }
		    });		
		
        
        
    	//When the open button is clicked the file chooser appears
        openBtn.addActionListener(new ActionListener(){
        	@Override
        	public void actionPerformed(ActionEvent arg0) {
				mainControlPanel.stopPlaying();
        		chooseFile();
        	}
        });

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
	        		effectsSection.setSpinnerDefault();
		        }
			}
        });
        
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
			currentJar = new File(EditorPanel.class.getProtectionDomain().getCodeSource().getLocation().toURI());
			if(!currentJar.getName().endsWith(".jar")){
				JOptionPane.showMessageDialog(mediaPlayerComponent, "Language features only work when program is run from jar file");
				return;
			}
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

	@Override
	public void mouseDragged(MouseEvent e) {
		state.showMouseControls();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		state.showMouseControls();
	}
	
	public void setFullScreen(long currentTime, EditorPanel ep, boolean isPaused){
	    f.setExtendedState(JFrame.MAXIMIZED_BOTH);
	}
	
	public void exitFullScreen() {
		f.setExtendedState(JFrame.NORMAL);
        f.setSize(1200, 750);
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
        		effectsSection.setSpinnerDefault();
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
	
	public void addToBottomPanel(JComponent comp){
		bottomPanel.add(comp);
	}
	
	/**
	 *  Used by the Audio/text sections to allow them to get the media file being played
	 * @return The string which comes straight from the filetextfield
	 */
	public String getMediaName() {
		return fileTextField.getText();
	}
	
	/** 
	 * Loads the settings into the user display
	 * @param projSettings the settings to be loaded
	 */
	public void loadSettings(ProjectSettings projSettings) {
		mediaPlayerComponent.getMediaPlayer().stop();
		fileTextField.setText(projSettings._mediaFile);
		audioSection.setAudioString(projSettings._audioFile);
		textSection.loadProjectSettings(projSettings);
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
	  
	  private class Overlay extends Window {

	        private static final long serialVersionUID = 1L;
	    	//private MigLayout myLayout = new MigLayout();
	    	private MigLayout myLayout = new MigLayout("", "10 [] [] [grow] 10", "20 [] [] [] [grow] 5");

	        public Overlay(Window owner, EditorPanel ep) {
	            super(owner, WindowUtils.getAlphaCompatibleGraphicsConfiguration());
	            
	            AWTUtilities.setWindowOpaque(this, false);

	            setLayout(myLayout);
	            repaint(500, 0, 0, 1200, 1200);
           
	            JPanel projectPanel = new JPanel();
	            TitledBorder border = BorderFactory.createTitledBorder(
	    				BorderFactory.createEtchedBorder(EtchedBorder.LOWERED, 
	    				new Color(50, 150, 50, 250), new Color(50, 150, 50, 250)), getString("project"));
	            border.setTitleColor(new Color(50, 150, 50, 250));
	    		border.setTitleFont(new Font("Sans Serif", Font.BOLD, 24));
	            
	            projectPanel.setBorder(border);
	            projectPanel.setLayout(new MigLayout());
	            projectPanel.add(loadBtn);
	            projectPanel.add(new CustomButton("   "), "grow");
	            projectPanel.add(saveBtn, "right");
	            
	            JPanel leftSidePane = new JPanel();
	            leftSidePane.setLayout(new MigLayout());
	            audioSection = new AudioSection(ep, mediaPlayerComponent);
	            leftSidePane.add(audioSection, "wrap");
	            textSection = new TextSection(ep, mainControlPanel);
	            leftSidePane.add(textSection, "wrap, grow");
	            leftSidePane.add(projectPanel, "grow");
	            
	            add(leftSidePane, "cell 0 1 1 3, grow, gaptop 10");
	            
	            JPanel rightSidePane = new JPanel();
	            rightSidePane.setLayout(new MigLayout());
	            rightSidePane.add(new DownloadPanel(ep), "wrap");
	            effectsSection = new EffectsSection(ep, mainControlPanel);
	            rightSidePane.add(effectsSection, "growx");
	            add(rightSidePane, "dock east, gaptop 30");
	            
	            JPanel openPanel = new JPanel();
	            openPanel.add(fileTextField);
	            openPanel.add(openBtn);
	            add(openPanel, "cell 1 1 2 1");
	            
	            setFocusable(true);
	            add(mainControlPanel, "dock south, gapbefore 30%");
	    		state.addColourListeners(leftSidePane, textSection, audioSection, mainControlPanel, projectPanel, rightSidePane,
	            		saveBtn, loadBtn, openPanel, openBtn, fileTextField);
	          	state.addStateListeners(leftSidePane, openPanel, projectPanel, mainControlPanel, rightSidePane);
	    		state.addMouseListeners(mainControlPanel);
	            
	            state.setTransparent();
	            
	        }
	    }
	  
	private String getString(String label){
		return LanguageSelector.getString(label);
	}
}
