package org.fidelitas.ide.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeView;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.BorderPane;

import org.fidelitas.ide.CodeditApplication;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Stream;

public class Editor extends CodeditApplication implements Initializable
{
    @FXML
    private BorderPane rootPane;

    @FXML
    private TreeView<String> treeView;

    private final Map<TreeItem<String>, File> fileReference = new HashMap<>();
    private final CodeArea codeArea = new CodeArea();
    private final Map<File, String> buffer = new HashMap<>();
    private String path = "/home/admindavid/IdeaProjects/example";
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
        codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));
        rootPane.setCenter(codeArea);

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
    public void onMouseClicked()
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

            if (lastSelectedFile != null)
                buffer.put(lastSelectedFile, codeArea.getText());
            lastSelectedFile = file;
            codeArea.clear();
            codeArea.appendText(buffer.get(lastSelectedFile));
            /*
            Matcher matcher = PATTERN.matcher(content);
            if (matcher.matches())
            {
                StyleSpans<Collection<String>> styleSpans = highlight(content);
                codeArea.setStyleSpans(0, styleSpans);
            }
            else
            {
                System.out.println("The code does not match the pattern.");
            }
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
}