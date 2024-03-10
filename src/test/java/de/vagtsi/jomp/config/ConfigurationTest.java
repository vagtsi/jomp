package de.vagtsi.jomp.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/**
 * Unit test pf {@link Configuration}.
 */
class ConfigurationTest {

	@Test
	void readConfig() {
		assertThat(Configuration.getString("jomp.recent.album.folder")).isNotEmpty();
	}

}
