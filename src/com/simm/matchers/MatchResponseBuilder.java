package com.simm.matchers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MatchResponseBuilder {
	private List<MatchPair> pairs;

	public MatchResponseBuilder(){
		pairs = new ArrayList<MatchPair>();
	}
	
	public void addPair(MatchPair pair){
		pairs.add(pair);
	}
	
	public List<MatchPair> build(){
		Collections.sort(pairs, new MatchResponceComparator());
		return Collections.unmodifiableList(pairs);
	}
	
	private static class MatchResponceComparator implements Comparator<MatchPair>{
		@Override
		public int compare(MatchPair o1, MatchPair o2) {
			if (o1.level < o2.level){
				return 1;
			}
			
			if (o1.level > o2.level){
				return -1;
			}
			
			return 0;
		}
		
	}
}
