package de.vagtsi.jomp.views;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.net.URISyntaxException;

import org.junit.jupiter.api.Test;

/**
 * Unit test for {@link AlbumDirectoryChooser}. 
 */
class AlbumDirectoryChooserTest {
	
	@Test
	void folderContainsMusic_mp3_only() throws URISyntaxException {
		File testMusicDirectory = new File(AlbumDirectoryChooserTest.class.getResource("/folders/mp3only").toURI());
		assertThat(AlbumDirectoryChooser.folderContainsMusic(testMusicDirectory)).isTrue();
	}

	@Test
	void folderContainsMusic_with_mp3_and_others() throws URISyntaxException {
		File testMusicDirectory = new File(AlbumDirectoryChooserTest.class.getResource("/folders/withmp3").toURI());
		assertThat(AlbumDirectoryChooser.folderContainsMusic(testMusicDirectory)).isTrue();
	}

	@Test
	void folderContainsMusic_flac_only() throws URISyntaxException {
		File testMusicDirectory = new File(AlbumDirectoryChooserTest.class.getResource("/folders/flaconly").toURI());
		assertThat(AlbumDirectoryChooser.folderContainsMusic(testMusicDirectory)).isFalse();
	}

	@Test
	void folderContainsMusic_no_music() throws URISyntaxException {
		File testMusicDirectory = new File(AlbumDirectoryChooserTest.class.getResource("/folders/nomusic").toURI());
		assertThat(AlbumDirectoryChooser.folderContainsMusic(testMusicDirectory)).isFalse();
	}
}
