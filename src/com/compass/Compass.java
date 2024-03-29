package com.compass;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Config;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

@SuppressWarnings("deprecation")
public class Compass extends Activity {

   private static final String TAG = "Compass";

	private SensorManager mSensorManager;
   private SampleView mView;
   private float[] mValues;
   
   private final SensorListener mListener = new SensorListener() {
   
       public void onSensorChanged(int sensor, float[] values) {
           if (Config.LOGD) Log.d(TAG, "sensorChanged (" + values[0] + ", " + values[1] + ", " + values[2] + ")");
           mValues = values;
           if (mView != null) {
               mView.invalidate();
           }
       }

       public void onAccuracyChanged(int sensor, int accuracy) {
       }
   };
   
   @SuppressWarnings("unused")
@Override
   public void setContentView(View view) {
       if (false) { // set to true to test Picture
           ViewGroup vg = new PictureLayout(this);
           vg.addView(view);
           view = vg;
       }
       super.setContentView(view);
   }

   @Override
   protected void onCreate(Bundle icicle) {
       super.onCreate(icicle);
       mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
       mView = new SampleView(this);
       setContentView(mView);
       File a = new File("/sdcard/");
       try{
       File.createTempFile("test", "txt", a);
       Log.d(TAG, "createTempFile");
       }
       catch(IOException e){
    	   
       }
       
   }

   @Override
   protected void onResume()
   {
       if (Config.LOGD) Log.d(TAG, "onResume");
       super.onResume();
       mSensorManager.registerListener(mListener, 
       		SensorManager.SENSOR_ORIENTATION,
       		SensorManager.SENSOR_DELAY_GAME);
   }
   
   @Override
   protected void onStop()
   {
       if (Config.LOGD) Log.d(TAG, "onStop");
       mSensorManager.unregisterListener(mListener);
       super.onStop();
   }

   private class SampleView extends View {
       private Paint   mPaint = new Paint();
       
       private Bitmap[] mBitmapArray = new Bitmap[6];
       InputStream is;
       
       int[] mBitmapWidth = new int[6];
       int[] mBitmapHeight = new int[6];
       private Resources mRes;
       
       private boolean mAnimate;

       public SampleView(Context context) {
           super(context);
           
           mRes = Compass.this.getResources();
           
           BitmapFactory.Options opts = new BitmapFactory.Options();
           
           opts.inJustDecodeBounds = false;    // this will request the bm
           opts.inSampleSize = 2;             // scaled down by 2
           
           SetBitmapArray(0, opts, R.drawable.panel);
           SetBitmapArray(1, opts, R.drawable.needle);
           SetBitmapArray(2, opts, R.drawable.compass_degree);
       }
       
       private void SetBitmapArray(int index, BitmapFactory.Options opts, int resId){
    	   is = mRes.openRawResource(resId);
    	   mBitmapArray[index] = BitmapFactory.decodeStream(is);//300*300
           mBitmapWidth[index] = mBitmapArray[index].getWidth();
           mBitmapHeight[index] = mBitmapArray[index].getHeight();
           mBitmapArray[index+3] = BitmapFactory.decodeStream(is, null, opts);
           mBitmapWidth[index+3] = mBitmapArray[index+3].getWidth();
           mBitmapHeight[index+3] = mBitmapArray[index+3].getHeight();
       }
       
       @Override protected void onDraw(Canvas canvas) {
           Paint paint = mPaint;
           
           paint.setColor(Color.RED);

           canvas.drawColor(Color.GRAY);
           paint.setAntiAlias(true);

           int w = canvas.getWidth();
           int h = canvas.getHeight();
           int cx = w / 2;
           int cy = h / 2;
           
           int mCurrentOrientation = getResources().getConfiguration().orientation;
           if ( mCurrentOrientation == Configuration.ORIENTATION_PORTRAIT ) { 
        	   // If current screen is portrait
        	   canvas.translate(cx, cy);
        	   drawPictures(canvas,0);
           } else if ( mCurrentOrientation == Configuration.ORIENTATION_LANDSCAPE ) {
        	   //If current screen is landscape
        	   canvas.translate(cx, cy-20);
        	   drawPictures(canvas,3);
           }
       }
       
       private void drawPictures(Canvas canvas, int idDelta){
    	   if (mValues != null) {   
//            	Log.d(TAG, "mValues[0] = "+ mValues[0]);
                canvas.rotate(-mValues[0]);
                canvas.drawBitmap(mBitmapArray[0+idDelta], -mBitmapWidth[0+idDelta]/2, -mBitmapHeight[0+idDelta]/2, mPaint);
                canvas.drawBitmap(mBitmapArray[1+idDelta], -mBitmapWidth[1+idDelta]/2, -mBitmapHeight[1+idDelta]/2, mPaint);
                canvas.rotate(360+mValues[0]);
                canvas.drawBitmap(mBitmapArray[2+idDelta], -mBitmapWidth[2+idDelta]/2, -mBitmapHeight[2+idDelta]/2, mPaint);
            }
            else{
         	    canvas.drawBitmap(mBitmapArray[0+idDelta], -mBitmapWidth[0+idDelta]/2, -mBitmapHeight[0+idDelta]/2, mPaint);
                canvas.drawBitmap(mBitmapArray[1+idDelta], -mBitmapWidth[1+idDelta]/2, -mBitmapHeight[1+idDelta]/2, mPaint);
                canvas.drawBitmap(mBitmapArray[2+idDelta], -mBitmapWidth[2+idDelta]/2, -mBitmapHeight[2+idDelta]/2, mPaint);
            }
       }
       
       @Override
       protected void onAttachedToWindow() {
           setmAnimate(true);
           super.onAttachedToWindow();
       }
       
       @Override
       protected void onDetachedFromWindow() {
           setmAnimate(false);
           super.onDetachedFromWindow();
       }

	public void setmAnimate(boolean mAnimate) {
		this.mAnimate = mAnimate;
	}

	@SuppressWarnings("unused")
	public boolean ismAnimate() {
		return mAnimate;
	}
   }
}
