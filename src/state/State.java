package state;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import components.CustomSpinner;

/**
 * This represents the state of the gui.
 * It is used to show/hide the gui features, and change the colour.
 * @author wesley
 *
 */
public class State {
	private static State state;
	private List<JComponent> stateListeners;
	private List<JComponent> mouseListeners;
	private List<JComponent> colourListeners;
	private List<CustomSpinner> spinnerColourListeners;
	
	private State(){
		stateListeners = new ArrayList<JComponent>();
		mouseListeners = new ArrayList<JComponent>();
		colourListeners = new ArrayList<JComponent>();
		spinnerColourListeners = new ArrayList<CustomSpinner>();

	}
	
	public static State getState(){
		if (state == null){
			state = new State();
			return state;
		} else {
			return state;
		}
	}
	
	public void addStateListeners(JComponent ... components){
		for (JComponent comp : components){
			stateListeners.add(comp);
		}
	}
	
	public void addMouseListeners(JComponent ... components){
		for (JComponent comp : components){
			mouseListeners.add(comp);
		}
	}
	
	public void addColourListeners(JComponent ... components){
		for (JComponent comp : components){
			colourListeners.add(comp);
		}
	}
	
	public void addSpinnerListeners(CustomSpinner ... spinners){
		for (CustomSpinner spinner : spinners){
			spinnerColourListeners.add(spinner);
		}
	}
	
	/**
	 * This makes everything transparent
	 */
	public void setTransparent(){
		for (JComponent comp : colourListeners){
			comp.setOpaque(false);
	  		comp.setBackground(new Color(0,0,0,10));
	  		comp.setFont(new Font("Sans", Font.BOLD, 16));
	  		comp.setForeground(new Color(255,0,0));
		}
	}
	
	/**
	 * Hide all the editing features if user chooses to
	 * @param visible
	 */
	public void setVisibility(boolean visible){
		for (JComponent comp : stateListeners){
	  		comp.setVisible(visible);
		}
	}
	
	/**
	 * If the gui is invisible, make certain important gui components appear if the mouse is moved
	 * Then they will disappear after 3 seconds
	 */
	public void showMouseControls(){
		final List<JComponent> temp = new ArrayList<JComponent>();
		for (JComponent comp : mouseListeners){
			if (!comp.isVisible()){
				comp.setVisible(true);
				temp.add(comp);
			}
		}
		Thread queryThread = new Thread() {
			public void run() {
				try{
					Thread.sleep(3000);
		        }
		        catch (InterruptedException e) { }
				SwingUtilities.invokeLater(new Runnable() {
				    public void run() {
				    	for (JComponent comp : temp){
				    		if (!stateListeners.get(0).isVisible())//check if everything else is visible or not
				    			comp.setVisible(false);
						}
				    }
				});
			}
		};
		queryThread.start();

	}
	
	/**
	 * This is used to change the foreground text
	 * @param colour the colour to change it to
	 */
	public void changeForeground(Color colour){
		for (JComponent comp : colourListeners){
	  		comp.setForeground(colour);
		}
		for (CustomSpinner spinner : spinnerColourListeners){
			spinner.getEditor().getComponent(0).setForeground(colour);
		}
	}
	

}
