package com.example.akash.blueprints;


import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.provider.ContactsContract;
import android.widget.ImageView;

import com.example.akash.shield.R;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// Custom class that loads image for each list view item while it's view's being instantiated
public class ImageLoader {

    // Declaring global variables
private MemoryCache memoryCache=new MemoryCache();
private FileCache fileCache;
private Map<ImageView, String> imageViews=Collections.synchronizedMap(new WeakHashMap<ImageView, String>());
private ExecutorService executorService;
private Context mContext;
private final int stub_id = R.drawable.profile;

    // Constructor that initializes this class object
public ImageLoader(Context context){
    fileCache=new FileCache(context);
    executorService=Executors.newFixedThreadPool(5);
    mContext = context;
}
// Displays image to the image view with respect to the list item's reference(contact id here)
public void DisplayImage(String url, ImageView imageView)
{
    // Keeping a reference of the image to the Weak hash map so that the next access to it will be able to reuse it
    imageViews.put(imageView, url);
    // Fetching the image bitmap from the cache with respect to the the file reference
    Bitmap bitmap=memoryCache.get(url);
    // If the bitmap is found then duly setting it to the respective imageview otherwise the default image is being set
    if(bitmap!=null)
        imageView.setImageBitmap(bitmap);
    else
    {
        queuePhoto(url, imageView);
        imageView.setImageResource(stub_id);
    }
}
// Queues the photo to be loaded
private void queuePhoto(String url, ImageView imageView)
{
    PhotoToLoad p=new PhotoToLoad(url, imageView);
    executorService.submit(new PhotosLoader(p));
}

// Gets image bitmap of the contact with respect to its contact number
private Bitmap queryImageBitmap(String imageDataRow) {

    // Fetching the file from file cache
    File f = fileCache.getFile(imageDataRow);

    // If the file has image bitmap then duly returning it otherwise proceeding to fetch the image from the user's contacts' book
    Bitmap b = decodeFile(f);
    if (b != null)
        return b;

    Cursor c = mContext.getContentResolver().query(ContactsContract.Data.CONTENT_URI, new String[] {ContactsContract.CommonDataKinds.Photo.PHOTO},
            ContactsContract.Data._ID + "=?", new String[] {
        String.valueOf(imageDataRow)
    }, null);
    byte[] imageBytes = null;
    if (c != null) {
        if (c.moveToFirst()) {
            imageBytes = c.getBlob(0);
        }
        c.close();
    }

    //// Converting the byte array image data to file copying to the cached location

    InputStream is = new ByteArrayInputStream(imageBytes);
    try {
        OutputStream os = new FileOutputStream(f);
        copyStream(is, os);
        os.close();
    } catch (FileNotFoundException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
    }

    //// Returning the image bitmap decoding the byte array data of the image

    if (imageBytes != null) {
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
    } else {
        return null;
    }
}



// Copies a stream from source to destination stream
 private static void copyStream(InputStream is, OutputStream os) {
     final int buffer_size = 3072;

     try {
         byte[] bytes = new byte[buffer_size];
         for (;;) {
             int count = is.read(bytes, 0, buffer_size);

             if (count == -1)
                 break;
             os.write(bytes, 0, count);
         }
     } catch (Exception ex) {
         ex.printStackTrace();
     }
 }

//decodes image and scales it to reduce memory consumption
private Bitmap decodeFile(File f){
    try {
        //decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(new FileInputStream(f),null,o);

        //Find the correct scale value. It should be the power of 2.
        final int REQUIRED_SIZE = 128;
        int width_tmp=o.outWidth, height_tmp=o.outHeight;
        int scale=1;
        while(true){
            if(width_tmp/2<REQUIRED_SIZE || height_tmp/2<REQUIRED_SIZE)
            break;
            width_tmp/=2;
            height_tmp/=2;
            scale*=2;
        }

        //decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize=scale;
        return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
    } catch (FileNotFoundException e) {}
    return null;
}

// Task for the queued photo that's being loaded
private class PhotoToLoad
{
    public String url;
    public ImageView imageView;
    public PhotoToLoad(String u, ImageView i){
        url=u;
        imageView=i;
    }
}
// Photo loader thread that actually loads the image to the imageview instance
class PhotosLoader implements Runnable {
    PhotoToLoad photoToLoad;
    PhotosLoader(PhotoToLoad photoToLoad){
        this.photoToLoad=photoToLoad;
    }

    @Override
    public void run() {
        // If the imageview instance is being reused, there's no need to load it and therefore returning straight away
        // otherwise proceeding to load the specified image
        if(imageViewReused(photoToLoad))
            return;

        Bitmap bmp = queryImageBitmap(photoToLoad.url);
        bmp = getCircledBitmap(bmp, 100);
        memoryCache.put(photoToLoad.url, bmp);
        BitmapDisplayer bd=new BitmapDisplayer(bmp, photoToLoad);
        Activity a=(Activity)photoToLoad.imageView.getContext();
        a.runOnUiThread(bd);
    }
}
// Returns if the specified image's availed to be reused
boolean imageViewReused(PhotoToLoad photoToLoad){
    String tag=imageViews.get(photoToLoad.imageView);
    return tag == null || !tag.equals(photoToLoad.url);
}

//Used to display bitmap in the UI thread
class BitmapDisplayer implements Runnable
{
    Bitmap bitmap;
    PhotoToLoad photoToLoad;
    public BitmapDisplayer(Bitmap b, PhotoToLoad p){bitmap=b;photoToLoad=p;}
    public void run()
    {
        if(imageViewReused(photoToLoad))
            return;
        if(bitmap!=null)
            photoToLoad.imageView.setImageBitmap(bitmap);
        else
            photoToLoad.imageView.setImageResource(stub_id);
    }
}
// Gets circled bitmap from the bitmap supplied along with the scaling ratio
@SuppressWarnings("unused")
private Bitmap getCircledBitmap(Bitmap bitmap, int pixels) {
    final Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
            bitmap.getHeight(), Bitmap.Config.ARGB_8888);
    final Canvas canvas = new Canvas(output);
    final int color = 0xff424242;
    final Paint paint = new Paint();
    final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
    final RectF rectF = new RectF(rect);
    paint.setAntiAlias(true);
    canvas.drawARGB(0, 0, 0, 0);
    paint.setColor(color);
    // canvas.drawCircle(100, 100, 90, paint);
    canvas.drawCircle(bitmap.getWidth() / 2 + 0.7f,
            bitmap.getHeight() / 2 + 0.7f, bitmap.getWidth() / 2 + 0.1f,
            paint);

    paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
    canvas.drawBitmap(bitmap, rect, rect, paint);
    bitmap.recycle();
    return output;
}

}