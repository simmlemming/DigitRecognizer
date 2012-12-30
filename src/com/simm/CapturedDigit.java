package com.simm;

import java.awt.image.BufferedImage;
import java.awt.image.RasterFormatException;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class CapturedDigit {
	private final int rowCount, columnCount;
	private final BufferedImage image;
	private final int chunkWidth, chunkHeight;
	private final float rowDensity, columnDensity;
	
	public CapturedDigit(int columnCount, int rowCount, File fileWithDigit){
		if (rowCount <= 0){
			throw new IllegalArgumentException("rowCount must be > 0");
		}
		if (columnCount <= 0){
			throw new IllegalArgumentException("collumnCount must be > 0");
		}
		
		this.rowCount = rowCount;
		this.columnCount = columnCount;
		
		try {
			image = ImageIO.read(fileWithDigit);
		} catch (IOException e) {
			throw new IllegalArgumentException("Cannot read image from file " + fileWithDigit.getAbsolutePath(), e);
		}
		
		rowDensity = ((float) image.getHeight()) / rowCount;
		columnDensity = ((float) image.getWidth()) / columnCount;
		
		chunkHeight = Math.round(rowDensity);
		chunkWidth = Math.round(columnDensity);
	}
	
	public int getWidth(){
		return image.getWidth();
	}
	
	public int getHeight(){
		return image.getHeight();
	}
	
	public DigitChunk getChunkAt(int column, int row){
		int chunkX = Math.round(column * columnDensity);
		int chunkY = Math.round(row * rowDensity);
		
		BufferedImage chunk = null;
		try{
			chunk = image.getSubimage(chunkX, chunkY, chunkWidth, chunkHeight);
		} catch (RasterFormatException e) {
			String message = String.format("Cannot return chunk at (%s,%s): chunkWidth = %s, chunkHeight = %s, imageWidth = %s, imageHeight = %s",
					row, column, chunkWidth, chunkHeight, image.getWidth(), image.getHeight());
			throw new IllegalArgumentException(message, e);
		}
		
		return new DigitChunk(chunk);
	}
}
