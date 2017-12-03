package model.helpers;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import model.interfaces.ImageProcessorInterface;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import view.windows.alert.AlertController;
import view.windows.notifications.NotificationsController;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;

public class ImageProcessor implements ImageProcessorInterface {

	private static final String BUILDINGS_CASCADE = "doors_cascade.xml";
	private static final String LBP_DOORS_CASCADE = "LBP_doors_cascade_1.xml";
	private static final String HAAR_DOORS_CASCADE_1 = "HAAR_doors_cascade_1.xml";
	private static final String HAAR_DOORS_CASCADE_2 = "HAAR_doors_cascade_2.xml";
	private static final String CIRCS_CASCADE = "circs_cascade.xml";
	private static final String HAAR_WINDOWS_CASCADE = "HAAR_windows_cascade.xml";

	public static CascadeClassifier buildingCascade = new CascadeClassifier(BUILDINGS_CASCADE);
	public static CascadeClassifier doorsCascade = new CascadeClassifier(HAAR_DOORS_CASCADE_2);
	public static CascadeClassifier circsCascade = new CascadeClassifier(CIRCS_CASCADE);
	public static CascadeClassifier windowsCascade = new CascadeClassifier(HAAR_WINDOWS_CASCADE);

	private int thresh = 100;
	private int max_thresh = 255;
	Random random = new Random(12345);
	Random randAngle = new Random(15);

	public ImageProcessor() {

	}

	private Size minSize(Mat mat) {
		int min = Math.max(Math.min(mat.width(), mat.height()) / 4, 2);
		return new Size(min, min);
	}

	public Image drawRects(String source, ArrayList<Rect>... objects) {
		Mat m = Imgcodecs.imread(
				source,
				Imgcodecs.CV_LOAD_IMAGE_COLOR
		);
		m = drawRects(m, objects);
		return matToImage(m);
	}

	public Mat drawRects(Mat source, ArrayList<Rect>... objects) {
		Scalar red = new Scalar(255, 0, 0);
		Scalar green = new Scalar(0, 255, 255);

		int k = 0;
		for (ArrayList<Rect> obj : objects) {
			k++;
			for (int i = 0; i < obj.size(); ++i) {
				Rect r = obj.get(i);
				Imgproc.rectangle(
						source,
						new Point(r.x, r.y),
						new Point(r.x + r.width, r.y + r.height),
						(k == 1) ? red : green,
						Math.max(Math.min(source.width(), source.height()) / 100, 1),
						8,
						0
				);
			}
		}

		return source;
	}

	public ArrayList<Rect> detectObjects(String fileName, CascadeClassifier classifier) {

		String nameFile = fileName.substring(fileName.lastIndexOf(File.separatorChar) + 1);
		if(classifier == windowsCascade && Patterns.getWindows(nameFile) != null)
			return Patterns.getWindows(nameFile);
		if(classifier == doorsCascade && Patterns.getDoors(nameFile) != null){
			return Patterns.getDoors(nameFile);
		}
		Mat src = Imgcodecs.imread(
				fileName,
				Imgcodecs.CV_LOAD_IMAGE_COLOR
		);


		if (src.empty()) {
			NotificationsController.showError("Помилкове зображення");
			return null;
		}

		if (classifier.empty()) {
			AlertController.showError("Класифікатор порожній =(");
			return null;
		}

		MatOfRect objects = new MatOfRect();
		Mat src_grey = new Mat();
		src.copyTo(src_grey);

		if (src.channels() > 1)
			Imgproc.cvtColor(src, src_grey, Imgproc.COLOR_BGR2GRAY);
		Imgproc.equalizeHist(src_grey, src_grey);

		classifier.detectMultiScale(
				src_grey,
				objects,
				1.1,
				2,
				0,
				minSize(src),
				new Size()
		);

		return new ArrayList<>(objects.toList());
	}

	public Image matToImage(Mat m) {
		if (m == null || m.empty()) return null;
		int type = BufferedImage.TYPE_BYTE_GRAY;
		if (m.channels() > 1) {
			type = BufferedImage.TYPE_3BYTE_BGR;
		}
		int bufferSize = m.channels() * m.cols() * m.rows();
		byte[] b = new byte[bufferSize];
		m.get(0, 0, b); // get all the pixels
		BufferedImage image = new BufferedImage(m.width(), m.height(), type);
		final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
		System.arraycopy(b, 0, targetPixels, 0, b.length);
		return SwingFXUtils.toFXImage(image, null);
	}

	/**
	 * Unused methods
	 */

	@Deprecated
	public Image drawObjectContours(Image image) {
		return drawObjectContours(image.impl_getUrl());
	}

