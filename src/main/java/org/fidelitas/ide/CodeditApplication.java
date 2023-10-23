package org.fidelitas.ide;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class CodeditApplication extends Application
{
    public static String projectPath = "/home/admindavid/IdeaProjects/ide";

    public static void main(String[] args)
    {
        launch(args);
    }
    private final String fontFamily = "Arial";
    private double fontSize = 12.0;
    private final FontWeight fontWeight = FontWeight.NORMAL;
    @Override
    public void start(Stage stage) throws IOException
    {
        stage.setTitle("Codedit");
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("hello-view.fxml")));
        Scene scene = new Scene(root, 1200, 750);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/stylesheet.css")).toExternalForm());
        stage.setScene(scene);

        stage.show();
    }
}
