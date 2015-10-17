package tw.ccmos.mobi.playcamera;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
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

import tw.ccmos.tools.camera.CropTextureView;
import tw.ccmos.tools.camera.FitMode;
import tw.ccmos.tools.camera.RecorderTextureView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    RecorderTextureView preview;
    Camera mCamera;
    Camera.Size mPreviewSize;
    boolean mRecording;
    MediaRecorder mMediaRecorder;
    Surface mSurface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preview = (RecorderTextureView) findViewById(R.id.preview);
        preview.setFitMode(FitMode.WIDTH);
        preview.setAspectRatio(16, 9);

        findViewById(R.id.startButton).setOnClickListener(this);
        findViewById(R.id.stopButton).setOnClickListener(this);

        mCamera = Camera.open();
        preview.setCamera(mCamera, RecorderTextureView.CameraOrientation.PORTRAIT);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.startButton:
                break;
            case R.id.stopButton:
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
