package com.example.nicolas.maniaplayer;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.design.widget.TabLayout;


import static com.example.nicolas.maniaplayer.R.id.TabLayout;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ViewPager viewPager = (ViewPager) findViewById(R.id.ViewPager);
        FragmentMusicAdapter fma = new FragmentMusicAdapter(getSupportFragmentManager());
        viewPager.setAdapter(fma);

        TabLayout tablayout = (TabLayout) findViewById(R.id.TabLayout);
        tablayout.setupWithViewPager(viewPager);

    }
}
