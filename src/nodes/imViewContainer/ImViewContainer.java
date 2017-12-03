package nodes.imViewContainer;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
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

	public static ImViewContainer copy(ImViewContainer imageView) {
		ImViewContainer newContainer = new ImViewContainer();
		newContainer.setImage(imageView.getImage());
		newContainer.setWidth(newContainer.getImage().getWidth());
		newContainer.setHeight(newContainer.getImage().getHeight());
		newContainer.setHistoricalArea(imageView.isHistoricalArea());
		newContainer.setTestContainer(imageView.testContainer);
		newContainer.getStylesheets().add("/nodes/markup/markupStyle.css");
		for (Markup m : imageView.markups) {
			Markup mk = new Markup(m);
			newContainer.markups.add(mk);
			mk.addAllToParent(newContainer);
			mk.updateCenterPosition_ImToReal();
		}
		for (Signboard s : imageView.signs) {
			Signboard sb = new Signboard(s);
			newContainer.signs.add(sb);
			newContainer.getChildren().add(sb);
			System.out.println("SB : " + sb.getImageX() + " " + sb.getImageY() + " " + sb.getImageWidth() + " " + sb.getImageHeight());
			sb.removeEdit();
			sb.updateBounds_ImageToReal();
		}

		return newContainer;
	}

	public void relocateElems() {
		for (Markup markup : markups)
			markup.updateCenterPosition_ImToReal();
		for (Signboard signboard : signs)
			signboard.updateBounds_ImageToReal();
	}

	/**
	 * add new sign to image
	 */
	public void addSign() {
		Signboard signboard = new Signboard(100, 100, 100, 100);
		signs.add(signboard);
		getChildren().add(signboard);
		signboard.updateBounds_ImageToReal();
		signboard.updateSize_RealToActual();
		signboard.setSizeLabel();
	}

	public void addSign(Rect r) {
		r = getPosition(r);
		Signboard sign = new Signboard(r.x, r.y, r.width, r.height);
		signs.add(sign);
		getChildren().add(sign);
		sign.setSizeLabel();
		sign.updateBounds_ImageToReal();
		sign.updateSize_RealToActual();
	}

	public void addMarkup() {
		System.out.println(this);
		setStyle("-fx-cursor: crosshair");
		markups.add(new Markup());
		setOnMouseClicked(clicked -> markups.get(markups.size() - 1).putStartPoint(this, clicked.getX(), clicked.getY()));
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

	//add sign on test container
	public void addSign(Signboard signboard) {
		signs.add(signboard);
		getChildren().add(signboard);
		signboard.updateBounds_ImageToReal();
		signboard.removeEdit();
		System.out.println("ADD " + signboard.getLayoutX() + " " + signboard.getLayoutY() + " "
				+ signboard.getPrefWidth() + " " + signboard.getPrefHeight());
		System.out.println(signboard.getImageX() + " " + signboard.getImageY() + " "
				+ signboard.getImageWidth() + " " + signboard.getImageHeight());
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

	public void removeMarkup(Markup markup) {
		markups.remove(markup);
		markup.removeFromParent();
		for (Signboard s : signs)
			s.setSizeLabel();
	}

	public void applyOptionsToAll(SignboardOptions sign) {
		for (Signboard s : signs)
			s.setOptions(sign);
	}

	public void updateSizes() {
		if (markups.isEmpty())
			return;
		for (Signboard signboard : signs) {
			signboard.limit();
			signboard.updateSize_RealToActual();
			signboard.setSizeLabel();
		}
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

	/**
	 * Сантиметри / (реальні) пікселі
	 *
	 * @return
	 */
	public double lengthCoef() {
		return (testContainer || markups.isEmpty()) ? 1 : markups.get(0).getLengthCoef();
	}

	/**
	 * розмір вікна до розміру зображення
	 *
	 * @return
	 */
	public double sizeCoef() {
		if (testContainer) return 1;
		System.err.println("IMAGE COEF ERROR IS "
				+ Math.abs(getWidth() / imageView.getImage().getWidth()
				- getHeight() / imageView.getImage().getHeight()));
		return getWidth() / imageView.getImage().getWidth();
	}

	public void fitSizeToImage() {
		//setSi
		setHeight(imageView.getImage().getHeight());
		setWidth(imageView.getImage().getWidth());
	}
}
