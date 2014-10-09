package gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import editing.AudioWorker;
import editing.GetAttributes;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import popups.LoadingScreen;
import state.State;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import net.miginfocom.swing.MigLayout;

/**
 * This panel contains all the controls and functionality for audio manipulation
 * @author Mathew and Wesley
 *
 */
@SuppressWarnings("serial")
public class AudioSection extends JPanel{

	private JTextField audioTextField = new JTextField(40);
	private JButton openAudioBtn = new JButton("Open");
	private JButton replaceBtn = new JButton("Replace");
	private JButton overlayBtn = new JButton("Overlay");
	private JButton stripBtn = new JButton("Strip");
	
	// Retrieved on construction
	private EditorPanel editorPanel;
	private Component mediaPlayer;
	
	private static LoadingScreen loadScreen = new LoadingScreen();
	
	/**
	 * The constructor that sets up the GUI and adds listeners to the buttons
	 * @param ep The EditorPanel that this is housed in. This is used to get the name
	 * 		of the currently playing media.
	 * @param mpc The MediaPlayer component used to center the pop up windows
	 */
	public AudioSection(EditorPanel ep, 
			EmbeddedMediaPlayerComponent mpc) {
		
		editorPanel = ep;
		mediaPlayer = mpc;
		
		setLayout(new MigLayout());
		TitledBorder border = BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(EtchedBorder.LOWERED, 
				new Color(250, 150, 150, 250), new Color(250, 150, 50, 250)), "Audio");
		border.setTitleColor(new Color(100,0,0));
		setBorder(border);
		
        State.getState().addBorderListeners(border);
		State.getState().addColourListeners(audioTextField, openAudioBtn, replaceBtn, overlayBtn, stripBtn);
		
		add(audioTextField, "grow, split 2");
		add(openAudioBtn, "wrap");
		add(replaceBtn, "grow, split 3");
		add(overlayBtn, "grow");
		add(stripBtn, "grow");
		
		replaceBtn.setEnabled(false);
		overlayBtn.setEnabled(false);
		
		//prompt the user for an audio file to open
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
        
		//this is used to strip audio from a media file
        stripBtn.addActionListener(new ActionListener(){
        	@Override
        	public void actionPerformed(ActionEvent arg0) {
        		        		
        		String [] buttons = { "Audio", "Video"};
        		int choice = JOptionPane.showOptionDialog(mediaPlayer, 
        				"Would you like to save the audio only, or video only?", "Confirmation"
        				,JOptionPane.INFORMATION_MESSAGE, 0, null, buttons, buttons[0]);
        		if (choice == 0) {
        			audioEdit("stripVideo"); //remove the video
        		} else if (choice == 1) {
        			audioEdit("stripAudio"); //remove the audio
        		}
        	}
        });
        
        //this will replace the audio from a media file with different audio track
        replaceBtn.addActionListener(new ActionListener(){
        	@Override
        	public void actionPerformed(ActionEvent arg0) {
        		audioEdit("replace");
        	}
        });
        
        //this will overlay different audio track onto the media file
        overlayBtn.addActionListener(new ActionListener(){
        	@Override
        	public void actionPerformed(ActionEvent arg0) {
        		audioEdit("overlay");
        	}
        });
	}
	
	/**
	 * This method is used to let the user choose the save location and then 
	 * start the audioWorker performing the audio task (corresponding to the option parameter)
	 * in the background
	 * @param option String used to distinguish between different audio worker tasks
	 */
	public void audioEdit(String option) {
		loadScreen = new LoadingScreen();
		if (editorPanel.isFileType(editorPanel.getMediaName(), "Audio")){
			JOptionPane.showMessageDialog(mediaPlayer, "Please enter a valid video file name.");
			return;
		}
    	if (option.startsWith("strip") || correctFileType(audioTextField.getText(), "Audio")){
    		if (editorPanel.isMediaFile(editorPanel.getMediaName())){
				final JFileChooser fc = new JFileChooser();
		        fc.showSaveDialog(fc);
		        if (fc.getSelectedFile() != null){
		        	int dur = GetAttributes.getDuration(editorPanel.getMediaName());
		        	int fps = GetAttributes.getFPS(editorPanel.getMediaName());
		            String audioFile = fc.getSelectedFile().getAbsolutePath().toString();
		            loadScreen.prepare();
			    	AudioWorker worker = new AudioWorker(editorPanel.getMediaName(), audioTextField.getText(),
			    							option, audioFile, loadScreen.getProgBar(), dur, fps);
			    	worker.execute();
		        }
    		}
    		else
    			JOptionPane.showMessageDialog(mediaPlayer, "Please open a valid media file.");
    	}
    	else
			JOptionPane.showMessageDialog(mediaPlayer, "Please enter a valid audio file name.");
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
			JOptionPane.showMessageDialog(null, "Error occurred.");
		} catch (InterruptedException e) {
			JOptionPane.showMessageDialog(null, "Error occurred.");
		}
		return false;
	}	
	/**
	 *  Used by the ProjectSettings to allow them to get the audio file name
	 * @return The string which comes straight from the audio text field
	 */
	public String getAudioString() {
		return audioTextField.getText();
	}
	
	/**
	 *  Used by the ProjectSettings to allow them to set the audio file name
	 */
	public void setAudioString(String audioString) {
		audioTextField.setText(audioString);
	}
	
	/**
	 * This method checks if the loading screen is cancelled
	 * @return true if cancelled, false otherwise
	 */
	public static boolean loadCancelled(){
		return loadScreen.isClosed;
	}

	public void enableButtons() {
		if (!audioTextField.getText().equals("")){
			replaceBtn.setEnabled(true);
			overlayBtn.setEnabled(true);
		}
	}
}
