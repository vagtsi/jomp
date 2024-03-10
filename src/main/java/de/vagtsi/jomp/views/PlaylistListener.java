package de.vagtsi.jomp.views;

import de.vagtsi.jomp.model.Song;

public interface PlaylistListener {

	void onSongSelected(Song newSong);
}
