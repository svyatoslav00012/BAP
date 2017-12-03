package cascadeTraining.view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import model.helpers.FileProcessor;
import model.helpers.OpenCVUtils;
import view.windows.alert.AlertController;
import view.windows.notifications.NotificationsController;

import java.io.*;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

public class Controller {

	private FileProcessor fileProcessor = new FileProcessor();

	private Properties properties = new Properties();
	private File propsFile = new File("properties.ini");

	private File workDir = null;

	private File data = null;
	private File pos = null;
	private File info = null;
	private File newPos = null;
	private File newInfo = null;
	private File neg = null;
	private File bg = null;
	private File negSource = null;
	private File vecFile = null;


	//prepearinng data
	@FXML
	private Hyperlink chooseDirLink;
	@FXML
	private Button createDataButton;
	@FXML
	private Button createNewPosButton;
	@FXML
	private Button createNewInfoButton;
	@FXML
	private Button createPosButton;
	@FXML
	private Button createInfoButton;
	@FXML
	private Button createNegButton;
	@FXML
	private Button createBgButton;
	@FXML
	private Button createNegSourceButton;
	@FXML
	private Button createVecFileButton;

	@FXML
	private Button rewriteBgButton;
	@FXML
	private Button rewriteInfoButton;
	@FXML
	private Button rewriteNewInfoButton;
	@FXML
	private Button annotationInfoButton;
	@FXML
	private Button annotationNewInfoButton;
	@FXML
	private Button mergeInfoButton;
	@FXML
	private Hyperlink relPathLink;
	@FXML
	private Hyperlink pthLvlUpLink;

	//create samples
	@FXML
	private VBox samplesParamsVBox;
	@FXML
	private TextField heightField;
	@FXML
	private TextField widthField;
	@FXML
	private TextField numSampsField;
	@FXML
	private Button createSamplesButton;

	//train cascade
	@FXML
	private VBox trainParamsVBox;
	@FXML
	private TextField numPosField;
	@FXML
	private TextField numNegField;
	@FXML
	private TextField numStagesField;
	@FXML
	private TextField pVBSField;
	@FXML
	private TextField pIBSField;
	@FXML
	private TextField numThreadsField;
	@FXML
	private Button trainCascadeButton;

