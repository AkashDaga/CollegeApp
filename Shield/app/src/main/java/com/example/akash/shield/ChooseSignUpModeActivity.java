package com.example.akash.shield;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TabHost;

import com.android.volley.VolleyError;
import com.example.akash.adapters.SwipeTabAdapter;
import com.example.akash.blueprints.FakeContents;
import com.example.akash.fragment.FacebookFragment;
import com.example.akash.fragment.GoogleFragment;
import com.example.akash.fragment.RegisterFragment;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class ChooseSignUpModeActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener, TabHost.OnTabChangeListener {
    ViewPager viewPager;
    TabHost tabHost;
    String[] name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_sign_up_mode);

        initViewPager();
        initTabHost();

    }

    private void initViewPager() {
        viewPager=(ViewPager)findViewById(R.id.view_pager);
        List<Fragment> listFragments=new ArrayList<Fragment>();
        listFragments.add(new RegisterFragment());
        listFragments.add(new FacebookFragment());
        listFragments.add(new GoogleFragment());

        SwipeTabAdapter adapter=new SwipeTabAdapter(getSupportFragmentManager(),listFragments);
        viewPager.setAdapter(adapter);
        viewPager.setOnPageChangeListener(this);

    }

    private void initTabHost() {
        tabHost=(TabHost)findViewById(R.id.tabHost);
        tabHost.setup();
        name=getResources().getStringArray(R.array.SignUp_tabs_name);

        for(int i=0;i<name.length;i++){
            TabHost.TabSpec tabSpec;
            tabSpec=tabHost.newTabSpec(name[i]);
            tabSpec.setIndicator(name[i]);
            tabSpec.setContent(new FakeContents(getApplicationContext()));
            tabHost.addTab(tabSpec);
        }

        tabHost.setOnTabChangedListener(this);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        tabHost.setCurrentTab(position);

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onTabChanged(String tabId) {
        int selectedItem=tabHost.getCurrentTab();
        viewPager.setCurrentItem(selectedItem);
    }

//    @Override
//    public void onVolleyErrorOccurred(VolleyError response) {
//
//    }
//
//    @Override
//    public void getResponse(String response) {
//        Log.e("Result in ChooseSignup:", response);
//    }
//
//    @Override
//    public void getResponse(JSONArray jsonArray) {
//
//    }
//
//    @Override
//    public void getDownloadedImage(Bitmap bitmap) {
//
//    }
}
