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
		cc = new JColorChooser(Color.black);
		
		selectBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				parent.changeColour(cc.getColor());
				setVisible(false);
			}
		});
		
		add(cc, "wrap");
		add(selectBtn, "center");
		
		setVisible(false);
		this.pack();
	}
	
	private static String getString(String label){
		return LanguageSelector.getLanguageSelector().getString(label);
	}
}