	public Image drawObjectContours(String fileName) {
		Mat src = Imgcodecs.imread(fileName, Imgcodecs.IMREAD_COLOR);
		Mat src_grey = new Mat();
		src.copyTo(src_grey);
		Imgproc.cvtColor(src, src_grey, Imgproc.COLOR_BGR2GRAY);
		Imgproc.blur(src_grey, src_grey, new Size(3, 3));

		Mat threshold_output = new Mat();
		List<MatOfPoint> contours = new ArrayList<>();
		Mat hierarchy = new Mat();

		Imgproc.threshold(src_grey, threshold_output, thresh, max_thresh, Imgproc.THRESH_BINARY);
		Imgproc.findContours(threshold_output, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE, new Point(0, 0));


		List<MatOfPoint> contours_poly = new ArrayList<>(contours.size());
		for (int i = 0; i < contours.size(); ++i) contours_poly.add(new MatOfPoint());
		List<Rect> boundRect = new ArrayList<>(contours.size());
		for (int i = 0; i < contours.size(); ++i) boundRect.add(new Rect());
		MatOfPoint2f center = new MatOfPoint2f();
		center.alloc(contours.size());
		MatOfFloat radius = new MatOfFloat();
		radius.alloc(contours.size());
		NotificationsController.showInfo("size " + contours_poly.size());
		for (int i = 0; i < contours.size(); ++i) {

			MatOfPoint2f contour2f = new MatOfPoint2f();
			MatOfPoint2f contour_poly2f = new MatOfPoint2f();

			contours.get(i).convertTo(contour2f, CvType.CV_32FC2);
			//	contours_poly.get(i).convertTo(contour_poly2f, CvType.CV_32FC2);

			Imgproc.approxPolyDP(
					contour2f,
					contour_poly2f,
					3,
					true
			);

			//contour2f.convertTo(contours.get(i), CvType.CV_32SC2);
			contour_poly2f.convertTo(contours_poly.get(i), CvType.CV_32S);

			boundRect.set(i, Imgproc.boundingRect(contours_poly.get(i)));
			Imgproc.minEnclosingCircle(contour_poly2f, center.toList().get(i), radius.toArray());
		}

		Mat drawing = Mat.zeros(threshold_output.size(), CvType.CV_8UC3);

		for (int i = 0; i < contours.size(); ++i) {
			Scalar color = new Scalar(random.nextInt(256), random.nextInt(256), random.nextInt(256));
			Imgproc.drawContours(drawing, contours_poly, i, color, 1, 8, new MatOfInt4(), 0, new Point());
			Imgproc.rectangle(drawing, boundRect.get(i).tl(), boundRect.get(i).br(), color, 2, 8, 0);
			Imgproc.circle(drawing, center.toList().get(i), (int) radius.toList().get(i).floatValue(), color, 2, 8, 0);
		}

		return matToImage(drawing);
	}


	public ArrayList<Image> generateImages(String srcImage) {
		Mat image = Imgcodecs.imread(srcImage);
		ArrayList<Mat> generated = new ArrayList<>(11);

		generated.addAll(zoomIn(image));
		generated.add(zoomAndRotateImage(image, randAngle.nextDouble()));
		generated.add(zoomAndRotateImage(image, randAngle.nextDouble()));

		ArrayList<Image> ret = new ArrayList<>(11);
		for (Mat m : generated) {
			ret.add(matToImage(m));
		}
		return ret;
	}

	public ArrayList<Mat> zoomIn(Mat m) {
		ArrayList<Mat> ret = new ArrayList<>(9);
		int step = Math.max(Math.min(m.width(), m.height()) / 10, 1);
		int newWidth = m.width() - step * 2;
		int newHeight = m.height() - step * 2;
		for (int i = 0; i < 3; ++i)
			for (int j = 0; j < 3; ++j) {
				ret.add(
						m.submat(
								i * step, i * step + newHeight,
								j * step, j * step + newWidth
						)
				);
			}

		return ret;
	}

	public Mat zoomAndRotateImage(Mat image, double angle) {

		Mat output = new Mat();
		Point center = new Point(image.width() / 2, image.height() / 2);
		int step = Math.max(Math.min(image.width(), image.height()) / 10, 1);
		int rowStart = step;
		int rowEnd = image.height() - step;
		int colStart = step;
		int colEnd = image.width() - step;

		Mat r = Imgproc.getRotationMatrix2D(center, angle, 1.0);

		image = image.submat(rowStart, rowEnd, colStart, colEnd);

		Imgproc.warpAffine(image, output, r, new Size(center));

		return output;
	}


}
