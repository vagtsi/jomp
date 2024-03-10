package de.vagtsi.jomp.views;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mpatric.mp3agic.ID3v1;
import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.Mp3File;

import de.vagtsi.jomp.config.Configuration;
import de.vagtsi.jomp.config.Settings;
import de.vagtsi.jomp.model.Song;
import jakarta.inject.Inject;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

/**
 * List view of one music folder/album.
 */
public class MusicAlbumView implements PlaylistController {
	private static final Logger log = LoggerFactory.getLogger(MusicAlbumView.class);
	private static final String RECENT_ALBUM_FOLDER = "jomp.recent.album.folder";

	private final TableView<Song> tableView;
	private PlaylistListener playlistListener;
	private File currentDirectory;
	
	@SuppressWarnings("unchecked")
	@Inject
	public MusicAlbumView() {
		tableView = new TableView<>();
		TableColumn<Song, String> title = new TableColumn<>("Title");
		TableColumn<Song, String> artist = new TableColumn<>("Artist");
		TableColumn<Song, String> album = new TableColumn<>("Album");
		TableColumn<Song, String> duration = new TableColumn<>("Duration");
		
		tableView.getColumns().addAll(title, artist, album, duration);
		tableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		tableView.getSelectionModel().selectedItemProperty().addListener((ChangeListener<Song>) (observable, oldValue, newValue) -> fireSongSelected(newValue));
		
		title.setCellValueFactory(p -> new SimpleStringProperty(p.getValue().title()));
		artist.setCellValueFactory(p -> new SimpleStringProperty(p.getValue().artist()));
		album.setCellValueFactory(p -> new SimpleStringProperty(p.getValue().album()));
		duration.setCellValueFactory(p -> new SimpleStringProperty(DurationFormatter.format(p.getValue().duration())));
		
		initializeRecentFolder();
	}
	
	/**
	 * Initialize album view with recently played folder
	 * Note: does support MP3 file (metadata) only for now!
	 */
	private boolean initializeRecentFolder() {
		String recentFolder = Configuration.getString(RECENT_ALBUM_FOLDER);
		if (recentFolder == null || !Files.exists(Path.of(recentFolder))) {
			log.info("No recent folder found in config or does not exist: {}", recentFolder);
			return false;
		}
		
		loadDirectory(new File(recentFolder));
		return true;
	}

	// TODO: refactor reading metadata into dedicated provider implementation
	private Song readSongFromMP3File(Path file) {
		try {
			Mp3File mp3file = new Mp3File(file);
			Song song = null;
			
			if (mp3file.hasId3v2Tag()) {
				ID3v2 tag = mp3file.getId3v2Tag();
				song = new Song(
						tag.getTitle(),
						tag.getArtist(),
						tag.getAlbum(),
						mp3file.getLengthInSeconds(),
						mp3file.getBitrate(),
						file.toUri(),
						tag.getAlbumImage()
						);
			} else if (mp3file.hasId3v1Tag()) {
				ID3v1 tag = mp3file.getId3v1Tag();
				song = new Song(
						tag.getTitle(),
						tag.getArtist(),
						tag.getAlbum(),
						mp3file.getLengthInSeconds(),
						mp3file.getBitrate(),
						file.toUri(),
						null
						);
			}
			
			return song;
		} catch (Exception e) {
			log.error("Invalid metadata in {}", file, e);
			return null;
		}
	}
	
	private void fireSongSelected(Song newValue) {
		if (playlistListener != null) {
			playlistListener.onSongSelected(newValue);
		}
	}

	public TableView<Song> getTableView() {
		return tableView;
	}

	// ~~ PlaylistController implementation ~~
	
	@Override
	public void selectFirst() {
		tableView.getSelectionModel().select(0);
	}

	@Override
	public void selectNext() {
		int selectedIndex = tableView.getSelectionModel().getSelectedIndex();
		log.info("Selecting next song after current index {} (total ={})",
				selectedIndex, tableView.getItems().size());
		tableView.getSelectionModel().select(selectedIndex  + 1);
	}

	@Override
	public void selectPrevious() {
		int selectedIndex = tableView.getSelectionModel().getSelectedIndex();
		log.info("Selecting previous song before current index {}", selectedIndex);
		tableView.getSelectionModel().select(selectedIndex  - 1);
	}

	@Override
	public void setPlaylistListener(PlaylistListener listener) {
		this.playlistListener = listener;
		fireSongSelected(tableView.getSelectionModel().getSelectedItem());
	}

	@Override
	public File getCurrentDirectory() {
		return currentDirectory;
	}

	@Override
	public void loadDirectory(File directory) {
		try (Stream<Path> files = Files.list(Path.of(directory.toURI()))) {
			List<Song> songList = files.sorted()
					.filter(f -> f.toString().endsWith(".mp3"))
					.map(this::readSongFromMP3File)
					.filter(Objects::nonNull)
					.toList();
			if (songList.isEmpty()) {
				log.info("No music file in recent folder {}", directory);
				return;
			}
			
			tableView.setItems(FXCollections.observableList(songList));
			currentDirectory = directory;
			Settings.setString(RECENT_ALBUM_FOLDER, currentDirectory.toString());
			selectFirst();
		} catch (Exception e) {
			log.error("Failed to load playlist from directory {}", directory, e);
		}
	}
	
}
