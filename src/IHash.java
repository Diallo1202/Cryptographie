import java.io.File;

public interface IHash {
    // Calculer le hash d'un texte
    String hash(String text, String algorithm) throws Exception;

    // Calculer le hash d'un fichier
    String hashFile(File file, String algorithm) throws Exception;

    // Vérifier si un hash correspond à un texte
    boolean verify(String text, String hash, String algorithm) throws Exception;

    // Vérifier si un hash correspond à un fichier
    boolean verifyFile(File file, String hash, String algorithm) throws Exception;
}