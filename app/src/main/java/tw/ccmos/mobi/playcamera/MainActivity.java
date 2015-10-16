package tw.ccmos.mobi.playcamera;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.TextureView;
import android.view.View;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final static String CLASS_LABEL = "MainActivity";
    private PowerManager.WakeLock mWakeLock;
    private boolean recording;
    private CameraView cameraView;
    private Camera cameraDevice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, CLASS_LABEL);
        mWakeLock.acquire();

        initLayout();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mWakeLock == null) {
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, CLASS_LABEL);
            mWakeLock.acquire();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        recording = false;

        if (cameraView != null) {
            cameraView.stopPreview();
        }

        if (cameraDevice != null) {
            cameraDevice.stopPreview();
            cameraDevice.release();
            cameraDevice = null;
        }

        if (mWakeLock != null) {
            mWakeLock.release();
            mWakeLock = null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (mWakeLock != null) {
            mWakeLock.release();
            mWakeLock = null;
        }
    }

    @Override
    public void onClick(View v) {

    }

    private void initLayout() {
        cameraView = (CameraView) findViewById(R.id.cameraView);

        findViewById(R.id.startButton).setOnClickListener(this);
        findViewById(R.id.stopButton).setOnClickListener(this);

        cameraDevice = Camera.open();
        cameraView.setCamera(cameraDevice);
        cameraView.setFitMode(FitMode.WIDTH);
        cameraView.setAspectRatio(4, 3);

        cameraDevice.setDisplayOrientation(90);
        Camera.Size size = cameraDevice.getParameters().getSupportedPreviewSizes().get(0);
        cameraView.setPreviewSize(size.height, size.width);
        cameraView.requestLayout();
    }

    private void initRecorder() {
        
    }
}
