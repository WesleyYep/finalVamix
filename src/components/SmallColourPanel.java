package components;

import gui.Vamix;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import state.State;

/**
 * This is the colour panel that is used in the bottom toolbar
 * @author Wesley
 *
 */
@SuppressWarnings("serial")
public class SmallColourPanel extends JPanel implements ActionListener{
	private JButton redButton = new JButton();
	private JButton greenButton = new JButton();
	private JButton blueButton = new JButton();
	private JButton yellowButton = new JButton();
	private JButton greyButton = new JButton();
	private JButton whiteButton = new JButton();
	private JButton blackButton = new JButton();
	
	public SmallColourPanel(Vamix ep){
		//sets the background of all buttons
		redButton.setBackground(Color.RED);
		greenButton.setBackground(Color.GREEN);
		blueButton.setBackground(Color.BLUE);
		yellowButton.setBackground(Color.YELLOW);
		greyButton.setBackground(Color.GRAY);
		whiteButton.setBackground(Color.WHITE);
		blackButton.setBackground(Color.BLACK);
		setBackground(Color.BLACK);
		//now add all buttons to the panel
		add(redButton);
		add(greenButton);
		add(blueButton);
		add(yellowButton);
		add(greyButton);
		add(whiteButton);
		add(blackButton);
		//ensure that all the buttons will do something
		redButton.addActionListener(this);
		greenButton.addActionListener(this);
		blueButton.addActionListener(this);
		yellowButton.addActionListener(this);
		greyButton.addActionListener(this);
		whiteButton.addActionListener(this);
		blackButton.addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		//change the state of the program depending on the colour of the button that was clicked.
		State.getState().changeForeground(((JButton)e.getSource()).getBackground());
	}
	
}
