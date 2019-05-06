package com.example.akash.fragment;

import com.example.akash.adapters.ContactsDBHelper;

import android.app.job.JobScheduler;
import android.content.Intent;
import android.database.Cursor;
import android.hardware.Camera;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.akash.blueprints.LocationProviderService;
import com.example.akash.shield.CameraActivity;
import com.example.akash.shield.ConvertIntoPhysicalAddress;
import com.example.akash.shield.FastLogIn;
import com.example.akash.shield.LogInActivity;
import com.example.akash.shield.R;
import com.example.akash.shield.SharedPreferenceInventory;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import static com.google.android.gms.internal.zzir.runOnUiThread;

/**
 * A simple {@link Fragment} subclass.
 */
public class SendMessageFragment extends Fragment {
    public ContactsDBHelper helper;
    private final int JOB_ID = 100;
    View rootView;
    private Button record;
    public Cursor cursor;
    public String PhoneNumber, Message;
    public LocationProviderService mLocationProviderService;
    public Double dCurLattitude, dCurLongitude;
    public Double dPrevLattitude=0.0, dPrevLongitude=0.0;
    public String sCurLattitude, sCurLongitude, sPrevLattitude, sPrevLongitude;
    public SharedPreferenceInventory myInventory;
    private JobScheduler mJobScheduler;

    //variables for camera starts here
    private static final String TAG = "CameraDemo";
    Camera camera;
    //variables for camera ends here


    public SendMessageFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_send_message, container, false);

        myInventory = new SharedPreferenceInventory(getActivity());

        record = (Button) rootView.findViewById(R.id.btnRecord);
        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Timer timer = new Timer();
                timer.scheduleAtFixedRate(new TimerTask() {

                    @Override
                    public void run() {
                        doFetchLocations();
                        if(((dPrevLongitude*100) == (dCurLongitude*100)) && ((dPrevLattitude*100) == (dCurLattitude*100))){
                            timer.cancel();
                        }
                        else {
                            doSpecifiedTasks();
                            dPrevLattitude = dCurLattitude;
                            dPrevLongitude = dCurLongitude;
                        }
                    }


                }, 0, 1000 * 60 * 5);

            }
        });
        return rootView;
    }

    private void doSpecifiedTasks() {
//      doFetchLocations();
        doCaptureContiniousShots();
        doRecordAudio();
        doConvertIntoPhysicalAddress();
        doSendSMS();
}

