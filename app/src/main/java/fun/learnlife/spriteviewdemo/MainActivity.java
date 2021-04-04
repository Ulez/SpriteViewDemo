package fun.learnlife.spriteviewdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import java.util.Random;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    LogoView logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        logo = findViewById(R.id.logo);
        findViewById(R.id.bt_idle).setOnClickListener(this);
        findViewById(R.id.bt_loading).setOnClickListener(this);
        findViewById(R.id.bt_start).setOnClickListener(this);
        findViewById(R.id.bt_speakstart).setOnClickListener(this);
        findViewById(R.id.bt_startcontinue).setOnClickListener(this);
        findViewById(R.id.bt_ttsplaying).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_idle:
                logo.idle();
                break;
            case R.id.bt_loading:
                logo.loading();
                break;
            case R.id.bt_start:
                logo.start();
                break;
            case R.id.bt_speakstart:
                logo.speakStart();
                break;
            case R.id.bt_startcontinue:
                logo.startContinuousListening();
            case R.id.bt_ttsplaying:
                Random random1 = new Random();
                logo.ttsPlaying(random1.nextInt(6));
                break;
        }
    }
}