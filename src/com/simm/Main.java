package com.simm;

import java.io.IOException;
import java.util.Iterator;
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
	
		Samples samples = new Samples("samples");
		Iterator<CapturedDigit> digits = samples.digitsFor(4);
		while (digits.hasNext()) {
			CapturedDigit digit = digits.next();
			digit.reSplit(columnCount, rowCount).print(logger);
		}
		
		//CapturedDigit first = new CapturedDigit(new File("samples", "4.jpg"), columnCount, rowCount);
		//CapturedDigit second = new CapturedDigit(new File("samples", "9.jpg"));
		
		//int distance = first.distanceTo(second);
		//float relativeDistance = first.relativeDistanceTo(second);
		
		//logger.info(String.valueOf(distance));
		//logger.info(String.valueOf(relativeDistance));
		
//		CapturedNumber capturedNumber = new CapturedNumber(numOfDigits, new File("samples", fileName + ".jpg"));
		
//		CapturedDigit[] capturedDigits = new CapturedDigit[numOfDigits];
//		for(int i = 0; i < numOfDigits; i++){
//			CapturedDigit digit = capturedNumber.getDigit(i);
//			digit.saveToFile(new File("tmp", fileName + "." + i + ".jpg"));
//			digit.reSplit(columnCount, rowCount).print(logger);
//			
//			CapturedDigit cropped = digit.crop();
//			cropped.saveToFile(new File("tmp", fileName + "." + i + ".cropped.jpg"));
//			cropped.reSplit(columnCount, rowCount).print(logger);
//		}
	}
		
}
