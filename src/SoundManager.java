// Em SoundManager.java
import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class SoundManager {

    private Clip backgroundMusic;
    private FloatControl gainControl;
    private float lastVolume = 0.8f;
    private boolean isMuted = false;
    private boolean isMusicPlaying = false; // <-- ADICIONADO

    private final String[] musicTracks = {
            "A little Piece of Heavens.wav",
            "Pastel Ghost - Dark Beach.wav",
            "Tame Impala - Let It Happen.wav",
            "Scarlet House - Slowing Down.wav",
            "Little Dark Age - MGMT.wav"
    };

    // --- MÚSICA DE FUNDO ---

    // NOVO MÉTODO: Inicia a música padrão se não estiver tocando
    public void startDefaultMusic() {
        if (isMusicPlaying) return; // Não faz nada se já estiver tocando

        String[] tracks = getTrackList();
        if (tracks.length > 0) {
            playMusic(tracks[0]); // Toca a primeira música da lista
        }
    }

    public void playMusic(String trackName) {
        stopMusic(); // Para qualquer música que esteja tocando

        try {
            File soundFile = new File("res/" + trackName);
            if (!soundFile.exists()) {
                System.err.println("Arquivo de música não encontrado: " + trackName);
                return;
            }

            AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
            backgroundMusic = AudioSystem.getClip();
            backgroundMusic.open(audioIn);

            gainControl = (FloatControl) backgroundMusic.getControl(FloatControl.Type.MASTER_GAIN);

            if (isMuted) {
                setClipVolume(0.0f);
            } else {
                setClipVolume(lastVolume);
            }

            backgroundMusic.loop(Clip.LOOP_CONTINUOUSLY);
            isMusicPlaying = true; // <-- ADICIONADO

        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("Erro ao tocar a música: " + e.getMessage());
        }
    }

    public void stopMusic() {
        if (backgroundMusic != null && backgroundMusic.isRunning()) {
            backgroundMusic.stop();
        }
        isMusicPlaying = false; // <-- ADICIONADO
    }

    public String[] getTrackList() {
        return musicTracks;
    }

    // --- (O resto da classe: Controle de Volume e Efeitos Sonoros, permanece o mesmo) ---

    public void setVolume(float volume) {
        if (volume < 0.0f) volume = 0.0f;
        if (volume > 1.0f) volume = 1.0f;

        if (volume > 0.0f) {
            this.lastVolume = volume;
        }
        this.isMuted = (volume == 0.0f);

        setClipVolume(volume);
    }

    private void setClipVolume(float volume) {
        if (gainControl == null) return;

        float min = gainControl.getMinimum();
        float max = gainControl.getMaximum();
        float gain = (max - min) * volume + min;

        if (volume == 0.0f) {
            gain = min;
        }

        gainControl.setValue(gain);
    }

    public boolean toggleMute() {
        if (isMuted) {
            setVolume(lastVolume);
        } else {
            setVolume(0.0f);
        }
        return isMuted;
    }

    public float getVolume() {
        return isMuted ? 0.0f : lastVolume;
    }

    public void playSound(String filePath, boolean loop) {
        if (isMuted) return;

        try {
            File soundFile = new File(filePath);
            if (!soundFile.exists()) {
                System.err.println("Arquivo de som não encontrado: " + filePath);
                return;
            }
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);

            if (loop) {
                clip.loop(Clip.LOOP_CONTINUOUSLY);
            } else {
                clip.start();
            }
        } catch (Exception e) {
            System.err.println("Erro ao tocar o som: " + e.getMessage());
        }
    }
}