	@FXML
	public void initialize() {
		try {

			if (!propsFile.exists()) {
				propsFile.createNewFile();
				storeProps();
			}
			properties.load(new FileInputStream(propsFile));

			String path = properties.getProperty("workDir", "Choose work directory");
			if (path.equals("Choose work directory"))
				workDir = null;
			else {
				workDir = new File(path);
				chooseDirLink.setText(path);
				initDirsAndFiles();
			}


		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		System.out.println(pos.getName());

	}

	//prepare data
	public void chooseDir(ActionEvent actionEvent) {
		File choice = new DirectoryChooser().showDialog(chooseDirLink.getScene().getWindow());
		if (choice != null) {
			workDir = choice;
			initDirsAndFiles();
			chooseDirLink.setText(workDir.getAbsolutePath());
			properties.put(cascadeTraining.Properties.WORK_DIR, workDir.getAbsolutePath());
			storeProps();
		}
	}

	public void initDirsAndFiles() {
		data = new File(workDir.getAbsolutePath() + "/" + "data");
		createDataButton.setDisable(data.exists());
		pos = new File(workDir.getAbsolutePath() + "/" + "Pos");
		createPosButton.setDisable(pos.exists());
		info = new File(workDir.getAbsolutePath() + "/" + "info.txt");
		createInfoButton.setDisable(info.exists());
		newPos = new File(workDir.getAbsolutePath() + "/" + "newPos");
		createNewPosButton.setDisable(newPos.exists());
		newInfo = new File(workDir.getAbsolutePath() + "/" + "newInfo.txt");
		createNewInfoButton.setDisable(newInfo.exists());
		neg = new File(workDir.getAbsolutePath() + "/" + "Neg");
		createNegButton.setDisable(neg.exists());
		bg = new File(workDir.getAbsolutePath() + "/" + "bg.txt");
		createBgButton.setDisable(bg.exists());
		negSource = new File(workDir.getAbsolutePath() + "/" + "NegSource");
		createNegSourceButton.setDisable(negSource.exists());
		vecFile = new File(workDir.getAbsolutePath() + "/" + "vecFile.vec");
		createVecFileButton.setDisable(vecFile.exists());
	}

	public void createFile(ActionEvent actionEvent) {
		if(!AlertController.showConfirm("Create new file?"))return;
		try {
			if (actionEvent.getSource() == createDataButton)
				data.mkdir();
			if (actionEvent.getSource() == createPosButton)
				pos.mkdir();
			if (actionEvent.getSource() == createNewPosButton)
				newPos.mkdir();
			if (actionEvent.getSource() == createNegSourceButton)
				negSource.mkdir();
			if (actionEvent.getSource() == createNegButton)
				neg.mkdir();

			if (actionEvent.getSource() == createInfoButton)
				info.createNewFile();
			if (actionEvent.getSource() == createNewInfoButton)
				newInfo.createNewFile();
			if (actionEvent.getSource() == createBgButton)
				bg.createNewFile();

			initDirsAndFiles();

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void rewriteInfo(ActionEvent actionEvent) {
		if(!AlertController.showConfirm("rewrite info.txt ?"))return;
		fileProcessor.writeAnnotations(pos, info);
		setInfoPathToRelative();
	}

	public void rewriteNewInfo(ActionEvent actionEvent) {
		if(!AlertController.showConfirm("rewrite newInfo.txt ?"))return;
		fileProcessor.writeAnnotations(newPos, newInfo);
		setNewInfoPathToRelative();
	}

	public void annotationInfo(ActionEvent actionEvent) {
		if(!AlertController.showConfirm("run annotation on info.txt ?"))return;
		OpenCVUtils.opencv_annotation(pos, info);
		setInfoPathToRelative();
	}

	public void annotationNewInfo(ActionEvent actionEvent) {
		if(!AlertController.showConfirm("run annotation on newInfo.txt ?"))return;
		OpenCVUtils.opencv_annotation(newPos, newInfo);
		setNewInfoPathToRelative();
	}

	public void mergeInfo(ActionEvent actionEvent) {
		if(!AlertController.showConfirm("Merge info.txt and newInfo.txt to info.txt ?"))return;
		fileProcessor.mergeFiles(newInfo, info);
		fileProcessor.copyFiles(newPos, pos);
		fileProcessor.updateRelPath(info, pos);
//		try {
//			PrintWriter pw = new PrintWriter(newInfo);
//			pw.write("");
//			pw.close();
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		}
	}

	public void rewriteBg(ActionEvent actionEvent) {
		if(!AlertController.showConfirm("rewrite bg.txt according to neg?"))return;
		fileProcessor.writeAnnotations(neg, bg);
		fileProcessor.updateRelPath(bg, neg);
		System.out.println(fileProcessor.countLines(bg));
	}

	//createSamples
	public void createSamples(ActionEvent actionEvent) {
		if(!AlertController.showConfirm("create samples ?"))return;
		if(wrongIntFields(heightField, widthField, numSampsField)){
			return;
		}
		int h = Integer.parseInt(heightField.getText());
		int w = Integer.parseInt(widthField.getText());
		int nums = Integer.parseInt(numSampsField.getText());
		OpenCVUtils.opencv_createsamples(workDir, info, vecFile, h, w, nums);
	}

	public boolean wrongIntFields(TextField... fields){
		boolean wrong = false;
		for(TextField f : fields){
			if(!isInt(f)){
				NotificationsController.showWarning("Wrong params in " + f.getId());
				wrong = true;
			}
		}
		return wrong;
	}

	private boolean isInt(TextField field){
		try{
			Integer.parseInt(heightField.getText());
			return true;
		} catch (Exception e) {
			heightField.setText("");
			NotificationsController.showWarning("Wrong parameters in height field");
			return false;
		}
	}

	//trainCascade
	public void trainCascade(ActionEvent actionEvent) {
		if(!AlertController.showConfirm("traincascade ?"))return;
		if(wrongIntFields(numPosField, numNegField, numStagesField, pVBSField, pIBSField, numThreadsField))
			return;
		int numPos = Integer.parseInt(numPosField.getText());
		int numNeg = Integer.parseInt(numNegField.getText());
		int numStages = Integer.parseInt(numStagesField.getText());
		int precalcValBufSize = Integer.parseInt(pVBSField.getText());
		int precalcIdxBufSize = Integer.parseInt(pIBSField.getText());
		int numThreads = Integer.parseInt(numThreadsField.getText());
		OpenCVUtils.opencv_traincascade(
				workDir, data, vecFile, bg, numPos, numNeg, numStages, precalcValBufSize, precalcIdxBufSize, numThreads
		);
	}

	public void generateNegImages(){
		if(!AlertController.showConfirm("generate?"))return;
		fileProcessor.duplicateImages(negSource, neg);
		fileProcessor.copyFiles(negSource, neg);
	}

	public void storeProps() {
		try {
			properties.store(new FileOutputStream(propsFile), "DataPrep");
		} catch (Exception e) {
			System.err.println("couldn't store properties");
		}
	}

	public void setInfoPathToRelative() {
		fileProcessor.updateRelPath(info, pos);
		fileProcessor.removeAll(info, pos.getName());
	}

	public void setNewInfoPathToRelative() {
		fileProcessor.updateRelPath(newInfo, newPos);
		fileProcessor.removeAll(newInfo, newPos.getName());
	}

	public void myFunction(ActionEvent actionEvent) {
		File info1 = new File(workDir.getAbsolutePath() + "\\" + "info1.txt");
		File info2 = new File(workDir.getAbsolutePath() + "\\" + "info2.txt");
		try {

			Set<String> strSet = new HashSet<String>();
			String in;

			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(info)));
			while ((in = reader.readLine()) != null){
				File f = new File(workDir.getAbsolutePath() + File.separatorChar + in.substring(0, in.indexOf(' ')));
				if(f.exists()) {
					strSet.add(in);
				}
			}
			reader.close();

			BufferedReader reader1 = new BufferedReader(new InputStreamReader(new FileInputStream(info1)));
			while ((in = reader1.readLine()) != null){
				File f = new File(workDir.getAbsolutePath() + File.separatorChar + in.substring(0, in.indexOf(' ')));
				if(f.exists()) {
					strSet.add(in);
				}
			}
			reader.close();

			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(info2)));
			for(String s : strSet) {
				writer.write(s);
				writer.newLine();
			}
			writer.close();

			System.out.println(fileProcessor.countLines(info2));

			//fileProcessor.updateRelPath(info1, pos);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
