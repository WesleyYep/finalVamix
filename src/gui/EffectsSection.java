package gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import components.PlayerVideoAdjustPanel;
import components.TransparentLabel;
import editing.CheckFileExists;
import editing.GetAttributes;
import editing.VideoWorker;
import models.ProjectFile.ProjectSettings;
import net.miginfocom.swing.MigLayout;
import popups.LoadingScreen;
import state.LanguageSelector;
import state.State;

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
	private JRadioButton flipH = new JRadioButton(getString("horizontal"));
	private JRadioButton flipV = new JRadioButton(getString("vertical"));
	private JRadioButton inverseRadio = new JRadioButton(getString("inverse"));
	private JRadioButton grayscaleRadio = new JRadioButton(getString("grayscale"));
	private JButton startTimeBtn = new JButton(getString("setStart"));
	private JButton endTimeBtn = new JButton(getString("setEnd"));
	private JRadioButton resizeRadio = new JRadioButton(getString("resize"));
	private JButton screenshotBtn = new JButton(getString("screenshot"));
	private JButton previewBtn = new JButton(getString("preview"));
	private JButton addBtn = new JButton(getString("create"));
	private JRadioButton gifRadio = new JRadioButton(getString("createGif"));
	private PlayerVideoAdjustPanel adjustPanel;
	private JCheckBox resetAdjustCheckBox = new JCheckBox(getString("resetAdjust"));
	private VideoWorker worker;
	private long startTime = 0;
	private long endTime = 0;

	public EffectsSection(Vamix v, MainControlPanel cp){
		this.vamix = v;
		this.controlPanel = cp;
		adjustPanel = new PlayerVideoAdjustPanel(vamix.getMediaPlayer(), resetAdjustCheckBox);
		
		//create a colourful border
		TitledBorder border = BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(EtchedBorder.LOWERED, 
				new Color(150, 250, 50, 180),new Color(150, 250, 50, 180)), getString("effects"));
		border.setTitleFont(new Font("Sans Serif", Font.BOLD, 24));
		border.setTitleColor(new Color(150, 250, 50, 180));
		setBorder(border);

		//tooltips
		startTimeBtn.setToolTipText(getString("startTimeToolTip"));
		endTimeBtn.setToolTipText(getString("endTimeToolTip"));
		resizeRadio.setToolTipText(getString("resizeToolTip"));
		gifRadio.setToolTipText(getString("createGifToolTip"));
		flipH.setToolTipText(getString("flipHToolTip"));
		flipV.setToolTipText(getString("flipVToolTip"));
		inverseRadio.setToolTipText(getString("inverseToolTip"));
		grayscaleRadio.setToolTipText(getString("grayscaleToolTip"));
		screenshotBtn.setToolTipText(getString("screenshotToolTip"));
		
		setLayout(new MigLayout());
		
		//can't say no to miglayout
		setLayout(new MigLayout());
		TransparentLabel flipLbl, adjustLbl, trimLbl, colourLbl;
		add(trimLbl = new TransparentLabel(getString("trim")), "grow, wrap");
		add(startTimeBtn, "grow, w 160!");
		add(endTimeBtn, "grow, wrap, w 160!");
		add(resizeRadio, "grow");
		add(gifRadio, "wrap");
		add(flipLbl = new TransparentLabel(getString("flip")), "grow");
		add(flipH, "split 2");
		add(flipV, "wrap");
		add(colourLbl = new TransparentLabel(getString("colour")), "grow");
		add(inverseRadio, "split 2");
		add(grayscaleRadio, "wrap");
		add(previewBtn, "grow");
		add(addBtn, "grow, wrap");
		add(adjustLbl = new TransparentLabel(getString("adjust")), "grow");
		add(resetAdjustCheckBox, "grow, wrap");
		add(adjustPanel, "span 2, grow, wrap");
		add(screenshotBtn, "span 2, grow, wrap");

		//add all gui components as colour listeners
		State.getState().addColourListeners(trimLbl, colourLbl, flipLbl, startTimeBtn, adjustLbl, resetAdjustCheckBox,
				endTimeBtn, flipH, flipV, screenshotBtn,inverseRadio, grayscaleRadio, resizeRadio, previewBtn, addBtn, gifRadio, this);
		
		//previewing
		previewBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				addEffects("preview", null);
			}
		});
		
		//prompt the user to enter an output filename. Then add the effects and save it.
		addBtn.addActionListener(new ActionListener(){
			@Override
        	public void actionPerformed(ActionEvent arg0) {
				final JFileChooser fc = new JFileChooser();
		        fc.showSaveDialog(fc);
		        if (fc.getSelectedFile() != null){
		        	String fileName = fc.getSelectedFile().getAbsolutePath().toString();
		        	if (!fileName.endsWith(".mp4") && !gifRadio.isSelected()){
		        		fileName = fileName + ".mp4";
		        	}
		        	if (CheckFileExists.check(fileName)){
						if (JOptionPane.showConfirmDialog((Component) null, getString("fileExists"),
						        "alert", JOptionPane.OK_CANCEL_OPTION) != 0){
							JOptionPane.showMessageDialog(null, getString("notOverwritten"));
							return;
						}
		        	}
					addEffects("conv", fileName);
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
		
		//give the user a warning if the gif is going to be large
		gifRadio.addItemListener(new ItemListener(){
			@Override
        	public void itemStateChanged(ItemEvent arg0) {
				if (gifRadio.isSelected()){
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
    		if (gifRadio.isSelected()){
    			if (!output.endsWith(".gif")){
    				cmd += " -v debug " + output + ".gif";
    			}else {
    				cmd += " -v debug " + output;
    			}
    		}else{
    			cmd += " -strict experimental -f mp4 -v debug " + output;
    		}
    	}else if (gifRadio.isSelected() && option.equals("preview")){
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
    	if (flipH.isSelected()){
    		cmd += "hflip,";
    	}if (flipV.isSelected()){
    		cmd += "vflip,";
    	}if (gifRadio.isSelected()){
    		cmd += "format=rgb24,scale=320:240,";
    	}if (resizeRadio.isSelected() && !gifRadio.isSelected()){
    		Dimension d = vamix.getFrameDimensions();
    		cmd += "scale=" + d.width + ":" + d.height + ",";
    	}if (grayscaleRadio.isSelected()){
    		cmd += "format=gray,";
    	}if (inverseRadio.isSelected()){
    		cmd += "lutrgb='r=negval:g=negval:b=negval',";
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
		settings._effectsStartTime = newStartTime;
		settings._effectsEndTime = newEndTime;
		settings._createGif = gifRadio.isSelected();
		settings._flipH = flipH.isSelected();
		settings._flipV = flipV.isSelected();
		settings._resize = resizeRadio.isSelected();
		settings._inverse = inverseRadio.isSelected();
		settings._grayscale = grayscaleRadio.isSelected();
		return settings;
	}
	
	/**
	 * fill fields from settings
	 */
	public void loadProjectSettings(ProjectSettings ps) {
		gifRadio.setSelected(ps._createGif);
		resizeRadio.setSelected(ps._resize);
		flipH.setSelected(ps._flipH);
		flipV.setSelected(ps._flipV);
		inverseRadio.setSelected(ps._inverse);
		grayscaleRadio.setSelected(ps._grayscale);
		startTime = Long.parseLong(ps._effectsStartTime);
		endTime = Long.parseLong(ps._effectsEndTime);
		startTimeBtn.setText(secsToString(startTime));
		endTimeBtn.setText(secsToString(endTime));
	}
	
	private String getString(String label){
		return LanguageSelector.getString(label);
	}

}
