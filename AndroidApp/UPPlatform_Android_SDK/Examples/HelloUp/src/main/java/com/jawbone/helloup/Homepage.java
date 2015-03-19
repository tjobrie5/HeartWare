package com.jawbone.helloup;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

/**
 * Created by markahlemeier on 3/1/15.
 */
public class Homepage extends Activity {

 @Override
 protected void onCreate(Bundle savedInstanceState){
     super.onCreate(savedInstanceState);
     getWindow().requestFeature(Window.FEATURE_NO_TITLE);

     setContentView(R.layout.homepage);

 }
}
