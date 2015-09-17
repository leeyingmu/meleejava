package com.melee.meleejava.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class JsonUtils {

	public static <T> void putToJson(Map<String, T> map, String key, T value) {
		if (value == null) {
			return;
		}
		map.put(key, value);
	} 
	
	/**
	 * Parse string to Map
	 * @param str
	 * @return
	 * @throws ParseException
	 */
	@SuppressWarnings("unchecked")
	public static <T> Map<String,T> getJsonObj(String str) throws ParseException {
		JSONParser parser = new JSONParser();
		return (Map<String,T>) parser.parse(str);
	} 
	
	/**
	 * Get map value from map json
	 * @param obj
	 * @param key
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> Map<String,T> getJsonObj(Map<?,?> obj, String key) {
		return (Map<String, T>) obj.get(key);
	}
	
	/**
	 * Get array value from map json
	 * @param obj
	 * @param key
	 * @return empty list if obj doesn't contains key
	 */
	public static <T> List<T> getJsonList(Map<?,?> obj, String key) {
		return getJsonList(obj, key, false);
	}
	
	/**
	 * Get array value from map josn and delete the key from the obj
	 * @param obj
	 * @param key
	 * @return
	 */
	public static <T> List<T> removeJsonList(Map<?,?> obj, String key) {
		return getJsonList(obj, key, true);
	}
	
	/**
	 * Get array value from map json
	 * @param obj
	 * @param key
	 * @param removed Whether to remove the key from the map
	 * @return empty list if obj doesn't contains key
	 */
	@SuppressWarnings("unchecked")
	private static <T> List<T> getJsonList(Map<?,?> obj, String key, boolean removed) {
		List<T> result = null;
		if (removed) {
			result = (List<T>) obj.remove(key);
		} else {
			result = (List<T>) obj.get(key);
		}
		if (result == null) {
			result = new ArrayList<T>();
		}
		return result;
	}
	
	
	/**
	 * Get value with specify type from map json.
	 * @param obj
	 * @param key
	 * @return 
	 */
	public static <T> T getJsonVal(Map<?,?> obj, String key) {
		return getJsonValWithDefault(obj, key, null);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T getJsonValWithDefault(Map<?,?> obj, String key, T defaultValue) {
		T value = (T) obj.get(key);
		if (value == null) {
			return defaultValue;
		}
		return value;
	}
	
	public static String toJsonString(Object obj) {
		return JSONValue.toJSONString(obj);
	}
	

	public static <T> T getJsonVal(Map<?,?> dataJson, String field, Class<T> clazz) {
		
		if (dataJson != null && dataJson.containsKey(field))
		{
			return clazz.cast(dataJson.get(field));
		}
		
		return null;
	}
}
