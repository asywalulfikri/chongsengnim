package sembako.sayunara.android.ui.util;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.OverScroller;
import android.widget.Scroller;

import androidx.appcompat.widget.AppCompatImageView;

import sembako.sayunara.android.R;


public class ZoomableImageView extends AppCompatImageView {

    private static final String TAG = "ImageViewZoom";

    static ColorableFrameLayout rootLayout;
    static ImageViewZoom imageViewZoom;
    static TouchImageView touchImageView;
    Context mContext;
    private ViewGroup viewGroup;

    int placeholderId;

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    String imageUrl;

    /**
     * The system "short" animation time duration, in milliseconds. This duration is ideal for
     * subtle animations or animations that occur very frequently.
     */
    private int mShortAnimationDuration;

    public ZoomableImageView(Context context) {
        super(context);
        init(null);
    }

    public ZoomableImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public ZoomableImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }


    void inflateRootLayout() {
        rootLayout = new ColorableFrameLayout(mContext);
        imageViewZoom = new ImageViewZoom(mContext);
        touchImageView = new TouchImageView(mContext);
        touchImageView.setVisibility(GONE);
        rootLayout.addView(touchImageView, -1, -1);
        rootLayout.addView(imageViewZoom, -1, -1);
        viewGroup = getRoot(getParent());
        viewGroup.addView(rootLayout);
    }

    void init(AttributeSet attrs) {

        mContext = getContext();

        mShortAnimationDuration = getResources().getInteger(android.R.integer.config_shortAnimTime);
        if (attrs != null) {
            TypedArray ta = mContext.obtainStyledAttributes(attrs, R.styleable.ZoomableImageView, 0, 0);
            try {
                mShortAnimationDuration = ta.getInteger(R.styleable.ZoomableImageView_animation_speed, mShortAnimationDuration);
                placeholderId = ta.getResourceId(R.styleable.ZoomableImageView_placeholder_id, -1);
                imageUrl = ta.getString(R.styleable.ZoomableImageView_image_url);
            } finally {
                ta.recycle();
            }
        }

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ZoomableImageView.this.getDrawable() != null) {
                    if (rootLayout == null) {
                        Log.i("zoom", "root lay not null");
                        inflateRootLayout();
                    } else if (rootLayout.getParent() != null) {

                        Log.i("zoom", "root lay parent not null");
                        inflateRootLayout();
                        ((ViewGroup) rootLayout.getParent()).removeView(rootLayout);
                        viewGroup.addView(rootLayout, -1, -1);
                    }else {

                    }

                    //   if(rootLayout.getParent() != null)
                    //       ((ViewGroup)rootLayout.getParent()).removeView(rootLayout);
                    //  viewGroup.addView(rootLayout, -1, -1);
                    imageViewZoom.zoomImageFromThumb(ZoomableImageView.this);

                    rootLayout.requestFocus(FOCUS_DOWN);
                }
            }
        });
    }

    ViewGroup getRoot(ViewParent viewParent) {
        ViewGroup viewGroup = (ViewGroup) viewParent;
        if (viewGroup.getId() == android.R.id.content) {
            Log.i("Zoom image", "yes include content");
            return viewGroup;
        }else {
            Log.i("Zoom image", "no include content");
        }

        return getRoot(viewGroup.getParent());
    }

    int background = 0;

    class ColorableFrameLayout extends FrameLayout {

        public ColorableFrameLayout(Context context) {
            super(context);
            init();
        }

        public ColorableFrameLayout(Context context, AttributeSet attrs) {
            super(context, attrs);
            init();
        }

        public ColorableFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            init();
        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        public ColorableFrameLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
            super(context, attrs, defStyleAttr, defStyleRes);
            init();
        }

        void init() {

            setFocusable(true);
            setFocusableInTouchMode(true);

        }

        void setColorAlpha(int alpha) {
            setBackgroundColor(Color.argb(alpha, Color.red(background), Color.green(background), Color.blue(background)));
        }

        @Override
        public boolean dispatchKeyEventPreIme(KeyEvent event) {
//        Log.e(TAG, "dispatchKeyEventPreIme(" + event + ")");
            if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                KeyEvent.DispatcherState state = getKeyDispatcherState();
                if (state != null) {
                    if (event.getAction() == KeyEvent.ACTION_DOWN
                            && event.getRepeatCount() == 0) {
                        state.startTracking(event, this);
                        return true;
                    } else if (event.getAction() == KeyEvent.ACTION_UP
                            && !event.isCanceled() && state.isTracking(event)) {
                        if (touchImageView.getVisibility() == VISIBLE) {
                            touchImageView.performClick();
                            return true;
                        }
                    }
                }
            }

            return super.dispatchKeyEventPreIme(event);
        }
    }

    class ImageViewZoom extends AppCompatImageView {

        /**
         * Hold a reference to the current animator, so that it can be canceled mid-way.
         */
        private Animator mCurrentAnimator;


        public ImageViewZoom(Context context) {
            super(context);
            init();
        }

        public ImageViewZoom(Context context, AttributeSet attrs) {
            super(context, attrs);
            init();
        }

        public ImageViewZoom(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            init();
        }

        void init() {
            setScaleType(ScaleType.CENTER_CROP);
        }

        public void setHeight(float f) {
            ViewGroup.LayoutParams layoutParams = getLayoutParams();
            layoutParams.height = (int) f;
            setLayoutParams(layoutParams);
            invalidate();
        }

        public void setPaddingBottom(float f) {
            setPadding(getPaddingLeft(), getPaddingTop(), getPaddingRight(), (int) f);
            invalidate();
        }

        public void setPaddingTop(float f) {
            setPadding(getPaddingLeft(), (int) f, getPaddingRight(), getPaddingBottom());
            invalidate();
        }

        public void setPaddingLeft(float f) {
            setPadding((int) f, getPaddingTop(), getPaddingRight(), getPaddingBottom());
            invalidate();
        }

        public void setPaddingRight(float f) {
            setPadding(getPaddingLeft(), getPaddingTop(), (int) f, getPaddingBottom());
            invalidate();
        }

        public void setWidth(float f) {
            ViewGroup.LayoutParams layoutParams = getLayoutParams();
            layoutParams.width = (int) f;
            setLayoutParams(layoutParams);
            invalidate();
        }

        public void zoomImageFromThumb(final ImageView imageView) {
            // If there's an animation in progress, cancel it immediately and proceed with this one.
            if (mCurrentAnimator != null) {
                mCurrentAnimator.cancel();
            }

            final int height;
            final int width;


            final Drawable drawable = imageView.getDrawable();

//            Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();

            setImageDrawable(drawable);
            // Calculate the starting and ending bounds for the zoomed-in image. This step
            // involves lots of math. Yay, math.
            final Rect startBounds = new Rect();
            final Rect finalBounds = new Rect();
            final Point globalOffset = new Point();

            // The start bounds are the global visible rectangle of the thumbnail, and the
            // final bounds are the global visible rectangle of the container view. Also
            // set the container view's offset as the origin for the bounds, since that's
            // the origin for the positioning animation properties (X, Y).
            imageView.getGlobalVisibleRect(startBounds);
            rootLayout.getGlobalVisibleRect(finalBounds, globalOffset);
            startBounds.offset(-globalOffset.x, -globalOffset.y);
            finalBounds.offset(-globalOffset.x, -globalOffset.y);

            float f = (((((float) startBounds.width()) / ((float) finalBounds.width())) * ((float) finalBounds.width())) - ((float) startBounds.width())) / 2.0f;
            startBounds.left = (int) (((float) startBounds.left) - f);
            startBounds.right = (int) (f + ((float) startBounds.right));
            f = (((((float) startBounds.height()) / ((float) finalBounds.height())) * ((float) finalBounds.height())) - ((float) startBounds.height())) / 2.0f;
            startBounds.top = (int) (((float) startBounds.top) - f);
            startBounds.bottom = (int) (f + ((float) startBounds.bottom));


            // Hide the thumbnail and show the zoomed-in view. When the animation begins,
            // it will position the zoomed-in view in the place of the thumbnail.
            setVisibility(View.VISIBLE);
            // Set the pivot point for SCALE_X and SCALE_Y transformations to the top-left corner of
            // the zoomed-in view (the default is the center of the view).
            setPivotX(0f);
            setPivotY(0f);

            if ((((float) rootLayout.getWidth()) * 1f) /
                    ((float) rootLayout.getHeight()) >
                    (((float) drawable.getIntrinsicWidth()) * 1f) /
                            ((float) drawable.getIntrinsicHeight())) {
                height = rootLayout.getHeight();
                width = (drawable.getIntrinsicWidth() * height) / drawable.getIntrinsicHeight();
            } else {
                width = rootLayout.getWidth();
                height = (drawable.getIntrinsicHeight() * width) / drawable.getIntrinsicWidth();
            }
            int top = 0;
            int bottom = 0;
            if (imageView.getHeight() > startBounds.height()) {
                if (startBounds.top == 0) {
                    top = imageView.getHeight() - startBounds.height();
                }
                if (startBounds.bottom >= finalBounds.height()) {
                    bottom = imageView.getHeight() - startBounds.height();
                }
            }

            final AnimatorSet set = new AnimatorSet();
            set
                    .play(ObjectAnimator.ofFloat(ImageViewZoom.this, View.X, startBounds.left,
                            finalBounds.left + ((rootLayout.getWidth() - width) / 2)))
                    .with(ObjectAnimator.ofFloat(ImageViewZoom.this, View.Y, startBounds.top,
                            finalBounds.top + ((rootLayout.getHeight() - height) / 2)))
                    .with(ObjectAnimator.ofFloat(ImageViewZoom.this, "width",
                            new float[]{(float) startBounds.width(), (float) width}))
                    .with(ObjectAnimator.ofFloat(ImageViewZoom.this, "height",
                            new float[]{(float) startBounds.height(), (float) height}))
                    .with(ObjectAnimator.ofFloat(ImageViewZoom.this, "paddingTop",
                            new float[]{(float) (-top), 0.0f}))
                    .with(ObjectAnimator.ofFloat(ImageViewZoom.this, "paddingBottom",
                            new float[]{(float) (-bottom), 0.0f}))
                    .with(ObjectAnimator.ofInt(rootLayout, "colorAlpha", 0, 255));

            set.setDuration(mShortAnimationDuration);
            set.setInterpolator(new DecelerateInterpolator());
            set.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    touchImageView.setImageDrawable(getDrawable());
                    touchImageView.setScaleType(ScaleType.FIT_CENTER);
                    touchImageView.resetZoom();
                    touchImageView.setVisibility(VISIBLE);
                    ImageViewZoom.this.setVisibility(GONE);
                    mCurrentAnimator = null;
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    imageView.setVisibility(VISIBLE);
                    mCurrentAnimator = null;
                }
            });
            set.start();
            imageView.setVisibility(INVISIBLE);
            mCurrentAnimator = set;

            // Upon clicking the zoomed-in image, it should zoom back down to the original bounds
            // and show the thumbnail instead of the expanded image.
