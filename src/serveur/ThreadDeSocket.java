// Emprunté ici https://stackoverflow.com/questions/10131377/socket-programming-multiple-client-to-one-server
package serveur;

import java.io.*;
import java.net.*;

public class ThreadDeSocket extends Thread {

    private final Socket socket;
    private final InterfaceServeur interServ;
    private CryptoAES cryptoAES = null;
    private final RSADecryption decryptionRSA;
    public static int nbConnexions = 0;
    public static final int MAXCONNEXIONS = 10;
    private String hashCommun;
    
    private void actualiserHashCommun(String message)
    {
        hashCommun = BaseDeDonnees.getHash(hashCommun + message);
    }

    /**
     * Constructeur.
     *
     * @param clientSocket L'objet socket
     * @param interServ L'interface permettant d'exécuter des commandes
     * @param decryption La fonctionnalité pour décrypter des messages encryptés
     * avec la clé publique du serveur
     */
    public ThreadDeSocket(Socket clientSocket, InterfaceServeur interServ, RSADecryption decryption) {
        this.socket = clientSocket;
        this.interServ = interServ;
        this.decryptionRSA = decryption;
        this.hashCommun = " ";
    }
    
    

    /**
     * Lorsque le thread est exécuté
     */
    @Override
    public void run() {
        nbConnexions++;
        System.out.println("Nouvelle connexion avec " + socket.getRemoteSocketAddress().toString() + ".");
        InputStream inputStream = null;
        BufferedReader bufferedReader = null;
        DataOutputStream outputStream = null;

        try {
            inputStream = socket.getInputStream();
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            outputStream = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        String ligneRecue;

        try {
            while (true) {
                ligneRecue = bufferedReader.readLine();

                if ((ligneRecue == null) || ligneRecue.equalsIgnoreCase("QUIT")) {
                    socket.close();
                    break;
                } else if (ligneRecue != null && !ligneRecue.isEmpty()) {
                    if (cryptoAES == null) {
                        // On récupère la clé AES envoyée par le client, puis on s'en sert pour encrypter une nouvelle clé AES,
                        // qu'on envoie au client et qui sera employée pour communiquer à partir de maintenant.
                        String ligneDecryptee = decryptionRSA.decrypter(ligneRecue);
                        CryptoAES cryptoAESTemp = new CryptoAES(ligneDecryptee);
                        cryptoAES = new CryptoAES();
                        outputStream.writeBytes(cryptoAESTemp.encryption(cryptoAES.getCle()) + "\n");
                    } else {
                        String messageRecu = cryptoAES.decryption(ligneRecue);
                        actualiserHashCommun(messageRecu);
                        try {
                            String message = interServ.executerCommande(messageRecu);
                            if (!message.isEmpty()) {
                                String lignes[] = message.split("\n");
                               
                                for(String morceau : lignes)
                                    actualiserHashCommun(morceau);
                                
                                
                                outputStream.writeBytes(cryptoAES.encryption(hashCommun) + "\n");
                                
                                for (String morceau : lignes) {                                    
                                    outputStream.writeBytes(cryptoAES.encryption(morceau) + "\n");
                                }

                            } 
                            
                            outputStream.writeBytes("\n");
                            
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        outputStream.flush();

                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("Connexion terminée.");
        nbConnexions--;
    }
}
