/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package serveur;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import javax.crypto.Cipher;

/**
 *
 * @author 11110305
 */
public class RSADecryption {
     PrivateKey clePrivee;
     
    public RSADecryption(String fichierClePrivee) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException
    {
        this(RSAUtil.lireFichier(fichierClePrivee));
    }
    
    
    public RSADecryption(byte[] clePrivee) throws NoSuchAlgorithmException, InvalidKeySpecException
    {
        KeyFactory kf = KeyFactory.getInstance("RSA");

        PKCS8EncodedKeySpec spec2 = new  PKCS8EncodedKeySpec(clePrivee);
        this.clePrivee = kf.generatePrivate(spec2);  
    }
    
        public String decrypter(String message) throws Exception
    {
        Cipher cipher = Cipher.getInstance("RSA");  
        cipher.init(Cipher.DECRYPT_MODE, clePrivee);
        String messageAvecBruit = new String(cipher.doFinal(Base64.getDecoder().decode(message)), StandardCharsets.UTF_8);
        
        // Enlever le bruit
        return messageAvecBruit.substring(0, messageAvecBruit.length() - RSAUtil.TAILLE_BRUIT);
    }
        
    public String creerSignature(String clePubliqueAutre) throws Exception
    {   
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(clePrivee);
        signature.update(clePubliqueAutre.getBytes());
        return Base64.getEncoder().encodeToString(signature.sign());
    }
}
