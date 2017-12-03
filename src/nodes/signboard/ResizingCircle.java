package nodes.signboard;

import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;

class ResizingCircle extends Region {

	private CirclePosition position;
	private double anchorX = -1, anchorY = -1, x = -1, y = -1, newCenterX = -1, newCenterY = -1;

	public Signboard getSign() {
		return (Signboard) getParent();
	}

	public ResizingCircle(CirclePosition position) {

		setVisible(false);

		this.position = position;
		getStyleClass().add("resizingCircle");
		setPrefSize(20, 20);

		addEventFilter(MouseEvent.MOUSE_PRESSED, this::onPressed);
		addEventFilter(MouseEvent.MOUSE_RELEASED, this::onReleased);

		switch (position) {

			case TOP_LEFT:
				AnchorPane.setLeftAnchor(this, 0.0);
				AnchorPane.setTopAnchor(this, 0.0);
				getStyleClass().add("tl_br_circle");
				addEventFilter(MouseEvent.MOUSE_DRAGGED, dr -> {
					newCenterX = getCenterXInImView(dr);
					newCenterY = getCenterYInImView(dr);
					getSign().setLayoutX(newCenterX);
					getSign().setLayoutY(newCenterY);
					//dr.consume();
				});
				break;
			case TOP_MID:
				getStyleClass().add("tm_bm_circle");
				addEventFilter(MouseEvent.MOUSE_DRAGGED, dr -> {
					//newCenterX = getCenterXInImView(dr);
					newCenterY = getCenterYInImView(dr);
					getSign().setLayoutY(newCenterY);
					//dr.consume();
				});

				break;
			case TOP_RIGHT:
				AnchorPane.setRightAnchor(this, 0.0);
				AnchorPane.setTopAnchor(this, 0.0);
				getStyleClass().add("tr_bl_circle");
				addEventFilter(MouseEvent.MOUSE_DRAGGED, dr -> {
					newCenterX = getCenterXInImView(dr);
					newCenterY = getCenterYInImView(dr);
					getSign().setLayoutY(newCenterY);
					//dr.consume();
				});

				break;
			case CENTER_LEFT:
				getStyleClass().add("cl_cr_circle");
				addEventFilter(MouseEvent.MOUSE_DRAGGED, dr -> {
					newCenterX = getCenterXInImView(dr);
					getSign().setLayoutX(newCenterX);
					//dr.consume();
				});

				break;
			case CENTER_RIGHT:
				getStyleClass().add("cl_cr_circle");

				addEventFilter(MouseEvent.MOUSE_DRAGGED, dr -> {
					newCenterX = getCenterXInImView(dr);
					//dr.consume();
				});

				break;
			case BOTTOM_LEFT:
				AnchorPane.setLeftAnchor(this, 0.0);
				AnchorPane.setBottomAnchor(this, 0.0);
				getStyleClass().add("tr_bl_circle");

				addEventFilter(MouseEvent.MOUSE_DRAGGED, dr -> {
					newCenterX = getCenterXInImView(dr);
					newCenterY = getCenterYInImView(dr);
					getSign().setLayoutX(newCenterX);
					//dr.consume();
				});

				break;
			case BOTTOM_MID:
				getStyleClass().add("tm_bm_circle");

				addEventFilter(MouseEvent.MOUSE_DRAGGED, dr -> {
					newCenterY = getCenterYInImView(dr);
					//dr.consume();
				});

				break;
			case BOTTOM_RIGHT:
				AnchorPane.setRightAnchor(this, 0.0);
				AnchorPane.setBottomAnchor(this, 0.0);
				getStyleClass().add("tl_br_circle");

				addEventFilter(MouseEvent.MOUSE_DRAGGED, dr -> {
					newCenterX = getCenterXInImView(dr);
					newCenterY = getCenterYInImView(dr);
					//dr.consume();
				});
				break;
		}


		addEventFilter(MouseEvent.MOUSE_DRAGGED, this::onDragged);
	}

