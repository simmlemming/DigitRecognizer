package com.simm;

import java.util.Iterator;

public class Matcher {
	private final Samples samples;
	
	public Matcher(Samples samples) {
		if (samples == null){
			throw new IllegalArgumentException("Samples cannot be null.");
		}
		this.samples = samples;
	}
	
	public float[] match(CapturedDigit digit){
		float[] matchLevels = new float[10];
		
		for(int candidate = 0; candidate < 10; candidate++){
			Iterator<CapturedDigit> sampleDigits = samples.digitsFor(candidate);
			while (sampleDigits.hasNext()) {
				CapturedDigit sample = sampleDigits.next();
				float newMatchLevel = 1 - sample.relativeDistanceTo(digit);
				matchLevels[candidate] = Math.max(matchLevels[candidate], newMatchLevel);
			}
		}
		
		return matchLevels;
	}
}
