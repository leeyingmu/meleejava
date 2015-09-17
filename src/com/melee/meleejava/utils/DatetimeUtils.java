package com.melee.meleejava.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;

public class DatetimeUtils {
	
	/**
	 * convert java.sql.Date to java.util.Date
	 * @param date
	 * @return
	 */
	public static java.util.Date convertDate(java.sql.Date date) {
		return new java.util.Date(date.getTime());
	}
	
	/**
	 * convert java.sql.Date to java.util.Date
	 * @param date
	 * @return
	 */
	public static java.sql.Date convertDate(java.util.Date date) {
		return new java.sql.Date(date.getTime());
	}
	
	/**
	 * convert java.util.Date to java.sql.Timestamp
	 * @param date
	 * @return
	 */
	public static java.sql.Timestamp convertTimestamp(java.util.Date date) {
		return new java.sql.Timestamp(date.getTime());
	}
	
	/**
	 * convert java.sql.Timestamp to java.util.Date
	 * @param timestamp
	 * @return
	 */
	public static java.util.Date convertTimestamp(java.sql.Timestamp timestamp) {
		return new java.util.Date(timestamp.getTime());
	}
	
	/**
	 * convert java.sql.Timestamp to java.sql.Date
	 * @param datetime
	 * @return
	 */
	public static java.sql.Date convert(java.sql.Timestamp datetime) {
		return new java.sql.Date(datetime.getTime());
	}

	/**
	 * get current system java.sql.Timestamp
	 * @return
	 */
	public static java.sql.Timestamp getSystemSqlTimestamp() {
		return new java.sql.Timestamp(System.currentTimeMillis());
	}
	
	
	/**
	 * format java.util.Date to yyyy-MM-dd HH:mm:ss
	 * @param date
	 * @return
	 */
	public static String convertDate2String(java.util.Date date) {
		return convertDate2String(date, "yyyy-MM-dd HH:mm:ss");
	}
	/**
	 * format java.util.Date to custom pattern
	 * @param date
	 * @param pattern
	 * @return
	 */
	public static String convertDate2String(java.util.Date date, String pattern) {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		return sdf.format(date);
	}
	
	/**
	 * convert string datetime with pattern "yyyy-MM-dd HH:mm:ss" to java.util.Date
	 * @param datetime
	 * @return
	 * @throws ParseException
	 */
	public static java.util.Date convertString2Date(String datetime) throws ParseException {
		return convertString2Date(datetime, "yyyy-MM-dd HH:mm:ss");
	}
	/**
	 * convert string datetime to java.util.Date with custom pattern
	 * @param datetime
	 * @param format
	 * @return
	 * @throws ParseException
	 */
	public static java.util.Date convertString2Date(String datetime, String format) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		return sdf.parse(datetime);
	}
}
