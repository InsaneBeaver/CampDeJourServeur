/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
import java.security.spec.*;
import java.util.*;
import javax.crypto.Cipher;

public class RSAUtil {
    
    public static void initialiserCles(String fichierClePublique, String fichierClePrivee) throws Exception
    {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(TAILLE_CLE);
        KeyPair kpair = kpg.genKeyPair();

        byte[] publicKeyBytes = kpair.getPrivate().getEncoded();

        FileOutputStream fos = new FileOutputStream(fichierClePublique);
        fos.write(kpair.getPublic().getEncoded());
        fos.close();
        
        fos = new FileOutputStream(fichierClePrivee);
        fos.write(kpair.getPrivate().getEncoded());
        fos.close();
        
    }
    
    public static byte[] lireFichier(String chemin) throws IOException
    {
        if(chemin.length() == 0) return new byte[0];
        Path path = Paths.get(chemin);
        return Files.readAllBytes(path);
    }

    public final static int TAILLE_BRUIT = 16; // Caractères aléatoires mis à la fin du message. 16 caractères => une chance sur 5192296858534827628530496329220096 qu'un message soit encrypté de la même façon deux fois.
    public final static int TAILLE_CLE = 2048;
    public final static int TAILLE_SIGNATURE = 16;
}
           
