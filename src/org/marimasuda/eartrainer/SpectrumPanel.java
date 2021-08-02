package org.marimasuda.eartrainer;

import java.text.DecimalFormat;

import org.marimasuda.eartrainer.RealPitches.PitchFreqAndNoteName;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class SpectrumPanel extends SurfaceView implements SurfaceHolder.Callback {
    private boolean isSurfaceCreated;
    private int mWidth;
    private int mHeight;
    private SurfaceHolder mSurfaceHolder;
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.00");
    private static final int MARK_SIZE = 7;
    private static final int SCALE_FACTOR = 4;

    public SpectrumPanel(Context context, AttributeSet attrs) {
	super(context, attrs);
	mSurfaceHolder = getHolder();
	mSurfaceHolder.addCallback(this);
    }

    public void drawSpectrum(double[] signal, double maxFFTSample, double peakFrequency) {
	if (isSurfaceCreated) {
	    Canvas canvas = null;
	    try {
		canvas = mSurfaceHolder.lockCanvas(null);
		synchronized (mSurfaceHolder) {
		    canvas.drawColor(Color.BLACK);
		    drawMarks(canvas, AudioListener.SAMPLE_RATE);
		    drawFFTSignal(canvas, signal, maxFFTSample);
		    drawPeakFrequency(canvas, peakFrequency);
		    drawClosestRealNote(canvas, peakFrequency);
		}
	    } catch (Exception e) {
		Log.e("ERROR", e.toString());
	    } finally {
		if (canvas != null) {
		    mSurfaceHolder.unlockCanvasAndPost(canvas);
		}
	    }
    	}
    }

    private void drawMarks(Canvas canvas, int sampleRate) {
	int interval = 100;
	String units = "Hz";
	Paint p1 = new Paint();
	p1.setColor(Color.DKGRAY);
	Paint p2 = new Paint();
	p2.setColor(Color.GRAY);
	for (int freq = 0; freq < sampleRate / SCALE_FACTOR; freq += interval) {
	    int point = (mWidth * freq) / (sampleRate / SCALE_FACTOR);
	    canvas.drawLine(point, 0, point, mHeight - MARK_SIZE, p1);
	    canvas.drawText(freq + units, point, 10, p2);
	}
    }

    private void drawFFTSignal(Canvas canvas, double[] signal, double maxFFTSample) {
	int sampleValue;
	int pos;
	Paint p = new Paint();
	p.setColor(Color.WHITE);
	for(int i = 0; i < (signal.length - 1); i++) {
	    sampleValue = (int)(300*(signal[i]/maxFFTSample));
	    pos = mWidth * i * SCALE_FACTOR / AudioListener.NUM_FFT_SAMPLES;
	    canvas.drawLine(pos, mHeight-2, pos, mHeight - 2 - sampleValue, p);
	}
    }

    private void drawPeakFrequency(Canvas canvas, double peakFrequency) {
	Paint p = new Paint();
	p.setColor(Color.RED);
	p.setTextSize(35);
	canvas.drawText("Mic: " + DECIMAL_FORMAT.format(peakFrequency) + "Hz", 0, 50, p);
	int pos = (int) ((peakFrequency * mWidth) / (AudioListener.SAMPLE_RATE / SCALE_FACTOR));
	for (int height = 0; height < mHeight; height += (MARK_SIZE + 3)) {
	    canvas.drawLine(pos, height, pos, height + MARK_SIZE, p);
	}
	pos = (int) ((mHeight / 2) / (peakFrequency / RealPitches.getNearestPitch(peakFrequency).getFrequency()));
	for (int width = 0; width < mWidth; width += (MARK_SIZE + 3)) {
	    canvas.drawLine(width, pos, width + MARK_SIZE, pos, p);
	}
    }

    private void drawClosestRealNote(Canvas canvas, double peakFrequency) {
	Paint p = new Paint();
	p.setColor(Color.GREEN);
	p.setTextSize(35);
	PitchFreqAndNoteName realNote = RealPitches.getNearestPitch(peakFrequency);
	canvas.drawText("Goal: " + DECIMAL_FORMAT.format(realNote.getFrequency()) + "Hz (" + realNote.getNoteName() + ")", 0, 90, p);
	int pos = (int) ((realNote.getFrequency() * mWidth) / (AudioListener.SAMPLE_RATE / SCALE_FACTOR));
	canvas.drawLine(pos, 0, pos, mHeight, p);
	canvas.drawLine(0, mHeight / 2, mWidth, mHeight / 2, p);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
	mWidth = width;
	mHeight = height;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
	isSurfaceCreated = true;
	mWidth = getWidth();
	mHeight = getHeight();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
	isSurfaceCreated = false;
    }

}
