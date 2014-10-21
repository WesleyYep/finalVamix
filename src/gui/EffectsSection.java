package gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner.DateEditor;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import components.CustomSpinner;
import components.TransparentLabel;
import editing.CheckFileExists;
import editing.GetAttributes;
import editing.VideoWorker;
import models.ProjectFile;
import models.ProjectFile.ProjectSettings;
import net.miginfocom.swing.MigLayout;
import popups.LoadingScreen;
import state.LanguageSelector;
import state.State;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * This section is for editing effects. It is incorporated into the main window
 * @author wesley
 *
 */
@SuppressWarnings("serial")
public class EffectsSection extends JPanel{
	private Vamix vamix;
	private MainControlPanel controlPanel;
	private static LoadingScreen loadScreen;
	private JComboBox<String> speedOption;
	private JRadioButton flipH = new JRadioButton(getString("horizontal"));
	private JRadioButton flipV = new JRadioButton(getString("vertical"));
	private JRadioButton fadeS = new JRadioButton(getString("start"));
	private JRadioButton fadeE = new JRadioButton(getString("end"));
	private JButton startTimeBtn = new JButton(getString("setStart"));
	private JButton endTimeBtn = new JButton(getString("setEnd"));
	private JButton previewBtn = new JButton(getString("preview"));
	private JButton addBtn = new JButton(getString("add"));
	private JCheckBox gifCheckBox;
	private VideoWorker worker;
	private long startTime = 0;
	private long endTime = 0;

	public EffectsSection(Vamix v, MainControlPanel cp){
		this.vamix = v;
		this.controlPanel = cp;
		TransparentLabel speedLbl, flipLbl, fadeLbl;
		speedOption = new JComboBox<String>(new String[] {"0.25x", "0.5x", "1x", "2x", "3x", "5x"});
		speedOption.setSelectedIndex(2);
//		startSpinner = new CustomSpinner(0);
//		endSpinner = new CustomSpinner(20);
		gifCheckBox = new JCheckBox(getString("createGif"));
		//create a colourful border
		TitledBorder border = BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(EtchedBorder.LOWERED, 
				new Color(150, 250, 50, 180),new Color(150, 250, 50, 180)), getString("effects"));
		border.setTitleFont(new Font("Sans Serif", Font.BOLD, 24));
		border.setTitleColor(new Color(150, 250, 50, 180));
		setBorder(border);
		//can't say no to miglayout
		setLayout(new MigLayout());
		add(speedLbl = new TransparentLabel(getString("speed")), "grow");
		add(speedOption, "wrap, grow");
//		add(startLbl = new TransparentLabel(getString("trimStart")), "grow");
		add(startTimeBtn, "grow, w 160!");
//		add(endLbl = new TransparentLabel(getString("trimEnd")), "grow");
		add(endTimeBtn, "grow, wrap, w 160!");
		add(gifCheckBox, "span 2, align right, wrap");
		add(flipLbl = new TransparentLabel(getString("flip")), "grow");
		add(flipH, "split 2");
		add(flipV, "wrap");		
		add(fadeLbl = new TransparentLabel(getString("fade")), "grow");
		add(fadeS, "split 2");
		add(fadeE, "wrap");
		add(previewBtn, "grow");
		add(addBtn, "grow");
		//add all gui components as colour listeners
		State.getState().addColourListeners(speedLbl, flipLbl, fadeLbl, speedOption, startTimeBtn,
				endTimeBtn, flipH, flipV, fadeS, fadeE, previewBtn, addBtn, gifCheckBox, this);
//		State.getState().addSpinnerListeners(startSpinner, endSpinner);
		
