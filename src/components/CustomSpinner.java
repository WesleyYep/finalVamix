package components;

import java.awt.Color;
import java.util.Calendar;

import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.JSpinner.DateEditor;

@SuppressWarnings("serial")
public class CustomSpinner extends JSpinner{
	
	public CustomSpinner(int defaultValue, SpinnerNumberModel model){
        setEditor(new JSpinner.NumberEditor(this , "00"));
        setModel(model);
        setValue(defaultValue);
        setOpaque(false);
        getEditor().setOpaque(false);
        ((JSpinner.NumberEditor)getEditor()).getTextField().setOpaque(false);
        getEditor().getComponent(0).setForeground(new Color(100,0,0));
	}
	
	public CustomSpinner(int defaultTime){
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, 100); //to allow it to go up to 99, rather than stop at 24
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, defaultTime);
        SpinnerDateModel timeModel = new SpinnerDateModel();
		timeModel.setValue(calendar.getTime());
		setModel(timeModel);
		DateEditor de = new DateEditor(this , "yy:mm:ss");
		setEditor(de);
		de.setOpaque(false);
        de.getTextField().setOpaque(false);
        getEditor().getComponent(0).setForeground(new Color(100,0,0));
	}
}
