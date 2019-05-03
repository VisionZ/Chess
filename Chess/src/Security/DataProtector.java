package Security;

import static Util.Constants.UTF_8;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

/**
 * Encrypts and decrypts text using a 128-bit Advanced Encryption Standard (AES) private
 * key which is shared by all instances of this application. This key must not be shared over a
 * connection (or anywhere for that matter) and is strictly private. 
 * The ciphertext is formed by reversing the encrypted bytes of the plaintext 
 * where each byte is separated by a "=" character. A 128-bit key was chosen 
 * because all JVMs are required to implement this key size, meaning that 
 * such keys will enable encryption/decryption on any Java platform.
 * @author Will
 */
public final class DataProtector {

    public static final String SEPERATOR = "=";
    
    private static final Cipher AES_CIPHER = initCipher();
    private static final SecretKey KEY = fetchKey();
    
    //don't use the static initalizer, use private methods to finalize variables
    
    @SuppressWarnings("CallToPrintStackTrace")
    private static Cipher initCipher() {
        try {
            return Cipher.getInstance("AES");
        }
        catch (NoSuchAlgorithmException | NoSuchPaddingException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @SuppressWarnings("CallToPrintStackTrace")
    private static SecretKey fetchKey() {
        ObjectInputStream objectInputStream;
        try {
            objectInputStream = new ObjectInputStream(DataProtector.class.getResourceAsStream("/Security/Key.bin"));
        }
        catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        SecretKey key;
        try {
            key = (SecretKey) objectInputStream.readObject();
            //retreve persistant key
        }
        catch (IOException | ClassNotFoundException | ClassCastException ex) {
            key = null;
            ex.printStackTrace();
        }
        try {
            objectInputStream.close();
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
        return key;
    }
    
    //use persistant stringbuilder for faster string concatenation
    private static final StringBuilder ENCODER = new StringBuilder();

    @SuppressWarnings("CallToPrintStackTrace")
    public synchronized static String encode(String plainText) {
        System.out.println("Encoding: " + plainText);
        try {
            AES_CIPHER.init(Cipher.ENCRYPT_MODE, KEY);
            byte[] cipherBytes = AES_CIPHER.doFinal(plainText.getBytes(UTF_8));
            ENCODER.setLength(0);
            for (int index = (cipherBytes.length - 1); index >= 0; --index) {
                ENCODER.append(cipherBytes[index]);
                if (index == 0) {
                    break;
                }
                ENCODER.append(SEPERATOR);
            }
            return ENCODER.toString();
        }
        catch (UnsupportedEncodingException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
            return null;
        }
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public synchronized static String decode(String cipherText) {
        System.out.println("Decoding: " + cipherText);
        try {
            //byte[] initVector = CIPHER.getParameters().getParameterSpec(IvParameterSpec.class).getIV();
            AES_CIPHER.init(Cipher.DECRYPT_MODE, KEY);
            String[] bytes = cipherText.split(SEPERATOR);
            int length = bytes.length;
            byte[] cipherBytes = new byte[length];
            --length;
            for (int index = 0; length >= 0; --length, ++index) {
                cipherBytes[index] = Byte.parseByte(bytes[length]);
            }
            return new String(AES_CIPHER.doFinal(cipherBytes), UTF_8);
        }
        catch (UnsupportedEncodingException | NumberFormatException | InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            e.printStackTrace();
            return null;
        }
    }

    //test code
    public static void main(String[] args) throws Exception {
        String original = "Will is cool!";
        String encoded = encode(original);
        System.out.println(encoded);
        String decoded = decode(encoded);
        System.out.println(decoded);
    }
}