		//previewing
		previewBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				addEffects("preview", "udp://localhost:1234");
			}
		});
		
		//prompt the user to enter an output filename. Then add the effects and save it.
		addBtn.addActionListener(new ActionListener(){
			@Override
        	public void actionPerformed(ActionEvent arg0) {
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
		            String outputFile = fc.getSelectedFile().getAbsolutePath().toString();
					addEffects("conv", outputFile);
		        }
        	}
        });
		
		startTimeBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (vamix.isMediaFile(vamix.getMediaName())){
					startTime = controlPanel.getTime()/1000;
					startTimeBtn.setText(secsToString(startTime));
				}
			}
		});
		
		endTimeBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (vamix.isMediaFile(vamix.getMediaName())){
					endTime = controlPanel.getTime()/1000;
					endTimeBtn.setText(secsToString(endTime));
				}
			}
		});
		
		//we should automatically suggest to the user that the gif shouldn't be longer than about 
		//20 seconds. This is done by automatically setting the end time spinner
		gifCheckBox.addActionListener(new ActionListener(){
			@Override
        	public void actionPerformed(ActionEvent arg0) {
				if (gifCheckBox.isSelected()){
					int dur = GetAttributes.getDuration(vamix.getMediaName());
					if (endTime == dur){
						JOptionPane.showMessageDialog(null, getString("longGif"), getString("error"), JOptionPane.DEFAULT_OPTION);
					}
				}
        	}
		});
	}
	
	/**
	 * This method is called when either the add or preview  button is clicked
	 * This will use either avconv or avplay to add the effects to the video.
	 * @oparam option - either convert or preview
	 * @param output - the user specified output file name
	 */
	private void addEffects(String option, String output) {
		if (endTime < startTime){
			JOptionPane.showMessageDialog(null, getString("endLessThanStart"), "Error", JOptionPane.DEFAULT_OPTION);
			return;
		}
		//get the duration and attributes for use in the progress bar
		int dur = GetAttributes.getDuration(vamix.getMediaName());
    	int frames = GetAttributes.getFrames(vamix.getMediaName());
    	
    	String cmd = initialiseCmd(option);
    	cmd = addEffectsToCmd(cmd, frames);
    	
    	if (option.equals("conv")){
    		if (gifCheckBox.isSelected()){
    			if (!output.endsWith(".gif")){
    				cmd += " -v debug " + output + ".gif";
    			}else {
    				cmd += " -v debug " + output;
    			}
    		}else{
    			cmd += " -strict experimental -f mp4 -v debug " + output;
    		}
    	}else if (option.equals("preview")){
    		 cmd += " -loop 20 -strict experimental";
    	}
        //only carry out the command if the video file is valid
        if (dur > 0 && frames > 0){
    		loadScreen = new LoadingScreen(vamix);
	        worker = new VideoWorker(cmd, loadScreen.getProgBar(), frames, option, "Effects", loadScreen);
	        if (option.equals("conv")){
				loadScreen.prepare();
		        loadScreen.setWorker(worker);
	        }
	        System.out.println(cmd);
	        worker.execute();
		}
        else{
			JOptionPane.showMessageDialog(null, getString("unsupportedFile"), getString("error"), JOptionPane.DEFAULT_OPTION);
        }
	}

//	/**
//	 * gets the difference between two times, and converts it into hh:mm:ss format
//	 * @param startTime start time on spinner
//	 * @param endTime end time on spinner
//	 * @return difference in hh:mm:ss format
//	 */
//	private String getTimeDiff(String startTime, String endTime) {
//        java.text.DateFormat df = new java.text.SimpleDateFormat("hh:mm:ss");
//        java.util.Date start, end;
//		try {
//	        start = df.parse(startTime);
//			end = df.parse(endTime);
//	        long diff = end.getTime() - start.getTime();
//	        return millisToString(diff);
//		} catch (ParseException e) {}
//		return null;
//	}
	
