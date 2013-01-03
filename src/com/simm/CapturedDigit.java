package com.simm;

import java.awt.image.BufferedImage;
import java.awt.image.RasterFormatException;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.simm.Actions.StoreMinAndMax;
import com.simm.Predicates.DarkLineFound;


public class CapturedDigit {
	private int rowCount, columnCount;
	private BufferedImage image;
	private int chunkWidth, chunkHeight;
	
	/** How many pixels would be if it wouldnt be an int. */
	private float pixelsPerRow, pixelsPerColumn;
	
	public CapturedDigit(File fileWithDigit){
		try {
			setImage(ImageIO.read(fileWithDigit));
			split(image.getWidth(), image.getHeight());
		} catch (IOException e) {
			throw new IllegalArgumentException("Cannot read image from file " + fileWithDigit.getAbsolutePath(), e);
		}		
	}

	public CapturedDigit(File fileWithDigit, int columnCount, int rowCount){
		try {
			setImage(ImageIO.read(fileWithDigit));
			split(columnCount, rowCount);
		} catch (IOException e) {
			throw new IllegalArgumentException("Cannot read image from file " + fileWithDigit.getAbsolutePath(), e);
		}		
	}
	
	public CapturedDigit(BufferedImage image){
		setImage(image);
		split(image.getWidth(), image.getHeight());
	}

	public CapturedDigit(BufferedImage image, int columnCount, int rowCount){
		setImage(image);
		split(columnCount, rowCount);
	}

	private void setImage(BufferedImage image){
		if(image == null){
			throw new IllegalArgumentException("Image cannot be null.");
		}
		
		this.image = image;
	}
	
	private void split(int columnCount, int rowCount){
		if (rowCount <= 0){
			throw new IllegalArgumentException("rowCount must be > 0");
		}
		if (columnCount <= 0){
			throw new IllegalArgumentException("collumnCount must be > 0");
		}
		
		this.columnCount = columnCount;
		this.rowCount = rowCount;		
		
		//image comes from constructor, there is no way image is null here
		pixelsPerRow = ((float) image.getHeight()) / rowCount;
		pixelsPerColumn = ((float) image.getWidth()) / columnCount;
		
		chunkHeight = Math.round(pixelsPerRow);
		chunkWidth = Math.round(pixelsPerColumn);
		
	}
	
	public CapturedDigit reSplit(int columnCount, int rowCount){
		return new CapturedDigit(image, columnCount, rowCount);
	}
	
	public int getWidth(){
		return image.getWidth();
	}
	
	public int getHeight(){
		return image.getHeight();
	}
	
	protected BufferedImage getImageForChunkAt(int column, int row){
		BufferedImage subImage = null;
		int chunkX = Math.round(column * pixelsPerColumn);
		int chunkY = Math.round(row * pixelsPerRow);
		
		if (chunkX + chunkWidth > image.getWidth()){
			chunkX = image.getWidth() - chunkWidth;
		}
		
		if (chunkY + chunkHeight > image.getHeight()){
			chunkY = image.getHeight() - chunkHeight;
		}
		
		try{
			subImage = image.getSubimage(chunkX, chunkY, chunkWidth, chunkHeight);
		} catch (RasterFormatException e) {
			String message = String.format("Cannot return chunk at (%s,%s): chunkWidth = %s, chunkHeight = %s, imageWidth = %s, imageHeight = %s",
					row, column, chunkWidth, chunkHeight, image.getWidth(), image.getHeight());
			throw new IllegalArgumentException(message, e);
		}
		
		return subImage;
	}
	
	public DigitChunk getChunkAt(int column, int row){
		return new DigitChunk(getImageForChunkAt(column, row));
	}
	
	public CapturedDigit crop(){
		int rowThreshold = columnCount / 20 + 1;
		int columnThreshold = rowCount / 20 + 1;
		Predicate<DigitChunk[]> darkRowFound = new DarkLineFound(CapturedDigit.darkThreshold(), rowThreshold);
		Predicate<DigitChunk[]> darkColumsFound = new DarkLineFound(CapturedDigit.darkThreshold(), columnThreshold);

		StoreMinAndMax rows = new StoreMinAndMax();
		applyToRow(darkRowFound, rows);
//		logger.info(String.format("Rows: [%s,%s]", rows.min(), rows.max()));

		StoreMinAndMax columns = new StoreMinAndMax();
		applyToColumn(darkColumsFound, columns);
//		logger.info(String.format("Columns: [%s,%s]", columns.min(), columns.max()));

		int x = Math.round(columns.min() * pixelsPerColumn);
		int y = Math.round(rows.min() * pixelsPerRow);
		int w = Math.round((columns.max() - columns.min() + 1) * pixelsPerColumn);
		int h = Math.round((rows.max() - rows.min() + 1) * pixelsPerRow);
		
		BufferedImage subimage = image.getSubimage(x, y, w, h);
		return new CapturedDigit(subimage);
	}
	
	public void saveToFile(File output){
		try {
			ImageIO.write(image, "jpg", output);
		} catch (IOException e) {
			throw new IllegalArgumentException("Cannot write image to file " + output.getAbsolutePath(), e);
		}
	}

	public void apply(Function<DigitChunk, Boolean> function, Runnable trueAction, Runnable falseAction){
		for(int r = 0; r < rowCount; r++){
			for(int c = 0; c < columnCount; c++){
				DigitChunk chunk = getChunkAt(c, r);
				if (function.apply(chunk)){
					trueAction.run();
				} else {
					falseAction.run();
				}
			}
		}
	}
	
	private DigitChunk[] getRow(int r){
		DigitChunk[] row = new DigitChunk[columnCount];
		
		for(int c = 0; c < row.length; c++){
			row[c] = getChunkAt(c, r);
		}
		
		return row;
	}

	private DigitChunk[] getColumn(int c){
		DigitChunk[] column = new DigitChunk[rowCount];
		
		for(int r = 0; r < rowCount; r++){
			column[r] = getChunkAt(c, r);
		}
		
		return column;
	}
	
	public void applyToRow(Predicate<DigitChunk[]> predicate, Action<Integer> action){
		for(int r = 0; r < rowCount; r++){
			DigitChunk[] row = getRow(r);
			boolean applied = predicate.apply(row);
			if (applied){
				action.action(r);
			}
		}
	}
	
	public void applyToColumn(Predicate<DigitChunk[]> predicate, Action<Integer> action){
		for(int c = 0; c < columnCount; c++){
			DigitChunk[] column = getColumn(c);
			boolean applied = predicate.apply(column);
			if (applied){
				action.action(c);
			}
		}
	}

	
	public static int darkThreshold(){
		return 128;
	}
	
	public void print(Logger logger){
		String info = String.format("Width: %s, height: %s, ppr: %s, ppc = %s",
				getWidth(), getHeight(), pixelsPerRow, pixelsPerColumn);
		StringBuilder matrix = new StringBuilder(info).append("\n");
		for(int r = 0; r < rowCount; r++){
			if (r < 10) matrix.append(" ");
			matrix.append(r);
			for(int c = 0; c < columnCount; c++){
				DigitChunk chunk = getChunkAt(c, r);
				if (chunk.getAverageGrayLevel() < darkThreshold()){
					matrix.append("#");
				} else {
					matrix.append("-");
				}
			}
			matrix.append("\n");
		}
		
		logger.info(matrix.toString());
	}
}
