import javax.crypto.Cipher;
import java.io.*;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

public class CryptoImplementRSA implements IcryptoRSA {

    @Override
    public KeyPair genKey(String algo, int keySize) throws NoSuchAlgorithmException {
        if ("RSA".equalsIgnoreCase(algo)) {
            if (keySize != 2048 && keySize != 3072) {
                throw new IllegalArgumentException("Le choix doit être fait entre les valeurs suivantes : 2048 et 3072");
            }
        }
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance(algo);
        keyGen.initialize(keySize);
        return keyGen.generateKeyPair();
    }

    @Override
    public String encrypt(KeyPair key, String textClair, String transformation) throws Exception {
        PublicKey publicKey = key.getPublic();
        Cipher c = Cipher.getInstance(transformation);
        c.init(Cipher.ENCRYPT_MODE, publicKey);
        byte[] cipherText = c.doFinal(textClair.getBytes("UTF-8"));
        return Base64.getEncoder().encodeToString(cipherText);
    }

    @Override
    public String decrypt(KeyPair key, String cipherText, String transformation) throws Exception {
        PrivateKey privateKey = key.getPrivate();
        Cipher c = Cipher.getInstance(transformation);
        c.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] cipherTextDecode = Base64.getDecoder().decode(cipherText);
        byte[] plainText = c.doFinal(cipherTextDecode);
        return new String(plainText, "UTF-8");
    }

    // Sauvegarder la clé publique
    public void savePublicKey(PublicKey publicKey, String filePath) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            byte[] keyBytes = publicKey.getEncoded();
            fos.write(keyBytes);
        }
    }

    // Sauvegarder la clé privée
    public void savePrivateKey(PrivateKey privateKey, String filePath) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            byte[] keyBytes = privateKey.getEncoded();
            fos.write(keyBytes);
        }
    }

    // Charger la clé publique
    public PublicKey loadPublicKey(String filePath) throws Exception {
        try (FileInputStream fis = new FileInputStream(filePath)) {
            byte[] keyBytes = fis.readAllBytes();
            X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(spec);
        }
    }

    // Charger la clé privée
    public PrivateKey loadPrivateKey(String filePath) throws Exception {
        try (FileInputStream fis = new FileInputStream(filePath)) {
            byte[] keyBytes = fis.readAllBytes();
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePrivate(spec);
        }
    }

    // Sauvegarder les deux clés en même temps
    public void saveKeyPair(KeyPair keyPair, String publicKeyPath, String privateKeyPath) throws IOException {
        savePublicKey(keyPair.getPublic(), publicKeyPath);
        savePrivateKey(keyPair.getPrivate(), privateKeyPath);
    }

    // Charger une paire de clés
    public KeyPair loadKeyPair(String publicKeyPath, String privateKeyPath) throws Exception {
        PublicKey publicKey = loadPublicKey(publicKeyPath);
        PrivateKey privateKey = loadPrivateKey(privateKeyPath);
        return new KeyPair(publicKey, privateKey);
    }

    // Convertir la clé publique en String Base64
    public String publicKeyToString(PublicKey publicKey) {
        return Base64.getEncoder().encodeToString(publicKey.getEncoded());
    }

    // Convertir la clé privée en String Base64
    public String privateKeyToString(PrivateKey privateKey) {
        return Base64.getEncoder().encodeToString(privateKey.getEncoded());
    }

    // Reconstruire la clé publique depuis un String Base64
    public PublicKey stringToPublicKey(String keyString) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(keyString);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(spec);
    }

    // Reconstruire la clé privée depuis un String Base64
    public PrivateKey stringToPrivateKey(String keyString) throws Exception {
        byte[] keyBytes = Base64.getDecoder().decode(keyString);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(spec);
    }

    // Signer un document avec la clé privée
    @Override
    public String sign(PrivateKey privateKey, String message) throws Exception {
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(message.getBytes("UTF-8"));
        byte[] signatureBytes = signature.sign();
        return Base64.getEncoder().encodeToString(signatureBytes);
    }

    // Vérifier une signature avec la clé publique
    @Override
    public boolean verify(PublicKey publicKey, String message, String signatureStr) throws Exception {
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initVerify(publicKey);
        signature.update(message.getBytes("UTF-8"));
        byte[] signatureBytes = Base64.getDecoder().decode(signatureStr);
        return signature.verify(signatureBytes);
    }
}