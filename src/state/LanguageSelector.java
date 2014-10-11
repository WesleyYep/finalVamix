package state;

import java.util.Locale;
import java.util.ResourceBundle;

public class LanguageSelector {
	private static LanguageSelector languageSelector;
	private ResourceBundle bundle;
	private LanguageSelector(){
		bundle = ResourceBundle.getBundle("Language");
	}
	
	public static LanguageSelector getLanguageSelector(){
		if (languageSelector == null){
			languageSelector = new LanguageSelector();
			return languageSelector;
		} else {
			return languageSelector;
		}
	}
	
	public void setLanguage(String langCode1, String langCode2){
		Locale locale = new Locale(langCode1, langCode2);
		bundle = ResourceBundle.getBundle("Language", locale);
	}
	
	public String getString(String label){
		return bundle.getString(label);
	}
	
}
