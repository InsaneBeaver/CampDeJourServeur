package serveur;

import java.sql.SQLException;

public class InterfaceServeur
{
    public final BaseDeDonnees bdd;
    private final static String mdpAdmin = "cJL9jWxwUc77PhgkZXlZT3/ddTqL+LhDwub80GVnN4Q="; // carotte
    
    /**
     * Constructeur
     * @throws SQLException
     * @throws ClassNotFoundException 
     */
    public InterfaceServeur() throws SQLException, ClassNotFoundException
    {
        bdd = new BaseDeDonnees();
    }
    
    /**
     * Exécute une commande du client et retourne le résultat
     * @param cmd Commande reçue
     * @return Résultat
     * @throws SQLException Si un incident regrettable s'est produit
     */
    public String executerCommande(String cmd) throws SQLException
    {
        try {
            String[] decoupage = cmd.split(" ");
            String nomCommande = decoupage[0];
            String mdpHashe = "";
            if(decoupage.length > 1) 
                mdpHashe = bdd.getHash(decoupage[1]);
            if(nomCommande.equals("liste"))
            {
                if(mdpAdmin.equals(mdpHashe))
                    return bdd.getEnfantsAdmin();
                else
                    return bdd.getEnfantsParent(mdpHashe);
            }

            else if(nomCommande.equals("getenfant") && estPermis(mdpHashe, Integer.parseInt(decoupage[2])))
                return bdd.getEnfant(Integer.parseInt(decoupage[2]));

            else if(nomCommande.equals("setpresence") && estPermis(mdpHashe, Integer.parseInt(decoupage[2])))
            {
                bdd.changerPresence(Integer.parseInt(decoupage[2]), Integer.parseInt(decoupage[3]));
                return "ok";
            }
            // Commande qui sert juste à vérifier que la connexion est toujours en place
            else if(nomCommande.equals("ping"))
                return "pong";

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        
        return "Erreur";
        
    }
    
    /**
     * Vérifie si l'utilisateur ayant le mot de passe en question à accès à l'enfant à l'id
     * @param mdpHashe Mot de passe hashé de l'utilisateur
     * @param id Id de l'enfant
     * @return Si permis
     * @throws SQLException 
     */
    private boolean estPermis(String mdpHashe, int id) throws SQLException
    {
        return mdpHashe.equals(mdpAdmin) || bdd.estPermis(mdpHashe, id);
    }
}
