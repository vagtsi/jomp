package de.vagtsi.jomp.model;

import java.net.URI;
import java.util.Objects;

/**
 * Complete information of one particular music song.
 */
public record Song(String title, String artist, String album, long duration, int bitrate, URI uri, byte[] albumImage) {
	
	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Song other = (Song) o;
		return title.equals(other.title) && artist.equals(other.artist) && duration == other.duration;
	}

	@Override
	public int hashCode() {
		return Objects.hash(title, artist, album, duration);
	}

	@Override
	public String toString() {
		return "Song{" + "title=" + title + ", arist=" + artist + ", album=" + album + '}';
	}
}
