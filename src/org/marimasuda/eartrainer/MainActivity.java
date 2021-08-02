package org.marimasuda.eartrainer;

import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity {
    private AudioListener mAudioListener;
    private SpectrumPanel mSpectrumPanel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_main);
	mAudioListener = new AudioListener();
	mAudioListener.registerMainActivity(this);
	mSpectrumPanel = (SpectrumPanel) findViewById(R.id.spectrum_panel);
    }

    @Override
    public void onPause() {
	super.onPause();
	mAudioListener.close();
	mAudioListener.unregisterMainActivity();
    }

    public void onDrawableFFTSignalAvailable(final double[] signal, final double maxFFTSample, final double peakFrequency) {
	this.runOnUiThread(new Runnable() {
		public void run() {
		    mSpectrumPanel.drawSpectrum(signal, maxFFTSample, peakFrequency);
		}
	    });
    }
}
