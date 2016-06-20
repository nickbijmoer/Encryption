/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javaapplication10;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.security.PublicKey;
import java.security.Signature;

public class Encryption3 {

    public static final String PRIVATE_KEY_FILE = "/Users/nickbijmoer/Documents/Encryption/keys/private.key";
    public static final String PUBLIC_KEY_FILE = "/Users/nickbijmoer/Documents/Encryption/keys/public.key";
    public static final String INPUT_FILE = "/Users/nickbijmoer/Documents/Encryption/keys/input.txt";
    public static final String NAME_OF_SIGNER = "NickEnBart";
    public static final String ENCRYPTED_FILE = "/Users/nickbijmoer/Documents/Encryption/keys/encrypted(SIGNED BY "+ NAME_OF_SIGNER +").ext";
    
    
    public static void main(String[] args)
    {
      
        ObjectInputStream objectInputStream = null;
        
        try{
            objectInputStream = new ObjectInputStream(new FileInputStream(PUBLIC_KEY_FILE));
            PublicKey publicKey = (PublicKey) objectInputStream.readObject();
            
            objectInputStream = new ObjectInputStream(new FileInputStream(ENCRYPTED_FILE));
            int sizeOfSignature = objectInputStream.readInt();
            byte[] signatureBytes = (byte[]) objectInputStream.readObject();
            String text = (String) objectInputStream.readObject();
             
            System.out.println("Size: " + sizeOfSignature);
            System.out.println("Text: " + text);
            Signature signature = Signature.getInstance("SHA1WithRSA");
            signature.initVerify(publicKey);
            boolean verifies = signature.verify(signatureBytes);
            System.out.println("Verified: " + verifies);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
    
}