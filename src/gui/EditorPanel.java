package gui;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
//	private JButton openAudioBtn = new JButton("Open Audio");
//	private JLabel audioTitle = new JLabel("Audio");
//	private JButton replaceBtn = new JButton("Replace");
//	private JButton overlayBtn = new JButton("Overlay");
//	private JButton stripBtn = new CustomButton("Strip");
	private JTextField fileTextField = new JTextField(40);
//	private JTextField audioTextField = new JTextField(40);
//	private JTextField textTextField = new JTextField(40);
//	private JProgressBar audioProgBar = new JProgressBar();
//	private JProgressBar textProgBar = new JProgressBar();
	private final Timer sliderTimer = new Timer(100, null);
	private final Timer videoMovementTimer = new Timer(100, null);
//	private JLabel textTitle = new JLabel("Text");
//	private JButton addTextBtn = new JButton("Add");
	// TODO get rid of some stuff here
//	private JTextField audioTextField = new JTextField(40);
	private JTextField textArea = new JTextField(40);
//	private JLabel textTitle = new JLabel("Text");
	private JComboBox<String> titleOrCredits;
//	private JComboBox<String> fontOption;
//	private JComboBox<String> colourOption;
//	private JSpinner fontSizeSpinner = new JSpinner();
//	private JSpinner timeForTextSpinner = new JSpinner();
//	private JButton addTextBtn = new JButton("Add");
	private String titleText;
	private String creditsText;

	private MigLayout myLayout = new MigLayout("",
			"10 [] 10 [] 0 []",
			"5 [] 5 []");

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
        
//        openAudioBtn.addActionListener(new ActionListener(){
//        	@Override
//        	public void actionPerformed(ActionEvent arg0) {
//        		final JFileChooser fc = new JFileChooser();
//                fc.showOpenDialog(fc);
//                if (fc.getSelectedFile() != null){
//                    String fileName = fc.getSelectedFile().getAbsolutePath().toString();
//                	audioTextField.setText(fileName);
//                	replaceBtn.setEnabled(true);
//                	overlayBtn.setEnabled(true);
//                }
//        	}
//        });
//        
//        stripBtn.addActionListener(new ActionListener(){
//        	@Override
//        	public void actionPerformed(ActionEvent arg0) {
//        		// TODO
//        		JFrame stripPopup = new StripAudio("Strip options");
//        		stripPopup.setVisible(true);
//        		
////        		String [] buttons = { "Audio", "Video"};
////        		int choice = JOptionPane.showOptionDialog(mediaPlayerComponent, 
////        				"Would you like to save the audio only, or video only?", "Confirmation"
////        				,JOptionPane.INFORMATION_MESSAGE, 0, null, buttons, buttons[0]);
////           		if (choice == 0)
////        			audioEdit("stripVideo"); //remove the video
////        		else if (choice == 1)
////        			audioEdit("stripAudio"); //remove the audio
//        	}
//        });
//        
//        replaceBtn.addActionListener(new ActionListener(){
//        	@Override
//        	public void actionPerformed(ActionEvent arg0) {
//        		audioEdit("replace");
//        	}
//        });
//        
//
//        overlayBtn.addActionListener(new ActionListener(){
//        	@Override
//        	public void actionPerformed(ActionEvent arg0) {
//        		audioEdit("overlay");
//        	}
//        });
        
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
        
//        addTextBtn.addActionListener(new ActionListener(){
//        	@Override
//        	public void actionPerformed(ActionEvent arg0) {
//				final JFileChooser fc = new JFileChooser();
//		        fc.showSaveDialog(fc);
//		        if (fc.getSelectedFile() != null){
//		            String outputFile = fc.getSelectedFile().getAbsolutePath().toString();
//		            String cmd = "avconv -i " + fileTextField.getText() + " -vf \"drawtext="
//		            		+ "fontfile='/usr/share/fonts/truetype/ttf-dejavu/DejaVuSans.ttf':text='" +
//        						textTextField.getText() + "':x=0:y=0:fontsize=24:fontcolor=red\" "
//        								+ "-strict experimental -f mp4 -v debug " + outputFile;
//        			TextWorker worker = new TextWorker(cmd, textProgBar);
//		            worker.execute();
//		        }
//        	}
//        });
        
        stopBtn.setEnabled(false);
        playBtn.setIcon(new ImageIcon("assets/player_play.png"));
        stopBtn.setIcon(new ImageIcon("assets/agt_action_fail1.png"));
        forwardBtn.setIcon(new ImageIcon("assets/player_fwd.png"));
        backwardBtn.setIcon(new ImageIcon("assets/player_start.png"));
        
        vidPosSlider.setMajorTickSpacing(10);
		vidPosSlider.setMinorTickSpacing(1);
		vidPosSlider.setPaintTicks(true);
		vidPosSlider.addChangeListener(sliderChangeListener);
		
		sliderTimer.addActionListener(timerListener);
		videoMovementTimer.addActionListener(secondTimerListener);
		videoMovementTimer.start();
		mediaPlayerComponent.setFont(new Font("Arial", 24, Font.BOLD));
		
