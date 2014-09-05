package gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
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
	
	EditorPanel () {
		this.setLayout(new BorderLayout());
		
		title.setAlignmentX(Component.CENTER_ALIGNMENT);
		title.setFont (new Font("Serif", Font.BOLD, 48));
		add(title, BorderLayout.NORTH);

		// This is the video player
        mediaPlayerComponent = new EmbeddedMediaPlayerComponent();

        
        playBtn.addActionListener(this);
        add(playBtn, BorderLayout.SOUTH);
        add(mediaPlayerComponent, BorderLayout.CENTER);
       
        
        
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
	 * When the button is clicked the video should be started
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		playMusic();
	}
		
}
