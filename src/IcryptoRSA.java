import java.io.IOException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

public interface IcryptoRSA {
    // Génération de la paire de clés
    KeyPair genKey(String algo, int keySize) throws NoSuchAlgorithmException;

    // Chiffrement
    String encrypt(KeyPair key, String textClair, String transformation) throws Exception;

    // Déchiffrement
    String decrypt(KeyPair key, String cipherText, String transformation) throws Exception;

    // Sauvegarde des clés
    void savePublicKey(PublicKey publicKey, String filePath) throws IOException;
    void savePrivateKey(PrivateKey privateKey, String filePath) throws IOException;
    void saveKeyPair(KeyPair keyPair, String publicKeyPath, String privateKeyPath) throws IOException;

    // Chargement des clés
    PublicKey loadPublicKey(String filePath) throws Exception;
    PrivateKey loadPrivateKey(String filePath) throws Exception;
    KeyPair loadKeyPair(String publicKeyPath, String privateKeyPath) throws Exception;

    // Conversion clés <-> String
    String publicKeyToString(PublicKey publicKey);
    String privateKeyToString(PrivateKey privateKey);
    PublicKey stringToPublicKey(String keyString) throws Exception;
    PrivateKey stringToPrivateKey(String keyString) throws Exception;

    // Signature numérique
    String sign(PrivateKey privateKey, String message) throws Exception;
    boolean verify(PublicKey publicKey, String message, String signature) throws Exception;
}