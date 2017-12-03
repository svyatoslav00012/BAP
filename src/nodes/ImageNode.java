package nodes;

import javafx.scene.Parent;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import nodes.imViewContainer.ImViewContainer;

public class ImageNode extends Region {

	private int imageX;
	private int imageY;
	private ContextMenu contextMenu;

	public ImViewContainer getImViewContainer(){
		return (ImViewContainer)getParent();
	}

	public ContextMenu getContextMenu() {
		return contextMenu;
	}

	private double nodeX = -1, nodeY = -1;

	public ImageNode(){
		MenuItem delete = new MenuItem("Delete");
		delete.setOnAction(remove -> getImViewContainer().getChildren().remove(this));
		contextMenu = new ContextMenu(delete);
		setOnContextMenuRequested(showContext -> contextMenu.show(this, showContext.getScreenX(), showContext.getScreenY()));

		addEventFilter(MouseEvent.MOUSE_PRESSED, pr -> {
			nodeX = pr.getX();
			nodeY = pr.getY();
			pr.consume();
		});
		addEventFilter(MouseEvent.MOUSE_DRAGGED, dr -> {
			if(nodeX == -1 || nodeY == -1)
				return;
			setLayoutX(getXInParent(dr) - nodeX);
			setLayoutY(getYInParent(dr) - nodeY);
			dr.consume();
		});
		addEventFilter(MouseEvent.MOUSE_RELEASED, rel -> {
			nodeX = -1;
			nodeY = -1;
			rel.consume();
		});
	}

	public int getImageX(){
		return imageX;
	}

	public int getImageY(){
		return imageY;
	}

	public void setImageX(int newImX){
		imageX = newImX;
	}

	public void setImageY(int newImY){
		imageY = newImY;
	}

	private double getXInParent(MouseEvent mouseEvent){
		double x = mouseEvent.getSceneX();
		Parent p = this.getParent();
		do{
			x -= p.getLayoutX();
		} while ((p = p.getParent()) != getScene().getRoot());
		return x;
	}

	private double getYInParent(MouseEvent mouseEvent){
		double y = mouseEvent.getSceneY();
		Parent p = this.getParent();
		do{
			y -= p.getLayoutY();
		} while ((p = p.getParent()) != getScene().getRoot());
		return y;
	}

	public double getX_ImToReal() {
		return getImageX() * getImViewContainer().sizeCoef();
	}

	public int getX_RealToIm() {
		return (int) (getLayoutX() / getImViewContainer().sizeCoef());
	}

	public double getY_ImToReal() {
		return getImageY() * getImViewContainer().sizeCoef();
	}

	public int getY_RealToIm() {
		return (int) (getLayoutY() / getImViewContainer().sizeCoef());
	}

}
