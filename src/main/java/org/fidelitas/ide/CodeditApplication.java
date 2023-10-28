package org.fidelitas.ide;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class CodeditApplication extends Application
{
    public static void main(String[] args)
    {
        launch(args);
    }
    @Override
    public void start(Stage stage) throws IOException
    {
        stage.setTitle("Codedit Start");
        Parent startRoot = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("start.fxml")));
        //Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("editor.fxml")));
        Scene scene = new Scene(startRoot);
        //scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/stylesheet.css")).toExternalForm());
        stage.setScene(scene);
        stage.show();
    }
}
