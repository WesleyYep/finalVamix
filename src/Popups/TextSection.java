package Popups;

import editing.TextWorker;
import gui.EditorPanel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

@SuppressWarnings("serial")
public class TextSection extends JPanel{
	
	private JTextField textTextField = new JTextField();
	private JButton addTextBtn = new JButton("Add text to Video");
	private JButton fontBtn = new JButton("Font");
	private JButton sizeBtn = new JButton("Size");
	private JButton colourBtn = new JButton("Colour");
	
	private EditorPanel editorPanel;
	
	private LoadingScreen loadScreen = new LoadingScreen();

	public TextSection(EditorPanel ep) {
		
		editorPanel = ep;
		
		setLayout(new MigLayout());
		add(textTextField, "grow, wrap");
		add(fontBtn, "grow, split 3");
		add(sizeBtn, "grow");
		add(colourBtn, "grow, wrap");
		add(addTextBtn, "grow");
		
		
		addTextBtn.addActionListener(new ActionListener(){
        	@Override
        	public void actionPerformed(ActionEvent arg0) {
				final JFileChooser fc = new JFileChooser();
		        fc.showSaveDialog(fc);
		        if (fc.getSelectedFile() != null){
		            String outputFile = fc.getSelectedFile().getAbsolutePath().toString();
		            String cmd = "avconv -i " + editorPanel.getMediaName() + " -vf \"drawtext="
		            		+ "fontfile='/usr/share/fonts/truetype/dejavu/DejaVuSans.ttf':text='" +
        						textTextField.getText() + "':x=0:y=0:fontsize=24:fontcolor=red\" "
        								+ "-strict experimental -f mp4 -v debug " + outputFile;
//		            String cmd = "avconv -i " + editorPanel.getMediaName() + " -vf \"drawtext="
//		            		+ "fontfile='/usr/share/fonts/truetype/ttf-dejavu/DejaVuSans.ttf':text='" +
//        						textTextField.getText() + "':x=0:y=0:fontsize=24:fontcolor=red\" "
//        								+ "-strict experimental -f mp4 -v debug " + outputFile;
		            loadScreen.prepare();
        			TextWorker worker = new TextWorker(cmd, loadScreen.getProgBar());
		            worker.execute();
		        }
        	}
        });
		
	}
	

}
