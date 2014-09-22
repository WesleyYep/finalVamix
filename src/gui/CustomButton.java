package gui;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.SwingConstants;

/**
 * This class is used to create buttons that display an image and text only
 * @author Mathew and Wesley
 *
 */
@SuppressWarnings("serial")
public class CustomButton extends JButton {

	public CustomButton() {
		super();
		initialiser();
	}
	
	
	public CustomButton(String text) {
		super(text);
		initialiser();
	}
	
	public CustomButton(String text, Icon image) {
		super(text);
		initialiser();
		setIcon(image);
	}
	
	void initialiser() {
		setOpaque(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setVerticalTextPosition(SwingConstants.BOTTOM);
        setHorizontalTextPosition(SwingConstants.CENTER);
	}
	
	
}
