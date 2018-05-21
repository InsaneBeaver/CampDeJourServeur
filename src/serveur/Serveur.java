
package serveur;
import java.net.*;


// Emprunté ici https://stackoverflow.com/questions/10131377/socket-programming-multiple-client-to-one-server
public class Serveur {
    
    private final static int PORT = 2000;
    

    /**
     * Fonction principale
     * @param args
     * @throws Exception 
     */
     public static void main(String args[]) throws Exception{
         RSADecryption decryption = new RSADecryption("clepriveeserv");
         CryptoAES foo = new CryptoAES();
         
         ServerSocket serverSocket;
         InterfaceServeur interfaceServeur = new InterfaceServeur();
         Socket socket;

        try {
            serverSocket = new ServerSocket(PORT);
            System.out.println("Commence à écouter ...");
            while(true) 
            {
                try{
                    if(ThreadDeSocket.nbConnexions < ThreadDeSocket.MAXCONNEXIONS)
                    {
                        socket = serverSocket.accept();
                        
                        new ThreadDeSocket(socket, interfaceServeur, decryption).start();
                    }
                }
                catch(Exception e) {}
            }
        } catch (Exception e) {
            e.printStackTrace();

        }

    }
    
}
