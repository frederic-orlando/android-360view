package com.example.a360view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Surface;
import android.widget.Toast;

import com.asha.vrlib.MDVRLibrary;
import com.asha.vrlib.model.BarrelDistortionConfig;
import com.asha.vrlib.texture.MD360BitmapTexture;

public class MainActivity extends AppCompatActivity {

    private MDVRLibrary mdvrLibrary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initLibrary();
    }

    private void initLibrary() {
        mdvrLibrary = MDVRLibrary.with(this)
                .displayMode(MDVRLibrary.DISPLAY_MODE_NORMAL)
                .interactiveMode(MDVRLibrary.INTERACTIVE_MODE_TOUCH)
                .asBitmap(new MDVRLibrary.IBitmapProvider() {
                    @Override
                    public void onProvideBitmap(MD360BitmapTexture.Callback callback) {
                        final BitmapFactory.Options options = new BitmapFactory.Options();
                        //options.inJustDecodeBounds = true;
                        options.inSampleSize = 2;
                        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.hd_image360, options);

                        callback.texture(bitmap);
                    }
                })
                //.barrelDistortionConfig(new BarrelDistortionConfig().setDefaultEnabled(true).setScale(0.95f))
                .pinchEnabled(true)
                .build(findViewById(R.id.surface_view));

        //mdvrLibrary.setAntiDistortionEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mdvrLibrary.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mdvrLibrary.onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mdvrLibrary.onDestroy();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mdvrLibrary.onOrientationChanged(this);
    }
}