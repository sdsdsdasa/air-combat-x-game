import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.io.File;
import javafx.application.Platform;

/**
 * This class provides mediaPlayer to bgm and sound effects.
 * 
 * @author Junhao Zhou
 * @version 2.0 (25-03-2025)
 */
public class SoundPlayer{
    private MediaPlayer sound;
    
    /**
     * @param relativePath format: "1111.mp3"
     * @param cycleCount format: 1 or MediaPlayer.INDEFINITE
     * @param volume volume of the sound
     */
    public SoundPlayer(String relativePath, int cycleCount, double volume) {
        File file = new File(relativePath);
        String mediaUrl = file.toURI().toString();
        Media media = new Media(mediaUrl);
        sound = new MediaPlayer(media);
        sound.setCycleCount(cycleCount);
        sound.setVolume(volume);
        // dispose itself when it ends
        if (cycleCount == 1){
            sound.setOnEndOfMedia(() -> {dispose();});
        }
    }
    
    public void pause() {
        if (sound != null) {
            sound.pause();
        }
    }
    
    public void play() {
        if (sound != null) {
            sound.play();
        }
    }
    
    public void dispose() {
        if (sound != null) {
            sound.stop();
            sound.dispose();
            sound = null;
        }
    }
}
