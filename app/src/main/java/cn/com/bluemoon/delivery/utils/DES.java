package cn.com.bluemoon.delivery.utils;

import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import Decoder.BASE64Decoder;
import Decoder.BASE64Encoder;
import cn.com.bluemoon.delivery.BuildConfig;


public class DES {

    public static String encrypt(String data){
        return encrypt(data, BuildConfig.DES_KEY);
    }

    public static String encrypt(String data,String key) {
    	try {
    		byte[] bt = encrypt(data.getBytes(), key.getBytes());
            String strs = new BASE64Encoder().encode(bt);
            return strs;
		} catch (Exception e) {
			return null;
		}
        
    }

    public static String decrypt(String data){
        return decrypt(data,BuildConfig.DES_KEY);
    }

    public static String decrypt(String data,String key){
        if (data == null)
            return null;
        try {
        	BASE64Decoder decoder = new BASE64Decoder();
            byte[] buf = decoder.decodeBuffer(data);
            byte[] bt = decrypt(buf,key.getBytes());
            return new String(bt);
		} catch (Exception e) {
			return null;
		}
        
    }
 

    private static byte[] encrypt(byte[] data, byte[] key) throws Exception {

        SecureRandom sr = new SecureRandom();
 

        DESKeySpec dks = new DESKeySpec(key);
 

        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(Constants.DES);
        SecretKey securekey = keyFactory.generateSecret(dks);
 

        Cipher cipher = Cipher.getInstance(Constants.DES);
 

        cipher.init(Cipher.ENCRYPT_MODE, securekey, sr);
 
        return cipher.doFinal(data);
    }
     

    private static byte[] decrypt(byte[] data, byte[] key) throws Exception {

        SecureRandom sr = new SecureRandom();
 

        DESKeySpec dks = new DESKeySpec(key);
 

        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(Constants.DES);
        SecretKey securekey = keyFactory.generateSecret(dks);
 

        Cipher cipher = Cipher.getInstance(Constants.DES);
 

        cipher.init(Cipher.DECRYPT_MODE, securekey, sr);
 
        return cipher.doFinal(data);
    }
    
}