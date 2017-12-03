package nodes.signboard;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import nodes.imViewContainer.ImViewContainer;
import stages.editSignboard.EditSignboardController;
import stages.editSignboard.SignboardOptions;

import java.io.Serializable;
import java.util.ArrayList;


public class Signboard extends Pane implements Serializable {

	double x = -1, y = -1;
	private int prevImageX, prevImageY, prevImageWidht, prevImageHeight;
	private int imageX, imageY, imageWidth, imageHeight;

	private SignboardOptions options;
	private ContextMenu contextMenu;

	private Label size;
	private Label sign;
	private Button edit;

	private Timeline showEdit;
	private Timeline hideEdit;

	private ArrayList<ResizingCircle> resizingCircles;

	public Signboard() {

	}

	public Signboard(Signboard s) {
		init();
		imageX = s.getImageX();
		imageY = s.getImageY();
		imageWidth = s.getImageWidth();
		imageHeight = s.getImageHeight();
		//setPrefWidth(s.getPrefWidth());
		//setPrefHeight(s.getPrefHeight());
		setOptions(s.getOptions());
		sign.setText(s.getText());
		applyStyle();
	}

	public ImViewContainer getImViewContainer() {
		return (ImViewContainer) getParent();
	}

	public void init() {

		options = new SignboardOptions();
		getStylesheets().add("/nodes/signboard/signboardStyle.css");
		getStyleClass().add("rectangle");
		applyStyle();

		addResizeCircles();
		addNodes();
		addCM();

		//addEventFilter(MouseEvent.MOUSE_DRAGGED, dr -> updateImageSizeAndBounds());
		heightProperty().addListener(((observable, oldValue, newValue) -> onSignboardResize()));
		widthProperty().addListener(((observable, oldValue, newValue) -> onSignboardResize()));

		initAnim();

		setOnMousePressed(this::onPressed);
		setOnMouseDragged(this::onDragged);
		setOnMouseReleased(this::onReleased);
	}

	public String getText() {
		return sign.getText();
	}

	public void setText(String text) {
		sign.setText(text);
	}

	private void initAnim() {
		showEdit = new Timeline(new KeyFrame(
				Duration.millis(200),
				new KeyValue(edit.layoutYProperty(), 0),
				new KeyValue(edit.opacityProperty(), 1.0)
		));
		hideEdit = new Timeline(new KeyFrame(
				Duration.millis(300),
				new KeyValue(edit.layoutYProperty(), -20),
				new KeyValue(edit.opacityProperty(), 0.0)
		));
		setOnMouseEntered(entered -> {
			if (showEdit.getStatus() != Animation.Status.RUNNING)
				showEdit.play();
		});
		setOnMouseExited(exited -> hideEdit.play());
	}

	private void addCM() {
		MenuItem delete = new MenuItem("Delete");
		delete.setOnAction(remove -> getImViewContainer().removeSign(this));
		contextMenu = new ContextMenu(delete);
		setOnContextMenuRequested(showContext -> contextMenu.show(this, showContext.getScreenX(), showContext.getScreenY()));
	}

	public Signboard(int imageX, int imageY, int imageWidth, int imageHeight) {
		//super();
		this.imageX = imageX;
		this.imageY = imageY;
		this.imageWidth = imageWidth;
		this.imageHeight = imageHeight;
		//update
		init();
	}

	public void onSignboardResize() {
		System.out.println("RESIZE");
		for (ResizingCircle r : resizingCircles)
			r.stayOnThePosition();
		setSizeLabel();
		sign.setPrefWidth(getPrefWidth());
		sign.setPrefHeight(getPrefHeight());
		edit.setLayoutX(getPrefWidth() - 30);

	}

