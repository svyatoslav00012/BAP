package view.windows.imageViewer;

import javafx.event.Event;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.Screen;
import javafx.stage.StageStyle;
import view.windows.stage.MyStage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class ImageViewer extends MyStage {

	private static final String STYLESHEET = "/library/view/nodes/imageViewer/ImageViewerStyle.css";
	private static final Image DEFAULT_IMAGE = new Image("/resources/view.images/errorim.png");

	private AnchorPane root = new AnchorPane();
	private HBox imageViewBox;
	private ImageView imageView;
	private Button btnClose;
	private Button btnNext;
	private Button btnPrev;

	public ImageViewer(Image im){
		init();
		setImage(im);
		smoothlyShow();
	}

	public ImageViewer(File f){
		init();
		try {
			if(!f.exists() || !f.isFile() || new Image(new FileInputStream(f)).isError())setErrorImage();
			else setImage(new Image(new FileInputStream(f)));
			smoothlyShow();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void setErrorImage(){
		setImage(DEFAULT_IMAGE);
	}

	private void init(){
		initStyle(StageStyle.TRANSPARENT);
		setMaximized(true);
		initNodes();
		root.getStylesheets().add(STYLESHEET);
		initAnchor();
		setScene(new Scene(root));
		super.initView();
	}

	private void initNodes(){
		root.setId("root");
		imageView = new ImageView();
		imageViewBox = new HBox(imageView);
		imageViewBox.setAlignment(Pos.CENTER);
		btnClose = new Button();
		root.getChildren().addAll(
				imageViewBox,
				btnClose
		);
		initButtons();
		imageViewBox.setOnMouseClicked(clicked -> smoothlyClose());
		for(Node n : imageViewBox.getChildren())n.setOnMouseClicked(Event::consume);
	}

	private void initButtons(){
		btnClose.setId("btnClose");
		btnClose.setPrefSize(100, 100);
		btnClose.setOnAction(close -> smoothlyClose());
	}

	private void initAnchor(){
		AnchorPane.setBottomAnchor(imageViewBox, 0.0);
		AnchorPane.setRightAnchor(imageViewBox, 0.0);
		AnchorPane.setTopAnchor(imageViewBox, 0.0);
		AnchorPane.setLeftAnchor(imageViewBox, 0.0);

		AnchorPane.setTopAnchor(btnClose, 10.0);
		AnchorPane.setRightAnchor(btnClose, 10.0);
	}

	public void setImage(Image im){
		imageView.setImage(im);
//		if(im.getHeight()>im.getWidth())imageView.setFitHeight(Screen.getPrimary().getVisualBounds().getHeight());
//		else imageView.setFitWidth(Screen.getPrimary().getVisualBounds().getWidth());
		imageView.setFitHeight(Screen.getPrimary().getVisualBounds().getHeight());
		imageView.setFitWidth(Screen.getPrimary().getVisualBounds().getWidth());
		imageView.setPreserveRatio(true);
	}

}
