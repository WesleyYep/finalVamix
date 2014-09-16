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
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;

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
	private JButton playBtn = new JButton("Play");
	private JButton stopBtn = new JButton("Stop");
	private JButton openBtn = new JButton("Open");
	private JButton openAudioBtn = new JButton("Open Audio");
	private JLabel audioTitle = new JLabel("Audio");
	private JButton replaceBtn = new JButton("Replace");
	private JButton overlayBtn = new JButton("Overlay");
	private JButton stripBtn = new JButton("Strip");
	private JTextField fileTextField = new JTextField(40);
	private JTextField audioTextField = new JTextField(40);
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
    	 * When the play button is clicked the video should be started and the play button
    	 * turns into a pause button
    	 * Also the stop button is activated when video is playing and will get rid of the 
    	 * video so that another video can be loaded in
    	 */
        playBtn.addActionListener(new ActionListener(){
        	@Override
        	public void actionPerformed(ActionEvent arg0) {
    			// If the media player is already playing audio then the button click 
    			// should pause the video
    			if (mediaPlayerComponent.getMediaPlayer().isPlaying()) {
    				mediaPlayerComponent.getMediaPlayer().pause();
    				playBtn.setText("Play");
    			// If the video is already pause then the button click will continue to play it
    			} else if (mediaPlayerComponent.getMediaPlayer().isPlayable()) {
    				mediaPlayerComponent.getMediaPlayer().play();
    				playBtn.setText("Pause");
    			// Otherwise we will ask the user for the file they want to play and 
    			// start playing that
    			} else {
    				playMusic();
    				playBtn.setText("Pause");
    			}
    			stopBtn.setEnabled(true);
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
			}      	
        });
    	//When the open button is clicked the file chooser appears
        openBtn.addActionListener(new ActionListener(){
        	@Override
        	public void actionPerformed(ActionEvent arg0) {
        		final JFileChooser fc = new JFileChooser();
                fc.showOpenDialog(fc);
               fileTextField.setText(fc.getSelectedFile().getAbsolutePath().toString());
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
        // This is temporary while i figure out how to properly use MigLayout
        // Note: the sizes should not be hard coded

        add(title, "cell 0 0, align center, wrap");
        add(fileTextField, "cell 0 1, span 9");
        add(openBtn, "cell 0 1");
        add(mediaPlayerComponent, "cell 0 2 2 1, width :800:, height :400:");
        add(playBtn, "cell 0 3, growx");
        add(stopBtn, "cell 1 3, growx");
        
        add(audioTitle, "cell 0 4, growx");
        add(audioTextField, "cell 0 5");
        add(openAudioBtn, "cell 0 5");
        add(replaceBtn, "cell 0 6");
        add(overlayBtn, "cell 0 6");
        add(stripBtn, "cell 0 6");
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
