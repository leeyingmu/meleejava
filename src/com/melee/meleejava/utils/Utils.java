package com.melee.meleejava.utils;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.yaml.snakeyaml.Yaml;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;



public class Utils {
	
	private static final int ONE_SECOND_MS = 1000;
	private static final int HALF_SECOND_MS = ONE_SECOND_MS / 2;
	private static final int THREE_SECONDS_MS = ONE_SECOND_MS * 3;
	private static final int FIVE_SECONDS_MS = ONE_SECOND_MS * 5;
	private static final int TEN_SECONDS_MS = ONE_SECOND_MS * 10;
	
	/**
	 * 判断一个对象是不是为空(null, [], {}, "" .eg)
	 * @param obj
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static boolean isEmpty(Object obj) {
		if (obj == null) {
			return true;
		}
		if (obj instanceof Number) {
			return ((Number)obj).equals(0);
		} else if (obj instanceof Collection) {
			return ((Collection<Object>)obj).size() == 0;
		} else if (obj instanceof String) {
			return ((String)obj).length() == 0;
		} else if (obj instanceof Object[]) {
			return ((Object[])obj).length == 0;
		} else {
			return obj == null;
		}
	}
	
	public static boolean isNotEmpty(Object obj) {
		return !isEmpty(obj);
	}
	
	/**
	 * 判断是不是所有参数都不为空(null, [], {}, "" .eg)
	 * @param objs
	 * @return
	 */
	public static boolean all(Object... objs) {
		if (objs == null || objs.length == 0) {
			return false;
		}
		for (Object o: objs) {
			if (isEmpty(o)) {
				return false;
			}
		}
		return true;
	}
	
	public static String urlEncode(String text) throws UnsupportedEncodingException {
		if (text != null && text.trim().length() > 0)
			return URLEncoder.encode(text, "utf-8");
		return "";
	}

	public static String urlDecode(String text) throws UnsupportedEncodingException {
		if (text != null && text.trim().length() > 0)
			return URLDecoder.decode(text, "utf-8");
		return "";
	}
	
	
	public static String encrypt(String sSrc, String sKey) throws Exception {
        if (sKey == null || sKey.length() != 16) {
            return null;
        }
        byte[] raw = sKey.getBytes();
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        Cipher cipher = Cipher.getInstance("AES");//"算法"
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
        byte[] encrypted = cipher.doFinal(sSrc.getBytes("utf-8"));

        String encrypt = new BASE64Encoder().encode(encrypted);
//        encrypt = URLEncoder.encode(encrypt, "utf-8");
        return encrypt;//此处使用BASE64做转码功能，同时能起到2次加密的作用。
    }

	public static String decrypt(String sSrc, String sKey) throws Exception {
		if (sKey == null || sKey.length() != 16) {
	            return null;
	    }

		byte[] raw = sKey.getBytes();
		SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.DECRYPT_MODE, skeySpec);

