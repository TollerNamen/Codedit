package org.fidelitas.ide;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeView;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.BorderPane;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Stream;

public class Controller implements Initializable
{
    private static final String[] KEYWORDS = new String[] {
            "abstract", "assert", "boolean", "break", "byte",
            "case", "catch", "char", "class", "const",
            "continue", "default", "do", "double", "else",
            "enum", "extends", "final", "finally", "float",
            "for", "goto", "if", "implements", "import",
            "instanceof", "int", "interface", "long", "native",
            "new", "package", "private", "protected", "public",
            "return", "short", "static", "strictfp", "super",
            "switch", "synchronized", "this", "throw", "throws",
            "transient", "try", "void", "volatile", "while"
    };
    /*
    private static final String KEYWORD_PATTERN = "\\b(" + String.join("|", KEYWORDS) + ")\\b";
    private static final String PAREN_PATTERN = "\\(|\\)";
    private static final String BRACE_PATTERN = "\\{|\\}";
    private static final String BRACKET_PATTERN = "\\[|\\]";
    private static final String SEMICOLON_PATTERN = ";";
    private static final String STRING_PATTERN = "\"([^\"\\\\]|\\\\.)*\"";
    private static final String COMMENT_PATTERN = "//.*|/\\*(.|\\R)*?\\*\/";
    private static final String NUMBER_PATTERN = "\\b\\d+(\\.\\d+)?\\b";
    private static final String OPERATOR_PATTERN = "\\+|-|\\*|/|%|=|==|!=|>|<|>=|<=|&&|\\|\\|";
    private static final String VARIABLE_PATTERN = "\\b[a-zA-Z_$][a-zA-Z\\d_$]*\\b";
    private static final Pattern PATTERN = Pattern.compile(
            "(?<KEYWORD>" + KEYWORD_PATTERN + ")"
                    + "|(?<PAREN>" + PAREN_PATTERN + ")"
                    + "|(?<BRACE>" + BRACE_PATTERN + ")"
                    + "|(?<BRACKET>" + BRACKET_PATTERN + ")"
                    + "|(?<SEMICOLON>" + SEMICOLON_PATTERN + ")"
                    + "|(?<STRING>" + STRING_PATTERN + ")"
                    + "|(?<COMMENT>" + COMMENT_PATTERN + ")"
                    + "|(?<NUMBER>" + NUMBER_PATTERN + ")"
                    + "|(?<OPERATOR>" + OPERATOR_PATTERN + ")"
                    + "|(?<VARIABLE>" + VARIABLE_PATTERN + ")"
    );
    */

    @FXML
    private BorderPane rootPane;

    @FXML
    private TreeView<String> treeView;

    private Map<TreeItem<String>, File> fileReference = new HashMap<>();
    private CodeArea codeArea = new CodeArea();

    public void initialize(URL location, ResourceBundle resources)
    {
        System.out.println("starting init");

        codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));
        rootPane.setCenter(codeArea);

        File file = new File(CodeditApplication.projectPath);
        TreeItem<String> rootTreeItem = createTreeView(file.getName(), file);
        treeView.setRoot(rootTreeItem);
    }
    private TreeItem<String> createTreeView(String name, File path)
    {
        System.out.println("executing");
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
    public void onEditStart(Event event)
    {
        try
        {
            System.out.println(fileReference);
            TreeItem<String> selectedItem = treeView.getSelectionModel().getSelectedItem();
            File file = fileReference.get(selectedItem);
            String content = Files.readString(file.toPath());
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
    /*
    public StyleSpans<Collection<String>> highlight(String code)
    {
        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();
        Matcher matcher = PATTERN.matcher(code);
        int lastKwEnd = 0;
        while (matcher.find())
        {
            String styleClass =
                    matcher.group("KEYWORD") != null ? "keyword" :
                            matcher.group("PAREN") != null ? "paren" :
                                    matcher.group("BRACE") != null ? "brace" :
                                            matcher.group("BRACKET") != null ? "bracket" :
                                                    matcher.group("SEMICOLON") != null ? "semicolon" :
                                                            matcher.group("STRING") != null ? "string" :
                                                                    matcher.group("COMMENT") != null ? "comment" :
                                                                            null; /* never happens *\/
            assert styleClass != null;
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.emptyList(), code.length() - lastKwEnd);
        return spansBuilder.create();
    }
    */

}