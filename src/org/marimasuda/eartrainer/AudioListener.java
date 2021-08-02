package org.marimasuda.eartrainer;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;
import fft.FFTHelper;

public class AudioListener extends Thread {
    private boolean mStopped;
    public static final int SAMPLE_RATE = 8000;
    public static final int CHANNELS_IN = AudioFormat.CHANNEL_IN_MONO;
    public static final int ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    public static final int MIN_BUFFER_SIZE = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNELS_IN, ENCODING);
    public static final int NUM_FFT_SAMPLES = 2048;
    private AudioRecord mRecorder;
    private FFTHelper mFFT;
    private MainActivity mMainActivity;

    public AudioListener() {
	mFFT = new FFTHelper((double)SAMPLE_RATE, NUM_FFT_SAMPLES);
	mStopped = false;
	start();
    }

    @Override
    public void run() {
	int numBytesRead = 0;
	int bufferSize = NUM_FFT_SAMPLES;
	double[] signal;

	try {
	    mRecorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
					SAMPLE_RATE,
					CHANNELS_IN,
					ENCODING,
					MIN_BUFFER_SIZE);
	    if(mRecorder == null) {
		throw new RuntimeException("Audio Recording Device was not initialized!!!");
	    }
	    mRecorder.startRecording();
	    while(!mStopped) {
		byte tempBuffer[] = new byte[bufferSize];
		numBytesRead = mRecorder.read(tempBuffer, 0, bufferSize);
		if (numBytesRead > 0) {
		    if (mFFT != null) {
			signal = mFFT.calculateFFT(tempBuffer);
			mMainActivity.onDrawableFFTSignalAvailable(signal, getMaxFFTSample(), getPeakFrequency());
		    }
		} else {
		    Log.e("ERROR", "Error reading from recording device");
		}
	    }
	} catch (Exception e) {
	    Log.e("ERROR", "Error: " + e);
	}
    }

    public void close() {
	mStopped = true;
	mRecorder.stop();
	mRecorder.release();
    }

    public void registerMainActivity(MainActivity mainActivity) {
	mMainActivity = mainActivity;
    }

    public void unregisterMainActivity() {
	mMainActivity = null;
    }

    public double getMaxFFTSample() {
	return mFFT.getMaxFFTSample();
    }

    public double getPeakFrequency() {
	return mFFT.getPeakFrequency();
    }
}
