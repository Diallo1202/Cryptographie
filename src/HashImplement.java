import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;

public class HashImplement implements IHash {

    @Override
    public String hash(String text, String algorithm) throws Exception {
        MessageDigest digest = MessageDigest.getInstance(algorithm);
        byte[] hashBytes = digest.digest(text.getBytes("UTF-8"));
        return bytesToHex(hashBytes);
    }

    @Override
    public String hashFile(File file, String algorithm) throws Exception {
        MessageDigest digest = MessageDigest.getInstance(algorithm);

        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[8192];
            int bytesRead;

            while ((bytesRead = fis.read(buffer)) != -1) {
                digest.update(buffer, 0, bytesRead);
            }
        }

        byte[] hashBytes = digest.digest();
        return bytesToHex(hashBytes);
    }

    @Override
    public boolean verify(String text, String expectedHash, String algorithm) throws Exception {
        String actualHash = hash(text, algorithm);
        return actualHash.equalsIgnoreCase(expectedHash.replaceAll("\\s+", ""));
    }

    @Override
    public boolean verifyFile(File file, String expectedHash, String algorithm) throws Exception {
        String actualHash = hashFile(file, algorithm);
        return actualHash.equalsIgnoreCase(expectedHash.replaceAll("\\s+", ""));
    }

    // Convertir bytes en hexadécimal
    private String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString().toLowerCase();
    }

    // Obtenir la taille du hash en bits
    public int getHashSize(String algorithm) {
        switch (algorithm) {
            case "MD5":
                return 128;
            case "SHA-1":
                return 160;
            case "SHA-256":
                return 256;
            case "SHA-512":
                return 512;
            default:
                return 0;
        }
    }

    // Vérifier si l'algorithme est déprécié
    public boolean isDeprecated(String algorithm) {
        return algorithm.equals("MD5") || algorithm.equals("SHA-1");
    }
}