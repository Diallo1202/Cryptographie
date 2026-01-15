import java.io.IOException;
import java.math.BigInteger;

public interface IDiffieHellman {
    // Classe pour stocker les paramètres publics
    class DHParameters {
        public BigInteger p;  // Nombre premier (modulus)
        public BigInteger g;  // Générateur

        public DHParameters(BigInteger p, BigInteger g) {
            this.p = p;
            this.g = g;
        }
    }

    // Classe pour stocker une paire de clés
    class DHKeyPair {
        public BigInteger privateKey;  // Clé privée (a ou b)
        public BigInteger publicKey;   // Clé publique (A ou B)

        public DHKeyPair(BigInteger privateKey, BigInteger publicKey) {
            this.privateKey = privateKey;
            this.publicKey = publicKey;
        }
    }

    // Générer les paramètres publics (p, g)
    DHParameters generateParameters(int bitLength) throws Exception;

    // Générer une paire de clés (privée, publique)
    DHKeyPair generateKeyPair(DHParameters params) throws Exception;

    // Calculer la clé secrète partagée
    BigInteger computeSharedSecret(BigInteger otherPublicKey, BigInteger myPrivateKey, BigInteger p) throws Exception;

    // Convertir la clé partagée en format utilisable
    String deriveKey(BigInteger sharedSecret, int keyLength) throws Exception;

    // Sauvegarder les paramètres
    void saveParameters(DHParameters params, String filePath) throws IOException;

    // Charger les paramètres
    DHParameters loadParameters(String filePath) throws IOException;

    // Conversion en String
    String parametersToString(DHParameters params);
    String keyPairToString(DHKeyPair keyPair);
}