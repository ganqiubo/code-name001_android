package tl.pojul.com.fastim.Audio;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.SoundPool;
import android.util.Log;
import android.widget.Toast;
import java.io.File;
import java.io.IOException;
import tl.pojul.com.fastim.R;

public class AudioManager {

    private static AudioManager mAudioManager;
    private final Context mContext;
    private MediaRecorder mMediaRecorder;
    private SoundPool soundPool;
    private MediaPlayer mediaPlayer = new MediaPlayer();
    private final String TAG = "AudioManager";

    private AudioManager(Context mContext) {
        this.mContext = mContext;
    }

    public static void Instance(Context context) {
        if (mAudioManager == null) {
            synchronized (AudioManager.class) {
                if (mAudioManager == null) {
                    mAudioManager = new AudioManager(context);
                }
            }
        }
    }

    public static AudioManager getInstance() {
        return mAudioManager;
    }

    public void startRecording(String filePath, String fileName) {
        File f = new File(filePath);
        if(!f.exists() && !f.mkdirs()){
            Log.e(TAG,"获取不到存储空间");
            return;
        }
        if(mContext != null){
            playShortSound(R.raw.record_end);
        }
        if(mMediaRecorder == null){
            mMediaRecorder = new MediaRecorder();
        }
        try {
            /* ②setAudioSource/setVedioSource */
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);// 设置麦克风
            /* ②设置音频文件的编码：AAC/AMR_NB/AMR_MB/Default 声音的（波形）的采样 */
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
            /*
             * ②设置输出文件的格式：THREE_GPP/MPEG-4/RAW_AMR/Default THREE_GPP(3gp格式
             * ，H263视频/ARM音频编码)、MPEG-4、RAW_AMR(只支持音频且音频编码要求为AMR_NB)
             */
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

            String fullFilePath = filePath + "/" + fileName;

            /* ③准备 */
            mMediaRecorder.setOutputFile(fullFilePath);
            mMediaRecorder.prepare();
            /* ④开始 */
            mMediaRecorder.start();
        } catch (IllegalStateException e) {
            Log.i(TAG, "call startAmr(File mRecAudioFile) failed!" + e.getMessage());
        } catch (IOException e) {
            Log.i(TAG, "call startAmr(File mRecAudioFile) failed!" + e.getMessage());
        }
    }

    public void stopRecording() {
        if(mContext != null){
            playShortSound(R.raw.record_end);
        }
        if (mMediaRecorder == null) {
            return;
        }
        try {
            mMediaRecorder.stop();
            mMediaRecorder.reset();
            mMediaRecorder.release();
            mMediaRecorder = null;
        }catch (RuntimeException e){
            mMediaRecorder.reset();
            mMediaRecorder.release();
            mMediaRecorder = null;
        }
    }

    public void playShortSound(int soundId){
        soundPool= new SoundPool(1,android.media.AudioManager.STREAM_SYSTEM,5);
        soundPool.setOnLoadCompleteListener((SoundPool soundPool, int sampleId, int status)->{
            if(status == 0){
                VibrateManager.getInstance().vibrate(80);
                soundPool.play(1, 1, 1, 0, 0, 1);
            }
        });
        soundPool.load(mContext, soundId,1);
    }

    public void playNetAudio(String url){
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(url);
            mediaPlayer.setAudioStreamType(android.media.AudioManager.STREAM_SYSTEM);
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                }
            });
            mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    Toast.makeText(mContext, "播放错误！", Toast.LENGTH_SHORT).show();
                    return true;
                }
            });
        } catch (IOException e) {
            Toast.makeText(mContext, "播放错误！", Toast.LENGTH_SHORT).show();
        }
    }

    public void playLocalAudio(String filePath){
        playNetAudio(filePath);
    }

    public void stopPlaySound() {
        if(mediaPlayer != null && mediaPlayer.isPlaying()){
            mediaPlayer.stop();
        }
    }
}
