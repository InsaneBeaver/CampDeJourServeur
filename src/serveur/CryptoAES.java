
package serveur;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.*;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

public class CryptoAES
{
    private final static int TAILLEBRUIT = 16;
    private SecretKeySpec skeySpec;
    
    public CryptoAES(String cleEncodee)
    {
        skeySpec = new SecretKeySpec(Base64Coder.decode(cleEncodee), 0, 16, "AES");
    }
    
    public String encryption(String message) throws NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, InvalidKeyException
    {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");

        SecureRandom randomSecureRandom = SecureRandom.getInstance("SHA1PRNG");
        byte[] iv = new byte[cipher.getBlockSize()];
        randomSecureRandom.nextBytes(iv);
        IvParameterSpec ivParams = new IvParameterSpec(iv);
        
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, ivParams);

        for(int i = 0; i < TAILLEBRUIT; i++)
            message += (char)(new Random()).nextInt(128);

        return Base64Coder.encode(iv) + Base64Coder.encode(cipher.doFinal(message.getBytes()));
    }
    
    public String decryption(String message) throws InvalidAlgorithmParameterException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, BadPaddingException, IllegalBlockSizeException
    {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        int tailleVecteur = 24;
        String ivChaine = message.substring(0, tailleVecteur);
        String messageChaine = message.substring(tailleVecteur, message.length());
        
        IvParameterSpec ivParams = new IvParameterSpec(Base64Coder.decode(ivChaine));
        
        cipher.init(Cipher.DECRYPT_MODE, skeySpec, ivParams);
        String messageDecode = new String(cipher.doFinal(Base64Coder.decode(messageChaine)));
        return messageDecode.substring(0, messageDecode.length() - TAILLEBRUIT);
    }
}
