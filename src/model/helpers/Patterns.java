package model.helpers;

import model.helpers.ObjectCopying.DeepCopy;
import org.opencv.core.Rect;

import java.io.*;
import java.util.ArrayList;
import java.util.Hashtable;

public class Patterns {

	private static Hashtable<String, ArrayList<Rect>> windows = new Hashtable<>();
	private static Hashtable<String, ArrayList<Rect>> doors = new Hashtable<>();

	public static void fillPatterns(File doors, File windows) {
		readDoorsPatterns(doors);
		readWindowsPatterns(windows);
	}

	public static void readWindowsPatterns(File file) {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			String in;
			while ((in = reader.readLine()) != null) {
				String[] vals = in.split(" ");
				String key = vals[0].substring(vals[0].lastIndexOf(File.separatorChar) + 1);
				int countRects = Integer.parseInt(vals[1]);

				ArrayList<Rect> arr = new ArrayList<>();
				for (int i = 0; i < countRects; ++i) {
					int x = Integer.parseInt(vals[1 + i * 4 + 1]);
					int y = Integer.parseInt(vals[1 + i * 4 + 2]);
					int w = Integer.parseInt(vals[1 + i * 4 + 3]);
					int h = Integer.parseInt(vals[1 + i * 4 + 4]);
					arr.add(new Rect(x, y, w, h));
				}
				windows.put(key, arr);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void readDoorsPatterns(File file) {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			String in;
			while ((in = reader.readLine()) != null) {
				String[] vals = in.split(" ");
				String key = vals[0].substring(vals[0].lastIndexOf(File.separatorChar) + 1);
				int countRects = Integer.parseInt(vals[1]);

				ArrayList<Rect> arr = new ArrayList<>();
				for (int i = 0; i < countRects; ++i) {
					int x = Integer.parseInt(vals[1 + i * 4 + 1]);
					int y = Integer.parseInt(vals[1 + i * 4 + 2]);
					int w = Integer.parseInt(vals[1 + i * 4 + 3]);
					int h = Integer.parseInt(vals[1 + i * 4 + 4]);
					arr.add(new Rect(x, y, w, h));
				}
				doors.put(key, arr);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static ArrayList<Rect> getWindows(String key) {
		simulateWork();
		ArrayList<Rect> arr = windows.get(key);
		if(arr == null) return arr;
		ArrayList<Rect> copy = new ArrayList<>();
		for(Rect r: arr){
			copy.add(new Rect(r.x, r.y, r.width, r.height));
		}
		return copy;
	}

	public static ArrayList<Rect> getDoors(String key) {
		simulateWork();
		ArrayList<Rect> arr = doors.get(key);
		if(arr == null) return arr;
		ArrayList<Rect> copy = new ArrayList<>();
		for(Rect r: arr){
			copy.add(new Rect(r.x, r.y, r.width, r.height));
		}
		return copy;
	}

	public static void simulateWork(){
		try{
			Thread.sleep(200 + (long)(Math.random() * 100));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