//        final float startScaleFinal = startScale;
            final int finalBottom = bottom;
            final int finalTop = top;
            touchImageView.setOnClickListener(view -> {

                if (mCurrentAnimator != null) {
                    mCurrentAnimator.cancel();
                }

                touchImageView.setVisibility(GONE);
                ImageViewZoom.this.setVisibility(VISIBLE);

                AnimatorSet set1 = new AnimatorSet();

                if (!touchImageView.isZoomed()) {
                    set1.play(ObjectAnimator.ofFloat(ImageViewZoom.this, View.X, ImageViewZoom.this.getX(), (float) startBounds.left))
                            .with(ObjectAnimator.ofFloat(ImageViewZoom.this, View.Y, ImageViewZoom.this.getY(), (float) startBounds.top))
                            .with(ObjectAnimator.ofFloat(ImageViewZoom.this, "width", (float) ImageViewZoom.this.getWidth(), (float) startBounds.width()))
                            .with(ObjectAnimator.ofFloat(ImageViewZoom.this, "height", (float) ImageViewZoom.this.getHeight(), (float) startBounds.height()))
                            .with(ObjectAnimator.ofFloat(ImageViewZoom.this, "paddingTop", ImageViewZoom.this.getPaddingTop(), (float) (-finalTop)))
                            .with(ObjectAnimator.ofFloat(ImageViewZoom.this, "paddingBottom", ImageViewZoom.this.getPaddingBottom(), (float) (-finalBottom)))
                            .with(ObjectAnimator.ofInt(rootLayout, "colorAlpha", 255, 0));

                } else {

                    RectF rectF = touchImageView.getZoomedRect();

                    float top1 = -rectF.top * touchImageView.getImageHeight();
                    float bottom1 = -touchImageView.getImageHeight() + rectF.bottom * touchImageView.getImageHeight();
                    float left = -rectF.left * touchImageView.getImageWidth();
                    float right = -touchImageView.getImageWidth() + rectF.right * touchImageView.getImageWidth();

                    float height1 = touchImageView.getImageHeight();
                    float width1 = touchImageView.getImageWidth();

                    if (height1 < touchImageView.getHeight() && width1 > touchImageView.getWidth()) {
                        top1 = bottom1 = (touchImageView.getHeight() - height1) / 2;
                    }

                    if (height1 > touchImageView.getHeight() && width1 < touchImageView.getWidth()) {
                        left = right = (touchImageView.getWidth() - width1) / 2;
                    }

                    set1.play(ObjectAnimator.ofFloat(ImageViewZoom.this, View.X, Math.min(0, ImageViewZoom.this.getX()), (float) startBounds.left))
                            .with(ObjectAnimator.ofFloat(ImageViewZoom.this, View.Y, Math.min(0, ImageViewZoom.this.getY()), (float) startBounds.top))
                            .with(ObjectAnimator.ofFloat(ImageViewZoom.this, "width", touchImageView.getWidth(), (float) startBounds.width()))
                            .with(ObjectAnimator.ofFloat(ImageViewZoom.this, "height", touchImageView.getHeight(), (float) startBounds.height()))
                            .with(ObjectAnimator.ofFloat(ImageViewZoom.this, "paddingTop", top1, -finalTop))
                            .with(ObjectAnimator.ofFloat(ImageViewZoom.this, "paddingBottom", bottom1, -finalBottom))
                            .with(ObjectAnimator.ofFloat(ImageViewZoom.this, "paddingLeft", left, 0))
                            .with(ObjectAnimator.ofFloat(ImageViewZoom.this, "paddingRight", right, 0))
                            .with(ObjectAnimator.ofInt(rootLayout, "colorAlpha", 255, 0));
                }
                set1.setDuration(mShortAnimationDuration);
                set1.setInterpolator(new DecelerateInterpolator());
                set1.addListener(new AnimatorListenerAdapter() {

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        ImageViewZoom.this.setVisibility(View.GONE);
                        imageView.setVisibility(VISIBLE);
                        mCurrentAnimator = null;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        ImageViewZoom.this.setVisibility(View.GONE);
                        imageView.setVisibility(VISIBLE);
                        mCurrentAnimator = null;
                    }
                });

                imageView.setVisibility(INVISIBLE);
                ImageViewZoom.this.setVisibility(View.VISIBLE);
                set1.start();
                mCurrentAnimator = set1;
            });
        }
    }

    static class TouchImageView extends AppCompatImageView {

        private static final String DEBUG = "DEBUG";

        //
        // SuperMin and SuperMax multipliers. Determine how much the image can be
        // zoomed below or above the zoom boundaries, before animating back to the
        // min/max zoom boundary.
        //
        private static final float SUPER_MIN_MULTIPLIER = .75f;
        private static final float SUPER_MAX_MULTIPLIER = 1.25f;

        //
        // Scale of image ranges from minScale to maxScale, where minScale == 1
        // when the image is stretched to fit view.
        //
        private float normalizedScale;

        //
        // Matrix applied to image. MSCALE_X and MSCALE_Y should always be equal.
        // MTRANS_X and MTRANS_Y are the other values used. prevMatrix is the matrix
        // saved prior to the screen rotating.
        //
        private Matrix matrix, prevMatrix;

        private static enum State {NONE, DRAG, ZOOM, FLING, ANIMATE_ZOOM}

        ;
        private State state;

        private float minScale;
        private float maxScale;
        private float superMinScale;
        private float superMaxScale;
        private float[] m;

        private Context context;
        private Fling fling;

        private ScaleType mScaleType;

        private boolean imageRenderedAtLeastOnce;
        private boolean onDrawReady;

        private ZoomVariables delayedZoomVariables;

        //
        // Size of view and previous view size (ie before rotation)
        //
        private int viewWidth, viewHeight, prevViewWidth, prevViewHeight;

        //
        // Size of image when it is stretched to fit view. Before and After rotation.
        //
        private float matchViewWidth, matchViewHeight, prevMatchViewWidth, prevMatchViewHeight;

        private ScaleGestureDetector mScaleDetector;
        private GestureDetector mGestureDetector;
        private GestureDetector.OnDoubleTapListener doubleTapListener = null;
        private OnTouchListener userTouchListener = null;
        private OnTouchImageViewListener touchImageViewListener = null;

        public TouchImageView(Context context) {
            super(context);
            sharedConstructing(context);
        }

        public TouchImageView(Context context, AttributeSet attrs) {
            super(context, attrs);
            sharedConstructing(context);
        }

        public TouchImageView(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
            sharedConstructing(context);
        }

        private void sharedConstructing(Context context) {
            super.setClickable(true);
            this.context = context;
            mScaleDetector = new ScaleGestureDetector(context, new ScaleListener());
            mGestureDetector = new GestureDetector(context, new GestureListener());
            matrix = new Matrix();
            prevMatrix = new Matrix();
            m = new float[9];
            normalizedScale = 1;
            if (mScaleType == null) {
                mScaleType = ScaleType.FIT_CENTER;
            }
            minScale = 1;
            maxScale = 3;
            superMinScale = SUPER_MIN_MULTIPLIER * minScale;
            superMaxScale = SUPER_MAX_MULTIPLIER * maxScale;
            setImageMatrix(matrix);
            setScaleType(ScaleType.MATRIX);
            setState(State.NONE);
            onDrawReady = false;
            super.setOnTouchListener(new PrivateOnTouchListener());
        }

        @Override
        public void setOnTouchListener(OnTouchListener l) {
            userTouchListener = l;
        }

        public void setOnTouchImageViewListener(OnTouchImageViewListener l) {
            touchImageViewListener = l;
        }

        public void setOnDoubleTapListener(GestureDetector.OnDoubleTapListener l) {
            doubleTapListener = l;
        }

        @Override
        public void setImageResource(int resId) {
            super.setImageResource(resId);
            savePreviousImageValues();
            fitImageToView();
        }

        @Override
        public void setImageBitmap(Bitmap bm) {
            super.setImageBitmap(bm);
            savePreviousImageValues();
            fitImageToView();
        }

        @Override
        public void setImageDrawable(Drawable drawable) {
            super.setImageDrawable(drawable);
            savePreviousImageValues();
            fitImageToView();
        }

        @Override
        public void setImageURI(Uri uri) {
            super.setImageURI(uri);
            savePreviousImageValues();
            fitImageToView();
        }

        @Override
        public void setScaleType(ScaleType type) {
            if (type == ScaleType.FIT_START || type == ScaleType.FIT_END) {
                throw new UnsupportedOperationException("TouchImageView does not support FIT_START or FIT_END");
            }
            if (type == ScaleType.MATRIX) {
                super.setScaleType(ScaleType.MATRIX);

            } else {
                mScaleType = type;
                if (onDrawReady) {
                    //
                    // If the image is already rendered, scaleType has been called programmatically
                    // and the TouchImageView should be updated with the new scaleType.
                    //
                    setZoom(this);
                }
            }
        }

        @Override
        public ScaleType getScaleType() {
            return mScaleType;
        }

        /**
         * Returns false if image is in initial, unzoomed state. False, otherwise.
         *
         * @return true if image is zoomed
         */
        public boolean isZoomed() {
            return normalizedScale != 1;
        }

        /**
         * Return a Rect representing the zoomed image.
         *
         * @return rect representing zoomed image
         */
        public RectF getZoomedRect() {
            if (mScaleType == ScaleType.FIT_XY) {
                throw new UnsupportedOperationException("getZoomedRect() not supported with FIT_XY");
            }
            PointF topLeft = transformCoordTouchToBitmap(0, 0, true);
            PointF bottomRight = transformCoordTouchToBitmap(viewWidth, viewHeight, true);

            float w = getDrawable().getIntrinsicWidth();
            float h = getDrawable().getIntrinsicHeight();
            return new RectF(topLeft.x / w, topLeft.y / h, bottomRight.x / w, bottomRight.y / h);
        }

        /**
         * Save the current matrix and view dimensions
         * in the prevMatrix and prevView variables.
         */
        private void savePreviousImageValues() {
            if (matrix != null && viewHeight != 0 && viewWidth != 0) {
                matrix.getValues(m);
                prevMatrix.setValues(m);
                prevMatchViewHeight = matchViewHeight;
                prevMatchViewWidth = matchViewWidth;
                prevViewHeight = viewHeight;
                prevViewWidth = viewWidth;
            }
        }

        @Override
        public Parcelable onSaveInstanceState() {
            Bundle bundle = new Bundle();
            bundle.putParcelable("instanceState", super.onSaveInstanceState());
            bundle.putFloat("saveScale", normalizedScale);
            bundle.putFloat("matchViewHeight", matchViewHeight);
            bundle.putFloat("matchViewWidth", matchViewWidth);
            bundle.putInt("viewWidth", viewWidth);
            bundle.putInt("viewHeight", viewHeight);
            matrix.getValues(m);
            bundle.putFloatArray("matrix", m);
            bundle.putBoolean("imageRendered", imageRenderedAtLeastOnce);
            return bundle;
        }

        @Override
        public void onRestoreInstanceState(Parcelable state) {
            if (state instanceof Bundle) {
                Bundle bundle = (Bundle) state;
                normalizedScale = bundle.getFloat("saveScale");
                m = bundle.getFloatArray("matrix");
                prevMatrix.setValues(m);
                prevMatchViewHeight = bundle.getFloat("matchViewHeight");
                prevMatchViewWidth = bundle.getFloat("matchViewWidth");
                prevViewHeight = bundle.getInt("viewHeight");
                prevViewWidth = bundle.getInt("viewWidth");
                imageRenderedAtLeastOnce = bundle.getBoolean("imageRendered");
                super.onRestoreInstanceState(bundle.getParcelable("instanceState"));
                return;
            }

            super.onRestoreInstanceState(state);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            onDrawReady = true;
            imageRenderedAtLeastOnce = true;
            if (delayedZoomVariables != null) {
                setZoom(delayedZoomVariables.scale, delayedZoomVariables.focusX, delayedZoomVariables.focusY, delayedZoomVariables.scaleType);
                delayedZoomVariables = null;
            }
            super.onDraw(canvas);
        }

        @Override
        public void onConfigurationChanged(Configuration newConfig) {
            super.onConfigurationChanged(newConfig);
            savePreviousImageValues();
        }

        /**
         * Get the max zoom multiplier.
         *
         * @return max zoom multiplier.
         */
        public float getMaxZoom() {
            return maxScale;
        }

        /**
         * Set the max zoom multiplier. Default valueApp: 3.
         *
         * @param max max zoom multiplier.
         */
        public void setMaxZoom(float max) {
            maxScale = max;
            superMaxScale = SUPER_MAX_MULTIPLIER * maxScale;
        }

        /**
         * Get the min zoom multiplier.
         *
         * @return min zoom multiplier.
         */
        public float getMinZoom() {
            return minScale;
        }

        /**
         * Get the current zoom. This is the zoom relative to the initial
         * scale, not the original resource.
         *
         * @return current zoom multiplier.
         */
        public float getCurrentZoom() {
            return normalizedScale;
        }

        /**
         * Set the min zoom multiplier. Default valueApp: 1.
         *
         * @param min min zoom multiplier.
         */
        public void setMinZoom(float min) {
            minScale = min;
            superMinScale = SUPER_MIN_MULTIPLIER * minScale;
        }

        /**
         * Reset zoom and translation to initial state.
         */
        public void resetZoom() {
            normalizedScale = 1;
            fitImageToView();
        }

        /**
         * Set zoom to the specified scale. Image will be centered by default.
         *
         * @param scale
         */
        public void setZoom(float scale) {
            setZoom(scale, 0.5f, 0.5f);
        }

        /**
         * Set zoom to the specified scale. Image will be centered around the point
         * (focusX, focusY). These floats range from 0 to 1 and denote the focus point
         * as a fraction from the left and top of the view. For example, the top left
         * corner of the image would be (0, 0). And the bottom right corner would be (1, 1).
         *
         * @param scale
         * @param focusX
         * @param focusY
         */
        public void setZoom(float scale, float focusX, float focusY) {
            setZoom(scale, focusX, focusY, mScaleType);
        }

        /**
         * Set zoom to the specified scale. Image will be centered around the point
         * (focusX, focusY). These floats range from 0 to 1 and denote the focus point
         * as a fraction from the left and top of the view. For example, the top left
         * corner of the image would be (0, 0). And the bottom right corner would be (1, 1).
         *
         * @param scale
         * @param focusX
         * @param focusY
         * @param scaleType
         */
        public void setZoom(float scale, float focusX, float focusY, ScaleType scaleType) {
            //
            // setZoom can be called before the image is on the screen, but at this point,
            // image and view sizes have not yet been calculated in onMeasure. Thus, we should
            // delay calling setZoom until the view has been measured.
            //
            if (!onDrawReady) {
                delayedZoomVariables = new ZoomVariables(scale, focusX, focusY, scaleType);
                return;
            }

            if (scaleType != mScaleType) {
                setScaleType(scaleType);
            }
            resetZoom();
            scaleImage(scale, viewWidth / 2, viewHeight / 2, true);
            matrix.getValues(m);
            m[Matrix.MTRANS_X] = -((focusX * getImageWidth()) - (viewWidth * 0.5f));
            m[Matrix.MTRANS_Y] = -((focusY * getImageHeight()) - (viewHeight * 0.5f));
            matrix.setValues(m);
            fixTrans();
            setImageMatrix(matrix);
        }

        public void setZoom(TouchImageView img) {
            PointF center = img.getScrollPosition();
            setZoom(img.getCurrentZoom(), center.x, center.y, img.getScaleType());
        }

        /**
         * Return the point at the center of the zoomed image. The PointF coordinates range
         * in valueApp between 0 and 1 and the focus point is denoted as a fraction from the left
         * and top of the view. For example, the top left corner of the image would be (0, 0).
         * And the bottom right corner would be (1, 1).
         *
         * @return PointF representing the scroll position of the zoomed image.
         */
        public PointF getScrollPosition() {
            Drawable drawable = getDrawable();
            if (drawable == null) {
                return null;
            }
            int drawableWidth = drawable.getIntrinsicWidth();
            int drawableHeight = drawable.getIntrinsicHeight();

            PointF point = transformCoordTouchToBitmap(viewWidth / 2, viewHeight / 2, true);
            point.x /= drawableWidth;
            point.y /= drawableHeight;
            return point;
        }

        /**
         * Set the focus point of the zoomed image. The focus points are denoted as a fraction from the
         * left and top of the view. The focus points can range in valueApp between 0 and 1.
         *
         * @param focusX
         * @param focusY
         */
        public void setScrollPosition(float focusX, float focusY) {
            setZoom(normalizedScale, focusX, focusY);
        }

        /**
         * Performs boundary checking and fixes the image matrix if it
         * is out of bounds.
         */
        private void fixTrans() {
            matrix.getValues(m);
            float transX = m[Matrix.MTRANS_X];
            float transY = m[Matrix.MTRANS_Y];

            float fixTransX = getFixTrans(transX, viewWidth, getImageWidth());
            float fixTransY = getFixTrans(transY, viewHeight, getImageHeight());

            if (fixTransX != 0 || fixTransY != 0) {
                matrix.postTranslate(fixTransX, fixTransY);
            }
        }

        /**
         * When transitioning from zooming from focus to zoom from center (or vice versa)
         * the image can become unaligned within the view. This is apparent when zooming
         * quickly. When the content size is less than the view size, the content will often
         * be centered incorrectly within the view. fixScaleTrans first calls fixTrans() and
         * then makes sure the image is centered correctly within the view.
         */
        private void fixScaleTrans() {
            fixTrans();
            matrix.getValues(m);
            if (getImageWidth() < viewWidth) {
                m[Matrix.MTRANS_X] = (viewWidth - getImageWidth()) / 2;
            }

            if (getImageHeight() < viewHeight) {
                m[Matrix.MTRANS_Y] = (viewHeight - getImageHeight()) / 2;
            }
            matrix.setValues(m);
        }

        private float getFixTrans(float trans, float viewSize, float contentSize) {
            float minTrans, maxTrans;

            if (contentSize <= viewSize) {
                minTrans = 0;
                maxTrans = viewSize - contentSize;

            } else {
                minTrans = viewSize - contentSize;
                maxTrans = 0;
            }

            if (trans < minTrans)
                return -trans + minTrans;
            if (trans > maxTrans)
                return -trans + maxTrans;
            return 0;
        }

        private float getFixDragTrans(float delta, float viewSize, float contentSize) {
            if (contentSize <= viewSize) {
                return 0;
            }
            return delta;
        }

        private float getImageWidth() {
            return matchViewWidth * normalizedScale;
        }

        private float getImageHeight() {
            return matchViewHeight * normalizedScale;
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            Drawable drawable = getDrawable();
            if (drawable == null || drawable.getIntrinsicWidth() == 0 || drawable.getIntrinsicHeight() == 0) {
                setMeasuredDimension(0, 0);
                return;
            }

            int drawableWidth = drawable.getIntrinsicWidth();
            int drawableHeight = drawable.getIntrinsicHeight();
            int widthSize = MeasureSpec.getSize(widthMeasureSpec);
            int widthMode = MeasureSpec.getMode(widthMeasureSpec);
            int heightSize = MeasureSpec.getSize(heightMeasureSpec);
            int heightMode = MeasureSpec.getMode(heightMeasureSpec);
            viewWidth = setViewSize(widthMode, widthSize, drawableWidth);
            viewHeight = setViewSize(heightMode, heightSize, drawableHeight);

            //
            // Set view dimensions
            //
            setMeasuredDimension(viewWidth, viewHeight);

            //
            // Fit content within view
            //
            fitImageToView();
        }

        /**
         * If the normalizedScale is equal to 1, then the image is made to fit the screen. Otherwise,
         * it is made to fit the screen according to the dimensions of the previous image matrix. This
         * allows the image to maintain its zoom after rotation.
         */
        private void fitImageToView() {
            Drawable drawable = getDrawable();
            if (drawable == null || drawable.getIntrinsicWidth() == 0 || drawable.getIntrinsicHeight() == 0) {
                return;
            }
            if (matrix == null || prevMatrix == null) {
                return;
            }

            int drawableWidth = drawable.getIntrinsicWidth();
            int drawableHeight = drawable.getIntrinsicHeight();

            //
            // Scale image for view
            //
            float scaleX = (float) viewWidth / drawableWidth;
            float scaleY = (float) viewHeight / drawableHeight;

            switch (mScaleType) {
                case CENTER:
                    scaleX = scaleY = 1;
                    break;

                case CENTER_CROP:
                    scaleX = scaleY = Math.max(scaleX, scaleY);
                    break;

                case CENTER_INSIDE:
                    scaleX = scaleY = Math.min(1, Math.min(scaleX, scaleY));

                case FIT_CENTER:
                    scaleX = scaleY = Math.min(scaleX, scaleY);
                    break;

                case FIT_XY:
                    break;

                default:
                    //
                    // FIT_START and FIT_END not supported
                    //
                    throw new UnsupportedOperationException("TouchImageView does not support FIT_START or FIT_END");

            }

            //
            // Center the image
            //
            float redundantXSpace = viewWidth - (scaleX * drawableWidth);
            float redundantYSpace = viewHeight - (scaleY * drawableHeight);
            matchViewWidth = viewWidth - redundantXSpace;
            matchViewHeight = viewHeight - redundantYSpace;
            if (!isZoomed() && !imageRenderedAtLeastOnce) {
                //
                // Stretch and center image to fit view
                //
                matrix.setScale(scaleX, scaleY);
                matrix.postTranslate(redundantXSpace / 2, redundantYSpace / 2);
                normalizedScale = 1;

            } else {
                //
                // These values should never be 0 or we will set viewWidth and viewHeight
                // to NaN in translateMatrixAfterRotate. To avoid this, call savePreviousImageValues
                // to set them equal to the current values.
                //
                if (prevMatchViewWidth == 0 || prevMatchViewHeight == 0) {
                    savePreviousImageValues();
                }

                prevMatrix.getValues(m);

                //
                // Rescale Matrix after rotation
                //
                m[Matrix.MSCALE_X] = matchViewWidth / drawableWidth * normalizedScale;
                m[Matrix.MSCALE_Y] = matchViewHeight / drawableHeight * normalizedScale;

                //
                // TransX and TransY from previous matrix
                //
                float transX = m[Matrix.MTRANS_X];
                float transY = m[Matrix.MTRANS_Y];

                //
                // Width
                //
                float prevActualWidth = prevMatchViewWidth * normalizedScale;
                float actualWidth = getImageWidth();
                translateMatrixAfterRotate(Matrix.MTRANS_X, transX, prevActualWidth, actualWidth, prevViewWidth, viewWidth, drawableWidth);

                //
                // Height
                //
                float prevActualHeight = prevMatchViewHeight * normalizedScale;
                float actualHeight = getImageHeight();
                translateMatrixAfterRotate(Matrix.MTRANS_Y, transY, prevActualHeight, actualHeight, prevViewHeight, viewHeight, drawableHeight);

                //
                // Set the matrix to the adjusted scale and translate values.
                //
                matrix.setValues(m);
            }
            fixTrans();
            setImageMatrix(matrix);
        }

        /**
         * Set view dimensions based on layout params
         *
         * @param mode
         * @param size
         * @param drawableWidth
         * @return
         */
        private int setViewSize(int mode, int size, int drawableWidth) {
            int viewSize;
            switch (mode) {
                case MeasureSpec.EXACTLY:
                    viewSize = size;
                    break;

                case MeasureSpec.AT_MOST:
                    viewSize = Math.min(drawableWidth, size);
                    break;

                case MeasureSpec.UNSPECIFIED:
                    viewSize = drawableWidth;
                    break;

                default:
                    viewSize = size;
                    break;
            }
            return viewSize;
        }

        /**
         * After rotating, the matrix needs to be translated. This function finds the area of image
         * which was previously centered and adjusts translations so that is again the center, post-rotation.
         *
         * @param axis          Matrix.MTRANS_X or Matrix.MTRANS_Y
         * @param trans         the valueApp of trans in that axis before the rotation
         * @param prevImageSize the width/height of the image before the rotation
         * @param imageSize     width/height of the image after rotation
         * @param prevViewSize  width/height of view before rotation
         * @param viewSize      width/height of view after rotation
         * @param drawableSize  width/height of drawable
         */
        private void translateMatrixAfterRotate(int axis, float trans, float prevImageSize, float imageSize, int prevViewSize, int viewSize, int drawableSize) {
            if (imageSize < viewSize) {
                //
                // The width/height of image is less than the view's width/height. Center it.
                //
                m[axis] = (viewSize - (drawableSize * m[Matrix.MSCALE_X])) * 0.5f;

            } else if (trans > 0) {
                //
                // The image is larger than the view, but was not before rotation. Center it.
                //
                m[axis] = -((imageSize - viewSize) * 0.5f);

            } else {
                //
                // Find the area of the image which was previously centered in the view. Determine its distance
                // from the left/top side of the view as a fraction of the entire image's width/height. Use that percentage
                // to calculate the trans in the new view width/height.
                //
                float percentage = (Math.abs(trans) + (0.5f * prevViewSize)) / prevImageSize;
                m[axis] = -((percentage * imageSize) - (viewSize * 0.5f));
            }
        }

        private void setState(State state) {
            this.state = state;
        }

        public boolean canScrollHorizontallyFroyo(int direction) {
            return canScrollHorizontally(direction);
        }

        @Override
        public boolean canScrollHorizontally(int direction) {
            matrix.getValues(m);
            float x = m[Matrix.MTRANS_X];

            if (getImageWidth() < viewWidth) {
                return false;

            } else if (x >= -1 && direction < 0) {
                return false;

            } else if (Math.abs(x) + viewWidth + 1 >= getImageWidth() && direction > 0) {
                return false;
            }

            return true;
        }

        /**
         * Gesture Listener detects a single click or long click and passes that on
         * to the view's listener.
         *
         * @author Ortiz
         */
        private class GestureListener extends GestureDetector.SimpleOnGestureListener {

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if (doubleTapListener != null) {
                    return doubleTapListener.onSingleTapConfirmed(e);
                }
                return performClick();
            }

            @Override
            public void onLongPress(MotionEvent e) {
                performLongClick();
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (fling != null) {
                    //
                    // If a previous fling is still active, it should be cancelled so that two flings
                    // are not run simultaenously.
                    //
                    fling.cancelFling();
                }
                fling = new Fling((int) velocityX, (int) velocityY);
                compatPostOnAnimation(fling);
                return super.onFling(e1, e2, velocityX, velocityY);
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                boolean consumed = false;
                if (doubleTapListener != null) {
                    consumed = doubleTapListener.onDoubleTap(e);
                }
                if (state == State.NONE) {
                    float targetZoom = (normalizedScale == minScale) ? maxScale : minScale;
                    DoubleTapZoom doubleTap = new DoubleTapZoom(targetZoom, e.getX(), e.getY(), false);
                    compatPostOnAnimation(doubleTap);
                    consumed = true;
                }
                return consumed;
            }

            @Override
            public boolean onDoubleTapEvent(MotionEvent e) {
                if (doubleTapListener != null) {
                    return doubleTapListener.onDoubleTapEvent(e);
                }
                return false;
            }
        }

        public interface OnTouchImageViewListener {
            public void onMove();
        }

        /**
         * Responsible for all touch events. Handles the heavy lifting of drag and also sends
         * touch events to Scale Detector and Gesture Detector.
         *
         * @author Ortiz
         */
        private class PrivateOnTouchListener implements OnTouchListener {

            //
            // Remember last point position for dragging
            //
            private PointF last = new PointF();

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                mScaleDetector.onTouchEvent(event);
                mGestureDetector.onTouchEvent(event);
                PointF curr = new PointF(event.getX(), event.getY());

                if (state == State.NONE || state == State.DRAG || state == State.FLING) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            last.set(curr);
                            if (fling != null)
                                fling.cancelFling();
                            setState(State.DRAG);
                            break;

                        case MotionEvent.ACTION_MOVE:
                            if (state == State.DRAG) {
                                float deltaX = curr.x - last.x;
                                float deltaY = curr.y - last.y;
                                float fixTransX = getFixDragTrans(deltaX, viewWidth, getImageWidth());
                                float fixTransY = getFixDragTrans(deltaY, viewHeight, getImageHeight());
                                matrix.postTranslate(fixTransX, fixTransY);
                                fixTrans();
                                last.set(curr.x, curr.y);
                            }
                            break;

                        case MotionEvent.ACTION_UP:
                        case MotionEvent.ACTION_POINTER_UP:
                            setState(State.NONE);
                            break;
                    }
                }

                setImageMatrix(matrix);

                //
                // User-defined OnTouchListener
                //
                if (userTouchListener != null) {
                    userTouchListener.onTouch(v, event);
                }

                //
                // OnTouchImageViewListener is set: TouchImageView dragged by user.
                //
                if (touchImageViewListener != null) {
                    touchImageViewListener.onMove();
                }

                //
                // indicate event was handled
                //
                return true;
            }
        }

        /**
         * ScaleListener detects user two finger scaling and scales image.
         *
         * @author Ortiz
         */
        private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
            @Override
            public boolean onScaleBegin(ScaleGestureDetector detector) {
                setState(State.ZOOM);
                return true;
            }

            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                scaleImage(detector.getScaleFactor(), detector.getFocusX(), detector.getFocusY(), true);

                //
                // OnTouchImageViewListener is set: TouchImageView pinch zoomed by user.
                //
                if (touchImageViewListener != null) {
                    touchImageViewListener.onMove();
                }
                return true;
            }

            @Override
            public void onScaleEnd(ScaleGestureDetector detector) {
                super.onScaleEnd(detector);
                setState(State.NONE);
                boolean animateToZoomBoundary = false;
                float targetZoom = normalizedScale;
                if (normalizedScale > maxScale) {
                    targetZoom = maxScale;
                    animateToZoomBoundary = true;

                } else if (normalizedScale < minScale) {
                    targetZoom = minScale;
                    animateToZoomBoundary = true;
                }

                if (animateToZoomBoundary) {
                    DoubleTapZoom doubleTap = new DoubleTapZoom(targetZoom, viewWidth / 2, viewHeight / 2, true);
                    compatPostOnAnimation(doubleTap);
                }
            }
        }

        private void scaleImage(double deltaScale, float focusX, float focusY, boolean stretchImageToSuper) {

            float lowerScale, upperScale;
            if (stretchImageToSuper) {
                lowerScale = superMinScale;
                upperScale = superMaxScale;

            } else {
                lowerScale = minScale;
                upperScale = maxScale;
            }

            float origScale = normalizedScale;
            normalizedScale *= deltaScale;
            if (normalizedScale > upperScale) {
                normalizedScale = upperScale;
                deltaScale = upperScale / origScale;
            } else if (normalizedScale < lowerScale) {
                normalizedScale = lowerScale;
                deltaScale = lowerScale / origScale;
            }

            matrix.postScale((float) deltaScale, (float) deltaScale, focusX, focusY);
            fixScaleTrans();
        }

        /**
         * DoubleTapZoom calls a series of runnables which apply
         * an animated zoom in/out graphic to the image.
         *
         * @author Ortiz
         */
        private class DoubleTapZoom implements Runnable {

            private long startTime;
            private static final float ZOOM_TIME = 500;
            private float startZoom, targetZoom;
            private float bitmapX, bitmapY;
            private boolean stretchImageToSuper;
            private AccelerateDecelerateInterpolator interpolator = new AccelerateDecelerateInterpolator();
            private PointF startTouch;
            private PointF endTouch;

            DoubleTapZoom(float targetZoom, float focusX, float focusY, boolean stretchImageToSuper) {
                setState(State.ANIMATE_ZOOM);
                startTime = System.currentTimeMillis();
                this.startZoom = normalizedScale;
                this.targetZoom = targetZoom;
                this.stretchImageToSuper = stretchImageToSuper;
                PointF bitmapPoint = transformCoordTouchToBitmap(focusX, focusY, false);
                this.bitmapX = bitmapPoint.x;
                this.bitmapY = bitmapPoint.y;

                //
                // Used for translating image during scaling
                //
                startTouch = transformCoordBitmapToTouch(bitmapX, bitmapY);
                endTouch = new PointF(viewWidth / 2, viewHeight / 2);
            }

            @Override
            public void run() {
                float t = interpolate();
                double deltaScale = calculateDeltaScale(t);
                scaleImage(deltaScale, bitmapX, bitmapY, stretchImageToSuper);
                translateImageToCenterTouchPosition(t);
                fixScaleTrans();
                setImageMatrix(matrix);

                //
                // OnTouchImageViewListener is set: double tap runnable updates listener
                // with every frame.
                //
                if (touchImageViewListener != null) {
                    touchImageViewListener.onMove();
                }

                if (t < 1f) {
                    //
                    // We haven't finished zooming
                    //
                    compatPostOnAnimation(this);

                } else {
                    //
                    // Finished zooming
                    //
                    setState(State.NONE);
                }
            }

            /**
             * Interpolate between where the image should start and end in order to translate
             * the image so that the point that is touched is what ends up centered at the end
             * of the zoom.
             *
             * @param t
             */
            private void translateImageToCenterTouchPosition(float t) {
                float targetX = startTouch.x + t * (endTouch.x - startTouch.x);
                float targetY = startTouch.y + t * (endTouch.y - startTouch.y);
                PointF curr = transformCoordBitmapToTouch(bitmapX, bitmapY);
                matrix.postTranslate(targetX - curr.x, targetY - curr.y);
            }

            /**
             * Use interpolator to get t
             *
             * @return
             */
            private float interpolate() {
                long currTime = System.currentTimeMillis();
                float elapsed = (currTime - startTime) / ZOOM_TIME;
                elapsed = Math.min(1f, elapsed);
                return interpolator.getInterpolation(elapsed);
            }

            /**
             * Interpolate the current targeted zoom and get the delta
             * from the current zoom.
             *
             * @param t
             * @return
             */
            private double calculateDeltaScale(float t) {
                double zoom = startZoom + t * (targetZoom - startZoom);
                return zoom / normalizedScale;
            }
        }

        /**
         * This function will transform the coordinates in the touch event to the coordinate
         * system of the drawable that the imageview contain
         *
         * @param x            x-coordinate of touch event
         * @param y            y-coordinate of touch event
         * @param clipToBitmap Touch event may occur within view, but outside image content. True, to clip return valueApp
         *                     to the bounds of the bitmap size.
         * @return Coordinates of the point touched, in the coordinate system of the original drawable.
         */
        private PointF transformCoordTouchToBitmap(float x, float y, boolean clipToBitmap) {
            matrix.getValues(m);
            float origW = getDrawable().getIntrinsicWidth();
            float origH = getDrawable().getIntrinsicHeight();
            float transX = m[Matrix.MTRANS_X];
            float transY = m[Matrix.MTRANS_Y];
            float finalX = ((x - transX) * origW) / getImageWidth();
            float finalY = ((y - transY) * origH) / getImageHeight();

            if (clipToBitmap) {
                finalX = Math.min(Math.max(finalX, 0), origW);
                finalY = Math.min(Math.max(finalY, 0), origH);
            }

            return new PointF(finalX, finalY);
        }

        /**
         * Inverse of transformCoordTouchToBitmap. This function will transform the coordinates in the
         * drawable's coordinate system to the view's coordinate system.
         *
         * @param bx x-coordinate in original bitmap coordinate system
         * @param by y-coordinate in original bitmap coordinate system
         * @return Coordinates of the point in the view's coordinate system.
         */
        private PointF transformCoordBitmapToTouch(float bx, float by) {
            matrix.getValues(m);
            float origW = getDrawable().getIntrinsicWidth();
            float origH = getDrawable().getIntrinsicHeight();
            float px = bx / origW;
            float py = by / origH;
            float finalX = m[Matrix.MTRANS_X] + getImageWidth() * px;
            float finalY = m[Matrix.MTRANS_Y] + getImageHeight() * py;
            return new PointF(finalX, finalY);
        }

        /**
         * Fling launches sequential runnables which apply
         * the fling graphic to the image. The values for the translation
         * are interpolated by the Scroller.
         *
         * @author Ortiz
         */
        private class Fling implements Runnable {

            CompatScroller scroller;
            int currX, currY;

            Fling(int velocityX, int velocityY) {
                setState(State.FLING);
                scroller = new CompatScroller(context);
                matrix.getValues(m);

                int startX = (int) m[Matrix.MTRANS_X];
                int startY = (int) m[Matrix.MTRANS_Y];
                int minX, maxX, minY, maxY;

                if (getImageWidth() > viewWidth) {
                    minX = viewWidth - (int) getImageWidth();
                    maxX = 0;

                } else {
                    minX = maxX = startX;
                }

                if (getImageHeight() > viewHeight) {
                    minY = viewHeight - (int) getImageHeight();
                    maxY = 0;

                } else {
                    minY = maxY = startY;
                }

                scroller.fling(startX, startY, (int) velocityX, (int) velocityY, minX,
                        maxX, minY, maxY);
                currX = startX;
                currY = startY;
            }

            public void cancelFling() {
                if (scroller != null) {
                    setState(State.NONE);
                    scroller.forceFinished(true);
                }
            }

            @Override
            public void run() {

                //
                // OnTouchImageViewListener is set: TouchImageView listener has been flung by user.
                // Listener runnable updated with each frame of fling animation.
                //
                if (touchImageViewListener != null) {
                    touchImageViewListener.onMove();
                }

                if (scroller.isFinished()) {
                    scroller = null;
                    return;
                }

                if (scroller.computeScrollOffset()) {
                    int newX = scroller.getCurrX();
                    int newY = scroller.getCurrY();
                    int transX = newX - currX;
                    int transY = newY - currY;
                    currX = newX;
                    currY = newY;
                    matrix.postTranslate(transX, transY);
                    fixTrans();
                    setImageMatrix(matrix);
                    compatPostOnAnimation(this);
                }
            }
        }

        private class CompatScroller {
            Scroller scroller;
            OverScroller overScroller;
            boolean isPreGingerbread;

            public CompatScroller(Context context) {

                isPreGingerbread = false;
                overScroller = new OverScroller(context);

            }

            public void fling(int startX, int startY, int velocityX, int velocityY, int minX, int maxX, int minY, int maxY) {
                if (isPreGingerbread) {
                    scroller.fling(startX, startY, velocityX, velocityY, minX, maxX, minY, maxY);
                } else {
                    overScroller.fling(startX, startY, velocityX, velocityY, minX, maxX, minY, maxY);
                }
            }

            public void forceFinished(boolean finished) {
                if (isPreGingerbread) {
                    scroller.forceFinished(finished);
                } else {
                    overScroller.forceFinished(finished);
                }
            }

            public boolean isFinished() {
                if (isPreGingerbread) {
                    return scroller.isFinished();
                } else {
                    return overScroller.isFinished();
                }
            }

            public boolean computeScrollOffset() {
                if (isPreGingerbread) {
                    return scroller.computeScrollOffset();
                } else {
                    overScroller.computeScrollOffset();
                    return overScroller.computeScrollOffset();
                }
            }

            public int getCurrX() {
                if (isPreGingerbread) {
                    return scroller.getCurrX();
                } else {
                    return overScroller.getCurrX();
                }
            }

            public int getCurrY() {
                if (isPreGingerbread) {
                    return scroller.getCurrY();
                } else {
                    return overScroller.getCurrY();
                }
            }
        }

        private void compatPostOnAnimation(Runnable runnable) {

            postDelayed(runnable, 1000 / 60);

        }

        private class ZoomVariables {
            public float scale;
            public float focusX;
            public float focusY;
            public ScaleType scaleType;

            public ZoomVariables(float scale, float focusX, float focusY, ScaleType scaleType) {
                this.scale = scale;
                this.focusX = focusX;
                this.focusY = focusY;
                this.scaleType = scaleType;
            }
        }

        private void printMatrixInfo() {
            float[] n = new float[9];
            matrix.getValues(n);
        }
    }
}

