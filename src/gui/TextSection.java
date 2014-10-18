package gui;

import editing.CheckFileExists;
import editing.GetAttributes;
import editing.ProjectFile;
import editing.VideoWorker;
import editing.ProjectFile.ProjectSettings;
import java.awt.event.MouseAdapter;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JViewport;
import javax.swing.SpinnerDateModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.JSpinner.DateEditor;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import components.CustomSpinner;
import components.TransparentLabel;
import popups.ColourChooser;
import popups.LoadingScreen;
import state.LanguageSelector;
import state.State;
import models.TextTableModel;
import net.miginfocom.swing.MigLayout;

/**
 * This panel contains all the controls and functionality for text manipulation
 * @author Wesley
 *
 */
@SuppressWarnings("serial")
public class TextSection extends JPanel implements MouseListener {
	
	private JTextArea textArea = new JTextArea(getString("defaultText"),5,15);
	private JButton renderBtn = new JButton(getString("render"));
	private JButton previewBtn = new JButton(getString("preview"));
	private JButton colourBtn = new JButton();
	private JButton textPosBtn = new JButton(getString("setPosition"));
	private JComboBox<String> fontOption;
	private ColourChooser cc = new ColourChooser(this);
	private CustomSpinner fontSizeSpinner;
	private final JScrollPane textScroll = new JScrollPane(textArea);
	private JButton startTimeBtn = new JButton(getString("setStart"));
	private JButton endTimeBtn = new JButton(getString("setEnd"));
	private JRadioButton addRadio = new JRadioButton(getString("add"));
	private JRadioButton removeRadio = new JRadioButton(getString("remove"));
	private JRadioButton editRadio = new JRadioButton(getString("edit"));
	private JButton okBtn = new JButton(getString("okay"));
	private TextTableModel tableModel;
	private JTable textTable;
	private JScrollPane tableScroll;
	private MainControlPanel cp;
	private Vamix vamix;
	private boolean isSelecting = false;
	private static LoadingScreen loadScreen;
	private VideoWorker worker;
	private int xPos = 0;
	private int yPos = 0;
	private long startTime = 0;
	private long endTime = 10;

	/**
	 * The constructor sets up the GUI and adds listeners
	 * @param v The main frame that this is housed in. This is used to get the name
	 * 		of the currently playing media.
	 * @param cp The main control panel of the video, so that we can retrieve current time of media
	 */
	public TextSection(final Vamix v, final MainControlPanel cp){
		
		vamix = v;
		this.cp = cp;
		fontOption = new JComboBox<String>(new String[]{"DejaVuSans", "DroidSans", "FreeSans", "NanumGothic", "Padauk", 
														"TakaoPGothic", "Ubuntu-C"});
       
		fontSizeSpinner = new CustomSpinner(18, new SpinnerNumberModel(0, 0, 72, 1));
		State.getState().addSpinnerListeners(fontSizeSpinner);
        
		setLayout(new MigLayout("", "[] []"));
		TitledBorder border = BorderFactory.createTitledBorder(
				BorderFactory.createEtchedBorder(EtchedBorder.LOWERED, 
						new Color(50, 150, 50, 250), new Color(50, 150, 50, 250)), getString("text"));
		border.setTitleFont(new Font("Sans Serif", Font.BOLD, 24));
		border.setTitleColor(new Color(50, 150, 50, 250));
		setBorder(border);

		tableModel = new TextTableModel();
		textTable = new JTable(tableModel);
		tableScroll = new JScrollPane(textTable);
		textTable.setTableHeader(null);
		tableScroll.setPreferredSize(new Dimension(300,200));
		textPosBtn.setMargin(new java.awt.Insets(1, 5, 1, 5));
		endTimeBtn.setMargin(new java.awt.Insets(1, 5, 1, 5));
		
		textArea.setBorder(BorderFactory.createEtchedBorder());
		textArea.setLineWrap(true);
		textArea.setFont( textArea.getFont().deriveFont(Float.parseFloat(fontSizeSpinner.getValue().toString())) );
		textArea.setPreferredSize(new Dimension(300,200));
		textScroll.setPreferredSize(new Dimension(300,200));
		colourBtn.setBackground(Color.RED);
		addRadio.setSelected(true);
		
		State.getState().addColourListeners(textArea, textScroll, textScroll.getViewport(), fontOption, fontSizeSpinner, tableScroll, textTable,
		tableScroll.getViewport(), previewBtn,	renderBtn, textPosBtn, startTimeBtn, endTimeBtn, okBtn, addRadio, removeRadio, editRadio);
		
		add(textScroll, "cell 0 0 2 1, grow");
		TransparentLabel fontLbl, colourLbl, sizeLbl;
		add(fontLbl = new TransparentLabel(getString("font")), "cell 0 2");
		add(fontOption, "cell 1 2, grow");
		add(colourLbl = new TransparentLabel(getString("colour")), "cell 0 3");
		add(colourBtn, "cell 1 3, grow");
		add(sizeLbl = new TransparentLabel(getString("size")), "cell 0 4");
		add(fontSizeSpinner, "cell 1 4, grow");
		add(startTimeBtn, "cell 0 5, w 160!");
		add(endTimeBtn, "cell 1 5, grow");
		add(textPosBtn, "cell 0 6, span 2, grow");
		ButtonGroup bgroup = new ButtonGroup();
		bgroup.add(addRadio);
		bgroup.add(removeRadio);
		bgroup.add(editRadio);
		add(addRadio, "cell 0 7, span 2, split 3, grow");
		add(removeRadio, "cell 0 7, grow");
		add(editRadio, "cell 0 7, grow");
		add(okBtn, "cell 0 8, span 2, grow");
		add(tableScroll, "cell 0 9, span 2, grow");
		add(previewBtn, "cell 0 10, grow");
		add(renderBtn, "cell 1 10, grow");
		
		State.getState().addColourListeners(fontLbl, colourLbl, sizeLbl, textScroll);
		
		registerListeners();
		
	}
	
