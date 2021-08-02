package org.marimasuda.eartrainer;

import android.util.Log;


public class RealPitches {
    public static final PitchFreqAndNoteName[] PITCHES = new PitchFreqAndNoteName[88];
    private static final String[] NOTE_NAMES = new String[] { "G#", "A", "A#", "B", "C", "C#", "D", "D#", "E", "F", "F#", "G"};
    static {
	for (double i = 1; i < PITCHES.length; i++) {
	    double freq = Math.pow(2, ((i-49)/12)) * 440;
	    String noteName = NOTE_NAMES[((int)i % NOTE_NAMES.length)] + ((int)i / NOTE_NAMES.length);
	    PITCHES[(int) i] = new PitchFreqAndNoteName(freq, noteName);

	    Log.i("PITCH", freq + ": " + noteName);
	}
    }

    public static class PitchFreqAndNoteName {
	private double frequency;
	private String noteName;

	public PitchFreqAndNoteName(double f, String n) {
	    frequency = f;
	    noteName = n;
	}

	public double getFrequency() {
	    return frequency;
	}

	public String getNoteName() {
	    return noteName;
	}
    }

    public static PitchFreqAndNoteName getNearestPitch(double peakFrequency) {
	int mid;
	int low = 0;
	int high = PITCHES.length - 1;

	while (low < high) {
	    mid = (low + high) / 2;

	    if (peakFrequency < PITCHES[mid].getFrequency()) {
		high = mid - 1;
	    } else if (peakFrequency > PITCHES[mid].getFrequency()) {
		low = mid + 1;
	    } else {
		return PITCHES[mid];
	    }
	}

	double diffLow = Math.abs(peakFrequency - PITCHES[low].getFrequency());
	double diffHigh = Math.abs(peakFrequency - PITCHES[high].getFrequency());

	if (diffLow < diffHigh) {
	    return PITCHES[low];
	} else {
	    return PITCHES[high];
	}
    }
}
