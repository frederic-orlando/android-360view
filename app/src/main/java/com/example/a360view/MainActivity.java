package com.example.a360view;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.asha.vrlib.MDVRLibrary;
import com.asha.vrlib.model.BarrelDistortionConfig;
import com.asha.vrlib.model.MDHotspotBuilder;
import com.asha.vrlib.model.MDPosition;
import com.asha.vrlib.model.MDRay;
import com.asha.vrlib.model.MDViewBuilder;
import com.asha.vrlib.plugins.MDWidgetPlugin;
import com.asha.vrlib.plugins.hotspot.IMDHotspot;
import com.asha.vrlib.plugins.hotspot.MDAbsView;
import com.asha.vrlib.plugins.hotspot.MDView;
import com.asha.vrlib.texture.MD360BitmapTexture;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.FileNotFoundException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    // android impl
    private class AndroidProvider implements MDVRLibrary.IImageLoadProvider {

        Activity activity;

        public AndroidProvider(Activity activity) {
            this.activity = activity;
        }

        @Override
        public void onProvideBitmap(Uri uri, MD360BitmapTexture.Callback callback) {
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(activity.getContentResolver().openInputStream(uri));
                callback.texture(bitmap);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        }
    }

    private MDVRLibrary mdvrLibrary;
    private GLSurfaceView glSurfaceView;

    private ArrayList<MDPosition> positions = new ArrayList<>();
    private ArrayList<Bitmap> listImage = new ArrayList<>();

    private int currentImageIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_main);

        glSurfaceView = findViewById(R.id.surface_view);

        initImage();
        initPosition();
        initLibrary();

        findViewById(R.id.button_change).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mdvrLibrary.switchDisplayMode(MainActivity.this);
            }
        });

        findViewById(R.id.button_print).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                printAngle();
            }
        });
    }

    private Bitmap toBitmap(int drawable) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), drawable, options);

        return bitmap;
    }

    private void initImage() {
        listImage.add(toBitmap(R.drawable.image360));
        listImage.add(toBitmap(R.drawable.hd_image360));
    }

    private void initPosition() {
        positions.add(MDPosition.newInstance().setZ(-12f).setAngleX(-20f).setAngleY(25f).setYaw(-60f).setRoll(-10f));
        positions.add(MDPosition.newInstance().setZ(-12f).setAngleY(60f));
    }

    private void initLibrary() {
        mdvrLibrary = MDVRLibrary.with(this)
                .displayMode(MDVRLibrary.DISPLAY_MODE_NORMAL)
                .interactiveMode(MDVRLibrary.INTERACTIVE_MODE_TOUCH)
                .asBitmap(new MDVRLibrary.IBitmapProvider() {
                    @Override
                    public void onProvideBitmap(MD360BitmapTexture.Callback callback) {
                        callback.texture(listImage.get(currentImageIndex));
                    }
                })
                //.barrelDistortionConfig(new BarrelDistortionConfig().setDefaultEnabled(true).setScale(0.95f))
                .pinchEnabled(true)
                .build(glSurfaceView);
        generateHotspot();
        //mdvrLibrary.setAntiDistortionEnabled(true);

//        TextView textView = new TextView(this);
//        textView.setBackgroundColor(0x55FFCC11);
//        textView.setText("Hello world.");
//
//        MDViewBuilder builder = MDViewBuilder.create()
//                .provider(textView, 400/*view width*/, 100/*view height*/)
//                .size(4, 1)
//                .position(MDPosition.newInstance().setZ(-12.0f))
//                .title("md view")
//                .tag("tag-md-text-view")
//                ;
//
//        MDAbsView mdView = new MDView(builder);
//        //plugins.add(mdView);
//        mdvrLibrary.addPlugin(mdView);
    }

    private void generateHotspot() {
        for (MDPosition position : positions) {
            final int index = positions.indexOf(position);
            if (index != currentImageIndex) {
                MDHotspotBuilder builder = MDHotspotBuilder.create(new AndroidProvider(this))
                        .size(3f,3f)
                        .provider(0, this, android.R.drawable.star_on)
                        .provider(1, this, android.R.drawable.star_off)
                        .provider(10, this, android.R.drawable.checkbox_off_background)
                        .provider(11, this, android.R.drawable.checkbox_on_background)
                        .listenClick(new MDVRLibrary.ITouchPickListener() {
                            @Override
                            public void onHotspotHit(IMDHotspot hitHotspot, MDRay ray) {
                                if (hitHotspot instanceof MDWidgetPlugin){
                                    changeImage(index);
//                                    Toast.makeText(MainActivity.this,
//                                            "Clicked : " + hitHotspot.getTitle(),
//                                            Toast.LENGTH_SHORT).show();
                                    //MDWidgetPlugin widgetPlugin = (MDWidgetPlugin) hitHotspot;
                                    //widgetPlugin.setChecked(!widgetPlugin.getChecked());
                                }
                            }
                        })
                        .title("star" + String.valueOf(index))
                        .position(position)
                        .status(0,1)
                        .checkedStatus(10,11);

                MDWidgetPlugin plugin = new MDWidgetPlugin(builder);
                mdvrLibrary.addPlugin(plugin);
            }
        }
    }

    private void generateTextView() {
//        for (MDPosition position : positions) {
//            TextView textView = new TextView(this);
//            textView.setBackgroundColor(0x55FFCC11);
//            //textView.setText();
//
//            MDViewBuilder builder = MDViewBuilder.create()
//                    .provider(textView, 400/*view width*/, 100/*view height*/)
//                    .size(4, 1)
//                    .position(MDPosition.newInstance().setZ(-12.0f))
//                    .title("md view")
//                    .tag("tag-md-text-view")
//                    ;
//
//            MDAbsView mdView = new MDView(builder);
//            //plugins.add(mdView);
//            mdvrLibrary.addPlugin(mdView);
//        }
    }

    private void changeImage(int index) {
        if (index < listImage.size()) {
            currentImageIndex = index;

            mdvrLibrary.notifyPlayerChanged();

            mdvrLibrary.removePlugins();
            generateHotspot();
        }
    }

    private void printAngle() {
        System.out.println("");
        System.out.println("Angle X : " + mdvrLibrary.getDirectorBrief().getPitch());
        System.out.println("Angle Y : " + mdvrLibrary.getDirectorBrief().getYaw());
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