//	/**
//	 * converts milliseconds to hh:mm:ss string
//	 * @param millis
//	 * @return string representing the time in hh:mm:ss format
//	 */
//	private String millisToString(long millis){
//		return String.format("%02d:%02d:%02d", 
//			    TimeUnit.MILLISECONDS.toHours(millis),
//			    TimeUnit.MILLISECONDS.toMinutes(millis) - 
//			    TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
//			    TimeUnit.MILLISECONDS.toSeconds(millis) - 
//			    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
//	}

	/**
	 * Write the initial bits of the avconv/avplay command
	 * @param option conv/play
	 * @return String which contains the initial command
	 */
	private String initialiseCmd(String option) {
        String start = startTime + "";
        String durTime = (endTime - startTime) + "";
    	String cmd = "";
    	if (option.equals("conv")){
    		cmd = "avconv -y -i " + vamix.getMediaName() + " -ss " + start + 
    				" -t " + durTime + " -vf \"";
    	}else{
    		cmd = "avplay -i " + vamix.getMediaName() + " -ss " + start + 
    				" -t " + durTime + " -vf \"";
    	}
    	return cmd;
	}
	
	/**
	 * This adds the other filters to the command
	 * @param cmd the command so far
	 * @param frames total number of frames in input
	 * @return the modified command
	 */
	private String addEffectsToCmd(String cmd, int frames) {
    	if (!speedOption.getSelectedItem().toString().equals("1x")){
    		cmd += "setpts=" + 1/Double.parseDouble(speedOption.getSelectedItem().toString().split("x")[0]) + "*PTS,";
    	}if (flipH.isSelected()){
    		cmd += "hflip,";
    	}if (flipV.isSelected()){
    		cmd += "vflip,";
    	}if (fadeS.isSelected()){
    		cmd += "fade=in:0:60,";
    	}if (fadeE.isSelected()){
    		cmd += "fade=out:" + (frames-60) + ":60,";
    	}if (gifCheckBox.isSelected()){
    		cmd += "format=rgb24,scale=320:240,";
    	}
	
    	if (cmd.endsWith(",")){
    		cmd = cmd.substring(0, cmd.length()-1);
    	}
    	if (cmd.endsWith("\"")){
    		cmd = cmd.substring(0, cmd.length()-5);
    	}else {
    		cmd += "\"";
    	}
    	
    	return cmd;
	}

	/**
	 * Method to convert seconds to hhmmss string.
	 * Taken from http://stackoverflow.com/questions/19205920/how-to-convert-seconds-of-timer-to-hhmmss
	 * @param seconds
	 * @return hhmmss string
	 */
	private String secsToString(long seconds){
		long hr = seconds/3600;
		long rem = seconds%3600;
		long mn = rem/60;
		long sec = rem%60;
		String hrStr = (hr<10 ? "0" : "")+hr;
		String mnStr = (mn<10 ? "0" : "")+mn;
		String secStr = (sec<10 ? "0" : "")+sec; 
		return hrStr+ ":"+mnStr+ ":"+secStr;
	}
	
	/**
	 * set the default value for endSpinner based on the length of media
	 */
	public void setTimeDefault() {
		int dur = GetAttributes.getDuration(vamix.getMediaName());
		endTime = dur;
	}

	/**
	 * add to project settings
	 */
	public ProjectSettings createProjectSettings(ProjectSettings settings) {
		String newStartTime = startTime + "";
        String newEndTime = endTime + "";
		settings._speed = speedOption.getSelectedIndex();
		settings._effectsStartTime = newStartTime;
		settings._effectsEndTime = newEndTime;
		settings._createGif = gifCheckBox.isSelected();
		settings._flipH = flipH.isSelected();
		settings._flipV = flipV.isSelected();
		settings._fadeS = fadeS.isSelected();
		settings._fadeE = fadeE.isSelected();

		return settings;
	}
	
	/**
	 * fill fields from settings
	 */
	public void loadProjectSettings(ProjectSettings ps) {
		speedOption.setSelectedIndex(ps._speed);
		gifCheckBox.setSelected(ps._createGif);
		flipH.setSelected(ps._flipH);
		flipV.setSelected(ps._flipV);
		fadeS.setSelected(ps._fadeS);
		fadeE.setSelected(ps._fadeE);
		startTime = Long.parseLong(ps._effectsStartTime);
		endTime = Long.parseLong(ps._effectsEndTime);
		startTimeBtn.setText(secsToString(startTime));
		endTimeBtn.setText(secsToString(endTime));
	}
	
	private String getString(String label){
		return LanguageSelector.getString(label);
	}

}
