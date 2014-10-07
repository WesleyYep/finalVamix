package popups;

import gui.TextSection;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;

import net.miginfocom.swing.MigLayout;

@SuppressWarnings("serial")
public class ColourChooser extends JFrame{
	private JLabel title = new JLabel("Working hard...");
	private JColorChooser cc;
	private JButton selectBtn = new JButton("Select");
	private TextSection parent;

	public ColourChooser(TextSection textSection) {
		super("Colour Chooser");
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
		
		add(title, "center, wrap");
		add(cc, "wrap");
		add(selectBtn, "center");
		
		setVisible(false);
		this.pack();
	}
}
