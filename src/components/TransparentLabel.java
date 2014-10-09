package components;

import java.awt.Color;

import javax.swing.JLabel;

public class TransparentLabel extends JLabel{
	
	public TransparentLabel(String caption){
		super(caption);
		this.setForeground(new Color(100,0,0));
	}
	
}
