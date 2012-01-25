package com.brotherstuck.equalizer;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.audiofx.Visualizer;
import android.view.View;

/**
 * A simple class that draws waveform data received from a
 * {@link Visualizer.OnDataCaptureListener#onWaveFormDataCapture }
 */
class VisualizerView extends View {
    private byte[] mBytes;
    private float[] mPoints;
    private Rect mRect = new Rect();
    private float magnitude;
    private int mDivisions;

    private Paint mForePaint = new Paint();

    public VisualizerView(Context context) {
        super(context);
        init();
    }

    private void init() {
        mBytes = null;
        mDivisions = 10;

        mForePaint.setStrokeWidth(28f);
        mForePaint.setAntiAlias(true);
        mForePaint.setColor(Color.rgb(0, 185, 255));
    }

    public void updateVisualizer(byte[] bytes) {
        mBytes = bytes;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mBytes == null) {
            return;
        }

        if (mPoints == null || mPoints.length < mBytes.length/mDivisions * 4) {
            mPoints = new float[mBytes.length /mDivisions * 4];
        }

        mRect.set(0, 0, getWidth(), getHeight());

        for (int i = 0; i < mBytes.length/mDivisions; i++) {
//            mPoints[i * 4] = (i-1)*getWidth()/(mBytes.length/2);
//            mPoints[i * 4 + 1] = 0;
//            mPoints[i * 4 + 2] = (i-1)*getWidth()/(mBytes.length/2);
//            byte rfk = mBytes[2*i];
//            byte ifk = mBytes[2*i+1];
//            magnitude = (float) (rfk * rfk + ifk * ifk);
//            int dbValue = (int) (70*Math.log10(magnitude));
//            mPoints[i * 4 + 3]=(float)(dbValue*getHeight()/350);    
        	mPoints[i*4] = i*4*mDivisions;
        	mPoints[i*4+2] = i*4*mDivisions;
        	byte rfk = mBytes[mDivisions * i];
        	byte ifk = mBytes[mDivisions * i +1];
        	magnitude = (rfk*rfk + ifk*ifk);
        	int dbValue = (int)(10*Math.log10(magnitude));
        	
        	mPoints[i*4+1]=getHeight();
        	mPoints[i*4+3]=getHeight() - (dbValue*4-10);
        }

        canvas.drawLines(mPoints, mForePaint);
    }
}
