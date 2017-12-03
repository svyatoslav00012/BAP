package view.windows.notifications;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import view.windows.stage.MyStage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.PriorityQueue;


public class NotificationsController {

	private static final String FXML = "/view/windows/notifications/notifications.fxml";

	private static final Image INFO = new Image("/view/images/info-notif.png");
	private static final Image WARNING = new Image("/view/images/warning-notif.png");
	private static final Image COMPLETE = new Image("/view/images/complete-notif.png");
	private static final Image WRONG = new Image("/view/images/wrong-notif.png");
	private static final Image ERROR = new Image("/view/images/error-notif.png");

	private static ArrayList<Notification> notifications = new ArrayList<>(10);
	private static PriorityQueue<Notification> notificationsQueue = new PriorityQueue<>(new Comparator<Notification>() {
		@Override
		public int compare(Notification o1, Notification o2) {
			int first = o1.getTypeId();
			int second = o2.getTypeId();
			int ans = -1;
			if(first == second)ans = 0;
			if(first < second)ans = 0;
			return ans;
		}
	});

	private static int moving = 0;

	private static void decMoving(){
		moving--;
		check();
	}

	private static void check(){
		if(moving == 0 && !notificationsQueue.isEmpty()) {
			notifications.add(0, notificationsQueue.poll());
			for(int i = 0;i<notifications.size();++i)notifications.get(i).moveUp();
		}
	}

	@FXML
	ImageView icon;
	@FXML
	Label info;
	@FXML
	Label time;

	public void initialize() {

	}

	public static void showInfo(String message) {
		new Notification(NotificationType.INFO, message).add();
	}

	public static void showWarning(String message) {
		new Notification(NotificationType.WARNING, message).add();
	}

	public static void showComplete(String message) {
		new Notification(NotificationType.COMPLETE, message).add();
	}

	public static void showWrong(String message) {
		new Notification(NotificationType.WRONG, message).add();
	}

	public static void showError(String message) {
		new Notification(NotificationType.ERROR, message).add();
	}

	static class Notification extends MyStage {

		private final Duration LIFE_DURATION = new Duration(10000);
		private final double HEIGHT = Screen.getPrimary().getVisualBounds().getHeight();
		private final double WIDTH = Screen.getPrimary().getVisualBounds().getWidth();
		private final Duration DURATION = new Duration(200);
		private NotificationType type;
		private double opacity;

		public Notification(NotificationType type, String shortMessage) {
			setScene(FXML);
			initStyle(StageStyle.TRANSPARENT);
			getScene().setFill(Color.TRANSPARENT);
			Tooltip tooltip = new Tooltip("press RMB to close");
			Tooltip.install(getScene().getRoot(), tooltip);
			setAlwaysOnTop(true);
			this.type = type;
			init();
			getController().time.setText(new SimpleDateFormat("HH:mm:ss").format(new Date()));
			getController().info.setText(shortMessage);
			getScene().addEventHandler(MouseEvent.MOUSE_PRESSED, action -> {
				if (action.getButton() == MouseButton.SECONDARY) {
					smoothlyClose();
					int i = notifications.indexOf(this);
					notifications.remove(this);
					for(int j = i;j<notifications.size();++j){
						notifications.get(j).moveDown();
					}
				}
			});
			getScene().setOnMouseEntered(entered -> {
				opacity = getOpacity();
				setOpacity(1.0);
			});
			getScene().setOnMouseExited(entered -> {
				setOpacity(opacity);
			});
			new Timeline(new KeyFrame(LIFE_DURATION, onFinished -> disappear_2())).play();
		}

		public double prefHeight() {
			return getScene().getRoot().prefHeight(0);
		}

		public double prefWidth() {
			return getScene().getRoot().prefWidth(0);
		}

		public void add(){
			notificationsQueue.offer(this);
			check();
		}

		private void appear() {
			setX(WIDTH - prefWidth());
			setY(HEIGHT + 100);
			setOpacity(0.1);
			show();
			for (int i = notifications.size() - 1; i > 0; i--) notifications.get(i).moveUp();
			moving++;
			new Timeline(new KeyFrame(DURATION, onFinished -> decMoving(),
					new KeyValue(getWritableY(), HEIGHT - prefHeight()),
					new KeyValue(getWritableOpacity(), 1.0)
			)).play();
		}

		private void disappear_1(){
			moving++;
			new Timeline(new KeyFrame(DURATION, onFinished -> {decMoving(); notifications.remove(this); close();},
					new KeyValue(getWritableY(), 0.0),
					new KeyValue(getWritableOpacity(), 0.0)
			)).play();
		}

		private void disappear_2(){
			moving++;
			new Timeline(new KeyFrame(DURATION, onFinished -> {decMoving(); notifications.remove(this); close();},
					//new KeyValue(getWritableY(), getY() - getHeight() - 10),
					new KeyValue(getWritableWidth(), 0.0),
					new KeyValue(getWritableX(), getX() + prefWidth()/2),
					new KeyValue(getWritableHeight(), 0.0)
			)).play();
		}

		public void moveUp() {
			if(!isShowing()){
				appear();
				return;
			}
			if (getOpacity() < 0.4) {
				disappear_1();
				return;
			}
			moving++;
			new Timeline(new KeyFrame(DURATION, onFinished -> decMoving(),
					new KeyValue(getWritableY(), getY() - prefHeight() - 10),
					new KeyValue(getWritableOpacity(), getOpacity() - 0.1)
			)).play();
		}

		public void moveDown() {
			moving++;
			new Timeline(new KeyFrame(DURATION, onFinished -> decMoving(),
					new KeyValue(getWritableY(), getY() + prefHeight() + 10),
					new KeyValue(getWritableOpacity(), getOpacity() + 0.1)
			)).play();
		}

		private NotificationsController getController() {
			return (NotificationsController) getLoader().getController();
		}

		public NotificationType getType() {
			return type;
		}

		public void init() {
			switch (type) {
				case INFO:
					getIcons().add(INFO);
					getController().icon.setImage(INFO);
					break;
				case WARNING:
					getIcons().add(WARNING);
					getController().icon.setImage(WARNING);
					break;
				case COMPLETE:
					getIcons().add(COMPLETE);
					getController().icon.setImage(COMPLETE);
					break;
				case WRONG:
					getIcons().add(WRONG);
					getController().icon.setImage(WRONG);
					break;
				case ERROR:
					getIcons().add(ERROR);
					getController().icon.setImage(ERROR);
					break;
			}
		}

		private int getTypeId(){
			switch (getType()){
				case INFO: return 1;
				case WARNING: return  2;
				case COMPLETE: return 3;
				case WRONG: return  4;
				case ERROR: return 5;
			}
			return 0;
		}

	}

//	public void check(){
//		if(priop)
//	}

}
