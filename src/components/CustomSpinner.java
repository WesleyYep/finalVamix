package components;

import java.awt.Color;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

/**
 * This is a custom spinner that is transparent and has a set foreground colour.
 * It  will always increment/decrement by one at a time
 * 
 * @author Wesley
 *
 */
@SuppressWarnings("serial")
public class CustomSpinner extends JSpinner{
	/**
	 * This is used if it is a number spinner
	 * @param defaultValue default value of spinner
	 * @param model the number model that contains information about max,min,etc.
	 */
	public CustomSpinner(int defaultValue, SpinnerNumberModel model){
        setEditor(new JSpinner.NumberEditor(this , "00"));
        setModel(model);
        setValue(defaultValue);
        setOpaque(false);
        getEditor().setOpaque(false);
        ((JSpinner.NumberEditor)getEditor()).getTextField().setOpaque(false);
        getEditor().getComponent(0).setForeground(new Color(255,0,0));
	}	
}
