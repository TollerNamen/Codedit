package org.fidelitas.ide.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TreeView;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.BorderPane;

import org.fidelitas.ide.CodeditApplication;
import org.fidelitas.ide.ProjectConfiguration;
import org.fidelitas.ide.syntaxhighlighting.Java;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.util.*;
import java.util.stream.Stream;

public class Editor extends CodeditApplication implements Initializable
{
    @FXML
    private BorderPane rootPane;
    @FXML
    private TreeView<String> treeView;
    @FXML
    private TabPane tabPane;
    private final Map<TreeItem<String>, File> fileReference = new HashMap<>();
    //private final CodeArea codeArea = new CodeArea();
    private final Map<File, String> buffer = new HashMap<>();
    public String path;
    private ProjectConfiguration projectConfiguration;
    public Editor()
    {
    }
    public void setPath(String path)
    {
        this.path = path;
    }
    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        //codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));
    }
    public void initializeProject()
    {
        projectConfiguration = new ProjectConfiguration(path);
        projectConfiguration.setLanguageAndBuildSystem("java", "gradle");
        projectConfiguration.configureProject();

        File file = new File(path);
        TreeItem<String> rootTreeItem = createTreeView(file.getName(), file);
        treeView.setRoot(rootTreeItem);
    }
    private TreeItem<String> createTreeView(String name, File path)
    {
        File[] subDirs = path.listFiles();
        assert subDirs != null;
        TreeItem<String> treeItem;

        if (subDirs.length == 1 && subDirs[0].isDirectory())
            return createTreeView(name + "." + subDirs[0].getName(), subDirs[0]);

        treeItem = new TreeItem<>(name);
        fileReference.put(treeItem, path);

        File[] directories = Arrays.stream(subDirs)
                .filter(File::isDirectory)
                .toArray(File[]::new);
        Arrays.sort(directories);

        File[] files = Arrays.stream(subDirs)
                .filter(file -> !file.isDirectory())
                .toArray(File[]::new);
        Arrays.sort(files);

        Stream<File> directoriesStream = Arrays.stream(directories);
        Stream<File> filesStream = Arrays.stream(files);

        File[] mergedArray = Stream.concat(directoriesStream, filesStream).toArray(File[]::new);

        for (File subDir: mergedArray)
        {
            if (subDir.isDirectory())
                treeItem.getChildren().add(createTreeView(subDir.getName(), subDir));
            else
            {
                TreeItem<String> children = new TreeItem<>(subDir.getName());
                treeItem.getChildren().add(children);
                fileReference.put(children, subDir);
            }
        }
        return treeItem;
    }
    private File lastSelectedFile;
    public void onTreeItemMouseClicked()
    {
        try
        {
            TreeItem<String> selectedItem = treeView.getSelectionModel().getSelectedItem();
            if (selectedItem == null)
                return;

            File file = fileReference.get(selectedItem);
            if (file.isDirectory())
                return;

            String content = Files.readString(file.toPath());

            buffer.putIfAbsent(file, content);

            CodeArea codeArea = new CodeArea();

            Java.syntax(tabPane, file.getName(), codeArea, content);

            //tabPane.getTabs().add(new Tab(file.getName(), codeArea));
            /*
            codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));

            if (lastSelectedFile != null)
                buffer.put(lastSelectedFile, codeArea.getText());
            lastSelectedFile = file;
            codeArea.clear();
            //codeArea.appendText(buffer.get(lastSelectedFile));
            codeArea.appendText(content);
            codeArea.setStyleClass(0, content.length(), ".code-area");
             */
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    public void onEditStart()
    {
    }
    public void onSaveAllAction()
    {
        System.out.println(buffer);
        writeFiles(buffer);
    }
    public void onExitAction()
    {
        exitDialog();
    }
    public static void writeFiles(Map<File, String> buffer)
    {
        buffer.forEach((file, s) -> {
            if (file.canWrite())
            {
                try
                {
                    Files.writeString(file.toPath(), s);
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }
        });
    }
    public void run()
    {
        try
        {
            projectConfiguration.executeRunTask();
        }
        catch (IOException | InterruptedException e)
        {
            throw new RuntimeException(e);
        }
    }
    public void build()
    {
        try
        {
            projectConfiguration.executeBuildTask();
        }
        catch (IOException | InterruptedException e)
        {
            throw new RuntimeException(e);
        }
    }
}