package gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.LayoutStyle;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import popups.LoadingScreen;

import state.State;

import net.miginfocom.swing.MigLayout;

import download.Bubba;

/**
 * This has been taken from my assignment 2 and should be usable with a few 
 * (or perhaps a lot of) changes
 * 
 * This screen is used to download either video or audio 
 * 
 * @author group 27
 */
@SuppressWarnings("serial")
public class DownloadPanel extends JPanel implements ActionListener{

	// My strings are all here so it is easy to make changes
	private String enterURL = "Enter URl to download: ";
	private String urSureOpenSource = "Open source?";
	private String submit = "Download";
	
	private String url;
	private JLabel urlInstr = new JLabel(enterURL);
	private JTextField urlField = new JTextField(20);
	private JLabel isOpen = new JLabel(urSureOpenSource);
	private JRadioButton openY = new JRadioButton("Yes");
	private JRadioButton openN = new JRadioButton("No");
	private JButton submitBtn = new JButton(submit);
	private JButton pauseBtn = new JButton("Pause");
	public static boolean isPaused = false;
	private Bubba shrimp;
	private static LoadingScreen loadScreen;
	private EditorPanel ep;

	/**
	 * This constructor is used to set up the layout of this download panel
	 */
	public DownloadPanel(EditorPanel ep) {
		this.ep = ep;
		setLayout(new MigLayout());
		add(urlInstr, "wrap, span 2");
		add(urlField, "grow, wrap, span 2");
		add(isOpen);
	    ButtonGroup group = new ButtonGroup();
	    group.add(openY);
	    group.add(openN);
		add(openY, "split 2");
		add(openN, "wrap");
		add(submitBtn);
		add(pauseBtn);
		
		TitledBorder border = BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(EtchedBorder.LOWERED, 
				new Color(255, 150, 250, 250), new Color(50, 50, 150, 250)), "Download");
		border.setTitleColor(new Color(255, 150, 250, 250));
		border.setTitleFont(new Font("Sans Serif", Font.BOLD, 24));
		setBorder(border);
		
		State.getState().addColourListeners(this, urlInstr, urlField, isOpen, openY, openN, submitBtn, pauseBtn);
		
		submitBtn.addActionListener(this);

		//this is used to pause the download
		pauseBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (isPaused){
					pauseBtn.setText("Pause");
					isPaused = false;
					submitBtn.setEnabled(true);
					String cmd = "wget -c " + url + " 2>&1 ";
					download(cmd);
				}else{
					pauseBtn.setText("Resume");
					isPaused = true;
					submitBtn.setEnabled(false);
				}
			}	
		});
		
		//ensure pause button is not active at start
		pauseBtn.setEnabled(false);
	}

	/**
	 * When you click the download button it will if a download is already running
	 * in which case it acts as the cancel button and stops the download
	 * If no download is running it will start the download process by first
	 * checking for valid input, then if its valid input it will continue 
	 * to further check input in more depth
	 */
	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (shrimp != null && !shrimp.isDone()) {
			shrimp.cancel(true);
		//	prog.setVisible(false);
			submitBtn.setText("Start Download");
			shrimp = null;
		} else {
			pauseBtn.setEnabled(true);
			url = urlField.getText();
			if (url == null || url.equals("")) {
				JOptionPane.showMessageDialog(submitBtn,
					    "You have not entered a URL",
					    "Error",
					    JOptionPane.WARNING_MESSAGE);
			} else if (!openY.isSelected() ) { 
				JOptionPane.showMessageDialog(submitBtn,
					    "It may be illegal to download this so I have stopped you",
					    "Error",
					    JOptionPane.ERROR_MESSAGE);
			} else {
				checkPreDownload();
			}
		}
	}
	
	/**
	 * This method ensures that you do not already have a file with the same name
	 * as the one you are attempting to download
	 * If acceptable it will begin the download
	 */
	private void checkPreDownload() {
		String cmd1 = "find `basename " + url + "`";
		try {
			ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd1);
			Process process = builder.start();
			String cmd;
			if (process.waitFor() == 0) {
				if(JOptionPane.showOptionDialog(submitBtn, "File already exists. Would you like to try to overwrite?",
								"Error", 0, 0, null, new String[]{"Overwrite", "Cancel"}, null) == 1){
					JOptionPane.showMessageDialog(submitBtn,
									    "File not overwritten",
									    "Error",
									    JOptionPane.ERROR_MESSAGE);
					pauseBtn.setEnabled(false);
					return;
				}
				cmd = "wget -N " + url + " 2>&1 "; //using -N to "overwrite"
			}else{
				cmd = "wget " + url + " 2>&1 ";
			}
			download(cmd);
		}catch (Exception e) {
			JOptionPane.showMessageDialog(submitBtn,
				    "Error attempting",
				    "Error",
				    JOptionPane.ERROR_MESSAGE);
		}
	}
	
	/**
	 * This method starts the download on a background thread
	 * During the download the progress bar will be displayed and updated 
	 * and you will be unable to start another download (the button is disabled)
	 */
	private void download(String cmd) {
		try {
			submitBtn.setText("Cancel");
			loadScreen = new LoadingScreen(ep);
            loadScreen.prepare();
			shrimp = new Bubba(cmd, loadScreen.getProgBar(), submitBtn, pauseBtn);
			shrimp.execute();
			
		} catch (Exception e) {
			JOptionPane.showMessageDialog(submitBtn,
				    "Error attempting download",
				    "Error",
				    JOptionPane.ERROR_MESSAGE);
		}
		
	}
	
	

}

