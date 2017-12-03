package stages.editSignboard;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import model.helpers.Helper;
import nodes.imViewContainer.ImViewContainer;
import nodes.markup.Markup;
import nodes.signboard.Signboard;
import view.nodes.wcbPane.WCBTypes;
import view.windows.stage.MyStage;

import java.util.ArrayList;
import java.util.List;

public class EditSignboardController {

	ImViewContainer testContainer;
	private Signboard signboard, cur;
	Markup testMarkup;

	@FXML
	private TextField widthField;
	@FXML
	private TextField heightField;
	@FXML
	private CheckBox boldBox;
	@FXML
	private CheckBox italicBox;
	@FXML
	private CheckBox underlinedBox;
	@FXML
	private TextField signTextField;
	@FXML
	private ColorPicker fontColorPicker;
	@FXML
	private ColorPicker backgroundColorPicker;
	@FXML
	private CheckBox autoLimitedChoiceBox;
	@FXML
	private ComboBox fontFamilyBox;
	@FXML
	private TextField fontSizeField;
	@FXML
	private AnchorPane anchorPane;
	@FXML
	private SplitPane splitPane;

	public void initialize() {
		testMarkup = new Markup();
		testContainer = new ImViewContainer();
		testContainer.setTestContainer(true);
		testContainer.addMarkup(testMarkup);
		splitPane.getItems().set(0, testContainer);
		fontFamilyBox.setItems(
				FXCollections.observableArrayList(
						getFontLabels(Font.getFamilies())
				)
		);
	}

	private ArrayList<Label> getFontLabels(List<String> families){
		ArrayList<Label> labels = new ArrayList<>();
		for(String s : families){
			labels.add(getFontLabel(s));
		}
		return labels;
	}

	public Label getFontLabel(String fontFamily){
		Label l = new Label(fontFamily);
		l.setStyle(SignboardOptions.FONT_FAMILY + "'" + fontFamily + "'");
		return l;
	}

	public void onAutoLimitedChoose(ActionEvent actionEvent) {
		signboard.getOptions().setAutoLimited(autoLimitedChoiceBox.isSelected());
		if(signboard.getOptions().isAutoLimited())
		while (testContainer.isHistoricalArea() && signboard.countSizeInMeters() > 1.0
				|| signboard.countSizeInMeters() > 3.0) {
			signboard.setPrefHeight(signboard.getPrefHeight() - 1);
			signboard.setPrefWidth(signboard.getPrefWidth() - 1);
			signboard.onSignboardResize();
		}
		signboard.saveCurBounds();
	}

	public void onFontFamilyChoose(ActionEvent actionEvent) {
		signboard.getOptions().getStyle().put(SignboardOptions.FONT_FAMILY, "'" + ((Label)fontFamilyBox.getValue()).getText() + "'");

		signboard.applyStyle();
	}


	public void checkDigits(KeyEvent mouseEvent) {
		if (!Character.isDigit(mouseEvent.getCharacter().charAt(0)))
			mouseEvent.consume();
	}

	public SignboardOptions getOptions() {
		return signboard.getOptions();
	}

	public void applyToAll(ActionEvent actionEvent) {
		cur.getImViewContainer().applyOptionsToAll(signboard.getOptions());
	}

	public void chooseBackgroundColor(ActionEvent actionEvent) {
		Color c = backgroundColorPicker.getValue();
		signboard.getOptions().getStyle().put(SignboardOptions.BACKGROUND_COLOR, "rgb(" + c.getRed() * 256 + ", " + c.getGreen() * 256 + ", " + c.getBlue() * 256 + ")");
		signboard.applyStyle();
	}

	public void chooseFontColor(ActionEvent actionEvent) {
		Color c = fontColorPicker.getValue();
		signboard.getOptions().getStyle().put(SignboardOptions.FONT_COLOR, "rgb(" + c.getRed() * 256 + ", " + c.getGreen() * 256 + ", " + c.getBlue() * 256 + ")");
		signboard.applyStyle();
	}


	public void onTextChanged(KeyEvent keyEvent) {
		signboard.setText(signTextField.getText());
	}

	public void onBoldChoose(ActionEvent actionEvent) {
		signboard.getOptions().getStyle().put(SignboardOptions.FONT_WEIGHT, boldBox.isSelected() ? "bold" : "200");
		signboard.applyStyle();
	}

