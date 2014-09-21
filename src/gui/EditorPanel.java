package gui;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.Timer;
import javax.swing.JSpinner.DateEditor;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import editing.AudioWorker;
import editing.GetAttributes;
import editing.TextWorker;
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
	private JTextField textArea = new JTextField(40);
	private JProgressBar audioProgBar = new JProgressBar();
	private JProgressBar textProgBar = new JProgressBar();
	private final Timer sliderTimer = new Timer(100, null);
	private final Timer videoMovementTimer = new Timer(100, null);
	private JLabel textTitle = new JLabel("Text");
	private JComboBox<String> titleOrCredits;
	private JComboBox<String> fontOption;
	private JComboBox<String> colourOption;
	private JSpinner fontSizeSpinner = new JSpinner();
	private JSpinner timeForTextSpinner = new JSpinner();
	private JButton addTextBtn = new JButton("Add");
	private String titleText;
	private String creditsText;

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
        
        openAudioBtn.addActionListener(new ActionListener(){
        	@Override
        	public void actionPerformed(ActionEvent arg0) {
        		final JFileChooser fc = new JFileChooser();
                fc.showOpenDialog(fc);
                if (fc.getSelectedFile() != null){
                    String fileName = fc.getSelectedFile().getAbsolutePath().toString();
                	audioTextField.setText(fileName);
                	replaceBtn.setEnabled(true);
                	overlayBtn.setEnabled(true);
                }
        	}
        });
        
        stripBtn.addActionListener(new ActionListener(){
        	@Override
        	public void actionPerformed(ActionEvent arg0) {
        		audioProgBar.setValue(0);
        		String [] buttons = { "Audio", "Video"};
        		int choice = JOptionPane.showOptionDialog(mediaPlayerComponent, "Would you like to save the audio only, or video only?", "Confirmation"
        				,JOptionPane.INFORMATION_MESSAGE, 0, null, buttons, buttons[0]);
           		if (choice == 0)
        			audioEdit("stripVideo"); //remove the video
        		else if (choice == 1)
        			audioEdit("stripAudio"); //remove the audio
        	}
        });
        
        replaceBtn.addActionListener(new ActionListener(){
        	@Override
        	public void actionPerformed(ActionEvent arg0) {
        		audioProgBar.setValue(0);
        		audioEdit("replace");
        	}
        });
        

        overlayBtn.addActionListener(new ActionListener(){
        	@Override
        	public void actionPerformed(ActionEvent arg0) {
        		audioProgBar.setValue(0);
        		audioEdit("overlay");
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
        
        addTextBtn.addActionListener(new ActionListener(){
        	@Override
        	public void actionPerformed(ActionEvent arg0) {
				final JFileChooser fc = new JFileChooser();
		        fc.showSaveDialog(fc);
		        if (fc.getSelectedFile() != null){
		        	int dur = GetAttributes.getDuration(fileTextField.getText());
		        	int fps = GetAttributes.getFPS(fileTextField.getText());
		            String outputFile = fc.getSelectedFile().getAbsolutePath().toString();
		            String fontPath = getFontPath(fontOption.getSelectedItem().toString());
		            String time = new DateEditor(timeForTextSpinner , "yy:mm:ss").getFormat().format(timeForTextSpinner.getValue());
		            String[] timeArray = time.split(":");
		            int timeInSecs = 60 * 60 *Integer.parseInt(timeArray[0]) + 60 * Integer.parseInt(timeArray[1]) + Integer.parseInt(timeArray[2]);
		            String timeFunction, metadata;
		            if (titleOrCredits.getSelectedItem().toString().equalsIgnoreCase("Title")){
		            	timeFunction = "lt(t," + timeInSecs + ")";
		            	metadata = "title=\"" + textArea.getText() + "\"";
		            }
		            else{
		            	timeFunction = "gt(t," + (dur - timeInSecs) + ")";
		            	metadata = "comment=\"" + textArea.getText() + "\"";
		            }
		            String cmd = "avconv -i " + fileTextField.getText() + " -metadata " + metadata + " -vf \"drawtext=fontfile='" + fontPath + "':text='" + textArea.getText() +
		            			"':x=0:y=0:fontsize=" + fontSizeSpinner.getValue() + ":fontcolor=" + colourOption.getSelectedItem() + 
		            			":draw='" + timeFunction + "'\" -strict experimental -f mp4 -v debug " + outputFile;
        			TextWorker worker = new TextWorker(cmd, textProgBar, dur, fps);
		            worker.execute();
		        }
        	}
        });

        titleOrCredits = new JComboBox<String>(new String[]{"Title", "Credits"});
        titleOrCredits.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (titleOrCredits.getSelectedItem().toString().equalsIgnoreCase("Title"))
	            	textArea.setText(titleText);
				else if (titleOrCredits.getSelectedItem().toString().equalsIgnoreCase("Credits"))
	            	textArea.setText(creditsText);
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
		
        fontOption = new JComboBox<String>(new String[]{"DejaVuSans", "Arial", "Comic Sans", "Times New Roman"});
        colourOption = new JComboBox<String>(new String[]{"Red", "Orange", "Yellow", "Green", "Blue"});
        fontSizeSpinner.setEditor(new JSpinner.NumberEditor(fontSizeSpinner , "00"));
        fontSizeSpinner.setModel(new SpinnerNumberModel(0, 0, 64, 1));
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, 100); //to allow it to go up to 99, rather than stop at 24
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
        SpinnerDateModel timeModel = new SpinnerDateModel();
		timeModel.setValue(calendar.getTime());
		timeForTextSpinner.setModel(timeModel);
		timeForTextSpinner.setEditor(new DateEditor(timeForTextSpinner , "yy:mm:ss"));
		
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
        replaceBtn.setEnabled(false);
        overlayBtn.setEnabled(false);
        add(stripBtn, "wrap");
        add(audioProgBar, "grow, wrap");
        //text components
        add(textTitle, "wrap");
        add(textArea, "split 2");
        add(titleOrCredits, "wrap");
        add(new JLabel("Font: "), "split 9");
        add(fontOption);
        add(new JLabel("Colour: "));
        add(colourOption);
        add(new JLabel("Size: "));
        add(fontSizeSpinner);
        add(new JLabel("Duration: "));
        add(timeForTextSpinner);
        add(addTextBtn, "wrap");
        add(textProgBar, "grow, wrap");
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
	
	// TODO make forward and rewind work
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
	
	public void audioEdit(String option) {
    	if (option.startsWith("strip") || correctFileType(audioTextField.getText(), "Audio")){
    		if (isMediaFile(fileTextField.getText())){
				final JFileChooser fc = new JFileChooser();
		        fc.showSaveDialog(fc);
		        if (fc.getSelectedFile() != null){
		        	int dur = GetAttributes.getDuration(fileTextField.getText());
		        	int fps = GetAttributes.getFPS(fileTextField.getText());
		            String audioFile = fc.getSelectedFile().getAbsolutePath().toString();
			    	AudioWorker worker = new AudioWorker(fileTextField.getText(), audioTextField.getText(),
			    							option, audioFile, audioProgBar, dur, fps);
			    	worker.execute();
		        }
    		}
    		else
    			JOptionPane.showMessageDialog(mediaPlayerComponent, "Please open a valid media file.");
    	}
    	else
			JOptionPane.showMessageDialog(mediaPlayerComponent, "Please enter a valid audio file name.");
	}
	
	private boolean isMediaFile(String input){
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
		
	private String getFontPath(String fontName) {
		//use the locate command in linux
		String cmd = "locate " + fontName + ".ttf";
		ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd);
		try {
			Process process = builder.start();
			process.waitFor();
			InputStream stdout = process.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(stdout));
			return (br.readLine());		
		} catch (IOException e) {
			//
		} catch (InterruptedException e) {
			//
		}
		return "";
	}	
}
