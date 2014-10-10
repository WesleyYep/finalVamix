package gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import components.CustomSpinner;
import components.TransparentLabel;
import editing.GetAttributes;
import editing.TextWorker;
import net.miginfocom.swing.MigLayout;
import popups.LoadingScreen;
import state.State;

public class EffectsSection extends JPanel{
	private EditorPanel editorPanel;
	private MainControlPanel controlPanel;
	private static LoadingScreen loadScreen;
	private JComboBox<String> speedOption;
	private JRadioButton flipH = new JRadioButton("Horz");
	private JRadioButton flipV = new JRadioButton("Vert");
	private JRadioButton fadeS = new JRadioButton("Start");
	private JRadioButton fadeE = new JRadioButton("End");
	private CustomSpinner startSpinner;
	private CustomSpinner endSpinner;
	private JButton previewBtn = new JButton("Preview");
	private JButton addBtn = new JButton("Add");

	public EffectsSection(EditorPanel ep, MainControlPanel cp){
		this.editorPanel = ep;
		this.controlPanel = cp;
		TransparentLabel speedLbl, startLbl, endLbl, flipLbl, fadeLbl;
		speedOption = new JComboBox<String>(new String[] {"0.25x", "0.5x", "1x", "2x", "3x", "5x"});
		speedOption.setSelectedIndex(2);
		startSpinner = new CustomSpinner(0);
		endSpinner = new CustomSpinner(20); //change default time later??
		
		TitledBorder border = BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(EtchedBorder.LOWERED, 
				new Color(150, 250, 50, 180),new Color(150, 250, 50, 180)), "Effects");
		border.setTitleFont(new Font("Sans Serif", Font.BOLD, 24));
		border.setTitleColor(new Color(150, 150, 250, 250));
		setBorder(border);
        State.getState().addBorderListeners(border);
		
		setLayout(new MigLayout());
		add(speedLbl = new TransparentLabel("Speed: "), "grow");
		add(speedOption, "wrap, grow");
		add(startLbl = new TransparentLabel("Trim - Start: "), "grow");
		add(startSpinner, "wrap, grow");
		add(endLbl = new TransparentLabel("Trim - End: "), "grow");
		add(endSpinner, "wrap, grow");
		add(flipLbl = new TransparentLabel("Flip: "), "grow");
		add(flipH, "split 2");
		add(flipV, "wrap");		
		add(fadeLbl = new TransparentLabel("Fade"), "grow");
		add(fadeS, "split 2");
		add(fadeE, "wrap");
		add(previewBtn, "grow");
		add(addBtn, "grow");
		
		State.getState().addColourListeners(speedLbl, startLbl, endLbl, flipLbl, fadeLbl, speedOption, startSpinner,
				endSpinner, flipH, flipV, fadeS, fadeE, previewBtn, addBtn, this);
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
    	
    	String cmd;
    	if (option.equals("conv")){
    		cmd = "avconv -i " + editorPanel.getMediaName() + " -vf \"";
    	}else{
    		cmd = "avconv -re -i " + editorPanel.getMediaName() + " -vf \"";
    	}
    	
    	if (!speedOption.getSelectedItem().toString().equals("1x")){
    		cmd += "setpts=" + 1/Double.parseDouble(speedOption.getSelectedItem().toString().split("x")[0]) + "*PTS,";
    	}if (flipH.isSelected()){
    		cmd += "hflip,";
    	}if (flipV.isSelected()){
    		cmd += "vflip,";
    	}if (fadeS.isSelected()){
    		cmd += "fade=in:0:30";
    	}if (fadeE.isSelected()){
    		cmd += "fade=out:" + frames + ":30";
    	}
    	
    	if (cmd.endsWith(",")){
    		cmd = cmd.substring(0, cmd.length()-1);
    	}
    	if (option.equals("conv")){
	        cmd += "\" -strict experimental -f mp4 -v debug " + output;
    	}else if (option.equals("preview")){
    		 cmd += "\" -strict experimental -f mpegts " + output;
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
	        TextWorker worker = new TextWorker(cmd, loadScreen.getProgBar(), frames, option);
	        worker.execute();
		}
	}
}
