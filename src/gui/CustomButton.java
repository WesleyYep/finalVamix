package gui;

import java.awt.Image;
import java.awt.Insets;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.SwingConstants;

/**
 * This class is used to create buttons that display an image and text only
 * @author Mathew and Wesley
 *
 */
@SuppressWarnings("serial")
public class CustomButton extends JButton {

	private ImageIcon _mainImage;
	private ImageIcon _secondImage;
	private Boolean _mainInUse = true;
	
	public CustomButton() {
		super();
		initialiser();
	}
	
	
	public CustomButton(String text) {
		super(text);
		initialiser();
	}
	
	public CustomButton(ImageIcon image, int width, int height) {
		super();
		initialiser();
		Image scaledImage = image.getImage().getScaledInstance( width, height,  java.awt.Image.SCALE_SMOOTH );
		setIcon(new ImageIcon(scaledImage));
		setMargin(new Insets(0,0,0,0));
		setSize(width, height);
	}
	
	public CustomButton(ImageIcon image, int width, int height, ImageIcon secondImage) {
		super();
		initialiser();
		Image scaledImage = image.getImage().getScaledInstance( width, height,  java.awt.Image.SCALE_SMOOTH );
		_mainImage = new ImageIcon(scaledImage);
		setIcon(_mainImage);
		scaledImage = secondImage.getImage().getScaledInstance( width, height,  java.awt.Image.SCALE_SMOOTH );
		_secondImage = new ImageIcon(scaledImage);
		setMargin(new Insets(0,0,0,0));
		setSize(width, height);
	}
	
	public void changeIcon() {
		if (_secondImage != null) {
			if (_mainInUse) {
				setIcon(_secondImage);
				_mainInUse = false;
			} else {
				setIcon(_mainImage);
				_mainInUse = true;
			}
		}
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
