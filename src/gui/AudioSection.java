package gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import editing.AudioWorker;
import editing.CheckFileExists;
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
import state.LanguageSelector;
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

	private JTextField audioTextField = new JTextField(15);
	private JButton openAudioBtn = new JButton(getString("open"));
	private JButton replaceBtn = new JButton(getString("replace"));
	private JButton overlayBtn = new JButton(getString("overlay"));
	private JButton stripBtn = new JButton(getString("strip"));
	// Retrieved on construction
	private Vamix vamix;
	private Component mediaPlayer;
	private static LoadingScreen loadScreen;
	
	/**
	 * The constructor that sets up the GUI and adds listeners to the buttons
	 * @param ep The EditorPanel that this is housed in. This is used to get the name
	 * 		of the currently playing media.
	 * @param mpc The MediaPlayer component used to center the pop up windows
	 */
	public AudioSection(Vamix v, 
			EmbeddedMediaPlayerComponent mpc) {
		
		vamix = v;
		mediaPlayer = mpc;
		
		setLayout(new MigLayout("","[][][]", "[]"));
		TitledBorder border = BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(EtchedBorder.LOWERED, 
				new Color(250, 150, 150, 250), new Color(250, 150, 50, 250)), getString("audio"));
		border.setTitleColor(new Color(250, 150, 150, 250));
		border.setTitleFont(new Font("Sans Serif", Font.BOLD, 24));
		setBorder(border);
		//add all the gui components as colour listeners
		State.getState().addColourListeners(audioTextField, openAudioBtn, replaceBtn, overlayBtn, stripBtn);
		
		add(audioTextField, "cell 0 0 2 1");
		add(openAudioBtn, "cell 2 0 1 1, grow");
		add(replaceBtn, "cell 0 1 1 1");
		add(overlayBtn, "cell 1 1 1 1, grow");
		add(stripBtn, "cell 2 1 1 1, grow");
		
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
        		        		
        		String [] buttons = { getString("audio"), getString("video")};
        		int choice = JOptionPane.showOptionDialog(mediaPlayer, 
        				getString("audioOrVideo"), getString("confirmation")
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
		if (vamix.isFileType(vamix.getMediaName(), "Audio")){
			JOptionPane.showMessageDialog(mediaPlayer, getString("validVideo"));
			return;
		}
    	if (option.startsWith("strip") || correctFileType(audioTextField.getText(), "Audio")){
    		if (vamix.isMediaFile(vamix.getMediaName())){
				final JFileChooser fc = new JFileChooser();
		        fc.showSaveDialog(fc);
		        if (fc.getSelectedFile() != null){
		        	if (CheckFileExists.check(fc.getSelectedFile().getAbsolutePath().toString())){
						if (JOptionPane.showConfirmDialog((Component) null, getString("fileExists"),
						        "alert", JOptionPane.OK_CANCEL_OPTION) != 0){
							JOptionPane.showMessageDialog(null, getString("notOverwritten"));
							return;
						}
		        	}
		        	int dur = GetAttributes.getDuration(vamix.getMediaName());
		        	int frames = GetAttributes.getFrames(vamix.getMediaName());
		            String audioFile = fc.getSelectedFile().getAbsolutePath().toString();
		            AudioWorker worker = null;
			    	loadScreen = new LoadingScreen(vamix);
		            loadScreen.prepare();
			    	worker = new AudioWorker(vamix.getMediaName(), audioTextField.getText(),
			    							option, audioFile, loadScreen.getProgBar(), dur, frames, loadScreen);
			    	loadScreen.setWorker(worker);
			    	worker.execute();
		        }
    		}
    		else
    			JOptionPane.showMessageDialog(mediaPlayer, getString("validMediaFile"));
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
			JOptionPane.showMessageDialog(null, getString("errorOccurred"));
		} catch (InterruptedException e) {
			JOptionPane.showMessageDialog(null, getString("errorOccurred"));
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
	 * Used to enable the replace and overlay button once something is inputted into audio file field
	 */
	public void enableButtons() {
		if (!audioTextField.getText().equals("")){
			replaceBtn.setEnabled(true);
			overlayBtn.setEnabled(true);
		}
	}
	
	private String getString(String label){
		return LanguageSelector.getString(label);
	}
}
