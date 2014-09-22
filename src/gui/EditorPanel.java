package gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import Popups.AudioSection;
import Popups.TextSection;
import editing.GetAttributes;
import net.miginfocom.swing.MigLayout;
import uk.co.caprica.vlcj.binding.internal.libvlc_position_e;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.MediaMeta;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;

/**
 * This screen is used for all your video editing needs
 * 
 * @author Mathew and Wesley
 */
@SuppressWarnings("serial")
public class EditorPanel extends JPanel{


	private JLabel title = new JLabel ("Lets get editing");
	private final EmbeddedMediaPlayerComponent mediaPlayerComponent;
	private JSlider vidPosSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 0);
	private JButton playBtn = new CustomButton("Play");
	private JButton stopBtn = new CustomButton("Stop");
	private JButton forwardBtn = new CustomButton("Fast Forward");
	private JButton backwardBtn = new CustomButton("Rewind");
	private JButton openBtn = new JButton("Open");
	private JTextField fileTextField = new JTextField(40);
	private final Timer sliderTimer = new Timer(100, null);
	private final Timer videoMovementTimer = new Timer(100, null);
	private JTextField textArea = new JTextField(40);
	private JComboBox<String> titleOrCredits;
	private String titleText;
	private String creditsText;

	private MigLayout myLayout = new MigLayout("", "10 [] 10 []", "5 [] 5 []");

	EditorPanel () {
		this.setLayout(myLayout);
		
		title.setFont (new Font("Serif", Font.BOLD, 48));
		setBackground(new Color(100,100,100));

		// This is the video player
        mediaPlayerComponent = new EmbeddedMediaPlayerComponent();

    	/**
    	 * When the play button is clicked the video is started and the play button
    	 * turns into a pause button
    	 * Also the stop button is activated when video is playing and will get rid of the 
    	 * video so that another video can be loaded in
    	 */
        playBtn.addActionListener(new ActionListener(){
        	@Override
        	public void actionPerformed(ActionEvent arg0) {
        		currentMove = videoMovement.Nothing;
    			// If the media player is already playing audio/video then the button click 
    			// should pause it
    			if (mediaPlayerComponent.getMediaPlayer().isPlaying()) {
    				mediaPlayerComponent.getMediaPlayer().pause();
    				playBtn.setText("Play");
    				sliderTimer.stop();
    			// If the video is already pause then the button click 
    			// will continue to play it
    			} else if (mediaPlayerComponent.getMediaPlayer().isPlayable()) {
    				mediaPlayerComponent.getMediaPlayer().play();
    				playBtn.setText("Pause");
    				sliderTimer.start();
    			// Otherwise we will ask the user for the file they want to play and 
    			// start playing that
    			} else {
    				// If file already selected just play that
    				if (!fileTextField.getText().equals("")) {
	    				playMusic();
	    				playBtn.setText("Pause");
	    				sliderTimer.start();
    				} else {
    					chooseFile();
    				}
    			}
    			stopBtn.setEnabled(true);
    			sliderSetup();
        	}
        });
        stopBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				currentMove = videoMovement.Nothing;
				if (mediaPlayerComponent.getMediaPlayer().isPlaying()) {
					mediaPlayerComponent.getMediaPlayer().stop();
					playBtn.setText("Play");
				} else if (mediaPlayerComponent.getMediaPlayer().isPlayable()) {
					mediaPlayerComponent.getMediaPlayer().stop();
				} 
				stopBtn.setEnabled(false);
				sliderTimer.stop();
				vidPosSlider.setValue(0);
			}      	
        });
    	//When the open button is clicked the file chooser appears
        openBtn.addActionListener(new ActionListener(){
        	@Override
        	public void actionPerformed(ActionEvent arg0) {
        		chooseFile();
        		
        		titleText = GetAttributes.getTitle(fileTextField.getText());
        		creditsText = GetAttributes.getCredits(fileTextField.getText());
				if (titleOrCredits.getSelectedItem().toString().equalsIgnoreCase("Title"))
	            	textArea.setText(titleText);
				else if (titleOrCredits.getSelectedItem().toString().equalsIgnoreCase("Credits"))
	            	textArea.setText(creditsText);
        	}
        });
        
        forwardBtn.addActionListener(new ActionListener(){
        	@Override
        	public void actionPerformed(ActionEvent arg0) {
        		if (currentMove == videoMovement.Forward) {
        			currentMove = videoMovement.Nothing;
        		} else {
        			currentMove = videoMovement.Forward;
        		}
        	}
        });
        
        backwardBtn.addActionListener(new ActionListener(){
        	@Override
        	public void actionPerformed(ActionEvent arg0) {
        		if (currentMove == videoMovement.Back) {
        			currentMove = videoMovement.Nothing;
        		} else {
        			currentMove = videoMovement.Back;
        		}
        	}
        });
        
        
        stopBtn.setEnabled(false);
        playBtn.setIcon(new ImageIcon("assets/player_play.png"));
        stopBtn.setIcon(new ImageIcon("assets/agt_action_fail1.png"));
        forwardBtn.setIcon(new ImageIcon("assets/player_fwd.png"));
        backwardBtn.setIcon(new ImageIcon("assets/player_start.png"));
        
        vidPosSlider.setMajorTickSpacing(10);
		vidPosSlider.setMinorTickSpacing(1);
		vidPosSlider.setPaintTicks(true);
		vidPosSlider.addChangeListener(sliderChangeListener);
		vidPosSlider.setBackground(new Color(100,100,100));
		
		sliderTimer.addActionListener(timerListener);
		videoMovementTimer.addActionListener(secondTimerListener);
		videoMovementTimer.start();
		mediaPlayerComponent.setFont(new Font("Arial", 24, Font.BOLD));
		
		
        // The open file stuff is at top
        add(fileTextField, "split 2, grow");
        add(openBtn, "wrap");
        
        JPanel sidePane = new JPanel();
        
        sidePane.setLayout(new MigLayout());
        sidePane.add(new AudioSection(this, mediaPlayerComponent), "growx, wrap");
        sidePane.add(new TextSection(this), "grow");
        sidePane.setBackground(new Color(100,100,100));
        
        add(sidePane, "grow, split 2");
        // This media  player has massive preferred size in 
        // order to force it to fill the screen
        
        add(mediaPlayerComponent, "grow, wrap, height 200:10000:, width 400:10000:");
        add(vidPosSlider, "wrap, grow");
