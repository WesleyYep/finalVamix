package components;

import java.awt.Image;
import java.awt.Insets;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.SwingConstants;

/**
 * This class is used to create buttons that display an image and text.
 * The buttons support possible icon changing.
 * @author Mathew and Wesley
 *
 */
@SuppressWarnings("serial")
public class CustomButton extends JButton {
	private ImageIcon _mainImage;
	private ImageIcon _secondImage;
	private Boolean _mainInUse = true;
	
	/**
	 * This constructor creates a blank button
	 */
	public CustomButton() {
		super();
		initialiser();
	}
	
	/**
	 *  This constructor creates a button with just text
	 */
	public CustomButton(String text) {
		super(text);
		initialiser();
	}
	
	/**
	 * This constructor creates a button that contains an image
	 * @param image the icon representing the image
	 */
	public CustomButton(ImageIcon image, int width, int height) {
		super();
		initialiser();
		Image scaledImage = image.getImage().getScaledInstance( width, height,  java.awt.Image.SCALE_SMOOTH );
		setIcon(new ImageIcon(scaledImage));
		setMargin(new Insets(0,0,0,0));
		setSize(width, height);
	}
	
	/**
	 * This constructor creates a button that can change between two images.
	 * The images will toggle each time the button is clicked
	 * @param image the default image
	 * @param secondImage the image that the button will switch to, upon clicking
	 */
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
	
	/**
	 * This creates a button with a single image and also text
	 */
	public CustomButton(String text, ImageIcon image, int width, int height) {
		super(text);
		initialiser();
		Image scaledImage = image.getImage().getScaledInstance( width, height,  java.awt.Image.SCALE_SMOOTH );
		_mainImage = new ImageIcon(scaledImage);
		setIcon(_mainImage);
		setMargin(new Insets(0,0,0,0));
		setSize(width, height);
	}
	
	/**
	 * call this method to toggle the icon of the button
	 */
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
	
	/**
	 * This method forces the button to display its second icon
	 */
	public void forceSecondIcon() {
		setIcon(_secondImage);
		_mainInUse = false;
	}
	
	/**
	 * This method is used to check if the main icon or the secondary icon is in use
	 * @return true if the main (default) icon is in use
	 */
	public boolean mainInUse(){
		return _mainInUse;
	}
	
	/**
	 * Initialises the button.
	 */
	void initialiser() {
		setOpaque(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setVerticalTextPosition(SwingConstants.BOTTOM);
        setHorizontalTextPosition(SwingConstants.CENTER);
	}
	
	
}
