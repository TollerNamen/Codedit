module org.fidelitas.ide {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.ikonli.javafx;
    requires org.fxmisc.richtext;
    requires reactfx;
    requires org.fxmisc.flowless;

    opens org.fidelitas.ide to javafx.fxml;
    exports org.fidelitas.ide;
}