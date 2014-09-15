package gui;

import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
import download.Bubba;

/**
 * This has been taken from my assignment 2 and should be usable with a few 
 * (or perhaps a lot of) changes
 * 
 * This screen is used to download either video or audio 
 * 
 * @author group 27
 */
public class DownloadPanel extends JPanel implements ActionListener{

	/** An unused ID here only to get rid of the warning */
	private static final long serialVersionUID = 2890178349343322712L;

	private JLabel title = new JLabel ("Yeah, cool");

	// My strings are all here so it is easy to make changes
	private String enterURL = "Enter video or audio URL: ";
	private String urSureOpenSource = "Is this open source?";
	private String submit = "Start Download";
	
	private String url;
	
	private JLabel urlInstr = new JLabel(enterURL);
	private JTextField urlField = new JTextField(20);
	
	private JLabel isOpen = new JLabel(urSureOpenSource);
	private JRadioButton openY = new JRadioButton("Yes");
	private JRadioButton openN = new JRadioButton("No");
	private JButton submitBtn = new JButton(submit);
	private JButton pauseBtn = new JButton("Pause");
	private JProgressBar prog = new JProgressBar(0, 100);
	public static boolean isPaused = false;
	
	private JPanel innerPanel = new JPanel();
	
	private Bubba shrimp;
	

	/**
	 * This constructor is used to set up the layout of this download panel
	 */
	public DownloadPanel() {
		
		title.setAlignmentX(Component.CENTER_ALIGNMENT);
		title.setFont (new Font("Serif", Font.BOLD, 48));
		add(title);
		
		GroupLayout layout = new GroupLayout(innerPanel);
		 innerPanel.setLayout(layout);
		 
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		
		// This section lays out all the various components in a beautiful configuration
		layout.setHorizontalGroup(
				layout.createSequentialGroup()
				  .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED,
                     GroupLayout.DEFAULT_SIZE, 50)
			      .addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
			           .addComponent(urlInstr)
			           .addComponent(isOpen)
			           .addComponent(submitBtn))
			      .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
			           .addComponent(urlField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
			        	          GroupLayout.PREFERRED_SIZE)
			           .addGroup(layout.createSequentialGroup()
			        		.addComponent(openY)
			        		.addComponent(openN))
			           .addComponent(pauseBtn))
		);
		layout.setVerticalGroup(
		   layout.createSequentialGroup()
		   	  .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED,
                     GroupLayout.DEFAULT_SIZE, 50)
		      .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
		           .addComponent(urlInstr)
		           .addComponent(urlField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE,
		        	          GroupLayout.PREFERRED_SIZE))
		      .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED,
                     GroupLayout.DEFAULT_SIZE, 15)
		      .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
		           .addComponent(isOpen)
		           .addComponent(openY)
		           .addComponent(openN))
		      .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED,
                     GroupLayout.DEFAULT_SIZE, 15)
              .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
		           .addComponent(submitBtn)
		           .addComponent(pauseBtn))
		      .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED,
                     GroupLayout.DEFAULT_SIZE, 15)
		      
		);
		
		submitBtn.addActionListener(this);
		// Group buttons so only one can be selected
		ButtonGroup andOrModeGrp = new ButtonGroup();
		andOrModeGrp.add(openY);
		andOrModeGrp.add(openN);
		
		// This section is the final step of the layout. It is used so that the submitBtn 
		// can be in the center of the window
		this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		innerPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		add(innerPanel);

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
		
		// Add the progress bar but don't show it until something is actually being downloaded
		add(prog);
		prog.setVisible(false);
		
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
			prog.setVisible(false);
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
			prog.setVisible(true);
			submitBtn.setText("Cancel");
			// Bubba is the download SwingWorker
			shrimp = new Bubba(cmd, prog, submitBtn, pauseBtn);
			shrimp.execute();
			
		} catch (Exception e) {
			JOptionPane.showMessageDialog(submitBtn,
				    "Error attempting download",
				    "Error",
				    JOptionPane.ERROR_MESSAGE);
		}
		
	}
	
	

}

