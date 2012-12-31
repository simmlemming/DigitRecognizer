package com.simm;

import com.google.common.base.Predicate;

public class Predicates {
	public static class DarkLineFound implements Predicate<DigitChunk[]>{
		private int chunksInDarkLine;
		private int darkThreshold;
		
		public DarkLineFound(int darkThreshold, int chunksInDarkLine){
			this.darkThreshold = darkThreshold;
			this.chunksInDarkLine = chunksInDarkLine;
		}
		
		@Override
		public boolean apply(DigitChunk[] chunks) {
			int darkCunkCount = 0;
			
			for(int i = 0; i < chunks.length; i++){
				int level = chunks[i].getAverageGrayLevel();
				if (level < darkThreshold){
					darkCunkCount++;
				}
			}
			
			return darkCunkCount >= chunksInDarkLine;
		}
		
	}

}
