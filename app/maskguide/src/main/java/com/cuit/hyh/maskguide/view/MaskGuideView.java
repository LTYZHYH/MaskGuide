package com.cuit.hyh.maskguide.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.Shader;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;

import com.cuit.hyh.maskguide.R;
import com.cuit.hyh.maskguide.model.HighlightView;
import com.cuit.hyh.maskguide.utils.SPUtils;
import com.cuit.hyh.maskguide.utils.StatusBarCompat;

import java.util.ArrayList;
import java.util.List;

public class MaskGuideView extends View {
    private static final String TAG = "MaskGuideView";
    private static MaskGuideView maskGuideView;
    private static Context mContext;
    private List<HighlightView> viewList;
    private int flag = 0;//遍历控件时用来标记遍历到第几个控件
    private String key;//需要将每个初始化MaskGuideView时传进来的activity保存起来,根据activity名称来判断该activity是否展示过引导层
    private String activityName;//页面类名
    private int screenWidth;
    private int screenHeight;
    //画画相关
    private int viewTop;
    private int viewLeft;
    private int viewHeight;
    private int viewLength;
    private Canvas mCanvas;
    private Paint rectPaint;//高亮区域画笔
    private RectF rectF;// 高亮的rectF
    private int statusAreaHeight;//状态栏高度
    private int allAppAreaHeight = 0;
    private int titleHeight = 0;
    private boolean hasShowMaskGuideView = false;
    private int textNum = 0;//每行字数
    private int lineNum = 0;//一共多少行
    private RectF textRectF;//文字描述区域
    private float radian = 16;//描述区域圆角弧度
    private int textSize;//字体大小
    private Paint textPaint;//描述区域画笔
    private int x,y;//描述区域小三角启点坐标
    private int triangleSpace = 16;//小三角尖端距离高亮区域的距离
    private boolean isLeft;//判读小三角在左边还是右边;
    private boolean isNeedTopOrBottom;//小三角是否需要放上边或下边
    private boolean isTop;//判读小三角在上边还是下边;
    private int triangleH = 40;//小三角的高,同时也设为小三角底边的二分之一


    public static MaskGuideView getInstance(Context context, List<HighlightView> viewList){
//        if (null == context){
//            Log.e(TAG, "getInstance: context == null");
//            return null;
//        } else if (null == mContext){
//            mContext = context;
//            Log.d(TAG, "getInstance: 首次创建新的MaskGuideView对象");
//            maskGuideView = new MaskGuideView(context, viewList);
////        } else if (!context.getClass().getName().equals(mContext.getClass().getName())){
//        } else if (context.hashCode() != mContext.hashCode()){
//            Log.d(TAG, "getInstance: 前后context不同,需要创建新的MaskGuideView对象");
//            maskGuideView = new MaskGuideView(context, viewList);
//        } else {
//            Log.e(TAG, "getInstance: MaskGuideView已经存在");
////            return null;
//        }
        mContext = context;
        maskGuideView = new MaskGuideView(context, viewList);
        return maskGuideView;
    }

    private MaskGuideView(Context context, List<HighlightView> viewList1) {
        super(context);
        this.mContext = context;
        this.viewList = viewList1;
        init();
    }

