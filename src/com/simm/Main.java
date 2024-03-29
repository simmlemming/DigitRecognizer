package com.simm;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import com.simm.captured.CapturedDigit;
import com.simm.captured.CapturedNumber;
import com.simm.matchers.MatchPair;
import com.simm.matchers.Matcher;
import com.simm.samples.Samples;

public class Main {

	private static Logger logger;
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) {
		logger = Logger.getAnonymousLogger();
		int columnCount = 12;
		int rowCount = 16;
//		String fileName = "001";
//		int numOfDigits = 5;
	
//		CapturedDigit digit = new CapturedDigit(new File("samples/0/b0-2.jpg"), columnCount, rowCount);
		CapturedNumber number = new CapturedNumber(5, new File("samples/originals/49043.jpg"));
		Matcher matcher = new Matcher(new Samples("samples"));
		
		for(int i = 0; i < 5; i++){
			CapturedDigit digit = number.getDigit(i);
			CapturedDigit cropped = digit.crop().reSplit(columnCount, rowCount);
			List<MatchPair> match = matcher.match(cropped);
			printMatchResponse(match);
			
		}
				
//		File[] files = new File("samples/preprocessed").listFiles(new FilenameFilter() {
//			
//			@Override
//			public boolean accept(File arg0, String arg1) {
//				boolean hasDash = arg1.contains("-");
//				boolean isPicture = arg1.endsWith(".jpg");
//				return isPicture && hasDash;
////				Set<String> names = new HashSet<String>();
////				names.add("b4-2.jpg");
////				names.add("b8-1.jpg");
////				names.add("b9-3.jpg");
////				names.add("b6-2.jpg");
////				names.add("b0-4.jpg");
////				return names.contains(arg1);
////				return arg1.endsWith("b1-2.jpg");
//			}
//		});
		
//		for (int i = 0; i < files.length; i++){
//			CapturedDigit digit = new CapturedDigit(files[i], columnCount, rowCount);
//			digit.print(logger);
//			try{
//				CapturedDigit cropped = digit.crop().reSplit(columnCount, rowCount);
//				cropped.print(logger);
//				cropped.saveToFile(new File("samples/cropped", files[i].getName()));
//			} catch (RasterFormatException e) {
//				logger.info("Cannot crop " + files[i].getName());
//			}
//		}

//		Samples samples = new Samples("samples/preprocessed");
//		Iterator<CapturedDigit> digits = samples.digitsFor(4);
//		while (digits.hasNext()) {
//			CapturedDigit digit = digits.next();
//			digit.reSplit(columnCount, rowCount).print(logger);
//		}
//		
		
		
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
		
	private static void printMatchResponse(List<MatchPair> response){
		StringBuilder matchInfo = new StringBuilder("\n");
		for(int i = 0; i < 10; i++){
			matchInfo.append(response.get(i)).append("\n");
		}

		logger.info(matchInfo.toString());
	}
}
