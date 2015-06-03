package com.shicaigj.otto.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.shicaigj.otto.bean.ImagePiece;
import com.shicaigj.otto.imagegame.R;
import com.shicaigj.otto.utils.ImageSplitterUtil;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Otto on 2015/6/2.
 */
public class GameRelativeLayout extends RelativeLayout implements View.OnClickListener {

    private static final String TAG = "GameRelativeLayout";
    private int mColumn = 3;
    //图片的内边距
    private int mPadding;
    //两张小图片之间的间距
    private int mMargin;

    private Bitmap imageBitmap;
    private ImageView[] mGamePictureItems;
    private int mImageItemWidth;

    private List<ImagePiece> imagePieces = new ArrayList<>();

    private int mWidth;
    private boolean once = false;

    //第一次和第二次点击的item
    private ImageView mFirst;
    private ImageView mSecond;

    private int level = 1;
    private boolean isGameSuccess = false;
    private boolean isGameOver = false;
    private boolean isPause = false;

    private GameImageLister onGameImageLister;

    public void setOnGameImageLister(GameImageLister onGameImageLister) {
        this.onGameImageLister = onGameImageLister;
    }

    private boolean isTimeEnable = false;
    private int mTime;

    public void setIsTimeEnable(boolean isTimeEnable) {
        this.isTimeEnable = isTimeEnable;
    }

    public interface GameImageLister{
        void nextLevel(int level);
        void timeChanged(int currenTime);
        void gameOver();
    }

    private static final int NEXT_LEVEL = 0x110;
    private static final int TIME_CHANGED = 0x111;

