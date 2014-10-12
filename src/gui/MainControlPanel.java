package gui;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.miginfocom.swing.MigLayout;
import state.LanguageSelector;
import state.State;
import uk.co.caprica.vlcj.player.MediaPlayer;

@SuppressWarnings("serial")
public class MainControlPanel extends JPanel{
	private MediaPlayer mp;
	private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
	private boolean isPreviewing = false;
	private boolean isPaused = false;
	private long duration;
	private long currentTime;
	//the use of setPositionValue is so that the position slider only fires change requests
	//when the user actually is changing its position
	private boolean setPositionValue;
	private JSlider vidPosSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 0);
	private JLabel timeLabel = new JLabel("hh:mm:ss");
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
	private videoMovement currentMove = videoMovement.Nothing;
	private final Timer videoMovementTimer = new Timer(100, null);
	private EditorPanel ep;
	private boolean isFullScreen = false;
//	private State state;
	
	public MainControlPanel(MediaPlayer mp, EditorPanel ep/*, State state*/){
		this.mp = mp;
		this.ep = ep;
		registerListeners();
      	executorService.scheduleAtFixedRate(new UpdateRunnable(mp), 0L, 1L, TimeUnit.SECONDS);
        vidPosSlider.setMajorTickSpacing(10);
		vidPosSlider.setMinorTickSpacing(1);
		vidPosSlider.setPaintTicks(true);
        volumeSlider.setOrientation(JSlider.VERTICAL);
        volumeSlider.setPreferredSize(new Dimension(17, 60));
		videoMovementTimer.addActionListener(secondTimerListener);
		videoMovementTimer.start();
		
		setLayout(new MigLayout());
        add(vidPosSlider, "cell 0 0, grow");
        add(timeLabel, "wrap");
        add(fullScreenBtn);
        add(backwardBtn, "cell 0 1, split 5");
        add(playBtn);
        add(stopBtn);
        add(forwardBtn);
        add(soundBtn, "cell 1 1, top");
        add(volumeSlider, "cell 1 1");
        
        State.getState().addColourListeners(volumeSlider, vidPosSlider, timeLabel);
        
	}


	private void registerListeners() {
		vidPosSlider.addChangeListener(new ChangeListener() {
	        @Override
	        public void stateChanged(ChangeEvent e) {
	          if(!vidPosSlider.getValueIsAdjusting() && !setPositionValue) {
	            float positionValue = (float)vidPosSlider.getValue() / 100.0f;
	            mp.setPosition(positionValue);
	          }
	        }
	      });
	    
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
        			if (ep.isMediaFile(ep.getMediaName())){
        				mp.playMedia(ep.getMediaName());
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
        
        stopBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				isPaused = false;
				stopPlaying();
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
        		if(!isFullScreen)
        			ep.setFullScreen(currentTime, ep, isPaused);
        		else
        			ep.exitFullScreen();
        	}
        });
        
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
	
	enum videoMovement {
		Forward, Back, Nothing
	}
	
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
	
	private void updatePosition(int value) {
	    // Set the guard to stop the update from firing a change event
		setPositionValue = true;
	    vidPosSlider.setValue(value);
	    setPositionValue = false;
	}
	
	public void changePlayIcon(){
		playBtn.changeIcon();
	}
	  
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
			playBtn.changeIcon();
		} else if (mp.isPlayable()) {
			mp.stop();
		} 
		if (isPreviewing){
			setIsPreviewing(false);
			ep.cancelPreviewTextSection();
			ep.cancelPreviewEffectsSection();
		}
		stopBtn.setEnabled(false);
		vidPosSlider.setValue(0);
    }
    
	public void playPreview(){
		isPreviewing = true;
		currentTime = 0;
		vidPosSlider.setValue(0);
		String mediaUrl = "udp://@:1234";
		mp.playMedia(mediaUrl);
		playBtn.changeIcon();
	}
	
	public void setDuration(long dur){
		duration = dur;
	}
	
	public long getTime(){
		return mp.getTime();
	}
	
	public void setIsPreviewing(boolean previewing){
		this.isPreviewing = previewing;
		if (previewing){
			forwardBtn.setEnabled(false);
			backwardBtn.setEnabled(false);
			stopBtn.setEnabled(true);
		}else{
			forwardBtn.setEnabled(true);
			backwardBtn.setEnabled(true);
		}
	}
	
	public boolean getIsPreviewing(){
		return isPreviewing;
	}
	
	  public void updateVolume(int value) {
		  volumeSlider.setValue(value);
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

	public void enableStopButton() {
        stopBtn.setEnabled(false); //disable stop button on initialisation       
	}


	public void isFullScreen(boolean b) {
		isFullScreen = b;
	}
	
	private String getString(String label){
		return LanguageSelector.getString(label);
	}
}
