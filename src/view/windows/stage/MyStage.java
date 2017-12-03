package view.windows.stage;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.StageStyle;
import view.nodes.wcPane.WCPane;
import view.nodes.wcbPane.WCBTypes;

import java.io.IOException;

/**
 * Created by Svyatoslav on 22.06.2017.
 */
public class MyStage extends AnimatingStage {
	private double x = -1, y = -1;
	private FXMLLoader loader;
	private WCPane wcPane;

	public MyStage() {

	}

	public FXMLLoader getLoader(){
		return loader;
	}

	public WCPane getWcPane() {
		return wcPane;
	}

	public MyStage(String fxml, WCBTypes... buttons) { // root must be Anchor Pane !!!
		super(StageStyle.TRANSPARENT);
		setScene(fxml);
		init(buttons);
	}

	public MyStage(String fxml){
		this(fxml, WCBTypes.ICONIFIED, WCBTypes.CLOSE);
	}

	public MyStage(Parent root) {
		super(StageStyle.TRANSPARENT);
		setScene(new Scene(root));
		init(WCBTypes.ICONIFIED, WCBTypes.CLOSE);
	}

	public MyStage(Parent root, WCBTypes... buttons) {
		super(StageStyle.TRANSPARENT);
		setScene(new Scene(root));
		init(WCBTypes.CLOSE);
	}

	public void init(WCBTypes... buttons){
		initMoving();
		initView();
		addWCP(buttons);
	}

	public void initView(){
		getScene().setFill(Color.TRANSPARENT);
		getIcons().add(new Image("logo.png"));
	}

	public void initMoving() {
		if(getScene() == null)return;
		getScene().setOnMousePressed(mp -> {
			x = mp.getX();
			y = mp.getY();
		});
		getScene().setOnMouseReleased(mr -> {
			x = -1;
			y = -1;
		});
		getScene().setOnMouseDragged(md -> {
			setX(md.getScreenX() - x);
			setY(md.getScreenY() - y);
		});
	}

	private void addWCP(WCBTypes... buttons){
		wcPane = new WCPane(buttons);
		((Pane)getScene().getRoot()).getChildren().add(wcPane);
	}

	public void setScene(String fxml){
		try {
			loader = new FXMLLoader(getClass().getResource(fxml));
			setScene(new Scene(loader.load()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void addWCP(WCPane wcPane){
		if(wcPane != null)this.wcPane = wcPane;
	}
}
