package de.vagtsi.jomp.views;

import jakarta.inject.Inject;
import javafx.scene.layout.BorderPane;

/**
 * The application main view/window containing all views and the main menu.
 */
public class MainView {

	private final BorderPane pane;

	@Inject
	public MainView(MainMenu mainMenu, MusicAlbumView albumView, MusicPlayerView playerView) {
        pane = new BorderPane();
        pane.setTop(mainMenu.getMenuBar());
        pane.setCenter(albumView.getTableView());
        pane.setBottom(playerView.getView());
	}

	public BorderPane getPane() {
		return pane;
	}
}
