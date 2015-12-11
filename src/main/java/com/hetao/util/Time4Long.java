package com.hetao.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Time4Long {
	static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:ms");
	public static void main(String[] args) {
		try {
			
			long startTime = sdf.parse("2015-11-11 07:00:00").getTime();
			long endTime = sdf.parse("2015-11-11 09:59:59").getTime();
			System.out.println(startTime);
			System.out.println(endTime);
			
			
			Map<String, Long> queryAllUsedSource = new HashMap<>();
			queryAllUsedSource.put("2015-11-11",0L);
			queryAllUsedSource.put("2015-11-13",0L);
			queryAllUsedSource.put("2015-11-12",0L);
			Object[] key = queryAllUsedSource.keySet().toArray();
			Arrays.sort(key);

			for (int i = 0; i < key.length; i++) {
				System.out.println(key[i]);
			}

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
