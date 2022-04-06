package com.example.ml2;

/*package whatever do not write package name here*/

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionPoint;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceLandmark;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    Button cameraButton;

    // whenever we request for our customized permission, we
    // need to declare an integer and initialize it to some
    // value .
    private final static int REQUEST_IMAGE_CAPTURE = 124;
    FirebaseVisionImage image;
    FirebaseVisionFaceDetector detector;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initializing our firebase in main activity
        FirebaseApp.initializeApp(this);

        // finding the elements by their id's alloted.
        cameraButton = findViewById(R.id.camera_button);

        // setting an onclick listener to the button so as
        // to request image capture using camera
        cameraButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {

                        // makin a new intent for opening camera
                        Intent intent = new Intent(
                                MediaStore.ACTION_IMAGE_CAPTURE);
                        if (intent.resolveActivity(
                                getPackageManager())
                                != null) {
                            startActivityForResult(
                                    intent, REQUEST_IMAGE_CAPTURE);
                        }
                        else {
                            // if the image is not captured, set
                            // a toast to display an error image.
                            Toast
                                    .makeText(
                                            MainActivity.this,
                                            "Something went wrong",
                                            Toast.LENGTH_SHORT)
                                    .show();
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode,
                                    @Nullable Intent data)
    {
        // after the image is captured, ML Kit provides an
        // easy way to detect faces from variety of image
        // types like Bitmap

        super.onActivityResult(requestCode, resultCode,
                data);
        if (requestCode == REQUEST_IMAGE_CAPTURE
                && resultCode == RESULT_OK) {
            Bundle extra = data.getExtras();
            Bitmap bitmap = (Bitmap)extra.get("data");
            detectFace(bitmap);
        }
    }

    // If you want to configure your face detection model
    // according to your needs, you can do that with a
    // FirebaseVisionFaceDetectorOptions object.
    private void detectFace(Bitmap bitmap)
    {
        FirebaseVisionFaceDetectorOptions options
                = new FirebaseVisionFaceDetectorOptions
                .Builder()
                .setModeType(
                        FirebaseVisionFaceDetectorOptions
                                .ACCURATE_MODE)
                .setLandmarkType(
                        FirebaseVisionFaceDetectorOptions
                                .ALL_LANDMARKS)
                .setClassificationType(
                        FirebaseVisionFaceDetectorOptions
                                .ALL_CLASSIFICATIONS)
                .build();

        // we need to create a FirebaseVisionImage object
        // from the above mentioned image types(bitmap in
        // this case) and pass it to the model.
        try {
            image = FirebaseVisionImage.fromBitmap(bitmap);
            detector = FirebaseVision.getInstance()
                    .getVisionFaceDetector(options);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        // It’s time to prepare our Face Detection model.
        detector.detectInImage(image)
                .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionFace> >() {
                    @Override
                    // adding an onSuccess Listener, i.e, in case
                    // our image is successfully detected, it will
                    // append it's attribute to the result
                    // textview in result dialog box.
                    public void onSuccess(
                            List<FirebaseVisionFace>
                                    firebaseVisionFaces)
                    {
                        String resultText = "";
                        int i = 1;
                        for (FirebaseVisionFace face :
                                firebaseVisionFaces) {
                            resultText
                                    = resultText
                                    .concat("\nFACE NUMBER. "
                                            + i + ": ")
                                    .concat(
                                            "\nSmile: "
                                                    + face.getSmilingProbability()
                                                    * 100
                                                    + "%")
                                    .concat(
                                            "\nleft eye open: "
                                                    + face.getLeftEyeOpenProbability()
                                                    * 100
                                                    + "%")
                                    .concat(
                                            "\nright eye open "
                                                    + face.getRightEyeOpenProbability()
                                                    * 100
                                                    + "%");
                            i++;
                        }

                        // if no face is detected, give a toast
                        // message.
                        if (firebaseVisionFaces.size() == 0) {
                            Toast
                                    .makeText(MainActivity.this,
                                            "NO FACE DETECT",
                                            Toast.LENGTH_SHORT)
                                    .show();
                        }
                        else {
                            Bundle bundle = new Bundle();
                            bundle.putString(
                                    LCOFaceDetection.RESULT_TEXT,
                                    resultText);
                            DialogFragment resultDialog
                                    = new ResultDialog();
                            resultDialog.setArguments(bundle);
                            resultDialog.setCancelable(true);
                            resultDialog.show(
                                    getSupportFragmentManager(),
                                    LCOFaceDetection.RESULT_DIALOG);
                        }
                    }
                }) // adding an onfailure listener as well if
                // something goes wrong.
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e)
                    {
                        Toast
                                .makeText(
                                        MainActivity.this,
                                        "Oops, Something went wrong",
                                        Toast.LENGTH_SHORT)
                                .show();
                    }
                });
    }
}