	private void registerListeners() {
		startTimeBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (vamix.isMediaFile(vamix.getMediaName())){
					startTime = cp.getTime()/1000;
					startTimeBtn.setText(secsToString(startTime));
				}
			}
		});
		
		endTimeBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (vamix.isMediaFile(vamix.getMediaName())){
					endTime = cp.getTime()/1000;
					endTimeBtn.setText(secsToString(endTime));
				}
			}
		});
		
		//bring up the JColorChooser
		colourBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				cc.setVisible(true);
			}
		});
		
		fontOption.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				setFont();
			}
		});
		
		fontSizeSpinner.addChangeListener(new ChangeListener(){
			@Override
			public void stateChanged(ChangeEvent arg0) {
				setFont();
			}
		});
		
		textPosBtn.addActionListener(new ActionListener(){
			@Override
        	public void actionPerformed(ActionEvent arg0) {
				isSelecting = true;
				vamix.setCursorOnOverlay(true);
        	}
		});
		
		textTable.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
	        public void valueChanged(ListSelectionEvent event) {
	        	try {
		            Object[] data = tableModel.getFullData(textTable.getSelectedRow());
		            textArea.setText((String) data[0]);
					fontOption.setSelectedIndex((int) data[8]);
					colourBtn.setForeground(Color.decode((String) data[6]));
					fontSizeSpinner.setValue(data[7]);
					startTimeBtn.setText((String) tableModel.getValueAt(textTable.getSelectedRow(), 1));
					endTimeBtn.setText((String) tableModel.getValueAt(textTable.getSelectedRow(), 2));
					startTime = (long) data[1];
					endTime = (long) data[2];
					xPos = (int) data[3];
					yPos = (int) data[4];
					editRadio.setSelected(true);
	        	}catch (ArrayIndexOutOfBoundsException e){}
	        }
	    });
		
		okBtn.addActionListener(new ActionListener(){
			@Override
        	public void actionPerformed(ActionEvent arg0) {
				checkIfValid();
				String start = "00:00:00";
				String end = "00:00:10";
				if (!startTimeBtn.getText().equals(getString("setStart"))){
					start = startTimeBtn.getText();
				}
				if (!endTimeBtn.getText().equals(getString("setEnd"))){
					end = endTimeBtn.getText();
				}
		        String fontPath = getFontPath(fontOption.getSelectedItem().toString());
		    	String colour = toHexString(colourBtn.getBackground());
		        if (addRadio.isSelected()){
					tableModel.add(textArea.getText(), start, end);
					tableModel.addFullData(textArea.getText(), startTime, endTime, xPos, yPos, fontPath, 
							colour, Integer.parseInt(fontSizeSpinner.getValue().toString()), fontOption.getSelectedIndex());
		        }else if (editRadio.isSelected()){
		        	tableModel.edit(textTable.getSelectedRow(), textArea.getText(), start, end);
					tableModel.editFullData(textTable.getSelectedRow(), textArea.getText(), startTime, endTime, xPos, yPos, fontPath, 
							colour, Integer.parseInt(fontSizeSpinner.getValue().toString()), fontOption.getSelectedIndex());
		        }
				tableModel.fireTableDataChanged();
				
				//reset stuff
				startTimeBtn.setText(getString("setStart"));
				endTimeBtn.setText(getString("setEnd"));
				startTime = 0;
				endTime = 10;
        	}
		});
		
		//prompt the user to enter an output filename. Then add the text and save it.
		renderBtn.addActionListener(new ActionListener(){
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
					addTextToVideo("conv", outputFile);
		        }
        	}
        });
		
		//preview the text using a stream
		previewBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				addTextToVideo("preview", "");
			}
		});
	}
	
	private void checkIfValid() {
		if (textArea.getText().split("\\s").length > 20){
			JOptionPane.showMessageDialog(null, getString("tooMuchText"), "Error", JOptionPane.DEFAULT_OPTION);
			return;
		}else if (Integer.parseInt(fontSizeSpinner.getValue().toString()) > 72){
			JOptionPane.showMessageDialog(null, getString("tooBigText"), "Error", JOptionPane.DEFAULT_OPTION);
			return;
		}else if (endTime-startTime < 0){
			JOptionPane.showMessageDialog(null, getString("endLessThanStart"), "Error", JOptionPane.DEFAULT_OPTION);
			return;
		}
	}
	
	public void setFont(){
		Font font;
		try {
	        String fontPath = getFontPath(fontOption.getSelectedItem().toString());
			font = Font.createFont(Font.TRUETYPE_FONT, new File(fontPath)).deriveFont(Float.parseFloat(fontSizeSpinner.getValue().toString()));
			textArea.setFont(font);
		} catch (FontFormatException | IOException e) {
			JOptionPane.showMessageDialog(null, getString("fontUnavailable"));
		}
	}

	public void changeColour(Color colour){
		colourBtn.setBackground(colour);
		textArea.setForeground(colour);
	}
	
	/**
	 * This method is called when either the add text or preview text button is clicked
	 * This will use either avconv or avplay to add the text to the video.
	 * @oparam option - either convert or preview
	 * @param output - the user specified output file name
	 */
	public void addTextToVideo(String option, String output){
//		//get the duration and attributes for use in the progress bar
		int dur = GetAttributes.getDuration(vamix.getMediaName());
    	int frames = GetAttributes.getFrames(vamix.getMediaName());
    	
    	String cmd = "";
    	if (option.equals("conv")){
    		cmd = "avconv -y -i " + vamix.getMediaName() + " -vf \"";
    	} else {
    		cmd = "avplay -i " + vamix.getMediaName() + " -vf \"";
    	}

    	for (int i = 0; i < tableModel.getRowCount(); i++){
    		Object[] data = tableModel.getFullData(i);
    		String text = (String) data[0];
    		long startTime = (long) data[1];
    		long endTime = (long) data[2];
    		int x = (int) data[3];
    		int y = (int) data[4];
    		String fontPath = (String) data[5];
    		String colour = (String) data[6];
    		int size = (int) data[7];
    	
	    	String timeFunction = "gt(t," + startTime + ")*lt(t," + endTime + ")";
	        //write text firstly to file, so special characters can be used
	        try {
				BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(".text" + i, false), "utf-8"));
				writer.write(text);
				writer.close();
			} catch (UnsupportedEncodingException e) {
			} catch (FileNotFoundException e) {
			} catch (IOException e) {
			}
	        Path currentRelativePath = Paths.get("");
	        String currentAbsPath = currentRelativePath.toAbsolutePath().toString();
	        //write the command
	        cmd += "drawtext=fontfile='" + fontPath + "':textfile='" + currentAbsPath + "/.text" + i +
	        			"':x=" + x + ":y=" + y + ":fontsize=" + size + ":fontcolor=" + colour + 
	        			":draw='" + timeFunction + "'";
	        if (i != tableModel.getRowCount()-1){
	        	cmd += ",";
	        }
    	}
    	if (option.equals("conv")){
        	cmd += "\" -strict experimental -f mp4 -v debug " + output;
    	} else {
        	cmd += "\" -strict experimental";
    	}
        //only carry out the command if the video file is valid
        if (dur > 0 && frames > 0){
    		loadScreen = new LoadingScreen(vamix);
    		System.out.println(cmd);
	        worker = new VideoWorker(cmd, loadScreen.getProgBar(), frames, option, "Text", loadScreen);
	        if (option.equals("conv")){
				loadScreen.prepare();
				loadScreen.setWorker(worker);
	        }
	        worker.execute();
		}
        else{
			JOptionPane.showMessageDialog(null, getString("unsupportedFile"), getString("error"), JOptionPane.DEFAULT_OPTION);
        }
    	
	}
	/**Method comes from http://www.javacreed.com/how-to-get-the-hex-value-from-color/
	 * 
	 * @param colour
	 * @return the hex string of the colour
	 */
	public final static String toHexString(Color colour){
		  String hexColour = Integer.toHexString(colour.getRGB() & 0xffffff);
		  if (hexColour.length() < 6) {
		    hexColour = "000000".substring(0, 6 - hexColour.length()) + hexColour;
		  }
		  return "0x" + hexColour;
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
	 * This method is used to locate the input font file within the users computer
	 * @param fontName The font to be located
	 * @return Returns a string which is the path to the font file
	 */
	private String getFontPath(String fontName) {
		//use the fc-list command in linux
		String cmd = "fc-list | grep " + fontName + ".ttf";
		ProcessBuilder builder = new ProcessBuilder("/bin/bash", "-c", cmd);
		try {
			Process process = builder.start();
			process.waitFor();
			InputStream stdout = process.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(stdout));
			String line = br.readLine();
			return line.split(":")[0];
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, getString("errorOccurred"));
		} catch (InterruptedException e) {
			JOptionPane.showMessageDialog(null, getString("errorOccurred"));

		}
		return "";
	}	
	

	public ProjectSettings createProjectSettings() {
//        String duration = new DateEditor(timeForTextSpinner , "yy:mm:ss").getFormat().format(timeForTextSpinner.getValue());
//		
//		return ProjectFile.getInstance(vamix).new ProjectSettings(null, null, titleText, 
//				 creditsText,  titleOrCredits.getSelectedIndex(),  fontOption.getSelectedIndex(), toHexString(colourBtn.getBackground()), 
//				 (int)xSpinner.getValue(), (int)ySpinner.getValue(), 
//				 (Integer)fontSizeSpinner.getValue(),  duration);
		return null;
	}
	
	public void loadProjectSettings(ProjectSettings ps) {
//		fontOption.setSelectedIndex(f);
//		colourBtn.setBackground(Color.decode(ps._colour));
//		xSpinner.setValue(ps._x);
//		ySpinner.setValue(ps._y);
//		fontSizeSpinner.setValue(ps._fontSize);
//		String duration = ps._duration;
//		SimpleDateFormat format = new SimpleDateFormat("yy:mm:ss");
//		try {
//			Date d = (java.util.Date)format.parse(duration);
//			java.sql.Time time = new java.sql.Time(d.getTime());
//		    timeForTextSpinner.setValue(time);
//		} catch (ParseException e) {
//			JOptionPane.showMessageDialog(null, getString("invalidSettings"));
//		}

	}
	
	private String getString(String label){
		return LanguageSelector.getString(label);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (isSelecting){
			int x = GetAttributes.getWidth(vamix.getMediaName());
			int y = GetAttributes.getHeight(vamix.getMediaName());
			xPos = e.getPoint().x*x/Vamix.WIDTH;
			yPos = e.getPoint().y*y/Vamix.HEIGHT;
			isSelecting = false;
			vamix.setCursorOnOverlay(false);
		}
	}

	@Override
	public void mousePressed(MouseEvent e){ 	}

	@Override
	public void mouseReleased(MouseEvent e) {	}

	@Override
	public void mouseEntered(MouseEvent e) {	}

	@Override
	public void mouseExited(MouseEvent e) {	}
}
