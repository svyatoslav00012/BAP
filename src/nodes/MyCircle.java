package nodes;

import javafx.scene.input.MouseEvent;
import nodes.markup.Markup;

public class MyCircle extends ImageNode {

	Markup markup;

	public MyCircle(){
		super();
		addEventFilter(MouseEvent.MOUSE_DRAGGED, dr -> updateCenterPosition_RealToIm());
	}

//	public double getCenterXInParent(MouseEvent mouseEvent){
//		double x = mouseEvent.getSceneX();
//		Parent p = this;
//		while((p = p.getParent()) != p.getScene().getRoot())
//	}
//
//	public double getCenterYInParent(MouseEvent mouseEvent){
//
//	}

	public double getCenterX(){
		return getLayoutX() + getPrefWidth() / 2;
	}

	public double getCenterY(){
		return getLayoutY() + getPrefHeight() / 2;
	}

	public void setCenterX(double x){
		setLayoutX(x - getPrefWidth() / 2);
	}

	public void setCenterY(double y){
		setLayoutY(y - getPrefHeight() / 2);
	}

	public void updateCenterPosition_ImToReal(){
		setCenterX(getCenterX_ImToReal());
		setCenterY(getCenterY_ImToReal());
	}

	public void updateCenterPosition_RealToIm(){
		setImageCenterX(getCenterX_RealToIm());
		setImageCenterY(getCenterY_RealToIm());
	}

	public int getImageCenterX(){
		return getImageX() + (int)(getPrefWidth() / 2 / getImViewContainer().getCoefX());
	}

	public int getImageCenterY(){
		return getImageY() + (int)(getPrefHeight() / 2 / getImViewContainer().getCoefY());
	}

	public void setImageCenterX(int x){
		setImageX(x - (int)(getPrefWidth() / 2 / ((getImViewContainer() == null) ? 1 :getImViewContainer().getCoefX())));
	}

	public void setImageCenterY(int y){
		setImageY(y - (int)(getPrefHeight() / 2 / ((getImViewContainer() == null) ? 1 : getImViewContainer().getCoefY())));
	}

	public double getX_ImToReal(){
		return getImageX() * getImViewContainer().getCoefX();
	}

	public int getX_RealToIm(){
		return (int)(getLayoutX() / getImViewContainer().getCoefX());
	}

	public double getY_ImToReal(){
		return getImageY() * getImViewContainer().getCoefY();
	}

	public int getY_RealToIm(){
		return (int)(getLayoutY() / getImViewContainer().getCoefY());
	}

	public double getCenterX_ImToReal(){
		return getImageCenterX() * getImViewContainer().getCoefX();
	}

	public int getCenterX_RealToIm(){
		return (int)(getCenterX() / getImViewContainer().getCoefX());
	}

	public double getCenterY_ImToReal(){
		return getImageCenterY() * getImViewContainer().getCoefY();
	}

	public int getCenterY_RealToIm(){
		return (int)(getCenterY() / getImViewContainer().getCoefY());
	}


}
