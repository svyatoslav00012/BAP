package view.windows.stage;

import javafx.stage.Modality;

/**
 * Created by Svyatoslav on 06.08.2017.
 */
public class ModalStage extends MyStage {

	public ModalStage(){
		initMoving();
		initModality(Modality.APPLICATION_MODAL);
	}

	public ModalStage(String fxml) {
		super(fxml);
		initModality(Modality.APPLICATION_MODAL);
	}

}
