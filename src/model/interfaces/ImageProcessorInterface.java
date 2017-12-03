package model.interfaces;

import javafx.scene.image.Image;
import org.opencv.core.Rect;
import org.opencv.objdetect.CascadeClassifier;

import java.util.ArrayList;

public interface ImageProcessorInterface {

	@Deprecated
	Image drawObjectContours(Image image);

	Image drawObjectContours(String fileName);

	ArrayList<Rect> detectObjects(String filename, CascadeClassifier classifier);
}
