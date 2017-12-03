package stages.editSignboard;

import model.helpers.ObjectCopying.DeepCopy;

import java.io.Serializable;
import java.util.Hashtable;

public class SignboardOptions implements Serializable{

	public static final String FONT_SIZE = "-fx-font-size: ";
	public static final String FONT_COLOR = "-fx-text-fill: ";
	public static final String FONT_FAMILY = "-fx-font-family: ";
	public static final String BACKGROUND_COLOR = "-fx-background-color: ";
	public static final String FONT_WEIGHT = "-fx-font-weight: ";
	public static final String FONT_STYLE = "-fx-font-style: ";
	public static final String FONT_UNDERLINED = "-fx-underline: ";

	private Hashtable<String, String> style;
	private boolean autoLimited = true;
	private boolean applyToAll;

	private int actualWidth = 100;
	private int actualHeight = 100;

	public SignboardOptions(){
		style = new Hashtable<>();
		style.put(FONT_SIZE, "12");
		style.put(FONT_COLOR, "rgb(0, 0, 0)");
		style.put(FONT_FAMILY, "Arial");
		style.put(BACKGROUND_COLOR, "rgb(255, 255, 255)");
		style.put(FONT_WEIGHT, "200");
		style.put(FONT_STYLE, "none");
		style.put(FONT_UNDERLINED, "false");
	}

	public Hashtable<String, String> getStyle() {
		return style;
	}

	public void setStyle(Hashtable<String, String> style) {
		this.style = style;
	}

	public boolean isAutoLimited() {
		return autoLimited;
	}

	public void setAutoLimited(boolean autoLimited) {
		this.autoLimited = autoLimited;
	}

	public boolean isApplyToAll() {
		return applyToAll;
	}

	public void setApplyToAll(boolean applyToAll) {
		this.applyToAll = applyToAll;
	}

	public static SignboardOptions copyOptions(SignboardOptions options){
		return (SignboardOptions)DeepCopy.copy(options);
	}

	public static Hashtable<String, String> copyStyle(Hashtable<String, String> style){
		return (Hashtable<String, String>)DeepCopy.copy(style);
	}

	public static Hashtable<String, String> copyStyle(SignboardOptions options){
		return (Hashtable<String, String>)DeepCopy.copy(options.getStyle());
	}

	public int getActualWidth() {
		return actualWidth;
	}

	public void setActualWidth(int actualWidth) {
		this.actualWidth = actualWidth;
	}

	public int getActualHeight() {
		return actualHeight;
	}

	public void setActualHeight(int actualHeight) {
		this.actualHeight = actualHeight;
	}
}
