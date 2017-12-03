import javafx.application.Application;
import javafx.stage.Stage;
import org.opencv.core.Core;
import view.nodes.wcbPane.WCBTypes;
import view.windows.stage.MyStage;

public class Main extends Application{

	static { System.loadLibrary(Core.NATIVE_LIBRARY_NAME);}

    public static void main(String[] args) {
		//System.out.println(new File("C://opencv/sources/data/haarcascades/haarcascade_frontalface_alt.xml").exists());
		//System.out.println(new File("haarcascade_frontalface_alt.xml").exists());
		launch(args);
    }

    public void start(Stage primaryStage) throws Exception {
		primaryStage = new MyStage("/stages/mainStage/mainStageView1.fxml", WCBTypes.ICONIFIED, WCBTypes.MXMIZE, WCBTypes.CLOSE);
		((MyStage)primaryStage).animatingShow();
	}
}
