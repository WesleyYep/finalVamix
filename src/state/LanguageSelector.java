package state;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * This is used to set the language and set text of all the gui components based on the current language
 * @author wesley
 *
 */
public class LanguageSelector {
	private static ResourceBundle bundle;
	
	public static void setLanguage(String langCode1, String langCode2){
		Locale locale = new Locale(langCode1, langCode2);
		bundle = ResourceBundle.getBundle("Language", locale);
	}
	
	public static String getString(String label){
		return bundle.getString(label);
	}
	
}
