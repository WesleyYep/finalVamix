package gui;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;

/**
 * This screen is used for all your video editing needs
 * 
 * @author Mathew and Wesley
 */
public class EditorPanel extends JPanel implements ActionListener{

	/** An unused ID here only to get rid of the warning */
	private static final long serialVersionUID = 1075006905710700282L;

	private JLabel title = new JLabel ("Lets get editing");
	private final EmbeddedMediaPlayerComponent mediaPlayerComponent;
	private JButton playBtn = new JButton("Play");
	private JButton stopBtn = new JButton("Stop");
	private JButton forwardBtn = new JButton("Fast Forward");
	private JButton backwardBtn = new JButton("Rewind");
	
	
	private MigLayout myLayout = new MigLayout("",
			"10 [] 10 [] 0 []",
			"5 [] 0 [] 10 []");
	
	EditorPanel () {
		this.setLayout(myLayout);
		
		//title.setAlignmentX(Component.CENTER_ALIGNMENT);
		title.setFont (new Font("Serif", Font.BOLD, 48));
		

		// This is the video player
        mediaPlayerComponent = new EmbeddedMediaPlayerComponent();

        playBtn.addActionListener(this);
        stopBtn.addActionListener(this);
        stopBtn.setEnabled(false);
        
        add(title, "wrap, center");
        // This media  player has massive preferred size in 
        // order to force it to fill the screen
        add(mediaPlayerComponent, "grow, wrap, height 200:10000:, width 600:10000:");
        add(backwardBtn, "split 4, grow");
        add(playBtn, "grow");
        add(stopBtn, "grow");
        add(forwardBtn, "grow");
        
        
	}
	
	private void playMusic() {
		// The user chooses the file they want to play only after clicking
		// the play button (this will be changed as we get further)
		final JFileChooser fc = new JFileChooser();
        fc.showOpenDialog(this);
        String fileLocation = fc.getSelectedFile().getAbsolutePath().toString();
        
        mediaPlayerComponent.getMediaPlayer().playMedia(fileLocation);
	}

	/** This method is to be called by the mainframe when it is exiting so that 
	 * the video is stopped (otherwise you will continue to hear the audio playing 
	 */
	public void stop() {
		mediaPlayerComponent.getMediaPlayer().stop();
	}
	
	/**
	 * When the play button is clicked the video should be started and the play button
	 * turns into a pause button
	 * Also the stop button is activated when video is playing and will get rid of the 
	 * video so that another video can be loaded in
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (arg0.getSource().equals(playBtn)) {
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
		} else if (arg0.getSource().equals(stopBtn)) {
			if (mediaPlayerComponent.getMediaPlayer().isPlaying()) {
				mediaPlayerComponent.getMediaPlayer().stop();
				playBtn.setText("Play");
			} else if (mediaPlayerComponent.getMediaPlayer().isPlayable()) {
				mediaPlayerComponent.getMediaPlayer().stop();
			} 
			stopBtn.setEnabled(false);
		}
		
		
		
		
	}
		
}
