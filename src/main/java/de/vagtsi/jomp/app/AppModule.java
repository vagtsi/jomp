package de.vagtsi.jomp.app;

import com.google.inject.AbstractModule;

import de.vagtsi.jomp.views.AlbumDirectoryChooser;
import de.vagtsi.jomp.views.MainMenu;
import de.vagtsi.jomp.views.MainView;
import de.vagtsi.jomp.views.MusicAlbumView;
import de.vagtsi.jomp.views.MusicPlayerView;
import de.vagtsi.jomp.views.PlaylistController;
import jakarta.inject.Singleton;
import javafx.stage.Stage;

/**
 * Guice module configuration for the application at runtime.
 */
public class AppModule extends AbstractModule {
	
	private final Stage primaryStage;

	public AppModule(Stage stage) {
		this.primaryStage = stage;
	}

	@Override
	protected void configure() {
		bind(Stage.class).toInstance(primaryStage);
		bind(MainView.class).in(Singleton.class);
		bind(MusicAlbumView.class).in(Singleton.class);
		bind(PlaylistController.class).to(MusicAlbumView.class);
		bind(MusicPlayerView.class).in(Singleton.class);
		bind(MainMenu.class).in(Singleton.class);
		bind(AlbumDirectoryChooser.class).in(Singleton.class);
	}
}
