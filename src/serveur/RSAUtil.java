
package serveur;

import java.io.*;
import java.nio.file.*;
import java.security.*;


public class RSAUtil {
    
    /**
     * Sert à initialiser une paire de clés.
     * @param fichierClePublique Le fichier dans lequel la fonction va écrire la clé publique
     * @param fichierClePrivee Le fichier dans lequel la fonction va écrire la clé privée
     * @throws Exception 
     */
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
    
    /**
     * Pour lire un fichier binaire
     * @param chemin Le chemin du fichier
     * @return Le contenu du fichier
     * @throws IOException 
     */
    public static byte[] lireFichier(String chemin) throws IOException
    {
        Path path = Paths.get(chemin);
        return Files.readAllBytes(path);
    }

    public final static int TAILLE_BRUIT = 16; // Caractères aléatoires mis à la fin du message. 16 caractères => une chance sur 5192296858534827628530496329220096 qu'un message soit encrypté de la même façon deux fois.
    public final static int TAILLE_CLE = 2048;
}
           