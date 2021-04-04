package fun.learnlife.spriteviewdemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

import androidx.constraintlayout.widget.ConstraintLayout;

public class LogoView extends ConstraintLayout implements ILogoView {

    private String TAG = "LogoView";

    private static final int ANIMATION_DURATION = 62;
    private ImageView mIdleBlinkAnimationView;

    // 下面是各个状态帧动画的图片张数
    private static final int IDLE_PICTURE_NUMBER = 72;
    private static final int IDLE_RIGHT_PICTURE_NUMBER = 72;
    private static final int IDLE_LISTEN_PICTURE_NUMBER = 25;
    private static final int KEEP_LISTENING_PICTURE_NUMBER = 48;
    private static final int LOADING_PICTURE_NUMBER = 35;
    private static final int FULL_DUPLEX_PICTURE_NUMBER = 56;
    private static final int LISTEN_PICTURE_NUMBER = 72;
    private static final int EMOJI_QUESTION_PICTURE_NUMBER = 75;
    private static final int EMOJI_QUESTION_RIGHT_PICTURE_NUMBER = 75;
    private static final int EMOJI_FAILED_PICTURE_NUMBER = 100;
    private static final int EMOJI_FAILED_RIGHT_PICTURE_NUMBER = 100;
    private static final int EMOJI_NOSIGNAL_PICTURE_NUMBER = 100;

    private static final int STEP = 1; //隔1张图片跳过一张,也就是留存图片比例的分母1/2的分子
    private static final int DENOMINATOR = 2; //留存图片比例的分母1/2的分母

    private int[] mIdleFrames;
    private int[] mIdleRightFrames;
    private int[] mIdle2ListenFrames;
    private int[] mLoadingFrames;
    private int[] mFullDuplexFrames;
    private int[] mFullDuplexRightFrames;
    private int[] mListenFrames;
    private int[] mEmojiQuestionFrames;
    private int[] mEmojiQuestionRightFrames;
    private int[] mEmojiFailedFrames;
    private int[] mEmojiFailedRightFrames;
    private int[] mEmojiNosignalFrames;
    private RenderTask mIdleRenderTask = new RenderTask(this);
    //    private RenderTask mFullDuplexRenderTask = new RenderTask(this);
    BitmapFactory.Options mOptions;
    private int mTtsFaildNum = 0;
    private View mMainView;

    public LogoView(Context context) {
        super(context);
        init(context);
    }

    public LogoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public LogoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(final Context context) {
        mMainView = LayoutInflater.from(context).inflate(R.layout.miniui_layout, this);
        mIdleBlinkAnimationView = mMainView.findViewById(R.id.idle_blink);
        mIdleRenderTask.setAnimationView(mIdleBlinkAnimationView);
    }

    @Override
    public void idle() {
        Log.d(TAG, "idle");
        playIdleAnimation(true);
    }

    private int getRealPictureNumber(int num) {
        return num / DENOMINATOR * STEP + (num % DENOMINATOR);
    }

    /**
     * 加载动画图片资源id数组
     *
     * @param frames  动画图片资源id数组
     * @param picNum  动画图片的数量
     * @param resName 图片的名字，不包含数字部分
     */
    private int[] loadAnimationFrames(int[] frames, int picNum, String resName) {
        if (frames == null) {
            // 因为由24帧抽成16帧，所以由下面这样计算抽帧后的size
            frames = new int[getRealPictureNumber(picNum)];
            int j = 0;
            for (int i = 0; i < picNum; i++) {
                if (i % DENOMINATOR == STEP) {
                    continue;
                }
                String name = i < 10 ? resName + "_0" + i : resName + "_" + i;
                frames[j] = getResources().getIdentifier(name, "drawable", getContext().getPackageName());
                j++;
            }
        }
        return frames;
    }

    private void playIdleAnimation(boolean isRepeat) {
        mIdleFrames = loadAnimationFrames(mIdleFrames, IDLE_PICTURE_NUMBER, "vr_idle");
        startAnimation(mIdleFrames, ANIMATION_DURATION, isRepeat, mIdleRenderTask);
    }


    private void playFullDuplexAnimation(boolean isRepeat) {
        mFullDuplexFrames = loadAnimationFrames(mFullDuplexFrames, FULL_DUPLEX_PICTURE_NUMBER, "vr_fullduplex");
    }

    @Override
    public void start() {
        Log.d(TAG, "start");
        playIdleAnimation(true);
    }


    @Override
    public void speakStart() {
        Log.d(TAG, "speakStart");
        mListenFrames = loadAnimationFrames(mListenFrames, LISTEN_PICTURE_NUMBER, "vr_listen");
        startAnimation(mListenFrames, ANIMATION_DURATION, true, mIdleRenderTask);
    }

