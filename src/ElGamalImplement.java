import java.io.*;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Base64;

public class ElGamalImplement implements IElGamal {

    private SecureRandom random = new SecureRandom();

    @Override
    public ElGamalKeys generateKeys(int bitLength) throws Exception {
        // Générer un nombre premier p
        BigInteger p = BigInteger.probablePrime(bitLength, random);

        // Trouver un générateur g
        BigInteger g = findGenerator(p);

        // Générer la clé privée x (1 < x < p-1)
        BigInteger x = new BigInteger(bitLength - 1, random);
        while (x.compareTo(BigInteger.ONE) <= 0 || x.compareTo(p.subtract(BigInteger.ONE)) >= 0) {
            x = new BigInteger(bitLength - 1, random);
        }

        // Calculer la clé publique y = g^x mod p
        BigInteger y = g.modPow(x, p);

        return new ElGamalKeys(p, g, y, x);
    }

    @Override
    public String encrypt(BigInteger p, BigInteger g, BigInteger y, String plainText) throws Exception {
        byte[] messageBytes = plainText.getBytes("UTF-8");
        StringBuilder result = new StringBuilder();

        // Chiffrer bloc par bloc
        int blockSize = (p.bitLength() - 1) / 8 - 1; // Taille de bloc en bytes

        for (int i = 0; i < messageBytes.length; i += blockSize) {
            int length = Math.min(blockSize, messageBytes.length - i);
            byte[] block = new byte[length];
            System.arraycopy(messageBytes, i, block, 0, length);

            // Convertir le bloc en BigInteger
            BigInteger m = new BigInteger(1, block);

            // Générer k aléatoire (1 < k < p-1)
            BigInteger k = new BigInteger(p.bitLength() - 2, random);
            while (k.compareTo(BigInteger.ONE) <= 0 || k.compareTo(p.subtract(BigInteger.ONE)) >= 0) {
                k = new BigInteger(p.bitLength() - 2, random);
            }

            // Calculer c1 = g^k mod p
            BigInteger c1 = g.modPow(k, p);

            // Calculer c2 = m * y^k mod p
            BigInteger c2 = m.multiply(y.modPow(k, p)).mod(p);

            // Ajouter au résultat
            if (result.length() > 0) {
                result.append("|");
            }
            result.append(c1.toString(16)).append(":").append(c2.toString(16));
        }

        return Base64.getEncoder().encodeToString(result.toString().getBytes("UTF-8"));
    }

    @Override
    public String decrypt(BigInteger p, BigInteger x, String cipherText) throws Exception {
        String decoded = new String(Base64.getDecoder().decode(cipherText), "UTF-8");
        String[] blocks = decoded.split("\\|");

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        for (String block : blocks) {
            String[] parts = block.split(":");
            BigInteger c1 = new BigInteger(parts[0], 16);
            BigInteger c2 = new BigInteger(parts[1], 16);

            // Calculer s = c1^x mod p
            BigInteger s = c1.modPow(x, p);

            // Calculer l'inverse modulaire de s
            BigInteger sInv = s.modInverse(p);

            // Calculer m = c2 * s^(-1) mod p
            BigInteger m = c2.multiply(sInv).mod(p);

            // Convertir en bytes
            byte[] messageBytes = m.toByteArray();

            // Retirer le bit de signe si nécessaire
            if (messageBytes[0] == 0 && messageBytes.length > 1) {
                byte[] temp = new byte[messageBytes.length - 1];
                System.arraycopy(messageBytes, 1, temp, 0, temp.length);
                messageBytes = temp;
            }

            outputStream.write(messageBytes);
        }

        return new String(outputStream.toByteArray(), "UTF-8");
    }

    @Override
    public void savePublicKey(BigInteger p, BigInteger g, BigInteger y, String filePath) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(p);
            oos.writeObject(g);
            oos.writeObject(y);
        }
    }

    @Override
    public void savePrivateKey(BigInteger p, BigInteger x, String filePath) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
            oos.writeObject(p);
            oos.writeObject(x);
        }
    }

    @Override
    public void saveKeys(ElGamalKeys keys, String publicPath, String privatePath) throws IOException {
        savePublicKey(keys.p, keys.g, keys.y, publicPath);
        savePrivateKey(keys.p, keys.x, privatePath);
    }

    @Override
    public ElGamalKeys loadPublicKey(String filePath) throws IOException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            BigInteger p = (BigInteger) ois.readObject();
            BigInteger g = (BigInteger) ois.readObject();
            BigInteger y = (BigInteger) ois.readObject();
            return new ElGamalKeys(p, g, y, null);
        } catch (ClassNotFoundException e) {
            throw new IOException("Erreur lors du chargement de la clé", e);
        }
    }

    @Override
    public ElGamalKeys loadPrivateKey(String filePath) throws IOException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            BigInteger p = (BigInteger) ois.readObject();
            BigInteger x = (BigInteger) ois.readObject();
            return new ElGamalKeys(p, null, null, x);
        } catch (ClassNotFoundException e) {
            throw new IOException("Erreur lors du chargement de la clé", e);
        }
    }

    @Override
    public String keysToString(ElGamalKeys keys) {
        StringBuilder sb = new StringBuilder();
        sb.append("p: ").append(keys.p.toString(16)).append("\n");
        if (keys.g != null) sb.append("g: ").append(keys.g.toString(16)).append("\n");
        if (keys.y != null) sb.append("y: ").append(keys.y.toString(16)).append("\n");
        if (keys.x != null) sb.append("x: ").append(keys.x.toString(16));
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

        // Par défaut, retourner 2 (devrait toujours trouver un générateur avant)
        return BigInteger.valueOf(2);
    }

    // Vérifier si g est un générateur
    private boolean isGenerator(BigInteger g, BigInteger p, BigInteger pMinusOne) {
        // Test simple : vérifier que g^((p-1)/2) != 1 mod p
        BigInteger exp = pMinusOne.divide(BigInteger.valueOf(2));
        return !g.modPow(exp, p).equals(BigInteger.ONE);
    }
}