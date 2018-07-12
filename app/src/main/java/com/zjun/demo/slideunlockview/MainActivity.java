package com.zjun.demo.slideunlockview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.zjun.widget.SlideUnlockView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ((SlideUnlockView)findViewById(R.id.slv_unlock)).setOnUnlockListener(new SlideUnlockView.OnUnlockListener() {
            @Override
            public void onUnlocked() {
                Toast.makeText(MainActivity.this, "已解锁", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