	public double getCenterXInImView(MouseEvent mouseEvent) {
		double xInB = mouseEvent.getSceneX();
		Parent p = getParent();
		while ((p = p.getParent()) != p.getScene().getRoot()) {
			xInB -= p.getLayoutX();
		}
		return xInB;
	}

	public double getCenterYInImView(MouseEvent mouseEvent) {
		double yInB = mouseEvent.getSceneY();
		Parent p = getParent();
		while ((p = p.getParent()) != p.getScene().getRoot()) {
			yInB -= p.getLayoutY();
		}
		return yInB;
	}

	public void onPressed(MouseEvent mouseEvent) {
		anchorX = getOpositeCenterX();
		anchorY = getOpositeCenterY();
		x = mouseEvent.getX();
		y = mouseEvent.getY();
		mouseEvent.consume();
	}


	private void onDragged(MouseEvent mouseEvent) {
		if (newCenterX != -1) getSign().setPrefWidth(Math.abs(newCenterX - anchorX));
		if (newCenterY != -1) getSign().setPrefHeight(Math.abs(newCenterY - anchorY));
		getSign().updateSize_RealToActual();
		getSign().updateBounds_RealToImage();
		if (getSign().getOptions().isAutoLimited())
			if (getSign().getImViewContainer().isHistoricalArea() && getSign().countSquare() > 1.0
					|| getSign().countSquare() > 3.0)
				getSign().backup();
		getSign().saveCurBounds();
		newCenterX = -1;
		newCenterY = -1;
		mouseEvent.consume();
	}

	private void onReleased(MouseEvent mouseEvent) {
		x = -1;
		y = -1;
		mouseEvent.consume();
	}

	//could be optimized
	public void stayOnThePosition() {
		switch (position) {
			case TOP_LEFT:
				setCenterX(0);
				setCenterY(0);
				break;
			case TOP_MID:
				setCenterX(getSign().getWidth() / 2);
				setCenterY(0);
				break;
			case TOP_RIGHT:
				setCenterX(getSign().getWidth());
				setCenterY(0);
				break;
			case CENTER_LEFT:
				setCenterX(0);
				setCenterY(getSign().getHeight() / 2);
				break;
			case CENTER_RIGHT:
				setCenterX(getSign().getWidth());
				setCenterY(getSign().getHeight() / 2);
				break;
			case BOTTOM_LEFT:
				setCenterX(0);
				setCenterY(getSign().getHeight());
				break;
			case BOTTOM_MID:
				setCenterX(getSign().getWidth() / 2);
				setCenterY(getSign().getHeight());
				break;
			case BOTTOM_RIGHT:
				setCenterX(getSign().getWidth());
				setCenterY(getSign().getHeight());
				break;
		}
	}

	public double getCenterX() {
		return getLayoutX() + getPrefWidth() / 2;
	}

	public double getCenterY() {
		return getLayoutY() + getPrefHeight() / 2;
	}

	public void setCenterX(double x) {
		setLayoutX(x - getPrefWidth() / 2);
	}

	public void setCenterY(double y) {
		setLayoutY(y - getPrefHeight() / 2);
	}

	public double getOpositeCenterX() {
		if (getCenterX() == 0)
			return getSign().getLayoutX() + getSign().getWidth();
		if (getCenterX() == getSign().getWidth())
			return getSign().getLayoutX();
		if (getCenterX() == getSign().getWidth() / 2)
			return getSign().getLayoutX() + getSign().getWidth() / 2;
		return -1;
	}

	public double getOpositeCenterY() {
		if (getCenterY() == 0)
			return getSign().getLayoutY() + getSign().getHeight();
		if (getCenterY() == getSign().getHeight())
			return getSign().getLayoutY();
		if (getCenterY() == getSign().getHeight() / 2)
			return getSign().getLayoutY() + getSign().getHeight() / 2;
		return -1;
	}

}