//        JPanel mediaPanel = new JPanel();
//        mediaPanel.setLayout(new FlowLayout());
//        mediaPanel.add(mediaPlayerComponent);
//        mediaPanel.add(vidPosSlider);
//        add(mediaPanel, "wrap, grow");
        
        JPanel mainControlPanel = new JPanel();
        mainControlPanel.add(backwardBtn, "split 4");
        mainControlPanel.add(playBtn);
        mainControlPanel.add(stopBtn);
        mainControlPanel.add(forwardBtn);
        mainControlPanel.setBackground(new Color(200,200,200));
        mainControlPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED));
        
        // Audio options
//        add(new AudioSection(this, mediaPlayerComponent), "split 3");
        // Basic video control (play, pause etc)
        add(mainControlPanel, "grow, center");
        // Text options
//        add(new TextSection(this), "right, wrap");
        
        
	}
	
	/**
	 * This method is used to change the JSlider to the size of the actual 
	 * media that will be playing
	 */
	private void sliderSetup() {
		MediaMeta mediaData = new MediaPlayerFactory().getMediaMeta(
				fileTextField.getText(), true);
		vidPosSlider.setMinimum(0);
		vidPosSlider.setMaximum((int)mediaData.getLength());
		vidPosSlider.setMajorTickSpacing((int)mediaData.getLength()/10);
		vidPosSlider.setMinorTickSpacing((int)mediaData.getLength()/100);
		vidPosSlider.setPaintTicks(true);
	}
	
	
	private int currentTime = 0;
	/** 
	 * This listens for timer events then updates the slider to the video position
	 */
	ActionListener timerListener = new ActionListener() {
	    @Override 
	    public void actionPerformed(ActionEvent e) {
	    	currentTime = (int)mediaPlayerComponent.getMediaPlayer().getTime();
	    	vidPosSlider.setValue(currentTime);
	    }
	};
	
	/**
	 * This listener is used to change the video position based on the user
	 * moving the slider
	 */
	ChangeListener sliderChangeListener = new ChangeListener() {
		@Override
		public void stateChanged(ChangeEvent arg0) {
		    if (vidPosSlider.getValue() > currentTime+200 || 
		    		vidPosSlider.getValue() < currentTime-200) {
		    	mediaPlayerComponent.getMediaPlayer().setTime(vidPosSlider.getValue());
		    }
		}
	};
	
	
	enum videoMovement {
		Forward, Back, Nothing
	}
	private videoMovement currentMove = videoMovement.Nothing;
	ActionListener secondTimerListener = new ActionListener() {
	    @Override 
	    public void actionPerformed(ActionEvent e) {
	    	if (currentMove == videoMovement.Forward) {
	    		mediaPlayerComponent.getMediaPlayer().setTime(
	    				mediaPlayerComponent.getMediaPlayer().getTime() + 200);
	    	} else if (currentMove == videoMovement.Back) {
	    		mediaPlayerComponent.getMediaPlayer().setTime(
	    				mediaPlayerComponent.getMediaPlayer().getTime() - 200);
	    	}
	    }
	};
	
	
	
	/** 
	 * Used to choose a media file to play
	 * Used from both open and play button
	 */
	private void chooseFile() {
		final JFileChooser fc = new JFileChooser();
        fc.showOpenDialog(fc);
        if (fc.getSelectedFile() != null){
        	if (isMediaFile(fc.getSelectedFile().getAbsolutePath().toString()))
        		fileTextField.setText(fc.getSelectedFile().getAbsolutePath().toString());
        	else
        		JOptionPane.showMessageDialog(mediaPlayerComponent, "Please enter a valid media file name.");
        }
	}
	
	private void playMusic() {
		mediaPlayerComponent.getMediaPlayer().setVideoTitleDisplay(libvlc_position_e.center, 0);
		mediaPlayerComponent.getMediaPlayer().playMedia(fileTextField.getText());
	}

	/** This method is to be called by the mainframe when it is exiting so that 
	 * the video is stopped (otherwise you will continue to hear the audio playing 
	 */
	public void stop() {
		mediaPlayerComponent.getMediaPlayer().stop();
	}
	
	public boolean isMediaFile(String input){
		return correctFileType(input, "Media")
	        	|| correctFileType(input, "media")
	        	|| correctFileType(input, "Audio");
	}
	
	/**
	 * This method is used to check that the file is a valid file type
	 * @return true if the user input matches expected file type, false if not
	 */
	private boolean correctFileType(String file, String expected) {
		//use the file command in linux
		ProcessBuilder processBuilder = new ProcessBuilder("file", "-b", file);
		try {
			Process process = processBuilder.start();
			process.waitFor();
			InputStream stdout = process.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(stdout));
			return (br.readLine().contains(expected));		
		} catch (IOException e) {
			//
		} catch (InterruptedException e) {
			//
		}
		return false;
	}	
	
	
	
	// Used by the Audio/text sections to allow them to get the media file being played
	public String getMediaName() {
		return fileTextField.getText();
	}
	
	
}
