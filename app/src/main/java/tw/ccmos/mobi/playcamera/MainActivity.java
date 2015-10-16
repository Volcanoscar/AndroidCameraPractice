package tw.ccmos.mobi.playcamera;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;

import org.jdeferred.android.AndroidDeferredManager;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity implements TextureView.SurfaceTextureListener, View.OnClickListener {

    AutoFitLayout mContainerView;
    AutoFitTextureView mPreviewTextureView;
    Button mStartButton;
    Button mStopButton;

    Camera mCamera;

    boolean mPreviewing;
    Camera.Size mPreviewSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContainerView = (AutoFitLayout)findViewById(R.id.containerView);
        mStartButton = (Button) findViewById(R.id.startButton);
        mStopButton = (Button) findViewById(R.id.stopButton);
        mPreviewTextureView = (AutoFitTextureView) findViewById(R.id.preview);

        mPreviewTextureView.setSurfaceTextureListener(this);
        mStartButton.setOnClickListener(this);
        mStopButton.setOnClickListener(this);

        mContainerView.setAutoFitMode(AutoFitMode.WIDTH);
        mContainerView.setAspectRatio(16, 9);
        mPreviewTextureView.setAutoFitMode(AutoFitMode.WIDTH);

        mStartButton.setEnabled(false);
        mStopButton.setEnabled(false);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        if(mCamera == null) mCamera = Camera.open();

        try {
            Camera.Parameters parameters = mCamera.getParameters();

            List<Camera.Size> supportedPreviewSizes = parameters.getSupportedPreviewSizes();
            mPreviewSize = supportedPreviewSizes.get(0);
            parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
            mPreviewTextureView.setAspectRatio(mPreviewSize.height, mPreviewSize.width);

            List<String> supportedFocusModes = parameters.getSupportedFocusModes();
            if(supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO))
                parameters.setFlashMode(Camera.Parameters.FOCUS_MODE_AUTO);
            else if(supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE))
                parameters.setFlashMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            else if(supportedFocusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO))
                parameters.setFlashMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);


            mCamera.setParameters(parameters);
            mCamera.setDisplayOrientation(90);
            mCamera.setPreviewTexture(surface);

            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    startPreview();
                }
            });
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
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.startButton:
                startPreview();
                break;
            case R.id.stopButton:
                stopPreview();
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        stopAndReleaseCamera();
    }

    private void stopAndReleaseCamera() {
        stopPreview();

        mCamera.stopPreview();
        mCamera.release();
        mCamera = null;
        mPreviewing = false;
    }

    private void stopPreview() {
        if(mPreviewing) {
            mPreviewing = false;

            mStartButton.setEnabled(true);
            mStopButton.setEnabled(false);

            mCamera.stopPreview();
        }
    }

    private void startPreview() {
        if(!mPreviewing) {
            mPreviewing = true;

            mStartButton.setEnabled(false);
            mStopButton.setEnabled(true);

            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    mCamera.startPreview();
                }
            });
        }
    }
}
