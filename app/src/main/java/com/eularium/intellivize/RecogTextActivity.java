package com.eularium.intellivize;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata;
import com.google.firebase.ml.vision.document.FirebaseVisionDocumentText;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

public class RecogTextActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int RC_IMAGE_PICK = 989;
    private ImageView ivRecog;
    private TextView tvRecog;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recog_text);
        ivRecog = findViewById(R.id.ivRecog);
        tvRecog = findViewById(R.id.tvMsgRecog);
        findViewById(R.id.btnRecog).setOnClickListener(this);
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
                        ivRecog.setImageBitmap(bitmap);
                        recognizeText();
                    } else {
                        Bundle extras = data.getExtras();
                        if (extras != null) {
                            if (extras.containsKey("data")) {
                                bitmap = (Bitmap) extras.get("data");
                                ivRecog.setImageBitmap(bitmap);
                                recognizeText();
                            }
                        }
                    }
                } catch (Exception e) {
                    showMessage(e.getMessage());
                }
            }
        }
    }

    private void recognizeText() {
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);
        FirebaseVisionTextRecognizer textRecognizer = FirebaseVision.getInstance().getOnDeviceTextRecognizer();
        textRecognizer.processImage(image)
                .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                    @Override
                    public void onSuccess(FirebaseVisionText firebaseVisionText) {
                        extracted(firebaseVisionText);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showMessage(e.getMessage());
                    }
                });
    }

    private void extracted(FirebaseVisionText firebaseVisionText) {
        String resultText = firebaseVisionText.getText();
        //showMessage(resultText);
        for (FirebaseVisionText.TextBlock block : firebaseVisionText.getTextBlocks()) {
            showMessage(block.getText());
        }
    }

    private void showMessage(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
        tvRecog.setText(message);
    }

    void recog() {
        try {
            //1. create FirebaseVisionImage object from an image

            //Bitmap bitmap = null;
            //FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);

            //image captured from camera
            //FirebaseVisionImage image = FirebaseVisionImage.fromMediaImage(image, rotation);

            /*FirebaseVisionImageMetadata metadata = new FirebaseVisionImageMetadata.Builder()
                    .setWidth(1280)
                    .setHeight(720)
                    .setFormat(FirebaseVisionImageMetadata.IMAGE_FORMAT_YV12)
                    //.setRotation(rotation)
                    .build();
            FirebaseVisionImage image = FirebaseVisionImage.fromByteBuffer(buffer, metadata);*/

            //FirebaseVisionImage.fromFilePath(this, uri);

            //2. get instance of FirebaseVisionTextRecognizer
            //FirebaseVisionTextRecognizer textRecognizer = FirebaseVision.getInstance().getOnDeviceTextRecognizer();

            //3. pass image to processImage
            /*textRecognizer.processImage(image)
                    .addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
                        @Override
                        public void onSuccess(FirebaseVisionText firebaseVisionText) {
                            //extract text from recognized text
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });*/
        } catch (Exception ex) {

        }
    }

    void extractText(FirebaseVisionText text) {
        //FirebaseVisionText contains the full text and 0 or more TextBlock
        //TextbBlock represents a rectangular block of text with 0 or more Line objects
        //Line objects contains 0 or more Element object
        //Element objects represents word and word-like entities (dates, numbers)

        String resultText = text.getText();
        for (FirebaseVisionText.TextBlock block : text.getTextBlocks()) {
            block.getText();
            block.getConfidence();
            block.getRecognizedLanguages();
            block.getCornerPoints();
            block.getBoundingBox();
            for (FirebaseVisionText.Line line : block.getLines()) {
                line.getText();
                line.getConfidence();
                line.getRecognizedLanguages();
                line.getCornerPoints();
                line.getBoundingBox();
                for (FirebaseVisionText.Element element : line.getElements()) {
                    element.getText();
                    element.getConfidence();
                    element.getRecognizedLanguages();
                    element.getCornerPoints();
                    element.getBoundingBox();
                }
            }
        }
    }

    void extractText(FirebaseVisionDocumentText documentText) {
        documentText.getText();
        for (FirebaseVisionDocumentText.Block block : documentText.getBlocks()) {
            block.getRecognizedBreak();
            for (FirebaseVisionDocumentText.Paragraph paragraph : block.getParagraphs()) {
                paragraph.getRecognizedBreak();
                for (FirebaseVisionDocumentText.Word word : paragraph.getWords()) {
                    for (FirebaseVisionDocumentText.Symbol symbol : word.getSymbols()) {
                        symbol.getText();
                    }
                }
            }
        }
    }

    /*
     * Images must be upright. Orientation is used to determine the
     * ange image must be rotate
     */
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }
    /*
     * Get the angle by which an image must be rotated given the device's current orientation
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private int getRotationCompensation(String cameraId, Activity activity, Context context) throws CameraAccessException {
        int deviceRotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int compensation = ORIENTATIONS.get(deviceRotation);
        CameraManager cameraManager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
        int sensorOrientation = cameraManager.getCameraCharacteristics(cameraId).get(CameraCharacteristics.SENSOR_ORIENTATION);
        compensation = (compensation + sensorOrientation + 270) % 360;
        int result;
        switch (compensation) {
            case 0:
                result = FirebaseVisionImageMetadata.ROTATION_0;
                break;
            case 90:
                result = FirebaseVisionImageMetadata.ROTATION_90;
                break;
            case 180:
                result = FirebaseVisionImageMetadata.ROTATION_180;
                break;
            case 270:
                result = FirebaseVisionImageMetadata.ROTATION_270;
                break;
            default:
                result = FirebaseVisionImageMetadata.ROTATION_0;
        }
        return result;
    }
}
