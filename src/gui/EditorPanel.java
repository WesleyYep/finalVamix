package gui;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import Popups.AudioSection;
import Popups.TextSection;
import editing.GetAttributes;
import editing.ProjectFile.ProjectSettings;
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
	private CustomButton fullScreenBtn = new CustomButton(
			new ImageIcon("assets/full_screen.png"), 30,30);
	private CustomButton playBtn = new CustomButton(
			new ImageIcon("assets/player_play.png"), 60, 60, new ImageIcon("assets/pause.png"));
	private CustomButton stopBtn = new CustomButton(
			new ImageIcon("assets/agt_action_fail1.png"), 50, 50);
	private CustomButton forwardBtn = new CustomButton(
			new ImageIcon("assets/player_fwd.png"), 40, 40);
	private CustomButton backwardBtn = new CustomButton(
			new ImageIcon("assets/player_start.png"), 40, 40);
	private JSlider volumeSlider = new JSlider(0, 200, 100);
	private CustomButton soundBtn = new CustomButton
    		(new ImageIcon("assets/volume_loud.png"), 30, 30, 
    				new ImageIcon("assets/volume_silent2.png"));
	private JButton openBtn = new JButton("Open");
	private JTextField fileTextField = new JTextField(40);
	private final Timer sliderTimer = new Timer(100, null);
	private final Timer videoMovementTimer = new Timer(100, null);
	private JTextField textArea = new JTextField(40);
	private JComboBox<String> titleOrCredits;
	private String titleText;
	private String creditsText;

	private MigLayout myLayout = new MigLayout("", "10 [] [] 10", "5 [] [] [] [] 5");

	EditorPanel () {
		this.setLayout(myLayout);
		
		title.setFont (new Font("Serif", Font.BOLD, 48));
//		setBackground(new Color(100,100,100));

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
    				playBtn.changeIcon();
    				sliderTimer.stop();
    			// If the video is already pause then the button click 
    			// will continue to play it
    			} else if (mediaPlayerComponent.getMediaPlayer().isPlayable()) {
    				mediaPlayerComponent.getMediaPlayer().play();
    				playBtn.changeIcon();
    				sliderTimer.start();
    			// Otherwise we will ask the user for the file they want to play and 
    			// start playing that
    			} else {
    				// If file already selected just play that
    				if (!fileTextField.getText().equals("")) {
	    				playMusic();
	    				playBtn.changeIcon();
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
					playBtn.changeIcon();
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
        
        soundBtn.addActionListener(new ActionListener() {
        	@Override
        	public void actionPerformed(ActionEvent arg0) {
        		// TODO
        		if (volumeSlider.getValue() == 0) {
        			volumeSlider.setValue(100);
        			soundBtn.changeIcon();
        		} else {
        			volumeSlider.setValue(0);
        		}
        		
        		
        	}
        	
        });
        fullScreenBtn.addActionListener(new ActionListener() {
        	@Override
        	public void actionPerformed(ActionEvent arg0) {
        		final EmbeddedMediaPlayerComponent mediaPlayer = new EmbeddedMediaPlayerComponent();
        		class FirstFrame extends JFrame implements WindowListener {
        			FirstFrame() {
        				this.addWindowListener(this);
        			}
        		    @Override
        		    public void windowClosed(WindowEvent e) {
        		        // Stop the media player (otherwise you will gear it continuing
        		    	mediaPlayer.getMediaPlayer().stop();
        		        dispose();
        		    }
					@Override
					public void windowActivated(WindowEvent arg0) {}
					@Override
					public void windowClosing(WindowEvent arg0) {}
					@Override
					public void windowDeactivated(WindowEvent arg0) {}
					@Override
					public void windowDeiconified(WindowEvent arg0) {}
					@Override
					public void windowIconified(WindowEvent arg0) {}
					@Override
					public void windowOpened(WindowEvent arg0) {}
        		}
        	    JFrame f = new FirstFrame();
        	    f.setSize(800, 600);

        	    
        	    f.add(mediaPlayer);
        	    f.setVisible(true);
        	    f.setExtendedState(JFrame.MAXIMIZED_BOTH);
        	    
        	    mediaPlayer.getMediaPlayer().playMedia(fileTextField.getText());
        	}
        });
        
        volumeSlider.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				// TODO Auto-generated method stub
				mediaPlayerComponent.getMediaPlayer().setVolume(volumeSlider.getValue());
				if (volumeSlider.getValue() == 0) {
					soundBtn.changeIcon();
				}
			}
        });
        
        stopBtn.setEnabled(false);
        
        vidPosSlider.setMajorTickSpacing(10);
		vidPosSlider.setMinorTickSpacing(1);
		vidPosSlider.setPaintTicks(true);
		vidPosSlider.addChangeListener(sliderChangeListener);
		
		sliderTimer.addActionListener(timerListener);
		videoMovementTimer.addActionListener(secondTimerListener);
		videoMovementTimer.start();
		mediaPlayerComponent.setFont(new Font("Arial", 24, Font.BOLD));
		
		
        // The open file stuff is at top
        add(fileTextField, "cell 0 0 2 1, split 2, grow");
        add(openBtn, "wrap");
        
        JPanel sidePane = new JPanel();
        
        sidePane.setLayout(new MigLayout());
        sidePane.add(new AudioSection(this, mediaPlayerComponent), "growx, wrap");
        sidePane.add(new TextSection(this), "grow");
        
        add(sidePane, "cell 0 1 1 3, grow");
        
        // This media  player has massive preferred size in 
        // order to force it to fill the screen
        add(mediaPlayerComponent, "cell 1 1, grow, wrap, height 200:10000:, width 400:10000:");
        add(vidPosSlider, "cell 1 2, wrap, grow");
        
        JPanel mainControlPanel = new JPanel();
//        mainControlPanel.setLayout(new MigLayout("", " [] [][]", ""));
        mainControlPanel.add(fullScreenBtn);
        mainControlPanel.add(backwardBtn, "cell 0 0, split 5");
        mainControlPanel.add(playBtn);
        mainControlPanel.add(stopBtn);
        mainControlPanel.add(forwardBtn);
        mainControlPanel.add(soundBtn, "cell 1 0, top");
        mainControlPanel.add(volumeSlider, "cell 1 0");
        
        volumeSlider.setOrientation(JSlider.VERTICAL);
        volumeSlider.setPreferredSize(new Dimension(17, 60));
        
        add(mainControlPanel, "cell 1 3, grow, center");
        
        
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
	
	/**
	 *  Used by the Audio/text sections to allow them to get the media file being played
	 * @return The string which comes straight from the filetextfield
	 */
	public String getMediaName() {
		return fileTextField.getText();
	}
	
	public void loadSettings(ProjectSettings projSettings) {
		mediaPlayerComponent.getMediaPlayer().stop();
		fileTextField.setText(projSettings._mediaFile);
	}
}
