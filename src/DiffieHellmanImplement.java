import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.SecureRandom;

public class DiffieHellmanImplement implements IDiffieHellman {

    private SecureRandom random = new SecureRandom();

    @Override
    public DHParameters generateParameters(int bitLength) throws Exception {
        // Générer un nombre premier p
        BigInteger p = BigInteger.probablePrime(bitLength, random);

        // Trouver un générateur g
        BigInteger g = findGenerator(p);

        return new DHParameters(p, g);
    }

    @Override
    public DHKeyPair generateKeyPair(DHParameters params) throws Exception {
        // Générer une clé privée aléatoire (1 < privateKey < p-1)
        BigInteger privateKey = new BigInteger(params.p.bitLength() - 1, random);
        while (privateKey.compareTo(BigInteger.ONE) <= 0 ||
                privateKey.compareTo(params.p.subtract(BigInteger.ONE)) >= 0) {
            privateKey = new BigInteger(params.p.bitLength() - 1, random);
        }

        // Calculer la clé publique : publicKey = g^privateKey mod p
        BigInteger publicKey = params.g.modPow(privateKey, params.p);

        return new DHKeyPair(privateKey, publicKey);
    }

    @Override
    public BigInteger computeSharedSecret(BigInteger otherPublicKey, BigInteger myPrivateKey, BigInteger p) throws Exception {
        // Calculer le secret partagé : sharedSecret = otherPublicKey^myPrivateKey mod p
        return otherPublicKey.modPow(myPrivateKey, p);
    }

    @Override
    public String deriveKey(BigInteger sharedSecret, int keyLength) throws Exception {
        // Utiliser SHA-256 pour dériver une clé de la longueur souhaitée
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(sharedSecret.toByteArray());

        // Tronquer ou étendre selon la longueur souhaitée
        StringBuilder key = new StringBuilder();
        for (int i = 0; i < Math.min(keyLength / 8, hash.length); i++) {
            key.append(String.format("%02x", hash[i]));
        }

        return key.toString();
    }

    @Override
    public void saveParameters(DHParameters params, String filePath) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(params.p);
            oos.writeObject(params.g);
        }
    }

    @Override
    public DHParameters loadParameters(String filePath) throws IOException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            BigInteger p = (BigInteger) ois.readObject();
            BigInteger g = (BigInteger) ois.readObject();
            return new DHParameters(p, g);
        } catch (ClassNotFoundException e) {
            throw new IOException("Erreur lors du chargement des paramètres", e);
        }
    }

    @Override
    public String parametersToString(DHParameters params) {
        StringBuilder sb = new StringBuilder();
        sb.append("p (modulus): ").append(params.p.toString(16)).append("\n");
        sb.append("g (générateur): ").append(params.g.toString(16));
        return sb.toString();
    }

    @Override
    public String keyPairToString(DHKeyPair keyPair) {
        StringBuilder sb = new StringBuilder();
        sb.append("Clé privée: ").append(keyPair.privateKey.toString(16)).append("\n");
        sb.append("Clé publique: ").append(keyPair.publicKey.toString(16));
        return sb.toString();
    }

    // Trouver un générateur pour le groupe multiplicatif mod p
    private BigInteger findGenerator(BigInteger p) {
        BigInteger pMinusOne = p.subtract(BigInteger.ONE);

        // Commencer avec 2 comme générateur candidat
        for (BigInteger g = BigInteger.valueOf(2); g.compareTo(p) < 0; g = g.add(BigInteger.ONE)) {
            if (isGenerator(g, p, pMinusOne)) {
                return g;
            }
        }

        // Par défaut, retourner 2
        return BigInteger.valueOf(2);
    }

    // Vérifier si g est un générateur
    private boolean isGenerator(BigInteger g, BigInteger p, BigInteger pMinusOne) {
        // Test simple : vérifier que g^((p-1)/2) != 1 mod p
        BigInteger exp = pMinusOne.divide(BigInteger.valueOf(2));
        return !g.modPow(exp, p).equals(BigInteger.ONE);
    }
}