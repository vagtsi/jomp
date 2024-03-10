package de.vagtsi.jomp.views;

import java.io.ByteArrayInputStream;

import org.kordamp.ikonli.fontawesome5.FontAwesomeRegular;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.vagtsi.jomp.model.Song;
import jakarta.inject.Inject;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;

/**
 * Music player view showing controls and actually playing the music.
 */
public class MusicPlayerView implements PlaylistListener {
	private static final Logger log = LoggerFactory.getLogger(MusicPlayerView.class);

	private static final String FONT_FAMILY = "Arial";
	private Label currentTimeLabel = new Label(DurationFormatter.format(0));
	private Label completeTimeLabel = new Label(DurationFormatter.format(0));
	private Label bitrateLabel = new Label();
	private ProgressBar titleProgressBar = new ProgressBar(0);
	private Label titleLabel = new Label();
	private Label artistLabel = new Label();
	private Image noCover = new Image(getClass().getResourceAsStream("/nocover.png"));
	private ImageView coverart = new ImageView(noCover);
	private Button previousButton = new Button(null, FontIcon.of(FontAwesomeSolid.STEP_BACKWARD));
	private Button nextButton = new Button(null, FontIcon.of(FontAwesomeSolid.STEP_FORWARD));
//	private FontIcon favoriteIcon = FontIcon.of(FontAwesomeSolid.HEART, 16); 
	private FontIcon notfavoriteIcon = FontIcon.of(FontAwesomeRegular.HEART, 16); 
	private FontIcon playIcon = FontIcon.of(FontAwesomeSolid.PLAY, 32); 
	private FontIcon pauseIcon = FontIcon.of(FontAwesomeSolid.PAUSE, 32);
	private Button playButton  = new Button(null, playIcon);
	private Button favoriteButton = new Button(null, notfavoriteIcon);
	
	private VBox root;
	private MediaPlayer mediaPlayer;
	private boolean playingSong = false;
	private long titleDuration;
	private ChangeListener<Duration> progressChangeListener;
	private final PlaylistController playlistController;
	
	@Inject
	public MusicPlayerView(PlaylistController playlistController) {
		this.playlistController = playlistController;
		Font smallFont = Font.font(FONT_FAMILY, FontWeight.NORMAL, 12.0);
		currentTimeLabel.setFont(smallFont);
		completeTimeLabel.setFont(smallFont);
		bitrateLabel.setFont(smallFont);
		bitrateLabel.setMinWidth(40.0);
		bitrateLabel.setPrefWidth(40.0);
		titleLabel.setFont(Font.font(FONT_FAMILY, FontWeight.BOLD, 18.0));
		artistLabel.setFont(Font.font(FONT_FAMILY, FontWeight.NORMAL, 18.0));
		
		coverart.setFitHeight(80.0);
		coverart.setFitWidth(80.0);
		coverart.setPreserveRatio(true);
		
		titleProgressBar.setMaxWidth(Double.MAX_VALUE); // necessary to get hgrow work!
		HBox.setHgrow(titleProgressBar, Priority.ALWAYS);
		HBox progressPane = new HBox(10, currentTimeLabel, titleProgressBar, completeTimeLabel);
		progressPane.setAlignment(Pos.CENTER_LEFT);

		VBox titlePane = new VBox(10, titleLabel, artistLabel);
		titlePane.setAlignment(Pos.CENTER_LEFT);
		HBox.setHgrow(titlePane, Priority.ALWAYS);
		
		Label kbpsLabel = new Label("kbps");
		kbpsLabel.setMinWidth(40.0);
		VBox bitratePane = new VBox(0, bitrateLabel, kbpsLabel);
		bitratePane.setAlignment(Pos.BASELINE_CENTER);
//		bitratePane.setBorder(new Border(new BorderStroke(Color.BLACK, 
//	            BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
		HBox controlPane = new HBox(10, coverart, titlePane, previousButton, playButton, nextButton, favoriteButton, bitratePane);
		controlPane.setAlignment(Pos.CENTER_LEFT);
		
		root = new VBox(10, progressPane, controlPane);
		root.setPadding(new Insets(10.0));
		
		// register action handlers
		playButton.setOnAction(event -> onPlayOrPause());
		previousButton.setOnAction(event -> onPreviousSong());
		nextButton.setOnAction(event -> onNextSong());
		
		// initialize with first title from playlist if any
		playlistController.setPlaylistListener(this);
	}
	
	public Pane getView() {
		return root;
	}

	// ~~ button click events ~~ 
	
	private void onPlayOrPause() {
		if (playingSong) {
			playingSong = false;
			mediaPlayer.pause();
			playButton.setGraphic(playIcon);
		} else {
			playingSong = true;
			mediaPlayer.play();
			playButton.setGraphic(pauseIcon);
		}
	}

	private void onPreviousSong() {
		playlistController.selectPrevious();
	}

	private void onNextSong() {
		playlistController.selectNext();
	}

	// ~~ MediaPlayer event handlers ~~ 
	
	private void onUpdateProgress() {
		titleProgressBar.setProgress(1.0 * mediaPlayer.getCurrentTime().toSeconds() / titleDuration);
		currentTimeLabel.setText(DurationFormatter.format((long)mediaPlayer.getCurrentTime().toSeconds()));
	}

	private void onSongPlaybackFinished() {
		log.info("Playback of title {} finished, looking for next one ...", titleLabel.getText());
		playlistController.selectNext();
	}

	// ~~ PlaylistListener implementation ~~
	
	@Override
	public void onSongSelected(Song newSong) {
		resetPlayer();
		loadSong(newSong);
	}

	// ~~ private ~~ 

	private void loadSong(Song song) {
		log.info("Loading song with title {}", song.title());
		Media media = new Media(song.uri().toString());
		mediaPlayer = new MediaPlayer(media);
		mediaPlayer.setOnEndOfMedia(this::onSongPlaybackFinished);
		progressChangeListener = (observableValue, oldValue, newValue) -> onUpdateProgress();
		mediaPlayer.currentTimeProperty().addListener(progressChangeListener);
		
		titleLabel.setText(song.title());
		artistLabel.setText(formatArtist(song));
		currentTimeLabel.setText(DurationFormatter.format(0));
		titleDuration = song.duration();
		completeTimeLabel.setText(DurationFormatter.format(titleDuration));
		bitrateLabel.setText(String.valueOf(song.bitrate()));
		ByteArrayInputStream coverData = new ByteArrayInputStream(song.albumImage());
		coverart.setImage(new Image(coverData));
		
		if (playingSong) {
			mediaPlayer.play();
		}
	}

	private String formatArtist(Song song) {
		if (song.album() == null) {
			return song.artist();
		}
		
		return String.format("%s - %s", song.artist(), song.album());
	}

	private void resetPlayer() {
		if (mediaPlayer != null) {
			mediaPlayer.stop();
			mediaPlayer.dispose();
			mediaPlayer = null;
		}
	}

}
