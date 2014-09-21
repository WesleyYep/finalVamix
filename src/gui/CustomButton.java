package gui;

import javax.swing.JButton;
import javax.swing.SwingConstants;

public class CustomButton extends JButton {

	/**
	 * Here to get rid of warnings
	 */
	private static final long serialVersionUID = 8400746613937178616L;

	public CustomButton() {
		super();
		initialiser();
	}
	
	
	public CustomButton(String text) {
		super(text);
		initialiser();
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
