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
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class Start implements Initializable
{
    @FXML
    private Button openProject;
    @FXML
    private Button newProject;
    @FXML
    private Button exit;
    @FXML
    private HBox parent;

    private final ListView<String> openProjects = new ListView<>();
    private final Map<String, String> existingProjects = new HashMap<>();
    private final List<String> names = new ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        populateWithExistingProjects();
        ObservableList<String> items = FXCollections.observableArrayList(names);
        openProjects.setItems(items);
        AtomicReference<String> selectedItem = new AtomicReference<>();

        openProjects
                .getSelectionModel()
                .selectedItemProperty()
                .addListener((arg0, arg1, arg2) ->
                        selectedItem.set(openProjects.getSelectionModel().getSelectedItem()));

        openProject.setOnAction(event -> {
            VBox vBox = new VBox();
            vBox.getChildren().add(openProjects);
            Button enter = new Button("Confirm");
            enter.setOnAction(event1 ->
            {
                try
                {
                    if (String.valueOf(selectedItem) != null)
                        switchToEditor(event1, String.valueOf(selectedItem));
                }
                catch (IOException e)
                {
                    throw new RuntimeException(e);
                }
            });
            vBox.getChildren().add(enter);
            parent.getChildren().add(vBox);
        });
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
                names.add(parts[0]);
                existingProjects.put(parts[0], parts[1]);

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
        System.out.println(existingProjects.get(selectedItem));
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(Objects.requireNonNull(getClass().getResource("editor.fxml")));
        Parent root = loader.load();
        Editor editorController = loader.getController();
        editorController.setPath(existingProjects.get(selectedItem));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
}