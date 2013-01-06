package com.simm.matchers;

import java.util.Iterator;
import java.util.List;

import com.simm.captured.CapturedDigit;
import com.simm.samples.Samples;

public class Matcher {
	private final Samples samples;
	
	public Matcher(Samples samples) {
		if (samples == null){
			throw new IllegalArgumentException("Samples cannot be null.");
		}
		this.samples = samples;
	}
	
	public List<MatchPair> match(CapturedDigit digit){
		MatchResponseBuilder matchResponse = new MatchResponseBuilder();
		
		for(int candidate = 0; candidate < 10; candidate++){
			Iterator<CapturedDigit> sampleDigits = samples.digitsFor(candidate);
			float level = 0;
			while (sampleDigits.hasNext()) {
				CapturedDigit sample = sampleDigits.next();
				float newLevel = 1 - sample.relativeDistanceTo(digit);
				level = Math.max(level, newLevel);
			}
			matchResponse.addPair(new MatchPair(candidate, level));
		}
		
		return matchResponse.build();
	}
}
