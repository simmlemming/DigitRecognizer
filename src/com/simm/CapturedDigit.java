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

	private int darkThreshold = 128;
	private String fileName = "-";
	private final static int USE_IMAGE_DIMENTIONS = -1;
	
	public CapturedDigit(File fileWithDigit){
		this(fileWithDigit, USE_IMAGE_DIMENTIONS, USE_IMAGE_DIMENTIONS);
	}

	public CapturedDigit(File fileWithDigit, int columnCount, int rowCount){
		try {
			setImage(ImageIO.read(fileWithDigit));
			split(columnCount, rowCount);
			darkThreshold = calculateDarkThreshold();
			fileName = fileWithDigit.getName();
		} catch (IOException e) {
			throw new IllegalArgumentException("Cannot read image from file " + fileWithDigit.getAbsolutePath(), e);
		}		
	}
	
	public CapturedDigit(BufferedImage image){
		this(image, USE_IMAGE_DIMENTIONS, USE_IMAGE_DIMENTIONS);
	}

	public CapturedDigit(BufferedImage image, int columnCount, int rowCount){
		setImage(image);
		split(columnCount, rowCount);
		darkThreshold = calculateDarkThreshold();
	}

	private void setImage(BufferedImage image){
		if(image == null){
			throw new IllegalArgumentException("Image cannot be null.");
		}
		
		this.image = image;
	}
	
	private void split(int columnCount, int rowCount){
		//image comes from constructor, there is no way image is null here
		if (columnCount == USE_IMAGE_DIMENTIONS){
			columnCount = image.getWidth();
		}

		if (rowCount == USE_IMAGE_DIMENTIONS){
			rowCount = image.getHeight();
		}
		
		if (rowCount <= 0){
			throw new IllegalArgumentException("rowCount must be > 0");
		}
		
		if (columnCount <= 0){
			throw new IllegalArgumentException("collumnCount must be > 0");
		}
		
		this.columnCount = columnCount;
		this.rowCount = rowCount;		
		
		pixelsPerRow = ((float) image.getHeight()) / this.rowCount;
		pixelsPerColumn = ((float) image.getWidth()) / this.columnCount;
		
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
		int rowThreshold = columnCount / 5 + 1;
		int columnThreshold = rowCount / 5 + 1;
		Predicate<DigitChunk[]> darkRowFound = new DarkLineFound(darkThreshold(), rowThreshold);
		Predicate<DigitChunk[]> darkColumsFound = new DarkLineFound(darkThreshold(), columnThreshold);

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

	
	private int calculateDarkThreshold(){
		int t = rowCount / 3;
		int[] minAndMax1 = minAndMaxGrayLevelForRow(t);
		int[] minAndMax2 = minAndMaxGrayLevelForRow(2 * t);
		
		int maxLevel = Math.max(minAndMax1[1], minAndMax2[1]);
		int minLevel = Math.min(minAndMax1[0], minAndMax2[0]);
		
//		return (int)Math.round((maxLevel + minLevel) * 0.5);
		double l = (maxLevel  + minLevel) * 0.5;
//		return (int)Math.round(l);
		return (maxLevel  + minLevel) / 2;
	}
	
	private int[] minAndMaxGrayLevelForRow(int row){
		int maxLevel = Integer.MIN_VALUE;
		int minLevel = Integer.MAX_VALUE;
		
		for(int i = 0; i < columnCount; i++){
			DigitChunk chunk = getChunkAt(i, row);
			int chunkLevel = chunk.getAverageGrayLevel();
			maxLevel = Math.max(maxLevel, chunkLevel);
			minLevel = Math.min(minLevel, chunkLevel);
		}
		
		return new int[] {minLevel, maxLevel};
	}
	
	private int darkThreshold(){
		return darkThreshold;
	}
	
	private int[][] toMatrix(){
		int[][] matrix = new int[rowCount][columnCount];
		
		for(int r = 0; r < rowCount; r++){
			for(int c = 0; c < columnCount; c++){
				DigitChunk chunk = getChunkAt(c, r);
				if (chunk.getAverageGrayLevel() < darkThreshold()){
					matrix[r][c] = 1;
				} else {
					matrix[r][c] = 0;
				}
			}
		}
		
		return matrix;
	}
	
	public int distanceTo(CapturedDigit digit){
		int[][] otherMatrix = digit.reSplit(columnCount, rowCount).toMatrix();
		int[][] thisMatrix = toMatrix();
		int distance = 0;
		
		for(int r = 0; r < rowCount; r++){
			for(int c = 0; c < columnCount; c++){
				boolean same = otherMatrix[r][c] == thisMatrix[r][c];
				if (!same){
					distance++;
				}
			}
		}
		
		return distance;
	}
	
	public float relativeDistanceTo(CapturedDigit digit){
		int distance = distanceTo(digit);
		
		return ((float) distance) / columnCount / rowCount;
	}
	
	public void print(Logger logger){
		String info = String.format("\nFile: %s,\nWidth: %s, height: %s,\nppr: %s, ppc = %s,\ndark threshold = %s",
				fileName, getWidth(), getHeight(), pixelsPerRow, pixelsPerColumn, darkThreshold());
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
