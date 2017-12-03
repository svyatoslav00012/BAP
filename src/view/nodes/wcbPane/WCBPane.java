package view.nodes.wcbPane;

import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import view.windows.alert.AlertController;
import view.windows.stage.MyStage;

/**
 * Created by Svyatoslav on 04.08.2017.
 */
public class WCBPane extends HBox{ //Window Control Button Panel

	private Button iconifiedButton;
	private Button mxmizeButton;
	private Button closeButton;

	public Button getHideButton(){
		return iconifiedButton;
	}

	public Button getMxmizeButton() {
		return mxmizeButton;
	}

	public Button getCloseButton() {
		return closeButton;
	}

	public WCBPane(double height, int spacing, WCBTypes... buttons){
		getStylesheets().add("/view/nodes/wcbPane/wcbPaneStyle.css");
		setPrefHeight(height);
		setSpacing(spacing);
		initButtons();
		setFillHeight(true);
		for(WCBTypes type: buttons){
			if(type == WCBTypes.ICONIFIED && !getChildren().contains(iconifiedButton))
				getChildren().add(iconifiedButton);
			if(type == WCBTypes.MXMIZE && !getChildren().contains(mxmizeButton))
				getChildren().add(mxmizeButton);
			if(type == WCBTypes.CLOSE && !getChildren().contains(closeButton))
				getChildren().add(closeButton);
		}
	}

	private void initButtons(){
		iconifiedButton = new Button();
		mxmizeButton = new Button();
		closeButton = new Button();
		initButtonsBehavior();
		initButtonsStyle();
	}

	private void initButtonsBehavior(){
		iconifiedButton.setOnAction(hide -> ((MyStage)getScene().getWindow()).setIconified(true));
		mxmizeButton.setOnAction(mxmize -> ((MyStage)getScene().getWindow()).setMaximized(
				!((MyStage)getScene().getWindow()).isMaximized()
		));
		closeButton.setOnAction(mxmize -> {
			try {
				((MyStage) getScene().getWindow()).animatingClose();
			} catch (Exception e){
				AlertController.showException(e);
			}
		});
		iconifiedButton.setFocusTraversable(false);
		mxmizeButton.setFocusTraversable(false);
		closeButton.setFocusTraversable(false);
	}

	private void initButtonsStyle(){
		iconifiedButton.setId("buttonIconif");
		mxmizeButton.setId("buttonMxmize");
		closeButton.setId("buttonClose");
		iconifiedButton.setPrefSize(getPrefHeight(), getPrefHeight());
		mxmizeButton.setPrefSize(getPrefHeight(), getPrefHeight());
		closeButton.setPrefSize(getPrefHeight(), getPrefHeight());
	}
}