	private void addNodes() {
		size = new Label();
		AnchorPane.setTopAnchor(size, 0.0);
		AnchorPane.setLeftAnchor(size, 0.0);
		size.setLayoutX(10);
		size.setLayoutY(0);
		size.getStyleClass().add("size");

		sign = new Label("ВАША ВИВІСКА");
		sign.setAlignment(Pos.CENTER);
		sign.setLayoutX(0);
		sign.setLayoutY(0);
		sign.setPrefWidth(getPrefWidth());
		sign.setPrefHeight(getPrefHeight());
		sign.getStyleClass().add("sign");

		edit = new Button();
		edit.getStyleClass().add("editButton");
		edit.setPrefSize(20, 20);
		edit.setLayoutX(getPrefWidth() - 20);
		edit.setLayoutY(-20);
		edit.setOpacity(0.0);
		edit.setOnAction(action -> {
			SignboardOptions sign = EditSignboardController.showOptionsWindow(this);
			hideEdit.play();
		});
		getChildren().addAll(size, sign, edit);
	}

	public void setSizeLabel(String text) {
		size.setText(text);
	}

	public void setSizeLabel() {
		Double square = countSquare();
		size.getStyleClass().remove("toBig");
		if (square > 3 || getImViewContainer().isHistoricalArea() && square > 1)
			size.getStyleClass().add("toBig");
		size.setText(square + " кв. м.");
	}


	private void addResizeCircles() {

		//addEventFilter(MouseEvent.ENTERED);
		onMouseMovedProperty().addListener((observable, newValue, oldValue) -> {
		});
		focusedProperty().addListener((observable, newValue, oldValue) -> {
			for (ResizingCircle r : resizingCircles)
				r.setVisible(observable.getValue());
		});

		resizingCircles = new ArrayList<>();

		resizingCircles.add(new ResizingCircle(CirclePosition.TOP_LEFT));
		resizingCircles.add(new ResizingCircle(CirclePosition.TOP_MID));
		resizingCircles.add(new ResizingCircle(CirclePosition.TOP_RIGHT));
		resizingCircles.add(new ResizingCircle(CirclePosition.CENTER_LEFT));
		resizingCircles.add(new ResizingCircle(CirclePosition.CENTER_RIGHT));
		resizingCircles.add(new ResizingCircle(CirclePosition.BOTTOM_LEFT));
		resizingCircles.add(new ResizingCircle(CirclePosition.BOTTOM_MID));
		resizingCircles.add(new ResizingCircle(CirclePosition.BOTTOM_RIGHT));

		getChildren().addAll(resizingCircles);
	}

	private void onPressed(MouseEvent e) {
		requestFocus();
		x = e.getX();
		y = e.getY();
		e.consume();
	}

	private void onDragged(MouseEvent e) {
		if (x == -1 || y == -1)
			return;
		setLayoutX(getXInParent(e) - x);
		setLayoutY(getYInParent(e) - y);
		updatePosition_RealToImage();
		saveCurBounds();
		e.consume();
	}

	private double getXInParent(MouseEvent e) {
		double xInParent = e.getSceneX();
		Parent p = this;
		while ((p = p.getParent()) != p.getScene().getRoot()) {
			xInParent -= p.getLayoutX();
		}
		return xInParent;
	}

	private double getYInParent(MouseEvent e) {
		double yInParent = e.getSceneY();
		Parent p = this;
		while ((p = p.getParent()) != p.getScene().getRoot()) {
			yInParent -= p.getLayoutY();
		}
		return yInParent;
	}

	private void onReleased(MouseEvent e) {
		x = -1;
		y = -1;
		e.consume();
	}

	public void removeEdit() {
		getChildren().remove(edit);
	}

	public SignboardOptions getOptions() {
		return options;
	}

	public void setOptions(SignboardOptions options) {
		this.options = SignboardOptions.copyOptions(options);
		applyStyle();
	}

	public void applyStyle() {
		StringBuilder sb = new StringBuilder();
		for (String key : options.getStyle().keySet())
			sb.append(key + options.getStyle().get(key) + ";\n");
		setStyle(sb.toString());
	}

