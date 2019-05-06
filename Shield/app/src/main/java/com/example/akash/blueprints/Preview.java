package com.example.akash.blueprints;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.Camera;
import android.os.Build;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;
import java.util.List;

/**
 * Created by Akash on 17-05-2016.
 */
public class Preview extends SurfaceView implements SurfaceHolder.Callback{
    private static final String TAG = "Preview";

    SurfaceHolder mHolder;
    public Camera camera;

    @SuppressWarnings("deprecation")
    public Preview(Context context)
    {
        super(context);

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void surfaceCreated(SurfaceHolder holder)
    {
        // The Surface has been created, acquire the camera and tell it where
        // to draw.
        camera = Camera.open();
        try
        {
            camera.setPreviewDisplay(holder);

            camera.setPreviewCallback(new Camera.PreviewCallback()
            {

                public void onPreviewFrame(byte[] data, Camera arg1)
                {
                    Preview.this.invalidate();
                }
            });
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder)
    {
        // Surface will be destroyed when we return, so stop the preview.
        // Because the CameraDevice object is not a shared resource, it's very
        // important to release it when the activity is paused.
        camera.stopPreview();
        camera = null;


    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h)
    {
        // Now that the size is known, set up the camera parameters and begin
        // the preview.



        Camera.Parameters parameters = camera.getParameters();
        List<Camera.Size> previewSizes = parameters.getSupportedPreviewSizes();

        // You need to choose the most appropriate previewSize for your app
        Camera.Size previewSize = previewSizes.get(0);    // .... select one of previewSizes here

        parameters.setPreviewSize(previewSize.width, previewSize.height);
        camera.setParameters(parameters);
        camera.enableShutterSound(false);//stop the sutter sound
        camera.setDisplayOrientation(90);// display rotate 90 degree
        camera.startPreview();
    }

    @Override
    public void draw(Canvas canvas)
    {
        super.draw(canvas);
        Paint p= new Paint(Color.RED);
        Log.d(TAG, "draw");
        canvas.drawText("PREVIEW", canvas.getWidth()/2, canvas.getHeight()/2, p );
    }

}
