package org.fidelitas.ide.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import org.fidelitas.ide.CodeditApplication;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class Start extends CodeditApplication implements Initializable
{
    @FXML
    private Button openProjectButton;
    @FXML
    private Button newProject;
    @FXML
    private Button exit;
    @FXML
    private HBox parent;
    private final ListView<String> openProjectsListView = new ListView<>();
    private final Map<String, String> existingProjectsMap = new HashMap<>();
    private final List<String> namesList = new ArrayList<>();
    private Label notificationLabel;
    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        populateWithExistingProjects();
        ObservableList<String> items = FXCollections.observableArrayList(namesList);
        openProjectsListView.setItems(items);
        AtomicReference<String> selectedItem = new AtomicReference<>();

        openProjectsListView
                .getSelectionModel()
                .selectedItemProperty()
                .addListener((arg0, arg1, arg2) ->
                        selectedItem.set(openProjectsListView.getSelectionModel().getSelectedItem()));

        openProjectButton.setOnAction(event -> {
            VBox vBox = new VBox();
            vBox.getChildren().add(openProjectsListView);
            Button enter = new Button("Confirm");
            enter.setOnAction(event1 ->
            {
                try
                {
                    if (!Objects.equals(String.valueOf(selectedItem), "null"))
                        switchToEditor(event1, String.valueOf(selectedItem));
                    else
                    {
                        notificationLabel.setStyle("-fx-text-fill: #ff0000");
                        notificationLabel.setText("You have to select a project!");
                    }
                }
                catch (IOException e)
                {
                    throw new RuntimeException(e);
                }
            });
            notificationLabel = new Label();
            vBox.getChildren().add(enter);
            vBox.getChildren().add(notificationLabel);
            vBox.setMaxWidth(200);
            parent.getChildren().add(vBox);
        });
        exit.setOnAction(event -> exitDialog());
    }
    public void populateWithExistingProjects()
    {
        try
        {
            BufferedReader reader = new BufferedReader(new FileReader("codeditprojects.txt"));
            String line = reader.readLine();
            while (line != null)
            {
                String[] parts = line.split("=");
                namesList.add(parts[0]);
                existingProjectsMap.put(parts[0], parts[1]);

                line = reader.readLine();
            }
            reader.close();
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }
    public void switchToEditor(ActionEvent event, String selectedItem) throws IOException
    {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Objects.requireNonNull(getClass().getResource("editor.fxml")));
        Parent root = loader.load();
        Editor editorController = loader.getController();
        editorController.path = existingProjectsMap.get(selectedItem);
        editorController.initializeProject();
        Stage oldStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        Stage newStage = new Stage();
        newStage.setScene(scene);
        oldStage.close();
        newStage.show();
    }
}