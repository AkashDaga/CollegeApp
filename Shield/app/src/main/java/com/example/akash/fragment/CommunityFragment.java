package com.example.akash.fragment;


import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.akash.shield.R;

import java.util.ArrayList;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class CommunityFragment extends Fragment {
    private View rootView;
    private ImageButton record;
    private EditText post;


    public CommunityFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView= inflater.inflate(R.layout.fragment_community, container, false);

        post = (EditText) rootView.findViewById(R.id.etPost);
        record=(ImageButton)rootView.findViewById(R.id.imgbtnRecord);
        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RecordSpeech recordSpeech=new RecordSpeech();
                recordSpeech.doInBackground();
            }
        });
        return rootView;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        switch (requestCode){
            case 100:{
                if(resultCode == Activity.RESULT_OK && intent != null){
                    ArrayList<String> result=intent.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    post.setText(result.get(0));
                }

            }

        }
    }

    private class RecordSpeech extends AsyncTask<Void,Void,Void> {

        @Override
        protected Void doInBackground(Void... params) {
            Intent intent= new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "say something...");
            try {
                startActivityForResult(intent,100);
            }catch(Exception e){
                Toast.makeText(getActivity(),"Sorry yourdevice doesn't support speech text",Toast.LENGTH_LONG).show();
            }
            return null;
        }
    }
}
