package stages.mainStage;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.input.Clipboard;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import model.helpers.FileProcessor;
import model.helpers.ImageProcessor;
import model.helpers.Patterns;
import nodes.imViewContainer.ImViewContainer;
import nodes.imViewContainer.PlanView;
import org.opencv.core.Rect;
import view.windows.alert.AlertController;
import view.windows.notifications.NotificationsController;
import view.windows.stage.MyStage;

import java.io.File;
import java.util.ArrayList;

public class MainStageController {


	private File srcImageFile = new File("curImage.png");

	public static ImageProcessor imageProcessor = new ImageProcessor();
	public static FileProcessor fileProcessor = new FileProcessor();

	@FXML
	public CheckBox historicalAreaBox;
	@FXML
	private Button pasteButton;
	@FXML
	private Button loadImageButton;
	@FXML
	private Button placeSignButton;
	@FXML
	private Button saveImageButton;
	@FXML
	private Label dNdLabel;
	@FXML
	private ImViewContainer imageView;
	@FXML
	private VBox imageViewVBox;

	public static File INFO = new File("C:/Users/asus/Desktop/annotations_buildings.txt");
	public static File BG = new File("C:/Users/asus/Desktop/bg.txt");
	public static File POSITIVES = new File("C:/Users/asus/Desktop/positives");
	public static File NEGATIVES = new File("C:/Users/asus/Desktop/testOut");

	@FXML
	public void initialize() {
		//openDataTool(null);
		Patterns.fillPatterns(new File("doors.txt"), new File("windows.txt"));
		imageViewVBox.widthProperty().addListener(((observable, oldValue, newValue) -> {
			imageView.setFitWidth((imageView.getImage() == null) ? 0 : imageViewVBox.getWidth());
		}));
		imageViewVBox.heightProperty().addListener(((observable, oldValue, newValue) -> {
			imageView.setFitHeight((imageView.getImage() == null) ? 0 : imageViewVBox.getHeight());
		}));

	}

	public void placeSign(ActionEvent actionEvent) {
		if (srcImageFile != null) {
			ArrayList<Rect> objects1 = imageProcessor.detectObjects(srcImageFile.getAbsolutePath(), ImageProcessor.doorsCascade);
			ArrayList<Rect> objects2 = imageProcessor.detectObjects(srcImageFile.getAbsolutePath(), ImageProcessor.windowsCascade);
			if (objects1 == null || objects2 == null) {
				NotificationsController.showError("Помилка обробки зображення, об'єктів не знайдено");
				return;
			}

			setImage(imageProcessor.drawRects(srcImageFile.getAbsolutePath(), objects1, objects2));

			placeSignboards(objects1, objects2);
			NotificationsController.showComplete("Обробку зображення завершено");
			if (objects1.isEmpty())
				NotificationsController.showInfo("Дверей не знайдено");
			if (objects2.isEmpty())
				NotificationsController.showInfo("Вікон не знайдено");
		} else NotificationsController.showWarning("Немає зображення");
	}

	public void placeSignboards(ArrayList<Rect>... objects) {
		double coefX = imageView.getWidth() / imageView.getImage().getWidth();
		double coefY = imageView.getHeight() / imageView.getImage().getHeight();


			for (Rect r : objects[0]) {
				imageView.addSign(r);
			}
	}

	public void saveImage(ActionEvent actionEvent) {
//		imageViewVBox.getChildren().remove(imageView);
//		imageView = ImViewContainer.copy(imageView);
//		imageViewVBox.getChildren().add(imageView);
		if (imageView.getMarkups().isEmpty()){
			AlertController.showInfo("Додайте розмітку на зображння");
			return;
		}
		if (imageView.getMarkups().get(0).getLength().getText().isEmpty()){
			AlertController.showInfo("Введіть значення у розмітку");
			return;
		}
		if (imageView.getImage() == null) {
			NotificationsController.showWarning("Нема чого зберігати");
			return;
		}
		File choice;
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Save Image");
		fileChooser.setInitialDirectory(new File(System.getProperty("user.home") + "/Desktop"));
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", ".png", ".jpg", ".jpeg"));
		choice = fileChooser.showSaveDialog(imageView.getScene().getWindow());
		if(choice == null)
			return;

		PlanView plan = new PlanView(imageView);
		WritableImage planImage = new WritableImage((int)plan.getPrefWidth(), (int)plan.getPrefHeight());
		new Scene(plan).snapshot(planImage);
		if(choice != null) {
			File planFile = new File(
					choice.getParentFile().getAbsolutePath()
					+ File.separatorChar + choice.getName().substring(0, choice.getName().indexOf('.')) + "_plan"
					+ choice.getName().substring(choice.getName().indexOf('.'))
			);
			fileProcessor.saveImage(planImage, planFile);
		}

		ImViewContainer imViewCopy = ImViewContainer.copy(imageView);
		WritableImage image = new WritableImage(
				(int)imViewCopy.getWidth() + 1, (int)imViewCopy.getHeight() + 1
		);
		new Scene(imViewCopy).snapshot(image);

		if (choice != null)
			fileProcessor.saveImage(image, choice);
	}

