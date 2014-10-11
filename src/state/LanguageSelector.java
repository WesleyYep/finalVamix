package state;

import java.util.Locale;
import java.util.ResourceBundle;

public class LanguageSelector {
	private static ResourceBundle bundle;
	
	public static void setLanguage(String langCode1, String langCode2){
		Locale locale = new Locale(langCode1, langCode2);
		// print this locale
	      System.out.println("Locale1:" + locale);

	      // print the country of this locale
	      System.out.println("Country:" + locale.getCountry());
		bundle = ResourceBundle.getBundle("Language", locale);
		System.out.println("set" + bundle.getLocale());
	}
	
	public static String getString(String label){
		return bundle.getString(label);
	}
	
}
