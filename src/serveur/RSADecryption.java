
package serveur;

import java.io.IOException;
import java.nio.file.*;
import java.security.*;
import java.security.spec.*;
import javax.crypto.Cipher;


public class RSADecryption {
     PrivateKey clePrivee;
     
    public final static int TAILLE_BRUIT = 16; // Caractères aléatoires mis à la fin du message. 16 caractères => une chance sur 5192296858534827628530496329220096 qu'un message soit encrypté de la même façon deux fois.
    public final static int TAILLE_CLE = 2048;
     
    public static byte[] lireFichier(String chemin) throws IOException 
    {
        Path path = Paths.get(chemin);
        return Files.readAllBytes(path);
    }
    
    /**
     * Sert à initialiser l'interface de décryption à partir d'une clé dans un fichier
     * @param fichierClePrivee Le fichier de clé privée
     * @throws IOException 
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException 
     */
    public RSADecryption(String fichierClePrivee) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException
    {
        this(lireFichier(fichierClePrivee));
    }
   
    /**
     * Sert à initialiser l'interface de décryption à partir d'une clé
     * @param clePrivee
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException 
     */
    public RSADecryption(byte[] clePrivee) throws NoSuchAlgorithmException, InvalidKeySpecException
    {
        KeyFactory kf = KeyFactory.getInstance("RSA");

        PKCS8EncodedKeySpec spec2 = new  PKCS8EncodedKeySpec(clePrivee);
        this.clePrivee = kf.generatePrivate(spec2); 

    }
    
    /**
     * Sert a décoder un message crypté
     * @param messageCrypte
     * @return Le message original
     * @throws Exception 
     */
    public String decrypter(String messageCrypte) throws Exception
    {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, clePrivee);
        String messageAvecBruit = new String(cipher.doFinal(Base64Coder.decode(messageCrypte)));
        
        // Enlever le bruit
        return messageAvecBruit.substring(0, messageAvecBruit.length() - TAILLE_BRUIT);
    }
        

}
