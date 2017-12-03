package nodes;

import javafx.beans.InvalidationListener;
import javafx.beans.property.DoubleProperty;
import javafx.scene.Group;
import javafx.scene.shape.Line;

public class TwoDirectionArrow extends Group {

	private  Line line;

	public TwoDirectionArrow(int startX, int startY, int endX, int endY) {
		this();
		setStartX(startX);
		setStartY(startY);
		setEndX(endX);
		setEndY(endY);
	}

	public TwoDirectionArrow() {
		this(new Line(), new Line(), new Line(), new Line(), new Line());
		getStyleClass().add("arrow");
	}

	private static final double arrowLength = 7;
	private static final double arrowWidth = 10;

	private TwoDirectionArrow(Line line, Line arrow1, Line arrow2, Line arrow3, Line arrow4) {
		super(line, arrow1, arrow2, arrow3, arrow4);
		line.setStrokeWidth(2);
		this.line = line;
		InvalidationListener updater = o -> {
			double ex = getEndX();
			double ey = getEndY();
			double sx = getStartX();
			double sy = getStartY();

			arrow1.setEndX(sx);
			arrow1.setEndY(sy);
			arrow2.setEndX(sx);
			arrow2.setEndY(sy);
			arrow3.setEndX(ex);
			arrow3.setEndY(ey);
			arrow4.setEndX(ex);
			arrow4.setEndY(ey);

			if (ex == sx && ey == sy) {
				// arrow parts of length 0
				arrow1.setStartX(sx);
				arrow1.setStartY(sy);
				arrow2.setStartX(sx);
				arrow2.setStartY(sy);
				arrow3.setStartX(ex);
				arrow3.setStartY(ey);
				arrow4.setStartX(ex);
				arrow4.setStartY(ey);
			} else {
				double factor = arrowLength / Math.hypot(sx-ex, sy-ey);
				double factorO = arrowWidth / Math.hypot(sx-ex, sy-ey);

				// part in direction of main line
				double dx1 = (ex - sx) * factor;
				double dy1 = (ey - sy) * factor;
				double dx2 = (sx - ex) * factor;
				double dy2 = (sy - ey) * factor;

				// part ortogonal to main line
				double ox1 = (ex - sx) * factorO;
				double oy1 = (ey - sy) * factorO;
				double ox2 = (sx - ex) * factorO;
				double oy2 = (sy - ey) * factorO;

				arrow1.setStartX(sx + dx1 - oy1);
				arrow1.setStartY(sy + dy1 + ox1);
				arrow2.setStartX(sx + dx1 + oy1);
				arrow2.setStartY(sy + dy1 - ox1);
				arrow3.setStartX(ex + dx2 - oy2);
				arrow3.setStartY(ey + dy2 + ox2);
				arrow4.setStartX(ex + dx2 + oy2);
				arrow4.setStartY(ey + dy2 - ox2);
			}
		};

		// add updater to properties
		startXProperty().addListener(updater);
		startYProperty().addListener(updater);
		endXProperty().addListener(updater);
		endYProperty().addListener(updater);
		updater.invalidated(null);
	}

	// start/end properties

	public final void setStartX(double value) {
		line.setStartX(value);
	}

	public final double getStartX() {
		return line.getStartX();
	}

	public final DoubleProperty startXProperty() {
		return line.startXProperty();
	}

	public final void setStartY(double value) {
		line.setStartY(value);
	}

	public final double getStartY() {
		return line.getStartY();
	}

	public final DoubleProperty startYProperty() {
		return line.startYProperty();
	}

	public final void setEndX(double value) {
		line.setEndX(value);
	}

	public final double getEndX() {
		return line.getEndX();
	}

	public final DoubleProperty endXProperty() {
		return line.endXProperty();
	}

	public final void setEndY(double value) {
		line.setEndY(value);
	}

	public final double getEndY() {
		return line.getEndY();
	}

	public final DoubleProperty endYProperty() {
		return line.endYProperty();
	}

}