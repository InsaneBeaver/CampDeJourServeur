
package serveur;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.sql.SQLException;
import org.json.JSONArray;



public class InterfaceServeur
{
    public final BaseDeDonnees bdd;
    public InterfaceServeur() throws SQLException, ClassNotFoundException
    {
        bdd = new BaseDeDonnees();
    }
    
    String executerCommande(String cmd) throws SQLException
    {
        try {
            String[] decoupage = cmd.split(" ");
            String nomCommande = decoupage[0];
            String mdpHashe = "";
            if(decoupage.length > 1) 
                mdpHashe = bdd.getHash(decoupage[1]);
            if(nomCommande.equals("liste"))
                return getListeEnfants(mdpHashe);

            else if(nomCommande.equals("getenfant") && estPermis(mdpHashe, Integer.parseInt(decoupage[2])))
                return bdd.getEnfant(Integer.parseInt(decoupage[2]));

            else if(nomCommande.equals("setpresence") && estPermis(mdpHashe, Integer.parseInt(decoupage[2])))
            {
                bdd.changerPresence(Integer.parseInt(decoupage[2]), Integer.parseInt(decoupage[3]));
                return "ok";
            }
/*
            else if(decoupage[0].equals("ajoutparent") && bdd.getHash(decoupage[1]).equals(hashMdpAdmin))
            {
                bdd.mettreParent(decoupage[2], decoupage[3]);
                return "ok";
            }
*/
            else if(nomCommande.equals("ping"))
                return "pong";

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        
        return "Erreur";
        
    }
    
    String getListeEnfants(String mdp) throws SQLException
    {
        return bdd.getEnfants(mdp);
    }
    
    boolean estPermis(String mdpHashe, int id) throws SQLException
    {
        return bdd.estPermis(mdpHashe, id);
    }
}
