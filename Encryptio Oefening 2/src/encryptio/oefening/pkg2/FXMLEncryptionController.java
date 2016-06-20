/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package encryptio.oefening.pkg2;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SealedObject;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

public class FXMLEncryptionController implements Initializable {

    @FXML
    private PasswordField tfPassword;
    @FXML
    private TextField tfLoginname;
    @FXML
    private CheckBox checkBoxEncrypt;
    @FXML
    private CheckBox checkBoxDecrypt;
    @FXML
    private Button btnAction;
    @FXML
    private TextArea taMessage;
    @FXML
    private Button btnLogin;

    private String passwordInput;
    
    private File file;

    private Cipher cipherEncrypt;
    private Cipher cipherDecrypt;

    private ObjectOutputStream out;
    private ObjectInputStream in;

    private FileOutputStream fos;
    private FileInputStream fis;

    SecureRandom randomSalt = new SecureRandom();
    byte[] saltBytes = new byte[8];

    int count = 20;

    public FXMLEncryptionController() throws IOException {
        randomSalt.nextBytes(saltBytes);
        try {
            this.cipherEncrypt = Cipher.getInstance("PBEWithMD5AndDES");
            this.cipherDecrypt = Cipher.getInstance("PBEWithMD5AndDES");
            Path pathOutput = Paths.get("OUTPUT.txt");
            this.file = pathOutput.toFile();

            fos = new FileOutputStream(file);
            out = new ObjectOutputStream(fos);
            fis = new FileInputStream(file);
            in = new ObjectInputStream(fis);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException ex) {
            Logger.getLogger(FXMLEncryptionController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        this.disableFunctions();

        disableFunctions();

    }

    @FXML
    public void loginAction(ActionEvent event) {
        if (login(tfLoginname.getText(), tfPassword.getText())) {
            this.enableFunctions();
            passwordInput = tfPassword.getText();

        }
    }

    @FXML
    public void cryptAction(ActionEvent event) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidAlgorithmParameterException {
        String message = this.taMessage.getText();
        if (checkBoxEncrypt.isSelected()) {
            this.encrypt(message);
        } else if (checkBoxDecrypt.isSelected()) {
            this.decrypt();
        } else {
            throw new IllegalArgumentException("Error");
        }
    }

    private void encrypt(String message) throws NoSuchAlgorithmException, InvalidKeySpecException, InvalidAlgorithmParameterException {
        PBEParameterSpec pbeParameterSpec = new PBEParameterSpec(saltBytes, count);
        PBEKeySpec pbeKeySpec = new PBEKeySpec(passwordInput.toCharArray());
        SecretKeyFactory keyFac = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
        SecretKey pbeKey = keyFac.generateSecret(pbeKeySpec);

        try {

            this.cipherEncrypt.init(Cipher.ENCRYPT_MODE, pbeKey, pbeParameterSpec);
            
            this.writeToFile(message);
        } catch (InvalidKeyException ex) {
            Logger.getLogger(FXMLEncryptionController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void writeToFile(Object message) {
        try {
            SealedObject so = new SealedObject((Serializable) message, cipherEncrypt);
            out.writeObject(so);
        } catch (IOException | IllegalBlockSizeException ex) {
            Logger.getLogger(FXMLEncryptionController.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                out.close();
            } catch (IOException ex) {
                Logger.getLogger(FXMLEncryptionController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private String readFromFile() {
        try {
            SealedObject so = (SealedObject) in.readObject();
            String textEncrypted = (String) so.getObject(cipherDecrypt);
            return textEncrypted;
        } catch (IOException | ClassNotFoundException | IllegalBlockSizeException | BadPaddingException ex) {
            Logger.getLogger(FXMLEncryptionController.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private void decrypt() throws InvalidKeySpecException, NoSuchAlgorithmException {

        PBEParameterSpec pbeParameterSpec = new PBEParameterSpec(saltBytes, count);
        PBEKeySpec pbeKeySpec = new PBEKeySpec(passwordInput.toCharArray());
        SecretKeyFactory keyFac = SecretKeyFactory.getInstance("PBEWithMD5AndDES");
        SecretKey pbeKey = keyFac.generateSecret(pbeKeySpec);
        try {
            cipherDecrypt.init(Cipher.DECRYPT_MODE, pbeKey, pbeParameterSpec);
            String textDecrypted = this.readFromFile();
        } catch (InvalidKeyException | InvalidAlgorithmParameterException ex) {
            Logger.getLogger(FXMLEncryptionController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void clearPassword(String pass) {
        char[] password = pass.toCharArray();
        for (int i = password.length; i > 0; i++) {
            password[i] = 0;
        }
        tfPassword.setText(Arrays.toString(password));
    }

    private void disableFunctions() {
        btnAction.setDisable(true);
        taMessage.setDisable(true);
        checkBoxDecrypt.setDisable(true);
        checkBoxEncrypt.setDisable(true);
    }

    private void enableFunctions() {
        btnAction.setDisable(false);
        taMessage.setDisable(false);
        checkBoxDecrypt.setDisable(false);
        checkBoxEncrypt.setDisable(false);
    }

    private boolean login(String loginname, String password) {
        if (loginname.equals("user") && password.equals("123")) {
            return true;
        }
        return false;
    }
}
