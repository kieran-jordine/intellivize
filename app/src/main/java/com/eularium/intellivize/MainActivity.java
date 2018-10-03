package com.eularium.intellivize;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.btnTR).setOnClickListener(this);
        findViewById(R.id.btnDF).setOnClickListener(this);
        findViewById(R.id.btnLB).setOnClickListener(this);
        findViewById(R.id.btnRV).setOnClickListener(this);
        startActivity(new Intent(this, CameraActivity.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.btnTR:
                startActivity(new Intent(this, RecogTextActivity.class));
                break;
            case R.id.btnDF:
                startActivity(new Intent(this, DetectFacesActivity.class));
                break;
            case R.id.btnLB:
                startActivity(new Intent(this, LabelActivity.class));
                break;
            case R.id.btnRV:
                startActivity(new Intent(this, CameraActivity.class));
                break;
        }
    }
}