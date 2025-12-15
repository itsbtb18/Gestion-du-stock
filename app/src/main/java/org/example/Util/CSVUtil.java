package org.example.util;

import org.example.app.AppConfig;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class CSVUtil {
    
    private static final String SEPARATOR = AppConfig.CSV_SEPARATOR;
    private static final String LINE_SEPARATOR = System.lineSeparator();
    
    private CSVUtil() {
        throw new UnsupportedOperationException("Utility class");
    }
    
    public static boolean exporter(String fichier, List<String[]> donnees) {
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(fichier), StandardCharsets.UTF_8))) {
            
            for (String[] ligne : donnees) {
                writer.write(creerLigneCSV(ligne));
                writer.write(LINE_SEPARATOR);
            }
            
            return true;
        } catch (IOException e) {
            System.err.println("Erreur lors de l'export CSV: " + e.getMessage());
            return false;
        }
    }
    
    public static List<String[]> importer(String fichier) {
        List<String[]> donnees = new ArrayList<>();
        
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(fichier), StandardCharsets.UTF_8))) {
            
            String ligne;
            while ((ligne = reader.readLine()) != null) {
                String[] valeurs = ligne.split(SEPARATOR);
                donnees.add(valeurs);
            }
            
        } catch (IOException e) {
            System.err.println("Erreur lors de l'import CSV: " + e.getMessage());
        }
        
        return donnees;
    }
    
    public static String creerLigneCSV(String[] valeurs) {
        StringBuilder sb = new StringBuilder();
        
        for (int i = 0; i < valeurs.length; i++) {
            if (i > 0) {
                sb.append(SEPARATOR);
            }
            sb.append(echapperValeur(valeurs[i]));
        }
        
        return sb.toString();
    }
    
    private static String echapperValeur(String valeur) {
        if (valeur == null) {
            return "";
        }
        
        if (valeur.contains(SEPARATOR) || valeur.contains("\"") || valeur.contains("\n")) {
            return "\"" + valeur.replace("\"", "\"\"") + "\"";
        }
        
        return valeur;
    }
    
    public static <T> List<String[]> convertirEnTableau(List<T> objets, ConvertisseurCSV<T> convertisseur) {
        List<String[]> resultat = new ArrayList<>();
        
        resultat.add(convertisseur.obtenirEntetes());
        
        for (T objet : objets) {
            resultat.add(convertisseur.convertirEnLigne(objet));
        }
        
        return resultat;
    }
    
    public interface ConvertisseurCSV<T> {
        String[] obtenirEntetes();
        String[] convertirEnLigne(T objet);
    }
    
    public static boolean exportVentes(List<org.example.model.entity.Vente> ventes, String filename) {
        List<String[]> data = new ArrayList<>();
        
        data.add(new String[]{"Numero", "Date", "Client", "Montant Total", "Remise", "TVA", "Montant Final", "Mode Paiement"});
        
        for (org.example.model.entity.Vente vente : ventes) {
            data.add(new String[]{
                vente.getNumero(),
                vente.getDateVente().toString(),
                vente.getClient() != null ? vente.getClient().getNom() : "Anonyme",
                String.valueOf(vente.getMontantTotal()),
                String.valueOf(vente.getMontantRemise()),
                String.valueOf(vente.getMontantTVA()),
                String.valueOf(vente.getMontantFinal()),
                vente.getModePaiement()
            });
        }
        
        return exporter(filename, data);
    }
    
    public static boolean exportMouvements(List<org.example.model.entity.MouvementStock> mouvements, String filename) {
        List<String[]> data = new ArrayList<>();
        
        data.add(new String[]{"Date", "Produit", "Type", "Quantite", "Motif"});
        
        for (org.example.model.entity.MouvementStock mouv : mouvements) {
            data.add(new String[]{
                mouv.getDateMouvement().toString(),
                mouv.getProduit() != null ? mouv.getProduit().getNom() : "N/A",
                mouv.getTypeMouvement().toString(),
                String.valueOf(mouv.getQuantite()),
                mouv.getMotif() != null ? mouv.getMotif() : ""
            });
        }
        
        return exporter(filename, data);
    }
}
