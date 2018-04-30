// Emprunté ici https://stackoverflow.com/questions/10131377/socket-programming-multiple-client-to-one-server
package serveur;

import java.io.*;
import java.net.*;
import java.util.Base64;

public class ThreadDeSocket extends Thread {

    private final Socket socket;
    private final InterfaceServeur interServ;
    private CryptoAES cryptoAES = null;
    private final RSADecryption decryption;
    public static int nbConnexions = 0;
    public static final int MAXCONNEXIONS = 10;
    
    

    public ThreadDeSocket(Socket clientSocket, InterfaceServeur interServ, RSADecryption decryption) {
        this.socket = clientSocket;
        this.interServ = interServ;
        this.decryption = decryption;
    }

    public void run() {
        nbConnexions++;
        System.out.println("nouvelle connexion");
        InputStream inp = null;
        BufferedReader brinp = null;
        DataOutputStream out = null;

        try {
            inp = socket.getInputStream();
            brinp = new BufferedReader(new InputStreamReader(inp));
            out = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            return;
        }

        String line;

        try {
            while (true) {
                line = brinp.readLine();

                if ((line == null) || line.equalsIgnoreCase("QUIT")) {
                    socket.close();
                    break;
                } else if (!line.isEmpty()) {
                    if (cryptoAES == null) {
                        // On ramasse la clé publique.
                        String ligneDecryptee = decryption.decrypter(line);
                        System.out.println(ligneDecryptee);
                        CryptoAES cryptoAESTemp = new CryptoAES(ligneDecryptee);
                        System.out.println(cryptoAESTemp.encryption("re"));
                        cryptoAES = new CryptoAES();
                        
                        out.writeBytes(cryptoAESTemp.encryption(cryptoAES.getCle()) + "\n");
                        out.flush();
                        System.out.println("Écrit");
                       
                    }
                    else
                    {
                        line = cryptoAES.decryption(line);
                        if(!line.equals("ping"))
                        System.out.println("Le message: " + line);
                        
                        try {
                            System.out.println(interServ.executerCommande(line));
                            String message = cryptoAES.encryption(interServ.executerCommande(line));
                            out.writeBytes(message + "\n");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        out.flush();

                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();

        }

        System.out.println("sorti");
        nbConnexions--;
    }
}