    public Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case NEXT_LEVEL:
                    level = level + 1;
                    if(onGameImageLister != null){
                        onGameImageLister.nextLevel(level);
                    }else{
                        nextLevel();
                     }
                    break;
                case TIME_CHANGED:
                    if(isGameSuccess || isGameOver){
                        return;
                    }
                    if(onGameImageLister != null){
                        onGameImageLister.timeChanged(mTime);
                    }
                    if(mTime == 0){
                        isGameOver = true;
                        onGameImageLister.gameOver();
                        return;
                    }
                    mTime--;
                    mHandler.sendEmptyMessageDelayed(TIME_CHANGED,1000);
                    break;
            }
        }
    };


    public GameRelativeLayout(Context context) {
        this(context, null);
    }

    public GameRelativeLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GameRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP , 3 , getResources().getDisplayMetrics());
        mPadding = min(getPaddingBottom(), getPaddingLeft(), getPaddingRight(), getPaddingTop());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = Math.min(getMeasuredWidth(), getMeasuredHeight());

        if(!once){

            //切片图片，乱序布局
            initBitmap();

            //设置Item的属性
            initItemBitmap();
            
            //检测时间
            checkTimeEnable();

            once = true;
        }

        setMeasuredDimension(mWidth, mWidth);

    }

    private void checkTimeEnable() {
        if(isTimeEnable){
            mTime = (int) (Math.pow(2,level) * 60);
            mHandler.sendEmptyMessage(TIME_CHANGED);
        }
    }


    /**
     * 切片图片，乱序布局
     */
    private void initBitmap() {
        if(imageBitmap == null){
            imageBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.t4);
        }
        imagePieces = ImageSplitterUtil.splitImage(imageBitmap, mColumn);

        Collections.sort(imagePieces, new Comparator<ImagePiece>() {
            @Override
            public int compare(ImagePiece imagePiece, ImagePiece t1) {
                return Math.random() > 0.5 ? 1 : -1;
            }
        });
    }

    /**
     * 设置Item的属性
     */
    private void initItemBitmap() {
        mImageItemWidth = (mWidth - mPadding * 2 - mMargin * (mColumn - 1)) / mColumn;
        mGamePictureItems = new ImageView[mColumn * mColumn];
        for (int i = 0; i < mGamePictureItems.length; i++){
            ImageView item = new ImageView(getContext());
            item.setOnClickListener(this);
            item.setImageBitmap(imagePieces.get(i).getBitmap());

            mGamePictureItems[i] = item;

            item.setId(i + 1);
            item.setTag(i + "_" + imagePieces.get(i).getIndex());

            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(mImageItemWidth,mImageItemWidth);

            //设置每个item的间隙
            //不是最会一列
            if((i + 1) % mColumn != 0){
                lp.rightMargin = mMargin;
            }
            //不是第一列
            if(i % mColumn != 0){
                lp.addRule(RelativeLayout.RIGHT_OF, mGamePictureItems[i - 1].getId());
            }
            //如果不是第一行
            if((i + 1) > mColumn){
                lp.topMargin = mMargin;
                lp.addRule(RelativeLayout.BELOW, mGamePictureItems[i - mColumn].getId());
            }
            addView(item,lp);
        }
    }

    @Override
    public void onClick(View view) {

        if(isAniming){
            return;
        }
        //两次点击的同一个，则取消选择
        if(mFirst == view){
            mFirst.setColorFilter(null);
            mFirst = null;
            return;
        }
        if(mFirst == null){
            mFirst = (ImageView) view;
            mFirst.setColorFilter(Color.parseColor("#55ff0000"));
        }else {
            mSecond = (ImageView) view;
            //交换我们的Item
            exchangeItem();
        }
    }

    private RelativeLayout mAnimLayout;
    private boolean isAniming = false;
    /**
     * 交换图片
     */
    private void exchangeItem() {

        mFirst.setColorFilter(null);

        final String firstTag = (String) mFirst.getTag();
        final String secondTag = (String) mSecond.getTag();

        String[] firstParams = firstTag.split("_");
        String[] secondParams = secondTag.split("_");
        //创建动画层
        if(mAnimLayout == null){
            mAnimLayout = new RelativeLayout(getContext());
            addView(mAnimLayout);
        }

        ImageView firstImage = new ImageView(getContext());
        final Bitmap firstBitmap = imagePieces.get(Integer.parseInt(firstParams[0])).getBitmap();
        firstImage.setImageBitmap(firstBitmap);
        LayoutParams lp = new LayoutParams(mImageItemWidth,mImageItemWidth);
        lp.topMargin = mFirst.getTop() - mPadding;
        lp.leftMargin = mFirst.getLeft() - mPadding;
        firstImage.setLayoutParams(lp);
        mAnimLayout.addView(firstImage);

        ImageView secondImage = new ImageView(getContext());
        final Bitmap secondBitmap = imagePieces.get(Integer.parseInt(secondParams[0])).getBitmap();
        secondImage.setImageBitmap(secondBitmap);
        LayoutParams lp2 = new LayoutParams(mImageItemWidth,mImageItemWidth);
        lp2.topMargin = mSecond.getTop() - mPadding;
        lp2.leftMargin = mSecond.getLeft() - mPadding;
        secondImage.setLayoutParams(lp2);
        mAnimLayout.addView(secondImage);


        //第一个图片移动动画
        TranslateAnimation firstAnim = new TranslateAnimation(0,mSecond.getLeft() - mFirst.getLeft(), 0, mSecond.getTop() - mFirst.getTop());
        firstAnim.setDuration(300);
        firstAnim.setFillAfter(true);
        firstImage.setAnimation(firstAnim);

        //第二个图片移动动画
        TranslateAnimation secondAnim = new TranslateAnimation(0,mFirst.getLeft() - mSecond.getLeft(), 0, mFirst.getTop() - mSecond.getTop());
        secondAnim.setDuration(300);
        secondAnim.setFillAfter(true);
        secondImage.setAnimation(secondAnim);

        firstAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                isAniming = true;
                mFirst.setVisibility(View.INVISIBLE);
                mSecond.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mSecond.setImageBitmap(firstBitmap);
                mFirst.setImageBitmap(secondBitmap);

                mFirst.setTag(secondTag);
                mSecond.setTag(firstTag);

                mFirst.setVisibility(View.VISIBLE);
                mSecond.setVisibility(View.VISIBLE);

                mFirst = mSecond = null;
                mAnimLayout.removeAllViews();
                checkSuccess();
                isAniming = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    /**
     * 判断游戏是否成功
     */
    private void checkSuccess() {
        boolean isSuccess = true;

        for (int i = 0; i < mGamePictureItems.length; i++){
            ImageView imageView = mGamePictureItems[i];
            String imageTag = (String) imageView.getTag();
            String[] imageParams = imageTag.split("_");
            if(Integer.valueOf(imageParams[1]) != i){
                isSuccess = false;
            }
        }
        if(isSuccess){

            isGameSuccess = true;
            mHandler.removeMessages(TIME_CHANGED);
            Log.d(TAG,"GAME IS SUCCESS!!");
            mHandler.sendEmptyMessage(NEXT_LEVEL);
        }
    }


    public void restartGame(){
        isGameOver = false;
        mColumn--;
        nextLevel();
    }

    /**
     * 进入下一关
     */
    public void nextLevel(){
        this.removeAllViews();
        mAnimLayout = null;
        mColumn++;
        isGameSuccess = false;
        checkTimeEnable();
        initBitmap();
        initItemBitmap();
    }

    /**
     * 暂停游戏
     */
    public void pause(){
        isPause = true;
        mHandler.removeMessages(TIME_CHANGED);
    }

    /**
     * 从新开始
     */
    public void resume(){
        if(isPause){
            isPause = false;
            mHandler.sendEmptyMessage(TIME_CHANGED);
        }
    }

    /**
     * 获取多个参数的最小值
     * @param params
     * @return
     */
    private int min(int... params) {
        int min = params[0];
        for (int param : params){
            if(param < min){
                min = param;
            }
        }
        return min;
    }


}
