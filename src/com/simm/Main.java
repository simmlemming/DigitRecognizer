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
	public static void main(String[] args) throws IOException {
		logger = Logger.getAnonymousLogger();
		int columnCount = 160;
		int rowCount = 30;
		
		CapturedDigit capturedDigit = new CapturedDigit(columnCount, rowCount, new File("samples/2.jpg"));
		logger.info("W: " + capturedDigit.getWidth() + ", H: " + capturedDigit.getHeight());
		
		String row = "\n";
//		String levels = "\n";
		for(int r = 0; r < rowCount; r++){
			for(int c = 0; c < columnCount; c++){
				DigitChunk chunk = capturedDigit.getChunkAt(c, r);
//				chunk.saveToFile(new File("tmp", c + "x" + r + ".jpg"));
//				levels += chunk.getAverageGrayLevel() + ", ";
				if (chunk.getAverageGrayLevel() < 128){
					row += "#";
				} else {
					row += "-";
				}
			}
			row += "\n";
//			levels += "\n";
		}
		logger.info(row);
//		logger.info(levels);
	}
}
