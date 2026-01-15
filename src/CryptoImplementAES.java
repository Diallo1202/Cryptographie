import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class CryptoImplementAES implements IcryptoAES {

    @Override
    public SecretKey genKey(String algo, int keySize) throws NoSuchAlgorithmException {
        if ("AES".equalsIgnoreCase(algo)) {
            if (keySize != 128 && keySize != 192 && keySize != 256) {
                throw new IllegalArgumentException("Le choix doit être fait entre les valeurs suivantes : 128, 192 et 256");
            }
        }
        KeyGenerator keyGen = KeyGenerator.getInstance(algo);
        keyGen.init(keySize);
        return keyGen.generateKey();
    }

    @Override
    public String encrypt(SecretKey key, String textClair, String transformation) throws Exception {
        Cipher c = Cipher.getInstance(transformation);

        byte[] cipherText;

        if (transformation.contains("ECB")) {
            // ECB → PAS D'IV
            c.init(Cipher.ENCRYPT_MODE, key);
            cipherText = c.doFinal(textClair.getBytes("UTF-8"));

            return Base64.getEncoder().encodeToString(cipherText);
        }

        // CBC → IV obligatoire
        byte[] iv = new byte[16];
        SecureRandom random = new SecureRandom();
        random.nextBytes(iv);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        c.init(Cipher.ENCRYPT_MODE, key, ivSpec);
        cipherText = c.doFinal(textClair.getBytes("UTF-8"));

        // IV + cipherText
        byte[] combined = new byte[iv.length + cipherText.length];
        System.arraycopy(iv, 0, combined, 0, iv.length);
        System.arraycopy(cipherText, 0, combined, iv.length, cipherText.length);

        return Base64.getEncoder().encodeToString(combined);
    }


    @Override
    public String decrypt(SecretKey key, String cipherText, String transformation) throws Exception {
        Cipher c = Cipher.getInstance(transformation);
        byte[] decoded = Base64.getDecoder().decode(cipherText);

        if (transformation.contains("ECB")) {
            // ECB → PAS D'IV
            c.init(Cipher.DECRYPT_MODE, key);
            byte[] plainText = c.doFinal(decoded);
            return new String(plainText, "UTF-8");
        }

        // CBC → extraire IV
        byte[] iv = new byte[16];
        System.arraycopy(decoded, 0, iv, 0, iv.length);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);

        byte[] cipherBytes = new byte[decoded.length - iv.length];
        System.arraycopy(decoded, iv.length, cipherBytes, 0, cipherBytes.length);

        c.init(Cipher.DECRYPT_MODE, key, ivSpec);
        byte[] plainText = c.doFinal(cipherBytes);

        return new String(plainText, "UTF-8");
    }


    @Override
    public void saveKey(SecretKey key, String filePath) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            byte[] keyBytes = key.getEncoded();
            fos.write(keyBytes);
        }
    }

    @Override
    public SecretKey loadKey(String filePath) throws Exception {
        try (FileInputStream fis = new FileInputStream(filePath)) {
            byte[] keyBytes = fis.readAllBytes();
            return new SecretKeySpec(keyBytes, "AES");
        }
    }

    @Override
    public String keyToString(SecretKey key) {
        return Base64.getEncoder().encodeToString(key.getEncoded());
    }

    @Override
    public SecretKey stringToKey(String keyString) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(keyString);
        return new SecretKeySpec(keyBytes, "AES");
    }
}