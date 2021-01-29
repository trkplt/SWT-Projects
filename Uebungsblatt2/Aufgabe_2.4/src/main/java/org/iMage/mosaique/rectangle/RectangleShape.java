package org.iMage.mosaique.rectangle;

import java.awt.Color;
import java.awt.image.BufferedImage;

import org.iMage.mosaique.base.BufferedArtImage;
import org.iMage.mosaique.base.IMosaiqueShape;
import org.iMage.mosaique.base.ImageUtils;

/**
 * This class represents a rectangle as {@link IMosaiqueShape} based on an
 * {@link BufferedArtImage}.
 *
 * @author Dominik Fuchss
 *
 */
public class RectangleShape implements IMosaiqueShape<BufferedArtImage> {

	private BufferedArtImage scaledImage;

	/**
	 * Create a new {@link IMosaiqueShape}.
	 *
	 * @param image the image to use
	 * @param w     the width
	 * @param h     the height
	 */
	public RectangleShape(BufferedArtImage image, int w, int h) {
		scaledImage = new BufferedArtImage(ImageUtils.scaleAndCrop(image.toBufferedImage(), w, h));
	}

	@Override
	public int getAverageColor() {

		long alpha = 0;
		long red = 0;
		long green = 0;
		long blue = 0;

		int height = scaledImage.getHeight();
		int width = scaledImage.getWidth();
		int pixelCount = height * width;

		for (int h = 0; h < height; h++) {
			for (int w = 0; w < width; w++) {
				Color color = new Color(scaledImage.getRGB(w, h), true);

				alpha += color.getAlpha();
				red += color.getRed();
				green += color.getGreen();
				blue += color.getBlue();
			}
		}

		return ((((int) Math.round(alpha / pixelCount)) << 24) | (((int) Math.round(red / pixelCount)) << 16)
				| (((int) Math.round(green / pixelCount)) << 8) | ((int) Math.round(blue / pixelCount)));
	}

	@Override
	public BufferedImage getThumbnail() {
		return scaledImage.toBufferedImage();
	}

	@Override
	public void drawMe(BufferedArtImage targetRect) {
		targetRect.setSubimage(0, 0, scaledImage.getSubimage(0, 0, targetRect.getWidth(), targetRect.getHeight()));
	}

	@Override
	public int getHeight() {
		return scaledImage.getHeight();
	}

	@Override
	public int getWidth() {
		return scaledImage.getWidth();
	}
}