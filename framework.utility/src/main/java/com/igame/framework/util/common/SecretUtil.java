package com.igame.framework.util.common;

import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

@SuppressWarnings("restriction")
public class SecretUtil {

	private final static String[] hexDigits = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };

	public final static String MD5 = "HmacMD5";
	public final static String SHA1 = "HmacSHA1";
	public final static String SHA256 = "HmacSHA256";
	public final static String SHA512 = "HmacSHA512";

	/**
	 * base64 加密
	 * 
	 * @param str
	 * @return
	 */
	public static String base64Encryption(byte[] b) {
		String base64 = (new BASE64Encoder()).encodeBuffer(b);
		base64 = base64.replaceAll("\r", "");
		base64 = base64.replaceAll("\n", "");
		return base64;
	}

	public static String base64Decryption(String str) {
		if (str == null)
			return null;
		BASE64Decoder decoder = new BASE64Decoder();
		try {
			byte[] b = decoder.decodeBuffer(str);
			return new String(b);
		} catch (Exception e) {
			return null;
		}
	}

	public static byte[] encryption(String data, String key, String code) throws NoSuchAlgorithmException, InvalidKeyException {

		final SecretKeySpec secret_key = new javax.crypto.spec.SecretKeySpec(key.getBytes(), code);
		final Mac mac = Mac.getInstance(code);
		mac.init(secret_key);
		final byte[] mac_data = mac.doFinal(data.getBytes());
		return mac_data;
	}

	public static String sha256Encryption(String data, String key) throws NoSuchAlgorithmException, InvalidKeyException {

		return byteArrayToHexString(encryption(data, key, SHA256));

	}

	public static String md5Encryption(String data, String key) throws InvalidKeyException, NoSuchAlgorithmException {
		return byteArrayToHexString(encryption(data, key, MD5));
	}

	public static String md5Encryption(String data) throws NoSuchAlgorithmException {

		MessageDigest md5 = MessageDigest.getInstance("MD5");
		byte[] b = md5.digest(data.getBytes());
		return byteArrayToHexString(b);
	}

	public static String sha512Encryption(String data, String key) throws InvalidKeyException, NoSuchAlgorithmException {
		return byteArrayToHexString(encryption(data, key, SHA512));
	}

	public static String sha1Encryption(String data, String key) throws InvalidKeyException, NoSuchAlgorithmException {
		return byteArrayToHexString(encryption(data, key, SHA1));
	}

	public static String byteArrayToHexString(byte[] b) {
		StringBuffer resultSb = new StringBuffer();
		for (int i = 0; i < b.length; i++) {
			resultSb.append(byteToHexString(b[i]));
		}
		return resultSb.toString();
	}

	private static String byteToHexString(byte b) {
		int n = b;
		if (n < 0)
			n = 256 + n;
		int d1 = n / 16;
		int d2 = n % 16;
		return hexDigits[d1] + hexDigits[d2];
	}

	public static void main(String[] args) throws NoSuchAlgorithmException {
		String ss = SecretUtil.md5Encryption(100 + "combat");
		System.out.println(ss);
	}

}
