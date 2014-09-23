package Popups;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

@SuppressWarnings("serial")
public class StripAudio extends JFrame {
	

	private JLabel topMsg = new JLabel("Strip the audio from your video");
	private JTextField audioTextField = new JTextField(40);
	private JButton openAudioBtn = new JButton("Save location");
	private JProgressBar audioProgBar = new JProgressBar();
	
	private JButton startBtn = new JButton("Start");
	private JButton cancelBtn = new JButton("Cancel");

	
	
	
	public StripAudio(String text, Boolean isAudio) {
		super(text);
		setSize(400,200);
		this.setLocation(500, 250);
		setMinimumSize(new Dimension(400,200));
		setLayout(new MigLayout("","","10 [] 10 [] 20 []"));
		add(topMsg, "center, wrap");
		add(audioTextField, "growy, split 2");
		add(openAudioBtn, "wrap");
		add(startBtn, "growx, split 2");
		add(cancelBtn, "growx, wrap");
		add(audioProgBar, "growx");
		
		if (!isAudio) {
			topMsg.setText("Keep video with no audio");
		}
		
		
		cancelBtn.addActionListener(new ActionListener(){
        	@Override
        	public void actionPerformed(ActionEvent arg0) {
        		setVisible(false);
        	}
        });
	}
}