    private MaskGuideView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.mContext = context;
        init();
    }

    private MaskGuideView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        init();
    }

    private MaskGuideView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.mContext = context;
    }

    private void init(){
        Log.d(TAG, "init()");
        if (null != getHostActivity()){
            activityName = getHostActivity().getLocalClassName();
            flag = (int) SPUtils.get(mContext, SPUtils.KEY_FLAG + "_" + activityName, 0);//获取标记
            key = SPUtils.KEY_HAS_SHOW_MASK_GUIDE + "_" +activityName;
            if (!SPUtils.contains(mContext, key)){
                SPUtils.put(mContext, key, false);
            } else {
                if ((boolean) (SPUtils.get(mContext, key, false))){
                    Log.d(TAG, "init: " + activityName + " 已经展示过引导层");
                    hasShowMaskGuideView = true;
                    return;
                }
            }
            DisplayMetrics dm = new DisplayMetrics();
            getHostActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
            Log.d(TAG, "onDraw: 屏幕高度 = " +dm.heightPixels);
            screenHeight = dm.heightPixels;
            Log.d(TAG, "onDraw: 屏幕宽度 = " +dm.widthPixels);
            screenWidth = dm.widthPixels;
            Rect rect = new Rect();
            getHostActivity().getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
            Log.d(TAG, "onDraw: 应用区顶部 = " + rect.top + " 应用区高度 = " + rect.height());
            Rect rect1 = new Rect();
            getHostActivity().getWindow().findViewById(Window.ID_ANDROID_CONTENT).getDrawingRect(rect1);
            titleHeight = getHostActivity().getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop();
            allAppAreaHeight = rect1.height();
            Log.d(TAG, "onDraw: allAppAreaHeight = " + allAppAreaHeight + " titleHeight = " + titleHeight);
            for (int i = 0; i < viewList.size(); i++) {
                int[] location = new int[2];
                HighlightView highlightView = viewList.get(i);
                View v = highlightView.getView();
                highlightView.getView().getLocationInWindow(location);
                Log.d(TAG, "init: length = " + (v.getRight()-v.getLeft())
                        + " height = " + (v.getBottom() - v.getTop()));
                viewLength = v.getRight() - v.getLeft();
                viewHeight = v.getBottom() - v.getTop();
                Log.d(TAG, "init: top = " + v.getTop() + " bottom = " + v.getBottom() + " left = " + v.getLeft() + " right = " + v.getRight());
                highlightView.getView().measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                Log.d(TAG, "init: width = " + highlightView.getView().getMeasuredWidth() + " height = " + highlightView.getView().getMeasuredHeight());
                Log.d(TAG, "init: getLocationInWindow() x = " + location[0] + " y = " + location[1]);
                viewLeft = location[0];
                viewTop = location[1];
            }
        }
        setMaskRect();
    }

    private void setMaskRect(){
        /*
        设置高亮区域及画笔
         */
        rectPaint = new Paint();
        rectPaint.setStrokeWidth(3);
        rectPaint.setAntiAlias(true);
        rectF = new RectF();
//        int top = viewList.get(flag).getTop();
//        int bottom = viewList.get(flag).getBottom();
//        int left = viewList.get(flag).getLeft();
//        int right = viewList.get(flag).getRight();
        HighlightView highlightView = viewList.get(flag);
        View view = highlightView.getView();
        int[] location = new int[2];
        view.getLocationInWindow(location);
        int top = location[1];
        int left = location[0];
        int bottom = top + (view.getBottom() - view.getTop());
        int right = left + (view.getRight()-view.getLeft());
        Log.d(TAG, "实际高亮区域 top = " + top + " bottom = " + bottom + " left = " + left + " right = " + right);
//        rectF.set(65,117,349,261);
        rectF.set(left,top,right,bottom);
        statusAreaHeight = StatusBarCompat.getStatusBarHeight(mContext);
        Log.d(TAG, "init: statusAreaHeight = " + statusAreaHeight);
        //判断高亮区域4边距离屏幕边框的距离
        int topSpace = top;
        int leftSpace = left;
        int bottomSpace = screenHeight - bottom;
        int rightSpace = screenWidth - right;
        /*
        开始设置文字描述区域及画笔
         */
        textPaint = new Paint();
//        textPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        textPaint.setStrokeWidth(3);
        textPaint.setAntiAlias(true);
        int textRectFWidth = screenWidth/3;//默认最小宽度
        textNum = 5;
        textSize = (int) ((textRectFWidth/textNum) * 0.6);
        textRectFWidth = (int) (textRectFWidth*0.6);
        int descriptionNum = highlightView.getDescription().length();
        if (descriptionNum > 50){
            descriptionNum = 50;
        }
        //int descriptionNum = 33;//定死33个字,超过则省略
        lineNum = descriptionNum/textNum + 1;//行数
        int textRectFHeight= lineNum * ((textRectFWidth/textNum) + 5) + textSize;//这里加5是为了把行间距一起算上
        int highLightHeightCenter = ((bottom - top)/2) + top;
        int highLightWidthCenter = ((right - left)/2) + left;
        Log.d(TAG, "setMaskRect: descriptionNum = " +descriptionNum
                + " lineNum= "+ lineNum
                + " textRectFWidth= "+ textRectFWidth
                + " textRectFHeight=" + textRectFHeight
                + " textSize = " + textSize
                + " highLightHeightCenter = " + highLightHeightCenter
                + " highLightWidthCenter = " + highLightWidthCenter);
        int topD = highLightHeightCenter - (textRectFHeight/2);
        int bottomD = highLightHeightCenter + (textRectFHeight/2);
        //1.假设描述可以放在高亮左边
        if (topD < 0){
            topD = 0;
        }
        if (bottomD > screenHeight){
            bottomD = screenHeight;
            topD = bottomD - textRectFHeight;
        }
        int leftD = left - triangleH - textRectFWidth;
        int rightD = left - triangleH;
        isLeft = true;
        isNeedTopOrBottom = false;
        //2.高亮左边放不下,则放到右边
        if ((textRectFWidth + triangleH ) > leftSpace){
            leftD = right + triangleH;
            rightD = leftD + textRectFWidth;
            isLeft = false;
        }
        //3.高亮右边也放不下.则放上面或者下面
        if (!isLeft && (textRectFWidth + triangleH) > rightSpace){
            Log.d(TAG, "setMaskRect: 放上面或者下面");
            isNeedTopOrBottom = true;
            //因为放上面或者下面,则每行字数需要重现设计,描述区域高度、宽度也需要重现计算
            textRectFWidth = (int) (screenWidth * 0.8);//因为描述放在上边或者下边,那么描述区域的宽度就不需要设置得这么窄了
            textNum = 22;
            lineNum = descriptionNum/textNum + 1;//行数
            textRectFHeight= lineNum * ((textRectFWidth/textNum) + 5) + textSize; //这里加5是为了把行间距一起算上
            Log.d(TAG, "放上面或者下面,setMaskRect: descriptionNum = " +descriptionNum
                    + " lineNum= "+ lineNum
                    + " textRectFWidth= "+ textRectFWidth
                    + " textRectFHeight=" + textRectFHeight);
            if ((textRectFHeight + triangleH) > topSpace){
                //4.放下面
                topD = bottom + triangleH;
                bottomD = topD + textRectFHeight;
                isTop = false;
            } else {
                //5.放上面
                bottomD = top - triangleH;
                topD = bottomD - textRectFHeight;
                isTop = true;
            }
            leftD = highLightWidthCenter - (textRectFWidth/2);
            rightD = highLightWidthCenter + (textRectFWidth/2);
        }
        Paint paint = new Paint();
        Paint.FontMetrics fm = paint.getFontMetrics();
        float height1 = fm.descent - fm.ascent;
        float height2 = fm.bottom - fm.top + fm.leading;
        float realH = height1 + height2;
        Log.d(TAG, "setMaskRect: height1 = " + height1 + " height2=" + height2 + " realH = " + realH);
        textRectF = new RectF();
        //textRectF.set(leftD, topD, rightD, (2*realH*lineNum) + topD);
        textRectF.set(leftD, topD, rightD, bottomD);
        Log.d(TAG, "文字描述矩形区域 topD = " + topD + " bottomD = " + bottomD + " leftD = " + leftD + " right = " + rightD);
        //小三角的原点
        if (isNeedTopOrBottom){
            if (isTop){
                y = top - triangleSpace;
            } else {
                y = bottom + triangleSpace;
            }
            x = highLightWidthCenter;
        } else {
            if (isLeft){
                x = left - triangleSpace;
            } else {
                x = right + triangleSpace;
            }
            y = highLightHeightCenter;
        }
        //刷新
        postInvalidate();
    }

    private Activity getHostActivity(){
        if(mContext instanceof Activity){
            Activity activity = (Activity) mContext;
            return activity;
        } else {
            return null;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Log.d(TAG, "onMeasure: widthMeasureSpec = " + widthMeasureSpec + " heightMeasureSpec = " +heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        Log.d(TAG, "onLayout: changed = " + changed + " left = " + left + " top = " + top
        + " right = " + right + " bottom = " + bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Log.d(TAG, "onDraw()");
        super.onDraw(canvas);
        if (hasShowMaskGuideView){
            return;
        }
        mCanvas = canvas;

        int canvasWidth = canvas.getWidth();
        int canvasHeight = canvas.getHeight();
        Log.d(TAG, "onDraw: canvasWidth = "+canvasWidth+" canvasHeight = "+canvasHeight);
        int layerId = canvas.saveLayer(0, 0, canvasWidth, canvasHeight, null, Canvas.ALL_SAVE_FLAG);
//        canvas.translate(0, titleHeight + statusAreaHeight);//平移canvas
//        canvas.drawARGB(180,163,163,163);
        //画高亮
        rectPaint.setColor(Color.parseColor("#B4A3A3A3"));
        canvas.drawRect(0, 0, canvasWidth, canvasHeight, rectPaint);
        rectPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        canvas.drawRect(rectF,rectPaint);
        rectPaint.setXfermode(null);


////        canvas.drawColor(Color.GRAY);
//        canvas.drawARGB(180,163,163,163);
//
//        canvas.drawRect(rectF,rectPaint);
////        canvas.drawARGB(180,163,163,163);
////        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
//        rectPaint.setXfermode(null);
        //画小三角
        textPaint.setColor(Color.parseColor("#FFFFFFFF"));
        Path path = new Path();
        path.moveTo(x, y);
        if (isNeedTopOrBottom){
            if (isTop){
                path.lineTo(x + triangleH, y - triangleH);
                path.lineTo(x - triangleH, y - triangleH);
            } else {
                path.lineTo(x + triangleH, y + triangleH);
                path.lineTo(x - triangleH, y + triangleH);
            }
        } else {
            if (isLeft){
                path.lineTo(x - triangleH, y - triangleH);
                path.lineTo(x - triangleH, y + triangleH);
            } else {
                path.lineTo(x + triangleH, y - triangleH);
                path.lineTo(x + triangleH, y + triangleH);
            }
        }
        path.lineTo(x, y);
        path.close();
        canvas.drawPath(path, textPaint);
        //画描述
        //设置阴影
        if (isNeedTopOrBottom){
            if (isTop){
                textPaint.setShadowLayer(10f, 15f, -15f, Color.GRAY);
            } else {
                textPaint.setShadowLayer(10f, 15f, 15f, Color.GRAY);
            }
        } else {
            if (isLeft){
                textPaint.setShadowLayer(10f, -15f, 15f, Color.GRAY);
            } else {
                textPaint.setShadowLayer(10f, 15f, 15f, Color.GRAY);
            }
        }

        canvas.drawRoundRect(textRectF, radian, radian, textPaint);
        HighlightView highlightView = viewList.get(flag);
//        canvas.drawText(highlightView.getDescription(), textRectF, textPaint);
        //真正开始画字
        String content = highlightView.getDescription();
        //TextPaint wordPaint = new TextPaint();
        //wordPaint.setAntiAlias(true);
        //wordPaint.setColor(Color.parseColor("#FF000000"));
        //wordPaint.setTextSize(textSize);
        ////超出xx字省略
        //if (content.length() > 50){
        //    content = content.substring(0,49) + "...";
        //}
        //StaticLayout sl = new StaticLayout(content, wordPaint, (int)(textRectF.right - textRectF.left) + 1, Layout.Alignment.ALIGN_NORMAL, 1.0f ,1.0f, false);
        //canvas.translate(textRectF.left, textRectF.top);//画字前将起点移动到指定区域右上角
        //sl.draw(canvas);

        TextView textView = new TextView(mContext);
        textView.setText(content);
        textView.setGravity(TEXT_ALIGNMENT_CENTER);
        textView.setSingleLine(false);
        textView.setEllipsize(TextUtils.TruncateAt.END);
        textView.setMaxEms(textNum);
        textView.setMaxLines(lineNum);
        //textView.setTextSize(textSize);
        //textView.setBackgroundColor(Color.YELLOW);
        int widthSpec = View.MeasureSpec.makeMeasureSpec((int) textRectF.width(), View.MeasureSpec.EXACTLY);
        int heightSpec = View.MeasureSpec.makeMeasureSpec((int) textRectF.height(), View.MeasureSpec.EXACTLY);
        //int widthSpec = View.MeasureSpec.makeMeasureSpec((int) textView.getWidth(), View.MeasureSpec.EXACTLY);
        //int heightSpec = View.MeasureSpec.makeMeasureSpec((int) textView.getHeight(), View.MeasureSpec.EXACTLY);
        Log.d(TAG, "onDraw: widthSpec = " + widthSpec + " heightSpec = " + heightSpec);
        //textView.measure(widthSpec, heightSpec);
        //Lay the view out at the rect width and height
        textView.layout(0, 0, (int) textRectF.width(), (int)textRectF.height());
        //textView.layout(0, 0, textView.getWidth(), textView.getHeight());

        //Translate the Canvas into position and draw it
        canvas.save();
        canvas.translate(textRectF.left, textRectF.top);
        textView.draw(canvas);
        //canvas.restore();
        canvas.restoreToCount(layerId);
    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d(TAG, "onTouchEvent: " + event.getRawX() + "," + event.getRawY());
        Log.d(TAG, "onTouchEvent: " + event.getRawX() + "," + (event.getRawY() - titleHeight - statusAreaHeight));
        if (isTouchInGuideArea(event)){
            flag = flag + 1;
            SPUtils.put(mContext, SPUtils.KEY_FLAG + "_" + activityName, flag);
            if (flag > viewList.size() - 1){
                // TODO: 2021/12/16 关闭引导层
                Log.d(TAG, "onTouchEvent: 关闭引导层 flag = " + flag);
                setVisibility(GONE);
                SPUtils.put(mContext, key, true);//将该activity是否展示过引导层置为"true"
                SPUtils.put(mContext, SPUtils.KEY_FLAG + "_" + activityName, 0);//将应当显示第几个控件标记置为0
            } else {
                setMaskRect();
            }
        } else {
            Log.d(TAG, "onTouchEvent: 未点击高亮区域");
        }
        if (getVisibility() == GONE){
            Log.d(TAG, "onTouchEvent: maskGuideView == GONE");
            return super.onTouchEvent(event);
        } else {
            return true;
        }
    }

    /**
     * 判断点击区域是否为高亮区域
     * @param event
     * @return
     */
    private boolean isTouchInGuideArea(MotionEvent event){
//        int top = viewList.get(flag).getTop();
//        int bottom = viewList.get(flag).getBottom();
//        int left = viewList.get(flag).getLeft();
//        int right = viewList.get(flag).getRight();
        HighlightView highlightView = viewList.get(flag);
        View view = highlightView.getView();
        int[] location = new int[2];
        view.getLocationInWindow(location);
        int top = location[1];
        int left = location[0];
        int bottom = top + (view.getBottom() - view.getTop());
        int right = left + (view.getRight()-view.getLeft());
        if (event.getRawX() > left
                && event.getRawX() < right
//                && (event.getRawY() - titleHeight - statusAreaHeight) > top
//                && (event.getRawY() - titleHeight - statusAreaHeight) < bottom)
            && event.getRawY() > top && event.getRawY() < bottom)
        {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取宿主activity内所有子控件合集
     * @param view
     * @return
     */
    private List<View> getAllChildViews(View view){
        List<View> allChildrenView = new ArrayList<>();
        if (view instanceof ViewGroup){
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View viewChild = viewGroup.getChildAt(i);
                allChildrenView.add(viewChild);
                allChildrenView.addAll(getAllChildViews(viewChild));
            }
        }
        return allChildrenView;
    }

    /**
     * 判断是否已经展示过引导层
     * @return
     */
    public boolean isHasShowMaskGuideView(){
        boolean result = (boolean) SPUtils.get(mContext, key, false);
        return result;
    }

    /**
     * 将引导层设置为需要展示
     */
    public void setMaskGuideViewShow(){
        Log.d(TAG, "setMaskGuideViewShow: key = " + key);
        SPUtils.put(mContext, key, false);
    }
}
