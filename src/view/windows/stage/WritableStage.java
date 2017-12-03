package view.windows.stage;

import javafx.beans.value.WritableValue;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Created by Svyatoslav on 17.02.2017.
 */
public class WritableStage extends Stage {

	public WritableStage(StageStyle style){
		super(style);
	}

	public WritableStage(){
	}

	private WritableValue<Double> writableX = new WritableValue<Double>() {
		@Override
		public Double getValue() {
			return getX();
		}

		@Override
		public void setValue(Double value) {
			setX(value);
		}
	};

	private WritableValue<Double> writableY = new WritableValue<Double>() {
		@Override
		public Double getValue() {
			return getY();
		}

		@Override
		public void setValue(Double value) {
			setY(value);
		}
	};

	private WritableValue<Double> writableWidth = new WritableValue<Double>() {
		@Override
		public Double getValue() {
			return getWidth();
		}

		@Override
		public void setValue(Double value) {
			setWidth(value);
		}
	};

	private WritableValue<Double> writableHeight = new WritableValue<Double>() {
		@Override
		public Double getValue() {
			return getHeight();
		}

		@Override
		public void setValue(Double value) {
			setHeight(value);
		}
	};

	private WritableValue<Double> writableOpacity = new WritableValue<Double>() {
		@Override
		public Double getValue() {
			return getOpacity();
		}

		@Override
		public void setValue(Double value) {
			setOpacity(value);
		}
	};

	public WritableValue<Double> getWritableX() {
		return writableX;
	}

	public WritableValue<Double> getWritableY() {
		return writableY;
	}

	public WritableValue<Double> getWritableOpacity() {
		return writableOpacity;
	}

	public WritableValue<Double> getWritableWidth() {
		return writableWidth;
	}

	public WritableValue<Double> getWritableHeight() {
		return writableHeight;
	}
}
