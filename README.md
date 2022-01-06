# MaskGuide
1、使用方法
</br>
  （1）在build.gradle文件中引入aar，aar在Releases中下载
  ```
    dependencies {
    ...
    
    implementation(name: 'maskguide-release', ext: 'aar')
    
    ...

  }
  ```
  (2)在需要展示的Activity中
  ```
  public class MainActivity extends AppCompatActivity {
    private FrameLayout frameLayout;
    private MaskGuideView maskGuideView;
    ...
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus){
            //需要高亮的view的集合
            List<HighlightView> viewList = new ArrayList<>();
            viewList.add(new HighlightView(testImg, "这是测试图片"));
            ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT);
            maskGuideView = MaskGuideView.getInstance(this, viewList);
            if (!maskGuideView.isHasShowMaskGuideView()){
                ViewGroup viewGroup = (ViewGroup) maskGuideView.getParent();
                if (null != viewGroup){
                    viewGroup.removeView(maskGuideView);
                }
                frameLayout.addView(maskGuideView, params);
                maskGuideView.bringToFront();
                maskGuideView.requestLayout();
            }
        }
    }
}
    
