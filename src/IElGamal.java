import java.io.IOException;
import java.math.BigInteger;

public interface IElGamal {
    // Classe pour stocker les clés
    class ElGamalKeys {
        public BigInteger p;  // Nombre premier
        public BigInteger g;  // Générateur
        public BigInteger y;  // Clé publique (y = g^x mod p)
        public BigInteger x;  // Clé privée

        public ElGamalKeys(BigInteger p, BigInteger g, BigInteger y, BigInteger x) {
            this.p = p;
            this.g = g;
            this.y = y;
            this.x = x;
        }
    }

    // Classe pour stocker le texte chiffré
    class CipherText {
        public BigInteger c1;
        public BigInteger c2;

        public CipherText(BigInteger c1, BigInteger c2) {
            this.c1 = c1;
            this.c2 = c2;
        }
    }

    // Génération des clés
    ElGamalKeys generateKeys(int bitLength) throws Exception;

    // Chiffrement
    String encrypt(BigInteger p, BigInteger g, BigInteger y, String plainText) throws Exception;

    // Déchiffrement
    String decrypt(BigInteger p, BigInteger x, String cipherText) throws Exception;

    // Sauvegarde des clés
    void savePublicKey(BigInteger p, BigInteger g, BigInteger y, String filePath) throws IOException;
    void savePrivateKey(BigInteger p, BigInteger x, String filePath) throws IOException;
    void saveKeys(ElGamalKeys keys, String publicPath, String privatePath) throws IOException;

    // Chargement des clés
    ElGamalKeys loadPublicKey(String filePath) throws IOException;
    ElGamalKeys loadPrivateKey(String filePath) throws IOException;

    // Conversion clés <-> String
    String keysToString(ElGamalKeys keys);
}