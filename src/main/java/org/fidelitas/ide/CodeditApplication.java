package org.fidelitas.ide;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

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
        //scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("stylesheet.css")).toExternalForm());
        stage.setScene(scene);
        stage.initStyle(StageStyle.UTILITY);
        stage.show();
    }
    public void exitDialog()
    {
        DialogPane exitQuestion = new DialogPane();
        Label label = new Label("Do you really want to exit?");
        exitQuestion.setContent(label);

        Dialog<String> dialog = new Dialog<>();
        dialog.setDialogPane(exitQuestion);
        dialog.initStyle(StageStyle.UTILITY);

        ButtonType exitButtonType = new ButtonType("Confirm Exit", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        exitQuestion.getButtonTypes().addAll(exitButtonType, cancelButtonType);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == exitButtonType) {
                Platform.exit();
                System.exit(0);
            }
            return null;
        });
        dialog.showAndWait();
    }
}