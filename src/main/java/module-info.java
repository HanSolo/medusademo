module eu.hansolo.medusademo {

    // Java
    requires java.base;

    // Java-FX
    requires transitive javafx.base;
    requires transitive javafx.graphics;
    requires transitive javafx.controls;
    requires transitive javafx.swing;
    requires transitive javafx.fxml;

    // 3rd party
    requires transitive eu.hansolo.medusa;
    requires transitive eu.hansolo.colors;
    //requires transitive eu.hansolo.toolbox;
    //requires transitive eu.hansolo.toolboxfx;

    exports eu.hansolo.medusademo.customstyleableskin;
    exports eu.hansolo.medusademo.skin;
    exports eu.hansolo.medusademo;
}