    @Override
    public void loading() {
        Log.d(TAG, "loading");
        if (mLoadingFrames == null) {
            mLoadingFrames = new int[LOADING_PICTURE_NUMBER];
            for (int i = 0; i < mLoadingFrames.length; i++) {
                String name = i < 10 ? "vr_loading" + "_0" + i : "vr_loading_" + i;
                mLoadingFrames[i] = getResources().getIdentifier(name, "drawable", getContext().getPackageName());
            }
        }
        startAnimation(mLoadingFrames, ANIMATION_DURATION, true, mIdleRenderTask);
    }

    @Override
    public void ttsPlaying(int type) {
        Log.d(TAG, "ttsPlaying type: " + type);
        switch (type) {
            case ISpriteView.TTS_ASK:
                playQuestionAnimation();
                break;
            case ISpriteView.TTS_FAILED:
                if (mTtsFaildNum == 0) {
                    playQuestionAnimation();
                } else {
                    playFailAnimation();
                }
                mTtsFaildNum++;
                break;
            case ISpriteView.TTS_NOTSUPPORT:
                playFailAnimation();
                break;
            case ISpriteView.TTS_NET_ERROR:
                mEmojiNosignalFrames = loadAnimationFrames(mEmojiNosignalFrames, EMOJI_NOSIGNAL_PICTURE_NUMBER,
                        "vr_emoji_nosignal");
                startAnimation(mEmojiNosignalFrames, ANIMATION_DURATION, true, mIdleRenderTask);
                break;
            case ISpriteView.TTS_SUCCESS:
            default:
                playIdleAnimation(true);
                break;
        }
    }


    @Override
    public void startContinuousListening() {
        Log.d(TAG, "startContinuousListening");
        playFullDuplexAnimation(true);
    }

    @Override
    protected void onDetachedFromWindow() {
        mTtsFaildNum = 0;
        stopAnimation(mIdleRenderTask);
        super.onDetachedFromWindow();
    }

    public void startAnimation(int[] frameResIds, long frameDuration, boolean isRepeat, RenderTask task) {
        if (frameResIds == null || frameResIds.length == 0 || frameDuration <= 0) {
            throw new IllegalArgumentException("frame size must > 0, duration must > 0");
        }
        stopAnimation(task);
        task.isRepeat = isRepeat;
        task.mFrames = frameResIds;
        task.mOrder = 0;
        task.mDuration = frameDuration;
        post(task);
    }

    private void stopAnimation(RenderTask task) {
        if (task.mFrames == null) {
            return;
        }
        removeCallbacks(task);
        task.mFrames = null;
    }

    private BitmapFactory.Options getOptions() {
        if (mOptions == null) {
            mOptions = new BitmapFactory.Options();
            mOptions.inPreferredConfig = Bitmap.Config.RGB_565;
        }
        return mOptions;
    }

    private Bitmap decode(int resId) {
        BitmapFactory.Options options = getOptions();
        options.inJustDecodeBounds = false;
        options.inMutable = false;
        return BitmapFactory.decodeResource(getResources(), resId);
    }

    private static class RenderTask implements Runnable {

        WeakReference<LogoView> mRef;
        ImageView mView;
        AnimationCallback callback = null;
        boolean isRepeat = false;
        int[] mFrames;
        int mOrder;
        long mDuration;
        Bitmap mBitmap;

        RenderTask(LogoView view) {
            mRef = new WeakReference<>(view);
        }

        public void setAnimationView(ImageView view) {
            mView = view;
        }

        public void setOnAnimationListener(AnimationCallback callback) {
            this.callback = callback;
        }

        @Override
        public void run() {
            LogoView view = mRef != null ? mRef.get() : null;
            if (view == null || mView == null) {
                return;
            }
            if (mFrames == null) {
                return;
            }
            if (mOrder == 0 && callback != null) {
                callback.onStart();
            }
            long t = SystemClock.elapsedRealtime();
            mBitmap = view.decode(mFrames[mOrder]);
            mView.setImageBitmap(mBitmap);
//            }
            if (isRepeat) {
                mOrder = (mOrder + 1) % mFrames.length;
            } else {
                mOrder++;
                if (mOrder >= mFrames.length && callback != null) {
                    callback.onEnd();
                    return;
                }
            }
            long remain = mDuration - (SystemClock.elapsedRealtime() - t);
            remain = remain < 0 ? 0L : remain;
            mView.postDelayed(this, remain);
        }
    }

    public interface AnimationCallback {

        void onStart();

        void onEnd();
    }


    private void playQuestionAnimation() {
        mEmojiQuestionFrames = loadAnimationFrames(mEmojiQuestionFrames, EMOJI_QUESTION_PICTURE_NUMBER,
                "vr_emoji_question");
        startAnimation(mEmojiQuestionFrames, ANIMATION_DURATION, true, mIdleRenderTask);
    }

    private void playFailAnimation() {
        mEmojiFailedFrames = loadAnimationFrames(mEmojiFailedFrames, EMOJI_FAILED_PICTURE_NUMBER,
                "vr_emoji_failed");
        startAnimation(mEmojiFailedFrames, ANIMATION_DURATION, true, mIdleRenderTask);
    }

}
