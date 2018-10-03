package com.eularium.intellivize;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.shapes.OvalShape;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionPoint;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceLandmark;

import java.util.List;

public class DetectFacesActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int RC_IMAGE_PICK = 989;
    private ImageView ivDetect;
    private TextView tvDetect;
    private Bitmap bitmap;
    private FirebaseVisionFaceDetector faceDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detect_faces);
        ivDetect = findViewById(R.id.ivDetect);
        tvDetect = findViewById(R.id.tvMsgDetect);
        findViewById(R.id.btnDetect).setOnClickListener(this);
        FirebaseVisionFaceDetectorOptions options = new FirebaseVisionFaceDetectorOptions.Builder()
                .setModeType(FirebaseVisionFaceDetectorOptions.ACCURATE_MODE)
                .setLandmarkType(FirebaseVisionFaceDetectorOptions.ALL_LANDMARKS) //eyes, ears, nose, cheeks, mouth
                .setClassificationType(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
                .setMinFaceSize(0.15f)
                .setTrackingEnabled(true)
                .build();
        faceDetector = FirebaseVision.getInstance().getVisionFaceDetector(options);
    }

    @Override
    public void onClick(View view) {
        Intent i2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Intent i1 = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        //Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        i1.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        Intent chooser = Intent.createChooser(i2, "Image");
        chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{i1});
        startActivityForResult(chooser, RC_IMAGE_PICK);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == RC_IMAGE_PICK) {
                try {
                    if (data.getData() != null) {
                        bitmap = BitmapFactory.decodeStream(getBaseContext().getContentResolver().openInputStream(data.getData()));
                        ivDetect.setImageBitmap(bitmap);
                        //drawDetections();
                        detectFaces();
                    } else {
                        Bundle extras = data.getExtras();
                        if (extras != null) {
                            if (extras.containsKey("data")) {
                                bitmap = (Bitmap) extras.get("data");
                                ivDetect.setImageBitmap(bitmap);
                                detectFaces();
                            }
                        }
                    }
                } catch (Exception e) {
                    showMessage(e.getMessage());
                }
            }
        }
    }

    private void detectFaces() {
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);
        faceDetector.detectInImage(image)
                .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionFace>>() {
                    @Override
                    public void onSuccess(List<FirebaseVisionFace> firebaseVisionFaces) {
                        detected(firebaseVisionFaces);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showMessage("detecterror: "+e.getLocalizedMessage());
                    }
                });
    }

    private void detected(List<FirebaseVisionFace> firebaseVisionFaces) {
        for (FirebaseVisionFace face : firebaseVisionFaces) {
            face.getBoundingBox();
            face.getHeadEulerAngleY(); //degree rotated to the right
            face.getHeadEulerAngleZ(); //degree tilted sideways
            //if landmark enabled
            FirebaseVisionFaceLandmark leftEye = face.getLandmark(FirebaseVisionFaceLandmark.LEFT_EYE);
            if (leftEye != null) {
                FirebaseVisionPoint pos = leftEye.getPosition();
            }
            float smiling = face.getSmilingProbability();
            if (smiling != FirebaseVisionFace.UNCOMPUTED_PROBABILITY) {
                showMessage("smiling: " + smiling);
            }
            if (face.getRightEyeOpenProbability() != FirebaseVisionFace.UNCOMPUTED_PROBABILITY) {
                showMessage("right eye open: " + face.getRightEyeOpenProbability());
            }
            if (face.getTrackingId() != FirebaseVisionFace.INVALID_ID) {
                showMessage("tracking id: " + face.getTrackingId());
            }
        }
    }

    private void drawDetections() {
        Bitmap copy = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        Canvas canvas = new Canvas(copy);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.parseColor("#99003399")); //semi-transparent blue
        Rect rect = new Rect(50, 50, 500, 500);
        canvas.drawRect(rect, paint);
        //canvas.drawOval(new RectF(600, 50, 1200, 500), paint);
        ivDetect.setImageBitmap(copy);
    }

    private void showMessage(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        tvDetect.setText(message);
    }

}