	public void loadImage(ActionEvent actionEvent) {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setSelectedExtensionFilter(
				new FileChooser.ExtensionFilter("only view.images", ".png", "jpeg", "jpg")
		);

		File choice;
		if ((choice = fileChooser.showOpenDialog(imageView.getScene().getWindow())) != null) {
			if (new Image(choice.toURI().toString()).isError())
				AlertController.showInfo("Choose correct image, please");
		} else return;
		loadImage(choice);
	}

	public void loadImage(File srcImageFile) {
		this.srcImageFile = srcImageFile;
		setImage(srcImageFile);
		historicalAreaBox.setSelected(AlertController.showConfirm("Чи знаходиться будівля у межах історичного ареалу?"));
		imageView.setHistoricalArea(historicalAreaBox.isSelected());
	}

	public void onDragOver(DragEvent dragEvent) {
		imageViewVBox.setStyle("-fx-background-color: rgba(0, 0, 255, 0.7)");
		if (dragEvent.getDragboard().hasFiles()
				&& dragEvent.getDragboard().getFiles().size() == 1
				&& dragEvent.getDragboard().getFiles().get(0) != null
				&& dragEvent.getDragboard().getFiles().get(0).isFile()
				&& dragEvent.getDragboard().getFiles().get(0).exists()
				&& !new Image(dragEvent.getDragboard().getFiles().get(0).toURI().toString()).isError()
				) {
			dragEvent.acceptTransferModes(TransferMode.ANY);
		}
	}

	public void setImage(File file) {    // sets showable image
		if (imageView.getImage() == null && srcImageFile.exists() && !new Image(srcImageFile.toURI().toString()).isError())
			imageViewVBox.getChildren().remove(dNdLabel);
		else if (imageView != null && (!srcImageFile.exists() || new Image(file.toURI().toString()).isError()))
			imageViewVBox.getChildren().add(dNdLabel);
		setImage(new Image(file.toURI().toString()));
	}

	public void setImage(Image image) {
//		if (!new Image(srcImageFile.toURI().toString()).isError())
//			imageViewVBox.getChildren().remove(dNdLabel);
//		else
//			imageViewVBox.getChildren().add(dNdLabel);
		imageViewVBox.setStyle("");

		imageView.setImage(image);
		if (imageView.getImage() != null)
			imageViewVBox.getChildren().remove(dNdLabel);
		else
			imageViewVBox.getChildren().add(dNdLabel);
		imageView.setFitHeight(imageViewVBox.getHeight());
		imageView.setFitWidth(imageViewVBox.getWidth());
	}

	public void onDragDropped(DragEvent dragEvent) {
		imageViewVBox.setStyle("");
		File im = dragEvent.getDragboard().getFiles().get(0);
		loadImage(im);
		NotificationsController.showComplete("Зображення\n" + im.getName() + "\nвдало завантажено");
	}

	public void onDragDone(DragEvent dragEvent) {
		imageViewVBox.setStyle("");
	}

	public void onDragExited(DragEvent dragEvent) {
		imageViewVBox.setStyle("");
	}

	public void pasteImageFromCB(ActionEvent actionEvent) {
		if (Clipboard.getSystemClipboard().hasImage())
			setImage(Clipboard.getSystemClipboard().getImage());
	}

	public void discardChanges(ActionEvent actionEvent) {
		setImage(srcImageFile);
	}

	public void openDataTool(ActionEvent actionEvent) {
		new MyStage("/cascadeTraining/view/view.fxml").animatingShow();
	}

	public void addSign(ActionEvent actionEvent) {
		imageView.addSign();
	}

	public void addMarkup(ActionEvent actionEvent) {
		imageView.addMarkup();
	}

	public void setHistoricalArea(ActionEvent actionEvent) {
		imageView.setHistoricalArea(historicalAreaBox.isSelected());
	}
}
