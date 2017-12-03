package model.helpers;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import view.windows.alert.AlertController;
import view.windows.notifications.NotificationsController;
import view.windows.stage.MyStage;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;

/**
 * Created by Svyatoslav on 03.07.2017.
 */
public class Helper {
	public static boolean isDouble(String number) {
		try {
			Double.parseDouble(number);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	public static boolean isInteger(String number) {
		try {
			Integer.parseInt(number);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	public static void hackTooltip(Tooltip tooltip, Duration startTime, Duration duration) {
		try {
			Field fieldBehavior = tooltip.getClass().getDeclaredField("BEHAVIOR");
			fieldBehavior.setAccessible(true);
			Object objBehavior = fieldBehavior.get(tooltip);

			Field fieldTimer = objBehavior.getClass().getDeclaredField("activationTimer");
			fieldTimer.setAccessible(true);
			Timeline objTimer = (Timeline) fieldTimer.get(objBehavior);

			objTimer.getKeyFrames().clear();
			objTimer.getKeyFrames().add(new KeyFrame(startTime));

			fieldTimer = objBehavior.getClass().getDeclaredField("hideTimer");
			fieldTimer.setAccessible(true);
			objTimer = (Timeline) fieldTimer.get(objBehavior);

			objTimer.getKeyFrames().clear();
			objTimer.getKeyFrames().add(new KeyFrame(duration));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void writeImageToFile(Image im, File f) {
		try {
			if (!f.exists()) f.createNewFile();
			ImageIO.write(SwingFXUtils.fromFXImage(im, null), "png", f);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void copyToCB(String text) {
		StringSelection ss = new StringSelection(text);
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null);
	}

	public static void openAlertTest() {
		Button showInfo = new Button("INFO");
		showInfo.setPrefSize(200, 50);
		showInfo.setOnAction(show -> AlertController.showInfo("Test info"));
		Button showWarning = new Button("WARNING");
		showWarning.setPrefSize(200, 50);
		showWarning.setOnAction(show -> AlertController.showWarning("Test warning"));
		Button showConfirm = new Button("CONFIRM");
		showConfirm.setPrefSize(200, 50);
		showConfirm.setOnAction(show -> System.out.println(AlertController.showConfirm("Test confirm")));
		Button showError = new Button("ERROR");
		showError.setPrefSize(200, 50);
		showError.setOnAction(show -> AlertController.showError("Test error"));
		Button showException = new Button("EXCEPTION");
		showException.setPrefSize(200, 50);
		showException.setOnAction(show -> AlertController.showException(new Exception("some exception")));
		VBox buts = new VBox(10, showInfo, showWarning, showConfirm, showError, showException);
		AnchorPane.setBottomAnchor(buts, 0.0);
		AnchorPane.setRightAnchor(buts, 0.0);
		AnchorPane.setLeftAnchor(buts, 0.0);
		AnchorPane.setTopAnchor(buts, 50.0);
		buts.setAlignment(Pos.CENTER);
		new MyStage(new AnchorPane(buts)).show();
	}

	public static void openNotificationsTest() {
		Button showInfo = new Button("INFO");
		showInfo.setPrefSize(200, 50);
		showInfo.setOnAction(show -> NotificationsController.showInfo("Test info"));
		Button showWarning = new Button("WARNING");
		showWarning.setPrefSize(200, 50);
		showWarning.setOnAction(show -> NotificationsController.showWarning("Test warning"));
		Button showComplete = new Button("COMPLETE");
		showComplete.setPrefSize(200, 50);
		showComplete.setOnAction(show -> NotificationsController.showComplete("Test complete"));
		Button showWrong = new Button("WRONG");
		showWrong.setPrefSize(200, 50);
		showWrong.setOnAction(show -> NotificationsController.showWrong("Test wrong"));
		Button showError = new Button("ERROR");
		showError.setPrefSize(200, 50);
		showError.setOnAction(show -> NotificationsController.showError("Test error"));
		VBox buts = new VBox(10, showInfo, showWarning, showComplete, showWrong, showError);
		AnchorPane.setBottomAnchor(buts, 0.0);
		AnchorPane.setRightAnchor(buts, 0.0);
		AnchorPane.setLeftAnchor(buts, 0.0);
		AnchorPane.setTopAnchor(buts, 50.0);
		buts.setAlignment(Pos.CENTER);
		new MyStage(new AnchorPane(buts)).show();
	}

	public static boolean isLatin(Character c) {
		if (c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z') return true;
		return false;
	}

	public static String makeDoubleShorter(String text) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < Math.min(2, text.length()); ++i)
			sb.append(text.charAt(i));
		for (int i = 2; i < text.length(); ++i) {
			sb.append(text.charAt(i));
			if (
					Character.isDigit(text.charAt(i))
							&& text.charAt(i - 1) == '.'
							&& Character.isDigit(text.charAt(i - 2))
					) {
				int dI = text.substring(i).indexOf(' ') - 1;
				if (dI < 0)
					i = text.length() - 2;
				else
					i += dI;
			}
		}
		return sb.toString();
	}

	public static double extractRedFromCssRGB(String cssRGB) {
		return getDoubleFromString(cssRGB, 0);
	}

	public static double extractGreenFromCssRGB(String cssRGB) {
		return getDoubleFromString(cssRGB, 1);
	}

	public static double extractBlueFromCssRGB(String cssRGB) {
		return getDoubleFromString(cssRGB, 2);
	}

	public static double getDoubleFromString(String string, int ind){
		int curInd = 0;
		StringBuilder curNum = new StringBuilder();
		for(int i = 0; i < string.length(); ++i)
			if(Character.isDigit(string.charAt(i))){
				curNum.setLength(0);
				for(int j = i; j < string.length(); ++j) {
					if(Character.isDigit(string.charAt(j))
					|| string.charAt(j) == '.' && j + 1 < string.length() && Character.isDigit(string.charAt(j + 1)))
						curNum.append(string.charAt(j));
					else {
						i = j - 1;
						break;
					}
				}
				if(curInd++ == ind)
					return Double.parseDouble(curNum.toString());
			}
		System.err.println("Helper.getDoubleFromString(String, int) NO DOUBLE IN SUCH INDEX");
		return -1;
	}
}
