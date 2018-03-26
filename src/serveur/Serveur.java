
package serveur;
import java.net.*;


// Emprunté ici https://stackoverflow.com/questions/10131377/socket-programming-multiple-client-to-one-server
public class Serveur {
    
    private final static int PORT = 2006;
    

     public static void main(String args[]) throws Exception{
         RSADecryption decryption = new RSADecryption("clepriveeserv");
         
         ServerSocket serverSocket;
         InterfaceServeur interfaceServeur = new InterfaceServeur();
         Socket socket;

        try {
            serverSocket = new ServerSocket(PORT);
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
