package com.fapple.Tools;

import java.util.*;
import java.util.regex.*;

public class Zhengze
{
    //正则匹配 index re0
	public static String ZZ(String str, String biaodashi, Boolean daxiaoxie, int index)
	{
		if (str == "" || str == null || biaodashi == "" || biaodashi == null || index < 0) {
			return null;
		}
		Pattern pa;
		if (daxiaoxie == true) {
			pa = Pattern.compile(biaodashi);
		} else {
			pa = Pattern.compile(biaodashi, Pattern.CASE_INSENSITIVE);
		}
		Matcher ma = pa.matcher(str);
		for (int i = 0; i < index; i++) {
			if (ma.find()) {
				ma.group();
			} else {
				break;
			}
		}
		if (ma.find()) {
			return ma.group();
		} else {
			return null;
		}
	}
	public ArrayList<String> ZZall(String str, String biaodashi, Boolean daxiaoxie, int index)
	{
		ArrayList<String> list = new ArrayList<String>();
		if (str == "" || str == null || biaodashi == "" || biaodashi == null || index < 0) {
			return list;
		}
		Pattern pa;
		if (daxiaoxie == true) {
			pa = Pattern.compile(biaodashi);
		} else {
			pa = Pattern.compile(biaodashi, Pattern.CASE_INSENSITIVE);
		}
		Matcher ma = pa.matcher(str);
		//ma.matches();
		while (ma.find()) {
			list.add(ma.group());
		}
		return list;
	}
}
