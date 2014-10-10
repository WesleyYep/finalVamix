package gui;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;

import components.CustomSpinner;
import components.TransparentLabel;
import editing.GetAttributes;
import net.miginfocom.swing.MigLayout;
import popups.LoadingScreen;
import state.State;

public class EffectsSection extends JPanel{
	private EditorPanel editorPanel;
	private MainControlPanel controlPanel;
	private static LoadingScreen loadScreen;
	private JComboBox<String> speedOption;
	private JComboBox<String> flipOption;
	private JComboBox<String> fadeOption;
	private CustomSpinner startSpinner;
	private CustomSpinner endSpinner;
	private JButton previewBtn = new JButton("Preview");
	private JButton addBtn = new JButton("Add");

	public EffectsSection(EditorPanel ep, MainControlPanel cp){
		this.editorPanel = ep;
		this.controlPanel = cp;
		TransparentLabel speedLbl, startLbl, endLbl, flipLbl, fadeLbl;
		speedOption = new JComboBox<String>(new String[] {"0.25x", "0.5x", "1x", "2x", "3x", "5x"});
		flipOption = new JComboBox<String>(new String[] {"None", "Horizontal", "Vertical"});
		fadeOption = new JComboBox<String>(new String[] {"None", "Start", "End"});
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
		add(speedLbl = new TransparentLabel("Speed: "), "span 2");
		add(speedOption, "wrap");
		add(startLbl = new TransparentLabel("Trim - Start: "), "span 2");
		add(startSpinner, "wrap");
		add(endLbl = new TransparentLabel("Trim - End: "), "span 2");
		add(endSpinner, "wrap");
		add(flipLbl = new TransparentLabel("Flip: "), "span 2");
		add(flipOption, "wrap");
		add(fadeLbl = new TransparentLabel("Fade"), "span 2");
		add(fadeOption, "wrap");
		add(previewBtn);
		add(addBtn);
		
		State.getState().addColourListeners(speedLbl, startLbl, endLbl, flipLbl, fadeLbl, speedOption, startSpinner,
				endSpinner, flipOption, fadeOption, previewBtn, addBtn, this);
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
    	int fps = GetAttributes.getFPS(editorPanel.getMediaName());
    	String cmd = "avconv -i " + editorPanel.getMediaName() + "-vf \"";
    	
	}
}
