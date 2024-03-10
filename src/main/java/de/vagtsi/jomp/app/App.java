package de.vagtsi.jomp.app;

import java.io.IOException;
import java.lang.management.ManagementFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;

import de.vagtsi.jomp.config.ApplicationVersion;
import de.vagtsi.jomp.config.Settings;
import de.vagtsi.jomp.views.MainView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * the jOMP Application entry point
 */
public class App extends Application {
	private static final Logger log = LoggerFactory.getLogger(App.class);
	private static final String APP_NAME = "jOMP";

    @Override
    public void start(Stage stage) throws IOException {
    	long uptime = ManagementFactory.getRuntimeMXBean().getUptime();
    	if (log.isInfoEnabled()) {
    		log.info("{} version {} started within {} seconds in directory {}",
    				APP_NAME, ApplicationVersion.getFullVersion(), uptime / 1000.0, System.getProperty("user.dir"));
    	}
        
    	long start = System.currentTimeMillis();
        Injector injector = Guice.createInjector(new AppModule(stage));
        log.info("Initialized guice within {} ms", System.currentTimeMillis() - start);
        BorderPane mainView = injector.getInstance(MainView.class).getPane();
        
        Scene scene = new Scene(mainView, 700, 500);
        stage.setScene(scene);
        stage.setTitle(APP_NAME);
        stage.show();
    }
    
    @Override
    public void stop() throws Exception {
    	log.info("Shutting down application.");
    	Settings.saveSettings();
    	super.stop();
    }

    public static void main(String[] args) {
        launch();
    }

}