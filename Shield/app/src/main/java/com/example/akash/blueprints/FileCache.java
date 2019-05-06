package com.example.akash.blueprints;

import android.content.Context;
import android.util.Log;

import java.io.File;

// Custom class that stores cache images to a directory that is accessed to load the images faster across listview
public class FileCache {

private File cacheDir;

public FileCache(Context context){
    //Find the dir to save cached images
    Log.e("contact","contact");
    if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
        cacheDir=new File(android.os.Environment.getExternalStorageDirectory(),"ContactList");
    else
        cacheDir=context.getCacheDir();

    if(!cacheDir.exists())
    {
        cacheDir.mkdirs();
    }
    else
    {
        Log.e("contact","con"+cacheDir.getAbsolutePath());
    }
}
// Returns the file from its hashcode id name
public File getFile(String url){
    //I identify images by hashcode. Not a perfect solution, good for the demo.
    String filename=String.valueOf(url.hashCode());
    //Another possible solution (thanks to grantland)
    //String filename = URLEncoder.encode(url);
    File f = new File(cacheDir, filename);
    return f;

}

// Clears(deletes) the cached files associated here
public void clear(){
    File[] files=cacheDir.listFiles();
    if(files==null)
        return;
    for(File f:files)
        f.delete();
}

}
