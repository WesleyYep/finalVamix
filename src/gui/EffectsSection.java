package gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner.DateEditor;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import components.CustomSpinner;
import components.TransparentLabel;
import editing.GetAttributes;
import editing.VideoWorker;
import net.miginfocom.swing.MigLayout;
import popups.LoadingScreen;
import state.LanguageSelector;
import state.State;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class EffectsSection extends JPanel{
	private EditorPanel editorPanel;
	private MainControlPanel controlPanel;
	private static LoadingScreen loadScreen;
	private JComboBox<String> speedOption;
	private JRadioButton flipH = new JRadioButton(getString("horizontal"));
	private JRadioButton flipV = new JRadioButton(getString("vertical"));
	private JRadioButton fadeS = new JRadioButton(getString("start"));
	private JRadioButton fadeE = new JRadioButton(getString("end"));
	private CustomSpinner startSpinner;
	private CustomSpinner endSpinner;
	private JButton previewBtn = new JButton(getString("preview"));
	private JButton addBtn = new JButton(getString("add"));
	private JCheckBox gifCheckBox;
	private boolean isDraggable;

	public EffectsSection(EditorPanel ep, MainControlPanel cp){
		this.editorPanel = ep;
		this.controlPanel = cp;
		TransparentLabel speedLbl, startLbl, endLbl, flipLbl, fadeLbl, gifLbl;
		speedOption = new JComboBox<String>(new String[] {"0.25x", "0.5x", "1x", "2x", "3x", "5x"});
		speedOption.setSelectedIndex(2);
		startSpinner = new CustomSpinner(0);
		endSpinner = new CustomSpinner(20);
		gifCheckBox = new JCheckBox(getString("createGif"));
		
		TitledBorder border = BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(EtchedBorder.LOWERED, 
				new Color(150, 250, 50, 180),new Color(150, 250, 50, 180)), getString("effects"));
		border.setTitleFont(new Font("Sans Serif", Font.BOLD, 24));
		border.setTitleColor(new Color(150, 150, 250, 250));
		setBorder(border);
		
		setLayout(new MigLayout());
		add(speedLbl = new TransparentLabel(getString("speed")), "grow");
		add(speedOption, "wrap, grow");
		add(startLbl = new TransparentLabel(getString("trimStart")), "grow");
		add(startSpinner, "grow, wrap");
		add(endLbl = new TransparentLabel(getString("trimEnd")), "grow");
		add(endSpinner, "grow, wrap");
		add(gifCheckBox, "span 2, align right, wrap");
		add(flipLbl = new TransparentLabel(getString("flip")), "grow");
		add(flipH, "split 2");
		add(flipV, "wrap");		
		add(fadeLbl = new TransparentLabel(getString("fade")), "grow");
		add(fadeS, "split 2");
		add(fadeE, "wrap");
		add(previewBtn, "grow");
		add(addBtn, "grow");
		
		State.getState().addColourListeners(speedLbl, startLbl, endLbl, flipLbl, fadeLbl, speedOption, startSpinner,
				endSpinner, flipH, flipV, fadeS, fadeE, previewBtn, addBtn, gifCheckBox, this);
		State.getState().addSpinnerListeners(startSpinner, endSpinner);
		
		previewBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				addEffects("preview", "udp://localhost:1234");
				controlPanel.playPreview();
			}
		});
		
		//prompt the user to enter an output filename. Then add the effects and save it.
		addBtn.addActionListener(new ActionListener(){
			@Override
        	public void actionPerformed(ActionEvent arg0) {
				final JFileChooser fc = new JFileChooser();
		        fc.showSaveDialog(fc);
		        if (fc.getSelectedFile() != null){
		            String outputFile = fc.getSelectedFile().getAbsolutePath().toString();
					addEffects("conv", outputFile);
		        }
        	}
        });
		
		gifCheckBox.addActionListener(new ActionListener(){
			@Override
        	public void actionPerformed(ActionEvent arg0) {
				if (gifCheckBox.isSelected()){
					previewBtn.setEnabled(false);
				}else{
					previewBtn.setEnabled(true);
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
		//get the duration and attributes for use in the progress bar
		int dur = GetAttributes.getDuration(editorPanel.getMediaName());
    	int frames = GetAttributes.getFrames(editorPanel.getMediaName());
    	
    	String cmd = initialiseCmd(option);
    	cmd = addEffectsToCmd(cmd, frames);
    	
    	if (option.equals("conv")){
    		if (gifCheckBox.isSelected()){
    			if (!output.endsWith(".gif")){
    				cmd += "-v debug " + output + ".gif";
    			}else {
    				cmd += "-v debug " + output;
    			}
    		}else{
    			cmd += " -strict experimental -f mp4 -v debug " + output;
    		}
    	}else if (option.equals("preview")){
    		 cmd += " -strict experimental";
    		 controlPanel.setDuration(dur*1000);
    		 controlPanel.setIsPreviewing(true);
    	}
    	System.out.println(cmd);
        //only carry out the command if the video file is valid
        if (dur > 0 && frames > 0){
    		loadScreen = new LoadingScreen(editorPanel);
	        if (option.equals("conv")){
				loadScreen.prepare();
	        }
	        System.out.println(cmd);
	        VideoWorker worker = new VideoWorker(cmd, loadScreen.getProgBar(), frames, option, "Effects", loadScreen);
	        worker.execute();
		}
	}

	private String getTimeDiff(String startTime, String endTime) {
        java.text.DateFormat df = new java.text.SimpleDateFormat("hh:mm:ss");
        java.util.Date start, end;
		try {
	        start = df.parse(startTime);
			end = df.parse(endTime);
	        long diff = end.getTime() - start.getTime();
	        return millisToString(diff);
		} catch (ParseException e) {}
		return null;
	}
	
	private String millisToString(long millis){
		return String.format("%02d:%02d:%02d", 
			    TimeUnit.MILLISECONDS.toHours(millis),
			    TimeUnit.MILLISECONDS.toMinutes(millis) - 
			    TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
			    TimeUnit.MILLISECONDS.toSeconds(millis) - 
			    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
	}

	private String initialiseCmd(String option) {
        String startTime = new DateEditor(startSpinner , "yy:mm:ss").getFormat().format(startSpinner.getValue());
        String endTime = new DateEditor(endSpinner , "yy:mm:ss").getFormat().format(endSpinner.getValue());
        String durTime = getTimeDiff(startTime, endTime);
    	String cmd = "";
    	if (option.equals("conv")){
    		cmd = "avconv -i " + editorPanel.getMediaName() + " -ss " + startTime + 
    				" -t " + durTime + " -vf \"";
    	}else{
    		cmd = "avplay -i " + editorPanel.getMediaName() + " -ss " + startTime + 
    				" -t " + durTime + " -vf \"";
    	}
    	return cmd;
	}
	
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
    	}
	
    	if (cmd.endsWith(",")){
    		cmd = cmd.substring(0, cmd.length()-1);
    	}
    	if (cmd.endsWith("\"")){
    		cmd = cmd.substring(0, cmd.length()-5);
    	}else {
    		cmd += "\"";
    	}
    	
    	if (gifCheckBox.isSelected()){
    		cmd += " -pix_fmt rgb24 -s 320x240 ";
    	}
    	
    	return cmd;
	}

	public void setSpinnerDefault() {
		int dur = GetAttributes.getDuration(editorPanel.getMediaName());
		int hr = dur/3600;
		int rem = dur%3600;
		int mn = rem/60;
		int sec = rem%60;
		String hrStr = (hr<10 ? "0" : "")+hr;
		String mnStr = (mn<10 ? "0" : "")+mn;
		String secStr = (sec<10 ? "0" : "")+sec; 
		System.out.println(mn + " - " + mnStr);
		SimpleDateFormat format = new SimpleDateFormat("yy:mm:ss");
		try {
			Date d = (java.util.Date)format.parse(hrStr + ":" + mnStr + ":" + secStr);
		    java.sql.Time time = new java.sql.Time(d.getTime());
		    endSpinner.setValue(time);
		} catch (ParseException e) {}
	}

	private String getString(String label){
		return LanguageSelector.getString(label);
	}
}
