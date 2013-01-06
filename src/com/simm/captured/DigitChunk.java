package com.simm.captured;

import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class DigitChunk {

	private final BufferedImage image;
	
	public DigitChunk(BufferedImage image){
		this.image = image;
	}

	public int getAverageGrayLevel(){
		int grayLevel = 0;
		ColorModel colorModel = ColorModel.getRGBdefault();
		
		for(int x = 0; x < image.getWidth(); x++){
			for(int y = 0; y < image.getHeight(); y++){
				int rgb = image.getRGB(x, y);
				int red = colorModel.getRed(rgb);
				int green = colorModel.getGreen(rgb);
				int blue = colorModel.getBlue(rgb);
				
				int gray = (red + green + blue) / 3;
				grayLevel += gray;
			}
		}
		
		return grayLevel / image.getWidth() / image.getHeight();
	}
	
	@Override
	public String toString() {
		return String.format("DigitChunk: with = %s, height = %s, average gray level = %s",
				image.getWidth(), image.getHeight(), getAverageGrayLevel());
	}
	
	public void saveToFile(File output){
		try {
			ImageIO.write(image, "jpg", output);
		} catch (IOException e) {
			throw new IllegalArgumentException("Cannot write image to file " + output.getAbsolutePath(), e);
		}
	}
	
}
