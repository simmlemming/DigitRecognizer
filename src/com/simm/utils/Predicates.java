package com.simm.utils;

import com.google.common.base.Predicate;
import com.simm.captured.DigitChunk;

public class Predicates {
	public static class DarkLineFound implements Predicate<DigitChunk[]>{
		private int chunksInWhiteLine;
		private int darkThreshold;
		
		public DarkLineFound(int darkThreshold, int chunksInWhiteLine){
			this.darkThreshold = darkThreshold;
			this.chunksInWhiteLine = chunksInWhiteLine;
		}
		
		@Override
		public boolean apply(DigitChunk[] chunks) {
			int whiteChunkCount = 0;
			
			for(int i = 0; i < chunks.length; i++){
				int level = chunks[i].getAverageGrayLevel();
				if (level > darkThreshold){
					whiteChunkCount++;
				}
			}
			
			return whiteChunkCount >= chunksInWhiteLine;
		}
		
	}

	public static class All implements Predicate<DigitChunk[]>{

		@Override
		public boolean apply(DigitChunk[] arg0) {
			return true;
		}
		
	}
}
