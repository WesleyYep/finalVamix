package components;

import java.awt.Color;
import java.util.Date;
import java.util.Calendar;

import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.JSpinner.DateEditor;

/**
 * This is a custom spinner that could be either a date spinner or a number spinner.
 * It is transparent and will always increment/decrement by one at a time
 * 
 * @author wesley
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
	
	/**
	 * This is used if its a date spinner
	 * @param defaultTime the default seconds (usually 0 or 20)
	 */
	public CustomSpinner(int defaultTime){
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, 100); //to allow it to go up to 99, rather than stop at 24
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, defaultTime);
        final SpinnerDateModel timeModel = new SpinnerDateModel(){
        	@Override 
            public Object getNextValue() {
                Date date = this.getDate();
                long millis = date.getTime();
                return new Date(millis+1000);
            }
            @Override 
            public Object getPreviousValue() {
                Date date = this.getDate();
                long millis = date.getTime();
                return new Date(millis-1000);
            }
        };
		timeModel.setValue(calendar.getTime());
		setModel(timeModel);
		DateEditor de = new DateEditor(this , "yy:mm:ss");
		setEditor(de);
		de.setOpaque(false);
        de.getTextField().setOpaque(false);
        getEditor().getComponent(0).setForeground(new Color(255,0,0));
	}
	
}
