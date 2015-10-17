package tw.ccmos.mobi.playcamera;

import android.hardware.Camera;
import android.media.MediaRecorder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Surface;
import android.view.View;

import tw.ccmos.tools.camera.FitMode;
import tw.ccmos.tools.camera.CameraTextureView;
import tw.ccmos.tools.camera.PlayerTextureView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    CameraTextureView preview;
    PlayerTextureView player;

    Camera mCamera;
    Camera.Size mPreviewSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preview = (CameraTextureView) findViewById(R.id.preview);
        preview.setFitMode(FitMode.WIDTH);
        preview.setAspectRatio(16, 9);

        player = (PlayerTextureView) findViewById(R.id.player);
        player.setFitMode(FitMode.WIDTH);
        player.setAspectRatio(16, 9);

        findViewById(R.id.startButton).setOnClickListener(this);
        findViewById(R.id.stopButton).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if (id == R.id.startButton) {
            preview.startRecord();
        } else {
            preview.stopRecord();

            playVideo();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        preview.stopRecord();
        preview.stopPreview();

        mCamera.release();
        mCamera = null;
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(mCamera == null) mCamera = Camera.open();

        preview.setCamera(mCamera, CameraTextureView.CameraOrientation.PORTRAIT);
        preview.startPreview();
    }

    private void playVideo() {
        player.setPreviewSize(preview.getPreviewWidth(), preview.getPreviewHeight());
        player.setVideoPathAndPlay(preview.getVideoPath());
    }
}
