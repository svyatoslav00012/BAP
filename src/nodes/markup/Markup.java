package nodes.markup;

import javafx.scene.control.TextField;
import javafx.scene.shape.Line;
import javafx.util.Pair;
import nodes.imViewContainer.ImViewContainer;

public class Markup {

	Line line;
	TextField length;
	MovingCircle startPoint;
	MovingCircle endPoint;

	public ImViewContainer getParent() {
		return (ImViewContainer) startPoint.getParent();
	}

	public Markup(Markup m){
		this();
		length.setText(m.getLength().getText());
		startPoint.setImageCenterX(m.getStartPoint().getImageCenterX());
		startPoint.setImageCenterY(m.getStartPoint().getImageCenterY());
		endPoint.setImageCenterX(m.getEndPoint().getImageCenterX());
		endPoint.setImageCenterY(m.getEndPoint().getImageCenterY());
		//updateCenterPosition_ImToReal();
	}

	public Markup() {

		length = new TextField();
		length.setOnKeyTyped(typed -> {
			if (!Character.isDigit(typed.getCharacter().charAt(0)))
				typed.consume();
		});
		length.setOnKeyReleased(released -> {
			if (getParent() != null)
				getParent().updateSizes();
		});
		length.getStyleClass().add("markup_length");
		length.setPrefWidth(70);

		startPoint = new MovingCircle(this);
		startPoint.getStyleClass().add("markup_circle");
		startPoint.setPrefSize(30, 30);
		endPoint = new MovingCircle(this);
		endPoint.getStyleClass().add("markup_circle");
		endPoint.setPrefSize(30, 30);

		line = new Line();
		line.getStyleClass().add("markup_line");
		line.startXProperty().addListener(((observable, oldValue, newValue) -> onLineResizeOrMove()));
		line.startYProperty().addListener(((observable, oldValue, newValue) -> onLineResizeOrMove()));
		line.endXProperty().addListener(((observable, oldValue, newValue) -> onLineResizeOrMove()));
		line.endYProperty().addListener(((observable, oldValue, newValue) -> onLineResizeOrMove()));
	}

	public void onLineResizeOrMove() {
		Pair mid = new Pair((line.getStartX() + line.getEndX()) / 2, (line.getStartY() + line.getEndY()) / 2);
		length.setLayoutX((double) mid.getKey() - length.getPrefWidth() / 2);
		length.setLayoutY((double) mid.getValue() - 50);

		if (getParent() != null)
			getParent().updateSizes();
	}

	public void onCirclesMove() {
		line.setStartX(startPoint.getLayoutX() + startPoint.getPrefWidth() / 2);
		line.setStartY(startPoint.getLayoutY() + startPoint.getPrefHeight() / 2);
		line.setEndX(endPoint.getLayoutX() + endPoint.getPrefWidth() / 2);
		line.setEndY(endPoint.getLayoutY() + endPoint.getPrefWidth() / 2);
	}

	public void updateCenterPosition_ImToReal() {
		startPoint.updateCenterPosition_ImToReal();
		endPoint.updateCenterPosition_ImToReal();
	}

	public void updateCenterPosition_RealToIm() {
		startPoint.updateCenterPosition_RealToIm();
		endPoint.updateCenterPosition_RealToIm();
	}

	public TextField getLength() {
		return length;
	}

	public double getLengthCoef() { // САНТИМЕТРИ НА (РЕАЛЬНІ) ПІКСЕЛІ
		try {
			return Integer.parseInt(length.getText()) /
					module(startPoint.getCenterX() - endPoint.getCenterX(),
							startPoint.getCenterY() - endPoint.getCenterY());
		} catch (Exception ignored) {
		}
		return 1;
	}

	public void addToParent(ImViewContainer parent, double x, double y) {
		parent.getStylesheets().add("/nodes/markup/markupStyle.css");
		startPoint.setLayoutX(x - startPoint.getWidth() / 2);
		startPoint.setLayoutY(y - startPoint.getHeight() / 2);
		line.setStartX(x);
		line.setStartY(y);
		line.setEndX(x);
		line.setEndY(y);
		endPoint.setLayoutX(x - endPoint.getWidth() / 2);
		endPoint.setLayoutY(y - endPoint.getHeight() / 2);
		parent.getChildren().addAll(startPoint, endPoint, line);
	}

	public void addAllToParent(ImViewContainer parent){
		parent.getChildren().addAll(startPoint, endPoint, line, length);
	}

	public void putStartPoint(ImViewContainer parent, double x, double y) {
		addToParent(parent, x, y);
		startPoint.setLayoutX(x - startPoint.getPrefWidth() / 2);
		startPoint.setLayoutY(y - startPoint.getPrefHeight() / 2);
		startPoint.updateCenterPosition_RealToIm();
		getParent().setOnMouseClicked(clicked -> putEndPoint(clicked.getX(), clicked.getY()));
		getParent().setOnMouseMoved(moved -> {
			endPoint.setLayoutX(moved.getX() - endPoint.getPrefWidth() / 2);
			endPoint.setLayoutY(moved.getY() - endPoint.getPrefHeight() / 2);
			endPoint.updateCenterPosition_RealToIm();
			moved.consume();
		});
	}

	public void putEndPoint(double x, double y) {
		getParent().setOnMouseMoved(moved -> {
		});
		getParent().setStyle("");
		getParent().getChildren().add(length);
		getParent().setOnMouseClicked(null);
		System.out.println(getParent());
	}

	public double getCoefX() {
		return getParent().getWidth() / getParent().getImage().getWidth();
	}

	public double getCoefY() {
		return getParent().getHeight() / getParent().getImage().getHeight();
	}

	public void removeFromParent() {
		getParent().getChildren().removeAll(startPoint, endPoint, length, line);
	}

	public MovingCircle getStartPoint() {
		return startPoint;
	}

	public MovingCircle getEndPoint() {
		return endPoint;
	}

	private double module(double x, double y) {
		return Math.sqrt(x * x + y * y);
	}
}