package tw.ccmos.tools.camera;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.view.Surface;
import android.view.TextureView;

import java.io.IOException;

/**
 * Created by mosluce on 15/10/17.
 */
public class PlayerTextureView extends CropTextureView implements TextureView.SurfaceTextureListener, MediaPlayer.OnCompletionListener {
    String mVideoPath;
    MediaPlayer mMediaPlayer;
    Surface mSurface;
    boolean mRepeat = true;

    public PlayerTextureView(Context context) {
        super(context);

        setSurfaceTextureListener(this);
    }

    public PlayerTextureView(Context context, AttributeSet attrs) {
        super(context, attrs);

        setSurfaceTextureListener(this);
    }

    public PlayerTextureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setSurfaceTextureListener(this);
    }

    /**
     * 放入影片路徑
     * @param videoPath
     * @return
     */
    public boolean setVideoPath(String videoPath) {
        try {
            if (mMediaPlayer != null) {
                releasePlayer();
            }

            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setSurface(mSurface);
            mMediaPlayer.setDataSource(videoPath);
            mMediaPlayer.prepare();
            mMediaPlayer.setOnCompletionListener(this);

            requestLayout();
        } catch (IllegalStateException e) {
            releasePlayer();
            return false;
        } catch (IOException e) {
            releasePlayer();
            return false;
        }

        return true;
    }

    private void releasePlayer() {
        mMediaPlayer.reset();
        mMediaPlayer.release();
        mMediaPlayer = null;
    }

    /**
     * 放入影片路徑並播放
     * @param videoPath
     */
    public void setVideoPathAndPlay(String videoPath) {
        if (setVideoPath(videoPath)) {
            play();
        }
    }

    private void play() {
        if (mMediaPlayer != null)
            mMediaPlayer.start();
    }

    /**
     * 停止播放
     */
    public void stop() {
        if (mMediaPlayer != null)
            mMediaPlayer.stop();
    }

    /**
     * 設定重複播放(預設：true)
     * @param repeat
     */
    public void setRepeat(boolean repeat) {
        this.mRepeat = repeat;
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        mSurface = new Surface(surface);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if(mRepeat) mp.start();
    }
}