	private double imToReal(int im, double coef) {
		return im * coef;
	}

	private int realToIm(double real, double coef) {
		return (int) (real / coef);
	}

	public void saveCurBounds() {
		prevImageX = imageX;
		prevImageY = imageY;
		prevImageWidht = imageWidth;
		prevImageHeight = imageHeight;
	}

	public void backup() {
		imageX = prevImageX;
		imageY = prevImageY;
		imageWidth = prevImageWidht;
		imageHeight = prevImageHeight;
		updateBounds_ImageToReal();
		updateSize_RealToActual();
	}

	public int getImageX() {
		return imageX;
	}

	public void setImageX(int imageX) {
		this.imageX = imageX;
	}

	public int getImageY() {
		return imageY;
	}

	public void setImageY(int imageY) {
		this.imageY = imageY;
	}

	public int getImageWidth() {
		return imageWidth;
	}

	public void setImageWidth(int width) {
		this.imageWidth = width;
	}

	public void setImageHeight(int height) {
		this.imageHeight = height;
	}

	public int getImageHeight() {
		return imageHeight;
	}

	public void updateSize_RealToActual() {
		System.out.println("updateSize_RealToActual");
		options.setActualWidth((int) (getPrefWidth() * getImViewContainer().lengthCoef()));
		options.setActualHeight((int) (getPrefHeight() * getImViewContainer().lengthCoef()));
	}

	public void updateBounds_RealToImage() {
		System.out.println("updateBounds_RealToImage");
		updateSize_RealToImage();
		updatePosition_RealToImage();
	}

	public void updateSize_RealToImage() {
		System.out.println("updateSize_RealToImage");
		setImageWidth((int) (getPrefWidth() / getImViewContainer().sizeCoef()));
		setImageHeight((int) (getPrefHeight() / getImViewContainer().sizeCoef()));
	}

	public void updatePosition_RealToImage() {
		System.out.println("updatePosition_RealToImage");
		setImageX((int) (getLayoutX() / getImViewContainer().sizeCoef()));
		setImageY((int) (getLayoutY() / getImViewContainer().sizeCoef()));
	}

	public void updateSize_ActualToReal() {
		System.out.println("updateSize_ActualToReal");
		setPrefWidth(options.getActualWidth() / getImViewContainer().lengthCoef());
		setPrefHeight(options.getActualHeight() / getImViewContainer().lengthCoef());
	}

	public void updateBounds_ImageToReal() {
		System.out.println("updateBounds_ImageToReal");
		updateSize_ImageToReal();
		updatePosition_ImageToReal();
	}

	public void updateSize_ImageToReal() {
		System.out.println("updateSize_ImageToReal");
		setPrefWidth(getImageWidth() * getImViewContainer().sizeCoef());
		setPrefHeight(getImageHeight() * getImViewContainer().sizeCoef());
	}

	public void updatePosition_ImageToReal() {
		System.out.println("updatePosition_ImageToReal");
		setLayoutX(getImageX() * getImViewContainer().sizeCoef());
		setLayoutY(getImageY() * getImViewContainer().sizeCoef());
	}

	public double countSquare() {
		return (double) (options.getActualWidth() * options.getActualHeight() / 100) / 100;
	}

	public void limit() {
		if (getImViewContainer() == null) return;
		if (getImViewContainer().getMarkups().isEmpty()) return;
		while (true) {
			if (countSquare() > 3.0 || getImViewContainer().isHistoricalArea() && countSquare() > 1.0)
				setPrefHeight(getPrefHeight() - 1);
			else
				break;

			if (countSquare() > 3.0 || getImViewContainer().isHistoricalArea() && countSquare() > 1.0)
				setPrefWidth(getPrefWidth() - 1);
			else
				break;
			updateSize_RealToActual();
		}
		updateBounds_RealToImage();
		updateSize_RealToActual();
	}
}