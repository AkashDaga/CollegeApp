package com.example.akash.shield;

import android.hardware.Camera;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;

import com.example.akash.blueprints.Preview;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class CameraActivity extends AppCompatActivity {
    private static final String TAG = "continious shutter camera";
    Camera camera;
    Preview preview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        preview = new Preview(this);
        ((FrameLayout) findViewById(R.id.preview)).addView(preview);

        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        // your code here
                        preview.camera.takePicture(shutterCallback, rawCallback, jpegCallback);

                    }
                },
                500   //Half Second
        );

        new java.util.Timer().schedule(
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        // your code here
                        finish();

                    }
                },
                1000   //Half Second
        );


//        Log.d(TAG, "onCreate'd");
    }


    android.hardware.Camera.ShutterCallback shutterCallback = new android.hardware.Camera.ShutterCallback()
    {
        public void onShutter()
        {
//            Log.d(TAG, "onShutter'd");
        }
    };

    /** Handles data for raw picture */
    android.hardware.Camera.PictureCallback rawCallback = new android.hardware.Camera.PictureCallback()
    {
        @Override
        public void onPictureTaken(byte[] data, android.hardware.Camera camera) {
//            Log.d(TAG, "onPictureTaken - raw");
        }


    };

    /** Handles data for jpeg picture */
    android.hardware.Camera.PictureCallback jpegCallback = new android.hardware.Camera.PictureCallback()
    {
        @Override
        public void onPictureTaken(byte[] data, android.hardware.Camera camera) {

            FileOutputStream outStream = null;
            try
            {
                // write to sdcard
                String name="Image"+System.currentTimeMillis();
                outStream = new FileOutputStream(String.format("/sdcard/"+name+".jpg", System.currentTimeMillis()));

                outStream.write(data);
                outStream.close();
//                Log.d(TAG, "onPictureTaken - wrote bytes: " + data.length);
            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            finally
            {
            }
//            Log.d(TAG, "onPictureTaken - jpeg");

        }
    };
}