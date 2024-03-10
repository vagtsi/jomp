package de.vagtsi.jomp.views;

import jakarta.inject.Inject;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;

/**
 * The main menu displayed on the top of the main window/OS
 */
public class MainMenu {
	private final MenuBar menuBar;

	@Inject
	public MainMenu(AlbumDirectoryChooser directoryChooser) {
		Menu fileMenu = new Menu("File");
		MenuItem loadFolder = new MenuItem("Load album from directory");
		loadFolder.setOnAction(event -> directoryChooser.selectAndLoadAlbumDirectory());
		
		fileMenu.getItems().add(loadFolder);

		menuBar = new MenuBar();
		menuBar.getMenus().addAll(fileMenu);
		menuBar.setUseSystemMenuBar(true);
	}
	
	public MenuBar getMenuBar() {
		return menuBar;
	}

}
