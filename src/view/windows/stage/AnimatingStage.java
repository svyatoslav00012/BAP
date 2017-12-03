package view.windows.stage;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Rectangle2D;
import javafx.stage.Screen;
import javafx.stage.StageStyle;
import javafx.util.Duration;

/**
 * Created by Svyatoslav on 19.02.2017.
 */
public class AnimatingStage extends WritableStage {
	private double fin = getOpacity();
	private Duration duration = new Duration(300);
	private Timeline fading = new Timeline();
	private Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
	private double centerX, centerY;

	public Timeline getFading() {
		return fading;
	}
	public Duration getDuration() { return duration;}
	public double getFin(){ return fin;}

	public AnimatingStage(StageStyle style) {
		super(style);
	}

	public AnimatingStage(){

	}

	public void setFinalOpacity(double value) {        // USE THIS METHOD TO SET OPACITY! super method setOpacity(double value) DOESN'T WORK!
		fin = value;
		setOpacity(value);
	}

	public void setDuration(int millis) {
		duration = new Duration(millis);
	}

	public void smoothlyShow(double millis) {
		duration = new Duration(millis);
		smoothlyShow();
	}

	public void smoothlyShow() {                //you should use it if you want to Show Stage smoothly
		if (isShowing())return;
		setOpacity(0.0);
		fading.getKeyFrames().clear();
		fading.getKeyFrames().add(new KeyFrame(duration, new KeyValue(getWritableOpacity(), fin)));
		fading.play();
		show();
	}

	public void smoothlyShowAndWait(double millis) {
		duration = new Duration(millis);
		smoothlyShowAndWait();
	}

	public void smoothlyShowAndWait() {
		if(isShowing())return;
		setOpacity(0.0);
		fading.getKeyFrames().clear();
		fading.getKeyFrames().add(new KeyFrame(duration, new KeyValue(getWritableOpacity(), fin)));
		fading.play();
		showAndWait();
	}

	public void animatingShow(double millis){
		duration = new Duration(millis);
		animatingShow();
	}

	public void animatingShow(){
		if(isShowing())return;
		centerY = (bounds.getHeight() - getScene().getRoot().prefHeight(0))/2;
		centerX = (bounds.getWidth() - getScene().getRoot().prefWidth(0))/2;
		setOpacity(0.0);
		setY(centerY);
		setX(centerX);
		getFading().getKeyFrames().clear();
		getFading().getKeyFrames().add(
				new KeyFrame(getDuration(),
						new KeyValue(getWritableOpacity(), getFin()),
						new KeyValue(getWritableY(), centerY-50)
				)
		);
		getFading().play();
		show();
	}

	public void animatingShowAndWait(double millis){
		duration = new Duration(millis);
		animatingShowAndWait();
	}

	public void animatingShowAndWait(){
		if(isShowing())return;
		centerY = (bounds.getHeight() - getScene().getRoot().prefHeight(0))/2;
		centerX = (bounds.getWidth() - getScene().getRoot().prefWidth(0))/2;
		setOpacity(0.0);
		setY(centerY);
		setX(centerX);
		getFading().getKeyFrames().clear();
		getFading().getKeyFrames().add(
				new KeyFrame(getDuration(),
						new KeyValue(getWritableOpacity(), getFin()),
						new KeyValue(getWritableY(), centerY-50)
				)
		);
		getFading().play();
		showAndWait();
	}

	public void smoothlyClose(double millis) {
		duration = new Duration(millis);
		smoothlyClose();
	}

	public void smoothlyClose() {            //you should use it if you want to Close Stage smoothly
		if (!isShowing()) return;
		fading.getKeyFrames().clear();
		fading.getKeyFrames().add(new KeyFrame(duration, new KeyValue(getWritableOpacity(), 0.0)));
		fading.setOnFinished(
				action -> {
					close();
					fading.setOnFinished(nothing -> {
					});
				});
		fading.play();
	}

	public void animatingClose(double millis){
		duration = new Duration(millis);
		animatingClose();
	}

	public void animatingClose(){
		if (!isShowing()) return;
		fading.getKeyFrames().clear();
		fading.getKeyFrames().add(new KeyFrame(
				duration,
				new KeyValue(getWritableOpacity(), 0.0),
				new KeyValue(getWritableY(), getY()+50)
		));
		fading.setOnFinished(
				action -> {
					fading.setOnFinished(nothing -> {});
					close();
				});
		fading.play();
	}
}
