package com.example.akash.blueprints;

import android.content.Context;
import android.view.View;
import android.widget.TabHost;

/**
 * Created by Akash on 01-04-2016.
 */
public class FakeContents implements TabHost.TabContentFactory {
    Context context;

    public FakeContents(Context context) {
        this.context = context;
    }

    @Override
    public View createTabContent(String tag) {
        View fakeView=new View(context);
        fakeView.setMinimumHeight(0);
        fakeView.setMinimumWidth(0);
        return fakeView;
    }
}
