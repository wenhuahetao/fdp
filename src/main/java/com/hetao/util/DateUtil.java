package com.hetao.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * date
 * @author heato
 */
public class DateUtil {

	public static final String LONG_MODEL = "yyyy-MM-dd HH:mm:ss";

	public static final String SHORT_MODEL = "yyyy-MM-dd";

	public static final String SHORT_HANZI_MODEL = "yyyy��MM��dd��";

	public static final String MONTH_MODEL = "yyyy-MM";

	public static final String ISO_TIME_ZONE_MASK = "yyyy-MM-dd'T'HH:mm:ss";

	public static final Long HOUR_MILLIS = 1000L * 60 * 60;

	public static final Long DAY_MILLIS = HOUR_MILLIS * 24;

	private DateUtil() {
	}

	public static String transferLongToDate(String dateFormat, Long millSec) {
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		Date date = new Date(millSec);
		return sdf.format(date);
	}

	public static String formatDateTime(Object obj, String mask) {
		if (obj != null) {
			Date date = (Date) obj;
			SimpleDateFormat format = new SimpleDateFormat(mask);
			return format.format(date);
		}
		return "";
	}

	public static Date formatDate(Object obj, String mask) {
		try {
			if (obj != null) {
				Date date = (Date) obj;
				SimpleDateFormat format = new SimpleDateFormat(mask);
				return format.parse(format.format(date));
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String dateFormat(Date d) {
		SimpleDateFormat sdf = new SimpleDateFormat(LONG_MODEL);
		return sdf.format(d);
	}

	public static String dateFormat(Date d, String model) {
		SimpleDateFormat sdf = new SimpleDateFormat(model);
		return sdf.format(d);
	}

	public static Date StringToDate(String s) throws ParseException {

		DateFormat sdf = new SimpleDateFormat(LONG_MODEL);
		try {
			return sdf.parse(s);
		} catch (ParseException e) {
			DateFormat sdf2 = new SimpleDateFormat(SHORT_MODEL);
			try {
				return sdf2.parse(s);
			} catch (ParseException e2) {
			}
		}
		return null;
	}
	
	public static String StringToFormat(String s,String model) throws ParseException {

		DateFormat sdf = new SimpleDateFormat(model);
		try {
			return sdf.format(sdf.parse(s));
		} catch (ParseException e) {
		}
		return null;
	}

	public static Date calculateDate(int h) {

		return calculateDate(new Date(), h, 0);
	}

	public static Date calculateDate(int h, int m) {

		return calculateDate(new Date(), h, m);
	}

	public static Date calculateDate(Date fromdate, int h) {

		return calculateDate(fromdate, h, 0);
	}

	public static Date calculateDate(Date fromdate, int h, int m) {

		Date date = null;
		Calendar cal = Calendar.getInstance();
		cal.setTime(fromdate);
		cal.add(Calendar.HOUR_OF_DAY, h);
		cal.add(Calendar.MINUTE, m);
		date = cal.getTime();
		return date;
	}

	public static long dateDiff(Date d1, Date d2) {

		return dateCompare(d1, d2) / 1000 / 60;
	}
	public static int dateDiffForDay(Date d1, Date d2) {

		return (int) dateDiff(d1, d2) / 60 / 24;
	}

	public static long dateCompare(Date d1, Date d2) {
		Calendar cal = Calendar.getInstance();
		Calendar ca2 = Calendar.getInstance();
		cal.setTime(d1);
		ca2.setTime(d2);
		long l1 = cal.getTimeInMillis();
		long l2 = ca2.getTimeInMillis();
		return l1 - l2;
	}

	public static Integer toHour(Long millis) {
		return Long.valueOf(millis / HOUR_MILLIS).intValue();
	}

	public static Integer toDay(Long millis) {
		return Long.valueOf(millis / DAY_MILLIS).intValue();
	}

	public static Date fromDate(String text) {
		if (!Pattern.compile("\\d[1,2]:d[1,2]").matcher(text).find()) {
			text = text + " 00:00:00";
		}
		return fromDate(text, LONG_MODEL);
	}

	public static Date fromDate(String text, String mask) {
		SimpleDateFormat format = new SimpleDateFormat(mask);
		try {
			return format.parse(text);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String fromDateCST(String text, String mask) {
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
			Date date = (Date) sdf.parse(text);
			String formatStr2 = new SimpleDateFormat(mask).format(date);
			System.out.println(formatStr2);
			return new SimpleDateFormat(mask).format(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Date addWorkDay(Date date, int num) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		int mod = num % 5;
		int other = num / 5 * 7;
		for (int i = 0; i < mod;) {
			cal.add(Calendar.DATE, 1);
			switch (cal.get(Calendar.DAY_OF_WEEK)) {
			case Calendar.SUNDAY:
			case Calendar.SATURDAY:
				break;
			default:
				i++;
				break;
			}
		}
		if (other > 0)
			cal.add(Calendar.DATE, other);
		return cal.getTime();
	}
}
