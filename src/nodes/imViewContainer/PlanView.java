package nodes.imViewContainer;

import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import model.helpers.Helper;
import nodes.TwoDirectionArrow;
import nodes.signboard.Signboard;
import stages.editSignboard.SignboardOptions;

import java.util.ArrayList;

public class PlanView extends ImViewContainer {

	private double sourceLengthCoef;

	public PlanView(ImViewContainer container) {
		sourceLengthCoef = container.lengthCoef();
		setPrefWidth(container.getMaxSignWidth() + 600);
		copySigns(container.getSigns());
		drawAnnotations();
	}

	public double lengthCoef() {
		return sourceLengthCoef;
	}

	public double sizeCoef() {
		return 1;
	}

	public void copySigns(ArrayList<Signboard> signboards) {
		for (Signboard s : signboards) {
			Signboard copy = new Signboard(s);
			addSignToPlan(copy);
			this.getSigns().add(copy);
		}
	}

	public void addSignToPlan(Signboard s) {
		int y = 100;
		for(Signboard prev: getSigns())
			y += prev.getImageHeight() + 200;
		setPrefHeight( y + s.getImageHeight() + 100);
		getChildren().add(s);
		s.updateBounds_ImageToReal();
		s.setLayoutX(200);
		s.setLayoutY(y);
	}

	public void drawAnnotations() {
		getStyleClass().add("imViewContainer");
		getStylesheets().add("/nodes/imViewContainer/patternStyle.css");

		for (Signboard s : getSigns()) {
			drawStrokes(s);
			drawArrows(s);
			drawSize(s);
			drawOptions(s);
		}
	}

	public void drawStrokes(Signboard s) {
		Line line1 = new Line();
		line1.setStartX(s.getImageX());
		line1.setStartY(s.getImageY() + s.getImageHeight() + 30);
		line1.setEndX(s.getImageX());
		line1.setEndY(s.getImageY() - 30);
		line1.getStyleClass().add("punktir");
		//line1.setStrokeWidth(2);

		Line line2 = new Line();
		line2.setStartX(s.getImageX() - 30);
		line2.setStartY(s.getImageY());
		line2.setEndX(s.getImageX() + s.getImageWidth() + 30);
		line2.setEndY(s.getImageY());
		line2.getStyleClass().add("punktir");
		//line2.setStrokeWidth(2);

		Line line3 = new Line();
		line3.setStartX(s.getImageX() - 30);
		line3.setStartY(s.getImageY() + s.getImageHeight());
		line3.setEndX(s.getImageX() + s.getImageWidth() + 30);
		line3.setEndY(s.getImageY() + s.getImageHeight());
		line3.getStyleClass().add("punktir");
		//line3.setStrokeWidth(2);

		Line line4 = new Line();
		line4.setStartX(s.getImageX() + s.getImageWidth());
		line4.setStartY(s.getImageY() + s.getImageHeight() + 30);
		line4.setEndX(s.getImageX() + s.getImageWidth());
		line4.setEndY(s.getImageY() - 30);
		line4.getStyleClass().add("punktir");
		//line4.setStrokeWidth(2);

		getChildren().addAll(line1, line2, line3, line4);
	}

	public void drawArrows(Signboard s) {
		TwoDirectionArrow vertical = new TwoDirectionArrow(
				s.getImageX() - 15,
				s.getImageY() + 5,
				s.getImageX() - 15,
				s.getImageY() + s.getImageHeight() - 5
		);

		TwoDirectionArrow horizontal = new TwoDirectionArrow(
				s.getImageX() + 5,
				s.getImageY() - 15,
				s.getImageX() + s.getImageWidth() - 5,
				s.getImageY() - 15
		);
		getChildren().addAll(vertical, horizontal);
	}

	public void drawSize(Signboard s) {
		Label labelWidth = new Label(s.getOptions().getActualWidth() + " см");
		Label labelHeight = new Label(s.getOptions().getActualHeight() + " см");
		labelHeight.getStyleClass().add("sizeLabel");
		labelWidth.getStyleClass().add("sizeLabel");

		labelWidth.setLayoutX(s.getImageX() + (s.getImageWidth() - 50) / 2);
		labelWidth.setLayoutY(s.getImageY() - 20 - 20);
		labelHeight.setLayoutX(s.getImageX() - 60 - 20);
		labelHeight.setLayoutY(s.getImageY() + (s.getImageHeight() - 30) / 2);

		getChildren().addAll(labelHeight, labelWidth);
	}

	public void drawOptions(Signboard s) {

		Label fontFamily1 = new Label("Шрифт:");
		Label fontFamily2 = new Label(s.getOptions().getStyle().get(SignboardOptions.FONT_FAMILY));
		fontFamily2.setStyle(SignboardOptions.FONT_FAMILY + "'" + fontFamily2.getText() + "'");

		Label fontColor1 = new Label("Колір шрифту:");
		Label fontColor2 = new Label(s.getOptions().getStyle().get(SignboardOptions.FONT_COLOR));
		Label fontColor3 = new Label("     ");
		fontColor3.setStyle(SignboardOptions.BACKGROUND_COLOR + fontColor2.getText()
				+ "; -fx-border-color: black; -fx-border-width: 1px;");
		fontColor2.setText(Helper.makeDoubleShorter(fontColor2.getText()));

		Label backgroundColor1 = new Label("Колір фону:");
		Label backgroundColor2 = new Label(s.getOptions().getStyle().get(SignboardOptions.BACKGROUND_COLOR));
		Label backgroundColor3 = new Label("     ");
		backgroundColor3.setStyle(SignboardOptions.BACKGROUND_COLOR + backgroundColor2.getText()
		+ "; -fx-border-color: black; -fx-border-width: 1px;");
		backgroundColor2.setText(Helper.makeDoubleShorter(backgroundColor2.getText()));

		Label square = new Label("Площа: " + s.countSquare() + " м. кв.");

		HBox fontFamily = new HBox(10, fontFamily1, fontFamily2);
		HBox fontColor = new HBox(10, fontColor1, fontColor2, fontColor3);
		HBox backgroundColor = new HBox(10, backgroundColor1, backgroundColor2, backgroundColor3);
		VBox options = new VBox(fontFamily, fontColor, backgroundColor, square);

		options.setLayoutX(s.getImageX() + s.getImageWidth() + 30);
		options.setLayoutY(s.getImageY() - 5);
		options.getStyleClass().add("sizeLabel");
		getChildren().add(options);
	}

}
