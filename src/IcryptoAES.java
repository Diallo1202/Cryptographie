import javax.crypto.SecretKey;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public interface IcryptoAES {
    // Génération de la clé secrète
    SecretKey genKey(String algo, int keySize) throws NoSuchAlgorithmException;

    // Chiffrement
    String encrypt(SecretKey key, String textClair, String transformation) throws Exception;

    // Déchiffrement
    String decrypt(SecretKey key, String cipherText, String transformation) throws Exception;

    // Sauvegarde de la clé
    void saveKey(SecretKey key, String filePath) throws IOException;

    // Chargement de la clé
    SecretKey loadKey(String filePath) throws Exception;

    // Conversion clé <-> String
    String keyToString(SecretKey key);
    SecretKey stringToKey(String keyString) throws Exception;
}