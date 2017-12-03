package nodes.imViewContainer;

import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Line;
import model.helpers.Helper;
import nodes.TwoDirectionArrow;
import nodes.markup.Markup;
import nodes.signboard.Signboard;
import org.opencv.core.Rect;
import stages.editSignboard.SignboardOptions;

import java.util.ArrayList;


public class ImViewContainer extends AnchorPane {

	ImageView imageView;

	private boolean testContainer = false;
	private boolean historicalArea = false;
	private ArrayList<Markup> markups = new ArrayList<>();
	private ArrayList<Signboard> signs = new ArrayList<>();

	public boolean isHistoricalArea() {
		return historicalArea;
	}

	public void setHistoricalArea(boolean historicalArea) {
		this.historicalArea = historicalArea;
		updateSizes();
	}

	public void setTestContainer(boolean testContainer) {
		this.testContainer = testContainer;
	}

	public ArrayList<Markup> getMarkups() {
		return markups;
	}

	public ArrayList<Signboard> getSigns() {
		return signs;
	}

	public ImViewContainer() {
		getStyleClass().add("imViewContainer");
		getStylesheets().add("/nodes/imViewContainer/imViewContainerStyle.css");
		imageView = new ImageView();
		imageView.setPreserveRatio(true);
		imageView.getStyleClass().add("imageView");
		AnchorPane.setLeftAnchor(imageView, 0.0);
		AnchorPane.setTopAnchor(imageView, 0.0);
		AnchorPane.setRightAnchor(imageView, 0.0);
		AnchorPane.setBottomAnchor(imageView, 0.0);
		getChildren().add(imageView);
		heightProperty().addListener(observable -> relocateElems());
		widthProperty().addListener(observable -> relocateElems());
	}

	public void relocateElems() {
		for (Signboard signboard : signs)
			signboard.updatePosition();
		for (Markup markup : markups)
			markup.updateCenterPosition_ImToReal();
	}

	public void addSign() {
		Signboard signboard = new Signboard(100, 100, 100, 100);
		signs.add(signboard);
		getChildren().add(signboard);
		signboard.updatePosition();
		updateSizes();
	}

	public void addSign(Rect r) {
		r = getPosition(r);
		Signboard sign = new Signboard(r.x, r.y, r.width, r.height);
		signs.add(sign);
		updateSizes();
		getChildren().add(sign);
		//sign.updateActualSizeFromReal();
		sign.updatePosition();

	}

	public void addMarkup() {
		System.out.println(this);
		setStyle("-fx-cursor: crosshair");
		markups.add(new Markup());
		setOnMouseClicked(clicked -> markups.get(markups.size() - 1).putStartPoint(this, clicked.getX(), clicked.getY()));
		markups.get(markups.size() - 1).getLength().setOnKeyReleased(set -> updateSizes());
	}

	//Puting test markup into test ImViewContainer
	public void addMarkup(Markup markup) {
		markup.putStartPoint(this, 20, 20);
		markup.putEndPoint(0, getPrefHeight());
		AnchorPane.setLeftAnchor(markup.getStartPoint(), 20.0);
		AnchorPane.setTopAnchor(markup.getStartPoint(), 20.0);
		AnchorPane.setLeftAnchor(markup.getEndPoint(), 20.0);
		AnchorPane.setBottomAnchor(markup.getEndPoint(), 20.0);
		markup.getEndPoint().layoutYProperty().addListener(listn -> {
			markup.getLength().setText((int) (getHeight()) + "");
			updateSizes();
		});
		markups.add(markup);
	}

	public void updateSizes() {
		if (markups.isEmpty())
			return;
		for (Signboard signboard : signs) {
			signboard.setSizeLabel();
			//signboard.updateActualSizeFromReal();
		}
	}

	public Rect getPosition(Rect r) {
		r.y = Math.max(0, r.y - r.height / 4);
		r.height = r.height / 5;
		return r;
	}

	public Image getImage() {
		return imageView.getImage();
	}

