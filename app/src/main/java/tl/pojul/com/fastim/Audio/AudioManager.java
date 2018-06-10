package tl.pojul.com.fastim.Audio;

public class AudioManager {

    private static AudioManager mAudioManager;

    public static AudioManager getInstance() {
        if (mAudioManager == null) {
            synchronized (AudioManager.class) {
                if (mAudioManager == null) {
                    mAudioManager = new AudioManager();
                }
            }
        }
        return mAudioManager;
    }

}
