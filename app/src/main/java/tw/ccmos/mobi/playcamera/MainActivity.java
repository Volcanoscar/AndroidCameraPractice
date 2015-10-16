package tw.ccmos.mobi.playcamera;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity implements TextureView.SurfaceTextureListener, View.OnClickListener {

    CropTextureView preview;
    Camera mCamera;
    Camera.Size mPreviewSize;
    boolean mRecording;
    MediaRecorder mMediaRecorder;
    Surface mSurface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preview = (CropTextureView) findViewById(R.id.preview);
        preview.setSurfaceTextureListener(this);
        preview.setFitMode(FitMode.WIDTH);
        preview.setAspectRatio(16, 9);

        findViewById(R.id.startButton).setOnClickListener(this);
        findViewById(R.id.stopButton).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.startButton:
                startRecording();
                break;
            case R.id.stopButton:
                stopRecording();
                break;
        }
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        mSurface = new Surface(surface);

        if (mCamera == null)
            mCamera = Camera.open();

        try {
            mCamera.setDisplayOrientation(90);
            mCamera.setPreviewTexture(surface);

            Camera.Parameters parameters = mCamera.getParameters();

            List<Camera.Size> supportedPreviewSizes = parameters.getSupportedPreviewSizes();
            mPreviewSize = supportedPreviewSizes.get(0);
            parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
            preview.setPreviewSize(mPreviewSize.height, mPreviewSize.width);
            preview.requestLayout();

            List<String> supportedFocusModes = parameters.getSupportedFocusModes();
            if (supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO))
                parameters.setFlashMode(Camera.Parameters.FOCUS_MODE_AUTO);
            else if (supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE))
                parameters.setFlashMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            else if (supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO))
                parameters.setFlashMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);

            mCamera.setParameters(parameters);

            startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
    protected void onPause() {
        super.onPause();

        releaseCamera();
    }

    private void releaseCamera() {
        mCamera.stopPreview();
        mCamera.release();
        mCamera = null;
    }

    private void startPreview() {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                mCamera.startPreview();
            }
        });
    }

    private void stopRecording() {

    }

    private void startRecording() {

    }

    public String getVideoPath() {
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).getAbsolutePath() + "/tmp.mp4";
        return path;
    }
}
