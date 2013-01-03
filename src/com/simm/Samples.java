package com.simm;

import java.io.File;
import java.util.Iterator;

public class Samples {
	private final String rootFolder;

	public Samples(String rootFolder){
		this.rootFolder = rootFolder;
	}
	
	public Iterator<CapturedDigit> digitsFor(int number){
		return new DigitsIterator(rootFolder, number);
	}
	
	private class DigitsIterator implements Iterator<CapturedDigit>{
		private final File[] files;
		private int currentIndex;
		
		public DigitsIterator(String folder, int number) {
			File rootFolder = new File(folder, String.valueOf(number));
			
			if(!rootFolder.exists()){
				throw new IllegalArgumentException("No such directory: " + rootFolder.getAbsolutePath());
			}
			
			if (!rootFolder.isDirectory()){
				throw new IllegalArgumentException(rootFolder.getAbsolutePath() + " is not a directory");
			}
			
			files = rootFolder.listFiles();
			currentIndex = 0;
		}

		@Override
		public boolean hasNext() {
			return currentIndex < files.length;
		}

		@Override
		public CapturedDigit next() {
			return new CapturedDigit(files[currentIndex++]);
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
		
	}
}
