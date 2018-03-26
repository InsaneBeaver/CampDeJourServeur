
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
    BaseDeDonnees bdd;
    private final int keySize = 1024;
    private final String hashMdpAdmin = "YGy1nx6e2TBtxc3uiaMohjUh37vFEhOgYpagjLROSF4=";
    
    public InterfaceServeur() throws SQLException, ClassNotFoundException
    {
        bdd = new BaseDeDonnees();
    }
    
    String executerCommsande(String cmd) throws SQLException
    {
        String[] decoupage = cmd.split(" ");
        if(decoupage[0].equals("liste"))
            return getListeEnfants(decoupage[1]);
        
        else if(decoupage[0].equals("getenfant") && estPermis(decoupage[1], Integer.parseInt(decoupage[2])))
            return bdd.getEnfant(Integer.parseInt(decoupage[2]));
        
        else if(decoupage[0].equals("setpresence") && estPermis(decoupage[1], Integer.parseInt(decoupage[2])))
        {
            bdd.changerPresence(Integer.parseInt(decoupage[2]), decoupage[3]);
            return "ok";
        }
        
        else if(decoupage[0].equals("ajoutparent") && bdd.getHash(decoupage[1]).equals(hashMdpAdmin))
        {
            bdd.mettreParent(decoupage[2], decoupage[3]);
            return "ok";
        }
        
        return "Erreur";
    }
    
    String getListeEnfants(String mdp) throws SQLException
    {
        return bdd.getEnfants(mdp);
    }
    
    boolean estPermis(String mdp, int id) throws SQLException
    {
        return bdd.estPermis(bdd.getHash(mdp), id);
    }
}
