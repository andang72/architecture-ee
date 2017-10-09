package tests;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;

public class Crypto2Test {
 
    public static void main(String args[]) throws Exception {
    	
        String passPhrase = "1234";
        String salt = "18b00b2fc5f0e0ee40447bba4dabc952"; 
        String iv = "4378110db6392f93e95d5159dabdee9b";
        int iterationCount = 10000;
        int keySize = 128;
        
    	// 암호화된 텍스트 ..
        //String ciphertext = "rV2MW3IoCfcCHMa6cZuCcDJpOnOhHDS4R8cOXEL+Z4kpOOThrPT6lKO0t4gcElOGacJ7uug7iJrMLh6H/YHO01LSXYmd+1Xzaf3xzSWaglE="; 
        //String ciphertext = "tucAyVT5f9GMf4t/D4ywHZpl1Jer6ps0A2ra4uiIKZmkQmKh6ev+uZ0EatMzgadh7cuZ+Y9TvXL9gW2KqB4YqU6+bNf0gHlTCzBQV0ASp7E=";
      //  System.out.println(decrypt(salt, iv, passPhrase,  ciphertext.getBytes("UTF-8"), iterationCount, keySize));
        
        
        // 평문 텍스트 ..
        String plaintext = "Led Zeppelin- Stairway to Heaven 레드제플린 - 천국으로 가능 계단";
        

        
        String encrypted = encrypt(salt, iv, passPhrase, plaintext, iterationCount, keySize);
        System.out.println(plaintext);
        System.out.println(encrypted);
        
        
        String decrypted = decrypt(salt, iv, passPhrase, encrypted, iterationCount, keySize);
        //System.out.println(encrypted);
        System.out.println(decrypted);
        
        
    }
    
    public static String encrypt(String salt, String iv, String passphrase, String ciphertext, int iterationCount, int keySize) throws Exception {            
    	SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        KeySpec spec = new PBEKeySpec(passphrase.toCharArray(), Hex.decodeHex(salt.toCharArray()), iterationCount, keySize);
        SecretKey key = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");        
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(Hex.decodeHex(iv.toCharArray())));        
        byte[] decrypted = cipher.doFinal(ciphertext.getBytes());        
        return new String( Base64.encodeBase64(decrypted), "UTF-8");
        
    }
    
    public static String decrypt(String salt, String iv, String passphrase, byte[] ciphertext, int iterationCount, int keySize) throws Exception {            
    	SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        KeySpec spec = new PBEKeySpec(passphrase.toCharArray(), Hex.decodeHex(salt.toCharArray()), iterationCount, keySize);
        SecretKey key = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");        
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(Hex.decodeHex(iv.toCharArray())));        
        byte[] decrypted = cipher.doFinal(ciphertext);        
        return new String(decrypted, "UTF-8");
        
    }
    
    public static String decrypt(String salt, String iv, String passphrase, String ciphertext, int iterationCount, int keySize) throws Exception {            
    	SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        KeySpec spec = new PBEKeySpec(passphrase.toCharArray(), Hex.decodeHex(salt.toCharArray()), iterationCount, keySize);
        SecretKey key = new SecretKeySpec(factory.generateSecret(spec).getEncoded(), "AES");        
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(Hex.decodeHex(iv.toCharArray())));        
        byte[] decrypted = cipher.doFinal(Base64.decodeBase64(ciphertext.getBytes()));        
        return new String(decrypted, "UTF-8");
        
    }
 
 
}
