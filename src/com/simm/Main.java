package com.simm;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

public class Main {

	private static Logger logger;
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) {
		logger = Logger.getAnonymousLogger();
		int columnCount = 16;
		int rowCount = 24;
		String fileName = "001";
		int numOfDigits = 5;
		
		CapturedNumber capturedNumber = new CapturedNumber(numOfDigits, new File("samples", fileName + ".jpg"));
		
		CapturedDigit[] capturedDigits = new CapturedDigit[numOfDigits];
		for(int i = 0; i < numOfDigits; i++){
			CapturedDigit digit = capturedNumber.getDigit(i);
			digit.saveToFile(new File("tmp", fileName + "." + i + ".jpg"));
			digit.divideIntoChunks(columnCount, rowCount);
			digit.print(logger);
			
			CapturedDigit cropped = digit.crop(columnCount, rowCount);
			cropped.saveToFile(new File("tmp", fileName + "." + i + ".cropped.jpg"));
			cropped.divideIntoChunks(columnCount, rowCount);
			cropped.print(logger);
		}
	}
		
}