	public void setImage(Image image) {
		deleteNodes();
		imageView.setImage(image);
		System.out.println(imageView.getX() + " " + imageView.getY());
	}

	public void deleteNodes() {
		getChildren().removeAll(signs);
		deleteMarkups();
		signs.clear();
	}

	public void deleteMarkups(){
		for (Markup m : markups)
			m.removeFromParent();
		markups.clear();
	}

	public void setFitHeight(double height) {
		imageView.setFitHeight(height);
		setHeight(imageView.getFitHeight());
		setWidth(imageView.getFitWidth());
	}

	public void setFitWidth(double width) {
		imageView.setFitWidth(width);
		setHeight(imageView.getFitHeight());
		setWidth(imageView.getFitWidth());
	}

	public double getCoefX() {
		return testContainer ? 1 : getWidth() / ((getImage() == null) ? getWidth() : getImage().getWidth());
	}

	public double getCoefY() {
		return testContainer ? 1 : getHeight() / ((getImage() == null) ? getHeight() : getImage().getHeight());
	}

	public void setPreserveRatio(boolean preserveRatio) {
		imageView.setPreserveRatio(preserveRatio);
	}

	public void removeMarkup(Markup markup) {
		markups.remove(markup);
		markup.removeFromParent();
		for (Signboard s : signs)
			s.setSizeLabel();
	}


	//add sign on test container
	public void addSign(Signboard signboard) {
		signs.add(signboard);
		getChildren().add(signboard);
		signboard.removeEdit();
	}

	public void applyOptionsToAll(SignboardOptions sign) {
		for (Signboard s : signs)
			s.setOptions(sign);
	}

	//public double getLengthCoef() {
	//	return markups.isEmpty() ? 1 : markups.get(0).getLengthCoef(testContainer);
	//}

	public static ImViewContainer copy(ImViewContainer imageView) {
		ImViewContainer newContainer = new ImViewContainer();
		newContainer.setImage(imageView.getImage());
		newContainer.setWidth(newContainer.getImage().getWidth());
		newContainer.setHeight(newContainer.getImage().getHeight());
		newContainer.setHistoricalArea(imageView.isHistoricalArea());
		newContainer.setTestContainer(imageView.testContainer);
		newContainer.getStylesheets().add("/nodes/markup/markupStyle.css");
		for (Signboard s : imageView.signs) {
			Signboard sb = new Signboard(s);
			newContainer.signs.add(sb);
			newContainer.getChildren().add(sb);
			sb.updatePosition();
		}
		for (Markup m : imageView.markups) {
			Markup mk = new Markup(m);
			newContainer.markups.add(mk);
			mk.addAllToParent(newContainer);
			mk.updateCenterPosition_ImToReal();
		}
		return newContainer;
	}

	public double getMaxSignWidth() {
		int maxImageWidth = 0;
		for(Signboard s : signs)
			maxImageWidth = Math.max(maxImageWidth, s.getImageWidth());
		return maxImageWidth;
	}

	public void removeSign(Signboard signboard) {
		getSigns().remove(signboard);
		getChildren().remove(signboard);
	}



	public double getLengthCoef_ActualPerImage(){
		return (markups.isEmpty()) ? 1.0 : markups.get(0).get
	}

	public double getLengthCoef_ImagePerReal(){

	}

	public double getLengthCoef_RealPerImage(){

	}

	public double getLengthCoef_ImagePerActual(){

	}

	public double getLengthCoefX_ActualPerImage(){

	}

	public double getLengthCoefX_ImagePerReal(){

	}

	public double getLengthCoefX_RealPerImage(){

	}

	public double getLengthCoefX_ImagePerActual(){

	}

	public double getLengthCoefY_ActualPerImage(){

	}

	public double getLengthCoefY_ImagePerReal(){

	}

	public double getLengthCoefY_RealPerImage(){

	}

	public double getLengthCoefY_ImagePerActual(){

	}
}
