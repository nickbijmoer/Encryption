/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package encryptio.oefening.pkg2;

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class EncryptioOefening2 extends Application {

    public static Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {

        primaryStage = stage;
        Parent root = FXMLLoader.load(getClass().getResource("FXMLEncryption.fxml"));
        Scene scene = new Scene(root);
        primaryStage.setTitle("Encryption");
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.resizableProperty().set(false);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
