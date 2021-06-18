 package com.example.mlkitbasic;

 import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

 public class MainActivity extends AppCompatActivity {

    ImageView imageView;
    Button chooseBtn;
    FloatingActionButton backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imgV);
        chooseBtn = findViewById(R.id.buttonCli);
        backBtn = findViewById(R.id.bckBtn);

        ActivityResultLauncher<Intent> itLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {

                if(result.getResultCode() == AppCompatActivity.RESULT_OK) {
                    backBtn.setVisibility(View.VISIBLE);
                    chooseBtn.setVisibility(View.GONE);
                    imageView.setVisibility(View.VISIBLE);

                    Intent i = result.getData();

                    imageView.setImageURI(i.getData());

                    InputImage image;
                    InputStream is = null;



                    try {

                        is = getContentResolver().openInputStream(i.getData());

                        Bitmap myBitmap = BitmapFactory.decodeStream(is);

                        Paint myRectPaint = new Paint();
                        myRectPaint.setStrokeWidth(15);
                        myRectPaint.setColor(Color.RED);
                        myRectPaint.setStyle(Paint.Style.STROKE);

                        Bitmap tempBitmap = Bitmap.createBitmap(myBitmap.getWidth(), myBitmap.getHeight(), Bitmap.Config.RGB_565);
                        Canvas tempCanvas = new Canvas(tempBitmap);
                        tempCanvas.drawBitmap(myBitmap, 0, 0, null);



                        image = InputImage.fromFilePath(getApplicationContext(), i.getData());

                        FaceDetectorOptions options =
                                new FaceDetectorOptions.Builder()
                                        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
                                        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
                                        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                                        .build();

                        FaceDetector detector = FaceDetection.getClient(options);

                        Task<List<Face>> res =
                                detector.process(image).addOnSuccessListener(
                                                new OnSuccessListener<List<Face>>() {
                                                    @Override
                                                    public void onSuccess(List<Face> faces) {
                                                        // Task completed successfully
                                                        // ...

                                                        if(faces.size() == 0){
                                                            Toast.makeText(MainActivity.this,"No Faces to detect",Toast.LENGTH_SHORT).show();
                                                        }

                                                        for(int j=0; j<faces.size(); j++) {

                                                            Face face = faces.get(j);

                                                            Rect rect = face.getBoundingBox();

                                                            myRectPaint.setStyle(Paint.Style.STROKE);
                                                            myRectPaint.setColor(Color.RED);

                                                            tempCanvas.drawRect(rect,myRectPaint);


                                                            // If classification was enabled:
                                                            if (face.getSmilingProbability() != null) {

                                                                float smileProb = face.getSmilingProbability();

                                                                myRectPaint.setStyle(Paint.Style.FILL);
                                                                myRectPaint.setTextSize(48);
                                                                myRectPaint.setColor(Color.GREEN);

                                                                String per = String.format("%.2f",(smileProb * 100));

                                                                tempCanvas.drawText("Smile :- " + per + " %",rect.left - 20,rect.top + 30,myRectPaint);

                                                            }



                                                        }

                                                        imageView.setImageDrawable(new BitmapDrawable(getResources(),tempBitmap));
                                                    }
                                                })
                                        .addOnFailureListener(
                                                new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        // Task failed with an exception
                                                        // ...

                                                    }
                                                });

//                        Toast.makeText(MainActivity.this,"Corr",Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                }
            }
        });

        chooseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent();
                it.setType("image/*");
                it.setAction(Intent.ACTION_GET_CONTENT);

                itLauncher.launch(it);
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backBtn.setVisibility(View.GONE);
                imageView.setVisibility(View.GONE);
                chooseBtn.setVisibility(View.VISIBLE);
            }
        });


    }
}