//  SENDING SMS TO FAVOURITE CONTACTS
    private void doSendSMS() {
        MessageAsync sendSMS=new MessageAsync();
        sendSMS.execute();

    }

    public class MessageAsync extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            //fetching the Message
            SharedPreferenceInventory myInventory = new SharedPreferenceInventory(getActivity());
            Message = myInventory.getUpdatdMessage();

            //fetching the contacts
            helper = new ContactsDBHelper(getActivity());
            cursor = helper.getAllFavourite();
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                PhoneNumber = cursor.getString(1);
                Log.e("Number", PhoneNumber);
                sendMessage(PhoneNumber, Message);//Sends Message
                cursor.moveToNext();
            }
            cursor.close();
            return null;
        }

        private void sendMessage(String phoneNumber, String message) {
            try {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(phoneNumber, null, message, null, null);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "SMS sent.",
                                Toast.LENGTH_LONG).show();
                    }
                });

            } catch (Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(),
                                "Sending SMS failed.",
                                Toast.LENGTH_LONG).show();
                    }
                });
                e.printStackTrace();
            }
        }
    }

    //CONVERT LONGITUDE LATTITUDE INTO PHYSICAL ADDRESS
    private void doConvertIntoPhysicalAddress() {
        ConvertIntoPhysicalAddress address=new ConvertIntoPhysicalAddress(dCurLattitude,dCurLongitude,getActivity());
        address.execute();

    }

    //AUDIO RECORDING FOR JUST ONE MINUITE

    private void doRecordAudio() {

        AudioRecorder recorder=new AudioRecorder();
        recorder.execute();
    }

        class AudioRecorder extends AsyncTask<Void, Void, Void> {
        private static final String AUDIO_RECORDER_FILE_EXT_3GP = ".3gp";
        private static final String AUDIO_RECORDER_FILE_EXT_MP4 = ".mp4";
        private static final String AUDIO_RECORDER_FOLDER = "AudioRecorder";

        private MediaRecorder recorder = null;
        private int currentFormat = 0;
        private int output_formats[] = {MediaRecorder.OutputFormat.MPEG_4,
                MediaRecorder.OutputFormat.THREE_GPP};
        private String file_exts[] = {AUDIO_RECORDER_FILE_EXT_MP4,
                AUDIO_RECORDER_FILE_EXT_3GP};

        @Override
        protected void onPreExecute() {
            Toast.makeText(getActivity(), "Audio Recording Start ", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected Void doInBackground(Void... params) {
            StartRecording();
            return null;
        }

        private void StartRecording() {
            recorder = new MediaRecorder();
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            recorder.setOutputFile(getFilename());
            recorder.setOnErrorListener(errorListener);
            recorder.setOnInfoListener(infoListener);

            try {
                recorder.prepare();
                recorder.start();
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        Thread timer=new Thread(){
                            public void run(){
                                try{
                                    if (recorder != null) {
                                    recorder.stop();
                                    recorder.reset();
                                    recorder.release();
                                    recorder = null;
                                    }
                                }
                                catch (Exception e){
                                    e.printStackTrace();
                                }
                            }

                        };
                        timer.start();
                    }
                }, 60000);
            } catch (Exception e) {
                Toast.makeText(getActivity(), e + "", Toast.LENGTH_LONG).show();
            }
        }

        private MediaRecorder.OnErrorListener errorListener = new MediaRecorder.OnErrorListener() {
            @Override
            public void onError(MediaRecorder mr, int what, int extra) {
                Toast.makeText(getActivity(),
                        "Error: " + what + ", " + extra, Toast.LENGTH_SHORT).show();
            }
        };

        private MediaRecorder.OnInfoListener infoListener = new MediaRecorder.OnInfoListener() {
            @Override
            public void onInfo(MediaRecorder mr, int what, int extra) {
                Toast.makeText(getActivity(),
                        "Warning: " + what + ", " + extra, Toast.LENGTH_SHORT)
                        .show();
            }
        };

        private String getFilename() {
            String filepath = Environment.getExternalStorageDirectory().getPath();
            File file = new File(filepath, AUDIO_RECORDER_FOLDER);

            if (!file.exists()) {
                file.mkdirs();
            }

            return (file.getAbsolutePath() + "/" + System.currentTimeMillis() + file_exts[currentFormat]);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Toast.makeText(getActivity(), "Audio Recording Stopped", Toast.LENGTH_SHORT).show();
        }
    }

    //CONTINIOUS PHOTO SHOOT IN CAMERA

    private void doCaptureContiniousShots() {
        CameraAsync camera=new CameraAsync();
        camera.execute();
    }

    class CameraAsync extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            Intent intent=new Intent(getActivity(), CameraActivity.class);
            startActivity(intent);
            return null;
        }
    }

    //FETCH LOCATION OF THE USER

    private void doFetchLocations() {
        mLocationProviderService = new LocationProviderService(getActivity());
        // check if GPS enabled
        if (mLocationProviderService.canGetLocation()) {

            myInventory = new SharedPreferenceInventory(getActivity());

            dCurLattitude = mLocationProviderService.getLatitude();
            sCurLattitude = dCurLattitude.toString();
//            myInventory.setCurrentLattitude(sCurLattitude);

            dCurLongitude = mLocationProviderService.getLongitude();
            sCurLongitude = dCurLongitude.toString();
//            myInventory.setCurrentLongitude(sCurLongitude);

            mLocationProviderService.stopUsingGPS();

            if (dCurLattitude == 0.0) {
                Toast.makeText(getActivity(), "Please try again", Toast.LENGTH_LONG);
            }
            myInventory.setCurrentLattitude(sCurLattitude);
            myInventory.setCurrentLongitude(sCurLongitude);

        } else {

            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings
            mLocationProviderService.showSettingsAlert();
        }
    }



