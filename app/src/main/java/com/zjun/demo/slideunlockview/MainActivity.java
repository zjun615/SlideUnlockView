package com.zjun.demo.slideunlockview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.zjun.widget.SlideUnlockView;

public class MainActivity extends AppCompatActivity {

    private SlideUnlockView slvUnlock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        slvUnlock = findViewById(R.id.slv_unlock);

        slvUnlock.setOnUnlockListener(new SlideUnlockView.OnUnlockListener() {
            @Override
            public void onUnlocked() {
                Toast.makeText(MainActivity.this, "已解锁", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_bold:
                slvUnlock.setBold(!slvUnlock.isBold());
                break;
                case R.id.btn_animator:
                slvUnlock.setBackAnimEnable(!slvUnlock.isBackAnimEnable());
                break;
                default: break;
        }

    }
}
