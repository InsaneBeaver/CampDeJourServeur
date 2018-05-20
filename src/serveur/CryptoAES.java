
package serveur;

import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.*;
import java.util.*;

public class CryptoAES
{
    private final static int TAILLEBRUIT = 16; // La taille du bruit mis à la fin de chaque message
    private SecretKey secretKey;
    private SecretKeySpec skeySpec;
    
    /**
     * Constructeur pour si la clé vient d'ailleurs
     * @param cleEncodee Clé encodée
     */
    public CryptoAES(String cleEncodee)
    {
        skeySpec = new SecretKeySpec(Base64Coder.decode(cleEncodee), 0, 16, "AES");
    }
    
    /**
     * Constructeur pour créer un nouveau système avec une nouvelle clé
     */
    public CryptoAES()
    {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("AES");
            keyGen.init(128);
            secretKey = keyGen.generateKey();
            skeySpec = new SecretKeySpec(secretKey.getEncoded(), "AES");
        }
        catch(Exception e) {}

    }
    
    /**
     * Pour obtenir la clé encodée
     * @return la clé
     */
    public String getCle()
    {
        return Base64Coder.encode(secretKey.getEncoded());
    }
    
    
    /**
     * Sert à encrypter un message
     * @param message Le message
     * @return Le message encrypté
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws IllegalBlockSizeException
     * @throws BadPaddingException
     * @throws InvalidAlgorithmParameterException
     * @throws InvalidKeyException 
     */
    public String encryption(String message) throws NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, InvalidKeyException
    {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");

        SecureRandom randomSecureRandom = SecureRandom.getInstance("SHA1PRNG");
        byte[] iv = new byte[cipher.getBlockSize()];
        randomSecureRandom.nextBytes(iv);
        IvParameterSpec ivParams = new IvParameterSpec(iv);
        
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, ivParams);

        // On met ensuite du bruit
        for(int i = 0; i < TAILLEBRUIT; i++)
            message += (char)(new Random()).nextInt(128);

        return Base64Coder.encode(iv) + Base64Coder.encode(cipher.doFinal(message.getBytes()));
    }
    
    /**
     * Sert à décrypter un message
     * @param messageCrypte Le message encodé
     * @return Le message
     * @throws InvalidAlgorithmParameterException
     * @throws InvalidKeyException
     * @throws NoSuchAlgorithmException
     * @throws NoSuchPaddingException
     * @throws BadPaddingException
     * @throws IllegalBlockSizeException 
     */
    public String decryption(String messageCrypte) throws InvalidAlgorithmParameterException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, BadPaddingException, IllegalBlockSizeException
    {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        int tailleVecteur = 24;
        String ivChaine = messageCrypte.substring(0, tailleVecteur);
        String messageChaine = messageCrypte.substring(tailleVecteur, messageCrypte.length());
        
        IvParameterSpec ivParams = new IvParameterSpec(Base64Coder.decode(ivChaine));
        
        cipher.init(Cipher.DECRYPT_MODE, skeySpec, ivParams);
        String messageDecode = new String(cipher.doFinal(Base64Coder.decode(messageChaine)));
        return messageDecode.substring(0, messageDecode.length() - TAILLEBRUIT);
    }
}
