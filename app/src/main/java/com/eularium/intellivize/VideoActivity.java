package com.eularium.intellivize;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

public class VideoActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int RC_IMAGE_PICK = 989;
    private TextView tvRecord;
 //   private Camera camera;
 //   private Preview preview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        tvRecord = findViewById(R.id.tvMsgRecord);
        findViewById(R.id.btnRecord).setOnClickListener(this);
   //     preview = new Preview(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, RC_IMAGE_PICK);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == RC_IMAGE_PICK) {
                ((VideoView) findViewById(R.id.videoView)).setVideoURI(data.getData());
            }
        }
    }
/*
    private boolean safeCameraOpen(int id) {
        try {
            releaseCamera();
            camera = Camera.open(id);
            return true;
        } catch (Exception ex) {
            showMessage(ex.getLocalizedMessage());
            return false;
        }
    }

    private void releaseCamera() {
        preview.setCamera(null);
        if (camera != null) {
            camera.release();
            camera = null;
        }
    }

    private void showMessage(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        tvRecord.setText(message);
    }

    class Preview extends ViewGroup implements SurfaceHolder.Callback {

        SurfaceView surfaceView;
        SurfaceHolder surfaceHolder;

        Preview(Context context) {
            super(context);
            surfaceView = new SurfaceView(context);
            addView(surfaceView);
            surfaceHolder = surfaceView.getHolder();
            surfaceHolder.addCallback(this);
            //surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {

        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            android.hardware.Camera.Parameters parameters = camera.getParameters();
            parameters.setPreviewSize(width, height);
            requestLayout();
            camera.setParameters(parameters);
            camera.startPreview();
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            if (camera != null) {
                camera.stopPreview();
            }
        }

        @Override
        protected void onLayout(boolean changed, int l, int t, int r, int b) {

        }

        public void setCamera(Camera c) {
            if (camera == c) {
                return;
            }
            stopCameraAndFreeCamera();
            camera = c;
            if (camera != null) {
                camera.getParameters().getSupportedPreviewSizes();
                requestLayout();
                camera.setPreviewDisplay();
                camera.startPreview();
            }
        }

        private void stopCameraAndFreeCamera() {
            if (camera != null) {
                camera.stopPreview();
                camera.release();
                camera = null;
            }
        }
    }
 */
}
