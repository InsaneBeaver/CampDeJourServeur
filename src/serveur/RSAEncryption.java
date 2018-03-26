package serveur;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.*;
import java.util.*;
import javax.crypto.Cipher;
public class RSAEncryption {
    
    PublicKey clePublique;
    
    public RSAEncryption(String fichierClePublique) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException
    {
        this(RSAUtil.lireFichier(fichierClePublique));
    }
    
    public RSAEncryption(byte[] clePublique) throws NoSuchAlgorithmException, InvalidKeySpecException
    {
        KeyFactory kf = KeyFactory.getInstance("RSA");
        
        X509EncodedKeySpec spec1 = new X509EncodedKeySpec(clePublique);
        this.clePublique = kf.generatePublic(spec1);
   
    }

    public String encrypter(String message) throws Exception
    {
        // Mettre du bruit
        Random r = new Random();
        for(int i = 0; i < RSAUtil.TAILLE_BRUIT; i++)
            message += (char)r.nextInt(128);
        
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, clePublique); 
        byte [] encrypted = cipher.doFinal(message.getBytes());
        return Base64Coder.encode(encrypted);
                
    }
    
    public boolean authentifierSignature(String signatureRecue, String messageAttendu) throws Exception
    {
        Signature publicSignature = Signature.getInstance("SHA256withRSA");
        publicSignature.initVerify(clePublique);
        System.out.println("La signature " + signatureRecue.length());
        publicSignature.update(messageAttendu.getBytes());
        
        return publicSignature.verify(Base64Coder.decode(signatureRecue));
    }
}