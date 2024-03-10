module de.vagtsi.jomp {
	opens de.vagtsi.jomp.views;
    requires transitive javafx.controls;
	requires org.kordamp.ikonli.javafx;
	requires org.kordamp.ikonli.fontawesome5;
	requires java.base;
	requires javafx.media;
	requires mp3agic;
	requires org.slf4j;
	requires java.management;
	requires com.google.guice;
	requires jakarta.inject;
    exports de.vagtsi.jomp.app;
}