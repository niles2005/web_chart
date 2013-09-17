/**
 * Copyright(c) 2010 XTWSoft, Inc.
 *
 * @author NieLei E-mail:niles2010@live.cn
 * @version create time：2011-9-26 下午10:28:18
 */
package com.xtwsoft.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Used to replace String default split method.
 * @author Administrator
 *
 */
public class Split {
	public static List split(String line, String regex) {
		List list = new ArrayList();
		if (line == null) {
			return list;
		}
		int pos0 = 0;
		int pos1 = line.indexOf(regex);
		while (pos1 != -1) {
			list.add(line.substring(pos0, pos1));
			pos0 = pos1 + regex.length();
			pos1 = line.indexOf(regex, pos0);
		}
		list.add(line.substring(pos0));
		return list;
	}

	public static String[] splitToArray(String line, String regex) {
		List list = split(line, regex);
		String[] array = new String[list.size()];
		for (int i = 0, size = list.size(); i < size; i++) {
			array[i] = (String) list.get(i);
		}
		return array;
	}

	public static List split(String line, String regex1, String regex2) {
		List list = new ArrayList();
		if (line == null) {
			return list;
		}
		int pos0 = 0;
		int pos1 = line.indexOf(regex1);

		int regex1Len = regex1.length();
		while (pos1 != -1) {
			String item = line.substring(pos0, pos1);
			split(list, item, regex2);
			pos0 = pos1 + regex1Len;
			pos1 = line.indexOf(regex1, pos0);
		}
		String item = line.substring(pos0);
		split(list, item, regex2);
		return list;
	}

	private static void split(List list, String line, String regex) {
		int pos0 = 0;
		int pos1 = line.indexOf(regex);

		int regexLen = regex.length();
		while (pos1 != -1) {
			list.add(line.substring(pos0, pos1));
			pos0 = pos1 + regexLen;
			pos1 = line.indexOf(regex, pos0);
		}
		list.add(line.substring(pos0));
	}
}
