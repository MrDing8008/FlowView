package com.zigsun.flowview;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= 23){
            if (Settings.canDrawOverlays(MainActivity.this)){
                Intent intent = new Intent(MainActivity.this, MainService.class);
                Toast.makeText(MainActivity.this,"已开启MainService",Toast.LENGTH_SHORT).show();
                startService(intent);
                finish();
            }else {
                //若没有权限，获取提示
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                Toast.makeText(MainActivity.this,"需要获取权取使用悬浮窗",Toast.LENGTH_SHORT).show();
                startActivity(intent);

            }
        }else{
            //SDK在23以下不用管
            Intent intent = new Intent(MainActivity.this, MainService.class);
            startService(intent);
            finish();

        }
        
    }
}
