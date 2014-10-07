package state;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;

public class State {
	private List<JComponent> stateListeners;
	
	public State(){
		stateListeners = new ArrayList<JComponent>();
	}
	
	public void addStateListeners(JComponent ... components){
		for (JComponent comp : components){
			stateListeners.add(comp);
		}
	}
	
	public void setTransparent(){
		for (JComponent comp : stateListeners){
			comp.setOpaque(false);
	  		comp.setBackground(new Color(0,0,0,10));
	  		comp.setFont(new Font("Sans", Font.BOLD, 16));
	  		comp.setForeground(Color.LIGHT_GRAY);
		}
	}
}
