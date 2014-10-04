package gui;

import java.awt.Color;
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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import Popups.AudioSection;
import Popups.TextSection;
import editing.ProjectFile;
import editing.ProjectFile.ProjectSettings;
import net.miginfocom.swing.MigLayout;
import uk.co.caprica.vlcj.binding.internal.libvlc_position_e;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.MediaMeta;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

/**
 * This screen is used for all your video editing needs
 * The images used in the buttons have been taken from: http://www.softicons.com/
 * The timing services including the executor service and UpdateRunnable class have been taken from
 * http://vlcj.googlecode.com/svn-history/r412/trunk/vlcj/src/test/java/uk/co/caprica/vlcj/test/PlayerControlsPanel.java
 * 
 * @author Wesley
 */
@SuppressWarnings("serial")
public class EditorPanel extends JPanel{

	private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
	private JLabel title = new JLabel ("Lets get editing");
	private final EmbeddedMediaPlayerComponent mediaPlayerComponent;
	private MediaPlayer mediaPlayer;
	private JSlider vidPosSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 0);
	private CustomButton fullScreenBtn = new CustomButton(
			new ImageIcon(EditorPanel.class.getResource("/full_screen.png")), 30,30);
	private CustomButton playBtn = new CustomButton(
			new ImageIcon(EditorPanel.class.getResource("/player_play.png")), 60, 60, 
			new ImageIcon(EditorPanel.class.getResource("/pause.png")));
	private CustomButton stopBtn = new CustomButton(
			new ImageIcon(EditorPanel.class.getResource("/agt_action_fail1.png")), 50, 50);
	private CustomButton forwardBtn = new CustomButton(
			new ImageIcon(EditorPanel.class.getResource("/player_fwd.png")), 40, 40);
	private CustomButton backwardBtn = new CustomButton(
			new ImageIcon(EditorPanel.class.getResource("/player_start.png")), 40, 40);
	private JSlider volumeSlider = new JSlider(0, 200, 100);
	private CustomButton soundBtn = new CustomButton
    		(new ImageIcon(EditorPanel.class.getResource("/volume_loud.png")), 30, 30, 
    				new ImageIcon(EditorPanel.class.getResource("/volume_silent2.png")));
	private JButton openBtn = new JButton("Open");
	private JTextField fileTextField = new JTextField(40);
	private CustomButton loadBtn = new CustomButton("Load", new ImageIcon(
			EditorPanel.class.getResource("/upload.png")), 25, 25);
	private JButton saveBtn = new CustomButton("Save", new ImageIcon(
			EditorPanel.class.getResource("/download.png")), 25, 25);
	private ProjectFile projFile = ProjectFile.getInstance(this);
	private final Timer videoMovementTimer = new Timer(100, null);
	private AudioSection audioSection;
	private TextSection textSection;
	private MigLayout myLayout = new MigLayout("", "10 [] [] 10", "5 [] [] [] [] 5");
	private boolean isPreviewing = false;
	private boolean isPaused = false;
	private JLabel timeLabel = new JLabel("hh:mm:ss");
	private long duration;
	private long currentTime;
	//the use of setPositionValue is so that the position slider only fires change requests
	//when the user actually is changing its position
	private boolean setPositionValue;


	EditorPanel () {
		this.setLayout(myLayout);
        mediaPlayerComponent = new EmbeddedMediaPlayerComponent();
        mediaPlayer = mediaPlayerComponent.getMediaPlayer();
        registerListeners();
        executorService.scheduleAtFixedRate(new UpdateRunnable(mediaPlayer), 0L, 1L, TimeUnit.SECONDS);

        //set up some stuff
		title.setFont (new Font("Serif", Font.BOLD, 48));
        stopBtn.setEnabled(false); //diable stop button on initialisation       
        vidPosSlider.setMajorTickSpacing(10);
		vidPosSlider.setMinorTickSpacing(1);
		vidPosSlider.setPaintTicks(true);
		mediaPlayerComponent.setFont(new Font("Arial", 24, Font.BOLD));
        loadBtn.setVerticalTextPosition(SwingConstants.CENTER);
        loadBtn.setHorizontalTextPosition(SwingConstants.RIGHT);
        saveBtn.setVerticalTextPosition(SwingConstants.CENTER);
        saveBtn.setHorizontalTextPosition(SwingConstants.RIGHT);
        volumeSlider.setOrientation(JSlider.VERTICAL);
        volumeSlider.setPreferredSize(new Dimension(17, 60));
		videoMovementTimer.addActionListener(secondTimerListener);
		videoMovementTimer.start();
		
        addComponents();
        
	}
	
	private void addComponents() {
		add(fileTextField, "cell 0 0 2 1, split 2, grow");
        add(openBtn, "wrap");
        
        JPanel projectPanel = new JPanel();
        projectPanel.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(EtchedBorder.LOWERED, 
				new Color(50, 150, 50, 250), new Color(50, 150, 50, 250)), "Project"));
        projectPanel.setLayout(new MigLayout());
        projectPanel.add(loadBtn);
        projectPanel.add(new CustomButton("   "), "grow");
        projectPanel.add(saveBtn, "right");
        
        JPanel sidePane = new JPanel();
        sidePane.setLayout(new MigLayout());
        audioSection = new AudioSection(this, mediaPlayerComponent);
        sidePane.add(audioSection, "growx, wrap");
        textSection = new TextSection(this);
        sidePane.add(textSection, "grow, wrap");
        sidePane.add(projectPanel, "grow");
        
        add(sidePane, "cell 0 1 1 3, grow");
        add(mediaPlayerComponent, "cell 1 1, grow, wrap, height 200:10000:, width 400:10000:");
        add(vidPosSlider, "cell 1 2, wrap, grow");
        
        JPanel mainControlPanel = new JPanel();
        mainControlPanel.add(fullScreenBtn);
        mainControlPanel.add(backwardBtn, "cell 0 0, split 5");
        mainControlPanel.add(playBtn);
        mainControlPanel.add(stopBtn);
        mainControlPanel.add(forwardBtn);
        mainControlPanel.add(soundBtn, "cell 1 0, top");
        mainControlPanel.add(volumeSlider, "cell 1 0");
        add(mainControlPanel, "cell 1 3, grow, center");
	}

	private void registerListeners() {
		
		mediaPlayer.addMediaPlayerEventListener(new MediaPlayerEventAdapter() {
		      @Override
		      public void playing(MediaPlayer mediaPlayer) {
		        updateVolume(mediaPlayer.getVolume());
		      }
		    });		
		
	    vidPosSlider.addChangeListener(new ChangeListener() {
	        @Override
	        public void stateChanged(ChangeEvent e) {
	          if(!vidPosSlider.getValueIsAdjusting() && !setPositionValue) {
	            float positionValue = (float)vidPosSlider.getValue() / 100.0f;
	            mediaPlayer.setPosition(positionValue);
	          }
	        }
	      });
		
        playBtn.addActionListener(new ActionListener(){
        	@Override
        	public void actionPerformed(ActionEvent arg0) {
        		if (mediaPlayer.isPlaying()){
        			mediaPlayer.pause();
        			isPaused = true;
        		}else if (mediaPlayer.isPlayable()){
        			mediaPlayer.play();
        			isPaused = false;
        		}else{
    				mediaPlayer.playMedia(fileTextField.getText());
        			isPaused = false;
        		}
        		currentMove = videoMovement.Nothing;
    			playBtn.changeIcon();
    			stopBtn.setEnabled(true);
        	}
        });
        
        stopBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				isPaused = false;
				stopPlaying();
			}      	
        });
        
    	//When the open button is clicked the file chooser appears
        openBtn.addActionListener(new ActionListener(){
        	@Override
        	public void actionPerformed(ActionEvent arg0) {
				stopPlaying();
        		chooseFile();
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
        		final EmbeddedMediaPlayerComponent mediaPlayerFull = new EmbeddedMediaPlayerComponent();
        		class FirstFrame extends JFrame implements WindowListener {
        			FirstFrame() {
        				this.addWindowListener(this);
        			}
        		    @Override
        		    public void windowClosed(WindowEvent e) {
        		        // Stop the media player (otherwise you will gear it continuing
        		    	mediaPlayerFull.getMediaPlayer().stop();
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
        		f.setIconImage(new ImageIcon(getClass().getClassLoader().getResource("icon.png")).getImage());

        	    
        	    f.add(mediaPlayerFull);
        	    f.setVisible(true);
        	    f.setExtendedState(JFrame.MAXIMIZED_BOTH);
        	    
        	    mediaPlayerFull.getMediaPlayer().playMedia(fileTextField.getText(), ":start-time=" + (int)currentTime/1000);
        	}
        });
        
        volumeSlider.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				mediaPlayerComponent.getMediaPlayer().setVolume(volumeSlider.getValue());
				if (volumeSlider.getValue() == 0) {
					soundBtn.changeIcon();
				}
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
		        }
			}
        });
	}

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
	 * This method is used to stop the current media file from playing, and reset button settings
	 */
    private void stopPlaying(){
		if (mediaPlayerComponent.getMediaPlayer().isPlaying()) {
			mediaPlayerComponent.getMediaPlayer().stop();
			playBtn.changeIcon();
		} else if (mediaPlayerComponent.getMediaPlayer().isPlayable()) {
			mediaPlayerComponent.getMediaPlayer().stop();
		} 
		setIsPreviewing(false);
		stopBtn.setEnabled(false);
		vidPosSlider.setValue(0);
    }
	
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
        		JOptionPane.showMessageDialog(mediaPlayerComponent, 
        				"Please enter a valid media file name.");
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
			JOptionPane.showMessageDialog(mediaPlayerComponent, "Please enter a valid file name.");
		} catch (InterruptedException e) {
			JOptionPane.showMessageDialog(mediaPlayerComponent, "Please enter a valid file name.");
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
	
	public void playPreview(){
		isPreviewing = true;
		vidPosSlider.setValue(0);
		String mediaUrl = "udp://@:1234";
		mediaPlayerComponent.getMediaPlayer().playMedia(mediaUrl);
		playBtn.changeIcon();
	}
	
	public void setDuration(long dur){
		duration = dur;
	}
	
	public void setIsPreviewing(boolean previewing){
		this.isPreviewing = previewing;
		if (previewing){
			forwardBtn.setEnabled(false);
			backwardBtn.setEnabled(false);
			stopBtn.setEnabled(true);
			playBtn.changeIcon();
		}else{
			forwardBtn.setEnabled(true);
			backwardBtn.setEnabled(true);
		}
	}
	
	/**
	 * This class was taken from 
	 * http://vlcj.googlecode.com/svn-history/r412/trunk/vlcj/src/test/java/uk/co/caprica/vlcj/test/PlayerControlsPanel.java
	 * which is a demo vlcj project.
	 *
	 */
	private final class UpdateRunnable implements Runnable {

	    private final MediaPlayer mediaPlayer;
	    
	    private UpdateRunnable(MediaPlayer mediaPlayer) {
	      this.mediaPlayer = mediaPlayer;
	    }
	    
	    @Override
	    public void run() {
	      if (isPreviewing && !isPaused)
	    	  currentTime += 1000;
	      else if (!isPreviewing){
		      currentTime = mediaPlayer.getTime();
	    	  duration = mediaPlayer.getLength();
	      }
	      final int position = duration > 0 ? (int)Math.round(100.0 * (double)currentTime / (double)duration) : 0;
//	      System.out.println("Time: " + currentTime);
//	      System.out.println("Dur: " + duration);

	      // Updates to user interface components must be executed on the Event
	      // Dispatch Thread
	      SwingUtilities.invokeLater(new Runnable() {
	        @Override
	        public void run() {
	          updateTime(currentTime);
	          updatePosition(position);
	        }
	      });
	    }
	  }
	  
	  private void updateTime(long millis) {
	    String s = String.format("%02d:%02d:%02d",
	      TimeUnit.MILLISECONDS.toHours(millis),
	      TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)), 
	      TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))
	    );
	    timeLabel.setText(s);
	  }

	  private void updatePosition(int value) {
	    // Set the guard to stop the update from firing a change event
	    setPositionValue = true;
	    vidPosSlider.setValue(value);
	    setPositionValue = false;
	  }
	   
	  private void updateVolume(int value) {
	    volumeSlider.setValue(value);
	  }
	
}