		byte[] encrypted1 = new BASE64Decoder().decodeBuffer(sSrc);// 先用base64解密
		byte[] original = cipher.doFinal(encrypted1);
		String originalString = new String(original, "utf-8");
		return originalString;
	}
	
	
	public static String get(String url) throws Exception {
		return get(url, TEN_SECONDS_MS);
	}
	
	/**
	 * timeout millionseconds
	 */
	public static String get(String url, int timeout) throws Exception
	{
		BufferedReader reader = null;
		StringBuilder sb = new StringBuilder();
		HttpURLConnection conn = null;
		try {
			conn = (HttpURLConnection) new URL(url).openConnection();
			conn.setDoInput(true);
			conn.setDoOutput(false);
			conn.setUseCaches(false);
			conn.setReadTimeout(timeout);
			conn.setConnectTimeout(timeout);
			conn.addRequestProperty("User-Agent", "meleejava");
			conn.connect();

			InputStream is = conn.getInputStream();

			reader = new BufferedReader(new InputStreamReader(is, "utf-8"));
			String line;
			while ((line = reader.readLine()) != null)
			{
				sb.append(line).append("\r\n");
			}
		} finally {
			close(reader);
			close(conn);
		}
		return sb.toString();
	}
	
	public static String postForm(String url, String params) throws Exception {
		return postForm(url, params, THREE_SECONDS_MS);
	}
	
	public static String postForm(String url, String params, int timeout) throws Exception {
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Content-Type", "application/x-www-form-urlencoded");
		return post(url, params, headers, timeout);
	}
	
	public static String postBody(String url, String params) throws Exception {
		return postBody(url, params, THREE_SECONDS_MS);
	}
	
	public static String postBody(String url, String params, int timeout) throws Exception {
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("Content-Type", "application/charset=UTF-8");
		return post(url, params, headers, timeout);
	}
	
	public static String post(String url, String params) throws Exception {
		return post(url, params, null);
	}
	
	public static String post(String url, String params, Map<String, String> headers) throws Exception {
		return post(url, params, headers, THREE_SECONDS_MS);
	}
	
	public static String post(String url, String params, Map<String, String> headers, int timeout) throws Exception {
		
		BufferedReader reader = null;
		HttpURLConnection conn = null;
		StringBuilder sb = new StringBuilder();
		
	  	try {
	  		byte[] bytes = params.getBytes("utf-8");
		    
		    conn = (HttpURLConnection) new URL(url).openConnection();
		    conn.setDoInput(true);
		    conn.setDoOutput(true);
		    conn.setUseCaches(false);
		    conn.setConnectTimeout(timeout);
		    conn.setReadTimeout(timeout);
		    conn.addRequestProperty("User-Agent", "meleejava");
		    conn.setRequestMethod("POST");
		    conn.setRequestProperty("Content-Type", "application/charset=UTF-8");
			conn.setFixedLengthStreamingMode(bytes.length);
			if (headers != null) {
				for (String k: headers.keySet()) {
					conn.setRequestProperty(k, headers.get(k));
				}
			}
			OutputStream out = conn.getOutputStream();
			out.write(bytes);
			out.close();
			conn.connect();
			
			reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));
			String line;
			while ((line = reader.readLine()) != null) {
				sb.append(line).append("\r\n");
			}
					
	  	} finally {
			close(reader);
			close(conn);
	  	}
		
	    return sb.toString();
	}

	private static void close(Closeable is) {
		if (is != null)
		{
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	private static void close(HttpURLConnection conn) {
		if (conn != null) {
			conn.disconnect();
			conn = null;
		}
	}
	
	public static String hash(MessageDigest digest, String src) throws UnsupportedEncodingException {
		return hash(digest, src, 1, "UTF-8");
	}
	
	/**
	 * Get hash result using the digest algorithm.
	 * @param digest hash algorithm.
	 * @param src
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String hash(MessageDigest digest, String src, int times, String coding) 
			throws UnsupportedEncodingException {
		if (coding == null) {
			coding = "UTF-8";
		}
		if (times <= 0) {
			times = 1;
		}
		byte[] data = src.getBytes(Charset.forName(coding));
		for (int i=0; i<times; i++) {
			data = digest.digest(data);
		}
		return toHexString(data);
	}
	
	public static String hmac(Mac mac, String key, String src) throws Exception {
		SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(), mac.getAlgorithm());
		mac.init(signingKey);
		byte[] data = mac.doFinal(src.getBytes("utf-8"));
		String result = toHexString(data);
		return result;
	}
	
	public static String toHexString(byte[] data) {
		StringBuilder sb = new StringBuilder(data.length * 2);
		for (int i = 0; i < data.length; i++) {
			sb.append(String.format("%02x", data[i]));
		}
		return sb.toString();
	}
	
	/**
	 * get first count chars of string src.
	 * @param src if src is null or src's length is zero, return src
	 * @param count if count==0, return src
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	public static String getStringLengthOfBytes(String src, int count) throws UnsupportedEncodingException {
		if (src==null || src.trim().length()==0)
			return src;
		
		byte[] b = src.getBytes("utf-8");
		if (b.length <= count) 
			return src;
					
		byte[] t = new byte[count+1];
		System.arraycopy(b, 0, t, 0, count+1);
		
		String result = new String(t, "utf-8");
		result = result.substring(0, result.length()-1);
			
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> Map<String,T> getYamlObj(String str) {
		Yaml yaml = new Yaml();
		return (Map<String,T>) yaml.load(str);
	}
	
	
	/**
	 * Whether all keys are contained in the map
	 * @param map
	 * @param objects
	 * @return true if all keys are contained in the map
	 */
	public static <T> boolean containsAll(Map<T, ? extends Object> map, T... keys) {
		if (keys == null || keys.length == 0) {
			return false;
		}
		for (T key: keys) {
			if ( ! map.containsKey(key)) {
				return false;
			}
		}
		return true;
	}
	

	public static String join(Collection<?> collection, String delimiter) {
		if (collection == null || collection.size() == 0) {
			return "";
		}
		StringBuilder builder = new StringBuilder();
		int size = collection.size();
		int count = 0;
		for (Object obj: collection) {
			if (count < size -1) {
				builder.append(obj).append(delimiter);
			} else {
				builder.append(obj);
			}
			count ++;
		}
		return builder.toString();
	}
	

	public static final String[] IGNORE_PREFIX_IN_CONFIG_FILE = new String[]{"#", "//"};
	/**
	 * Get all lines except for ones withs ignorePrefix from file.
	 * @param filepath
	 * @return
	 * @throws Exception if file is not found
	 */
	public static String getFileContent(String filepath) throws Exception {
		
		File file = new File(filepath);
		if (!file.exists()) {
			throw new IllegalArgumentException("File " + filepath + " not exists");
		}
		
		BufferedReader breader = new BufferedReader(
				new InputStreamReader(
						new FileInputStream(file), "utf-8"
					)
				);
		
		StringBuilder sb = new StringBuilder();
		String line;
		while((line = breader.readLine()) != null) {
			boolean ignore = false;
			for (String prefix : IGNORE_PREFIX_IN_CONFIG_FILE) {
				if (line.trim().startsWith(prefix)) {
					ignore = true;
					break;
				}
			}
			if (! ignore) {
				sb.append(line).append("\r\n");
			}
		}
		
		return sb.toString();
	}

}
