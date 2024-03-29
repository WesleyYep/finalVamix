package gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import components.CustomButton;
import editing.CheckFileExists;
import net.miginfocom.swing.MigLayout;
import state.LanguageSelector;
import state.State;
import uk.co.caprica.vlcj.player.MediaPlayer;

/**
 * represents the main control panel that is found on the main window
 * 
 * @author wesley
 *
 */
@SuppressWarnings("serial")
public class MainControlPanel extends JPanel{
	private MediaPlayer mp;
	private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
	private boolean isPaused = true;
	private long duration;
	private long currentTime;
	//the use of setPositionValue is so that the position slider only fires change requests
	//when the user actually is changing its position
	private boolean setPositionValue;
	private JSlider vidPosSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 0);
	private JLabel timeLabel = new JLabel("hh:mm:ss");
	private CustomButton fullScreenBtn = new CustomButton(
			new ImageIcon(Vamix.class.getResource("/full_screen.png")), 30,30);
	private CustomButton playBtn = new CustomButton(
			new ImageIcon(Vamix.class.getResource("/player_play.png")), 60, 60, 
			new ImageIcon(Vamix.class.getResource("/pause.png")));
	private CustomButton stopBtn = new CustomButton(
			new ImageIcon(Vamix.class.getResource("/agt_action_fail1.png")), 50, 50);
	private CustomButton forwardBtn = new CustomButton(
			new ImageIcon(Vamix.class.getResource("/player_fwd.png")), 40, 40);
	private CustomButton backwardBtn = new CustomButton(
			new ImageIcon(Vamix.class.getResource("/player_start.png")), 40, 40);
	private JSlider volumeSlider = new JSlider(0, 200, 100);
	private CustomButton soundBtn = new CustomButton
    		(new ImageIcon(Vamix.class.getResource("/volume_loud.png")), 30, 30, 
    				new ImageIcon(Vamix.class.getResource("/volume_silent2.png")));
	private videoMovement currentMove = videoMovement.Nothing;
	private final Timer videoMovementTimer = new Timer(100, null);
	private Vamix vamix;
	private boolean isFullScreen = false;
	
	public MainControlPanel(MediaPlayer mp, Vamix v){
		this.mp = mp;
		this.vamix = v;
		
		//call this method to register all listeners
		registerListeners();
      	
		//set up all the sliders and timers
		executorService.scheduleAtFixedRate(new UpdateRunnable(mp), 0L, 1L, TimeUnit.SECONDS);
        vidPosSlider.setMajorTickSpacing(10);
		vidPosSlider.setMinorTickSpacing(1);
		vidPosSlider.setPaintTicks(true);
        volumeSlider.setOrientation(JSlider.VERTICAL);
        volumeSlider.setPreferredSize(new Dimension(17, 60));
		videoMovementTimer.addActionListener(secondTimerListener);
		videoMovementTimer.start();
		
		setLayout(new MigLayout());
		//add all components to the control panel
        add(vidPosSlider, "cell 0 0, grow");
        add(timeLabel, "wrap");
        add(fullScreenBtn);
        add(backwardBtn, "cell 0 1, split 5");
        add(playBtn);
        add(stopBtn);
        add(forwardBtn);
        add(soundBtn, "cell 1 1, top");
        add(volumeSlider, "cell 1 1");
        //add all gui components as colour listeners
        State.getState().addColourListeners(volumeSlider, vidPosSlider, timeLabel);
        
	}

	private void registerListeners() {
		//set slider to change position of video if it gets changed
		vidPosSlider.addChangeListener(new ChangeListener() {
	        @Override
	        public void stateChanged(ChangeEvent e) {
	          if(!vidPosSlider.getValueIsAdjusting() && !setPositionValue) {
	            float positionValue = (float)vidPosSlider.getValue() / 100.0f;
	            mp.setPosition(positionValue);
	          }
	        }
	      });
	    //play/pause media
		playBtn.addActionListener(new ActionListener(){
        	@Override
        	public void actionPerformed(ActionEvent arg0) {
        		if (mp.isPlaying()){
        			mp.pause();
        			isPaused = true;
        		}else if (mp.isPlayable()){
        			mp.play();
        			isPaused = false;
        		}else{
        			if (vamix.isMediaFile(vamix.getMediaName())){
            			if (!isPaused){playBtn.changeIcon();}
        				mp.playMedia(vamix.getMediaName());
            			isPaused = false;
        			}else{
        				JOptionPane.showMessageDialog(null, getString("validFileRequired"));
        				return;
        			}
        		}
        		currentMove = videoMovement.Nothing;
    			playBtn.changeIcon();
    			stopBtn.setEnabled(true);
        	}
        });
        //stop and reset media
        stopBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (!isPaused){
					playBtn.changeIcon();
				}
				stopBtn.setEnabled(false);
				isPaused = false;
				stopPlaying();
			}      	
        });
        //fast forward
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
        //rewind
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
        //change volume when volume slider is changed
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
        
        //toggle fullscreen
        fullScreenBtn.addActionListener(new ActionListener() {
        	@Override
        	public void actionPerformed(ActionEvent arg0) {
        		if(!isFullScreen){
        			vamix.setFullScreen();
        			isFullScreen = true;
        		}else{
        			vamix.exitFullScreen();
        			isFullScreen = false;
        		}
        	}
        });
        //this checks if media is muted or not
        volumeSlider.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				mp.setVolume(volumeSlider.getValue());
				if (volumeSlider.getValue() == 0) {
					soundBtn.changeIcon();
				}
			}
        });
        
	}
	
	//this is used to set that the video is fast forwarding, rewinding, or just playing normally
	enum videoMovement {
		Forward, Back, Nothing
	}
	
	//this is used for fastforward and rewinding
	ActionListener secondTimerListener = new ActionListener() {
	    @Override 
	    public void actionPerformed(ActionEvent e) {
	    	if (currentMove == videoMovement.Forward) {
	    		mp.setTime(
	    		mp.getTime() + 200);
	    	} else if (currentMove == videoMovement.Back) {
	    		mp.setTime(
	    		mp.getTime() - 200);
	    	}
	    }
	};
	
	//update position of slider every so often
	private void updatePosition(int value) {
	    // Set the guard to stop the update from firing a change event
		setPositionValue = true;
	    vidPosSlider.setValue(value);
	    setPositionValue = false;
	}
	
	//update the time display
	private void updateTime(long millis) {
		String s = String.format("%02d:%02d:%02d",
	    TimeUnit.MILLISECONDS.toHours(millis),
	    TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)), 
	    TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
	    timeLabel.setText(s);
	}
	
	/**
	 * This method is used to stop the current media file from playing, and reset button settings
	 */
    public void stopPlaying(){
		if (mp.isPlaying()) {
			mp.stop();
		} else if (mp.isPlayable()) {
			mp.stop();
		} 
		vidPosSlider.setValue(0);
    }
	
	/**
	 * This main Vamix class will call this method to set the duration after the file is loaded
	 */
	public void setDuration(long dur){
		duration = dur;
	}
	
	/**
	 * used by other objects to get the current time in the media
	 * @return current time position of the media
	 */
	public long getTime(){
		return mp.getTime();
	}
	/**
	 * This is called by the effects section to take a screen shot when the screenshot button is clicked
	 */
	public void captureScreenShot() {
		boolean wasPlaying = false;
		//if there is no media file, or a audio file, then warn the user
		if ((!mp.isPlayable() && !mp.isPlaying()) || vamix.isAudioFile()){
			JOptionPane.showMessageDialog(null, getString("noMediaToScreenshot"));
			return;
		}
		//pause video if it is playing
		else if (mp.isPlaying()){
			playBtn.doClick();
			wasPlaying = true;
		}

		final JFileChooser fc = new JFileChooser();
        fc.showSaveDialog(fc);
        if (fc.getSelectedFile() != null){
        	//add png extension if needed
        	String fileName = fc.getSelectedFile().getAbsolutePath().toString();
        	if (!fileName.endsWith(".png")){
        		fileName = fileName + ".png";
        	}
        	//give the user an option to overwrite if the file already exists
        	if (CheckFileExists.check(fileName)){
				if (JOptionPane.showConfirmDialog((Component) null, getString("fileExists"),
				        "alert", JOptionPane.OK_CANCEL_OPTION) != 0){
					JOptionPane.showMessageDialog(null, getString("notOverwritten"));
					return;
				}
        	}
	        File file = new File(fileName);
			vamix.getMediaPlayer().saveSnapshot(file);
			//play the video again, if it was previously playing
			if (wasPlaying){
				playBtn.doClick();
			}
        }
	}
	
	/**
	 * This class was taken from 
	 * http://vlcj.googlecode.com/svn-history/r412/trunk/vlcj/src/test/java/uk/co/caprica/vlcj/test/PlayerControlsPanel.java
	 * which is a demo vlcj project.
	 * 
	 * It is used to change time and position of the video slider
	 *
	 */
	private final class UpdateRunnable implements Runnable {

	    private final MediaPlayer mediaPlayer;
	    
	    private UpdateRunnable(MediaPlayer mediaPlayer) {
	      this.mediaPlayer = mediaPlayer;
	    }
	    
	    @Override
	    public void run() {
		  currentTime = mediaPlayer.getTime();
		  duration = mediaPlayer.getLength();
	      final int position = duration > 0 ? (int)Math.round(100.0 * (double)currentTime / (double)duration) : 0;
	      if (mp.isPlaying() && playBtn.mainInUse()){
	    	  playBtn.forceSecondIcon();
	      }
	      
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

	/**
	 * used to disable stop button on initialisation 
	 */
	public void enableStopButton() {
        stopBtn.setEnabled(false);      
	}

	/**
	 * Used by other objects to set if the frame is fullscreen
	 * @param fullscreen true if fullscreen, false otherwise
	 */
	public void isFullScreen(boolean fullscreen) {
		isFullScreen = fullscreen;
	}
	
	/**
	 * This method gets the string that is associated with each label, in the correct language
	 * @param label
	 * @return the string for this label
	 */
	private String getString(String label){
		return LanguageSelector.getString(label);
	}

}
