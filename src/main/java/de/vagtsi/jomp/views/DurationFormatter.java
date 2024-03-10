package de.vagtsi.jomp.views;

public class DurationFormatter {

	public static String format(long seconds) {
		return String.format("%02d:%02d", seconds / 60, seconds % 60); 
	}

	private DurationFormatter() {
		// utility class
	}
}
