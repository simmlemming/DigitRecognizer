package com.simm;

import java.awt.image.BufferedImage;
import java.io.File;

public class CapturedNumber {
	private CapturedDigit number;
	private final int digitCount;
	
	public CapturedNumber(int digitCount, File fileWithNumber) {
		this.digitCount = digitCount;
		number = new CapturedDigit(fileWithNumber, digitCount, 1);
	}
	
	public CapturedDigit getDigit(int position){
		BufferedImage imageForDigit = number.getImageForChunkAt(position, 0);
		return new CapturedDigit(imageForDigit); 
	}

}
