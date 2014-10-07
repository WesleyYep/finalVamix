package state;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

public class State {
	private List<JComponent> stateListeners;
	private List<JComponent> mouseListeners;

	
	public State(){
		stateListeners = new ArrayList<JComponent>();
		mouseListeners = new ArrayList<JComponent>();
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
	
	public void setTransparent(){
		for (JComponent comp : stateListeners){
			comp.setOpaque(false);
	  		comp.setBackground(new Color(0,0,0,10));
	  		comp.setFont(new Font("Sans", Font.BOLD, 16));
	  		comp.setForeground(Color.GRAY);
		}
	}
	
	public void setVisibility(boolean visible){
		for (JComponent comp : stateListeners){
	  		comp.setVisible(visible);
		}
	}
	
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
							comp.setVisible(false);
						}
				    }
				});
			}
		};
		queryThread.start();

	}
	
}
