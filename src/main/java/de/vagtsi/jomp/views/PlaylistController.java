package de.vagtsi.jomp.views;

import java.io.File;

/**
 * Interface for controlling list of songs (e.g. album or favorites).
 */
public interface PlaylistController {

	/**
	 * Select the first song of this playlist
	 */
	void selectFirst();

	/**
	 * select the next song in the playlist (if any)
	 */
	void selectNext();

	/**
	 * select the previous song in the playlist (if any)
	 */
	void selectPrevious();

	/**
	 * Apply the given listener to get notified of playlist selection changes 
	 * @param listener the new list7ener to be applied
	 */
	void setPlaylistListener(PlaylistListener listener);

	/**
	 * The currently shown/played directory of this playlist
	 * @return the source directory of the playlist
	 */
	File getCurrentDirectory();

	/**
	 * Load/create playlist from given directory
	 * @param directory the new source directory for the playlist 
	 */
	void loadDirectory(File directory);
}
