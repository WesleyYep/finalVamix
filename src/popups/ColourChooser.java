package popups;

import gui.TextSection;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JFrame;

import state.LanguageSelector;
import net.miginfocom.swing.MigLayout;
/**
 * Simple colour picker that sends the colour to the text section
 * @author Wesley
 *
 */
@SuppressWarnings("serial")
public class ColourChooser extends JFrame{
	private JColorChooser cc;
	private JButton selectBtn = new JButton(getString("select"));
	private TextSection parent;

	public ColourChooser(TextSection textSection) {
		super(getString("colourChooser"));
		parent = textSection;
		setLocation(500, 250);
		setLayout(new MigLayout());
		//default colour is black
		cc = new JColorChooser(Color.black);
		
		//tell the text section to change colour of the text area
		selectBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				parent.changeColour(cc.getColor());
				setVisible(false);
			}
		});
		
		//add the colour chooser as well as the select button to the frame
		add(cc, "wrap");
		add(selectBtn, "center");
		
		setVisible(false);
		this.pack();
	}
	
	/**
	 * This method gets the string that is associated with each label, in the correct language
	 * @param label
	 * @return the string for this label
	 */
	private static String getString(String label){
		return LanguageSelector.getString(label);
	}
}
