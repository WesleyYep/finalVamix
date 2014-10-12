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
import state.LanguageSelector;
import state.State;
import net.miginfocom.swing.MigLayout;
import download.DownloadWorker;

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
	//	add(pauseBtn);
		
		TitledBorder border = BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(EtchedBorder.LOWERED, 
				new Color(255, 150, 250, 250), new Color(50, 50, 150, 250)), getString("download"));
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
					pauseBtn.setText(getString("pause"));
					isPaused = false;
					submitBtn.setEnabled(true);
					String cmd = "wget -c " + url + " 2>&1 ";
					download(cmd);
				}else{
					pauseBtn.setText(getString("resume"));
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
		if (downloadWorker != null && !downloadWorker.isDone()) {
			downloadWorker.cancel(true);
			submitBtn.setText(getString("startDownload"));
			downloadWorker = null;
		} else {
			pauseBtn.setEnabled(true);
			url = urlField.getText();
			if (url == null || url.equals("")) {
				JOptionPane.showMessageDialog(submitBtn,
					    getString("enterUrlPlease"),
					    getString("error"),
					    JOptionPane.WARNING_MESSAGE);
			} else if (!openY.isSelected() ) { 
				JOptionPane.showMessageDialog(submitBtn,
					    getString("illegalDownload"),
					    getString("error"),
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
				if(JOptionPane.showOptionDialog(submitBtn, getString("fileExists"),
						getString("error"), 0, 0, null, new String[]{getString("overwrite"), getString("cancel")}, null) == 1){
					JOptionPane.showMessageDialog(submitBtn,
									    getString("notOverwritten"),
									    getString("error"),
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
				    getString("downloadError"),
				    getString("error"),
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
			loadScreen = new LoadingScreen(ep, submitBtn);
            loadScreen.prepare();
			downloadWorker = new DownloadWorker(cmd, loadScreen.getProgBar(), submitBtn, pauseBtn, loadScreen, this);
			downloadWorker.execute();
			
		} catch (Exception e) {
			JOptionPane.showMessageDialog(submitBtn,
					getString("downloadError"),
				    getString("error"),
				    JOptionPane.ERROR_MESSAGE);
		}
		
	}
	
	public String getUrl() {
		return urlField.getText();
	}
	public void setUrl(String url){
		urlField.setText(url);
	}
	
	public void downloadFinished(JButton submit){
		add(submit);
	}
	
	private String getString(String label){
		return LanguageSelector.getString(label);
	}



}