//        fontOption = new JComboBox<String>(new String[]{"DejaVuSans", "Arial", "Comic Sans", "Times New Roman"});
//        colourOption = new JComboBox<String>(new String[]{"Red", "Orange", "Yellow", "Green", "Blue"});
//        fontSizeSpinner.setEditor(new JSpinner.NumberEditor(fontSizeSpinner , "00"));
//        fontSizeSpinner.setModel(new SpinnerNumberModel(0, 0, 64, 1));
//		Calendar calendar = Calendar.getInstance();
//		calendar.set(Calendar.YEAR, 100); //to allow it to go up to 99, rather than stop at 24
//		calendar.set(Calendar.MINUTE, 0);
//		calendar.set(Calendar.SECOND, 0);
//        SpinnerDateModel timeModel = new SpinnerDateModel();
//		timeModel.setValue(calendar.getTime());
//		timeForTextSpinner.setModel(timeModel);
//		timeForTextSpinner.setEditor(new DateEditor(timeForTextSpinner , "yy:mm:ss"));
		
//        add(title, "wrap, center");
        //moved the open file stuff to the top for now
        add(fileTextField, "split 2, grow");
        add(openBtn, "wrap");
        // This media  player has massive preferred size in 
        // order to force it to fill the screen
        add(mediaPlayerComponent, "grow, wrap, height 200:10000:, width 600:10000:");
        add(vidPosSlider, "wrap, grow");
        
        JPanel mainControlPanel = new JPanel();
        mainControlPanel.add(backwardBtn, "split 4");
        mainControlPanel.add(playBtn);
        mainControlPanel.add(stopBtn);
        mainControlPanel.add(forwardBtn);
        
        add(new AudioSection(this, mediaPlayerComponent), "split 3");
        add(mainControlPanel, "grow, center");
        add(new TextSection(this), "right, wrap");
        
        //audio components
//        add(audioTitle, "wrap");
//        add(audioTextField, "split 2");
//        add(openAudioBtn, "wrap");
//        add(replaceBtn, "split 3");
//        add(overlayBtn);
//        replaceBtn.setEnabled(false);
//        overlayBtn.setEnabled(false);
//        add(stripBtn, "wrap");
//        add(audioProgBar, "grow, wrap");
        
//        //text components
//        add(textTitle, "wrap");
//        add(textArea, "split 2");
//        add(titleOrCredits, "wrap");
//        add(new JLabel("Font: "), "split 9");
//        add(fontOption);
//        add(new JLabel("Colour: "));
//        add(colourOption);
//        add(new JLabel("Size: "));
//        add(fontSizeSpinner);
//        add(new JLabel("Duration: "));
//        add(timeForTextSpinner);
//        add(addTextBtn, "wrap");
//        add(textProgBar, "grow, wrap");
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
	
//	public void audioEdit(String option) {
//    	if (option.startsWith("strip") || correctFileType(audioTextField.getText(), "Audio")){
//    		if (isMediaFile(fileTextField.getText())){
//				final JFileChooser fc = new JFileChooser();
//		        fc.showSaveDialog(fc);
//		        if (fc.getSelectedFile() != null){
//		        	int dur = GetAttributes.getDuration(fileTextField.getText());
//		        	int fps = GetAttributes.getFPS(fileTextField.getText());
//		            String audioFile = fc.getSelectedFile().getAbsolutePath().toString();
//			    	AudioWorker worker = new AudioWorker(fileTextField.getText(), audioTextField.getText(),
//			    							option, audioFile, audioProgBar, dur, fps);
//			    	worker.execute();
//		        }
//    		}
//    		else
//    			JOptionPane.showMessageDialog(mediaPlayerComponent, "Please open a valid media file.");
//    	}
//    	else
//			JOptionPane.showMessageDialog(mediaPlayerComponent, "Please enter a valid audio file name.");
//	}
	
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
	
	
	
	// TODO Matt messing with the GUI
	public String getMediaName() {
		return fileTextField.getText();
	}
	
		
//	private String getFontPath(String fontName) {
//		//use the locate command in linux
//		String cmd = "locate " + fontName + ".ttf";
//		ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd);
//		try {
//			Process process = builder.start();
//			process.waitFor();
//			InputStream stdout = process.getInputStream();
//			BufferedReader br = new BufferedReader(new InputStreamReader(stdout));
//			return (br.readLine());		
//		} catch (IOException e) {
//			//
//		} catch (InterruptedException e) {
//			//
//		}
//		return "";
//	}	
}