	public void onItalicChoose(ActionEvent actionEvent) {
		signboard.getOptions().getStyle().put(SignboardOptions.FONT_STYLE, italicBox.isSelected() ? "italic" : "none");
		signboard.applyStyle();
	}

	public void onUnderlinedChoose(ActionEvent actionEvent) {
		signboard.getOptions().getStyle().put(SignboardOptions.FONT_UNDERLINED, underlinedBox.isSelected() ? "true" : "false");
		signboard.applyStyle();
	}

	public void update(Signboard s){
		cur = s;
		testContainer.setHistoricalArea(s.getImViewContainer().isHistoricalArea());
		signboard = new Signboard(s);
		testContainer.addSign(signboard);
		signboard.setOptions(s.getOptions());
		signboard.setLayoutX((testContainer.getPrefWidth() - signboard.getPrefWidth()) / 2);
		signboard.setLayoutY((testContainer.getPrefHeight() - signboard.getPrefHeight()) / 2);
		signboard.applyStyle();
		updateItems();
	}

	public void updateItems(){

		heightField.setText(getOptions().getActualHeight() + "");
		widthField.setText(getOptions().getActualWidth() + "");

		signTextField.setText(signboard.getText());
		fontSizeField.setText(getOptions().getStyle().get(SignboardOptions.FONT_SIZE));

		String fontColor = getOptions().getStyle().get(SignboardOptions.FONT_COLOR);
		double red = Helper.extractRedFromCssRGB(fontColor);
		if(red == 256)
			red--;
		double green = Helper.extractRedFromCssRGB(fontColor);
		if(green == 256)
			green--;
		double blue = Helper.extractBlueFromCssRGB(fontColor);
		if(blue == 256)
			blue--;
		fontColorPicker.setValue(Color.rgb((int)red, (int)green, (int)blue));

		fontFamilyBox.setValue(getFontLabel(getOptions().getStyle().get(SignboardOptions.FONT_FAMILY)));
		boldBox.setSelected(getOptions().getStyle().get(SignboardOptions.FONT_WEIGHT).equals("bold"));
		italicBox.setSelected(getOptions().getStyle().get(SignboardOptions.FONT_STYLE).equals("italic"));
		underlinedBox.setSelected(Boolean.parseBoolean(getOptions().getStyle().get(SignboardOptions.FONT_UNDERLINED)));

		String background = getOptions().getStyle().get(SignboardOptions.BACKGROUND_COLOR);
		red = Helper.extractRedFromCssRGB(background);
		if(red == 256)
			red--;
		green = Helper.extractGreenFromCssRGB(background);
		if(green == 256)
			green--;
		blue = Helper.extractBlueFromCssRGB(background);
		if(blue == 256)
			blue--;
		backgroundColorPicker.setValue(Color.rgb((int)red, (int)green, (int)blue));

		autoLimitedChoiceBox.setSelected(getOptions().isAutoLimited());
	}

	public void onHeightChanged(KeyEvent keyEvent) {
		signboard.getOptions().setActualHeight(heightField.getText().isEmpty() ? 0 : Integer.parseInt(heightField.getText()));
		signboard.updateSizeFromActual();
	}

	public void onWidthChanged(KeyEvent keyEvent) {
		signboard.getOptions().setActualWidth(widthField.getText().isEmpty() ? 0 : Integer.parseInt(widthField.getText()));
		signboard.updateSizeFromActual();
	}

	public void onFontSizeChanged(KeyEvent keyEvent) {
		if (!fontSizeField.getText().isEmpty())
			signboard.getOptions().getStyle().put(SignboardOptions.FONT_SIZE, fontSizeField.getText());
		signboard.applyStyle();
	}

	public static SignboardOptions showOptionsWindow(Signboard s) {
		MyStage stage = new MyStage("/stages/editSignboard/editSignboard1.fxml", WCBTypes.ICONIFIED, WCBTypes.MXMIZE, WCBTypes.CLOSE);
		((EditSignboardController) stage.getLoader().getController()).update(s);
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.animatingShowAndWait();
		SignboardOptions options = ((EditSignboardController) stage.getLoader().getController()).getOptions();
		s.setOptions(options);
		if(options.isApplyToAll())
			s.getImViewContainer().applyOptionsToAll(options);
		//s.applyStyle();
		s.setText(((EditSignboardController) stage.getLoader().getController()).signboard.getText());
		s.updateSizeFromActual();
		return options;
	}
}
