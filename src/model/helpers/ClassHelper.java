package model.helpers;

import java.lang.reflect.Field;
import java.util.ArrayList;

/**
 * Created by Svyatoslav on 10.07.2017.
 */
public class ClassHelper {
	public void copy(Object from, Object to) {
		if (from.getClass() != to.getClass()) {
			System.out.println("classes don't similar");
		}
		Class common = getCommonClass(from, to);
		try {
			for (Field f : common.getDeclaredFields()) f.set(to, f.get(from));
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	public Class getCommonClass(Object first, Object second) {
		return getCommonClass(first.getClass(), second.getClass());
	}

	public Class getCommonClass(Class first, Class second) {
		ArrayList<Class> firstClasses = new ArrayList<>();
		ArrayList<Class> secondClasses = new ArrayList<>();
		firstClasses.add(first);
		secondClasses.add(second);
		while (true) {
			if (first == Object.class && second == Object.class) break;
			if (first != Object.class) {
				first = first.getSuperclass();
				firstClasses.add(0, first);
			}
			if (second != Object.class) {
				second = second.getSuperclass();
				secondClasses.add(0, second);
			}
		}
		for (int i = 0; i < Math.max(firstClasses.size(), secondClasses.size()); ++i) {
			if (i < firstClasses.size()) System.out.print(firstClasses.get(i).getName() + "\t");
			else System.out.println("\t\t");
			if (i < secondClasses.size()) System.out.print(secondClasses.get(i).getName());
			System.out.println();
		}
		for (int i = 0; i < Math.min(firstClasses.size(), secondClasses.size()); ++i)
			if (firstClasses.get(i) == secondClasses.get(i)) first = firstClasses.get(i);
			else break;
		return first;
	}
}
