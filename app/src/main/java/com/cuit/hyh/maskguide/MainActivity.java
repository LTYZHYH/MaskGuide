package com.cuit.hyh.maskguide;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cuit.hyh.maskguide.model.HighlightView;
import com.cuit.hyh.maskguide.utils.SPUtils;
import com.cuit.hyh.maskguide.view.MaskGuideView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private Button btn1, btn2;
    private FrameLayout frameLayout;
    private MaskGuideView maskGuideView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn1 = findViewById(R.id.button);
        btn2 = findViewById(R.id.button2);
        frameLayout = (FrameLayout) this.getWindow().getDecorView();
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, MainActivity2.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        boolean hasShowMaskGuide = (boolean) SPUtils.get(this, SPUtils.KEY_HAS_SHOW_MASK_GUIDE + "_" + this.getLocalClassName(), true);
        if (hasFocus && !hasShowMaskGuide){
            int[] location = new int[2];
            btn1.getLocationInWindow(location);
            Log.d(TAG, "onWindowFocusChanged: length = " + (btn1.getRight()-btn1.getLeft())
                    + " height = " + (btn1.getBottom() - btn1.getTop()));
            Log.d(TAG, "onWindowFocusChanged: getLocationInWindow() x = " + location[0] + " y = " + location[1]);
            int[] location2 = new int[2];
            btn2.getLocationOnScreen(location2);
            Log.d(TAG, "onWindowFocusChanged: getLocationOnScreen() x = " + location2[0] + " y = " + location2[1]);

            List<HighlightView> viewList = new ArrayList<>();
            viewList.add(new HighlightView(btn1, "这是一个按钮,它是按钮1"));
            viewList.add(new HighlightView(btn2, "这是按钮2,它是第二个按钮"));
            ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT);
            maskGuideView = MaskGuideView.getInstance(this, viewList);
            Log.d(TAG, "onWindowFocusChanged: 开始添加自定义布局");
//        mainLinearLayout.addView(maskGuideView, params);
//        constraintLayout.addView(maskGuideView, params);
//            if (!maskGuideView.isHasShowMaskGuideView()){
                ViewGroup viewGroup = (ViewGroup) maskGuideView.getParent();
                if (null != viewGroup){
                    viewGroup.removeView(maskGuideView);
                }
                Log.d(TAG, "onWindowFocusChanged: frameLayout child num" + frameLayout.getChildCount());
                frameLayout.addView(maskGuideView, params);
                maskGuideView.bringToFront();
                maskGuideView.requestLayout();
            //}
        }
    }

}