//    //SmS sendig Code starts here
//    public class MessageAsync extends AsyncTask<Void, Void, Void> {
//
//        @Override
//        protected Void doInBackground(Void... params) {
//            //fetching the Message
//            SharedPreferenceInventory myInventory = new SharedPreferenceInventory(getActivity());
//            Message = myInventory.getUpdatdMessage();
//
//            //fetching the contacts
//            helper = new ContactsDBHelper(getActivity());
//            cursor = helper.getAllFavourite();
//            cursor.moveToFirst();
//            while (!cursor.isAfterLast()) {
//                PhoneNumber = cursor.getString(1);
//                Log.e("Number", PhoneNumber);
//                sendMessage(PhoneNumber, Message);//Sends Message
//                cursor.moveToNext();
//            }
//            cursor.close();
//            return null;
//        }
//
//        private void sendMessage(String phoneNumber, String message) {
//            try {
//                SmsManager smsManager = SmsManager.getDefault();
//                smsManager.sendTextMessage(phoneNumber, null, message, null, null);
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(getActivity(), "SMS sent.",
//                                Toast.LENGTH_LONG).show();
//                    }
//                });
//
//            } catch (Exception e) {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(getActivity(),
//                                "Sending SMS failed.",
//                                Toast.LENGTH_LONG).show();
//                    }
//                });
//                e.printStackTrace();
//            }
//        }
//    }
//
//    //Music recorder starts here
//    public class AudioRecorder extends AsyncTask<Void, Void, Void> {
//        private static final String AUDIO_RECORDER_FILE_EXT_3GP = ".3gp";
//        private static final String AUDIO_RECORDER_FILE_EXT_MP4 = ".mp4";
//        private static final String AUDIO_RECORDER_FOLDER = "AudioRecorder";
//
//        private MediaRecorder recorder = null;
//        private int currentFormat = 0;
//        private int output_formats[] = {MediaRecorder.OutputFormat.MPEG_4,
//                MediaRecorder.OutputFormat.THREE_GPP};
//        private String file_exts[] = {AUDIO_RECORDER_FILE_EXT_MP4,
//                AUDIO_RECORDER_FILE_EXT_3GP};
//
//        @Override
//        protected void onPreExecute() {
//            Toast.makeText(getActivity(), "Audio Recording Start ", Toast.LENGTH_SHORT).show();
//        }
//
//        @Override
//        protected Void doInBackground(Void... params) {
//            StartRecording();
//            return null;
//        }
//
//        private void StartRecording() {
//            recorder = new MediaRecorder();
//            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
//            recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
//            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
//            recorder.setOutputFile(getFilename());
//            recorder.setOnErrorListener(errorListener);
//            recorder.setOnInfoListener(infoListener);
//
//            try {
//                recorder.prepare();
//                recorder.start();
//                new Timer().schedule(new TimerTask() {
//                    @Override
//                    public void run() {
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                if (recorder != null) {
//                                    recorder.stop();
//                                    recorder.reset();
//                                    recorder.release();
//                                    recorder = null;
//                                }
//                            }
//                        });
//                    }
//                }, 60000);
//            } catch (Exception e) {
//                Toast.makeText(getActivity(), e + "", Toast.LENGTH_LONG).show();
//            }
//        }
//
//        private MediaRecorder.OnErrorListener errorListener = new MediaRecorder.OnErrorListener() {
//            @Override
//            public void onError(MediaRecorder mr, int what, int extra) {
//                Toast.makeText(getActivity(),
//                        "Error: " + what + ", " + extra, Toast.LENGTH_SHORT).show();
//            }
//        };
//
//        private MediaRecorder.OnInfoListener infoListener = new MediaRecorder.OnInfoListener() {
//            @Override
//            public void onInfo(MediaRecorder mr, int what, int extra) {
//                Toast.makeText(getActivity(),
//                        "Warning: " + what + ", " + extra, Toast.LENGTH_SHORT)
//                        .show();
//            }
//        };
//
//        private String getFilename() {
//            String filepath = Environment.getExternalStorageDirectory().getPath();
//            File file = new File(filepath, AUDIO_RECORDER_FOLDER);
//
//            if (!file.exists()) {
//                file.mkdirs();
//            }
//
//            return (file.getAbsolutePath() + "/" + System.currentTimeMillis() + file_exts[currentFormat]);
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            Toast.makeText(getActivity(), "Audio Recording Stopped", Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    //Music recorder ends here
//
//    // Background Task for camera
//    private class CameraAsync extends AsyncTask<Void, Void, Void> {
//        @Override
//        protected Void doInBackground(Void... params) {
//            Intent intent = new Intent(getActivity(), CameraActivity.class);
//            startActivity(intent);
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            Toast.makeText(getActivity(), "One image clicked by the camera", Toast.LENGTH_SHORT).show();
//        }
//    }
}