/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javaapplication10;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.*;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Encryption2 {

    public static final String PRIVATE_KEY_FILE = "/Users/nickbijmoer/Documents/Encryption/keys/private.key";
    public static final String PUBLIC_KEY_FILE = "/Users/nickbijmoer/Documents/Encryption/keys/public.key";
    public static final String INPUT_FILE = "/Users/nickbijmoer/Documents/Encryption/keys/input.txt";
    public static final String NAME_OF_SIGNER = "NickEnBart";
    public static final String ENCRYPTED_FILE = "/Users/nickbijmoer/Documents/Encryption/keys/encrypted(SIGNED BY "+ NAME_OF_SIGNER +").ext";
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
       
        ObjectInputStream objectInputStream = null;
        InputStream inputStream = null;
        
        try
        {
            objectInputStream = new ObjectInputStream(new FileInputStream(PRIVATE_KEY_FILE));
            PrivateKey privateKey = (PrivateKey) objectInputStream.readObject();
            
            inputStream = new FileInputStream(INPUT_FILE);
            FileReader fr = new FileReader(INPUT_FILE);
            BufferedReader reader = new BufferedReader(fr);
            String text = reader.readLine();
            reader.close();
            fr.close();
            encrypt(text, privateKey);
        }
        catch (Exception ex)
        {
            Logger.getLogger(Encryption2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static void encrypt(String text, PrivateKey key) {
        try
        {
            Signature signature = Signature.getInstance("SHA1WithRSA");
            signature.initSign(key);
            byte[] signatureBytes = signature.sign();
            File outputFile = new File(ENCRYPTED_FILE);
            if(outputFile.getParentFile() != null) {
                outputFile.getParentFile().mkdirs();
            }
            outputFile.createNewFile();
            
            ObjectOutputStream outputFileOS = new ObjectOutputStream(new FileOutputStream(outputFile));
            outputFileOS.writeInt(signatureBytes.length);
            outputFileOS.writeObject(signatureBytes);
            outputFileOS.writeObject(text);
            outputFileOS.close();
            
            System.out.println("Grootte van signature: " + signatureBytes.length + ", text: " + text);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
    
}