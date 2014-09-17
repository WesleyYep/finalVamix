package gui;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.miginfocom.swing.MigLayout;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import uk.co.caprica.vlcj.player.MediaMeta;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;

/**
 * This screen is used for all your video editing needs
 * 
 * @author Mathew and Wesley
 */
public class EditorPanel extends JPanel{

	/** An unused ID here only to get rid of the warning */
	private static final long serialVersionUID = 1075006905710700282L;

	private JLabel title = new JLabel ("Lets get editing");
	private final EmbeddedMediaPlayerComponent mediaPlayerComponent;
	private JSlider vidPosSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 0);
	private JButton playBtn = new JButton("Play");
	private JButton stopBtn = new JButton("Stop");
	private JButton forwardBtn = new JButton("Fast Forward");
	private JButton backwardBtn = new JButton("Rewind");
	private JButton openBtn = new JButton("Open");
	private JButton openAudioBtn = new JButton("Open Audio");
	private JLabel audioTitle = new JLabel("Audio");
	private JButton replaceBtn = new JButton("Replace");
	private JButton overlayBtn = new JButton("Overlay");
	private JButton stripBtn = new JButton("Strip");
	private JTextField fileTextField = new JTextField(40);
	private JTextField audioTextField = new JTextField(40);

	private final Timer timer = new Timer(100, null);

	private MigLayout myLayout = new MigLayout("",
			"10 [] 10 [] 0 []",
			"5 [] 0 [] 10 []");

	EditorPanel () {
		this.setLayout(myLayout);
		
		//title.setAlignmentX(Component.CENTER_ALIGNMENT);
		title.setFont (new Font("Serif", Font.BOLD, 48));
		

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
    			// If the media player is already playing audio/video then the button click 
    			// should pause it
    			if (mediaPlayerComponent.getMediaPlayer().isPlaying()) {
    				mediaPlayerComponent.getMediaPlayer().pause();
    				playBtn.setText("Play");
    				timer.stop();
    			// If the video is already pause then the button click 
    			// will continue to play it
    			} else if (mediaPlayerComponent.getMediaPlayer().isPlayable()) {
    				mediaPlayerComponent.getMediaPlayer().play();
    				playBtn.setText("Pause");
    				timer.start();
    			// Otherwise we will ask the user for the file they want to play and 
    			// start playing that
    			} else {
    				// If file already selected just play that
    				if (!fileTextField.getText().equals("")) {
	    				playMusic();
	    				playBtn.setText("Pause");
	    				timer.start();
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
				if (mediaPlayerComponent.getMediaPlayer().isPlaying()) {
					mediaPlayerComponent.getMediaPlayer().stop();
					playBtn.setText("Play");
				} else if (mediaPlayerComponent.getMediaPlayer().isPlayable()) {
					mediaPlayerComponent.getMediaPlayer().stop();
				} 
				stopBtn.setEnabled(false);
				timer.stop();
			}      	
        });
    	//When the open button is clicked the file chooser appears
        openBtn.addActionListener(new ActionListener(){
        	@Override
        	public void actionPerformed(ActionEvent arg0) {
        		chooseFile();
        	}
        });
        
        openAudioBtn.addActionListener(new ActionListener(){
        	@Override
        	public void actionPerformed(ActionEvent arg0) {
        		final JFileChooser fc = new JFileChooser();
                fc.showOpenDialog(fc);
               audioTextField.setText(fc.getSelectedFile().getAbsolutePath().toString());
        	}
        });
        
        stripBtn.addActionListener(new ActionListener(){
        	@Override
        	public void actionPerformed(ActionEvent arg0) {
        		audioEdit("strip");
        	}
        });
        
        replaceBtn.addActionListener(new ActionListener(){
        	@Override
        	public void actionPerformed(ActionEvent arg0) {
        		audioEdit("replace");
        	}
        });
        
        stopBtn.setEnabled(false);
        
        vidPosSlider.setMajorTickSpacing(10);
		vidPosSlider.setMinorTickSpacing(1);
		vidPosSlider.setPaintTicks(true);
		vidPosSlider.addChangeListener(sliderChangeListener);
		
		timer.addActionListener(timerListener);
		
        
        add(title, "wrap, center");
        //moved the open file stuff to the top for now
        add(fileTextField, "split 2, grow");
        add(openBtn, "wrap");
        // This media  player has massive preferred size in 
        // order to force it to fill the screen
        add(mediaPlayerComponent, "grow, wrap, height 200:10000:, width 600:10000:");
        add(vidPosSlider, "wrap, grow");
        add(backwardBtn, "split 4, grow");
        add(playBtn, "grow");
        add(stopBtn, "grow");
        add(forwardBtn, "grow, wrap");
        
        //audio components
        add(audioTitle, "wrap");
        add(audioTextField, "split 2");
        add(openAudioBtn, "wrap");
        add(replaceBtn, "split 3");
        add(overlayBtn);
        add(stripBtn);

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
	
	/** 
	 * Used to choose a media file to play
	 * Used from both open and play button
	 */
	private void chooseFile() {
		final JFileChooser fc = new JFileChooser();
        fc.showOpenDialog(fc);
        fileTextField.setText(fc.getSelectedFile().getAbsolutePath().toString());
	}
	
	private void playMusic() {
		mediaPlayerComponent.getMediaPlayer().playMedia(fileTextField.getText());
	}

	/** This method is to be called by the mainframe when it is exiting so that 
	 * the video is stopped (otherwise you will continue to hear the audio playing 
	 */
	public void stop() {
		mediaPlayerComponent.getMediaPlayer().stop();
	}
	
	public void audioEdit(String option) {
		final JFileChooser fc = new JFileChooser();
        fc.showSaveDialog(fc);
        String file = fc.getSelectedFile().getAbsolutePath().toString();
    	String cmd;
    	String message;
    	if (option.equals("strip")){
    		cmd = "avconv -i " + fileTextField.getText() + " -an -c:v copy " + file;
    		message = "Audio track removed";
    	}
    	else{
    		cmd = "avconv -i " +  fileTextField.getText() + " -i " + audioTextField.getText() +
    				" -map 0:0 -map 1:0 -codec copy " + file;
    		message = "Audio track replaced";
    	}
    	System.out.println(cmd);
        try {
        	ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd);
			Process process = builder.start();
			process.waitFor();
			if (process.exitValue()==0)
				JOptionPane.showMessageDialog(null, message, "Done", JOptionPane.DEFAULT_OPTION);
			else
				JOptionPane.showMessageDialog(null, "Error occurred.", "Error", JOptionPane.WARNING_MESSAGE);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
		
}
