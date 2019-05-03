package Security;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.NoSuchAlgorithmException;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

/**
 * Class for creating secure 128-bit Advanced Encryption Standard (AES)
 * keys. A 128-bit key was chosen because 
 * all JVMs are required to implement this key size, meaning that such keys will
 * work on any Java platform. The class is for testing purposes and saves the generated 
 * key into a file.
 * @author Will
 */
public class KeyCreator {
    
    private KeyCreator() {
        
    }

    @SuppressWarnings("CallToPrintStackTrace")
    public static void main(String[] args) {
        FileOutputStream fileOutputStream;
        try {
            fileOutputStream = new FileOutputStream("Key.bin");
        }
        catch (FileNotFoundException ex) {
            ex.printStackTrace(); 
            System.exit(1);
            return;
        }
        
        ObjectOutputStream objectOutputStream;
        try {
            objectOutputStream = new ObjectOutputStream(fileOutputStream);
        }
        catch (IOException ex) {
            ex.printStackTrace(); 
            System.exit(1);
            return;
        }

        KeyGenerator keyGenerator;
        try {
            keyGenerator = KeyGenerator.getInstance("AES");
        }
        catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace(); 
            System.exit(1);
            return;
        }
        
        keyGenerator.init(128);

        SecretKey secretKey = keyGenerator.generateKey();
        
        try {
            objectOutputStream.writeObject(secretKey);
        }
        catch (IOException ex) {
            ex.printStackTrace(); 
            System.exit(1);
            return;
        }
        
        try {
            objectOutputStream.close();
        }
        catch (IOException ex) {
            ex.printStackTrace(); 
            System.exit(1);
            return;
        }

        FileInputStream fileInputStream;
        try {
            fileInputStream = new FileInputStream("Key.bin");
        }
        catch (FileNotFoundException ex) {
            ex.printStackTrace(); 
            System.exit(1);
            return;
        }
        
        ObjectInputStream objectInputStream;
        try {
            objectInputStream = new ObjectInputStream(fileInputStream);
        }
        catch (IOException ex) {
            ex.printStackTrace(); 
            System.exit(1);
            return;
        }
        
        SecretKey result;
        try {
            result = (SecretKey) objectInputStream.readObject();
        }
        catch (IOException | ClassNotFoundException ex) {
            ex.printStackTrace(); 
            System.exit(1);
            return;
        }
        
        try {
            objectInputStream.close();
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
        
        System.out.println(secretKey.equals(result));
    }
}