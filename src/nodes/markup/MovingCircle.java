package nodes.markup;

import javafx.scene.input.MouseEvent;
import nodes.MyCircle;

public class MovingCircle extends MyCircle {

	Markup markup;

	public MovingCircle(Markup parent){
		super();
		this.markup = parent;
		layoutXProperty().addListener(observable -> parent.onCirclesMove());
		layoutYProperty().addListener(observable -> parent.onCirclesMove());
		addEventFilter(MouseEvent.MOUSE_DRAGGED, e -> {
			updateCenterPosition_RealToIm();
		});
		getContextMenu().getItems().get(0).setOnAction(remAll -> getImViewContainer().removeMarkup(markup));
	}


}