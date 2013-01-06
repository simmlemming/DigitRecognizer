package com.simm.matchers;

public class MatchPair {
	public final int number;
	public final float level;
	
	public MatchPair(int number, float level) {
		this.number = number;
		this.level = level;
	}
	
	@Override
	public String toString() {
		return number + ": " + level;
	}
}
