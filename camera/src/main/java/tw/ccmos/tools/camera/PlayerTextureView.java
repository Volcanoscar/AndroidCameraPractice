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
public class PlayerTextureView extends CropTextureView implements TextureView.SurfaceTextureListener {
    String mVideoPath;
    MediaPlayer mMediaPlayer;
    Surface mSurface;

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

    public boolean setVideoPath(String videoPath) {
        try {
            if (mMediaPlayer != null) {
                releasePlayer();
            }

            mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setSurface(mSurface);
            mMediaPlayer.setDataSource(videoPath);
            mMediaPlayer.prepare();

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

    public void setVideoPathAndPlay(String videoPath) {
        if (setVideoPath(videoPath)) {
            play();
        }
    }

    private void play() {
        if (mMediaPlayer != null)
            mMediaPlayer.start();
    }

    public void stop() {
        if (mMediaPlayer != null)
            mMediaPlayer.stop();
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
}
