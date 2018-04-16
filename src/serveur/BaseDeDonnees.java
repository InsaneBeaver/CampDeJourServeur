
package serveur;
import java.sql.*;
import java.io.*;
import java.util.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import org.json.*;


public class BaseDeDonnees {
    private final static String NOM_BASE = "CampDeJourbdeb.db";
    private final static String TABLE_PARENTS_ENFANTS = "parents";
    private final static String TABLE_ENFANTS = "enfants";
  
    Connection connexion;
    
    public BaseDeDonnees() throws SQLException, ClassNotFoundException
    {
        Class.forName("org.sqlite.JDBC");
        File f = new File(NOM_BASE);
        boolean existe = f.exists();
        
        connexion = DriverManager.getConnection("jdbc:sqlite:"+NOM_BASE);

        if(!existe)
        {
            connexion.createStatement().execute("CREATE TABLE " + TABLE_PARENTS_ENFANTS + " (mdphash varchar(256) NOT NULL UNIQUE, enfants varchar(1024))");
            connexion.createStatement().execute("CREATE TABLE " + TABLE_ENFANTS + " (nom varchar(64), prenom varchar(64), saitNager int, sexe varchar(1), id int NOT NULL UNIQUE, estPresent int, dateNaissance varchar(64))");
        }
    }
    
    public String getEnfant(int id) throws SQLException
    {
        JSONObject obj = new JSONObject();
        ResultSet rs = connexion.createStatement().executeQuery("Select * from " + TABLE_ENFANTS + " where id=" + id);
        if(!rs.next()) return "{}";
        obj.put("nom", rs.getString("nom"));
        obj.put("prenom", rs.getString("prenom"));
        obj.put("saitNager", rs.getInt("saitNager") == 1);
        obj.put("sexe", rs.getString("sexe"));
        obj.put("id", id);
        obj.put("estPresent", rs.getInt("estPresent") == 1);
        obj.put("dateNaissance", rs.getString("dateNaissance"));
        return obj.toString(); 

    }
    
    public void changerPresence(int id, int nouvelEtat) throws SQLException
    {
        connexion.createStatement().execute("UPDATE " + TABLE_ENFANTS + " SET estPresent='" + nouvelEtat + "' WHERE id=" + id);
    }
    
    public boolean estPermis(String hash, int id) throws SQLException
    {
        System.out.println("LE HASH " + hash);
        ResultSet rs = connexion.createStatement().executeQuery("SELECT enfants from " + TABLE_PARENTS_ENFANTS + " where mdphash='" + hash+"'");
        rs.next();
        JSONArray liste = new JSONArray(rs.getString("enfants"));
        boolean estPermis = false;
        for(int i = 0; i < liste.length() && !estPermis; i++) estPermis |= liste.getInt(i) == id;
        return estPermis;
    }
    
    public String getEnfants(String mdpHashe) throws SQLException
    {
       ResultSet rs = connexion.createStatement().executeQuery("SELECT * from " + TABLE_PARENTS_ENFANTS + " where mdphash='" + mdpHashe + "'");
       if(!rs.next()) return "[]";
       
       JSONArray liste = new JSONArray(rs.getString("enfants"));
       JSONArray listeARetourner = new JSONArray();
       for(int i = 0; i < liste.length(); i++)
       {
           int id = liste.getInt(i);
           listeARetourner.put(getEnfant(id));
       }
       return listeARetourner.toString();
    }
    
    public void mettreEnfant(String prenom, String nom, int id, boolean saitNager, String sexe, boolean estPresent, String dateNaissance) throws SQLException
    {
        String valId = ""+id;
        String valSaitNager = saitNager ? "1" : "0";
        String valEstPresent = estPresent ? "1" : "0";
  
        connexion.createStatement().execute("insert into " + TABLE_ENFANTS + " (nom, prenom, saitNager, sexe, id, estPresent, dateNaissance) values ('" + nom + "', '" + prenom + "', " + valSaitNager + ", '" + 
                sexe + "', " + valId + ", " + valEstPresent + ", '" + dateNaissance+"')");
    }
    
    public void mettreParent(String mdp, String listeIds) throws SQLException
    {
        System.out.println("insert into " + TABLE_PARENTS_ENFANTS + " values ('" + getHash(mdp) + "', '" + listeIds + "'");
        connexion.createStatement().execute("insert into " + TABLE_PARENTS_ENFANTS + " values ('" + getHash(mdp) + "', '" + listeIds + "')");
    }
    
    public final static String getHash(String chaine)
    {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            String hash = Base64.getEncoder().encodeToString(digest.digest(chaine.getBytes(StandardCharsets.UTF_8)));
            return hash;
        }
        catch(Exception e) {}
        return "";
    }
    
    
    

}
