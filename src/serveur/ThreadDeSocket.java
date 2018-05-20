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
    
    /**
     * Constructeur.
     * @param clientSocket L'objet socket
     * @param interServ L'interface permettant d'exécuter des commandes
     * @param decryption La fonctionnalité pour décrypter des messages encryptés avec la clé publique du serveur
     */
    public ThreadDeSocket(Socket clientSocket, InterfaceServeur interServ, RSADecryption decryption) {
        this.socket = clientSocket;
        this.interServ = interServ;
        this.decryptionRSA = decryption;
    }
    /**
     * Lorsque le thread est exécuté
     */
    @Override
    public void run() {
        nbConnexions++;
        System.out.println("Nouvelle connexion avec " + socket.getRemoteSocketAddress().toString()+".");
        InputStream inputStream = null;
        BufferedReader bufferedReader = null;
        DataOutputStream outputStream = null;

        try 
        {
            inputStream = socket.getInputStream();
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            outputStream = new DataOutputStream(socket.getOutputStream());
        }
        catch (IOException e)
        {
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
                } 
                
                else if (ligneRecue != null && !ligneRecue.isEmpty()) {
                    if (cryptoAES == null) {
                        // On récupère la clé AES envoyée par le client, puis on s'en sert pour encrypter une nouvelle clé AES,
                        // qu'on envoie au client et qui sera employée pour communiquer à partir de maintenant.
                        System.out.println("LIGNE " + ligneRecue + " " +  " ...");
                        System.out.println(BaseDeDonnees.getHash(ligneRecue));
                        String ligneDecryptee = decryptionRSA.decrypter(ligneRecue);
                        CryptoAES cryptoAESTemp = new CryptoAES(ligneDecryptee);
                        cryptoAES = new CryptoAES();
                        outputStream.writeBytes(cryptoAESTemp.encryption(cryptoAES.getCle()) + "\n");
                    }
                    else
                    {
                        ligneRecue = cryptoAES.decryption(ligneRecue);
                        
                        try 
                        {
                            System.out.println(interServ.executerCommande(ligneRecue));
                            String message = interServ.executerCommande(ligneRecue);
                            if(!message.isEmpty())
                            {
                                for(String morceau : message.split("\n"))
                                    outputStream.writeBytes(cryptoAES.encryption(morceau) + "\n");

                            }                            
                            outputStream.writeBytes("\n");
                        } 
                        
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                        outputStream.flush();

                    }
                }
            }
        } 
        catch (Exception e)
        {
            e.printStackTrace();
        }

        System.out.println("Connexion terminée.");
        nbConnexions--;
    }
}
