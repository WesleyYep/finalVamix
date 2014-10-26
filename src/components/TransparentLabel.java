package components;

import java.awt.Color;

import javax.swing.JLabel;

/**
 * A simple JLabel that is transparent, and has initially a red foreground colour
 * @author wesley
 *
 */
@SuppressWarnings("serial")
public class TransparentLabel extends JLabel{
	
	public TransparentLabel(String caption){
		super(caption);
		this.setForeground(new Color(255,0,0));
	}
	
}
