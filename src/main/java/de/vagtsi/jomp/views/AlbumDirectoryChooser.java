package de.vagtsi.jomp.views;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.inject.Inject;
import javafx.scene.control.Alert;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

/**
 * Directory chooser for local folders containing music files.
 */
public class AlbumDirectoryChooser {
	private static final Logger log = LoggerFactory.getLogger(AlbumDirectoryChooser.class);
	private final PlaylistController playlistController;
	private final Stage stage;

	@Inject
	public AlbumDirectoryChooser(Stage stage, PlaylistController playlistController) {
		this.stage = stage;
		this.playlistController = playlistController;
	}

	public void selectAndLoadAlbumDirectory() {
		Optional<File> directory = selectAlbumDirectory();
		if (directory.isPresent()) {
			playlistController.loadDirectory(directory.get());
		}
	}

	public Optional<File> selectAlbumDirectory() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select a folder containing some music");
        directoryChooser.setInitialDirectory(playlistController.getCurrentDirectory());
        
        File selectedDirectory = null;
        boolean musicDirectorySelected = false;
		while (!musicDirectorySelected) {
	        selectedDirectory = directoryChooser.showDialog(stage);
	        if (selectedDirectory == null) {
	        	// canceled by user without selecting a directory
		        return Optional.empty();
		    }

	        musicDirectorySelected = folderContainsMusic(selectedDirectory);
	        if (!musicDirectorySelected) {
		        Alert alert = new Alert(Alert.AlertType.WARNING);
		        alert.setTitle("The selected directory does not contain music");
		        alert.setHeaderText("Please select a directory containing at least one file of type 'mp3'");
		        alert.showAndWait();
		        directoryChooser.setInitialDirectory(selectedDirectory);
	        }
		}
		
		return Optional.of(selectedDirectory);
	}

	// ~~ private ~~
	
	static boolean folderContainsMusic(File selectedDirectory) {
		if (selectedDirectory == null) {
			return false;
		}

		try (Stream<Path> files = Files.list(selectedDirectory.toPath())) {
			return files.anyMatch(f -> f.getFileName().toString().endsWith(".mp3"));
		} catch (Exception e) {
			log.error("Failed to read directory {}", selectedDirectory, e);
			return false;
		}
	}
}
