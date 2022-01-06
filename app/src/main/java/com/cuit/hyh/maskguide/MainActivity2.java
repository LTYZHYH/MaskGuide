package com.cuit.hyh.maskguide;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.cuit.hyh.maskguide.model.HighlightView;
import com.cuit.hyh.maskguide.view.MaskGuideView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity2 extends AppCompatActivity {
    private static final String TAG = "MainActivity2";
    private Button testBtn1, testBtn2;
    private ImageView testImg;
    private LinearLayout testLL;
    private TextView textView;
    private FrameLayout frameLayout;
    private MaskGuideView maskGuideView;
    private Switch aSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        testBtn1 = findViewById(R.id.button3);
        testBtn2 = findViewById(R.id.button4);
        testImg = findViewById(R.id.imageView);
        textView = findViewById(R.id.textView);
        testLL = findViewById(R.id.linear_layout);
        aSwitch = findViewById(R.id.switch2);
        frameLayout = (FrameLayout) this.getWindow().getDecorView();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus){
            List<HighlightView> viewList = new ArrayList<>();
            viewList.add(new HighlightView(testBtn1, "开始这是一个测试按钮,这是一个测试按钮,这是一个测试按钮,这是一个测试按钮,这是一个测试按钮,这是一个测试按钮,这是一个测试按钮,这是一个测试按钮,这是一个测试按钮,这是一个测试按钮,这是一个测试按钮,这是一个测试按钮,这是一个测试按钮,这是一个测试按钮,这是一个测试按钮,这是一个测试按钮,这是一个测试按钮,这是一个测试按钮,结束"));
            viewList.add(new HighlightView(testBtn2, "这是一个测试按钮2,这是一个测试按钮2,这是一个测试按钮2,这是一个测试按钮2,这是一个测试按钮2,这是一个测试按钮2,这是一个测试按钮2,这是一个测试按钮2,这是一个测试按钮2,这是一个测试按钮2,这是一个测试按钮2,这是一个测试按钮2,这是一个测试按钮2,这是一个测试按钮2,这是一个测试按钮2,这是一个测试按钮2,这是一个测试按钮2,这是一个测试按钮2,这是一个测试按钮2,这是一个测试按钮2,这是一个测试按钮2,这是一个测试按钮2,这是一个测试按钮2,这是一个测试按钮2,这是一个测试按钮2,结束"));
            viewList.add(new HighlightView(testImg, "这是测试图片"));
            viewList.add(new HighlightView(textView, "这是一个TextView"));
            viewList.add(new HighlightView(testLL, "这是一个测试LinearLayout"));
            viewList.add(new HighlightView(aSwitch, "这是一个开关捏~这是一个开关捏~这是一个开关捏~这是一个开关捏~这是一个开关捏~这是一个开关捏~这是一个开关捏~"));
            int[] location = new int[2];
            testBtn2.getLocationInWindow(location);
            Log.d(TAG, "onWindowFocusChanged: length = " + (testBtn2.getRight()-testBtn2.getLeft())
                    + " height = " + (testBtn2.getBottom() - testBtn2.getTop()));
            Log.d(TAG, "onWindowFocusChanged: getLocationInWindow() x = " + location[0] + " y = " + location[1]);
            int[] location2 = new int[2];
            testBtn2.getLocationOnScreen(location2);
            Log.d(TAG, "onWindowFocusChanged: getLocationOnScreen() x = " + location2[0] + " y = " + location2[1]);
            ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT);
            maskGuideView = MaskGuideView.getInstance(this, viewList);
            //maskGuideView.setMaskGuideViewShow();
            if (!maskGuideView.isHasShowMaskGuideView()){
                ViewGroup viewGroup = (ViewGroup) maskGuideView.getParent();
                if (null != viewGroup){
                    viewGroup.removeView(maskGuideView);
                }
                Log.d(TAG, "onWindowFocusChanged: frameLayout child num" + frameLayout.getChildCount());
                frameLayout.addView(maskGuideView, params);
                maskGuideView.bringToFront();
                maskGuideView.requestLayout();
            }
        }
    }
}