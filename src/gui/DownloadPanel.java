package gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import popups.LoadingScreen;
import state.LanguageSelector;
import state.State;
import net.miginfocom.swing.MigLayout;
import download.DownloadWorker;

/**
 * 
 * This section is used to download either video or audio. The download can be paused at any time.
 * It is incorporated into the main window
 * 
 * @author matt and wesley
 */
@SuppressWarnings("serial")
public class DownloadPanel extends JPanel implements ActionListener{

	private String url;
	private JLabel urlInstr = new JLabel(getString("enterUrl"));
	private JTextField urlField = new JTextField(20);
	private JLabel isOpen = new JLabel(getString("urSureOpenSource"));
	private JRadioButton openY = new JRadioButton(getString("yes"));
	private JRadioButton openN = new JRadioButton(getString("no"));
	private JButton submitBtn = new JButton(getString("startDownload"));
	private JButton pauseBtn = new JButton(getString("pause"));
	public static boolean isPaused = false;
	private DownloadWorker downloadWorker;
	private static LoadingScreen loadScreen;
	private Vamix vamix;

	/**
	 * This constructor is used to set up the layout of this download panel
	 */
	public DownloadPanel(Vamix v) {
		this.vamix = v;
		setLayout(new MigLayout());
		
		//add all the components to the panel
		add(urlInstr, "wrap, span 2");
		add(urlField, "grow, wrap, span 2");
		add(isOpen);
		//can't have both yes and no selected at the same time
	    ButtonGroup group = new ButtonGroup();
	    group.add(openY);
	    group.add(openN);
		add(openY, "split 2");
		add(openN, "wrap");
		add(submitBtn, "grow");
		add(pauseBtn, "grow");
		
		//create a colourful border
		TitledBorder border = BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(EtchedBorder.LOWERED, 
				new Color(255, 150, 250, 250), new Color(50, 50, 150, 250)), getString("download"));
		border.setTitleColor(new Color(255, 150, 250, 250));
		border.setTitleFont(new Font("Sans Serif", Font.BOLD, 24));
		setBorder(border);
		
		//add gui components as colour listeners
		State.getState().addColourListeners(this, urlInstr, urlField, isOpen, openY, openN, submitBtn, pauseBtn);
		
		submitBtn.addActionListener(this);

		//this is used to pause the download
		pauseBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (isPaused){
					pauseBtn.setText(getString("pause"));
					isPaused = false;
					String cmd = "wget -c " + url + " 2>&1 ";
					download(cmd);
				}else{
					pauseBtn.setText(getString("resume"));
					isPaused = true;
					loadScreen.setVisible(false);
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
		url = urlField.getText();
		if (url == null || url.equals("")) {
			JOptionPane.showMessageDialog(submitBtn, getString("enterUrlPlease"),getString("error"),JOptionPane.WARNING_MESSAGE);
		} else if (!openY.isSelected()) { 
			JOptionPane.showMessageDialog(submitBtn, getString("illegalDownload"), getString("error"), JOptionPane.ERROR_MESSAGE);
		} else {
			pauseBtn.setEnabled(true);
			checkPreDownload();
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
				//if it does exist, the user is asked if they would like to overwrite
				if(JOptionPane.showOptionDialog(submitBtn, getString("fileExists"),
						getString("error"), 0, 0, null, new String[]{getString("overwrite"), getString("cancel")}, null) == 1){
					JOptionPane.showMessageDialog(submitBtn, getString("notOverwritten"), getString("error"),JOptionPane.ERROR_MESSAGE);
					//if not, then just return
					pauseBtn.setEnabled(false);
					return;
				}
				cmd = "wget -N \"" + url + "\" 2>&1 "; //using -N to "overwrite"
			}else{
				cmd = "wget \"" + url + "\" 2>&1 ";
			}
			download(cmd);
		}catch (Exception e) {
			JOptionPane.showMessageDialog(submitBtn, getString("downloadError"), getString("error"), JOptionPane.ERROR_MESSAGE);
		}
	}
	
	/**
	 * This method starts the download on a background thread
	 * During the download the progress bar will be displayed and updated 
	 * 
	 */
	private void download(String cmd) {
		try {
			submitBtn.setEnabled(false);
			loadScreen = new LoadingScreen(vamix);
			if (!loadScreen.isHidden()){
				loadScreen.prepare();
			}
			downloadWorker = new DownloadWorker(cmd, loadScreen.getProgBar(), loadScreen, this);
			loadScreen.setWorker(downloadWorker);
			downloadWorker.execute();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(submitBtn, getString("downloadError"), getString("error"), JOptionPane.ERROR_MESSAGE);
		}
		
	}
	
	/**
	 * This will re-enable the download button and disable the pause button once the download is finished
	 */
	public void done(){
		submitBtn.setEnabled(true);
		pauseBtn.setEnabled(false);
	}
	
	/**
	 * accessors for the all important url field - primarily used to load/save settings
	 * @return
	 */
	public String getUrl() {
		return urlField.getText();
	}
	public void setUrl(String url){
		urlField.setText(url);
	}
	
	/**
	 * This method gets the string that is associated with each label, in the correct language
	 * @param label
	 * @return the string for this label
	 */
	private String getString(String label){
		return LanguageSelector.getString(label);
	}



}

