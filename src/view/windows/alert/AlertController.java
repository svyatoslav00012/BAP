package view.windows.alert;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import model.helpers.Helper;
import view.windows.stage.ModalStage;
import view.windows.stage.MyStage;

import java.io.PrintWriter;
import java.io.StringWriter;

public class AlertController {

	private static final Duration DURATION = new Duration(200);

	private static final String FXML = "/view/windows/alert/aler.fxml";

	private static final Image INFO_IMAGE = new Image("/view/images/info.png");
	private static final Image WARNING_IMAGE = new Image("/view/images/warning.png");
	private static final Image CONFIRM_IMAGE = new Image("/view/images/confirm.png");
	private static final Image EXCEPTION_IMAGE = new Image("/view/images/exception.png");
	private static final Image ERROR_IMAGE = new Image("/view/images/error.png");

	private AlertType type;
	private boolean answer = false;
	private Exception exception;


	@FXML
	private AnchorPane infoPane;
	@FXML
	private ImageView icon;
	@FXML
	private VBox messagePane;
	@FXML
	private Label labelTitle;
	@FXML
	private TextArea messageArea;
	@FXML
	private VBox buttonsPane;
	@FXML
	private Button buttonYes;
	@FXML
	private Button buttonNo;
	@FXML
	private AnchorPane exceptionPane;
	@FXML
	private ScrollPane tracePane;
	@FXML
	private AnchorPane traceAnchor;
	@FXML
	private TextArea traceArea;
	@FXML
	private Button buttonShowHideExceptionPane;
	@FXML
	private HBox optionsPane;
	@FXML
	private Button buttonCopyToCB;
	@FXML
	private Button buttonReport;

	@FXML
	public void initialize() {
		buttonsPane.getChildren().remove(buttonNo);
	}

	public void init(AlertType type, Object message) {
		this.type = type;
		switch (type) {
			case INFO:
				labelTitle.setText("INFO");
				messageArea.setText(message.toString());
				icon.setImage(INFO_IMAGE);
				break;
			case WARNING:
				labelTitle.setText("WARNING");
				messageArea.setText(message.toString());
				icon.setImage(WARNING_IMAGE);
				break;
			case EXCEPT:
				labelTitle.setText("EXCEPTION");
				messageArea.setText(message.toString());
				buttonShowHideExceptionPane.setVisible(true);
				StringWriter sw = new StringWriter();
				((Exception) message).printStackTrace(new PrintWriter(sw));
				traceArea.setText(sw.toString());
				icon.setImage(EXCEPTION_IMAGE);
				break;
			case ERROR:
				labelTitle.setText("ERROR");
				messageArea.setText(message.toString());
				icon.setImage(ERROR_IMAGE);
				break;
			case CONFIRM:
				if(!buttonsPane.getChildren().contains(buttonNo))buttonsPane.getChildren().add(buttonNo);
				labelTitle.setText("");
				messageArea.setText(message.toString());
				icon.setImage(CONFIRM_IMAGE);
				break;
		}
	}

	public AlertType getType() {
		return type;
	}

	public boolean getAnswer() {
		return answer;
	}

	public Exception getException() {
		return exception;
	}

	public void showHide(ActionEvent actionEvent) {
		if (exceptionPane.getOpacity() == 0.0)
			new Timeline(new KeyFrame(DURATION,
					new KeyValue(window().getWritableHeight(), 500.0),
					new KeyValue(exceptionPane.opacityProperty(), 1.0),
					new KeyValue(buttonShowHideExceptionPane.rotateProperty(), 180.0)
			)).play();
		else new Timeline(new KeyFrame(DURATION,
				new KeyValue(window().getWritableHeight(), 200.0),
				new KeyValue(exceptionPane.opacityProperty(), 0.0),
				new KeyValue(buttonShowHideExceptionPane.rotateProperty(), 0.0)
		)).play();
	}

	public MyStage window() {
		return ((MyStage) icon.getScene().getWindow());
	}

	public void copyToCB(ActionEvent actionEvent) {
		Helper.copyToCB(traceArea.getText());
	}

	public void answer(ActionEvent actionEvent) {
		if (actionEvent.getSource() == buttonYes) answer = true;
		if (actionEvent.getSource() == buttonNo) answer = false;
		((MyStage) buttonYes.getScene().getWindow()).animatingClose();
	}

	public void report(ActionEvent actionEvent) {
	}

	static class Alert extends ModalStage {
		public Alert(AlertType type, Object message) {
			super(FXML);
			getWcPane().getChildren().clear();
			getController().init(type, message);
		}

		private AlertController getController() {
			return (AlertController) getLoader().getController();
		}
	}

	public static void showInfo(String message) {
		new Alert(AlertType.INFO, message).animatingShowAndWait();
	}

	public static void showWarning(String message) {
		new Alert(AlertType.WARNING, message).animatingShowAndWait();
	}

	public static boolean showConfirm(String message) {
		Alert confirm = new Alert(AlertType.CONFIRM, message);
		confirm.animatingShowAndWait();
		return confirm.getController().getAnswer();
	}

	public static void showError(String message) {
		new Alert(AlertType.ERROR, message).animatingShowAndWait();
	}

	public static void showException(Exception exception) {
		new Alert(AlertType.EXCEPT, exception).animatingShowAndWait();
	}
}
