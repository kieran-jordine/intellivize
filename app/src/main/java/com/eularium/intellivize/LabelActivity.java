package com.eularium.intellivize;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.google.firebase.ml.vision.label.FirebaseVisionLabel;
import com.google.firebase.ml.vision.label.FirebaseVisionLabelDetector;
import com.google.firebase.ml.vision.label.FirebaseVisionLabelDetectorOptions;

import java.util.List;

public class LabelActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int RC_IMAGE_PICK = 989;
    private ImageView ivLabel;
    private TextView tvLabel;
    private Bitmap bitmap;
    private FirebaseVisionLabelDetector detector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_label);
        ivLabel = findViewById(R.id.ivLabel);
        tvLabel = findViewById(R.id.tvMsgLabel);
        findViewById(R.id.btnLabel).setOnClickListener(this);
        FirebaseVisionLabelDetectorOptions options = new FirebaseVisionLabelDetectorOptions.Builder()
                .setConfidenceThreshold(0.5f).build();
        detector = FirebaseVision.getInstance().getVisionLabelDetector(options);
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
                        ivLabel.setImageBitmap(bitmap);
                        detectLabels();
                    } else {
                        Bundle extras = data.getExtras();
                        if (extras != null) {
                            if (extras.containsKey("data")) {
                                bitmap = (Bitmap) extras.get("data");
                                ivLabel.setImageBitmap(bitmap);
                                detectLabels();
                            }
                        }
                    }
                } catch (Exception e) {
                    showMessage(e.getMessage());
                }
            }
        }
    }

    private void detectLabels() {
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);
        detector.detectInImage(image)
                .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionLabel>>() {
                    @Override
                    public void onSuccess(List<FirebaseVisionLabel> firebaseVisionLabels) {
                        detected(firebaseVisionLabels);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showMessage(e.getLocalizedMessage());
                    }
                });
    }

    private void detected(List<FirebaseVisionLabel> firebaseVisionLabels) {
        for (FirebaseVisionLabel label : firebaseVisionLabels) {
            showMessage(label.getEntityId()+" "+label.getLabel()+" "+label.getConfidence());
        }
    }

    private void showMessage(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        tvLabel.setText(message);
    }

}
